package com.kodcu.config.factory;

import com.dooapp.fxform.view.FXFormNode;
import com.dooapp.fxform.view.FXFormNodeWrapper;
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

    public ListChoiceBoxFactory(ChoiceBox choiceBox) {
        this.choiceBox = choiceBox;
    }

    public FXFormNode call(Void aVoid) {

        choiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (Objects.nonNull(newValue)) {
                if (!newValue.equals(choiceBox.getItems().get(0))) {
                    choiceBox.getItems().removeAll(newValue);
                    choiceBox.getItems().add(0, newValue);
                }

            }
        });

        FXFormNodeWrapper fxFormNodeWrapper = new FXFormNodeWrapper(choiceBox, choiceBox.itemsProperty());

        choiceBox.itemsProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (Objects.nonNull(oldValue)) {
                    ObservableList observableList = (ObservableList) oldValue;
                    observableList.removeListener(this::addListener);
                }
                if (Objects.nonNull(newValue)) {
                    ObservableList observableList = (ObservableList) newValue;
                    observableList.addListener(this::addListener);
                }
            }

            private void addListener(ListChangeListener.Change change) {
                change.next();
                if (change.wasReplaced()) {
                    choiceBox.getSelectionModel().selectFirst();
                }
            }
        });
        return fxFormNodeWrapper;

    }


}