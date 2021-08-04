package com.example.minipos.roomdb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.minipos.models.Supplier;

import java.util.List;

@Dao
public interface SupplierDao {
    @Query("SELECT *FROM supplier")
    List<Supplier> getAllSuppliers();

    @Query("SELECT * FROM supplier WHERE supplier_id = :id")
    Supplier findBySupplierId(int id);

    //    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @Insert()
    void insertSupplier(Supplier supplier);


    @Update
    void updateSupplier(Supplier supplier);

    // below line is use to delete a
    // specific car in our database.
    @Delete
    void deleteSupplier(Supplier supplier);

    // on below line we are making query to
    // delete all cars from our database.
    @Query("DELETE FROM supplier")
    void deleteAllSuppliers();

    //count car
    @Query("SELECT * FROM supplier WHERE supplier_id = :id")
    int getSingleSupplierCount(int id);

    //count all car
    @Query("SELECT COUNT(*) FROM supplier ")
    int countAllSuppliers();
}
