package com.example.minipos.roomdb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.minipos.models.User;

import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT *FROM user")
    List<User> getAllUsers();

    @Query("SELECT * FROM user WHERE user_id = :id")
    User findByUserId(int id);

    //    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @Insert()
    void insertUser(User user);


    @Update
    void updateUser(User user);

    // below line is use to delete a
    // specific car in our database.
    @Delete
    void deleteUser(User user);

    // on below line we are making query to
    // delete all cars from our database.
    @Query("DELETE FROM user")
    void deleteAllUsers();

    //count car
    @Query("SELECT * FROM user WHERE user_id = :id")
    int getSingleUserCount(int id);

    //count all car
    @Query("SELECT COUNT(*) FROM user ")
    int countAllUsers();
}