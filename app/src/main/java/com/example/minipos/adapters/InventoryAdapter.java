package com.example.minipos.adapters;

import android.content.Context;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minipos.R;
import com.example.minipos.fragments.InventoryFragment;
import com.example.minipos.models.ChangeInventoryItem;
import com.example.minipos.models.Product;
import com.example.minipos.roomdb.AppDatabase;
import com.example.minipos.utils.MyProgressDialog;

import java.util.List;


public class InventoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;

    private List<Product> productList;
    AppDatabase room_db;
    MyProgressDialog progressDialog;
    AlertDialog alertDialog;

    private final int SHOW_MENU = 1;
    private final int HIDE_MENU = 2;
    InventoryFragment inventoryFragment;

    public InventoryAdapter(InventoryFragment inventoryFragment, Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
        this.inventoryFragment = inventoryFragment;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;

        if (viewType == SHOW_MENU) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_menu, parent, false);
            return new MenuViewHolder(v);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_inventory_line, parent, false);
            return new MyViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        room_db = AppDatabase.getDbInstance(context);

        if (holder instanceof MyViewHolder) {
            ((MyViewHolder) holder).textViewName.setText(productList.get(position).getProduct_name());
            ((MyViewHolder) holder).editTextQty.setText("" + productList.get(position).getProduct_quantity());

            ((MyViewHolder) holder).buttonAdd.setOnClickListener(v -> {
                Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(100);

                int qty = Integer.parseInt(((MyViewHolder) holder).editTextQty.getText().toString());
                if (qty >= 0) {
                    ((MyViewHolder) holder).editTextQty.setText(qty + 1 + "");
                } else {
                    ((MyViewHolder) holder).editTextQty.setText(0 + "");
                    ((MyViewHolder) holder).buttonMinus.setActivated(false);
                }

            });

            ((MyViewHolder) holder).buttonMinus.setOnClickListener(v -> {
                Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(100);

                int qty = Integer.parseInt(((MyViewHolder) holder).editTextQty.getText().toString());
                if (qty > 0) {
                    ((MyViewHolder) holder).editTextQty.setText(qty - 1 + "");
                } else {
                    ((MyViewHolder) holder).editTextQty.setText(0 + "");
                    ((MyViewHolder) holder).buttonMinus.setActivated(false);
                }

            });

            ((MyViewHolder) holder).editTextQty.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    int qty = Integer.parseInt(s.toString());
                    if (qty > 20000000) {
                        ((MyViewHolder) holder).editTextQty.setText(productList.get(position).getProduct_quantity());
                    } else {
//                        ((MyViewHolder) holder).editTextQty.setText(0 + "");
                        ((MyViewHolder) holder).buttonMinus.setActivated(false);
                    }

                    ChangeInventoryItem changeInventoryItem = new ChangeInventoryItem();
                    changeInventoryItem.setProduct_id(productList.get(position).getProduct_id());
                    changeInventoryItem.setQty(qty);
                    //delete old product_id if it already exists
                    if (room_db.changeInventoryItemDao().getSingleChangeInventoryItemCount(productList.get(position).getProduct_id()) > 0) {
                        room_db.changeInventoryItemDao().deleteChangeInventoryItem(productList.get(position).getProduct_id());
                    }
                    room_db.changeInventoryItemDao().insertChangeInventoryItem(changeInventoryItem);
                    inventoryFragment.linearLayoutButtons.setVisibility(View.VISIBLE);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

        }

        if (holder instanceof MenuViewHolder) {

        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public int getItemViewType(int position) {

        return HIDE_MENU;

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

    public void sortList(List<Product> productList) {
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


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        EditText editTextQty;
        Button buttonAdd, buttonMinus;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.inventoryProdName);
            editTextQty = itemView.findViewById(R.id.inventoryProdQty);
            buttonAdd = itemView.findViewById(R.id.inventoryProdAdd);
            buttonMinus = itemView.findViewById(R.id.inventoryProdMinus);


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
