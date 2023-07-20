package com.kodedu.service.extension.processor;

import com.kodedu.helper.IOHelper;
import com.kodedu.other.RefProps;
import org.asciidoctor.ast.Document;
import org.asciidoctor.extension.IncludeProcessor;
import org.asciidoctor.extension.PreprocessorReader;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Component
@Scope("prototype")
public class XrefIncludeProcessor extends IncludeProcessor {

    @Override
    public boolean handles(String target) {
        return true;
    }

    @Override
    public void process(Document document, PreprocessorReader reader, String target, Map<String, Object> attributes) {

        Path dirPath = Paths.get(reader.getDir());
        Path targetPath = dirPath.resolve(target);
        String targetString = targetPath.toString();
        String readed = reader.read();
        String content = IOHelper.readFile(targetPath);

        Map<String, List<RefProps>> xrefMap = XrefHelper.parseXrefs(targetString, content);

        Map<String, List<RefProps>> xref = ProcessorThreadLocal.getXref();
        xref.putAll(xrefMap);

        int lineNumber = reader.getLineNumber();
        reader.pushInclude(readed, target, target, lineNumber, attributes);
        reader.pushInclude(content, target, target, lineNumber, attributes);
    }

}
