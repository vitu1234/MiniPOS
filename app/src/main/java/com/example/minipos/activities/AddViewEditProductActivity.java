package com.example.minipos.activities;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.minipos.R;
import com.example.minipos.adapters.CategoryAutoCompleteAdapter;
import com.example.minipos.adapters.SupplierAutoCompleteAdapter;
import com.example.minipos.api.RetrofitClient;
import com.example.minipos.models.AllDataResponse;
import com.example.minipos.models.Category;
import com.example.minipos.models.Product;
import com.example.minipos.models.Supplier;
import com.example.minipos.models.User;
import com.example.minipos.roomdb.AppDatabase;
import com.example.minipos.utils.BetterActivityResult;
import com.example.minipos.utils.CheckInternet;
import com.example.minipos.utils.MyProgressDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.sandrios.sandriosCamera.internal.SandriosCamera;
import com.sandrios.sandriosCamera.internal.configuration.CameraConfiguration;
import com.sandrios.sandriosCamera.internal.ui.model.Media;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddViewEditProductActivity extends AppCompatActivity {


    AppDatabase room_db;
    ArrayList<Category> arrayListCategories = new ArrayList<>();
    ArrayList<User> arrayListSuppliers = new ArrayList<>();

    Call<AllDataResponse> call;
    private List<User> userList;
    private List<Category> categoryList;
    private List<Supplier> supplierList;
    private List<Product> productList;

    AutoCompleteTextView acTextViewCategory, acTextViewSupplier;

    TextInputLayout textInputLayoutCategory, textInputLayoutQty, textInputLayoutSupplier, textInputLayoutProName, textInputLayoutProdCode, textInputLayoutCost, textInputLayoutPrice, textInputLayoutThreshold, textInputLayoutDesc;

    CircleImageView circleImageViewItemPic;
    TextView textViewTitle;

    ImageView imageViewChangeProdPic, imageViewDeleteProduct;

    MyProgressDialog progressDialog;
    int category_id = -1, supplier_id = -1, product_id = -1;
    CheckInternet checkInternet;

    String product_code = "";

    //properties
    private static final String[] CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA};
    private static final int CAMERA_REQUEST_CODE = 10;

    protected final BetterActivityResult<Intent, ActivityResult> activityLauncher = BetterActivityResult.registerActivityForResult(this);
    File imgFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_view_edit_product);

        room_db = AppDatabase.getDbInstance(this);
        progressDialog = new MyProgressDialog(this);
        checkInternet = new CheckInternet(this);

        acTextViewCategory = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        acTextViewSupplier = (AutoCompleteTextView) findViewById(R.id.autoCompleteSupplierTextView);
        circleImageViewItemPic = findViewById(R.id.productImageAdd);
        textInputLayoutProName = findViewById(R.id.productNameAdd);
        textInputLayoutProdCode = findViewById(R.id.productCodeAdd);
        textInputLayoutCost = findViewById(R.id.productItemCostAdd);
        textInputLayoutPrice = findViewById(R.id.productItemPriceAdd);
        textInputLayoutThreshold = findViewById(R.id.productItemThresholdAdd);
        textInputLayoutDesc = findViewById(R.id.productNotesAdd);
        textInputLayoutCategory = findViewById(R.id.productCategoryAdd);
        textInputLayoutSupplier = findViewById(R.id.productSupplierAdd);
        textInputLayoutQty = findViewById(R.id.productItemQtyAdd);
        textViewTitle = findViewById(R.id.product_title);
        imageViewChangeProdPic = findViewById(R.id.pickProductPicAdd);
        imageViewDeleteProduct = findViewById(R.id.cancelProductPicAdd);

        categoriesDropDown();
        suppliersDropDown();

        Intent intent = getIntent();

        // There are no request codes
        if (intent.getIntExtra("product_id", -1) > 0) {
            product_id = intent.getIntExtra("product_id", -1);
            setEditProductViews();
            textViewTitle.setText("View|Edit product");
        } else {
            imageViewChangeProdPic.setVisibility(View.GONE);
            imageViewDeleteProduct.setVisibility(View.GONE);
        }


    }

    private void setEditProductViews() {
        Product product = room_db.productDao().findByProductId(product_id);
        String imageUri = RetrofitClient.BASE_URL2 + "images/products/" + product.getProduct_img_url();

        Picasso.get().load(imageUri)
                .placeholder(R.drawable.image_icon)
                .error(R.drawable.image_icon)
                .into(circleImageViewItemPic);

        textInputLayoutProdCode.getEditText().setText(product.getProduct_code());
        textInputLayoutProName.getEditText().setText(product.getProduct_name());
        textInputLayoutCost.getEditText().setText(product.getProduct_cost() + "");
        textInputLayoutPrice.getEditText().setText(product.getProduct_price() + "");
        textInputLayoutThreshold.getEditText().setText(product.getProduct_threshold() + "");
        textInputLayoutQty.getEditText().setText(product.getProduct_quantity() + "");
        textInputLayoutDesc.getEditText().setText(product.getProduct_description());
        category_id = product.getCategory_id();
        supplier_id = product.getSupplier_id();

        int uid = room_db.supplierDao().findBySupplierId(supplier_id).getUser_id();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            acTextViewSupplier.setText(room_db.userDao().findByUserId(uid).getFullname());
            acTextViewCategory.setText(room_db.categoryDao().findByCategoryId(category_id).getCategory_name());

            acTextViewCategory.dismissDropDown();
            acTextViewSupplier.dismissDropDown();
            acTextViewCategory.clearFocus();
            acTextViewSupplier.clearFocus();
        }, 100);


    }

    private void categoriesDropDown() {
        for (int i = 0; i < room_db.categoryDao().getAllCategorys().size(); i++) {
            arrayListCategories.add(room_db.categoryDao().getAllCategorys().get(i));
        }


        //Set the number of characters the user must type before the drop down list is shown
        //Set the adapter
        CategoryAutoCompleteAdapter categoryAutoCompleteAdapter = new CategoryAutoCompleteAdapter(this, arrayListCategories);
        acTextViewCategory.setThreshold(0);
        acTextViewCategory.setAdapter(categoryAutoCompleteAdapter);

        acTextViewCategory.setOnItemClickListener((parent, view, position, id) -> {
            Category model = (Category) categoryAutoCompleteAdapter.getItem(position);
            acTextViewCategory.setText(model.getCategory_name() + " ");
            acTextViewCategory.setSelection(model.getCategory_name().length());
            category_id = model.getCategory_id();
        });

        acTextViewCategory.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                if (product_id == -1) {
                    acTextViewCategory.setText(" ");
                    acTextViewCategory.showDropDown();
                } else {
                    acTextViewCategory.setText(room_db.categoryDao().findByCategoryId(category_id).getCategory_name());
                }

            }
        });

        acTextViewCategory.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                acTextViewCategory.showDropDown();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                acTextViewCategory.showDropDown();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void suppliersDropDown() {
        for (int i = 0; i < room_db.userDao().getAllUserSuppliers("supplier").size(); i++) {
            arrayListSuppliers.add(room_db.userDao().getAllUserSuppliers("supplier").get(i));
        }


        //Set the number of characters the user must type before the drop down list is shown
        //Set the adapter
        SupplierAutoCompleteAdapter supplierAutoCompleteAdapter = new SupplierAutoCompleteAdapter(this, arrayListSuppliers);
        acTextViewSupplier.setThreshold(0);
        acTextViewSupplier.setAdapter(supplierAutoCompleteAdapter);
        supplierAutoCompleteAdapter.notifyDataSetInvalidated();

        acTextViewSupplier.setOnItemClickListener((parent, view, position, id) -> {
            User model = (User) supplierAutoCompleteAdapter.getItem(position);
            acTextViewSupplier.setText(model.getFullname() + " ");
            acTextViewSupplier.setSelection(model.getFullname().length());
            int user_id = model.getUser_id();
            supplier_id = room_db.supplierDao().findByUserId(user_id).getSupplier_id();
        });

        acTextViewSupplier.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {


                if (product_id == -1) {
                    acTextViewSupplier.setText(" ");
                    acTextViewSupplier.showDropDown();
                } else {
                    int uid = room_db.supplierDao().findBySupplierId(supplier_id).getUser_id();
                    acTextViewSupplier.setText(room_db.userDao().findByUserId(uid).getFullname());
                }


            }
        });

        acTextViewSupplier.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                acTextViewSupplier.showDropDown();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                acTextViewSupplier.showDropDown();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void goback(View view) {
        onBackPressed();
    }

    public void pickImage(View view) {
        launchCamera();
    }

    private void launchCamera() {
        SandriosCamera
                .with()
                .setShowPicker(true)
                .setVideoFileSize(20)
                .setMediaAction(CameraConfiguration.MEDIA_ACTION_BOTH)
                .enableImageCropping(true)
                .launchCamera(this);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK
                && requestCode == SandriosCamera.RESULT_CODE
                && data != null) {
            if (data.getSerializableExtra(SandriosCamera.MEDIA) instanceof Media) {
                Media media = (Media) data.getSerializableExtra(SandriosCamera.MEDIA);

                Log.e("File", "" + media.getPath());
                Log.e("Type", "" + media.getType());


                if (media.getType() == 1) {
                    progressDialog.showErrorToast("Video not allowed!");
                } else {
                    imgFile = new File(media.getPath());
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    circleImageViewItemPic.setImageBitmap(myBitmap);
//                    Toast.makeText(getApplicationContext(), "Media captured.", Toast.LENGTH_SHORT).show();
                    if (product_id != -1) {
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            if (checkInternet.isInternetConnected(this)) {
                                changeProductPicture();
                            } else {
                                checkInternet.showInternetDialog(this);
                            }
                        }, 100);
                    }


                }

            }
        }
    }

    private void changeProductPicture() {
        progressDialog.showDialog("Please wait...");
        if (imgFile != null && product_id != -1) {
            RequestBody reqBody = RequestBody.create(MediaType.parse("multipart/form-file"), imgFile);
            MultipartBody.Part partImage = MultipartBody.Part.createFormData("file", imgFile.getName(), reqBody);
            call = RetrofitClient.getInstance().getApi().changeProductImage(partImage, product_id);
            call.enqueue(new Callback<AllDataResponse>() {
                @Override
                public void onResponse(Call<AllDataResponse> call, Response<AllDataResponse> response) {
                    AllDataResponse response1 = response.body();
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
    }

    public void scanBarcode(View view) {


        if (hasCameraPermission()) {
            Intent intent = new Intent(this, CameraActivity.class);

            activityLauncher.launch(intent, result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // There are no request codes
                    Intent data = result.getData();
                    if (data.getStringExtra("barcode") != null && !data.getStringExtra("barcode").isEmpty()) {
                        product_code = data.getStringExtra("barcode");
                        textInputLayoutProdCode.getEditText().setText(data.getStringExtra("barcode"));
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void addProductServer(View view) {

        if (category_id == -1) {
            textInputLayoutCategory.setError("Required | select from dropdown");
            textInputLayoutCategory.setErrorEnabled(true);
            return;
        } else {
            textInputLayoutCategory.setError(null);
            textInputLayoutCategory.setErrorEnabled(false);
        }

        if (supplier_id == -1) {
            textInputLayoutSupplier.setError("Required | select from dropdown");
            textInputLayoutSupplier.setErrorEnabled(true);
            return;
        } else {
            textInputLayoutSupplier.setError(null);
            textInputLayoutSupplier.setErrorEnabled(false);
        }

        if (checkInternet.isInternetConnected(this)) {
            if (validateField(textInputLayoutProdCode) && validateField(textInputLayoutProName) && validateField(textInputLayoutQty) && validateField(textInputLayoutCost) && validateField(textInputLayoutPrice) && validateField(textInputLayoutThreshold)) {
                String notes = "-";
                if (!textInputLayoutDesc.getEditText().getText().toString().isEmpty()) {
                    notes = textInputLayoutDesc.getEditText().getText().toString();
                }
                String product_code = textInputLayoutProdCode.getEditText().getText().toString();
                String product_name = textInputLayoutProName.getEditText().getText().toString();
                String product_cost = textInputLayoutCost.getEditText().getText().toString();
                String product_price = textInputLayoutPrice.getEditText().getText().toString();
                String product_threshold = textInputLayoutThreshold.getEditText().getText().toString();
                String qty = textInputLayoutQty.getEditText().getText().toString();

                if (product_id == -1) {
                    progressDialog.showDialog("Adding...");
                    if (imgFile == null) {
                        call = RetrofitClient.getInstance().getApi().addProduct(supplier_id, category_id, product_name, product_code, product_cost, product_price, qty, product_threshold, notes);
                        call.enqueue(new Callback<AllDataResponse>() {
                            @Override
                            public void onResponse(Call<AllDataResponse> call, Response<AllDataResponse> response) {
                                AllDataResponse response1 = response.body();
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
                                        progressDialog.showSuccessToast(response1.getMessage());

                                        textInputLayoutProdCode.getEditText().setText("");
                                        textInputLayoutProName.getEditText().setText("");
                                        textInputLayoutCost.getEditText().setText("");
                                        textInputLayoutPrice.getEditText().setText("");
                                        textInputLayoutThreshold.getEditText().setText("");
                                        textInputLayoutQty.getEditText().setText("");
                                        textInputLayoutDesc.getEditText().setText("");
                                        category_id = -1;
                                        supplier_id = -1;
                                        acTextViewCategory.setText("");
                                        acTextViewSupplier.setText("");

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
                    } else {
                        RequestBody reqBody = RequestBody.create(MediaType.parse("multipart/form-file"), imgFile);
                        MultipartBody.Part partImage = MultipartBody.Part.createFormData("file", imgFile.getName(), reqBody);
                        call = RetrofitClient.getInstance().getApi().addProductWithPic(partImage, supplier_id, category_id, product_name, product_code, product_cost, product_price, qty, product_threshold, notes);
                        call.enqueue(new Callback<AllDataResponse>() {
                            @Override
                            public void onResponse(Call<AllDataResponse> call, Response<AllDataResponse> response) {
                                AllDataResponse response1 = response.body();
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
                                        progressDialog.showSuccessToast(response1.getMessage());

                                        textInputLayoutProdCode.getEditText().setText("");
                                        textInputLayoutProName.getEditText().setText("");
                                        textInputLayoutCost.getEditText().setText("");
                                        textInputLayoutPrice.getEditText().setText("");
                                        textInputLayoutThreshold.getEditText().setText("");
                                        textInputLayoutQty.getEditText().setText("");
                                        textInputLayoutDesc.getEditText().setText("");
                                        category_id = -1;
                                        supplier_id = -1;
                                        acTextViewCategory.setText("");
                                        acTextViewSupplier.setText("");

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
                } else {
                    progressDialog.showDialog("Updating...");
                    if (imgFile == null) {
                        call = RetrofitClient.getInstance().getApi().editProduct(product_id, supplier_id, category_id, product_name, product_code, product_cost, product_price, qty, product_threshold, notes);
                        call.enqueue(new Callback<AllDataResponse>() {
                            @Override
                            public void onResponse(Call<AllDataResponse> call, Response<AllDataResponse> response) {
                                AllDataResponse response1 = response.body();
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
                                        ;
                                        for (int i = 0; i < productList.size(); i++) {
                                            room_db.productDao().insertProduct(productList.get(i));
                                        }
                                        progressDialog.showSuccessToast(response1.getMessage());

                                        textInputLayoutProdCode.getEditText().setText("");
                                        textInputLayoutProName.getEditText().setText("");
                                        textInputLayoutCost.getEditText().setText("");
                                        textInputLayoutPrice.getEditText().setText("");
                                        textInputLayoutThreshold.getEditText().setText("");
                                        textInputLayoutQty.getEditText().setText("");
                                        textInputLayoutDesc.getEditText().setText("");
                                        category_id = -1;
                                        supplier_id = -1;
                                        acTextViewCategory.setText("");
                                        acTextViewSupplier.setText("");

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
                    } else {
                        RequestBody reqBody = RequestBody.create(MediaType.parse("multipart/form-file"), imgFile);
                        MultipartBody.Part partImage = MultipartBody.Part.createFormData("file", imgFile.getName(), reqBody);
                        call = RetrofitClient.getInstance().getApi().editProductWithPic(partImage, product_id, supplier_id, category_id, product_name, product_code, product_cost, product_price, qty, product_threshold, notes);
                        call.enqueue(new Callback<AllDataResponse>() {
                            @Override
                            public void onResponse(Call<AllDataResponse> call, Response<AllDataResponse> response) {
                                AllDataResponse response1 = response.body();
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
                                        progressDialog.showSuccessToast(response1.getMessage());

                                        textInputLayoutProdCode.getEditText().setText("");
                                        textInputLayoutProName.getEditText().setText("");
                                        textInputLayoutCost.getEditText().setText("");
                                        textInputLayoutPrice.getEditText().setText("");
                                        textInputLayoutThreshold.getEditText().setText("");
                                        textInputLayoutQty.getEditText().setText("");
                                        textInputLayoutDesc.getEditText().setText("");
                                        category_id = -1;
                                        supplier_id = -1;
                                        acTextViewCategory.setText("");
                                        acTextViewSupplier.setText("");

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
                }


            }
        } else {
            checkInternet.showInternetDialog(this);
        }
    }

    public boolean validateField(TextInputLayout textInputLayout) {
        String email = textInputLayout.getEditText().getText().toString();

        if (email.isEmpty()) {
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError("Fill field");
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
            textInputLayout.setError(null);
            return true;


        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (call != null) {
            call.cancel();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (call != null) {
            call.cancel();
        }
    }

    public void deleteProduct(View view) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Are you sure to delete product? All related info will be lost");
        builder1.setTitle("Warning");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                (dialog, id) -> {
                    if (product_id != -1) {
                        progressDialog.showDialog("Deleting...");

                        if (checkInternet.isInternetConnected(AddViewEditProductActivity.this)) {
                            call = RetrofitClient.getInstance().getApi().deleteProduct(product_id);
                            call.enqueue(new Callback<AllDataResponse>() {
                                @Override
                                public void onResponse(Call<AllDataResponse> call, Response<AllDataResponse> response) {
                                    AllDataResponse response1 = response.body();
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
                                            progressDialog.showSuccessToast(response1.getMessage());
                                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                                finish();
                                            }, 500);

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
                        } else {
                            checkInternet.showInternetDialog(AddViewEditProductActivity.this);
                        }
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder1.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(this.getResources().getColor(R.color.red));
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(this.getResources().getColor(R.color.colorPrimary));


    }
}