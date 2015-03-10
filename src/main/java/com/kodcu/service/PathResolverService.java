package com.kodcu.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Arrays;
import java.util.List;

/**
 * Created by usta on 07.09.2014.
 */
@Component
public class PathResolverService {

    private static Logger logger = LoggerFactory.getLogger(PathResolverService.class);

    List<String> rootList =
            Arrays.asList("book.asc", "book.txt", "book.asciidoc", "book.adoc", "book.ad");

    PathMatcher pdfMatcher = FileSystems.getDefault().getPathMatcher("glob:**.pdf");
    PathMatcher markdownMatcher = FileSystems.getDefault().getPathMatcher("glob:{**.md,**.markdown}");
    PathMatcher htmlMatcher = FileSystems.getDefault().getPathMatcher("glob:**.{htm,html}");
    PathMatcher docBookMatcher = FileSystems.getDefault().getPathMatcher("glob:**.xml");
    PathMatcher ascMatcher = FileSystems.getDefault().getPathMatcher("glob:**.{asc,asciidoc,ad,adoc,txt}");
    PathMatcher imageMatcher = FileSystems.getDefault().getPathMatcher("glob:**.{png,svg,jpg,jpeg,bmp,gif}");
    PathMatcher pptMatcher = FileSystems.getDefault().getPathMatcher("glob:**.{ppt,pptx}");
    PathMatcher docxMatcher = FileSystems.getDefault().getPathMatcher("glob:**.{doc,docx}");
    PathMatcher excelMatcher = FileSystems.getDefault().getPathMatcher("glob:**.{xls,xlsx}");
    PathMatcher archieveMatcher = FileSystems.getDefault().getPathMatcher("glob:**.{zip,jar,tar,rar,tar.gz}");
    PathMatcher videoMatcher = FileSystems.getDefault().getPathMatcher("glob:**.{cda,avi,flv,mkv,mov,mp4,mpeg,mpg,ogv,webm,divx,wmv}");
    PathMatcher cssMatcher = FileSystems.getDefault().getPathMatcher("glob:**.{css,css3,scss,less}");
    PathMatcher terminalMatcher = FileSystems.getDefault().getPathMatcher("glob:**.{bat,sh,cmd}");
    PathMatcher codeMatcher = FileSystems.getDefault().getPathMatcher("glob:**.{asp,aspx,c,cpp,java,js,aj,php,rb,yml,py}");
    PathMatcher epubMatcher = FileSystems.getDefault().getPathMatcher("glob:**.{epub,epub3}");
    PathMatcher mobiMatcher = FileSystems.getDefault().getPathMatcher("glob:**.{mobi,azw,azw3}");
    PathMatcher anyMatcher = FileSystems.getDefault().getPathMatcher("glob:**.{*}");
    PathMatcher uniqueMatcher = FileSystems.getDefault().getPathMatcher("glob:{license,readme,gradlew}");
    PathMatcher bookMatcher = FileSystems.getDefault().getPathMatcher("glob:{**book.asc,**book.txt,**book.asciidoc,**book.adoc,**book.ad}");

    public boolean isPDF(Path path) {
        return pdfMatcher.matches(path);
    }

    public boolean isImage(Path path) {
        return imageMatcher.matches(path);
    }

    public boolean isHidden(Path path) {
        try {
            return path.getFileName().toString().startsWith(".") || Files.isHidden(path);
        } catch (IOException e) {
            logger.info(e.getMessage(), e);
        }
        return false;
    }

    public boolean isMarkdown(Path path) {
        return markdownMatcher.matches(path);
    }

    public boolean isXML(Path path) {
        return docBookMatcher.matches(path);
    }

    public boolean isHTML(Path path) {
        return htmlMatcher.matches(path);
    }

    public boolean isAsciidoc(Path path) {
        return ascMatcher.matches(path);
    }

    public boolean isViewable(Path path) {
        return Files.isDirectory(path)
                || isAsciidoc(path)
                || isImage(path)
                || isPDF(path)
                || isEpub(path)
                || isMobi(path)
                || isHTML(path)
                || isXML(path)
                || isMarkdown(path);
    }


    public boolean isBook(Path path) {
        return bookMatcher.matches(path);
    }

    public boolean isMobi(Path path) {
        return mobiMatcher.matches(path);
    }

    public boolean isPPT(Path path) {
        return pptMatcher.matches(path);
    }

    public boolean isDocx(Path path) {
        return docxMatcher.matches(path);
    }

    public boolean isExcel(Path path) {
        return excelMatcher.matches(path);
    }

    public boolean isArchive(Path path) {
        return archieveMatcher.matches(path);
    }

    public boolean isVideo(Path path) {
        return videoMatcher.matches(path);
    }

    public boolean isCSS(Path path) {
        return cssMatcher.matches(path);
    }

    public boolean isBash(Path path) {
        return terminalMatcher.matches(path);
    }

    public boolean isCode(Path path) {
        return codeMatcher.matches(path);
    }

    public boolean isAny(Path path) {
        return anyMatcher.matches(path) || uniqueMatcher.matches(path);
    }

    public boolean isEpub(Path path) {
        return epubMatcher.matches(path);
    }
}
