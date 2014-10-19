package com.kodcu.service;

import com.kodcu.other.IOHelper;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Created by usta on 19.10.2014.
 */
@Component
public class NashornService {


    private NashornScriptEngine engine;

//    @PostConstruct
    public void init() throws FileNotFoundException, ScriptException {

        engine= (NashornScriptEngine) new NashornScriptEngineFactory().getScriptEngine();
        engine.eval(new FileReader("asciidoctor-all.js"));
        engine.eval(new FileReader("asciidoctor-docbook.js"));
    }


    public  String renderToHtml(String text) {
        String rendered = null;
        try {
            rendered = (String) engine.eval(String.format("Opal.Asciidoctor.$render('%s')", IOHelper.normalize(text)));
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        return rendered;
    }
}
