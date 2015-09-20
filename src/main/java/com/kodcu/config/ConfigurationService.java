package com.kodcu.config;

import com.kodcu.component.PreviewTab;
import com.kodcu.component.ToggleButtonBuilt;
import com.kodcu.controller.ApplicationController;
import com.kodcu.service.ThreadService;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
    private final ThreadService threadService;
    private final Accordion configAccordion = new Accordion();
    private final VBox configBox = new VBox(5);
    private final Tab mockTab = new PreviewTab("Editor Settings");

    @Autowired
    public ConfigurationService(LocationConfigBean locationConfigBean, EditorConfigBean editorConfigBean, PreviewConfigBean previewConfigBean, HtmlConfigBean htmlConfigBean, OdfConfigBean odfConfigBean, DocbookConfigBean docbookConfigBean, ApplicationController controller, StoredConfigBean storedConfigBean, ThreadService threadService) {
        this.locationConfigBean = locationConfigBean;
        this.editorConfigBean = editorConfigBean;
        this.previewConfigBean = previewConfigBean;
        this.htmlConfigBean = htmlConfigBean;
        this.odfConfigBean = odfConfigBean;
        this.docbookConfigBean = docbookConfigBean;
        this.controller = controller;
        this.storedConfigBean = storedConfigBean;
        this.threadService = threadService;
    }

    public void loadConfigurations(Runnable... runnables) {

        locationConfigBean.load();
        storedConfigBean.load();
        editorConfigBean.load();
        previewConfigBean.load();
        htmlConfigBean.load();
        odfConfigBean.load();
        docbookConfigBean.load();

        List<ConfigurationBase> configBeanList = Arrays.asList(editorConfigBean,
                locationConfigBean,
                previewConfigBean,
                htmlConfigBean,
                docbookConfigBean,
                odfConfigBean);

        ScrollPane formsPane = new ScrollPane();

        ToggleGroup toggleGroup = new ToggleGroup();
        FlowPane flowPane = new FlowPane(5, 5);

        List<ToggleButton> toggleButtons = new ArrayList<>();
        VBox editorConfigForm = null;

        for (ConfigurationBase configBean : configBeanList) {
            VBox form = configBean.createForm();
            ToggleButton toggleButton = ToggleButtonBuilt.item(configBean.formName()).click(event -> {
                formsPane.setContent(form);
            });
            toggleButtons.add(toggleButton);

            if (Objects.isNull(editorConfigForm))
                editorConfigForm = form;
        }

        final VBox finalEditorConfigForm = editorConfigForm;
        threadService.runActionLater(() -> {

            formsPane.setContent(finalEditorConfigForm);

            for (ToggleButton toggleButton : toggleButtons) {
                toggleGroup.getToggles().add(toggleButton);
                flowPane.getChildren().add(toggleButton);
            }

            configBox.getChildren().add(flowPane);
            configBox.getChildren().add(formsPane);

            VBox.setVgrow(formsPane, Priority.ALWAYS);

            for (Runnable runnable : runnables) {
                runnable.run();
            }
        });
    }

    public void showConfig() {

        TabPane previewTabPane = controller.getPreviewTabPane();
        ObservableList<Tab> tabs = previewTabPane.getTabs();

        if (!tabs.contains(mockTab)) {
            Tab configurationTab = new PreviewTab("Editor Settings", configBox);
            tabs.add(configurationTab);
        }

        previewTabPane.getSelectionModel().select(mockTab);
    }

}
