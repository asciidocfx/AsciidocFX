package com.kodedu.config;

//import com.dooapp.fxform.annotation.Accessor;

import com.dooapp.fxform.FXForm;
import com.dooapp.fxform.builder.FXFormBuilder;
import com.dooapp.fxform.handler.NamedFieldHandler;
import com.dooapp.fxform.view.factory.DefaultFactoryProvider;
import com.kodedu.component.SliderBuilt;
import com.kodedu.config.factory.FileChooserEditableFactory;
import com.kodedu.config.factory.ListChoiceBoxFactory;
import com.kodedu.config.factory.SliderFactory;
import com.kodedu.config.factory.SpinnerFactory;
import com.kodedu.controller.ApplicationController;
import com.kodedu.other.IOHelper;
import com.kodedu.service.ThreadService;
import com.kodedu.service.ui.TabService;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by usta on 17.07.2015.
 */
@Component
public class EditorConfigBean extends ConfigurationBase {

    private ObjectProperty<ObservableList<String>> aceTheme = new SimpleObjectProperty<>(FXCollections.observableArrayList());
    private ObjectProperty<ObservableList<Theme>> editorTheme = new SimpleObjectProperty<>(FXCollections.observableArrayList());
    private ObjectProperty<ObservableList<String>> defaultLanguage = new SimpleObjectProperty<>(FXCollections.observableArrayList());
    private DoubleProperty firstSplitter = new SimpleDoubleProperty(0.17551963048498845);
    private DoubleProperty secondSplitter = new SimpleDoubleProperty(0.5996920708237106);
    private DoubleProperty verticalSplitter = new SimpleDoubleProperty(0.5);
    private StringProperty fontFamily = new SimpleStringProperty("'Monaco', 'Menlo', 'Ubuntu Mono', 'Consolas', 'source-code-pro', monospace");
    private ObjectProperty<Integer> fontSize = new SimpleObjectProperty(16);
    private DoubleProperty scrollSpeed = new SimpleDoubleProperty(0.1);
    private BooleanProperty useWrapMode = new SimpleBooleanProperty(true);
    private ObjectProperty<Integer> wrapLimit = new SimpleObjectProperty<>(0);
    private BooleanProperty showGutter = new SimpleBooleanProperty(false);
    private BooleanProperty autoUpdate = new SimpleBooleanProperty(true);
    private BooleanProperty validateDocbook = new SimpleBooleanProperty(false);
    private BooleanProperty detachedPreview = new SimpleBooleanProperty(false);
    private StringProperty clipboardImageFilePattern = new SimpleStringProperty("'Image'-ddMMyy-hhmmss.SSS'.png'");
    private DoubleProperty screenX = new SimpleDoubleProperty(0);
    private DoubleProperty screenY = new SimpleDoubleProperty(0);
    private DoubleProperty screenWidth = new SimpleDoubleProperty();
    private DoubleProperty screenHeight = new SimpleDoubleProperty();

    private DoubleProperty previewScreenX = new SimpleDoubleProperty(0);
    private DoubleProperty previewScreenY = new SimpleDoubleProperty(0);
    private DoubleProperty previewScreenWidth = new SimpleDoubleProperty();
    private DoubleProperty previewScreenHeight = new SimpleDoubleProperty();

    private ObjectProperty<Integer> hangFileSizeLimit = new SimpleObjectProperty<>(3);
    public ObjectProperty<FoldStyle> foldStyle = new SimpleObjectProperty<>(FoldStyle.DEFAULT);


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

    public boolean isDetachedPreview() {
        return detachedPreview.get();
    }

    public BooleanProperty detachedPreviewProperty() {
        return detachedPreview;
    }

    public void setDetachedPreview(boolean detachedPreview) {
        this.detachedPreview.set(detachedPreview);
    }

    public double getPreviewScreenX() {
        return previewScreenX.get();
    }

    public DoubleProperty previewScreenXProperty() {
        return previewScreenX;
    }

    public void setPreviewScreenX(double previewScreenX) {
        this.previewScreenX.set(previewScreenX);
    }

    public double getPreviewScreenY() {
        return previewScreenY.get();
    }

    public DoubleProperty previewScreenYProperty() {
        return previewScreenY;
    }

    public void setPreviewScreenY(double previewScreenY) {
        this.previewScreenY.set(previewScreenY);
    }

    public double getPreviewScreenWidth() {
        return previewScreenWidth.get();
    }

    public DoubleProperty previewScreenWidthProperty() {
        return previewScreenWidth;
    }

