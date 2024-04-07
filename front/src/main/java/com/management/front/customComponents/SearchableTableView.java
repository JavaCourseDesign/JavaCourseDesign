package com.management.front.customComponents;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.lang.reflect.Field;
import java.util.List;

public class SearchableTableView<T> extends VBox {
    private TableView<T> tableView = new TableView<>();
    private TextField searchField = new TextField();
    private ObservableList<T> masterData;
    private List<String> searchableFields;

    public SearchableTableView(ObservableList<T> masterData, List<String> searchableFields, List<TableColumn<T, ?>> columns) {
        this.masterData = masterData;
        this.searchableFields = searchableFields;
        setupTableView(columns);
        setupSearchField();
    }

    private void setupTableView(List<TableColumn<T, ?>> columns) {
        for (TableColumn<T, ?> column : columns) {
            tableView.getColumns().add(column);
        }

        FilteredList<T> filteredData = new FilteredList<>(masterData, p -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(item -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();

                for (String fieldName : searchableFields) {
                    try {
                        Field field = item.getClass().getDeclaredField(fieldName);
                        field.setAccessible(true);
                        Object value = field.get(item);
                        if (value != null && value.toString().toLowerCase().contains(lowerCaseFilter)) {
                            return true;
                        }
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            });
        });

        SortedList<T> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedData);

        this.getChildren().addAll(searchField, tableView);
    }

    private void setupSearchField() {
        searchField.setPromptText("Search...");
    }
}
