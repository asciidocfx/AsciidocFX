package com.kodedu.copilot.config;

import com.dooapp.fxform.FXForm;
import com.dooapp.fxform.builder.FXFormBuilder;
import com.dooapp.fxform.handler.NamedFieldHandler;
import com.dooapp.fxform.view.factory.DefaultFactoryProvider;
import com.kodedu.component.SliderBuilt;
import com.kodedu.config.ConfigurationBase;
import com.kodedu.config.factory.SliderFactory;
import com.kodedu.controller.ApplicationController;
import com.kodedu.helper.IOHelper;
import com.kodedu.service.ThreadService;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import java.io.Reader;
import java.nio.file.Path;
import java.util.ResourceBundle;

/**
 * Configuration bean for Copilot integration settings.
 * Persisted as copilot_config.json in the user config folder.
 */
@Component
public class CopilotConfigBean extends ConfigurationBase {

    private static final Logger logger = LoggerFactory.getLogger(CopilotConfigBean.class);

    private StringProperty defaultMode = new SimpleStringProperty("ASK");
    private BooleanProperty inlineCompletionEnabled = new SimpleBooleanProperty(false);
    private IntegerProperty contextWindowSize = new SimpleIntegerProperty(4000);
    private DoubleProperty temperature = new SimpleDoubleProperty(0.3);
    private IntegerProperty maxTokens = new SimpleIntegerProperty(4096);
    private StringProperty model = new SimpleStringProperty("gpt-4o");
    private BooleanProperty enabled = new SimpleBooleanProperty(true);

    // Auth tokens — stored but not shown in UI form
    private StringProperty accessToken = new SimpleStringProperty("");
    private StringProperty refreshToken = new SimpleStringProperty("");
    private LongProperty tokenExpiresAt = new SimpleLongProperty(0);

    private final Button saveButton = new Button("Save");
    private final Button loadButton = new Button("Load");
    private final Label infoLabel = new Label();

    @Autowired
    public CopilotConfigBean(ApplicationController controller, ThreadService threadService) {
        super(controller, threadService);
    }

    @Override
    public String formName() {
        return "Copilot Settings";
    }

    @Override
    public VBox createForm() {
        FXForm form = new FXFormBuilder<>()
                .resourceBundle(ResourceBundle.getBundle("copilotConfig"))
                .includeAndReorder(
                        "enabled",
                        "defaultMode",
                        "model",
                        "inlineCompletionEnabled",
                        "temperature",
                        "maxTokens",
                        "contextWindowSize")
                .build();

        DefaultFactoryProvider factoryProvider = new DefaultFactoryProvider();
        factoryProvider.addFactory(new NamedFieldHandler("temperature"),
                new SliderFactory(SliderBuilt.create(0.0, 2.0, 0.3).step(0.1)));
        form.setEditorFactoryProvider(factoryProvider);
        form.setSource(this);

        VBox vBox = new VBox();
        vBox.getChildren().add(form);

        saveButton.setOnAction(this::save);
        loadButton.setOnAction(this::load);
        HBox box = new HBox(5, saveButton, loadButton, infoLabel);
        box.setPadding(new Insets(0, 0, 15, 5));
        vBox.getChildren().add(box);

        return vBox;
    }

    @Override
    public Path getConfigPath() {
        return super.resolveConfigPath("copilot_config.json");
    }

