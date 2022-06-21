package com.kodedu.config;

public enum PdfConverterType {
	
	FOP,
	ASCIIDOCTOR
	;
	
    public static boolean contains(String test) {

        for (PdfConverterType c : PdfConverterType.values()) {
            if (c.name().equals(test)) {
                return true;
            }
        }

        return false;
    }

}
