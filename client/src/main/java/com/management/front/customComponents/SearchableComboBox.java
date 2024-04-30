package com.management.front.customComponents;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchableComboBox extends VBox {
    private JFXTextField searchField = new JFXTextField();
    private JFXComboBox<Map<String, Object>> comboBox;
    private ObservableList<Map<String, Object>> data;
    private FilteredList<Map<String, Object>> filteredItems;
    private List<String> keys;

    public SearchableComboBox(ObservableList<Map<String, Object>> data, List<String> keys) {
        this.keys = keys;
        this.data = data;
        initializeComponents();
    }

    private void initializeComponents() {
        setupSearchField();
        setupComboBox();
        this.getChildren().addAll(searchField, comboBox);
    }

    private void setupSearchField() {
        searchField.setPromptText("Search here!");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterItems(newValue));
    }

    private void filterItems(String newValue) {
        filteredItems.setPredicate(item -> {
            if (newValue == null || newValue.isEmpty()) {
                return true;
            }
            String itemValue = itemToString(item);
            return itemValue.toLowerCase().contains(newValue.toLowerCase());
        });
    }

    private void setupComboBox() {
        filteredItems = new FilteredList<>(data, p -> true);
        comboBox = new JFXComboBox<>(filteredItems);
        comboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Map<String, Object> item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : itemToString(item));
            }
        });
    }

    private String itemToString(Map<String, Object> item) {
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            sb.append(item.get(key)).append(" ");
        }
        return String.valueOf(sb);
    }

    public Map<String, Object> getSelectedItem() {
        return comboBox.getSelectionModel().getSelectedItem();
    }

    public void setSelectedItem(Map<String, Object> item) {
        comboBox.getSelectionModel().select(item);
    }
}