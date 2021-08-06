package com.example.minipos.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minipos.R;
import com.example.minipos.activities.AddSupplierActivity;
import com.example.minipos.api.RetrofitClient;
import com.example.minipos.models.AllDataResponse;
import com.example.minipos.models.Category;
import com.example.minipos.models.Product;
import com.example.minipos.models.Supplier;
import com.example.minipos.models.User;
import com.example.minipos.roomdb.AppDatabase;
import com.example.minipos.utils.CheckInternet;
import com.example.minipos.utils.MyProgressDialog;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProductsSupplierAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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

    public ProductsSupplierAdapter(Context context, List<Supplier> supplierList) {
        this.context = context;
        this.supplierList = supplierList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;

        if (viewType == SHOW_MENU) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_menu, parent, false);
            return new MenuViewHolder(v);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_recycler_line, parent, false);
            return new MyViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        AppDatabase room_db = AppDatabase.getDbInstance(context);
        Supplier entity = supplierList.get(position);
        User user = room_db.userDao().findByUserId(entity.getUser_id());

        int user_id = user.getUser_id();
        String supplier_name = user.getFullname();
        String supplier_phone = user.getPhone();
        int supplier_id = entity.getSupplier_id();

        if (holder instanceof MyViewHolder) {
            ((MyViewHolder) holder).textViewName.setText(supplier_name);
            ((MyViewHolder) holder).textViewPhone.setText(supplier_phone);
            ((MyViewHolder) holder).container.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showMenu(position);
                    return true;
                }
            });
        }

        if (holder instanceof MenuViewHolder) {
            ((MenuViewHolder) holder).textViewSupplierNameMenu.setText(supplier_name);
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
                    closeMenu();
                    Intent intent = new Intent(context, AddSupplierActivity.class);
                    intent.putExtra("supplier_id", supplierList.get(position).getSupplier_id());
                    intent.putExtra("user_id", supplierList.get(position).getUser_id());
                    context.startActivity(intent);


                }
            });

            ((MenuViewHolder) holder).buttonDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                    builder1.setMessage("Are you sure to delete supplier? All items related to this supplier will be lost");
                    builder1.setTitle("Warning");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    deleteSupplier(supplierList.get(position).getSupplier_id(), position);
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
        return supplierList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (supplierList.get(position).isShowMenu()) {
            return SHOW_MENU;
        } else {
            return HIDE_MENU;
        }
    }

    public void showMenu(int position) {
        for (int i = 0; i < supplierList.size(); i++) {
            supplierList.get(i).setShowMenu(false);
        }
        supplierList.get(position).setShowMenu(true);
        notifyDataSetChanged();
    }


    public boolean isMenuShown() {
        for (int i = 0; i < supplierList.size(); i++) {
            if (supplierList.get(i).isShowMenu()) {
                return true;
            }
        }
        return false;
    }

    public void closeMenu() {
        for (int i = 0; i < supplierList.size(); i++) {
            supplierList.get(i).setShowMenu(false);
        }
        notifyDataSetChanged();
    }

    //deletes supplier from server
    private void deleteSupplier(int supplier_id, int position) {
        checkInternet = new CheckInternet(context);
        progressDialog = new MyProgressDialog(context);
        room_db = AppDatabase.getDbInstance(context);
        progressDialog.showDialog("Deleting...");

        if (checkInternet.isInternetConnected(context)) {
            call = RetrofitClient.getInstance().getApi().deleteSupplier(supplier_id);
            call.enqueue(new Callback<AllDataResponse>() {
                @Override
                public void onResponse(Call<AllDataResponse> call, Response<AllDataResponse> response) {
                    AllDataResponse response1 = response.body();
                    progressDialog.closeDialog();
                    if (response1 != null) {
                        if (!response1.isError()) {
                            supplierList.remove(position);
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
        } else {
            checkInternet.showInternetDialog(context);
        }
    }

    //filter list'
    // method for filtering our recyclerview items.
    public void filterList(List<Supplier> supplierList) {
        // below line is to add our filtered
        // list in our course array list.
        this.supplierList = supplierList;
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged();
    }


    public void swapItems(List<Supplier> supplierList) {
        this.supplierList = supplierList;
        notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        TextView textViewName, textViewPhone;
        RelativeLayout container;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            container = itemView.findViewById(R.id.frameSupplier);
            textViewName = itemView.findViewById(R.id.user_name);
            textViewPhone = itemView.findViewById(R.id.user_phone_number);

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
        TextView textViewSupplierNameMenu;

        public MenuViewHolder(View view) {
            super(view);
            buttonClose = view.findViewById(R.id.closeBtn);
            buttonEdit = view.findViewById(R.id.editCategoryBtn);
            buttonDel = view.findViewById(R.id.delCategoryBtn);

            textViewSupplierNameMenu = view.findViewById(R.id.categoryNameMenu);
            textViewSupplierNameMenu.setVisibility(View.INVISIBLE);
        }
    }
}
