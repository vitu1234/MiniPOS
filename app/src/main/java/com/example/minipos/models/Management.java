package com.example.minipos.models;

public class Management {
    Integer id, img_url;
    String management_name, description;

    public Management(Integer id, Integer img_url, String management_name, String description) {
        this.id = id;
        this.img_url = img_url;
        this.management_name = management_name;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getImg_url() {
        return img_url;
    }

    public void setImg_url(Integer img_url) {
        this.img_url = img_url;
    }

    public String getManagement_name() {
        return management_name;
    }

    public void setManagement_name(String management_name) {
        this.management_name = management_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
