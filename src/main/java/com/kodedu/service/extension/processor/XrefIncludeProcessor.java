package com.kodedu.service.extension.processor;

import com.kodedu.helper.IOHelper;
import com.kodedu.other.RefProps;
import org.asciidoctor.ast.Document;
import org.asciidoctor.extension.IncludeProcessor;
import org.asciidoctor.extension.PreprocessorReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Scope("prototype")
public class XrefIncludeProcessor extends IncludeProcessor {

    private static Logger logger = LoggerFactory.getLogger(XrefIncludeProcessor.class);

    private static final Pattern NEWLINE_RX = Pattern.compile("\\r\\n?|\\n");
    private static final Pattern TAG_DIRECTIVE_RX = Pattern.compile("\\b(?:tag|(e)nd)::(\\S+?)\\[\\](?=$|[ \\r])", Pattern.MULTILINE);

    record FilterContent(String content, Integer lineNo){}

    private HttpClient httpClient = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .build();

    @Override
    public boolean handles(String target) {
        return Objects.nonNull(target) && !target.isEmpty() && !target.contains("*");
    }

    @Override
    public void process(Document document, PreprocessorReader reader, String target, Map<String, Object> attributes) {
        Path targetPath = null;
        String targetString = null;
        String content = null;
        if (isUri(target)) {
            targetString = target;
            content = readUri(targetString);
        } else {
            String dir = reader.getDir();
            if (isUri(dir)) {
                URI uri = URI.create(dir).resolve(target);
                targetString = uri.toString();
                content = readUri(uri);
            } else {
                targetPath = resolveTargetPath(document, Paths.get(dir), target);
                targetString = targetPath.toString();
                content = IOHelper.readFile(targetPath);
            }
        }

        int startLineNumber = 1;

        Map<String, List<RefProps>> xrefMap = XrefHelper.parseXrefs(targetString, content);

        Map<String, List<RefProps>> xref = ProcessorThreadLocal.getXref();
        xref.putAll(xrefMap);

        List<Integer> lineNums = getLines(attributes);
        if (!lineNums.isEmpty()) {
            FilterContent tuple  = filterLinesByLineNumbers(content, lineNums);
            content = tuple.content();
            startLineNumber = tuple.lineNo();
        } else {
            Map<String, Boolean> tags = getTags(attributes);
            if (!tags.isEmpty()) {
                FilterContent tuple = filterLinesByTags(content, target, tags);
                content = tuple.content();
                startLineNumber = tuple.lineNo();
            }
        }

        reader.pushInclude(content, target, targetString, startLineNumber, attributes);
    }

    private boolean isUri(String target) {
        return Objects.nonNull(target) && isHttpOrHttps(target);
    }

    private String readUri(URI uri) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String readUri(String uri) {
        return readUri(URI.create(uri));
    }

    public List<Integer> getLines(Map<String, Object> attrs) {
        if (attrs.containsKey("lines")) {
            String lines = (String) attrs.get("lines");
            if (lines != null && !lines.isEmpty()) {
                List<Integer> linenums = new ArrayList<>();
                List<String> ranges = Arrays.asList(lines.split("[,;]"));
                for (String linedef : ranges) {
                    if (!linedef.isEmpty()) {
                        int delim = linedef.indexOf("..");
                        try {
                            if (delim != -1) {
                                parseRange(linedef, delim, linenums);
                            } else {
                                int singleLine = Integer.parseInt(linedef);
                                if (singleLine > 0) {
                                    linenums.add(singleLine);
                                }
                            }
                        } catch (Exception e) {
                            return new ArrayList<>();
                        }
                    }
                }

                return linenums.stream()
                        .sorted(Comparator.comparingInt(a -> a))
                        .collect(Collectors.toList());
            }
        }

        return new ArrayList<>();
    }

    private void parseRange(String linedef, int delim, List<Integer> linenums) {
        int from = Integer.parseInt(linedef.substring(0, delim));
        int to = Integer.parseInt(linedef.substring(delim + 2));

        if (to > 0 && from > 0) {
            for (int i = from; i <= to; i++) {
                linenums.add(i);
            }
        } else if (to == -1 && from > 0) {
            linenums.add(from);
            linenums.add(Integer.MAX_VALUE);
        }
    }

    public Map<String, Boolean> getTags(Map<String, Object> attrs) {
        if (attrs.containsKey("tag")) {
            String tag = (String) attrs.get("tag");
            if (tag != null && !tag.equals("!")) {
                Map<String, Boolean> map = !tag.isEmpty() && tag.charAt(0) == '!' ?
                        Map.of(tag.substring(1), false) :
                        Map.of(tag, true);
                return new HashMap<>(map);
            }
        } else if (attrs.containsKey("tags")) {
            String tags = (String) attrs.get("tags");
            if (tags != null && !tags.isEmpty()) {
                return parseTags(tags);
            }
        }
        return new HashMap<>();
    }

    private Map<String, Boolean> parseTags(String tags) {
        return Stream.of(tags.split("[,;]"))
                .filter(tag -> !tag.isEmpty())
                .collect(Collectors.toMap(
                        tag -> tag.charAt(0) == '!' ? tag.substring(1) : tag,
                        tag -> tag.charAt(0) != '!')
                );
    }

