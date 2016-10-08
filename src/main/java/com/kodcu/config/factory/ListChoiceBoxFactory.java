package com.kodcu.config.factory;

import com.dooapp.fxform.view.FXFormNode;
import com.dooapp.fxform.view.FXFormNodeWrapper;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.util.Callback;

import java.util.Objects;

/**
 * Created by usta on 17.07.2015.
 */
public class ListChoiceBoxFactory implements Callback<Void, FXFormNode> {

    private final ChoiceBox choiceBox;

    public ListChoiceBoxFactory() {
        this.choiceBox = new ChoiceBox();
    }

    public ListChoiceBoxFactory(ChoiceBox choiceBox) {
        this.choiceBox = choiceBox;
    }


    public FXFormNode call(Void aVoid) {

        choiceBox.itemsProperty().addListener((observable, oldValue, newValue) -> {
            choiceBox.getSelectionModel().selectFirst();
            ((ObservableList) newValue).addListener(new ListChangeListener() {
                @Override
                public void onChanged(Change change) {
                    change.next();
                    if (change.wasAdded()) {
                        choiceBox.getSelectionModel().selectFirst();
                    }
                }
            });
        });

        choiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (Objects.nonNull(newValue)) {
                if (!newValue.equals(choiceBox.getItems().get(0))) {
                    choiceBox.getItems().removeAll(newValue);
                    choiceBox.getItems().add(0, newValue);
                    choiceBox.getSelectionModel().selectFirst();
                }
            }
        });


        FXFormNodeWrapper fxFormNodeWrapper = new FXFormNodeWrapper(choiceBox, choiceBox.itemsProperty());

        return fxFormNodeWrapper;

    }


}