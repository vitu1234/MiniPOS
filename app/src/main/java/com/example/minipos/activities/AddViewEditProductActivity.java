package com.example.minipos.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.minipos.R;
import com.example.minipos.adapters.CategoryAutoCompleteAdapter;
import com.example.minipos.adapters.SupplierAutoCompleteAdapter;
import com.example.minipos.models.Category;
import com.example.minipos.models.User;
import com.example.minipos.roomdb.AppDatabase;
import com.example.minipos.utils.MyProgressDialog;
import com.sandrios.sandriosCamera.internal.SandriosCamera;
import com.sandrios.sandriosCamera.internal.configuration.CameraConfiguration;
import com.sandrios.sandriosCamera.internal.ui.model.Media;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddViewEditProductActivity extends AppCompatActivity {
    AppDatabase room_db;
    ArrayList<Category> arrayListCategories = new ArrayList<>();
    ArrayList<User> arrayListSuppliers = new ArrayList<>();

    AutoCompleteTextView acTextViewCategory, acTextViewSupplier;

    CircleImageView circleImageViewItemPic;

    MyProgressDialog progressDialog;
    int category_id = -1, supplier_id = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_view_edit_product);

        room_db = AppDatabase.getDbInstance(this);
        progressDialog = new MyProgressDialog(this);

        acTextViewCategory = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        acTextViewSupplier = (AutoCompleteTextView) findViewById(R.id.autoCompleteSupplierTextView);
        circleImageViewItemPic = findViewById(R.id.productImageAdd);


        categoriesDropDown();
        suppliersDropDown();

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
                acTextViewCategory.setText(" ");
                acTextViewCategory.showDropDown();
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
                acTextViewSupplier.setText(" ");
                acTextViewSupplier.showDropDown();
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
                    File imgFile = new File(media.getPath());
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    circleImageViewItemPic.setImageBitmap(myBitmap);
                    Toast.makeText(getApplicationContext(), "Media captured.", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

}