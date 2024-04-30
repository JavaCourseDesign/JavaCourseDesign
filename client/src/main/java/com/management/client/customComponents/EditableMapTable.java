package com.management.client.customComponents;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;

import java.util.Map;
import java.util.AbstractMap.SimpleEntry;
import java.util.stream.Collectors;

public class EditableMapTable extends VBox {
    private final TableView<Map.Entry<String, Object>> tableView = new TableView<>();

    public EditableMapTable(Map<String, Object> map, Map<String, String> displayNameMap) {
        // 过滤并转换Map，只包含displayNameMap中有的键
        ObservableList<Map.Entry<String, Object>> items = FXCollections.observableArrayList(
                map.entrySet().stream()
                        .filter(entry -> displayNameMap.containsKey(entry.getKey()))
                        .map(entry -> new SimpleEntry<>(displayNameMap.get(entry.getKey()), entry.getValue()))
                        .collect(Collectors.toList())
        );
        tableView.setItems(items);

        // 创建并设置列
        TableColumn<Map.Entry<String, Object>, String> displayNameColumn = new TableColumn<>("Display Name");
        displayNameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getKey()));

        TableColumn<Map.Entry<String, Object>, String> valueColumn = new TableColumn<>("Value");
        valueColumn.setCellValueFactory(param -> new SimpleStringProperty(String.valueOf(param.getValue().getValue())));
        valueColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        valueColumn.setOnEditCommit(event -> {
            Map.Entry<String, Object> entry = event.getRowValue();
            // 注意：这里的处理假设值的更改不需要更新原始Map的键，因为显示名称可能与原始键不同
            entry.setValue(event.getNewValue());
        });

        tableView.getColumns().addAll(displayNameColumn, valueColumn);
        tableView.setEditable(true);

        this.getChildren().add(tableView);
    }
}