    private Path resolveTargetPath(Document document, Path dirPath, String target) {
        Path resolvedPath = dirPath.resolve(target);
        if (Files.exists(resolvedPath)) {
            return resolvedPath;
        }
        // Alternative resolution for attributes-{lang}.adoc
        Path alternatePath = Paths.get((String) document.getAttribute("docdir")).resolve(resolvedPath);
        if (Files.exists(alternatePath)) {
            return alternatePath;
        }
        return resolvedPath;
    }

    public FilterContent filterLinesByLineNumbers(String fileContent, List<Integer> linenums) {
        int lineNum = 0;
        Integer startLineNum = null;
        boolean selectRest = false;
        List<String> lines = new ArrayList<>();

        String[] fileLines = fileContent.split(NEWLINE_RX.pattern());
        for (String line : fileLines) {
            lineNum++;

            if (selectRest || (selectRest = linenums.get(0) == Integer.MAX_VALUE)) {
                if (Objects.isNull(startLineNum)) {
                    startLineNum = lineNum;
                }
                lines.add(line);
            } else {
                if (linenums.get(0) == lineNum) {
                    if (Objects.isNull(startLineNum)) {
                        startLineNum = lineNum;
                    }
                    linenums.removeFirst();
                    lines.add(line);
                }
                if (linenums.isEmpty()) {
                    break;
                }
            }
        }

        String content = String.join(System.lineSeparator(), lines);
        Integer lineNo = Objects.requireNonNullElse(startLineNum, 1);
        return new FilterContent(content, lineNo);
    }

    public FilterContent filterLinesByTags(String fileContent, String target, Map<String, Boolean> tags) {
        Boolean selectingDefault, selecting, wildcard = null;
        Boolean globstar = tags.get("**");
        Boolean star = tags.get("*");

        if (globstar == null) {
            if (star == null) {
                selectingDefault = selecting = !tags.containsValue(true);
            } else {
                if ((wildcard = star) || !tags.keySet().iterator().next().equals("*")) {
                    selectingDefault = selecting = false;
                } else {
                    selectingDefault = selecting = !wildcard;
                }
                tags.remove("*");
            }
        } else {
            tags.remove("**");
            selectingDefault = selecting = globstar;

            if (star == null) {
                Iterator<Boolean> iterator = tags.values().iterator();
                if (!globstar && iterator.hasNext() && !iterator.next()) {
                    wildcard = true;
                }
            } else {
                tags.remove("*");
                wildcard = star;
            }
        }

        List<String> lines = new ArrayList<>();
        List<String[]> tagStack = new ArrayList<>();
        List<String> foundTags = new ArrayList<>();
        String activeTag = null;
        int lineNum = 0;
        Integer startLineNum = null;

        for (String line : fileContent.split(NEWLINE_RX.pattern())) {
            lineNum++;

            Matcher tagMatcher = TAG_DIRECTIVE_RX.matcher(line);
            if (Objects.nonNull(line) && line.contains("::") && line.contains("[]") && tagMatcher.find()) {
                String thisTag = tagMatcher.group(2);
                if (tagMatcher.group(1) != null) {
                    if (thisTag.equals(activeTag)) {
                        tagStack.removeFirst();
                        String[] top = tagStack.size() > 0 ? tagStack.get(0) : new String[]{null, String.valueOf(selectingDefault)};
                        activeTag = top[0];
                        selecting = Boolean.valueOf(top[1]);
                    } else if (tags.containsKey(thisTag)) {
                        int idx = tagStack.indexOf(thisTag);
                        if (idx != -1) {
                            tagStack.remove(idx);
                            logger.warn("mismatched end tag (expected '{}' but found '{}') at line {} of include file: {}",
                                    activeTag, thisTag, lineNum, target);
                        } else {
                            logger.warn("unexpected end tag '{}' at line {} of include file: {}",
                                    thisTag, lineNum, target);
                        }
                    }
                } else if (tags.containsKey(thisTag)) {
                    foundTags.add(thisTag);
                    tagStack.add(0, new String[]{activeTag = thisTag, String.valueOf(selecting = tags.get(thisTag)), String.valueOf(lineNum)});
                } else if (wildcard != null) {
                    selecting = activeTag != null && !selecting ? false : wildcard;
                    tagStack.add(0, new String[]{activeTag = thisTag, String.valueOf(selecting), String.valueOf(lineNum)});
                }
            } else if (selecting) {
                if (Objects.isNull(startLineNum)) {
                    startLineNum = lineNum;
                }
                lines.add(line);
            }
        }

        if (!tagStack.isEmpty()) {
            for (String[] entry : tagStack) {
                logger.warn("detected unclosed tag '{}' starting at line {} of include file: {}",
                        entry[0], entry[2], target);
            }
        }

        if (!foundTags.isEmpty()) {
            foundTags.forEach(name -> tags.remove(name));
        }

        if (!tags.isEmpty()) {
            logger.warn("tag{} '{}' not found in include file: {}",
                    tags.size() > 1 ? "s" : "", String.join(", ", tags.keySet()), target);
        }

        String content = String.join(System.lineSeparator(), lines);
        Integer lineNo = Objects.requireNonNullElse(startLineNum, 1);
        return new FilterContent(content, lineNo);
    }

    private boolean isHttpOrHttps(String url) {
        return Pattern.matches("^https?://.*", url);
    }

}
