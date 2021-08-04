package com.example.minipos.common;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.minipos.R;
import com.example.minipos.activities.DashboardActivity;
import com.example.minipos.api.RetrofitClient;
import com.example.minipos.models.AllDataResponse;
import com.example.minipos.models.Category;
import com.example.minipos.models.Product;
import com.example.minipos.models.Supplier;
import com.example.minipos.models.User;
import com.example.minipos.roomdb.AppDatabase;
import com.example.minipos.utils.CheckInternet;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SplashScreen extends AppCompatActivity {

    private static int SLIDE_TIMER = 1000;

    SharedPreferences sharedPreferencesOnboardingScreen;
    AppDatabase room_db; //room database instance
    CheckInternet checkInternet;

    Call<AllDataResponse> call;
    private List<User> userList;
    private List<Category> categoryList;
    private List<Supplier> supplierList;
    private List<Product> productList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //remove status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        room_db = AppDatabase.getDbInstance(this);
        room_db.clearAllTables();//clear all tables
        checkInternet = new CheckInternet(this);

        if (checkInternet.isInternetConnected(this)) {
            getAllData();
        } else {
            checkInternet.showInternetDialog(this);
        }


    }

    public void getAllData() {
        call = RetrofitClient.getInstance().getApi().getAllData();
        call.enqueue(new Callback<AllDataResponse>() {
            @Override
            public void onResponse(Call<AllDataResponse> call, Response<AllDataResponse> response) {
                AllDataResponse response1 = response.body();
                if (response1 != null) {
                    if (!response1.isError()) {

                        userList = response1.getUsers();
                        room_db.userDao().deleteAllUsers();
                        for (int i = 0; i < userList.size(); i++) {
                            room_db.userDao().insertUser(userList.get(i));
                        }

                        supplierList = response1.getSuppliers();
                        room_db.supplierDao().deleteAllSuppliers();
                        for (int i = 0; i < supplierList.size(); i++) {
                            room_db.supplierDao().insertSupplier(supplierList.get(i));
                        }

                        categoryList = response1.getCategories();
                        room_db.categoryDao().deleteAllCategorys();
                        for (int i = 0; i < categoryList.size(); i++) {
                            room_db.categoryDao().insertCategory(categoryList.get(i));
                        }

                        productList = response1.getProducts();
                        room_db.productDao().getAllProducts();
                        for (int i = 0; i < productList.size(); i++) {
                            room_db.productDao().insertProduct(productList.get(i));
                        }

                        splashScreen();

                    } else {
                        Toast.makeText(SplashScreen.this, response1.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SplashScreen.this, "No server response!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<AllDataResponse> call, Throwable t) {
                Toast.makeText(SplashScreen.this, "No server response", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void splashScreen() {
        //delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            sharedPreferencesOnboardingScreen = getSharedPreferences("onBoardingScreen", MODE_PRIVATE);

            boolean isFirstTimeUser = sharedPreferencesOnboardingScreen.getBoolean("firstTime", true);
            if (isFirstTimeUser) {

                SharedPreferences.Editor editor = sharedPreferencesOnboardingScreen.edit();
                editor.putBoolean("firstTime", false);
                editor.commit();
                //start the next actitivty after 5 seconds
                startActivity(new Intent(getApplicationContext(), OnBoardingActivity.class));
                finish();
            } else {
                startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                finish();
            }


        }, SLIDE_TIMER);

    }
}