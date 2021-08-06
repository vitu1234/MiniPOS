package com.example.minipos.activities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minipos.R;
import com.example.minipos.adapters.ProductCategoryAdapter;
import com.example.minipos.api.RetrofitClient;
import com.example.minipos.models.AllDataResponse;
import com.example.minipos.models.Category;
import com.example.minipos.models.Product;
import com.example.minipos.models.Supplier;
import com.example.minipos.models.User;
import com.example.minipos.roomdb.AppDatabase;
import com.example.minipos.utils.CheckInternet;
import com.example.minipos.utils.MyProgressDialog;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductsCategoryActivity extends AppCompatActivity {

    ProductCategoryAdapter adapter;
    TextInputLayout textInputLayoutCategoryName, textInputLayoutCategoryNotes, textInputLayoutSearch;
    TextView textViewCategoryWarning;

    final Context context = this;
    AppDatabase room_db;
    CheckInternet checkInternet;
    MyProgressDialog progressDialog;
    AlertDialog alertDialog;

    String category_name = " ", notes = "-";

    Call<AllDataResponse> call;
    private List<User> userList;
    private List<Category> categoryList;
    private List<Supplier> supplierList;
    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_category);
        room_db = AppDatabase.getDbInstance(this);
        checkInternet = new CheckInternet(this);
        progressDialog = new MyProgressDialog(this);

        textViewCategoryWarning = findViewById(R.id.categoryWarning);
        textInputLayoutSearch = findViewById(R.id.searchCategoryLayout);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setCategoryRecycler();

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

    private void setCategoryRecycler() {

        if (room_db.categoryDao().countAllCategorys() > 0) {
            List<Category> categoryList = room_db.categoryDao().getAllCategorys();

            adapter = new ProductCategoryAdapter(this, categoryList);

// Initialize the RecyclerView and attach the Adapter to it as usual
            RecyclerView recyclerView = findViewById(R.id.categoryListRecycler);

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
            textViewCategoryWarning.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onBackPressed() {
        if (adapter.isMenuShown()) {
            adapter.closeMenu();
        } else {
            super.onBackPressed();
        }
    }


    public void goback(View view) {
        onBackPressed();
    }

    public void addCategory(View view) {

        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.category_prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        textInputLayoutCategoryName = promptsView
                .findViewById(R.id.categoryNamePrompt);
        textInputLayoutCategoryNotes = promptsView
                .findViewById(R.id.categoryNotesPrompt);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Save",
                        (dialog, id) -> {
                            // get user input and set it to result
                            // edit text
//                                result.setText(userInput.getText());

                        })
                .setNegativeButton("Cancel",
                        (dialog, id) -> dialog.cancel());

        // create alert dialog
        alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.red));
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean wantToCloseDialog = false;
                //Do stuff, possibly set wantToCloseDialog to true then...
                if (wantToCloseDialog) {
                    alertDialog.dismiss();
                } else {

                    if (textInputLayoutCategoryName.getEditText().getText().toString().isEmpty()) {
                        textInputLayoutCategoryName.setErrorEnabled(true);
                        textInputLayoutCategoryName.setError("required");
                        return;
                    }

                    if (textInputLayoutCategoryNotes.getEditText().getText().toString().isEmpty()) {
                        notes = "-";
                    } else {
                        notes = textInputLayoutCategoryNotes.getEditText().getText().toString();
                    }
                    category_name = textInputLayoutCategoryName.getEditText().getText().toString();
                    if (checkInternet.isInternetConnected(context)) {
                        addCategoryToServer();
                    } else {
                        checkInternet.showInternetDialog(context);
                    }
                }
            }
        });

    }


    private void addCategoryToServer() {
        progressDialog.showDialog("Adding...");
        call = RetrofitClient.getInstance().getApi().add_category(category_name, notes);
        call.enqueue(new Callback<AllDataResponse>() {
            @Override
            public void onResponse(Call<AllDataResponse> call, Response<AllDataResponse> response) {
                AllDataResponse response1 = response.body();
                alertDialog.dismiss();
                progressDialog.closeDialog();
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
                        room_db.productDao().deleteAllProducts();
                        for (int i = 0; i < productList.size(); i++) {
                            room_db.productDao().insertProduct(productList.get(i));
                        }
                        adapter.swapItems(room_db.categoryDao().getAllCategorys());
                        progressDialog.showSuccessToast(response1.getMessage());


                    } else {
                        progressDialog.showErrorToast(response1.getMessage());
                    }
                } else {
                    progressDialog.showErrorToast("No server response!");
                }


            }

            @Override
            public void onFailure(Call<AllDataResponse> call, Throwable t) {
                progressDialog.showErrorToast("No server response");
                progressDialog.closeDialog();
            }
        });


    }

    //filtering the list
    private void filter(String text) {
        List<Category> filteredList = new ArrayList<>();
        for (Category category : room_db.categoryDao().getAllCategorys()) {
            if (category.getCategory_name().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(category);
                textViewCategoryWarning.setVisibility(View.INVISIBLE);
            } else {
                textViewCategoryWarning.setVisibility(View.VISIBLE);
                textViewCategoryWarning.setText("No records found!");
            }
        }
        if (adapter != null) {
            adapter.filterList(filteredList);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (call != null) {
            call.cancel();
        }

        if (adapter != null) {
            if (adapter.call != null) {
                adapter.call.cancel();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (call != null) {
            call.cancel();
        }

        if (adapter != null) {
            if (adapter.call != null) {
                adapter.call.cancel();
            }
        }
    }
}
