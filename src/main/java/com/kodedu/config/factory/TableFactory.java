package com.kodedu.config.factory;

import com.dooapp.fxform.AbstractFXForm;
import com.dooapp.fxform.model.Element;
import com.dooapp.fxform.reflection.ReflectionUtils;
import com.dooapp.fxform.view.FXFormNode;
import com.dooapp.fxform.view.FXFormNodeWrapper;
import com.kodedu.component.TextFieldTableCell;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by usta on 17.07.2015.
 */
public class TableFactory implements Callback<Void, FXFormNode> {

    private final TableView tableView;
    private final Button addButton = new Button("Add");
    private final Button removeButton = new Button("Remove");

    public TableFactory(TableView tableView) {
        this.tableView = tableView;
    }

    @Override
    public FXFormNode call(Void param) {
        tableView.setEditable(true);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        return new FXFormNodeWrapper(new VBox(3, tableView, new HBox(5, addButton, removeButton)), tableView.itemsProperty()) {

            @Override
            public void init(Element element, AbstractFXForm fxForm) {
                super.init(element, fxForm);
                Class wrappedType = element.getWrappedType();
                List<Field> fields = ReflectionUtils.listFields(wrappedType);
                for (Field field : fields) {
                    TableColumn col = new TableColumn(field.getName());
                    col.setCellValueFactory(new PropertyValueFactory(field.getName()));
                    col.setCellFactory(list -> new TextFieldTableCell(new DefaultStringConverter()));

                    tableView.getColumns().add(col);

                }

                addButton.setOnAction(event -> {
                    try {
                        tableView.getItems().add(element.getWrappedType().newInstance());
                        tableView.edit(tableView.getItems().size() - 1, (TableColumn) tableView.getColumns().get(0));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                removeButton.setOnAction(event -> {
                    tableView.getItems().removeAll(tableView.getSelectionModel().getSelectedItems());
                });
            }
        };
    }
}
