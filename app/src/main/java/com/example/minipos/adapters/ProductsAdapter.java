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
import com.example.minipos.activities.AddViewEditProductActivity;
import com.example.minipos.api.RetrofitClient;
import com.example.minipos.models.AllDataResponse;
import com.example.minipos.models.Category;
import com.example.minipos.models.Product;
import com.example.minipos.models.Supplier;
import com.example.minipos.models.User;
import com.example.minipos.roomdb.AppDatabase;
import com.example.minipos.utils.CheckInternet;
import com.example.minipos.utils.MyProgressDialog;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProductsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<Category> categoryList;
    private List<User> userList;
    private List<Supplier> supplierList;
    private List<Product> productList;
    AppDatabase room_db;
    CheckInternet checkInternet;
    MyProgressDialog progressDialog;
    public Call<AllDataResponse> call;

    private final int SHOW_MENU = 1;
    private final int HIDE_MENU = 2;

    public ProductsAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;

        if (viewType == SHOW_MENU) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_menu, parent, false);
            return new MenuViewHolder(v);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_recycler_line, parent, false);
            return new MyViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Product entity = productList.get(position);
        if (holder instanceof MyViewHolder) {
            ((MyViewHolder) holder).textViewName.setText(productList.get(position).getProduct_name());
            ((MyViewHolder) holder).textViewCost.setText("K " + productList.get(position).getProduct_cost());
            ((MyViewHolder) holder).textViewPrice.setText("K " + productList.get(position).getProduct_price());

            String imageUri = RetrofitClient.BASE_URL2 + "images/products/" + productList.get(position).getProduct_img_url();

            Picasso.get().load(imageUri)
                    .placeholder(R.drawable.image_icon)
                    .error(R.drawable.image_icon)
                    .into(((MyViewHolder) holder).itemPic);

            ((MyViewHolder) holder).container.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showMenu(position);
                    return true;
                }
            });
        }

        if (holder instanceof MenuViewHolder) {
            ((MenuViewHolder) holder).textViewProductNameMenu.setText(productList.get(position).getProduct_name());
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
                    Intent intent = new Intent(context, AddViewEditProductActivity.class);
                    intent.putExtra("product_id", productList.get(position).getProduct_id());
                    context.startActivity(intent);


                }
            });

            ((MenuViewHolder) holder).buttonDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                    builder1.setMessage("Are you sure to delete product? All related info will be lost");
                    builder1.setTitle("Warning");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    deleteProduct(productList.get(position).getProduct_id(), position);
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
        return productList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (productList.get(position).isShowMenu()) {
            return SHOW_MENU;
        } else {
            return HIDE_MENU;
        }
    }

    public void showMenu(int position) {
        for (int i = 0; i < productList.size(); i++) {
            productList.get(i).setShowMenu(false);
        }
        productList.get(position).setShowMenu(true);
        notifyDataSetChanged();
    }


    public boolean isMenuShown() {
        for (int i = 0; i < productList.size(); i++) {
            if (productList.get(i).isShowMenu()) {
                return true;
            }
        }
        return false;
    }

    public void closeMenu() {
        for (int i = 0; i < productList.size(); i++) {
            productList.get(i).setShowMenu(false);
        }
        notifyDataSetChanged();
    }

    //deletes supplier from server
    private void deleteProduct(int product_id, int position) {
        checkInternet = new CheckInternet(context);
        progressDialog = new MyProgressDialog(context);
        room_db = AppDatabase.getDbInstance(context);
        progressDialog.showDialog("Deleting...");

        if (checkInternet.isInternetConnected(context)) {
            call = RetrofitClient.getInstance().getApi().deleteProduct(product_id);
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
    public void filterList(List<Product> productList) {
        // below line is to add our filtered
        // list in our course array list.
        this.productList = productList;
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged();
    }


    public void swapItems(List<Product> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        TextView textViewName, textViewCost, textViewPrice;
        RelativeLayout container;

        CircleImageView itemPic;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            container = itemView.findViewById(R.id.frameProduct);
            textViewName = itemView.findViewById(R.id.item_name);
            itemPic = itemView.findViewById(R.id.item_picture);
            textViewCost = itemView.findViewById(R.id.item_cost);
            textViewPrice = itemView.findViewById(R.id.item_price);

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
        TextView textViewProductNameMenu;

        public MenuViewHolder(View view) {
            super(view);
            buttonClose = view.findViewById(R.id.closeBtn);
            buttonEdit = view.findViewById(R.id.editCategoryBtn);
            buttonDel = view.findViewById(R.id.delCategoryBtn);

            textViewProductNameMenu = view.findViewById(R.id.categoryNameMenu);
            textViewProductNameMenu.setVisibility(View.INVISIBLE);
        }
    }
}
