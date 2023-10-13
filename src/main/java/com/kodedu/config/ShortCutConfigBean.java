package com.kodedu.config;

import com.dooapp.fxform.FXForm;
import com.dooapp.fxform.builder.FXFormBuilder;
import com.dooapp.fxform.handler.NamedFieldHandler;
import com.dooapp.fxform.view.FXFormNode;
import com.dooapp.fxform.view.FXFormNodeWrapper;
import com.dooapp.fxform.view.factory.DefaultFactoryProvider;
import com.kodedu.commands.EditorCommand;
import com.kodedu.component.EditorPane;
import com.kodedu.controller.ApplicationController;
import com.kodedu.helper.IOHelper;
import com.kodedu.helper.OSHelper;
import com.kodedu.service.ThreadService;
import jakarta.json.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.Reader;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

@Component
public class ShortCutConfigBean extends ConfigurationBase {

    private final CountDownLatch disabledLoadingLatch = new CountDownLatch(1);

    private Comparator<EditorCommand> editorCommandComparator =
            Comparator.comparing(editorCommand -> Objects.toString(editorCommand.getDescription(), ""));

    private VBox shortcutListContainer = new VBox(2);

    private final Button saveButton = new Button("Save");
    private final Button loadButton = new Button("Load");
    private final Label infoLabel = new Label();
    private final Label disableInfo = new Label("Restart AsciidocFX to activate!");

    private BooleanProperty disable = new SimpleBooleanProperty(false);
    private BooleanProperty debugMode = new SimpleBooleanProperty(false);
    private BooleanProperty dumpMode = new SimpleBooleanProperty(false);
    private StringProperty filter = new SimpleStringProperty("");

    private ListProperty<EditorCommand> shortcuts = new SimpleListProperty<>(FXCollections.observableArrayList());

    public ShortCutConfigBean(ApplicationController controller, ThreadService threadService) {
        super(controller, threadService);
    }

    @Override
    public String formName() {
        return "Editor shortcuts";
    }

    private class CheckBoxFactory implements Callback<Void, FXFormNode> {
        protected CheckBox checkBox;

        public CheckBoxFactory(String text) {
            this.checkBox = new CheckBox(text);
            this.checkBox.setMnemonicParsing(false);
        }

        @Override
        public FXFormNode call(Void param) {
            return new FXFormNodeWrapper(checkBox, checkBox.selectedProperty());
        }
    }

    private class DisableCheckBoxFactory extends CheckBoxFactory {
        public DisableCheckBoxFactory(String text) {
            super(text);
        }

        @Override
        public FXFormNode call(Void param) {
            VBox box = new VBox();
            box.getChildren().add(checkBox);
            box.getChildren().add(disableInfo);
            disableInfo.setVisible(false);
            disableInfo.setManaged(false);
            disableInfo.setEffect(new DropShadow(3, Color.YELLOW));
            return new FXFormNodeWrapper(box, checkBox.selectedProperty());
        }
    }

    private class FilterViewFactory implements Callback<Void, FXFormNode> {

        private TextField textField = new TextField("");

        @Override
        public FXFormNode call(Void param) {
            textField.setPromptText("Search in shortcuts");
            return new FXFormNodeWrapper(textField, textField.textProperty());
        }
    }

    private class ShortcutViewFactory implements Callback<Void, FXFormNode> {

        private ListProperty<EditorCommand> shortcuts = new SimpleListProperty<>(FXCollections.observableArrayList());

