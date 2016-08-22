package com.kodcu.service;

import com.kodcu.other.IOHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;

/**
 * Created by usta on 07.09.2014.
 */
@Component
public class PathResolverService {

    private final Logger logger = LoggerFactory.getLogger(PathResolverService.class);

    private final PathMatcher pdfMatcher = FileSystems.getDefault().getPathMatcher("glob:**.pdf");
    private final PathMatcher markdownMatcher = FileSystems.getDefault().getPathMatcher("glob:{**.md,**.markdown}");
    private final PathMatcher htmlMatcher = FileSystems.getDefault().getPathMatcher("glob:**.{htm,html}");
    private final PathMatcher docBookMatcher = FileSystems.getDefault().getPathMatcher("glob:**.xml");
    private final PathMatcher ascMatcher = FileSystems.getDefault().getPathMatcher("glob:**.{asc,asciidoc,ad,adoc,txt}");
    private final PathMatcher imageMatcher = FileSystems.getDefault().getPathMatcher("glob:**.{png,svg,jpg,jpeg,bmp,gif}");
    private final PathMatcher pptMatcher = FileSystems.getDefault().getPathMatcher("glob:**.{ppt,pptx,odp,fodp}");
    private final PathMatcher excelMatcher = FileSystems.getDefault().getPathMatcher("glob:**.{xls,xlsx,ods,fods}");
    private final PathMatcher archieveMatcher = FileSystems.getDefault().getPathMatcher("glob:**.{zip,jar,tar,rar,tar.gz,gz,epub,ear,war}");
    private final PathMatcher videoMatcher = FileSystems.getDefault().getPathMatcher("glob:**.{cda,avi,flv,mkv,mov,mp4,mpeg,mpg,ogv,webm,divx,wmv}");
    private final PathMatcher cssMatcher = FileSystems.getDefault().getPathMatcher("glob:**.{css,css3,scss,less}");
    private final PathMatcher terminalMatcher = FileSystems.getDefault().getPathMatcher("glob:**.{bat,sh,cmd,exe,msi,dmg}");
    private final PathMatcher codeMatcher = FileSystems.getDefault().getPathMatcher("glob:**.{asp,aspx,c,cpp,java,js,aj,php,rb,yml,py}");
    private final PathMatcher epubMatcher = FileSystems.getDefault().getPathMatcher("glob:**.{epub,epub3}");
    private final PathMatcher mobiMatcher = FileSystems.getDefault().getPathMatcher("glob:**.{mobi,azw,azw3}");
    private final PathMatcher anyMatcher = FileSystems.getDefault().getPathMatcher("glob:**.{*}");
    private final PathMatcher uniqueMatcher = FileSystems.getDefault().getPathMatcher("glob:{license,readme,gradlew}");
    private final PathMatcher bookMatcher = FileSystems.getDefault().getPathMatcher("glob:{**book.asc,**book.txt,**book.asciidoc,**book.adoc,**book.ad}");
    private final PathMatcher wordMatcher = FileSystems.getDefault().getPathMatcher("glob:**.{doc,dot,docx,docm,dotx,dotm,docb,odt,fodt}");

    public boolean isPDF(Path path) {
        return pdfMatcher.matches(path);
    }

    public boolean isImage(Path path) {
        return imageMatcher.matches(path);
    }

    public boolean isHidden(Path path) {
        return IOHelper.isHidden(path);
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
        return true || Files.isDirectory(path)
                || isAsciidoc(path)
                || isImage(path)
                || isPDF(path)
                || isEpub(path)
                || isMobi(path)
                || isHTML(path)
                || isXML(path)
                || isMarkdown(path);
    }

    public boolean isOffice(Path path){
        return isWord(path) || isExcel(path) || isPPT(path);
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

    public boolean isWord(Path path) {
        return wordMatcher.matches(path);
    }
}
