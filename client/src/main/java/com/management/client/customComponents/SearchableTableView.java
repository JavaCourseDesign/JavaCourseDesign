package com.management.client.customComponents;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.Parent;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import com.jfoenix.controls.JFXTextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SearchableTableView extends VBox {
    private JFXTextField searchField = new JFXTextField();
    private TableView<Map> tableView = new TableView<>();
    private ObservableList<Map> data;
    private List<String> searchableFields;
    private Consumer<Map<String, Object>> onItemClick; // Updated to use Map
    private HBox filterPanel = new HBox();//用于从外部传入筛选面板
    private HBox searchFieldContainer = new HBox(searchField,filterPanel);


    public SearchableTableView(ObservableList<Map> data, List<String> searchableFields, List<TableColumn<Map, ?>> columns) {
        this.data = data;
        this.searchableFields = searchableFields;
        setupTableView(columns);
        setupSearchField();
    }

    private void setupTableView(List<TableColumn<Map, ?>> columns) {
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
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

        //this.setStyle("-fx-background-color: #000000; -fx-padding: 10px;");
        tableView.prefHeightProperty().bind(this.heightProperty());
        tableView.prefWidthProperty().bind(this.widthProperty());

        searchFieldContainer.setSpacing(10);
        this.getChildren().addAll(searchFieldContainer, tableView);
    }

    private void setupSearchField() {
        searchField.setPromptText("Search...");
    }

    public void setData(ObservableList<Map> data) {
        this.data = data;
    }

    public Map getSelectedItem() {
        //多选返回null
        if(tableView.getSelectionModel().getSelectedItems().size() > 1) {
            return null;
        }
        return tableView.getSelectionModel().getSelectedItem();
    }
    public List<Map> getSelectedItems() {
        return tableView.getSelectionModel().getSelectedItems();
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

    public void setFilterPanel(HBox filterPanel) {
        this.filterPanel = filterPanel;
        searchFieldContainer.getChildren().set(1,filterPanel);
    }
}
