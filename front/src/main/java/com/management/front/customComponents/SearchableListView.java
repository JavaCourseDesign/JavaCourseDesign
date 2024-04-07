package com.management.front.customComponents;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Map;
import java.util.function.Consumer;

public class SearchableListView extends VBox {
    private TextField searchField;
    private ListView<Map<String, Object>> listView;
    private ObservableList<Map<String, Object>> items;
    private FilteredList<Map<String, Object>> filteredItems;
    private Consumer<Map<String, Object>> onItemClick; // Updated to use Map

    public SearchableListView(ObservableList<Map<String, Object>> items) {
        this.items = items;
        initializeComponents();
    }

    private void initializeComponents() {
        searchField = new TextField();
        searchField.setPromptText("Search here!");

        filteredItems = new FilteredList<>(items, p -> true);
        listView = new ListView<>(filteredItems);

        listView.setVisible(false); // Initially invisible
        listView.setManaged(false); // Initially not managed to not take up space

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean showListView = newValue != null && !newValue.isEmpty();
            listView.setVisible(showListView);
            listView.setManaged(showListView);

            filteredItems.setPredicate(item -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String itemValue = ""+item.get("studentId")+item.get("teacherId")+item.get("name"); // Assuming you want to filter based on "name" key
                return itemValue.toLowerCase().contains(newValue.toLowerCase());
            });
        });

        listView.setCellFactory(lv -> new ListCell<Map<String, Object>>() {
            @Override
            protected void updateItem(Map<String, Object> item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(""+item.get("studentId")+item.get("teacherId")+item.get("name")); // Display the "name" attribute
                }
            }
        });

        listView.setOnMouseClicked(event -> {
            Map<String, Object> selectedItem = listView.getSelectionModel().getSelectedItem();
            if (selectedItem != null && onItemClick != null) {
                onItemClick.accept(selectedItem);
            }
        });

        this.getChildren().addAll(searchField, listView);
    }

    public void setOnItemClick(Consumer<Map<String, Object>> action) {
        this.onItemClick = action;
    }
}


