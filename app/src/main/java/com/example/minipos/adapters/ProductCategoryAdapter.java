package com.example.minipos.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProductCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<Category> categoryList;
    private List<User> userList;
    private List<Supplier> supplierList;
    private List<Product> productList;
    AppDatabase room_db;

    AlertDialog alertDialog;


    private final int SHOW_MENU = 1;
    private final int HIDE_MENU = 2;
    CheckInternet checkInternet;
    MyProgressDialog progressDialog;

    public Call<AllDataResponse> call;

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
                    textInputLayoutCategoryNotes.getEditText().setText(categoryList.get(position).getCategory_note());

                    // set dialog message
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("Save",
                                    (dialog, id) -> {
                                        //save here
                                    })
                            .setNegativeButton("Cancel",
                                    (dialog, id) -> dialog.cancel());

                    // create alert dialog
                    alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.red));
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.colorPrimary));

                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String category_name = " ", notes = "-";
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

                                checkInternet = new CheckInternet(context);
                                progressDialog = new MyProgressDialog(context);

                                if (checkInternet.isInternetConnected(context)) {
                                    updateCategoryToServer(categoryList.get(position).getCategory_id(), category_name, notes, position);
                                } else {
                                    checkInternet.showInternetDialog(context);
                                }
                            }
                        }
                    });


                }
            });

            ((MenuViewHolder) holder).buttonDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                    builder1.setMessage("Are you sure to delete? Products related to this category will also be deleted");
                    builder1.setTitle("Warning");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    deleteCategory(categoryList.get(position).getCategory_id(), position);
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

    //updates category
    private void updateCategoryToServer(int category_id, String category_name, String notes, int position) {
        room_db = AppDatabase.getDbInstance(context);
        progressDialog.showDialog("Updating...");
        call = RetrofitClient.getInstance().getApi().update_category(category_id, category_name, notes);
        call.enqueue(new Callback<AllDataResponse>() {
            @Override
            public void onResponse(Call<AllDataResponse> call, Response<AllDataResponse> response) {

                AllDataResponse response1 = response.body();
                progressDialog.closeDialog();
                if (response1 != null) {
                    if (!response1.isError()) {
//                        notifyItemChanged(position);
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
                        room_db.productDao().getAllProducts();
                        for (int i = 0; i < productList.size(); i++) {
                            room_db.productDao().insertProduct(productList.get(i));
                        }
                        progressDialog.showSuccessToast(response1.getMessage());
                        alertDialog.dismiss();
                        swapItems(room_db.categoryDao().getAllCategorys());

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

    //deletes category from server
    private void deleteCategory(int category_id, int position) {
        checkInternet = new CheckInternet(context);
        progressDialog = new MyProgressDialog(context);
        room_db = AppDatabase.getDbInstance(context);
        progressDialog.showDialog("Deleting...");

        if (checkInternet.isInternetConnected(context)) {
            call = RetrofitClient.getInstance().getApi().deleteCategory(category_id);
            call.enqueue(new Callback<AllDataResponse>() {
                @Override
                public void onResponse(Call<AllDataResponse> call, Response<AllDataResponse> response) {
                    AllDataResponse response1 = response.body();
                    progressDialog.closeDialog();
                    if (response1 != null) {
                        if (!response1.isError()) {
                            categoryList.remove(position);
                            notifyItemRemoved(position);

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
                            room_db.productDao().getAllProducts();
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
        }else{
            checkInternet.showInternetDialog(context);
        }
    }

    //filter list'
    // method for filtering our recyclerview items.
    public void filterList(List<Category> categoryList) {
        // below line is to add our filtered
        // list in our course array list.
        this.categoryList = categoryList;
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged();
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

    //update the list once data is updated
    public void swapItems(List<Category> categoryList) {
        this.categoryList = categoryList;
        notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        TextView textViewName;
        FrameLayout container;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.categoryName);
            container = itemView.findViewById(R.id.frameCategory);

            itemView.setOnLongClickListener(this);

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
