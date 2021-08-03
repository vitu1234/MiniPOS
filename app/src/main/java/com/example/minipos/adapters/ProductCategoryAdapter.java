package com.example.minipos.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minipos.R;
import com.example.minipos.activities.ProductsCategoryActivity;
import com.example.minipos.models.Category;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;


public class ProductCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<Category> categoryList;
    private final int SHOW_MENU = 1;
    private final int HIDE_MENU = 2;

    public ProductCategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;

        if (viewType == SHOW_MENU) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_menu, parent, false);
            return new MenuViewHolder(v);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_recycler_line, parent, false);
            return new MyViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Category entity = categoryList.get(position);
        if (holder instanceof MyViewHolder) {
            ((MyViewHolder) holder).textViewName.setText(categoryList.get(position).getCategory_name());
            ((MyViewHolder) holder).container.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showMenu(position);
                    return true;
                }
            });
        }

        if (holder instanceof MenuViewHolder) {
            ((MenuViewHolder) holder).textViewCategoryNameMenu.setText(categoryList.get(position).getCategory_name());
            //Menu Actions
            ((MenuViewHolder) holder).buttonClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeMenu();
                }
            });

            ((MenuViewHolder) holder).buttonEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextInputLayout textInputLayoutCategoryName, textInputLayoutCategoryNotes;
                    TextView textViewcategoryTitle;

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
                    textViewcategoryTitle = promptsView.findViewById(R.id.categoryTitle);

                    textViewcategoryTitle.setText("Edit Category");
                    textInputLayoutCategoryName.getEditText().setText(categoryList.get(position).getCategory_name());

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
                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.red));
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.colorPrimary));


                }
            });

            ((MenuViewHolder) holder).buttonDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                    builder1.setMessage("Are you sure to delete?");
                    builder1.setTitle("Warning");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
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
                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.red));
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.colorPrimary));

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (categoryList.get(position).isShowMenu()) {
            return SHOW_MENU;
        } else {
            return HIDE_MENU;
        }
    }

    public void showMenu(int position) {
        for (int i = 0; i < categoryList.size(); i++) {
            categoryList.get(i).setShowMenu(false);
        }
        categoryList.get(position).setShowMenu(true);
        notifyDataSetChanged();
    }


    public boolean isMenuShown() {
        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryList.get(i).isShowMenu()) {
                return true;
            }
        }
        return false;
    }

    public void closeMenu() {
        for (int i = 0; i < categoryList.size(); i++) {
            categoryList.get(i).setShowMenu(false);
        }
        notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView textViewName;
        FrameLayout container;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.categoryName);
            container = itemView.findViewById(R.id.frameCategory);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View v) {
//            Toast.makeText(v.getContext(), "position = " + getLayoutPosition(), Toast.LENGTH_SHORT).show();

            //go through each item if you have few items within recycler view
            if (getLayoutPosition() == 0) {
                //Do whatever you want here

            } else if (getLayoutPosition() == 1) {
                //Do whatever you want here
                Intent intent = new Intent(v.getContext(), ProductsCategoryActivity.class);
                v.getContext().startActivity(intent);
            } else if (getLayoutPosition() == 2) {


            } else if (getLayoutPosition() == 3) {

            } else if (getLayoutPosition() == 4) {

            } else if (getLayoutPosition() == 5) {

            }
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }


    //Our menu view
    public class MenuViewHolder extends RecyclerView.ViewHolder {

        ImageView buttonClose, buttonEdit, buttonDel;
        TextView textViewCategoryNameMenu;

        public MenuViewHolder(View view) {
            super(view);
            buttonClose = view.findViewById(R.id.closeBtn);
            buttonEdit = view.findViewById(R.id.editCategoryBtn);
            buttonDel = view.findViewById(R.id.delCategoryBtn);
            textViewCategoryNameMenu = view.findViewById(R.id.categoryNameMenu);
        }
    }
}
