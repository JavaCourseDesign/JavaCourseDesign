package com.management.server.request;

import java.util.List;

public class OptionItemList {
    private Integer code;
    private List<OptionItem> itemList;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public List<OptionItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<OptionItem> itemList) {
        this.itemList = itemList;
    }

}