        @Override
        public FXFormNode call(Void unused) {
            shortcuts.addListener((observable, oldValue, newValue) -> {
                List<HBox> boxList = newValue.stream()
                        .map(e -> {
                            Label desc = new Label(e.getDescription());
                            desc.setPrefWidth(150);
                            desc.setWrapText(true);
                            desc.setTooltip(new Tooltip(e.getDescription()));
                            //
                            TextArea shortcut = new TextArea(e.getShortcut());
                            shortcut.setPrefWidth(250);
                            shortcut.setWrapText(true);
                            shortcut.setTooltip(new Tooltip(e.getShortcut()));
                            shortcut.textProperty().addListener((observable1, oldValue1, newValue1) -> {
                                if (OSHelper.isMac()) {
                                    e.setMac(newValue1);
                                } else {
                                    e.setWin(newValue1);
                                }
                                e.setNative(false);
                            });
                            //
                            TextField command = new TextField(e.getName());
                            command.setMinWidth(100);
                            command.textProperty().addListener((observable2, oldValue2, newValue2) -> {
                                e.setName(newValue2);
                            });
                            command.setTooltip(new Tooltip(e.getName()));
                            HBox.setHgrow(shortcut, Priority.ALWAYS);
                            HBox.setHgrow(desc, Priority.ALWAYS);
                            HBox.setHgrow(command, Priority.SOMETIMES);
                            return new HBox(5, desc, shortcut, command);
                        }).collect(Collectors.toList());

                shortcutListContainer.getChildren().setAll(boxList);
            });
            shortcuts.get().addListener((ListChangeListener<EditorCommand>) c -> {
                System.out.println();
            });
            FXFormNodeWrapper wrapper = new FXFormNodeWrapper(shortcutListContainer, shortcuts);
            return wrapper;
        }
    }

