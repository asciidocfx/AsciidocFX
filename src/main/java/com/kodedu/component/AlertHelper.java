package com.kodedu.component;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Screen;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static javafx.scene.control.Alert.AlertType;

/**
 * Created by usta on 06.03.2015.
 */
public final class AlertHelper {

    public static final ButtonType LOAD_FILE_SYSTEM_CHANGES = new ButtonType("Load File System Changes");
    public static final ButtonType KEEP_MEMORY_CHANGES = new ButtonType("Keep Memory Changes");

    public static final ButtonType OPEN_IN_APP = new ButtonType("Open anyway");
    public static final ButtonType OPEN_EXTERNAL = new ButtonType("Open external");

    static Alert buildDeleteAlertDialog(List<Path> pathsLabel) {
        Alert deleteAlert = new Alert(Alert.AlertType.WARNING, null, ButtonType.YES, ButtonType.CANCEL);
        deleteAlert.setHeaderText("Do you want to delete selected path(s)?");
        DialogPane dialogPane = deleteAlert.getDialogPane();

        ObservableList<Path> paths = Optional.ofNullable(pathsLabel)
                .map(FXCollections::observableList)
                .orElse(FXCollections.emptyObservableList());

        if (paths.isEmpty()) {
            dialogPane.setContentText("There are no files selected.");
            deleteAlert.getButtonTypes().clear();
            deleteAlert.getButtonTypes().add(ButtonType.CANCEL);
            return deleteAlert;
        }

        ListView<Path> listView = new ListView<>(paths);
        listView.setId("listOfPaths");

        GridPane gridPane = new GridPane();
        gridPane.addRow(0, listView);
        GridPane.setHgrow(listView, Priority.ALWAYS);

        double minWidth = 200.0;
        double maxWidth = Screen.getScreens().stream()
                .mapToDouble(s -> s.getBounds().getWidth() / 3)
                .min().orElse(minWidth);

        double prefWidth = paths.stream()
                .map(String::valueOf)
                .mapToDouble(s -> s.length() * 7)
                .max()
                .orElse(maxWidth);

        double minHeight = IntStream.of(paths.size())
                .map(e -> e * 70)
                .filter(e -> e <= 300 && e >= 70)
                .findFirst()
                .orElse(200);

        gridPane.setMinWidth(minWidth);
        gridPane.setPrefWidth(prefWidth);
        gridPane.setPrefHeight(minHeight);
        dialogPane.setContent(gridPane);
        return deleteAlert;
    }

    public static Optional<ButtonType> deleteAlert(List<Path> pathsLabel) {
        return buildDeleteAlertDialog(pathsLabel).showAndWait();
    }

    public static Optional<ButtonType> showAlert(String alertMessage) {
        AlertDialog deleteAlert = new AlertDialog(AlertType.WARNING, null, ButtonType.YES, ButtonType.CANCEL);
        deleteAlert.setHeaderText(alertMessage);
        return deleteAlert.showAndWait();
    }

    public static void okayAlert(String alertMessage) {
        AlertDialog deleteAlert = new AlertDialog(AlertType.WARNING, null, ButtonType.OK);
        deleteAlert.setHeaderText(alertMessage);
        deleteAlert.show();
    }

    public static Optional<ButtonType> nullDirectoryAlert() {
        AlertDialog deleteAlert = new AlertDialog(AlertType.WARNING, null, ButtonType.OK);
        deleteAlert.setHeaderText("Please select directorie(s)");
        return deleteAlert.showAndWait();
    }

    public static Optional<ButtonType> notImplementedDialog() {
        AlertDialog alert = new AlertDialog(AlertType.WARNING, null, ButtonType.OK);
        alert.setHeaderText("This feature is not available for Markdown.");
        return alert.showAndWait();
    }

    public static Optional<ButtonType> saveAlert() {
        AlertDialog saveAlert = new AlertDialog();
        saveAlert.setHeaderText("This document is not saved. Do you want to close it?");
        return saveAlert.showAndWait();
    }

    public static Optional<ButtonType> conflictAlert(Path path) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("File Cache Conflict");
        alert.setHeaderText(String.format("Changes have been made to '%s' in memory and on disk", path));
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(LOAD_FILE_SYSTEM_CHANGES, KEEP_MEMORY_CHANGES, ButtonType.CANCEL);
        return alert.showAndWait();
    }

    public static Optional<ButtonType> sizeHangAlert(Path path, int hangFileSizeLimit) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(String.format("File size > %dMB", hangFileSizeLimit));
        alert.setHeaderText(String.format("It may cause application being unresponsive", path));
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(OPEN_IN_APP, OPEN_EXTERNAL, ButtonType.CANCEL);
        return alert.showAndWait();
    }

    public static Optional<ButtonType> nosizeAlert(Path path, int hangFileSizeLimit) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("No file size");
        alert.setHeaderText(String.format("It may cause application being unresponsive if it's real size > %dMB", path, hangFileSizeLimit));
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(OPEN_IN_APP, OPEN_EXTERNAL, ButtonType.CANCEL);
        return alert.showAndWait();
    }

    public static void showDuplicateWarning(List<String> duplicatePaths, Path lib) {
        Alert alert = new Alert(Alert.AlertType.WARNING);

        DialogPane dialogPane = alert.getDialogPane();

        ListView listView = new ListView();
        listView.getStyleClass().clear();
        ObservableList items = listView.getItems();
        items.addAll(duplicatePaths);
        listView.setEditable(false);

        dialogPane.setContent(listView);

        alert.setTitle("Duplicate JARs found");
        alert.setHeaderText(String.format("Duplicate JARs found, it may cause unexpected behaviours.\n\n" +
                "Please remove the older versions from these pair(s) manually. \n" +
                "JAR files are located at %s directory.", lib));
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(ButtonType.OK);
        alert.showAndWait();
    }

    public static Optional<String> showOldConfiguration(List<String> paths) {
        Alert alert = new Alert(AlertType.INFORMATION);

        DialogPane dialogPane = alert.getDialogPane();

        ListView listView = new ListView();
        listView.getStyleClass().clear();
        ObservableList items = listView.getItems();
        items.addAll(paths);
        listView.setEditable(false);

        dialogPane.setContent(listView);

        alert.setTitle("Load previous configuration?");
        alert.setHeaderText(String.format("You have configuration files from previous AsciidocFX versions\n\n" +
                "Select the configuration which you want to load configuration \n" +
                "or continue with fresh configuration"));
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(ButtonType.APPLY);
        alert.getButtonTypes().addAll(ButtonType.CANCEL);
        ButtonType buttonType = alert.showAndWait().orElse(ButtonType.CANCEL);

        Object selectedItem = listView.getSelectionModel().getSelectedItem();
        return (buttonType == ButtonType.APPLY) ?
                Optional.ofNullable((String) selectedItem) :
                Optional.empty();
    }
}
