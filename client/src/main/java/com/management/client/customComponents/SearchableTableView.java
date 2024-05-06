package com.management.client.customComponents;

import com.jfoenix.controls.JFXTextField;
import impl.org.controlsfx.tableview2.filter.parser.string.StringParser;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.controlsfx.control.tableview2.FilteredTableColumn;
import org.controlsfx.control.tableview2.FilteredTableView;
import org.controlsfx.control.tableview2.filter.parser.Parser;
import org.controlsfx.control.tableview2.filter.popupfilter.PopupFilter;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SearchableTableView extends VBox {
    private JFXTextField searchField = new JFXTextField();
    private FilteredTableView<Map> tableView = new FilteredTableView<>();
    private ObservableList<Map> data;
    private List<String> searchableFields;
    private Consumer<Map<String, Object>> onItemClick; // Updated to use Map
    private HBox filterPanel = new HBox();//用于从外部传入筛选面板
    private HBox searchFieldContainer = new HBox(searchField,filterPanel);


    public SearchableTableView(ObservableList<Map> data, List<String> searchableFields, List<FilteredTableColumn<Map,?>> columns) {
        this.data = data;
        this.searchableFields = searchableFields;
        setupTableView(columns);
        setupSearchField();
    }

    private void setupTableView(List<FilteredTableColumn<Map,?>> columns) {
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        for (FilteredTableColumn<Map,?> column : columns) {
            column.setResizable(true);
            PopupFilter<Map, ?> popupFilter = new PopupContainsFilter<>(column);
            column.setOnFilterAction(event -> {
                popupFilter.showPopup();
            });
        }

        tableView.getColumns().addAll(columns);
        //tableView.setPadding(new javafx.geometry.Insets(10));
        tableView.setColumnResizePolicy(FilteredTableView.UNCONSTRAINED_RESIZE_POLICY);

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

        //controlsFx的语句，意义暂不明
        FilteredTableView.configureForFiltering(tableView,sortedData);
        tableView.filter();

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

class PopupContainsFilter<S, T> extends PopupFilter<S, T> {

    private final StringParser<T> stringParser;

    public PopupContainsFilter(FilteredTableColumn<S, T> tableColumn) {
        super(tableColumn);
        stringParser = new StringParser<>(false, getConverter());

        tableColumn.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            // Set the width of the popup to be the same as the width of the tableColumn
            setWidth(newWidth.doubleValue());
        });

        text.addListener((obs, ov, nv) -> {
            if (nv == null || nv.isEmpty()) {
                tableColumn.setPredicate(null);
            } else {
                tableColumn.setPredicate(item -> item != null && item.toString().contains(nv));
            }
        });
    }

    @Override
    public List<String> getOperations() {
        return List.of();
    }

    @Override
    public Parser<T> getParser() {
        return stringParser;
    }

    // --- string converter
    private final ObjectProperty<StringConverter<T>> converter = new SimpleObjectProperty<StringConverter<T>>(this, "converter", defaultStringConverter()) {
        @Override
        protected void invalidated() {
            stringParser.setConverter(get());
        }
    };
    public final ObjectProperty<StringConverter<T>> converterProperty() { return converter; }
    public final void setConverter(StringConverter<T> value) { converterProperty().set(value); }
    public final StringConverter<T> getConverter() { return converterProperty().get(); }

    private static <T> StringConverter<T> defaultStringConverter() {
        return new StringConverter<T>() {
            @Override public String toString(T t) {
                return t == null ? null : t.toString();
            }

            @Override public T fromString(String string) {
                return (T) string;
            }
        };
    }
}