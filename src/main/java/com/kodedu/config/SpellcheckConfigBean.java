package com.kodedu.config;

//import com.dooapp.fxform.annotation.Accessor;

import com.kodedu.controller.ApplicationController;
import com.kodedu.other.IOHelper;
import com.kodedu.service.ThreadService;
import com.kodedu.service.ui.TabService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by usta on 17.07.2015.
 */
@Component
public class SpellcheckConfigBean extends ConfigurationBase {

    private Logger logger = LoggerFactory.getLogger(SpellcheckConfigBean.class);

    private final ObjectProperty<ObservableList<Path>> languages = new SimpleObjectProperty<>(FXCollections.observableArrayList());
    private final ObjectProperty<Path> defaultLanguage = new SimpleObjectProperty<>();
    private final BooleanProperty disableSpellCheck = new SimpleBooleanProperty(false);
    private final ListView<Path> languagePathList = new ListView<>();

    private final ApplicationController controller;
    private final ThreadService threadService;
    private final TabService tabService;

    private final Button saveButton = new Button("Save");
    private final Button loadButton = new Button("Load");
    private final Label infoLabel = new Label();

    @Autowired
    public SpellcheckConfigBean(ApplicationController controller, ThreadService threadService, TabService tabService) {
        super(controller, threadService);
        this.controller = controller;
        this.threadService = threadService;
        this.tabService = tabService;
    }

    public Path getDefaultLanguage() {
        if (Objects.isNull(defaultLanguage.get())) {
            Optional.ofNullable(getLanguages())
                    .filter(langs -> !langs.isEmpty())
                    .ifPresent(langs -> {
                        setDefaultLanguage(langs.get(0));
                    });
        }
        return defaultLanguage.get();
    }

    public ObjectProperty<Path> defaultLanguageProperty() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(Path defaultLanguage) {
        this.defaultLanguage.set(defaultLanguage);
    }

    public ObservableList<Path> getLanguages() {
        return languages.get();
    }

    public ObjectProperty<ObservableList<Path>> languagesProperty() {
        return languages;
    }

    public void setLanguages(ObservableList<Path> languages) {
        this.languages.set(languages);
    }

    public ListView<Path> getLanguagePathList() {
        return languagePathList;
    }

    public boolean getDisableSpellCheck() {
        return disableSpellCheck.get();
    }

    public BooleanProperty disableSpellCheckProperty() {
        return disableSpellCheck;
    }

    public void setDisableSpellCheck(boolean disableSpellCheck) {
        this.disableSpellCheck.set(disableSpellCheck);
    }

    @Override
    public String formName() {
        return "SpellCheck Settings";
    }

    @Override
    public VBox createForm() {

        return null;

//        FXForm spellCheckConfigForm = new FXFormBuilder<>()
//                .resourceBundle(ResourceBundle.getBundle("spellcheck"))
//                .includeAndReorder("disableSpellCheck", "languages")
//                .build();
//
//        DefaultFactoryProvider spellCheckConfigFormProvider = new DefaultFactoryProvider();
//
//        spellCheckConfigFormProvider.addFactory(new NamedFieldHandler("languages"), new DefaultSpellCheckLanguageFactory(this));
//        spellCheckConfigForm.setEditorFactoryProvider(spellCheckConfigFormProvider);
//
//        spellCheckConfigForm.setSource(this);
//
//        VBox vBox = new VBox();
//        vBox.getChildren().add(spellCheckConfigForm);
//
//        saveButton.setOnAction(this::save);
//        loadButton.setOnAction(this::load);
//        HBox box = new HBox(5, saveButton, loadButton, infoLabel);
//        box.setPadding(new Insets(0, 0, 15, 5));
//        vBox.getChildren().add(box);
//
//        return vBox;
    }

    @Override
    public Path getConfigPath() {
        return super.resolveConfigPath("spellcheck_config.json");
    }

    @Override
    public void load(Path configPath, ActionEvent... actionEvent) {

        fadeOut(infoLabel, "Loading...");

        loadFoundDictionaries();

        Reader fileReader = IOHelper.fileReader(configPath);
        JsonReader jsonReader = Json.createReader(fileReader);

        JsonObject jsonObject = jsonReader.readObject();

        final String defaultLanguage = jsonObject.getString("defaultLanguage", null);
        final boolean disableSpellCheck = jsonObject.getBoolean("disableSpellCheck", false);

        IOHelper.close(jsonReader, fileReader);

        final Optional<Path> defaultLanguagePath = Optional.ofNullable(defaultLanguage)
                .map(Paths::get)
                .filter(Files::exists);

        threadService.runActionLater(() -> {

            this.setDisableSpellCheck(disableSpellCheck);

            languagePathList.itemsProperty().addListener((observable, oldValue, newValue) -> {
                defaultLanguagePath
                        .ifPresent(languagePathList.getSelectionModel()::select);
            });

            defaultLanguageProperty().addListener((observable, oldValue, newValue) -> {
                Optional.ofNullable(newValue)
                        .ifPresent(languagePathList.getSelectionModel()::select);
            });

            defaultLanguagePath
                    .ifPresent(this::setDefaultLanguage);

            fadeOut(infoLabel, "Loaded...");
        });

    }

    private void loadFoundDictionaries() {
        Path configPath = controller.getConfigPath(); // installed /conf dir
        Path rootLocation = getConfigRootLocation(); // user.home root

        final Path localLanguages = configPath.resolve("spellcheck");
        final Path addedLanguages = rootLocation.resolve("spellcheck");

        IOHelper.createDirectories(addedLanguages);

        final String extension = ".dict";

        final Stream<Path> localDictStream =
                IOHelper.find(localLanguages, Integer.MAX_VALUE, (path, attrs) -> path.toString().endsWith(extension));

        final Stream<Path> addedDictStream =
                IOHelper.find(addedLanguages, Integer.MAX_VALUE, (path, attrs) -> path.toString().endsWith(extension));

        final Stream<Path> dictStream = Stream.concat(localDictStream, addedDictStream);

        languages.set(FXCollections.observableArrayList(dictStream.sorted(Collections.reverseOrder()).collect(Collectors.toList())));

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

        if (Objects.nonNull(getDefaultLanguage())) {
            objectBuilder.add("defaultLanguage", getDefaultLanguage().toString());
        }

        objectBuilder.add("disableSpellCheck", getDisableSpellCheck());

        return objectBuilder.build();
    }
}
