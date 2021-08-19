package com.example.minipos.roomdb;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.minipos.models.Category;
import com.example.minipos.models.ChangeInventoryItem;
import com.example.minipos.models.POS;
import com.example.minipos.models.Product;
import com.example.minipos.models.Supplier;
import com.example.minipos.models.User;

@Database(entities = {User.class, Product.class, Supplier.class, Category.class, POS.class, ChangeInventoryItem.class}, exportSchema = false, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();

    public abstract SupplierDao supplierDao();

    public abstract CategoryDao categoryDao();

    public abstract ProductDao productDao();

    public abstract PosDao posDao();
    public abstract ChangeInventoryItemDao changeInventoryItemDao();


    private static AppDatabase INSTANCE;

    public static AppDatabase getDbInstance(Context context) {
        String DB_NAME = "minipos";
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DB_NAME)
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }
}
