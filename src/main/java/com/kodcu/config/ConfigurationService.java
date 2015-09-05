package com.kodcu.config;

import com.kodcu.component.PreviewTab;
import com.kodcu.component.ScrollPaneBuilt;
import com.kodcu.controller.ApplicationController;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Created by usta on 17.07.2015.
 */
@Component
public class ConfigurationService {

    private final LocationConfigBean locationConfigBean;
    private final EditorConfigBean editorConfigBean;
    private final PreviewConfigBean previewConfigBean;
    private final HtmlConfigBean htmlConfigBean;
    private final OdfConfigBean odfConfigBean;
    private final DocbookConfigBean docbookConfigBean;
    private final ApplicationController controller;
    private final StoredConfigBean storedConfigBean;
    private final Accordion configAccordion = new Accordion();
    private final Tab mockTab = new PreviewTab("Editor Settings");

    @Autowired
    public ConfigurationService(LocationConfigBean locationConfigBean, EditorConfigBean editorConfigBean, PreviewConfigBean previewConfigBean, HtmlConfigBean htmlConfigBean, OdfConfigBean odfConfigBean, DocbookConfigBean docbookConfigBean, ApplicationController controller, StoredConfigBean storedConfigBean) {
        this.locationConfigBean = locationConfigBean;
        this.editorConfigBean = editorConfigBean;
        this.previewConfigBean = previewConfigBean;
        this.htmlConfigBean = htmlConfigBean;
        this.odfConfigBean = odfConfigBean;
        this.docbookConfigBean = docbookConfigBean;
        this.controller = controller;
        this.storedConfigBean = storedConfigBean;
    }

    public void loadConfigurations() {

        VBox locationConfigForm = locationConfigBean.createForm();
        VBox editorConfigForm = editorConfigBean.createForm();
        VBox previewConfigForm = previewConfigBean.createForm();
        VBox htmlConfigForm = htmlConfigBean.createForm();
        VBox odfConfigForm = odfConfigBean.createForm();
        VBox docbookConfigForm = docbookConfigBean.createForm();

        locationConfigBean.load();
        storedConfigBean.load();
        editorConfigBean.load();
        previewConfigBean.load();
        htmlConfigBean.load();
        odfConfigBean.load();
        docbookConfigBean.load();

        TitledPane editorConfigPane = new TitledPane("Editor Settings", ScrollPaneBuilt.content(editorConfigForm).full());
        TitledPane locationConfigPane = new TitledPane("Location Settings", ScrollPaneBuilt.content(locationConfigForm).full());
        TitledPane previewConfigPane = new TitledPane("Preview Settings", ScrollPaneBuilt.content(previewConfigForm).full());
        TitledPane htmlConfigPane = new TitledPane("Html Settings", ScrollPaneBuilt.content(htmlConfigForm).full());
        TitledPane odfConfigPane = new TitledPane("Odt Settings", ScrollPaneBuilt.content(odfConfigForm).full());
        TitledPane docbookConfigPane = new TitledPane("Docbook Settings", ScrollPaneBuilt.content(docbookConfigForm).full());

        configAccordion.setExpandedPane(editorConfigPane);
        configAccordion.getPanes().addAll(editorConfigPane, locationConfigPane, previewConfigPane, htmlConfigPane, odfConfigPane, docbookConfigPane);

    }

    public void showConfig() {

        TabPane previewTabPane = controller.getPreviewTabPane();
        ObservableList<Tab> tabs = previewTabPane.getTabs();

        if (!tabs.contains(mockTab)) {
            Tab configurationTab = new PreviewTab("Editor Settings", configAccordion);
            tabs.add(configurationTab);
        }

        previewTabPane.getSelectionModel().select(mockTab);
    }

}
