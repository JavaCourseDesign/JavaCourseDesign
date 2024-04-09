package com.management.front.customComponents;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SearchableListView extends VBox {
    private List<Map> selectedItems = new ArrayList<>();
    private Label selectedLabel=new Label();
    private TextField searchField=new TextField();
    private ListView<Map<String, Object>> listView;
    private ObservableList<Map<String, Object>> items;
    private FilteredList<Map<String, Object>> filteredItems;
    //private Consumer<Map<String, Object>> onItemClick; // Updated to use Map
    private List<String> keys;

    public SearchableListView(ObservableList<Map<String, Object>> items, List<String> keys) {
        this.keys = keys;
        this.items = items;
        initializeComponents();
    }

    private void initializeComponents() {
        searchField.setPromptText("Search here!");

        filteredItems = new FilteredList<>(items, p -> true);
        listView = new ListView<>(filteredItems);

        //listView.setVisible(false); // Initially invisible
        //listView.setManaged(false); // Initially not managed to not take up space

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean showListView = newValue != null && !newValue.isEmpty();
            listView.setVisible(showListView);
            listView.setManaged(showListView);

            filteredItems.setPredicate(item -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String itemValue = itemToString(item); // Assuming you want to filter based on "name" key
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
                    setText(itemToString(item)); // Display the "name" attribute
                }
            }
        });

        listView.setOnMouseClicked(event -> {
            Map<String, Object> selectedItem = listView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                if(selectedItems.contains(selectedItem)) selectedItems.remove(selectedItem);
                else selectedItems.add(selectedItem);
                selectedLabel.setText(selectedItemsToString());
                searchField.clear();
            }
        });

        listView.setMaxHeight(100);

        this.getChildren().addAll(selectedLabel, searchField, listView);
    }

    /*public void setOnItemClick(Consumer<Map<String, Object>> action) {
        this.onItemClick = action;
    }*/

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
        System.out.println("a:"+selectedItems);
        System.out.println("b:"+items);
        for (int i = 0; i < selectedItems.size(); i++) {
            System.out.println(i);
            System.out.println(selectedItems.get(i));
            if(!items.contains(selectedItems.get(i)))
            {
                selectedItems.remove(i);
                i--;
                System.out.println("removed"+i);
            }
        }
        this.selectedItems = selectedItems;
        selectedLabel.setText(selectedItemsToString());
    }


}


