package com.kodedu.service;

import java.nio.file.Path;

/**
 * Created by usta on 07.09.2014.
 */
public interface PathResolverService {

    public boolean isPDF(Path path);

    public boolean isImage(Path path);

    public boolean isHidden(Path path);

    public boolean isMarkdown(Path path);

    public boolean isXML(Path path);

    public boolean isHTML(Path path);

    public boolean isAsciidoc(Path path);

    public boolean isViewable(Path path);

    public boolean isOffice(Path path);


    public boolean isBook(Path path);

    public boolean isMobi(Path path);

    public boolean isPPT(Path path);

    public boolean isExcel(Path path);

    public boolean isArchive(Path path);

    public boolean isVideo(Path path);

    public boolean isCSS(Path path);

    public boolean isBash(Path path);

    public boolean isCode(Path path);

    public boolean isAny(Path path);

    public boolean isEpub(Path path);

    public boolean isWord(Path path);
}
