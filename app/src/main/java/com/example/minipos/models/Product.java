package com.example.minipos.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "product")
public class Product {
    /*
            "product_threshold": "2",
            "product_img_url": "2288acf8204acfff.jpg",
            "product_description": "This is a description",
            "date_created": "2021-08-04 12:03:22",
            "date_updated": "2021-08-04 12:03:22"
     */

    @PrimaryKey(autoGenerate = true)
    int room_product_id;

    @ColumnInfo(name = "product_id")
    int product_id;

    @ColumnInfo(name = "supplier_id")
    int supplier_id;

    @ColumnInfo(name = "category_id")
    int category_id;

    @ColumnInfo(name = "product_name")
    String product_name;

    @ColumnInfo(name = "product_code")
    String product_code;
    @ColumnInfo(name = "product_cost")
    String product_cost;

    @ColumnInfo(name = "product_price")
    String product_price;
    @ColumnInfo(name = "product_quantity")
    String product_quantity;
    @ColumnInfo(name = "product_threshold")
    String product_threshold;

    @ColumnInfo(name = "product_img_url")
    String product_img_url;
    @ColumnInfo(name = "product_description")
    String product_description;

    @ColumnInfo(name = "date_created")
    String date_created;


    @ColumnInfo(name = "date_updated")
    String date_updated;
    private boolean showMenu = false;

    public Product(int product_id, int supplier_id, int category_id, String product_name, String product_code, String product_cost, String product_price, String product_quantity, String product_threshold, String product_img_url, String product_description, String date_created, String date_updated, boolean showMenu) {
        this.product_id = product_id;
        this.supplier_id = supplier_id;
        this.category_id = category_id;
        this.product_name = product_name;
        this.product_code = product_code;
        this.product_cost = product_cost;
        this.product_price = product_price;
        this.product_quantity = product_quantity;
        this.product_threshold = product_threshold;
        this.product_img_url = product_img_url;
        this.product_description = product_description;
        this.date_created = date_created;
        this.date_updated = date_updated;
        this.showMenu = showMenu;
    }

    public int getRoom_product_id() {
        return room_product_id;
    }

    public void setRoom_product_id(int room_product_id) {
        this.room_product_id = room_product_id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public int getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(int supplier_id) {
        this.supplier_id = supplier_id;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_code() {
        return product_code;
    }

    public void setProduct_code(String product_code) {
        this.product_code = product_code;
    }

    public String getProduct_cost() {
        return product_cost;
    }

    public void setProduct_cost(String product_cost) {
        this.product_cost = product_cost;
    }

    public String getProduct_price() {
        return product_price;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }

    public String getProduct_quantity() {
        return product_quantity;
    }

    public void setProduct_quantity(String product_quantity) {
        this.product_quantity = product_quantity;
    }

    public String getProduct_threshold() {
        return product_threshold;
    }

    public void setProduct_threshold(String product_threshold) {
        this.product_threshold = product_threshold;
    }

    public String getProduct_img_url() {
        return product_img_url;
    }

    public void setProduct_img_url(String product_img_url) {
        this.product_img_url = product_img_url;
    }

    public String getProduct_description() {
        return product_description;
    }

    public void setProduct_description(String product_description) {
        this.product_description = product_description;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getDate_updated() {
        return date_updated;
    }

    public void setDate_updated(String date_updated) {
        this.date_updated = date_updated;
    }

    public boolean isShowMenu() {
        return showMenu;
    }

    public void setShowMenu(boolean showMenu) {
        this.showMenu = showMenu;
    }
}
