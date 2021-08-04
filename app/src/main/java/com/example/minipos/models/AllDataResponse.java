package com.example.minipos.models;

import java.util.List;

public class AllDataResponse {
    private boolean error;
    private String message;

    private List<User> users;
    private List<Category>categories;
    private List<Supplier>suppliers;
    private List<Product>products;

    public AllDataResponse(boolean error, String message, List<User> users, List<Category> categories, List<Supplier> suppliers, List<Product> products) {
        this.error = error;
        this.message = message;
        this.users = users;
        this.categories = categories;
        this.suppliers = suppliers;
        this.products = products;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<Supplier> getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(List<Supplier> suppliers) {
        this.suppliers = suppliers;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
