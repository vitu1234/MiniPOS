package com.example.minipos.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minipos.R;
import com.example.minipos.activities.CameraActivity;
import com.example.minipos.adapters.InventoryAdapter;
import com.example.minipos.api.RetrofitClient;
import com.example.minipos.models.AllDataResponse;
import com.example.minipos.models.Category;
import com.example.minipos.models.ChangeInventoryItem;
import com.example.minipos.models.Product;
import com.example.minipos.models.Supplier;
import com.example.minipos.models.User;
import com.example.minipos.roomdb.AppDatabase;
import com.example.minipos.utils.BetterActivityResult;
import com.example.minipos.utils.MyProgressDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventoryFragment extends Fragment implements View.OnClickListener {

    RecyclerView recyclerViewInventory;
    InventoryAdapter adapter;
    AppDatabase roomdb;
    List<Product> productList;
    private List<User> userList;
    private List<Category> categoryList;
    private List<Supplier> supplierList;

    public TextView textViewWarning, textViewTotalItems;
    TextInputLayout textInputLayoutSearch;
    MyProgressDialog progressDialog;
    ImageView imageViewBarcode, sortListImageView;
    public MaterialButton buttonReset, buttonSave;
    public LinearLayout linearLayoutButtons;
    Call<AllDataResponse> call;

    //properties
    private static final String[] CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA};
    private static final int CAMERA_REQUEST_CODE = 10;
    protected final BetterActivityResult<Intent, ActivityResult> activityLauncher = BetterActivityResult.registerActivityForResult(this);


    public InventoryFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inventory, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        recyclerViewInventory = view.findViewById(R.id.productsListInventory);
        textViewWarning = view.findViewById(R.id.inventoryWarning);
        textViewTotalItems = view.findViewById(R.id.itemsTotal);
        textInputLayoutSearch = view.findViewById(R.id.inventorySearchProduct);
        imageViewBarcode = view.findViewById(R.id.scan_barcode);
        sortListImageView = view.findViewById(R.id.sortListImageview);
        buttonReset = view.findViewById(R.id.resetChangesBtn);
        buttonSave = view.findViewById(R.id.saveChangesBtn);
        linearLayoutButtons = view.findViewById(R.id.buttonsContainer);

        roomdb = AppDatabase.getDbInstance(this.getContext());
        productList = roomdb.productDao().getAllProducts();
        progressDialog = new MyProgressDialog(this.getContext());

        imageViewBarcode.setOnClickListener(v -> openBarcode(view));
        setRecyclerView();

        sortListImageView.setOnClickListener(v -> sortList());
        textInputLayoutSearch.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        buttonReset.setOnClickListener(this);
        buttonSave.setOnClickListener(this);

        return view;
    }

    private void setRecyclerView() {
        if (productList.size() > 0) {
            adapter = new InventoryAdapter(this, this.getContext(), productList);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            recyclerViewInventory.setLayoutManager(layoutManager);
            recyclerViewInventory.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));//line between items
            // at last set adapter to recycler view.
            recyclerViewInventory.setLayoutManager(layoutManager);
            recyclerViewInventory.setAdapter(adapter);
        } else {
            recyclerViewInventory.setVisibility(View.GONE);
            textViewWarning.setText("No items availabe now!");
            textViewWarning.setVisibility(View.VISIBLE);
        }

    }

    //filtering the list
    private void filter(String text) {
        List<Product> filteredList = new ArrayList<>();
        for (Product product : roomdb.productDao().getAllProducts()) {
            if (product.getProduct_name().toLowerCase().contains(text.toLowerCase()) || product.getProduct_code().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(product);
                textViewWarning.setVisibility(View.INVISIBLE);
            } else {
                textViewWarning.setVisibility(View.VISIBLE);
                textViewWarning.setText("change search query for better results!");
            }
        }
        if (adapter != null) {
            adapter.filterList(filteredList);
        }
    }

    public void openBarcode(View view) {
        if (hasCameraPermission()) {
            Intent intent = new Intent(this.getContext(), CameraActivity.class);

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
            progressDialog.showErrorToast("Please grant camera permission");
        }
    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(
                this.getActivity(),
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(
                this.getActivity(),
                CAMERA_PERMISSION,
                CAMERA_REQUEST_CODE
        );
    }

    public void sortList() {
        linearLayoutButtons.setVisibility(View.GONE);
        adapter.sortList(sortByName(productList));
    }

    public List<Product> sortByName(List<Product> productList) {

        Collections.sort(productList, (product1, product2) -> product1.getProduct_name().compareTo(product2.getProduct_name()));
        return productList;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveChangesBtn:
                saveInventorySave();
                break;
            case R.id.resetChangesBtn:
                cancelChanges();

                break;
        }

    }

    private void cancelChanges() {
        linearLayoutButtons.setVisibility(View.GONE);
        roomdb.changeInventoryItemDao().deleteAllChangeInventoryItems();
        adapter.notifyDataSetChanged();
    }

    private void saveInventorySave() {
        progressDialog.showDialog("Please wait...");
        List<Integer> product_id = new ArrayList<>();
        List<Integer> qty = new ArrayList<>();
        List<ChangeInventoryItem> changeInventoryItems = roomdb.changeInventoryItemDao().getAllChangeInventoryItems();
        for (int i = 0; i < changeInventoryItems.size(); i++) {
            product_id.add(changeInventoryItems.get(i).getProduct_id());
            qty.add(changeInventoryItems.get(i).getQty());
        }

        call = RetrofitClient.getInstance().getApi().update_inventory(product_id, qty);
        call.enqueue(new Callback<AllDataResponse>() {
            @Override
            public void onResponse(Call<AllDataResponse> call, Response<AllDataResponse> response) {
                AllDataResponse response1 = response.body();
                progressDialog.closeDialog();
                if (response1 != null) {
                    if (!response1.isError()) {

                        userList = response1.getUsers();
                        roomdb.userDao().deleteAllUsers();
                        for (int i = 0; i < userList.size(); i++) {
                            roomdb.userDao().insertUser(userList.get(i));
                        }

                        supplierList = response1.getSuppliers();
                        roomdb.supplierDao().deleteAllSuppliers();
                        for (int i = 0; i < supplierList.size(); i++) {
                            roomdb.supplierDao().insertSupplier(supplierList.get(i));
                        }

                        categoryList = response1.getCategories();
                        roomdb.categoryDao().deleteAllCategorys();
                        for (int i = 0; i < categoryList.size(); i++) {
                            roomdb.categoryDao().insertCategory(categoryList.get(i));
                        }

                        productList.clear();
                        productList = response1.getProducts();
                        roomdb.productDao().deleteAllProducts();
                        for (int i = 0; i < productList.size(); i++) {
                            roomdb.productDao().insertProduct(productList.get(i));
                        }

                        progressDialog.showSuccessToast(response1.getMessage());
                        roomdb.changeInventoryItemDao().deleteAllChangeInventoryItems();
                        sortList();
                        linearLayoutButtons.setVisibility(View.GONE);

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (call != null) {
            call.cancel();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (call != null) {
            call.cancel();
        }
    }
}