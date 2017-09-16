package com.kodedu.service.table;

import com.kodedu.controller.ApplicationController;
import com.kodedu.other.Current;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Collections;
import java.util.ResourceBundle;

/**
 * Created by usta on 13.03.2015.
 */
@Component
public class MarkdownTableController implements Initializable {

    public TextField columns;
    public TextField rows;
    public Button tablePopupApply;

    private final Logger logger = LoggerFactory.getLogger(MarkdownTableController.class);
    public TextField initialValue;

    @Autowired
    private Current current;

    @Autowired
    private ApplicationController controller;

    public void createBasicTable(String row, String column) {
        rows.textProperty().setValue(row);
        columns.textProperty().setValue(column);
        initialValue.setText("cell");
        tablePopupApply(null);
    }

    public void tablePopupApply(ActionEvent actionEvent) {
        StringBuilder stringBuffer = new StringBuilder();

        stringBuffer.append("\n");

        Integer row = 1;
        Integer column = 1;

        try {
            row = Integer.valueOf(rows.textProperty().getValue());
            column = Integer.valueOf(columns.textProperty().getValue());
        } catch (RuntimeException e) {
        }

        for (int i = 0; i < row; i++) {
            if (i == 1) {
                for (int j = 0; j < column; j++) {
                    int length = initialValue.getText().length();
                    length = length < 2 ? 2 : length;
                    stringBuffer.append("| " + String.join("", Collections.nCopies(length, "-")) + " ");
                }
                stringBuffer.append("\n");
            }
            for (int j = 0; j < column; j++) {
                stringBuffer.append("| " + initialValue.getText() + " ");
            }
            stringBuffer.append("\n");
        }

        current.insertEditorValue(stringBuffer.toString());

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

}
