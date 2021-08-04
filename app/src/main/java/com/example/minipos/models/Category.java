package com.example.minipos.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "category")
public class Category {
    /*

            "category_id": "1",
            "category_name": "Computer Accessories",
            "category_note": "This is like the first category i think",
            "date_created": "2021-08-04 11:41:37",
            "date_updated": "2021-08-04 11:41:37"
     */
    @PrimaryKey(autoGenerate = true)
    int room_category_id;

    @ColumnInfo(name = "category_id")
    int category_id;

    @ColumnInfo(name = "category_name")
    String category_name;

    @ColumnInfo(name = "category_note")
    String category_note;

    @ColumnInfo(name = "date_created")
    String date_created;


    @ColumnInfo(name = "date_updated")
    String date_updated;


    private boolean showMenu = false;

    public Category(int category_id, String category_name, String category_note, String date_created, String date_updated, boolean showMenu) {
        this.category_id = category_id;
        this.category_name = category_name;
        this.category_note = category_note;
        this.date_created = date_created;
        this.date_updated = date_updated;
        this.showMenu = showMenu;
    }

    public int getRoom_category_id() {
        return room_category_id;
    }

    public void setRoom_category_id(int room_category_id) {
        this.room_category_id = room_category_id;
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

    public String getCategory_note() {
        return category_note;
    }

    public void setCategory_note(String category_note) {
        this.category_note = category_note;
    }
}
