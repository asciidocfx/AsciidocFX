package com.kodedu.config;

import com.kodedu.component.ToggleButtonBuilt;
import com.kodedu.controller.ApplicationController;
import com.kodedu.service.ThreadService;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
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
    private final DocbookConfigBean docbookConfigBean;
    private final ApplicationController controller;
    private final StoredConfigBean storedConfigBean;
    private final ThreadService threadService;
    private final SpellcheckConfigBean spellcheckConfigBean;
    private final TerminalConfigBean terminalConfigBean;
    private final ExtensionConfigBean extensionConfigBean;
    private final Epub3ConfigBean epub3ConfigBean;
    private final RevealjsConfigBean revealjsConfigBean;
    private VBox configBox;
    private final AsciidoctorConfigBase<PdfConfigAttributes> pdfConfigBean;

    @Autowired
    public ConfigurationService(LocationConfigBean locationConfigBean, EditorConfigBean editorConfigBean,
                                PreviewConfigBean previewConfigBean, HtmlConfigBean htmlConfigBean,
                                DocbookConfigBean docbookConfigBean, ApplicationController controller,
                                StoredConfigBean storedConfigBean, ThreadService threadService,
                                SpellcheckConfigBean spellcheckConfigBean, TerminalConfigBean terminalConfigBean,
                                ExtensionConfigBean extensionConfigBean, Epub3ConfigBean epub3ConfigBean, RevealjsConfigBean revealjsConfigBean, AsciidoctorConfigBase<PdfConfigAttributes> pdfConfigBean) {
        this.locationConfigBean = locationConfigBean;
        this.editorConfigBean = editorConfigBean;
        this.previewConfigBean = previewConfigBean;
        this.htmlConfigBean = htmlConfigBean;
        this.docbookConfigBean = docbookConfigBean;
        this.controller = controller;
        this.storedConfigBean = storedConfigBean;
        this.threadService = threadService;
        this.spellcheckConfigBean = spellcheckConfigBean;
        this.terminalConfigBean = terminalConfigBean;
        this.extensionConfigBean = extensionConfigBean;
        this.epub3ConfigBean = epub3ConfigBean;
        this.revealjsConfigBean = revealjsConfigBean;
        this.pdfConfigBean = pdfConfigBean;
    }

    public void loadConfigurations(Runnable... runnables) {

        locationConfigBean.load();
        storedConfigBean.load();
        editorConfigBean.load();
        previewConfigBean.load();
        htmlConfigBean.load();
        docbookConfigBean.load();
        spellcheckConfigBean.load();
        terminalConfigBean.load();
        extensionConfigBean.load();
        pdfConfigBean.load();
        epub3ConfigBean.load();
        revealjsConfigBean.load();

        List<ConfigurationBase> configBeanList = Arrays.asList(
                editorConfigBean,
                terminalConfigBean,
                locationConfigBean,
                previewConfigBean,
                htmlConfigBean,
                docbookConfigBean,
//                odfConfigBean,
                extensionConfigBean,
                pdfConfigBean,
                epub3ConfigBean,
                revealjsConfigBean
//                ,spellcheckConfigBean
        );

        ScrollPane formsPane = new ScrollPane();

        ToggleGroup toggleGroup = new ToggleGroup();
        controller.setConfigToggleGroup(toggleGroup);
        FlowPane flowPane = new FlowPane(5, 5);
        flowPane.setPadding(new Insets(5, 0, 0, 0));

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

            configBox = controller.getConfigBox();
            configBox.getChildren().add(flowPane);
            configBox.getChildren().add(formsPane);

            VBox.setVgrow(formsPane, Priority.ALWAYS);

            for (Runnable runnable : runnables) {
                runnable.run();
            }
        });
    }

}
