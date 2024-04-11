package com.management.front.customComponents;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SearchableTableView extends VBox {
    private TextField searchField = new TextField();
    private TableView<Map> tableView = new TableView<>();
    private ObservableList<Map> data;
    private List<String> searchableFields;
    private Consumer<Map<String, Object>> onItemClick; // Updated to use Map


    public SearchableTableView(ObservableList<Map> data, List<String> searchableFields, List<TableColumn<Map, ?>> columns) {
        this.data = data;
        this.searchableFields = searchableFields;
        setupTableView(columns);
        setupSearchField();
    }

    private void setupTableView(List<TableColumn<Map, ?>> columns) {
        tableView.getColumns().addAll(columns);

        FilteredList<Map> filteredData = new FilteredList<>(data, p -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(map -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                for (String key : searchableFields) {
                    Object value = map.get(key);
                    if (value != null && value.toString().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    }
                }
                return false;
            });
        });

        /*tableView.setOnMouseClicked(event -> {
            Map<String, Object> selectedItem = tableView.getSelectionModel().getSelectedItem();
            if (selectedItem != null && onItemClick != null) {
                onItemClick.accept(selectedItem);
            }
        });*/

        tableView.selectionModelProperty().get().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null&&onItemClick != null) {
                onItemClick.accept(newSelection);
            }
        });

        SortedList<Map> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedData);

        this.setStyle("-fx-background-color: #000000; -fx-padding: 10px;");
        tableView.prefHeightProperty().bind(this.heightProperty());
        tableView.prefWidthProperty().bind(this.widthProperty());

        this.getChildren().addAll(searchField, tableView);
    }

    private void setupSearchField() {
        searchField.setPromptText("Search...");
    }

    public void setData(ObservableList<Map> data) {
        this.data = data;
    }

    public Map getSelectedItem() {
        return tableView.getSelectionModel().getSelectedItem();
    }

    public void setSelectedItem(int index) {
        tableView.getSelectionModel().select(index);
    }

    public int getSelectedIndex() {
        return tableView.getSelectionModel().getSelectedIndex();
    }

    public void setOnItemClick(Consumer<Map<String, Object>> action) {
        if(action != null) {
            this.onItemClick = action;
        }
    }//这个逻辑需要再研究一下 还有奇怪的命名
}
