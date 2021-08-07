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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minipos.R;
import com.example.minipos.activities.CameraActivity;
import com.example.minipos.adapters.TerminalAdapter;
import com.example.minipos.models.POS;
import com.example.minipos.models.Product;
import com.example.minipos.roomdb.AppDatabase;
import com.example.minipos.utils.BetterActivityResult;
import com.example.minipos.utils.MyProgressDialog;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class PosTerminalFragment extends Fragment {
    RecyclerView recyclerViewPosTerminal;
    TerminalAdapter adapter;
    AppDatabase roomdb;
    List<Product> productList;
    public TextView textViewWarning, textViewTotalItems;
    TextInputLayout textInputLayoutSearch;
    MyProgressDialog progressDialog;
    ImageView imageViewBarcode, sortListImageView;
    public Button buttonDiscard;

    //properties
    private static final String[] CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA};
    private static final int CAMERA_REQUEST_CODE = 10;
    protected final BetterActivityResult<Intent, ActivityResult> activityLauncher = BetterActivityResult.registerActivityForResult(this);


    double total = 0;

    public PosTerminalFragment() {
        // Required empty public constructor
    }


    public static PosTerminalFragment newInstance(String param1, String param2) {
        PosTerminalFragment fragment = new PosTerminalFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pos_terminal, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);


        recyclerViewPosTerminal = view.findViewById(R.id.productsListPos);
        textViewWarning = view.findViewById(R.id.posWarning);
        textViewTotalItems = view.findViewById(R.id.itemsTotal);
        textInputLayoutSearch = view.findViewById(R.id.posSearchProduct);
        imageViewBarcode = view.findViewById(R.id.scan_barcode);
        buttonDiscard = view.findViewById(R.id.discardBtn);
        sortListImageView = view.findViewById(R.id.sortListImageview);

        roomdb = AppDatabase.getDbInstance(this.getContext());
        productList = roomdb.productDao().getAllProductsAvailable();
        progressDialog = new MyProgressDialog(this.getContext());


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
        imageViewBarcode.setOnClickListener(v -> openBarcode(view));


        textViewTotalItems.setOnClickListener(v -> {
            List<POS> posList = roomdb.posDao().getAllPos();
            for (int i = 0; i < posList.size(); i++) {
                total += posList.get(i).getTotal();
            }
            if (posList.size() > 0) {
                textViewTotalItems.setText("Items: K" + total);
                checkOutReceipt();
            } else {
                progressDialog.showErrorToast("No items!");
            }
        });
        sortListImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortList();
            }
        });


        setRecyclerView();
        return view;
    }

    private void checkOutReceipt() {
        progressDialog.showSuccessToast("show checkout receipt");
    }

    private void setRecyclerView() {
        if (productList.size() > 0) {
            adapter = new TerminalAdapter(this, this.getContext(), productList);

            // setting grid layout manager to implement grid view.
            // in this method '2' represents number of columns to be displayed in grid view.
            GridLayoutManager layoutManager = new GridLayoutManager(this.getContext(), 2);

            // at last set adapter to recycler view.
            recyclerViewPosTerminal.setLayoutManager(layoutManager);
            recyclerViewPosTerminal.setAdapter(adapter);
        } else {
            recyclerViewPosTerminal.setVisibility(View.GONE);
            textViewWarning.setText("No items availabe now!");
            textViewWarning.setVisibility(View.VISIBLE);
        }

    }

    //filtering the list
    private void filter(String text) {
        List<Product> filteredList = new ArrayList<>();
        for (Product product : roomdb.productDao().getAllProductsAvailable()) {
            if (product.getProduct_name().toLowerCase().contains(text.toLowerCase()) || product.getProduct_code().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(product);
                textViewWarning.setVisibility(View.INVISIBLE);
            } else {
                textViewWarning.setVisibility(View.VISIBLE);
                textViewWarning.setText("change query for more filter results!");
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

    public void sortList(){

        adapter.sortList(sortByName(productList));
    }

    public List<Product> sortByName(List<Product> productList) {

        Collections.sort(productList, (product1, product2) -> product1.getProduct_name().compareTo(product2.getProduct_name()));
        return productList;
    }


}