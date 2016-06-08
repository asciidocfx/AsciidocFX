package com.kodcu.config;

//import com.dooapp.fxform.annotation.Accessor;

import com.dooapp.fxform.FXForm;
import com.dooapp.fxform.builder.FXFormBuilder;
import com.dooapp.fxform.handler.NamedFieldHandler;
import com.dooapp.fxform.view.factory.DefaultFactoryProvider;
import com.kodcu.component.SliderBuilt;
import com.kodcu.config.factory.FileChooserEditableFactory;
import com.kodcu.config.factory.ListChoiceBoxFactory;
import com.kodcu.config.factory.SliderFactory;
import com.kodcu.config.factory.SpinnerFactory;
import com.kodcu.controller.ApplicationController;
import com.kodcu.other.IOHelper;
import com.kodcu.service.ThreadService;
import com.kodcu.service.ui.TabService;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.joox.JOOX;
import org.joox.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.json.*;
import java.io.Reader;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by usta on 17.07.2015.
 */
@Component
public class EditorConfigBean extends ConfigurationBase {


    private ObjectProperty<ObservableList<String>> editorTheme = new SimpleObjectProperty<>(FXCollections.observableArrayList());
    private DoubleProperty firstSplitter = new SimpleDoubleProperty(0.17551963048498845);
    private DoubleProperty secondSplitter = new SimpleDoubleProperty(0.5996920708237106);
    private StringProperty fontFamily = new SimpleStringProperty("'Monaco', 'Menlo', 'Ubuntu Mono', 'Consolas', 'source-code-pro', monospace");
    private ObjectProperty<Integer> fontSize = new SimpleObjectProperty(14);
    private DoubleProperty scrollSpeed = new SimpleDoubleProperty(0.1);
    private BooleanProperty useWrapMode = new SimpleBooleanProperty(true);
    private ObjectProperty<Integer> wrapLimit = new SimpleObjectProperty<>(0);
    private BooleanProperty showGutter = new SimpleBooleanProperty(false);
    private ObjectProperty<ObservableList<String>> defaultLanguage = new SimpleObjectProperty<>(FXCollections.observableArrayList());
    private BooleanProperty autoUpdate = new SimpleBooleanProperty(true);
    private StringProperty terminalCharset = new SimpleStringProperty("UTF-8");
    private StringProperty terminalWinCommand = new SimpleStringProperty("cmd.exe");
    private StringProperty terminalNixCommand = new SimpleStringProperty("/bin/bash");
    private BooleanProperty showDonate = new SimpleBooleanProperty(true);
    private BooleanProperty validateDocbook = new SimpleBooleanProperty(true);
    private StringProperty clipboardImageFilePattern = new SimpleStringProperty("'Image'-ddMMyy-hhmmss.SSS'.png'");

    private Logger logger = LoggerFactory.getLogger(EditorConfigBean.class);

    private final ApplicationController controller;
    private final ThreadService threadService;
    private final TabService tabService;

    private final Button saveButton = new Button("Save");
    private final Button loadButton = new Button("Load");
    private final Label infoLabel = new Label();

    @Autowired
    public EditorConfigBean(ApplicationController controller, ThreadService threadService, TabService tabService) {
        super(controller, threadService);
        this.controller = controller;
        this.threadService = threadService;
        this.tabService = tabService;
    }

    public Integer getWrapLimit() {
        return wrapLimit.get();
    }

    public ObjectProperty<Integer> wrapLimitProperty() {
        return wrapLimit;
    }

    public void setWrapLimit(Integer wrapLimit) {
        this.wrapLimit.set(wrapLimit);
    }

    public boolean getShowGutter() {
        return showGutter.get();
    }

    public BooleanProperty showGutterProperty() {
        return showGutter;
    }

    public void setShowGutter(boolean showGutter) {
        this.showGutter.set(showGutter);
    }

    public boolean getUseWrapMode() {
        return useWrapMode.get();
    }

    public BooleanProperty useWrapModeProperty() {
        return useWrapMode;
    }

    public void setUseWrapMode(boolean useWrapMode) {
        this.useWrapMode.set(useWrapMode);
    }

    public ObservableList<String> getEditorTheme() {
        return editorTheme.get();
    }

    public ObjectProperty<ObservableList<String>> editorThemeProperty() {
        return editorTheme;
    }

    public void setEditorTheme(ObservableList<String> editorTheme) {
        this.editorTheme.set(editorTheme);
    }

    public String getFontFamily() {
        return fontFamily.get();
    }