    @Override
    public VBox createForm() {
        VBox vBox = new VBox();

        FXForm fxForm = new FXFormBuilder<>()
                .resourceBundle(ResourceBundle.getBundle("shortcuts"))
                .includeAndReorder("disable", "debugMode", "dumpMode", "filter", "shortcuts")
                .build();

        DefaultFactoryProvider provider = new DefaultFactoryProvider();
        provider.addFactory(new NamedFieldHandler("disable"), new DisableCheckBoxFactory("Disable custom shortcut handler."));
        provider.addFactory(new NamedFieldHandler("debugMode"), new CheckBoxFactory("Enable shortcut debug mode"));
        provider.addFactory(new NamedFieldHandler("dumpMode"), new CheckBoxFactory("Dump all shortcuts in shortcut_config.json"));
        provider.addFactory(new NamedFieldHandler("filter"), new FilterViewFactory());
        provider.addFactory(new NamedFieldHandler("shortcuts"), new ShortcutViewFactory());
        fxForm.setEditorFactoryProvider(provider);
        vBox.getChildren().add(fxForm);

        filter.addListener((observable, oldValue, newValue) -> {
            shortcutListContainer.getChildren()
                    .stream().map(s -> (HBox) s)
                    .forEach(hBox -> {
                        ObservableList<Node> hBoxChildren = hBox.getChildren();
                        Label desc = (Label) hBoxChildren.get(0);
                        TextArea shortcut = (TextArea) hBoxChildren.get(1);
                        TextField command = (TextField) hBoxChildren.get(2);
                        if (Objects.isNull(newValue) || newValue.isEmpty() ||
                                StringUtils.containsIgnoreCase(desc.getText(), newValue) ||
                                StringUtils.containsIgnoreCase(shortcut.getText(), newValue) ||
                                StringUtils.containsIgnoreCase(command.getText(), newValue)
                        ) {
                            hBox.setVisible(true);
                            hBox.setManaged(true);
                        } else {
                            hBox.setVisible(false);
                            hBox.setManaged(false);
                        }
                    });
        });

        disable.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                disableInfo.setVisible(true);
                disableInfo.setManaged(true);
            }
        });

        saveButton.setOnAction(this::save);
        loadButton.setOnAction(this::load);
        HBox box = new HBox(5, saveButton, loadButton, infoLabel);
        box.setPadding(new Insets(0, 0, 15, 5));

        vBox.getChildren().add(box);

        fxForm.setSource(this);

        return vBox;
    }

    @Override
    public Path getConfigPath() {
        return Path.of("/Users/usta/projects/AsciidocFX/conf/shortcut_config.json");
//        return super.resolveConfigPath("shortcut_config.json");
    }

    @Override
    public void load(Path configPath, ActionEvent... actionEvent) {

        fadeOut(infoLabel, "Loading...");

        threadService.runTaskLater(() -> {

            Reader fileReader = IOHelper.fileReader(configPath);
            JsonReader jsonReader = Json.createReader(fileReader);

            JsonObject jsonObject = jsonReader.readObject();
            boolean disabled = jsonObject.getBoolean("disabled", false);
            this.disable.set(disabled);
            disabledLoadingLatch.countDown();

            boolean debugMode = jsonObject.getBoolean("debugMode", false);
            this.debugMode.set(debugMode);

            boolean dumpMode = jsonObject.getBoolean("dumpMode", false);
            this.dumpMode.set(dumpMode);

            List<EditorCommand> savedCommands = new ArrayList<>();
            if (jsonObject.containsKey("shortcuts")) {
                JsonArray jsonArray = jsonObject.getJsonArray("shortcuts");
                List<EditorCommand> loadedCommands = jsonArray.stream().map(j -> j.asJsonObject())
                        .map(o -> {
                            try {
                                String name = o.getString("name");
                                String desc = o.getString("desc", "");
                                EditorCommand editorCommand = new EditorCommand();
                                if (o.containsKey("win")) {
                                    editorCommand.setWin(o.getString("win"));

                                }
                                if (o.containsKey("mac")) {
                                    editorCommand.setMac(o.getString("mac"));
                                }
                                editorCommand.setName(name);
                                editorCommand.setDescription(desc);
                                return editorCommand;
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }).collect(Collectors.toList());
                savedCommands.addAll(loadedCommands);
            }

            List<EditorCommand> editorKeyMapping = EditorPane.getNativeKeyMappings();

            List<EditorCommand> commandList = editorKeyMapping
                    .stream()
                    .filter(e -> e.hasShortcut())
                    .filter(e -> !savedCommands.contains(e))
                    .collect(Collectors.toList());

            commandList.addAll(savedCommands);
            Collections.sort(commandList, editorCommandComparator);

            threadService.runActionLater(() -> {
                ObservableList<EditorCommand> list = shortcuts.get();
                list.setAll(commandList);
            });
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
        JsonArrayBuilder shortcutsBuilder = Json.createArrayBuilder();
        for (EditorCommand shortcut : shortcuts) {
            if (!shortcut.isNative() || dumpMode.get()) {
                JsonObjectBuilder shortcutBuilder = Json.createObjectBuilder();
                shortcutBuilder.add("name", shortcut.getName());
                String description = shortcut.getDescription();
                if (Objects.nonNull(description)) {
                    shortcutBuilder.add("desc", description);
                } else {
                    shortcutBuilder.addNull("desc");
                }
                if (Objects.nonNull(shortcut.getWin())) {
                    shortcutBuilder.add("win", shortcut.getWin());
                }
                if (Objects.nonNull(shortcut.getMac())) {
                    shortcutBuilder.add("mac", shortcut.getMac());
                }
                shortcutsBuilder.add(shortcutBuilder);
            }
        }

        JsonArray values = shortcutsBuilder.build();
        objectBuilder.add("disabled", disable.get());
        objectBuilder.add("debugMode", debugMode.get());
        objectBuilder.add("dumpMode", dumpMode.get());
        objectBuilder.add("shortcuts", values);
        return objectBuilder.build();
    }

    public ObservableList<EditorCommand> getShortcuts() {
        return shortcuts.get();
    }

    public ListProperty<EditorCommand> shortcutsProperty() {
        return shortcuts;
    }

    public boolean isDisabled() {
        return disable.get();
    }

    public BooleanProperty disableProperty() {
        return disable;
    }

    public boolean isDebugMode() {
        return debugMode.get();
    }

    public BooleanProperty debugModeProperty() {
        return debugMode;
    }

    public void awaitDisabledLoading() {
        try {
            disabledLoadingLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
