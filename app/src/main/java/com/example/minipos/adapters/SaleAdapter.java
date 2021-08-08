package com.example.minipos.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minipos.R;
import com.example.minipos.models.POS;
import com.example.minipos.models.Product;
import com.example.minipos.roomdb.AppDatabase;

import java.util.List;


public class SaleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;

    private List<POS> posList;
    AppDatabase room_db;


    private final int SHOW_MENU = 1;
    private final int HIDE_MENU = 2;

    public SaleAdapter(Context context, List<POS> posList) {
        this.context = context;
        this.posList = posList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;

        if (viewType == SHOW_MENU) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_menu, parent, false);
            return new MenuViewHolder(v);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sales_recycler_line, parent, false);
            return new MyViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        room_db = AppDatabase.getDbInstance(context);
        POS pos = posList.get(position);
        Product entity = room_db.productDao().findByProductId(pos.getProduct_id());
        int qty = room_db.posDao().totalProQtyPosCount(pos.getProduct_id());

        if (holder instanceof MyViewHolder) {
            ((MyViewHolder) holder).textViewName.setText(entity.getProduct_name());
            ((MyViewHolder) holder).textViewQty.setText("" + qty);
            ((MyViewHolder) holder).textViewPrice.setText("K " + entity.getProduct_price());
            ((MyViewHolder) holder).textViewTotal.setText("K " + (qty*(Integer.parseInt(entity.getProduct_price()))));

            ((MyViewHolder) holder).container.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showMenu(position);
                    return true;
                }
            });
        }

        if (holder instanceof MenuViewHolder) {
            ((MenuViewHolder) holder).textViewProductNameMenu.setText(entity.getProduct_name());
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
                    Toast.makeText(context, "edit product quantity", Toast.LENGTH_SHORT).show();


                }
            });

            ((MenuViewHolder) holder).buttonDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                    builder1.setMessage("Are you sure to delete ?");
                    builder1.setTitle("Warning");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    deleteProduct(posList.get(position).getRoom_pos_id(), position);
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
        return posList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (posList.get(position).isShowMenu()) {
            return SHOW_MENU;
        } else {
            return HIDE_MENU;
        }
    }

    public void showMenu(int position) {
        for (int i = 0; i < posList.size(); i++) {
            posList.get(i).setShowMenu(false);
        }
        posList.get(position).setShowMenu(true);
        notifyDataSetChanged();
    }


    public boolean isMenuShown() {
        for (int i = 0; i < posList.size(); i++) {
            if (posList.get(i).isShowMenu()) {
                return true;
            }
        }
        return false;
    }

    public void closeMenu() {
        for (int i = 0; i < posList.size(); i++) {
            posList.get(i).setShowMenu(false);
        }
        notifyDataSetChanged();
    }

    //deletes supplier from server
    private void deleteProduct(int product_id, int position) {
        Toast.makeText(context, "delete product", Toast.LENGTH_SHORT).show();
    }

    //filter list'
    // method for filtering our recyclerview items.
    public void filterList(List<POS> posList) {
        // below line is to add our filtered
        // list in our course array list.
        this.posList = posList;
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged();
    }


    public void swapItems(List<POS> posList) {
        this.posList = posList;
        notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        TextView textViewName, textViewQty, textViewPrice, textViewTotal;
        FrameLayout container;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            container = itemView.findViewById(R.id.frameSale);
            textViewName = itemView.findViewById(R.id.saleProdName);
            textViewQty = itemView.findViewById(R.id.saleProdQty);
            textViewPrice = itemView.findViewById(R.id.saleProdPrice);
            textViewTotal = itemView.findViewById(R.id.saleProdTotal);

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
