package com.example.minipos.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user")
public class User {
    @PrimaryKey(autoGenerate = true)
    int room_user_id;

    @ColumnInfo(name = "user_id")
    int user_id;

    @ColumnInfo(name = "fullname")
    String fullname;

    @ColumnInfo(name = "email")
    String email;

    @ColumnInfo(name = "phone")
    String phone;

    @ColumnInfo(name = "img_url", defaultValue = "NULL")
    String img_url;

    @ColumnInfo(name = "user_role")
    String user_role;

    @ColumnInfo(name = "account_status")
    String account_status;

    @ColumnInfo(name = "date_created")
    String date_created;

    public User(int user_id, String fullname, String email, String phone, String img_url, String user_role, String account_status, String date_created) {
        this.user_id = user_id;
        this.fullname = fullname;
        this.email = email;
        this.phone = phone;
        this.img_url = img_url;
        this.user_role = user_role;
        this.account_status = account_status;
        this.date_created = date_created;
    }

    public int getRoom_user_id() {
        return room_user_id;
    }

    public void setRoom_user_id(int room_user_id) {
        this.room_user_id = room_user_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getUser_role() {
        return user_role;
    }

    public void setUser_role(String user_role) {
        this.user_role = user_role;
    }

    public String getAccount_status() {
        return account_status;
    }

    public void setAccount_status(String account_status) {
        this.account_status = account_status;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }
}
