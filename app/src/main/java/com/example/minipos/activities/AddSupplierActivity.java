package com.example.minipos.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.minipos.R;
import com.example.minipos.api.RetrofitClient;
import com.example.minipos.models.AllDataResponse;
import com.example.minipos.models.Category;
import com.example.minipos.models.Product;
import com.example.minipos.models.Supplier;
import com.example.minipos.models.User;
import com.example.minipos.roomdb.AppDatabase;
import com.example.minipos.utils.CheckInternet;
import com.example.minipos.utils.MyProgressDialog;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddSupplierActivity extends AppCompatActivity {

    TextInputLayout textInputLayoutSupplierName, textInputLayoutSupplierPhone, textInputLayoutSupplierEmail, textInputLayoutSupplierAddress, textInputLayoutSupplierNotes;
    MaterialCheckBox checkBoxIsDefault;
    TextView textViewTitle;

    AppDatabase room_db;
    CheckInternet checkInternet;
    MyProgressDialog progressDialog;

    Call<AllDataResponse> call;
    private List<User> userList;
    private List<Category> categoryList;
    private List<Supplier> supplierList;
    private List<Product> productList;

    int supplier_id = -1, user_id = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_view_edit_supplier);

        textInputLayoutSupplierName = findViewById(R.id.supplierNameAdd);
        textInputLayoutSupplierPhone = findViewById(R.id.supplierPhoneAdd);
        textInputLayoutSupplierEmail = findViewById(R.id.supplierEmailAdd);
        textInputLayoutSupplierAddress = findViewById(R.id.supplierAddressAdd);
        textInputLayoutSupplierNotes = findViewById(R.id.supplierNotesAdd);
        checkBoxIsDefault = findViewById(R.id.checkBoxIsDefault);
        textViewTitle = findViewById(R.id.screen_title);

        room_db = AppDatabase.getDbInstance(this);
        checkInternet = new CheckInternet(this);
        progressDialog = new MyProgressDialog(this);

        Intent intent = getIntent();
        if (intent.getIntExtra("supplier_id", -1) > 0) {
            supplier_id = intent.getIntExtra("supplier_id", -1);
            user_id = intent.getIntExtra("user_id", -1);
            textViewTitle.setText("Update Supplier");

            textInputLayoutSupplierName.getEditText().setText(room_db.userDao().findByUserId(user_id).getFullname());
            textInputLayoutSupplierPhone.getEditText().setText(room_db.userDao().findByUserId(user_id).getPhone());
            textInputLayoutSupplierEmail.getEditText().setText(room_db.userDao().findByUserId(user_id).getEmail());
            textInputLayoutSupplierAddress.getEditText().setText(room_db.supplierDao().findBySupplierId(supplier_id).getAddress());
            textInputLayoutSupplierNotes.getEditText().setText(room_db.supplierDao().findBySupplierId(supplier_id).getNotes());
            if (room_db.supplierDao().findBySupplierId(supplier_id).getIs_default() == 1){
                checkBoxIsDefault.setChecked(true);
            }else{
                checkBoxIsDefault.setChecked(false);
            }


        }

    }

    public void goback(View view) {
        onBackPressed();
    }

    public void saveSupplierToServer(View view) {
        String name = textInputLayoutSupplierName.getEditText().getText().toString();
        String phone = textInputLayoutSupplierPhone.getEditText().getText().toString().trim();
        String email = textInputLayoutSupplierEmail.getEditText().getText().toString().trim();
        String address = textInputLayoutSupplierAddress.getEditText().getText().toString();
        String notes = textInputLayoutSupplierNotes.getEditText().getText().toString();
        int checked = 0;
        if (checkBoxIsDefault.isChecked()) {
            checked = 1;
        }

        if (validationName(textInputLayoutSupplierName) && validationName(textInputLayoutSupplierPhone) && validationName(textInputLayoutSupplierEmail) && validationName(textInputLayoutSupplierAddress)) {
            isValidPhoneNumber(phone);
            isValidMail(email);

            if (notes.isEmpty()) {
                notes = "-";
            }

            if (supplier_id == -1 && user_id ==-1) {

                progressDialog.showDialog("Adding...");
                call = RetrofitClient.getInstance().getApi().add_supplier(name, phone, email, address, notes, checked);
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

                                textInputLayoutSupplierName.getEditText().setText("");
                                textInputLayoutSupplierPhone.getEditText().setText("");
                                textInputLayoutSupplierEmail.getEditText().setText("");
                                textInputLayoutSupplierAddress.getEditText().setText("");
                                textInputLayoutSupplierNotes.getEditText().setText("");
                                checkBoxIsDefault.setChecked(false);

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

            }else{
                progressDialog.showDialog("Updating...");
                call = RetrofitClient.getInstance().getApi().update_supplier(supplier_id,user_id,name, phone, email, address, notes, checked);
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

                                textInputLayoutSupplierName.getEditText().setText("");
                                textInputLayoutSupplierPhone.getEditText().setText("");
                                textInputLayoutSupplierEmail.getEditText().setText("");
                                textInputLayoutSupplierAddress.getEditText().setText("");
                                textInputLayoutSupplierNotes.getEditText().setText("");
                                checkBoxIsDefault.setChecked(false);

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

    private boolean isValidPhoneNumber(CharSequence phoneNumber) {
        if (!TextUtils.isEmpty(phoneNumber)) {
            if (phoneNumber.length() < 10) {
                textInputLayoutSupplierPhone.setErrorEnabled(true);
                textInputLayoutSupplierPhone.setError("Invalid phone");
                return false;
            } else {
                textInputLayoutSupplierPhone.setErrorEnabled(false);
                textInputLayoutSupplierPhone.setError(null);
                return true;
            }
        }
        return false;
    }


    public boolean validationName(TextInputLayout textInputLayout) {
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

    private boolean isValidMail(String email) {
        String EMAIL_STRING = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        return Pattern.compile(EMAIL_STRING).matcher(email).matches();
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
}