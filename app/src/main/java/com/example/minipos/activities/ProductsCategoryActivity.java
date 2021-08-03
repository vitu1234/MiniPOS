package com.example.minipos.activities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minipos.R;
import com.example.minipos.adapters.ProductCategoryAdapter;
import com.example.minipos.models.Category;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class ProductsCategoryActivity extends AppCompatActivity {

    ProductCategoryAdapter adapter;
    TextInputLayout textInputLayoutCategoryName, textInputLayoutCategoryNotes;

    final Context context = this;
    private Button button;
    private EditText result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_category);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setCategoryRecycler();

    }

    private void setCategoryRecycler() {
        List<Category> categoryList = new ArrayList<>();
        categoryList.add(new Category(1, "PC Accessories", false));
        categoryList.add(new Category(2, "Plumbing", false));
        categoryList.add(new Category(3, "Home Appliances", false));
        categoryList.add(new Category(4, "Car Accessories", false));
        categoryList.add(new Category(5, "Kitchen Items", false));
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
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchHelperCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
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
                            Toast.makeText(context, "" + textInputLayoutCategoryName.getEditText().getText(), Toast.LENGTH_SHORT).show();
                        })
                .setNegativeButton("Cancel",
                        (dialog, id) -> dialog.cancel());

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.red));
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));


    }


}
