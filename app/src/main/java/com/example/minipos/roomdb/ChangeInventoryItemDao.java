package com.example.minipos.roomdb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.minipos.models.ChangeInventoryItem;

import java.util.List;

@Dao
public interface ChangeInventoryItemDao {
    @Query("SELECT *FROM changeinventoryitem")
    List<ChangeInventoryItem> getAllChangeInventoryItems();


    @Query("SELECT * FROM changeinventoryitem WHERE product_id = :id")
    ChangeInventoryItem findByChangeInventoryItemId(int id);

    //    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @Insert()
    void insertChangeInventoryItem(ChangeInventoryItem changeinventoryitem);


    @Update
    void updateChangeInventoryItem(ChangeInventoryItem changeinventoryitem);

    // below line is use to delete a
    // specific car in our database.
    @Delete
    void deleteChangeInventoryItem(ChangeInventoryItem changeinventoryitem);

    // on below line we are making query to
    // delete all cars from our database.
    @Query("DELETE FROM changeinventoryitem")
    void deleteAllChangeInventoryItems();

    @Query("DELETE FROM changeinventoryitem WHERE product_id = :id")
    void deleteChangeInventoryItem(int id);

    //count car
    @Query("SELECT * FROM changeinventoryitem WHERE product_id = :id")
    int getSingleChangeInventoryItemCount(int id);

    //count all car
    @Query("SELECT COUNT(*) FROM changeinventoryitem ")
    int countAllChangeInventoryItems();
}
