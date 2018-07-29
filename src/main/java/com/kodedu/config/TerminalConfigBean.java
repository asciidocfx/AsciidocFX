package com.kodedu.config;

//import com.dooapp.fxform.annotation.Accessor;

import com.dooapp.fxform.FXForm;
import com.dooapp.fxform.builder.FXFormBuilder;
import com.dooapp.fxform.handler.NamedFieldHandler;
import com.dooapp.fxform.view.factory.DefaultFactoryProvider;
import com.kodedu.component.SliderBuilt;
import com.kodedu.config.factory.FileChooserEditableFactory;
import com.kodedu.config.factory.SliderFactory;
import com.kodedu.config.factory.SpinnerFactory;
import com.kodedu.controller.ApplicationController;
import com.kodedu.other.IOHelper;
import com.kodedu.service.ThreadService;
import com.kodedu.service.ui.TabService;
import com.kodedu.terminalfx.config.TerminalConfig;
import com.kodedu.terminalfx.helper.FxHelper;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import java.io.Reader;
import java.nio.file.Path;
import java.util.ResourceBundle;

/**
 * Created by usta on 17.07.2015.
 */
@Component
public class TerminalConfigBean extends ConfigurationBase {

    private StringProperty terminalWinCommand = new SimpleStringProperty("cmd.exe");
    private StringProperty terminalNixCommand = new SimpleStringProperty("/bin/bash -i");
    private ObjectProperty<Color> backgroundColor = new SimpleObjectProperty<>(Color.rgb(16, 16, 16));
    private ObjectProperty<Color> cursorColor = new SimpleObjectProperty<>(Color.WHITE);
    private ObjectProperty<Color> foregroundColor = new SimpleObjectProperty<>(Color.rgb(240, 240, 240));
    private BooleanProperty clearSelectionAfterCopy = new SimpleBooleanProperty(true);
    private BooleanProperty copyOnSelect = new SimpleBooleanProperty(false);
    private BooleanProperty ctrlCCopy = new SimpleBooleanProperty(true);
    private BooleanProperty ctrlVPaste = new SimpleBooleanProperty(true);
    private BooleanProperty cursorBlink = new SimpleBooleanProperty(false);
    private BooleanProperty enableClipboardNotice = new SimpleBooleanProperty(true);
    private BooleanProperty scrollbarVisible = new SimpleBooleanProperty(true);
    private BooleanProperty useDefaultWindowCopy = new SimpleBooleanProperty(true);
    private StringProperty fontFamily = new SimpleStringProperty("'DejaVu Sans Mono', 'Everson Mono', FreeMono, 'Menlo', 'Terminal', monospace");
    private IntegerProperty fontSize = new SimpleIntegerProperty(14);
    private DoubleProperty scrollWhellMoveMultiplier = new SimpleDoubleProperty(0.1);
    private StringProperty receiveEncoding = new SimpleStringProperty("utf-8");
    private StringProperty sendEncoding = new SimpleStringProperty("raw");
    private StringProperty userCss = new SimpleStringProperty("data:text/plain;base64,eC1zY3JlZW4geyBjdXJzb3I6IGF1dG87IH0=");

    public String getTerminalWinCommand() {
        return terminalWinCommand.getValue();
    }

    public StringProperty terminalWinCommandProperty() {
        return terminalWinCommand;
    }

    public void setTerminalWinCommand(String terminalWinCommand) {
        this.terminalWinCommand.set(terminalWinCommand);
    }

    public String getTerminalNixCommand() {
        return terminalNixCommand.getValue();
    }

    public StringProperty terminalNixCommandProperty() {
        return terminalNixCommand;
    }

    public void setTerminalNixCommand(String terminalNixCommand) {
        this.terminalNixCommand.set(terminalNixCommand);
    }

    public Color getBackgroundColor() {
        return backgroundColor.getValue();
    }

