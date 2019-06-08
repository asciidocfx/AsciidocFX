package com.kodedu.config.factory;

import com.dooapp.fxform.view.FXFormNode;
import com.dooapp.fxform.view.FXFormNodeWrapper;
import com.kodedu.config.SpellcheckConfigBean;
import com.kodedu.helper.IOHelper;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.nio.file.Path;

/**
 * Created by usta on 08.12.2015.
 */
public class DefaultSpellCheckLanguageFactory implements Callback<Void, FXFormNode> {

    final Button setDefaultButton = new Button("Set default");
    final Button addNewLanguageButton = new Button("Add new language");
    final Label languageLabel = new Label();
    private final SpellcheckConfigBean spellcheckConfigBean;

    public DefaultSpellCheckLanguageFactory(SpellcheckConfigBean spellcheckConfigBean) {
        this.spellcheckConfigBean = spellcheckConfigBean;
    }

    @Override
    public FXFormNode call(Void param) {

        final ListView<Path> listView = spellcheckConfigBean.getLanguagePathList();

        listView.setCellFactory(li -> new TextFieldListCell<>(new StringConverter<Path>() {
            @Override
            public String toString(Path object) {
                return IOHelper.getPathCleanName(object);
            }

            @Override
            public Path fromString(String string) {
                return IOHelper.getPath(string);
            }
        }));

        HBox hBox = new HBox(5);
        hBox.getChildren().add(listView);

        hBox.getChildren().add(new VBox(10, languageLabel, setDefaultButton, addNewLanguageButton));
        HBox.setHgrow(listView, Priority.ALWAYS);

        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        setDefaultButton.setOnAction(this::setDefaultLanguage);

        return new FXFormNodeWrapper(hBox, listView.itemsProperty());
    }

    private void setDefaultLanguage(ActionEvent actionEvent) {
        final ListView<Path> listView = spellcheckConfigBean.getLanguagePathList();
        final MultipleSelectionModel<Path> selectionModel = listView.getSelectionModel();
        final Path selectedItem = selectionModel.getSelectedItem();
        spellcheckConfigBean.setDefaultLanguage(selectedItem);
        languageLabel.setText(IOHelper.getPathCleanName(selectedItem));
    }
}