    @Override
    public void load(Path configPath, ActionEvent... actionEvent) {
        fadeOut(infoLabel, "Loading...");
        createConfigFileIfNotExist(configPath);

        Reader fileReader = IOHelper.fileReader(configPath);
        JsonReader jsonReader = Json.createReader(fileReader);
        JsonObject json = jsonReader.readObject();

        String defaultMode = json.getString("defaultMode", this.defaultMode.getValue());
        boolean inlineCompletionEnabled = json.getBoolean("inlineCompletionEnabled", this.inlineCompletionEnabled.getValue());
        int contextWindowSize = json.getInt("contextWindowSize", this.contextWindowSize.getValue());
        String model = json.getString("model", this.model.getValue());
        boolean enabled = json.getBoolean("enabled", this.enabled.getValue());
        int maxTokens = json.getInt("maxTokens", this.maxTokens.getValue());
        String accessToken = json.getString("accessToken", this.accessToken.getValue());
        String refreshToken = json.getString("refreshToken", this.refreshToken.getValue());

        IOHelper.close(jsonReader, fileReader);

        threadService.runActionLater(() -> {
            this.setDefaultMode(defaultMode);
            this.setInlineCompletionEnabled(inlineCompletionEnabled);
            this.setContextWindowSize(contextWindowSize);
            this.setModel(model);
            this.setEnabled(enabled);
            this.setMaxTokens(maxTokens);
            this.setAccessToken(accessToken);
            this.setRefreshToken(refreshToken);

            if (json.containsKey("temperature")) {
                try {
                    this.setTemperature(json.getJsonNumber("temperature").doubleValue());
                } catch (Exception e) {
                    // use default
                }
            }
            if (json.containsKey("tokenExpiresAt")) {
                try {
                    this.setTokenExpiresAt(json.getJsonNumber("tokenExpiresAt").longValue());
                } catch (Exception e) {
                    // use default
                }
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
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("defaultMode", getDefaultMode())
                .add("inlineCompletionEnabled", isInlineCompletionEnabled())
                .add("contextWindowSize", getContextWindowSize())
                .add("temperature", getTemperature())
                .add("maxTokens", getMaxTokens())
                .add("model", getModel())
                .add("enabled", isEnabled())
                .add("accessToken", getAccessToken())
                .add("refreshToken", getRefreshToken())
                .add("tokenExpiresAt", getTokenExpiresAt());
        return builder.build();
    }

    // --- Property accessors ---

    public String getDefaultMode() { return defaultMode.get(); }
    public StringProperty defaultModeProperty() { return defaultMode; }
    public void setDefaultMode(String defaultMode) { this.defaultMode.set(defaultMode); }

    public boolean isInlineCompletionEnabled() { return inlineCompletionEnabled.get(); }
    public BooleanProperty inlineCompletionEnabledProperty() { return inlineCompletionEnabled; }
    public void setInlineCompletionEnabled(boolean v) { this.inlineCompletionEnabled.set(v); }

    public int getContextWindowSize() { return contextWindowSize.get(); }
    public IntegerProperty contextWindowSizeProperty() { return contextWindowSize; }
    public void setContextWindowSize(int v) { this.contextWindowSize.set(v); }

    public double getTemperature() { return temperature.get(); }
    public DoubleProperty temperatureProperty() { return temperature; }
    public void setTemperature(double v) { this.temperature.set(v); }

    public int getMaxTokens() { return maxTokens.get(); }
    public IntegerProperty maxTokensProperty() { return maxTokens; }
    public void setMaxTokens(int v) { this.maxTokens.set(v); }

    public String getModel() { return model.get(); }
    public StringProperty modelProperty() { return model; }
    public void setModel(String v) { this.model.set(v); }

    public boolean isEnabled() { return enabled.get(); }
    public BooleanProperty enabledProperty() { return enabled; }
    public void setEnabled(boolean v) { this.enabled.set(v); }

    public String getAccessToken() { return accessToken.get(); }
    public StringProperty accessTokenProperty() { return accessToken; }
    public void setAccessToken(String v) { this.accessToken.set(v); }

    public String getRefreshToken() { return refreshToken.get(); }
    public StringProperty refreshTokenProperty() { return refreshToken; }
    public void setRefreshToken(String v) { this.refreshToken.set(v); }

    public long getTokenExpiresAt() { return tokenExpiresAt.get(); }
    public LongProperty tokenExpiresAtProperty() { return tokenExpiresAt; }
    public void setTokenExpiresAt(long v) { this.tokenExpiresAt.set(v); }

    public boolean hasValidToken() {
        return !getAccessToken().isEmpty() && getTokenExpiresAt() > System.currentTimeMillis() / 1000;
    }
}
