package com.example.minipos.activities;

import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minipos.R;
import com.example.minipos.adapters.SaleAdapter;
import com.example.minipos.models.POS;
import com.example.minipos.models.Product;
import com.example.minipos.roomdb.AppDatabase;

import java.util.List;

public class SalesReceiptCheckoutActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    SaleAdapter adapter;
    AppDatabase room_db;
    List<POS> posList;

    public TextView textViewSubTotal, textViewChange, textViewTotal;
    public EditText editTextDiscount, editTextPaidAmount;

    CheckBox checkBoxVAT, checkBoxPrint;
    double sub_total_amount = 0;
    double total_amount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_receipt_checkout);
        recyclerView = findViewById(R.id.salesListRecycler);

        textViewSubTotal = findViewById(R.id.saleSubTotal);
        textViewChange = findViewById(R.id.saleChange);
        textViewTotal = findViewById(R.id.saleTotal);
        editTextDiscount = findViewById(R.id.saleDiscount);
        editTextPaidAmount = findViewById(R.id.salePaidAmount);
        checkBoxVAT = findViewById(R.id.saleVATCheck);
        checkBoxPrint = findViewById(R.id.salePrintReceipt);

        room_db = AppDatabase.getDbInstance(this);

        setViews();
        setSalesRecylerView();

        editTextDiscount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Float.parseFloat(s.toString()) != 0) {
                    double discount = (double) Integer.parseInt(s.toString());
                    double percentage = (total_amount * discount) / 100;
                    editTextDiscount.setText(percentage + "");
                    total_amount = percentage;
                    textViewTotal.setText("K " + total_amount);
                } else {
                    textViewTotal.setText("K " + total_amount);
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editTextPaidAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                double paid_amount = (double) Float.parseFloat(s.toString());
                textViewChange.setText("K " + (total_amount - paid_amount));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        checkBoxVAT.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    double vat = 16.50;
                    double percentage = (total_amount * vat) / 100;
                    total_amount = total_amount + percentage;
                    textViewTotal.setText("K " + total_amount);
                } else {
                    textViewTotal.setText("K " + total_amount);
                }
            }
        });

    }

    public void setViews() {
        posList = room_db.posDao().getAllPosGrouped();
        for (int position = 0; position < posList.size(); position++) {
            POS pos = posList.get(position);
            Product entity = room_db.productDao().findByProductId(pos.getProduct_id());
            int qty = room_db.posDao().totalProQtyPosCount(pos.getProduct_id());

            sub_total_amount += (double) (qty * (Integer.parseInt(entity.getProduct_price())));
            total_amount += (double) (qty * (Integer.parseInt(entity.getProduct_price())));

        }

        textViewSubTotal.setText("K " + sub_total_amount);
        textViewChange.setText("K 0.00");
        textViewTotal.setText("K " + sub_total_amount);
    }


    private void setSalesRecylerView() {

        if (room_db.posDao().countAllPos() > 0) {
            adapter = new SaleAdapter(this, posList);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));//line between items
            recyclerView.setAdapter(adapter);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                recyclerView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> adapter.closeMenu());
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

//            @Override
//            public float getSwipeThreshold( RecyclerView.ViewHolder viewHolder){
//
//                return .5f;
//            }
            };
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchHelperCallback);
            itemTouchHelper.attachToRecyclerView(recyclerView);
        }
    }


    public void goback(View view) {
        onBackPressed();
    }
}