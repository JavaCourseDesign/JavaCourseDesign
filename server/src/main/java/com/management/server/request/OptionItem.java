package com.management.server.request;

public class OptionItem {
    private Integer id;
    private String value;
    private String label;

    public OptionItem(){

    }
    public OptionItem(Integer id, String value, String label){
        this.id = id;
        this.value = value;
        this.label = label;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String toString(){
        return label;
    }

}
