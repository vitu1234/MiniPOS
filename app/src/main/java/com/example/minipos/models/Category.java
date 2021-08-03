package com.example.minipos.models;

public class Category {
    int category_id;
    String category_name;
    private boolean showMenu = false;

    public Category(int category_id, String category_name, boolean showMenu) {
        this.category_id = category_id;
        this.category_name = category_name;
        this.showMenu = showMenu;
    }

    public Category() {
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public boolean isShowMenu() {
        return showMenu;
    }

    public void setShowMenu(boolean showMenu) {
        this.showMenu = showMenu;
    }
}