    public StringProperty fontFamilyProperty() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily.set(fontFamily);
    }

    public Integer getFontSize() {
        return fontSize.get();
    }

    public ObjectProperty<Integer> fontSizeProperty() {
        return fontSize;
    }

    public void setFontSize(Integer fontSize) {
        this.fontSize.set(fontSize);
    }

    public double getScrollSpeed() {
        return scrollSpeed.get();
    }

    public DoubleProperty scrollSpeedProperty() {
        return scrollSpeed;
    }

    public void setScrollSpeed(double scrollSpeed) {
        this.scrollSpeed.set(scrollSpeed);
    }

    public ObservableList<String> getDefaultLanguage() {
        return defaultLanguage.get();
    }

    public ObjectProperty<ObservableList<String>> defaultLanguageProperty() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(ObservableList<String> defaultLanguage) {
        this.defaultLanguage.set(defaultLanguage);
    }

    public double getFirstSplitter() {
        return firstSplitter.get();
    }

    public DoubleProperty firstSplitterProperty() {
        return firstSplitter;
    }

    public void setFirstSplitter(double firstSplitter) {
        this.firstSplitter.set(firstSplitter);
    }

    public double getSecondSplitter() {
        return secondSplitter.get();
    }

    public DoubleProperty secondSplitterProperty() {
        return secondSplitter;
    }

    public void setSecondSplitter(double secondSplitter) {
        this.secondSplitter.set(secondSplitter);
    }

    public boolean getAutoUpdate() {
        return autoUpdate.get();
    }

    public BooleanProperty autoUpdateProperty() {
        return autoUpdate;
    }

    public void setAutoUpdate(boolean autoUpdate) {
        this.autoUpdate.set(autoUpdate);
    }

    public String getTerminalCharset() {
        return terminalCharset.get();
    }

    public StringProperty terminalCharsetProperty() {
        return terminalCharset;
    }

    public void setTerminalCharset(String terminalCharset) {
        this.terminalCharset.set(terminalCharset);
    }

    public String getTerminalNixCommand() {
        return terminalNixCommand.get();
    }

    public StringProperty terminalNixCommandProperty() {
        return terminalNixCommand;
    }

    public void setTerminalNixCommand(String terminalNixCommand) {
        this.terminalNixCommand.set(terminalNixCommand);
    }

    public String getTerminalWinCommand() {
        return terminalWinCommand.get();
    }

    public StringProperty terminalWinCommandProperty() {
        return terminalWinCommand;
    }

    public void setTerminalWinCommand(String terminalWinCommand) {
        this.terminalWinCommand.set(terminalWinCommand);
    }

    public boolean getShowDonate() {
        return showDonate.get();
    }

    public BooleanProperty showDonateProperty() {
        return showDonate;
    }

    public void setShowDonate(boolean showDonate) {
        this.showDonate.set(showDonate);
    }

    public boolean getValidateDocbook() {
        return validateDocbook.get();
    }

    public BooleanProperty validateDocbookProperty() {
        return validateDocbook;
    }

    public void setValidateDocbook(boolean validateDocbook) {
        this.validateDocbook.set(validateDocbook);
    }

    public String getClipboardImageFilePattern() {
        return clipboardImageFilePattern.get();
    }

    public StringProperty clipboardImageFilePatternProperty() {
        return clipboardImageFilePattern;
    }

    public void setClipboardImageFilePattern(String clipboardImageFilePattern) {
        this.clipboardImageFilePattern.set(clipboardImageFilePattern);
    }

    @Override
    public String formName() {
        return "Editor Settings";
    }

    @Override
    public VBox createForm() {

        FXForm editorConfigForm = new FXFormBuilder<>()
                .resourceBundle(ResourceBundle.getBundle("editorConfig"))
                .includeAndReorder("showDonate", "validateDocbook", "editorTheme", "fontFamily", "fontSize",
                        "scrollSpeed", "useWrapMode", "wrapLimit", "showGutter", "defaultLanguage", "autoUpdate",
                        "terminalWinCommand", "terminalNixCommand", "terminalCharset","clipboardImageFilePattern")
                .build();

        DefaultFactoryProvider editorConfigFormProvider = new DefaultFactoryProvider();

        editorConfigFormProvider.addFactory(new NamedFieldHandler("editorTheme"), new ListChoiceBoxFactory(new ChoiceBox()));
        editorConfigFormProvider.addFactory(new NamedFieldHandler("defaultLanguage"), new ListChoiceBoxFactory(new ChoiceBox()));
        editorConfigFormProvider.addFactory(new NamedFieldHandler("scrollSpeed"), new SliderFactory(SliderBuilt.create(0.0, 1, 0.1).step(0.1)));
        editorConfigFormProvider.addFactory(new NamedFieldHandler("fontSize"), new SpinnerFactory(new Spinner(8, 32, 14)));
        editorConfigFormProvider.addFactory(new NamedFieldHandler("wrapLimit"), new SpinnerFactory(new Spinner(0, 500, 0)));
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
        return super.resolveConfigPath("editor_config.json");
    }

    @Override
    public void load(ActionEvent... actionEvent) {

        fadeOut(infoLabel, "Loading...");

        List<String> aceThemeList = IOHelper.readAllLines(getConfigDirectory().resolve("ace_themes.txt"));
        List<String> languageList = this.languageList();

        Reader fileReader = IOHelper.fileReader(getConfigPath());
        JsonReader jsonReader = Json.createReader(fileReader);

        JsonObject jsonObject = jsonReader.readObject();

        String fontFamily = jsonObject.getString("fontFamily", "'Monaco', 'Menlo', 'Ubuntu Mono', 'Consolas', 'source-code-pro', monospace");
        int fontSize = jsonObject.getInt("fontSize", 14);
        String theme = jsonObject.getString("editorTheme", "xcode");
        String defaultLanguage = jsonObject.getString("defaultLanguage", "en");
        boolean useWrapMode = jsonObject.getBoolean("useWrapMode", true);
        boolean showGutter = jsonObject.getBoolean("showGutter", false);
        int wrapLimit = jsonObject.getInt("wrapLimit", 0);
        boolean autoUpdate = jsonObject.getBoolean("autoUpdate", true);
        String terminalWinCommand = jsonObject.getString("terminalWinCommand", "cmd.exe");
        String terminalNixCommand = jsonObject.getString("terminalNixCommand", "/bin/bash");
        String terminalCharset = jsonObject.getString("terminalCharset", "UTF-8");
        boolean showDonate = jsonObject.getBoolean("showDonate", true);
        final boolean validateDocbook = jsonObject.getBoolean("validateDocbook", true);
        String clipboardImageFilePattern = jsonObject.getString("clipboardImageFilePattern", "'Image'-ddMMyy-hhmmss.SSS'.png'");

        IOHelper.close(jsonReader, fileReader);

        threadService.runActionLater(() -> {
            this.setEditorTheme(FXCollections.observableList(aceThemeList));
            this.setDefaultLanguage(FXCollections.observableList(languageList));
            this.setFontFamily(fontFamily);
            this.setFontSize(fontSize);
            this.setUseWrapMode(useWrapMode);
            this.setShowGutter(showGutter);
            this.setWrapLimit(wrapLimit);
            this.setAutoUpdate(autoUpdate);
            this.setTerminalWinCommand(terminalWinCommand);
            this.setTerminalNixCommand(terminalNixCommand);
            this.setTerminalCharset(terminalCharset);
            this.setShowDonate(showDonate);
            this.setValidateDocbook(validateDocbook);
            this.setClipboardImageFilePattern(clipboardImageFilePattern);

            if (jsonObject.containsKey("scrollSpeed")) {
                this.setScrollSpeed(jsonObject.getJsonNumber("scrollSpeed").doubleValue());
            }

            if (jsonObject.containsKey("firstSplitter")) {
                JsonNumber firstSplitter = jsonObject.getJsonNumber("firstSplitter");
                this.setFirstSplitter(firstSplitter.doubleValue());
            }

            if (jsonObject.containsKey("secondSplitter")) {
                JsonNumber secondSplitter = jsonObject.getJsonNumber("secondSplitter");
                this.setSecondSplitter(secondSplitter.doubleValue());
            }

            this.getEditorTheme().set(0, theme);
            this.getDefaultLanguage().set(0, defaultLanguage);

            fadeOut(infoLabel, "Loaded...");

        });
    }

    private List<String> languageList() {

        Path configPath = controller.getConfigPath();

        Stream<Path> languageStream = IOHelper.list(configPath.resolve("docbook/common"));
        List<String> languageList = languageStream.parallel().filter(p -> !p.endsWith("xml"))
                .map(path -> {
                    try {
                        Match $ = JOOX.$(path.toFile());
                        String language = $.attr("language");
                        String languageName = $.attr("english-language-name");

                        Objects.requireNonNull(languageName);
                        Objects.requireNonNull(language);

                        return language;
                    } catch (Exception ex) {
                        // no-op
                    }

                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return languageList;
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
                .add("fontFamily", getFontFamily())
                .add("fontSize", getFontSize())
                .add("scrollSpeed", getScrollSpeed())
                .add("useWrapMode", getUseWrapMode())
                .add("wrapLimit", getWrapLimit())
                .add("showGutter", getShowGutter())
                .add("editorTheme", getEditorTheme().get(0))
                .add("defaultLanguage", getDefaultLanguage().get(0))
                .add("firstSplitter", getFirstSplitter())
                .add("secondSplitter", getSecondSplitter())
                .add("autoUpdate", getAutoUpdate())
                .add("terminalCharset", getTerminalCharset())
                .add("terminalWinCommand", getTerminalWinCommand())
                .add("terminalNixCommand", getTerminalNixCommand())
                .add("showDonate", getShowDonate())
                .add("validateDocbook", getValidateDocbook())
                .add("clipboardImageFilePattern",getClipboardImageFilePattern());

        return objectBuilder.build();
    }
}
