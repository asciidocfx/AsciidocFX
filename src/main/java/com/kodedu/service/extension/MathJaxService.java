package com.kodedu.service.extension;

import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.springframework.stereotype.Component;

/**
 * Created by usta on 25.12.2014.
 */
public interface MathJaxService {
    public final static String label = "core::service::extension::MathJax";

    public void reload();

    public void processFormula(String formula, String imagesDir, String imageTarget);

    public JSObject getWindow();

    public WebView getWebView();

    public void snapshotFormula(String formula, String imagesDir, String imageTarget);

}
