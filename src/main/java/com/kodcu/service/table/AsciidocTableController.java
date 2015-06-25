package com.kodcu.service.table;


import com.kodcu.other.Current;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;


@Component
public class AsciidocTableController implements Initializable {

    private final Logger logger = LoggerFactory.getLogger(AsciidocTableController.class);
    
    private final Current current;

    public TextField initialValue;

    @Autowired
    public AsciidocTableController(final Current current) {
        this.current = current;
    }

    // FIXME: why are those fields public
    public CheckBox tablePopupHeader;
    public CheckBox tablePopupFooter;
    public TextField tablePopupWidth;
    public TextField tablePopupColumns;
    public TextField tablePopupRows;
    public TextField tablePopupTitle;
    public Button tablePopupApply;

    public void createBasicTable(String row, String column) {
        tablePopupRows.textProperty().setValue(row);
        tablePopupColumns.textProperty().setValue(column);
        initialValue.setText("cell");
        tablePopupApply(null);
    }

    @FXML
    private void tablePopupApply(ActionEvent actionEvent) {

        StringBuilder stringBuffer = new StringBuilder();

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
        } catch (RuntimeException e) {}

        stringBuffer.append("|====================\n");

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                stringBuffer.append("| " + initialValue.getText() + " ");
            }
            stringBuffer.append("\n");
        }

        stringBuffer.append("|====================");

        current.insertEditorValue(stringBuffer.toString());

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
