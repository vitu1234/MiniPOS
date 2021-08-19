package com.example.minipos.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minipos.R;
import com.example.minipos.adapters.ProductsAdapter;
import com.example.minipos.models.Product;
import com.example.minipos.roomdb.AppDatabase;
import com.example.minipos.utils.BetterActivityResult;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class ProductsActivity extends AppCompatActivity {

    ProductsAdapter adapter;
    TextView textViewProductsWarning;
    final Context context = this;
    AppDatabase room_db;
    List<Product> productList;

    //properties
    private static final String[] CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA};
    private static final int CAMERA_REQUEST_CODE = 10;
    protected final BetterActivityResult<Intent, ActivityResult> activityLauncher = BetterActivityResult.registerActivityForResult(this);

    TextInputLayout textInputLayoutSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        room_db = AppDatabase.getDbInstance(this);
        textViewProductsWarning = findViewById(R.id.productWarning);
        textInputLayoutSearch = findViewById(R.id.searchProductText);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setProductsRecycler();

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

    private void setProductsRecycler() {
        if (room_db.productDao().countAllProducts() > 0) {
            productList = room_db.productDao().getAllProducts();

            adapter = new ProductsAdapter(this, productList);

// Initialize the RecyclerView and attach the Adapter to it as usual
            RecyclerView recyclerView = findViewById(R.id.productsListRecycler);

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
            textViewProductsWarning.setVisibility(View.VISIBLE);
        }

    }

    public void addProduct(View view) {
        Intent intent = new Intent(getApplicationContext(), AddViewEditProductActivity.class);

        //add shared animation
        Pair[] pairs = new Pair[1];//number of elements to be animated
        pairs[0] = new Pair<View, String>(view.findViewById(R.id.fabaddProduct), "to_add_productTransition");
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, pairs);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }

    public void goback(View view) {
        onBackPressed();
    }

    public void openBarcode(View view) {
        if (hasCameraPermission()) {
            Intent intent = new Intent(this, CameraActivity.class);

            activityLauncher.launch(intent, result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // There are no request codes
                    Intent data = result.getData();
                    if (data.getStringExtra("barcode") != null && !data.getStringExtra("barcode").isEmpty()) {
                        textInputLayoutSearch.getEditText().setText(data.getStringExtra("barcode"));
                    }

                }
            });


        } else {
            requestPermission();
            Toast.makeText(this, "Please grant camera permission", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(
                this,
                CAMERA_PERMISSION,
                CAMERA_REQUEST_CODE
        );
    }

    //filtering the list
    private void filter(String text) {
        List<Product> filteredList = new ArrayList<>();
        for (Product product : room_db.productDao().getAllProducts()) {
            if (product.getProduct_name().toLowerCase().contains(text.toLowerCase()) || product.getProduct_code().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(product);
                textViewProductsWarning.setVisibility(View.INVISIBLE);
            } else {
                textViewProductsWarning.setVisibility(View.VISIBLE);
                textViewProductsWarning.setText("change query for more filter results!");
            }
        }
        if (adapter != null) {
            adapter.filterList(filteredList);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Toast.makeText(context, "resumesd", Toast.LENGTH_SHORT).show();
        productList.clear();
        productList = room_db.productDao().getAllProducts();
        adapter.filterList(productList);
    }
}