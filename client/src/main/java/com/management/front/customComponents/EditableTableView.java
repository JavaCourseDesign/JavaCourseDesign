package com.management.front.customComponents;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.management.front.util.HttpClientUtil.request;

public class EditableTableView<T> extends TableView<T> {
    public EditableTableView() {
        this.setEditable(true);
        this.setItems(FXCollections.observableArrayList((ArrayList) request("/getAllStudents", null).getData()));
        System.out.println("EditableTableView: " + this.getItems());
        addColumn("学号", "studentId");
        addColumn("姓名", "name");
    }

    public void setTableData(ObservableList<T> data) {
        this.setItems(data);
    }

    public ObservableList<T> getTableData() {
        return this.getItems();
    }

    public void addColumn(String columnName, String property) {
        TableColumn<T, String> column = new TableColumn<>(columnName);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        this.getColumns().add(column);
    }
}