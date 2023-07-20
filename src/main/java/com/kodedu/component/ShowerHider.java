package com.kodedu.component;

import com.kodedu.helper.FxHelper;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

import java.util.Optional;

/**
 * Created by usta on 29.11.2015.
 */
public class ShowerHider extends AnchorPane {

    private ObjectProperty<Node> master = new SimpleObjectProperty<>();
    private ObservableList<Node> slaves = FXCollections.observableArrayList();

    public ShowerHider() {

        getChildren().addListener((ListChangeListener<Node>) c -> {
            final ObservableList<? extends Node> nodes = c.getList();
            if (!nodes.isEmpty()) {
                final Node node = nodes.get(0);
                FxHelper.fitToParent(node);
            }
        });

        master.addListener((observable, oldValue, newValue) -> {
            if (getChildren().contains(newValue)) {
                return;
            }
            getChildren().clear();
            getChildren().add(newValue);
        });


    }

    public void showDefaultNode() {
        Platform.runLater(() -> {
            getChildren().clear();
            getChildren().add(getMaster());
        });
    }

    public void showNode(Node node, Runnable... runnables) {

        if (!slaves.contains(node)) {
            slaves.add(node);
        }

        if (getChildren().contains(node)) {
            return;
        }

        Platform.runLater(() -> {
            getChildren().clear();
            getChildren().add(node);
            for (Runnable runnable : runnables) {
                runnable.run();
            }
        });
    }


    private void makeHidden(Node node) {
        node.setVisible(false);
        node.setManaged(false);
    }

    private void makeVisible(Node node) {
        node.setVisible(true);
        node.setManaged(true);
    }


    public Node getMaster() {
        return master.get();
    }

    public ObjectProperty<Node> masterProperty() {
        return master;
    }

    public void setMaster(Node master) {
        this.master.set(master);
    }

    public ObservableList<Node> getSlaves() {
        return slaves;
    }

    public Optional<ViewPanel> getShowing() {
        return Optional.ofNullable(getChildren().get(0))
                .filter(n -> n instanceof ViewPanel)
                .map(n -> (ViewPanel) n);

    }
}
