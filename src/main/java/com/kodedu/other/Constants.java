package com.kodedu.other;

import java.util.regex.Pattern;

/**
 * Created by usta on 16.12.2014.
 */
public class Constants {

    public static final Pattern URL_MATCH = Pattern.compile("((https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])", Pattern.MULTILINE);
    public static final Pattern IMAGE_URL_MATCH = Pattern.compile("((https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|](.png|.svg|.jpg|.bmp|.gif))", Pattern.MULTILINE);

    public static final String ASCIIDOC_EXTENSIONS = ".*?\\.(asc|adoc|jam|ad|asciidoc)";
}
