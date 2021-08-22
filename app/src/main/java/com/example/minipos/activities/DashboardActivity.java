package com.example.minipos.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.minipos.R;
import com.example.minipos.fragments.InventoryFragment;
import com.example.minipos.fragments.ManagementsFragment;
import com.example.minipos.fragments.PosTerminalFragment;
import com.example.minipos.fragments.SettingsFragment;
import com.google.android.material.navigation.NavigationView;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //    ?DRAWER MENU
    static final float END_SCALE = 0.7f;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView menu_icon;
    LinearLayout contentView;
    TextView textVietitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.design_navigation_view);
        menu_icon = findViewById(R.id.menu_icon_btn);
        textVietitle = findViewById(R.id.app_title);

        textVietitle.setText("POS Terminal");
        contentView = findViewById(R.id.content);
//        changeAppTitleText("Magik Rentals");

        //opena navigation drawer
        openNavigationDrawer();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PosTerminalFragment(), null).commit();

    }

    //    NAVIGATION DRAWER
    private void openNavigationDrawer() {
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.pos_terminal);
        MenuItem item = navigationView.getCheckedItem();

        menu_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });
//        Animating the navigation drawer
        animateNavigationDrawer();

    }

    //        Animating the navigation drawer
    private void animateNavigationDrawer() {
        drawerLayout.setScrimColor(getResources().getColor(R.color.drawerOpacity));


        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                //scALE the view based on the current slide offset
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);

                //translate the view, accounting for the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        drawerLayout.closeDrawers();
        switch (item.getItemId()) {

            case R.id.pos_terminal:
                displayFragment(new PosTerminalFragment());
                break;
            case R.id.inventory:
                textVietitle.setText("Inventory");
                displayFragment(new InventoryFragment());
                break;
            case R.id.managements:
                displayFragment(new ManagementsFragment());
                textVietitle.setText("StaticGeneralModel");
                break;
            case R.id.reports:
                startActivity(new Intent(this, ReportsActivity.class));
//                textVietitle.setText("Reports");
                break;

            case R.id.settings:
                textVietitle.setText("Settings");
                displayFragment(new SettingsFragment());
                break;
            case R.id.help:
                textVietitle.setText("Help");
                break;

            case R.id.signout:
                Toast.makeText(this,
                        "signout", Toast.LENGTH_SHORT).show();
                break;

        }


        return true;
    }

    //hooking fragments
    private void displayFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, null).addToBackStack(null).commit();
    }
}