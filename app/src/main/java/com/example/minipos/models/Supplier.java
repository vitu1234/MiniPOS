package com.example.minipos.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "supplier")
public class Supplier {

    @PrimaryKey(autoGenerate = true)
    int room_supplier_id;

    @ColumnInfo(name = "supplier_id")
    int supplier_id;

    @ColumnInfo(name = "user_id")
    int user_id;

    @ColumnInfo(name = "address")
    String address;

    @ColumnInfo(name = "notes")
    String notes;

    @ColumnInfo(name = "is_default")
    int is_default;

    @ColumnInfo(name = "date_created")
    String date_created;

    @ColumnInfo(name = "date_updated")
    String date_updated;

    private boolean showMenu = false;

    public Supplier(int supplier_id, int user_id, String address, String notes, int is_default, String date_created, String date_updated, boolean showMenu) {
        this.supplier_id = supplier_id;
        this.user_id = user_id;
        this.address = address;
        this.notes = notes;
        this.is_default = is_default;
        this.date_created = date_created;
        this.date_updated = date_updated;
        this.showMenu = showMenu;
    }

    public int getRoom_supplier_id() {
        return room_supplier_id;
    }

    public void setRoom_supplier_id(int room_supplier_id) {
        this.room_supplier_id = room_supplier_id;
    }

    public int getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(int supplier_id) {
        this.supplier_id = supplier_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getIs_default() {
        return is_default;
    }

    public void setIs_default(int is_default) {
        this.is_default = is_default;
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
