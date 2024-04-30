package com.management.server.request;

import java.util.ArrayList;
import java.util.List;

public class MyTreeNode {
    private Integer id;
    private String value;
    private String label;
    private Integer pid;
    private List<MyTreeNode> childList;
    public MyTreeNode(Integer id, String value, String label){
        this.id  = id;
        this.value = value;
        this.label = label;
        this.childList= new ArrayList<MyTreeNode>();
    }
    public String toString(){
        return label;
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


    public List<MyTreeNode> getChildList() {
        return childList;
    }

    public void setChildList(List<MyTreeNode> childList) {
        this.childList = childList;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

}
