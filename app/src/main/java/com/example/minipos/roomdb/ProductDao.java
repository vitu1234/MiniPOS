package com.example.minipos.roomdb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.minipos.models.Product;

import java.util.List;

@Dao
public interface ProductDao {
    @Query("SELECT *FROM product")
    List<Product> getAllProducts();

    @Query("SELECT *FROM product WHERE product_quantity >0 ORDER BY product_name ASC")
    List<Product> getAllProductsAvailable();

    @Query("SELECT * FROM product WHERE product_id = :id")
    Product findByProductId(int id);

    //    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @Insert()
    void insertProduct(Product product);


    @Update
    void updateProduct(Product product);

    // below line is use to delete a
    // specific car in our database.
    @Delete
    void deleteProduct(Product product);

    // on below line we are making query to
    // delete all cars from our database.
    @Query("DELETE FROM product")
    void deleteAllProducts();

    //count car
    @Query("SELECT * FROM product WHERE product_id = :id")
    int getSingleProductCount(int id);

    //count all car
    @Query("SELECT COUNT(*) FROM product ")
    int countAllProducts();
}
