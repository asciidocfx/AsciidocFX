package com.kodcu;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;


@Component
public class TablePopupController implements Initializable {

    @Autowired
    private Current current;

    public CheckBox tablePopupHeader;
    public CheckBox tablePopupFooter;
    public TextField tablePopupWidth;
    public TextField tablePopupColumns;
    public TextField tablePopupRows;
    public TextField tablePopupTitle;
    public Button tablePopupApply;

    @FXML
    private void tablePopupApply(ActionEvent actionEvent) {

        StringBuffer stringBuffer = new StringBuffer();

        stringBuffer.append("\n");
        String title = tablePopupTitle.textProperty().getValue();
        // Table title
        if (!"".equals(title))
            stringBuffer.append(".").append(title).append("\n");

        // Table options
        stringBuffer.append("[width=\"");

        String width = tablePopupWidth.textProperty().getValue();
        if ("".equals(width))
            stringBuffer.append("100%");
        else
            stringBuffer.append(width);
        stringBuffer.append("\"");

        if (tablePopupHeader.isSelected() && tablePopupFooter.isSelected())
            stringBuffer.append(",").append("options=\"header,footer\"").append("]");

        else if (tablePopupHeader.isSelected())
            stringBuffer.append(",").append("options=\"header\"").append("]");

        else if (tablePopupFooter.isSelected())
            stringBuffer.append(",").append("options=\"footer\"").append("]");
        else
            stringBuffer.append("]");

        stringBuffer.append("\n");

        Integer row = 1;
        Integer column = 1;

        try {
            row = Integer.valueOf(tablePopupRows.textProperty().getValue());
            column = Integer.valueOf(tablePopupColumns.textProperty().getValue());
        } catch (RuntimeException ex) {
        }

        stringBuffer.append("|====================\n");

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                stringBuffer.append("|abcdefq ");
            }
            stringBuffer.append("\n");
        }

        stringBuffer.append("|====================");

        current.currentView().getEngine().executeScript(String.format("editor.insert('%s')", IOHelper.normalize(stringBuffer.toString())));

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public CheckBox getTablePopupHeader() {
        return tablePopupHeader;
    }

    public CheckBox getTablePopupFooter() {
        return tablePopupFooter;
    }


    public TextField getTablePopupWidth() {
        return tablePopupWidth;
    }

    public TextField getTablePopupColumns() {
        return tablePopupColumns;
    }

    public TextField getTablePopupRows() {
        return tablePopupRows;
    }

    public TextField getTablePopupTitle() {
        return tablePopupTitle;
    }

    public Button getTablePopupApply() {
        return tablePopupApply;
    }
}
