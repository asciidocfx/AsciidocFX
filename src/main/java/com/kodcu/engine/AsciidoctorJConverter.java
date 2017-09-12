package com.kodcu.engine;

import com.kodcu.other.ConverterResult;
import com.kodcu.other.Current;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.OptionsBuilder;
import org.asciidoctor.SafeMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("AsciidoctorJEngine")
public class AsciidoctorJConverter implements AsciidocConvertible {

    Asciidoctor asciidoctor = Asciidoctor.Factory.create();
    private final Current current;

    @Autowired
    public AsciidoctorJConverter(Current current) {
        this.current = current;
    }

    @Override
    public ConverterResult convertDocbook(String asciidoc) {

        Map<String, Object> map = OptionsBuilder.options()
                .backend("docbook5")
                .headerFooter(true)
                .inPlace(true)
                .safe(SafeMode.SAFE)
                .asMap();
        String convert = asciidoctor.convert(asciidoc, map);
        return new ConverterResult(null, convert, "docbook5", "article");
    }

    @Override
    public ConverterResult convertAsciidoc(String asciidoc) {
        Map<String, Object> map = OptionsBuilder.options()
                .backend("html5")
                .headerFooter(false)
                .inPlace(true)
                .safe(SafeMode.SAFE)
                .asMap();
        String convert = asciidoctor.convert(asciidoc, map);

        return new ConverterResult(null, convert, "html5", "article");
    }

    @Override
    public ConverterResult convertHtml(String asciidoc) {
        return null;
    }

    @Override
    public void convertOdf(String asciidoc) {

    }

    @Override
    public void fillOutlines(Object doc) {

    }

    @Override
    public String applyReplacements(String asciidoc) {
        return null;
    }
}