    public void setPreviewScreenWidth(double previewScreenWidth) {
        this.previewScreenWidth.set(previewScreenWidth);
    }

    public double getPreviewScreenHeight() {
        return previewScreenHeight.get();
    }

    public DoubleProperty previewScreenHeightProperty() {
        return previewScreenHeight;
    }

    public void setPreviewScreenHeight(double previewScreenHeight) {
        this.previewScreenHeight.set(previewScreenHeight);
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

    public ObservableList<String> getAceTheme() {
        return aceTheme.get();
    }

    public ObjectProperty<ObservableList<String>> aceThemeProperty() {
        return aceTheme;
    }

    public void setAceTheme(ObservableList<String> aceTheme) {
        this.aceTheme.set(aceTheme);
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

    public Double getScreenY() {
        return screenY.getValue();
    }

    public DoubleProperty screenYProperty() {
        return screenY;
    }

    public void setScreenY(double screenY) {
        this.screenY.set(screenY);
    }

    public Double getScreenX() {
        return screenX.getValue();
    }

    public DoubleProperty screenXProperty() {
        return screenX;
    }

    public void setScreenX(double screenX) {
        this.screenX.set(screenX);
    }

    public Double getScreenWidth() {
        return screenWidth.getValue();
    }

    public DoubleProperty screenWidthProperty() {
        return screenWidth;
    }

    public void setScreenWidth(double screenWidth) {
        this.screenWidth.set(screenWidth);
    }

    public Double getScreenHeight() {
        return screenHeight.getValue();
    }

    public DoubleProperty screenHeightProperty() {
        return screenHeight;
    }

    public void setScreenHeight(double screenHeight) {
        this.screenHeight.set(screenHeight);
    }

    public FoldStyle getFoldStyle() {
        return foldStyle.get();
    }

    public ObjectProperty<FoldStyle> foldStyleProperty() {
        return foldStyle;
    }

    public void setFoldStyle(FoldStyle foldStyle) {
        this.foldStyle.set(foldStyle);
    }

    public Integer getHangFileSizeLimit() {
        return hangFileSizeLimit.get();
    }

    public ObjectProperty<Integer> hangFileSizeLimitProperty() {
        return hangFileSizeLimit;
    }

    public void setHangFileSizeLimit(Integer hangFileSizeLimit) {
        this.hangFileSizeLimit.set(hangFileSizeLimit);
    }

    public ObservableList<Theme> getEditorTheme() {
        return editorTheme.get();
    }

    public ObjectProperty<ObservableList<Theme>> editorThemeProperty() {
        return editorTheme;
    }

    public void setEditorTheme(ObservableList<Theme> editorTheme) {
        this.editorTheme.set(editorTheme);
    }

    public double getVerticalSplitter() {
        return verticalSplitter.get();
    }

    public DoubleProperty verticalSplitterProperty() {
        return verticalSplitter;
    }

    public void setVerticalSplitter(double verticalSplitter) {
        this.verticalSplitter.set(verticalSplitter);
    }

    @Override
    public String formName() {
        return "Editor Settings";
    }

    @Override
    public VBox createForm() {

        FXForm editorConfigForm = new FXFormBuilder<>()
                .resourceBundle(ResourceBundle.getBundle("editorConfig"))
                .includeAndReorder("editorTheme", "aceTheme", "detachedPreview", "validateDocbook", "fontFamily", "fontSize",
                        "scrollSpeed", "useWrapMode", "wrapLimit", "foldStyle", "showGutter", "defaultLanguage", "autoUpdate",
                        "clipboardImageFilePattern", "hangFileSizeLimit", "extensionImageScale")
                .build();

        DefaultFactoryProvider editorConfigFormProvider = new DefaultFactoryProvider();

        editorConfigFormProvider.addFactory(new NamedFieldHandler("aceTheme"), new ListChoiceBoxFactory());
        editorConfigFormProvider.addFactory(new NamedFieldHandler("editorTheme"), new ListChoiceBoxFactory());
        editorConfigFormProvider.addFactory(new NamedFieldHandler("defaultLanguage"), new ListChoiceBoxFactory());
        editorConfigFormProvider.addFactory(new NamedFieldHandler("scrollSpeed"), new SliderFactory(SliderBuilt.create(0.0, 1, 0.1).step(0.1)));
        editorConfigFormProvider.addFactory(new NamedFieldHandler("fontSize"), new SpinnerFactory(new Spinner(8, 32, 14)));
        editorConfigFormProvider.addFactory(new NamedFieldHandler("wrapLimit"), new SpinnerFactory(new Spinner(0, 500, 0)));
        editorConfigFormProvider.addFactory(new NamedFieldHandler("hangFileSizeLimit"), new SpinnerFactory(new Spinner(0, Integer.MAX_VALUE, 3)));
        editorConfigFormProvider.addFactory(new NamedFieldHandler("extensionImageScale"), new SpinnerFactory(new Spinner(0, Integer.MAX_VALUE, 2)));
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

        List<String> aceThemeList = new LinkedList<>(IOHelper.readAllLines(getConfigDirectory().resolve("ace_themes.txt")));
        List<String> languageList = this.languageList();

        Reader fileReader = IOHelper.fileReader(getConfigPath());
        JsonReader jsonReader = Json.createReader(fileReader);

        JsonObject jsonObject = jsonReader.readObject();

        setMainStagePositions(jsonObject);
        setDetachedStagePositions(jsonObject);

        String fontFamily = jsonObject.getString("fontFamily", "'Monaco', 'Menlo', 'Ubuntu Mono', 'Consolas', 'source-code-pro', monospace");
        int fontSize = jsonObject.getInt("fontSize", 14);
        String aceTheme = jsonObject.getString("aceTheme", "tomorrow_night");
        String defaultLanguage = jsonObject.getString("defaultLanguage", "en");
        boolean useWrapMode = jsonObject.getBoolean("useWrapMode", true);
        boolean showGutter = jsonObject.getBoolean("showGutter", false);
        boolean detachedPreview = jsonObject.getBoolean("detachedPreview", false);
        int wrapLimit = jsonObject.getInt("wrapLimit", 0);
        boolean autoUpdate = jsonObject.getBoolean("autoUpdate", true);
        final boolean validateDocbook = jsonObject.getBoolean("validateDocbook", false);
        String clipboardImageFilePattern = jsonObject.getString("clipboardImageFilePattern", "'Image'-ddMMyy-hhmmss.SSS'.png'");
        String foldStyle = jsonObject.getString("foldStyle", "default");
        int hangFileSizeLimit = jsonObject.getInt("hangFileSizeLimit", 3);
        String editorTheme = jsonObject.getString("editorTheme");

        // Editor themes
        Stream<Path> themeStream = IOHelper.walk(getConfigDirectory().resolve("themes"), Integer.MAX_VALUE);

        List<Path> themeConfigs = themeStream.filter(p -> p.endsWith("theme.json")).collect(Collectors.toList());

        List<Theme> themeList = new LinkedList<>();

        for (Path themeConfig : themeConfigs) {
            try (JsonReader themeReader = Json.createReader(IOHelper.fileReader(themeConfig));) {
                Theme theme = new Theme(themeReader.readObject());
                if (theme.isEnabled()) {
                    theme.setConfigPath(themeConfig);
                    themeList.add(theme);
                }
            }
        }

        Optional<Theme> themeOptional = themeList.stream().filter(e -> e.getThemeName().equals(editorTheme)).findFirst();
        themeOptional.ifPresent(t -> {
            themeList.remove(t);
            themeList.add(0, t);
        });

//      Ace set first theme
        aceThemeList.remove(aceTheme);
        aceThemeList.add(0, aceTheme);

//      Set language
        languageList.remove(defaultLanguage);
        languageList.add(0, defaultLanguage);

        IOHelper.close(jsonReader, fileReader);

        threadService.runActionLater(() -> {
            getEditorTheme().addAll(themeList);
            getAceTheme().addAll(aceThemeList);
            getDefaultLanguage().addAll(languageList);

            this.setFontFamily(fontFamily);
            this.setFontSize(fontSize);
            this.setUseWrapMode(useWrapMode);
            this.setShowGutter(showGutter);
            this.setDetachedPreview(detachedPreview);
            this.setWrapLimit(wrapLimit);
            this.setAutoUpdate(autoUpdate);
            this.setValidateDocbook(validateDocbook);
            this.setClipboardImageFilePattern(clipboardImageFilePattern);
            this.setHangFileSizeLimit(hangFileSizeLimit);

            if (FoldStyle.contains(foldStyle)) {
                this.setFoldStyle(FoldStyle.valueOf(foldStyle));
            }

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

            if (jsonObject.containsKey("verticalSplitter")) {
                JsonNumber secondSplitter = jsonObject.getJsonNumber("verticalSplitter");
                this.setVerticalSplitter(secondSplitter.doubleValue());
            }

            fadeOut(infoLabel, "Loaded...");

        });
    }

    private void setMainStagePositions(JsonObject jsonObject) {
        if (jsonObject.containsKey("screenX")) {
            double screenX = jsonObject.getJsonNumber("screenX").doubleValue();
            this.setScreenX(screenX);
        }

        if (jsonObject.containsKey("screenY")) {
            double screenY = jsonObject.getJsonNumber("screenY").doubleValue();
            this.setScreenY(screenY);
        }

        if (jsonObject.containsKey("screenWidth")) {
            double screenWidth = jsonObject.getJsonNumber("screenWidth").doubleValue();
            this.setScreenWidth(screenWidth);
        }

        if (jsonObject.containsKey("screenHeight")) {
            double screenHeight = jsonObject.getJsonNumber("screenHeight").doubleValue();
            this.setScreenHeight(screenHeight);
        }
    }

    private void setDetachedStagePositions(JsonObject jsonObject) {
        if (jsonObject.containsKey("previewScreenX")) {
            double screenX = jsonObject.getJsonNumber("previewScreenX").doubleValue();
            this.setPreviewScreenX(screenX);
        }

        if (jsonObject.containsKey("previewScreenY")) {
            double screenY = jsonObject.getJsonNumber("previewScreenY").doubleValue();
            this.setPreviewScreenY(screenY);
        }

        if (jsonObject.containsKey("previewScreenWidth")) {
            double screenWidth = jsonObject.getJsonNumber("previewScreenWidth").doubleValue();
            this.setPreviewScreenWidth(screenWidth);
        }

        if (jsonObject.containsKey("previewScreenHeight")) {
            double screenHeight = jsonObject.getJsonNumber("previewScreenHeight").doubleValue();
            this.setPreviewScreenHeight(screenHeight);
        }
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
                .add("detachedPreview", isDetachedPreview())
                .add("aceTheme", getAceTheme().get(0))
                .add("editorTheme", getEditorTheme().get(0).getThemeName())
                .add("defaultLanguage", getDefaultLanguage().get(0))
                .add("firstSplitter", getFirstSplitter())
                .add("secondSplitter", getSecondSplitter())
                .add("verticalSplitter", getVerticalSplitter())
                .add("autoUpdate", getAutoUpdate())
                .add("validateDocbook", getValidateDocbook())
                .add("clipboardImageFilePattern", getClipboardImageFilePattern())
                .add("screenX", getScreenX())
                .add("screenY", getScreenY())
                .add("screenWidth", getScreenWidth())
                .add("screenHeight", getScreenHeight())
                .add("previewScreenX", getPreviewScreenX())
                .add("previewScreenY", getPreviewScreenY())
                .add("previewScreenWidth", getPreviewScreenWidth())
                .add("previewScreenHeight", getPreviewScreenHeight())
                .add("foldStyle", getFoldStyle().name())
                .add("hangFileSizeLimit", getHangFileSizeLimit());

        return objectBuilder.build();
    }

    public void updateAceTheme(String aceTheme) {
        getAceTheme().remove(aceTheme);
        getAceTheme().add(0, aceTheme);
    }

    public class Theme {
        private final boolean enabled;
        private final String themeName;
        private final String rootCss;
        private final String aceTheme;
        private Path configPath;

        public Theme(JsonObject jsonObject) {
            this.themeName = jsonObject.getString("theme-name");
            Objects.requireNonNull(themeName, "Theme Name must nut be null");
            this.rootCss = jsonObject.getString("root-css");
            Objects.requireNonNull(rootCss, "Root css must nut be null");
            this.aceTheme = jsonObject.getString("ace-theme");
            Objects.requireNonNull(aceTheme, "Ace theme must nut be null");
            this.enabled = jsonObject.getBoolean("enabled", true);
        }

        public String getThemeName() {
            return themeName;
        }

        public String getRootCss() {
            return rootCss;
        }

        public String getAceTheme() {
            return aceTheme;
        }

        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Theme theme = (Theme) o;
            return Objects.equals(themeName, theme.themeName);
        }

        public String themeUri() {
            String themeUri = null;
            try {
                themeUri = getConfigPath().getParent().resolve(getRootCss()).normalize().toUri().toString();
            } catch (Exception e) {
                logger.warn("Theme uri wrong {}", e);
            }
            return themeUri;
        }

        @Override
        public int hashCode() {
            return Objects.hash(themeName);
        }

        @Override
        public String toString() {
            return getThemeName();
        }

        public void setConfigPath(Path configPath) {
            this.configPath = configPath;
        }

        public Path getConfigPath() {
            return configPath;
        }
    }
}
