package com.zhengdao.zqb.entity;

public class DropdownItemObject {

    public int    id;
    public String text;
    public String value;
    public String suffix;

    public DropdownItemObject(String text, int id, String value) {
        this.text = text;
        this.id = id;
        this.value = value;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    @Override
    public String toString() {
        return "DropdownItemObject{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", value='" + value + '\'' +
                ", suffix='" + suffix + '\'' +
                '}';
    }
}
