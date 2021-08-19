package com.example.minipos.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "changeinventoryitem")
public class ChangeInventoryItem {
    @PrimaryKey(autoGenerate = true)
    int room_id;
    @ColumnInfo(name = "product_id")
    int product_id;
    @ColumnInfo(name = "qty")
    int qty;

    public ChangeInventoryItem() {
    }

    public ChangeInventoryItem(int room_id, int product_id, int qty) {
        this.room_id = room_id;
        this.product_id = product_id;
        this.qty = qty;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public int getRoom_id() {
        return room_id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }
}
