package com.management.front.customComponents;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchableListView extends VBox {
    private List<Map> selectedItems = new ArrayList<>();
    private Label selectedLabel = new Label();
    private TextField searchField = new TextField();
    private ListView<Map<String, Object>> listView;
    private ObservableList<Map<String, Object>> data;
    private FilteredList<Map<String, Object>> filteredItems;
    private List<String> keys;

    public SearchableListView(ObservableList<Map<String, Object>> data, List<String> keys) {
        this.keys = keys;
        this.data = data;
        initializeComponents();
    }

    private void initializeComponents() {
        setupSearchField();
        setupListView();
        this.getChildren().addAll(selectedLabel, searchField, listView);
    }

    private void setupSearchField() {
        searchField.setPromptText("Search here!");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterItems(newValue));
    }

    private void filterItems(String newValue) {
        boolean showListView = newValue != null && !newValue.isEmpty();
        listView.setVisible(showListView);
        listView.setManaged(showListView);

        filteredItems.setPredicate(item -> {
            if (newValue == null || newValue.isEmpty()) {
                return true;
            }
            String itemValue = itemToString(item);
            return itemValue.toLowerCase().contains(newValue.toLowerCase());
        });
    }

    private void setupListView() {
        filteredItems = new FilteredList<>(data, p -> true);
        listView = new ListView<>(filteredItems);
        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Map<String, Object> item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : itemToString(item));
            }
        });
        listView.setOnMouseClicked(event -> handleItemClick());
        listView.setMaxHeight(100);
        listView.setMinHeight(70);
    }

    private void handleItemClick() {
        Map<String, Object> selectedItem = listView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            if(selectedItems.contains(selectedItem)){
                selectedItems.remove(selectedItem);
            }
            else selectedItems.add(selectedItem);
            selectedLabel.setText(selectedItemsToString());
            searchField.clear();
        }
    }

    private String itemToString(Map<String, Object> item) {
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            sb.append(item.get(key)).append(" ");
        }
        return String.valueOf(sb);
    }

    private String selectedItemsToString() {
        StringBuilder sb = new StringBuilder();
        for (Map item : selectedItems) {
            sb.append(item.get("name")).append(" ");
        }
        return String.valueOf(sb);
    }

    public List<Map> getSelectedItems() {
        return selectedItems;
    }

    public void setSelectedItems(List<Map> selectedItems) {
        List<Map> copyOfSelectedItems = new ArrayList<>(selectedItems);//非常重要！不能直接操作参数，因为传的是引用，会导致原数据被修改
        copyOfSelectedItems.removeIf(item -> !data.contains(item));
        this.selectedItems = copyOfSelectedItems;
        selectedLabel.setText(selectedItemsToString());
    }
}