    public ObjectProperty<Color> backgroundColorProperty() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor.set(backgroundColor);
    }

    public Color getCursorColor() {
        return cursorColor.getValue();
    }

    public ObjectProperty<Color> cursorColorProperty() {
        return cursorColor;
    }

    public void setCursorColor(Color cursorColor) {
        this.cursorColor.set(cursorColor);
    }

    public Color getForegroundColor() {
        return foregroundColor.getValue();
    }

    public ObjectProperty<Color> foregroundColorProperty() {
        return foregroundColor;
    }

    public void setForegroundColor(Color foregroundColor) {
        this.foregroundColor.set(foregroundColor);
    }

    public Boolean isClearSelectionAfterCopy() {
        return clearSelectionAfterCopy.getValue();
    }

    public BooleanProperty clearSelectionAfterCopyProperty() {
        return clearSelectionAfterCopy;
    }

    public void setClearSelectionAfterCopy(boolean clearSelectionAfterCopy) {
        this.clearSelectionAfterCopy.set(clearSelectionAfterCopy);
    }

    public Boolean isCopyOnSelect() {
        return copyOnSelect.getValue();
    }

    public BooleanProperty copyOnSelectProperty() {
        return copyOnSelect;
    }

    public void setCopyOnSelect(boolean copyOnSelect) {
        this.copyOnSelect.set(copyOnSelect);
    }

    public Boolean isCtrlCCopy() {
        return ctrlCCopy.getValue();
    }

    public BooleanProperty ctrlCCopyProperty() {
        return ctrlCCopy;
    }

    public void setCtrlCCopy(boolean ctrlCCopy) {
        this.ctrlCCopy.set(ctrlCCopy);
    }

    public Boolean isCtrlVPaste() {
        return ctrlVPaste.getValue();
    }

    public BooleanProperty ctrlVPasteProperty() {
        return ctrlVPaste;
    }

    public void setCtrlVPaste(boolean ctrlVPaste) {
        this.ctrlVPaste.set(ctrlVPaste);
    }

    public boolean isCursorBlink() {
        return cursorBlink.getValue();
    }

    public BooleanProperty cursorBlinkProperty() {
        return cursorBlink;
    }

    public void setCursorBlink(boolean cursorBlink) {
        this.cursorBlink.set(cursorBlink);
    }

    public Boolean isEnableClipboardNotice() {
        return enableClipboardNotice.getValue();
    }

    public BooleanProperty enableClipboardNoticeProperty() {
        return enableClipboardNotice;
    }

    public void setEnableClipboardNotice(boolean enableClipboardNotice) {
        this.enableClipboardNotice.set(enableClipboardNotice);
    }

    public Boolean isScrollbarVisible() {
        return scrollbarVisible.getValue();
    }

    public BooleanProperty scrollbarVisibleProperty() {
        return scrollbarVisible;
    }

    public void setScrollbarVisible(boolean scrollbarVisible) {
        this.scrollbarVisible.set(scrollbarVisible);
    }

    public Boolean isUseDefaultWindowCopy() {
        return useDefaultWindowCopy.getValue();
    }

    public BooleanProperty useDefaultWindowCopyProperty() {
        return useDefaultWindowCopy;
    }

    public void setUseDefaultWindowCopy(boolean useDefaultWindowCopy) {
        this.useDefaultWindowCopy.set(useDefaultWindowCopy);
    }

    public String getFontFamily() {
        return fontFamily.getValue();
    }

    public StringProperty fontFamilyProperty() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily.set(fontFamily);
    }

    public Integer getFontSize() {
        return fontSize.getValue();
    }

    public IntegerProperty fontSizeProperty() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize.set(fontSize);
    }

    public Double getScrollWhellMoveMultiplier() {
        return scrollWhellMoveMultiplier.getValue();
    }

    public DoubleProperty scrollWhellMoveMultiplierProperty() {
        return scrollWhellMoveMultiplier;
    }

    public void setScrollWhellMoveMultiplier(double scrollWhellMoveMultiplier) {
        this.scrollWhellMoveMultiplier.set(scrollWhellMoveMultiplier);
    }

    public String getReceiveEncoding() {
        return receiveEncoding.getValue();
    }

    public StringProperty receiveEncodingProperty() {
        return receiveEncoding;
    }

    public void setReceiveEncoding(String receiveEncoding) {
        this.receiveEncoding.set(receiveEncoding);
    }

    public String getSendEncoding() {
        return sendEncoding.getValue();
    }

    public StringProperty sendEncodingProperty() {
        return sendEncoding;
    }

    public void setSendEncoding(String sendEncoding) {
        this.sendEncoding.set(sendEncoding);
    }

    public String getUserCss() {
        return userCss.getValue();
    }

    public StringProperty userCssProperty() {
        return userCss;
    }

    public void setUserCss(String userCss) {
        this.userCss.set(userCss);
    }

    private Logger logger = LoggerFactory.getLogger(TerminalConfigBean.class);

    private final ApplicationController controller;
    private final ThreadService threadService;
    private final TabService tabService;

    private final Button saveButton = new Button("Save");
    private final Button loadButton = new Button("Load");
    private final Label infoLabel = new Label();

    @Autowired
    public TerminalConfigBean(ApplicationController controller, ThreadService threadService, TabService tabService) {
        super(controller, threadService);
        this.controller = controller;
        this.threadService = threadService;
        this.tabService = tabService;
    }


    @Override
    public String formName() {
        return "Terminal Settings";
    }

    @Override
    public VBox createForm() {

        FXForm editorConfigForm = new FXFormBuilder<>()
                .resourceBundle(ResourceBundle.getBundle("terminalConfig"))
                .includeAndReorder(
                        "terminalWinCommand",
                        "terminalNixCommand",
                        "backgroundColor",
                        "cursorColor",
                        "foregroundColor",
                        "clearSelectionAfterCopy",
                        "copyOnSelect",
                        "ctrlCCopy",
                        "ctrlVPaste",
                        "cursorBlink",
                        "enableClipboardNotice",
                        "scrollbarVisible",
                        "useDefaultWindowCopy",
                        "fontFamily",
                        "fontSize",
                        "scrollWhellMoveMultiplier",
                        "receiveEncoding",
                        "sendEncoding",
                        "userCss")
                .build();

        DefaultFactoryProvider editorConfigFormProvider = new DefaultFactoryProvider();

        editorConfigFormProvider.addFactory(new NamedFieldHandler("scrollWhellMoveMultiplier"), new SliderFactory(SliderBuilt.create(0.0, 1, 0.1).step(0.1)));
        editorConfigFormProvider.addFactory(new NamedFieldHandler("fontSize"), new SpinnerFactory(new Spinner(8, 32, 14)));
        FileChooserEditableFactory fileChooserEditableFactory = new FileChooserEditableFactory();
        editorConfigForm.setEditorFactoryProvider(editorConfigFormProvider);

        fileChooserEditableFactory.setOnEdit(tabService::addTab);

        editorConfigForm.setSource(this);

        VBox vBox = new VBox();
        vBox.getChildren().add(editorConfigForm);

        saveButton.setOnAction(this::save);
        loadButton.setOnAction(this::load);
        HBox box = new HBox(5, saveButton, loadButton, infoLabel);
        box.setPadding(new Insets(0, 0, 15, 5));
        vBox.getChildren().add(box);

        return vBox;
    }

    @Override
    public Path getConfigPath() {
        return super.resolveConfigPath("terminal_config.json");
    }

    @Override
    public void load(Path configPath, ActionEvent... actionEvent) {

        fadeOut(infoLabel, "Loading...");

        Reader fileReader = IOHelper.fileReader(configPath);
        JsonReader jsonReader = Json.createReader(fileReader);

        JsonObject jsonObject = jsonReader.readObject();

        String terminalWinCommand = jsonObject.getString("terminalWinCommand", this.terminalWinCommand.getValue());
        String terminalNixCommand = jsonObject.getString("terminalNixCommand", this.terminalNixCommand.getValue());
        String backgroundColor = jsonObject.getString("backgroundColor", FxHelper.colorToHex(this.backgroundColor.getValue()));
        String cursorColor = jsonObject.getString("cursorColor", FxHelper.colorToHex(this.backgroundColor.getValue()));
        String foregroundColor = jsonObject.getString("foregroundColor", FxHelper.colorToHex(this.backgroundColor.getValue()));
        Boolean clearSelectionAfterCopy = jsonObject.getBoolean("clearSelectionAfterCopy", this.clearSelectionAfterCopy.getValue());
        Boolean copyOnSelect = jsonObject.getBoolean("copyOnSelect", this.copyOnSelect.getValue());
        Boolean ctrlCCopy = jsonObject.getBoolean("ctrlCCopy", this.ctrlCCopy.getValue());
        Boolean ctrlVPaste = jsonObject.getBoolean("ctrlVPaste", this.ctrlVPaste.getValue());
        Boolean cursorBlink = jsonObject.getBoolean("cursorBlink", this.cursorBlink.getValue());
        Boolean enableClipboardNotice = jsonObject.getBoolean("enableClipboardNotice", this.enableClipboardNotice.getValue());
        Boolean scrollbarVisible = jsonObject.getBoolean("scrollbarVisible", this.scrollbarVisible.getValue());
        Boolean useDefaultWindowCopy = jsonObject.getBoolean("useDefaultWindowCopy", this.useDefaultWindowCopy.getValue());
        String fontFamily = jsonObject.getString("fontFamily", this.fontFamily.getValue());
        Integer fontSize = jsonObject.getInt("fontSize", this.fontSize.getValue());
        String receiveEncoding = jsonObject.getString("receiveEncoding", this.receiveEncoding.getValue());
        String sendEncoding = jsonObject.getString("sendEncoding", this.sendEncoding.getValue());
        String userCss = jsonObject.getString("userCss", this.userCss.getValue());

        IOHelper.close(jsonReader, fileReader);

        threadService.runActionLater(() -> {

            this.setTerminalWinCommand(terminalWinCommand);
            this.setTerminalNixCommand(terminalNixCommand);
            this.setBackgroundColor(Color.web(backgroundColor));
            this.setCursorColor(Color.web(cursorColor));
            this.setForegroundColor(Color.web(foregroundColor));
            this.setClearSelectionAfterCopy(clearSelectionAfterCopy);
            this.setCopyOnSelect(copyOnSelect);
            this.setCtrlCCopy(ctrlCCopy);
            this.setCtrlVPaste(ctrlVPaste);
            this.setCursorBlink(cursorBlink);
            this.setEnableClipboardNotice(enableClipboardNotice);
            this.setScrollbarVisible(scrollbarVisible);
            this.setUseDefaultWindowCopy(useDefaultWindowCopy);
            this.setFontFamily(fontFamily);
            this.setFontSize(fontSize);
            this.setReceiveEncoding(receiveEncoding);
            this.setSendEncoding(sendEncoding);
            this.setUserCss(userCss);

            if (jsonObject.containsKey("scrollWhellMoveMultiplier")) {
                this.setScrollWhellMoveMultiplier(jsonObject.getJsonNumber("scrollWhellMoveMultiplier").doubleValue());
            }

            fadeOut(infoLabel, "Loaded...");

        });
    }

    @Override
    public void save(ActionEvent... actionEvent) {

        infoLabel.setText("Saving...");
        saveJson(getJSON());
        fadeOut(infoLabel, "Saved...");
    }

    @Override
    public JsonObject getJSON() {
        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();

        objectBuilder
                .add("terminalWinCommand", getTerminalWinCommand())
                .add("terminalNixCommand", getTerminalNixCommand())
                .add("backgroundColor", FxHelper.colorToHex(getBackgroundColor()))
                .add("cursorColor", FxHelper.colorToHex(getCursorColor()))
                .add("foregroundColor", FxHelper.colorToHex(getForegroundColor()))
                .add("clearSelectionAfterCopy", isClearSelectionAfterCopy())
                .add("copyOnSelect", isCopyOnSelect())
                .add("ctrlCCopy", isCtrlCCopy())
                .add("ctrlVPaste", isCtrlVPaste())
                .add("cursorBlink", isCursorBlink())
                .add("enableClipboardNotice", isEnableClipboardNotice())
                .add("scrollbarVisible", isScrollbarVisible())
                .add("useDefaultWindowCopy", isUseDefaultWindowCopy())
                .add("fontFamily", getFontFamily())
                .add("fontSize", getFontSize())
                .add("scrollWhellMoveMultiplier", getScrollWhellMoveMultiplier())
                .add("receiveEncoding", getReceiveEncoding())
                .add("sendEncoding", getSendEncoding())
                .add("userCss", getUserCss());

        return objectBuilder.build();
    }

    public TerminalConfig createTerminalConfig() {
        TerminalConfig terminalConfig = new TerminalConfig();
        terminalConfig.setBackgroundColor(getBackgroundColor());
        terminalConfig.setCursorColor(getCursorColor());
        terminalConfig.setForegroundColor(getForegroundColor());
        terminalConfig.setClearSelectionAfterCopy(isClearSelectionAfterCopy());
        terminalConfig.setCopyOnSelect(isCopyOnSelect());
        terminalConfig.setCtrlCCopy(isCtrlCCopy());
        terminalConfig.setCtrlVPaste(isCtrlVPaste());
        terminalConfig.setCursorBlink(isCursorBlink());
        terminalConfig.setEnableClipboardNotice(isEnableClipboardNotice());
        terminalConfig.setScrollbarVisible(isScrollbarVisible());
        terminalConfig.setUseDefaultWindowCopy(isUseDefaultWindowCopy());
        terminalConfig.setFontFamily(getFontFamily());
        terminalConfig.setFontSize(getFontSize());
        terminalConfig.setScrollWhellMoveMultiplier(getScrollWhellMoveMultiplier());
        terminalConfig.setReceiveEncoding(getReceiveEncoding());
        terminalConfig.setSendEncoding(getSendEncoding());
        terminalConfig.setUserCss(getUserCss());
        terminalConfig.setWindowsTerminalStarter(getTerminalWinCommand());
        terminalConfig.setUnixTerminalStarter(getTerminalNixCommand());

        return terminalConfig;
    }

    public void changeTheme(EditorConfigBean.Theme theme) {
        Platform.runLater(() -> {
            if (theme.getThemeName().equals("Dark")) {
                setBackgroundColor(Color.rgb(16, 16, 16));
                setForegroundColor(Color.rgb(240, 240, 240));
                setCursorColor(Color.WHITE);
            } else if (theme.getThemeName().equals("Default")) {
                setBackgroundColor(Color.WHITE);
                setForegroundColor(Color.BLACK);
                setCursorColor(Color.BLACK);
            }
        });
    }
}
