package com.example.minipos.activities;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minipos.R;
import com.example.minipos.adapters.ProductsSupplierAdapter;
import com.example.minipos.models.Supplier;
import com.example.minipos.models.User;
import com.example.minipos.roomdb.AppDatabase;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class ProductsSupplierActivity extends AppCompatActivity {

    ProductsSupplierAdapter adapter;
    final Context context = this;
    AppDatabase room_db;
    List<Supplier> supplierList;
    TextView textViewSupplierWarning;
    TextInputLayout textInputLayoutSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_supplier);
        room_db = AppDatabase.getDbInstance(this);
        textViewSupplierWarning = findViewById(R.id.supplierWarning);
        textInputLayoutSearch = findViewById(R.id.searchSupplier);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//remove keyboard
        setSupplerRecylerView();

        textInputLayoutSearch.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

    }

    private void setSupplerRecylerView() {

        if (room_db.supplierDao().countAllSuppliers() > 0) {
            supplierList = room_db.supplierDao().getAllSuppliers();
            adapter = new ProductsSupplierAdapter(this, supplierList);

// Initialize the RecyclerView and attach the Adapter to it as usual
            RecyclerView recyclerView = findViewById(R.id.supplierListRecycler);

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));//line between items
            recyclerView.setAdapter(adapter);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                recyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                        adapter.closeMenu();
                    }
                });
            }

            ItemTouchHelper.SimpleCallback touchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                private final ColorDrawable background = new ColorDrawable(getResources().getColor(R.color.drawerOpacity));

                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    adapter.showMenu(viewHolder.getAdapterPosition());
                }

                @Override
                public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                    View itemView = viewHolder.itemView;

                    if (dX > 0) {
                        background.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + ((int) dX), itemView.getBottom());
                    } else if (dX < 0) {
                        background.setBounds(itemView.getRight() + ((int) dX), itemView.getTop(), itemView.getRight(), itemView.getBottom());
                    } else {
                        background.setBounds(0, 0, 0, 0);
                    }

                    background.draw(c);
                }

//            @Override
//            public float getSwipeThreshold( RecyclerView.ViewHolder viewHolder){
//
//                return .5f;
//            }
            };
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchHelperCallback);
            itemTouchHelper.attachToRecyclerView(recyclerView);
        } else {
            textViewSupplierWarning.setVisibility(View.VISIBLE);
        }

    }

    public void addSupplier(View view) {

        Intent intent = new Intent(getApplicationContext(), AddSupplierActivity.class);

        //add shared animation
        Pair[] pairs = new Pair[1];//number of elements to be animated
        pairs[0] = new Pair<View, String>(view.findViewById(R.id.fabaddSupplier), "to_add_supplierTransition");
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, pairs);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }

    }

    //filtering the list
    private void filter(String text) {
        List<Supplier> filteredList = new ArrayList<>();
        for (User user : room_db.userDao().getAllUsers()) {
            if (user.getFullname().toLowerCase().contains(text.toLowerCase())) {
                if (room_db.supplierDao().countAllSuppliersByUserId(user.getUser_id()) > 0) {
                    filteredList.add(room_db.supplierDao().findByUserId(user.getUser_id()));
                    textViewSupplierWarning.setVisibility(View.INVISIBLE);
                }
            } else {
                textViewSupplierWarning.setVisibility(View.VISIBLE);
                textViewSupplierWarning.setText("No records found!");
            }
        }
        if (adapter != null) {
            adapter.filterList(filteredList);
        }
    }

    public void goback(View view) {
        onBackPressed();
    }
}