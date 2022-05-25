package com.ask2784.fieldmanagement.databases;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Fields {

    private String uId, name, area, currentCrop;

    public Fields() {
    }

    public Fields(String uId, String name, String area, String currentCrop) {
        this.uId = uId;
        this.name = name;
        this.area = area;
        this.currentCrop = currentCrop;
    }

    public String getUId() {
        return uId;
    }

    public void setUId(String uId) {
        this.uId = uId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCurrentCrop() {
        return currentCrop;
    }

    public void setCurrentCrop(String currentCrop) {
        this.currentCrop = currentCrop;
    }

    @NonNull
    @Override
    public String toString() {
        return "Fields [area=" + area + ", currentCrop=" + currentCrop + ", name=" + name + ", uId=" + uId + "]";
    }

}