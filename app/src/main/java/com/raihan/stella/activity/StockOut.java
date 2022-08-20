package com.raihan.stella.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.raihan.stella.R;
import com.raihan.stella.model.AutoLogout;
import com.raihan.stella.model.DialogCustom;
import com.raihan.stella.model.GlobalVariable;
import com.raihan.stella.model.Sell;
import com.raihan.stella.model.Stock;
import com.raihan.stella.model.ValidationUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class StockOut extends AutoLogout {
    GlobalVariable globalVariable;
    private ImageView ivLogout;
    private ImageView ivBack;
    private TextView tv_genereal_header_title;

    private EditText date_value;
    private EditText product_value;
    private EditText previous_stock_value;
    private EditText color_value;
    private EditText product_mrp_value;
    private EditText product_percentage_value;
    private EditText product_qty_value;
    private EditText sell_percent_value;
    private EditText sell_price_value;
    private Button btnStockOut;
    private Spinner id_value;
    final Calendar myCalendar = Calendar.getInstance();


    ValueEventListener listener;
    ArrayList<String> spinerList;
    ArrayAdapter<String> adapter;


    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceStock;
    DatabaseReference databaseReferenceProduct;
    DatabaseReference databaseReferenceSell;

    String id = "";
    String productName = "";
    String productId = "";
    String flag = "";
    String color = "";
    String productMrp = "0";
    String productPercent = "0";
    String date = "";
    final LoadingDialog loadingDialog = new LoadingDialog(StockOut.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_out);

        globalVariable = ((GlobalVariable) getApplicationContext());

        ivLogout = findViewById(R.id.ivLogout);
        ivBack = findViewById(R.id.ivBack);
        tv_genereal_header_title = findViewById(R.id.tv_genereal_header_title);

        date_value = findViewById(R.id.date_value);
        product_value = findViewById(R.id.product_value);
        previous_stock_value = findViewById(R.id.previous_stock_value);
        color_value = findViewById(R.id.color_value);
        btnStockOut = findViewById(R.id.btnStockOut);
        product_mrp_value = findViewById(R.id.product_mrp_value);
        id_value = findViewById(R.id.id_value);
        product_percentage_value = findViewById(R.id.product_percentage_value);
        product_qty_value = findViewById(R.id.product_qty_value);
        sell_percent_value = findViewById(R.id.sell_percent_value);
        sell_price_value = findViewById(R.id.sell_price_value);


        spinerList = new ArrayList<>();


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceStock = FirebaseDatabase.getInstance().getReference("Stock");
        databaseReferenceProduct = FirebaseDatabase.getInstance().getReference("Product");
        databaseReferenceSell = FirebaseDatabase.getInstance().getReference("Sell");
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StockOut.this, DashboardActivity.class);
                DialogCustom.doClearActivity(intent, StockOut.this);
            }
        });

        tv_genereal_header_title.setText(R.string.stock_out);

        ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogCustom.englishcustomLogout(StockOut.this);
            }
        });


        date_value.setText(ValidationUtil.getTransactionCurrentDate());

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                date_value.setText(ValidationUtil.dateFormate(myCalendar));
            }
        };
        date_value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(StockOut.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        if (!DialogCustom.isOnline(StockOut.this)) {
            DialogCustom.showInternetConnectionMessage(StockOut.this);
        } else {
            getProductList();
        }


        id_value.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!DialogCustom.isOnline(StockOut.this)) {
                    DialogCustom.showInternetConnectionMessage(StockOut.this);
                } else {
                    product_qty_value.setText("");
                    sell_percent_value.setText("");
                    sell_price_value.setText("");
                    getProductInfo();
                    getPreviousStock();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        sell_percent_value.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (product_qty_value.getText().toString().isEmpty()) {
                    product_qty_value.requestFocus();
                    DialogCustom.showErrorMessage(StockOut.this, "Please Enter Product Quantity");

                } else if (product_qty_value.getText().toString().equals("0")) {
                    product_qty_value.requestFocus();
                    DialogCustom.showErrorMessage(StockOut.this, "Please Enter Valid Product Quantity");

                } else if (sell_percent_value.getText().toString().equals("0")) {
                    sell_percent_value.requestFocus();
                    DialogCustom.showErrorMessage(StockOut.this, "Please Enter Product Sell Percentage");

                } else if (sell_percent_value.getText().toString().isEmpty()) {
                    sell_percent_value.requestFocus();
                    DialogCustom.showErrorMessage(StockOut.this, "Please Enter Product Sell Percentage");

                } else {
                    try {
                        double oldMrp = Double.parseDouble(ValidationUtil.replacecomma(product_mrp_value.getText().toString()));
                        double quantity = Double.parseDouble(ValidationUtil.replacecomma(product_qty_value.getText().toString()));
                        double sellPercentage = Double.parseDouble(ValidationUtil.replacecomma(sell_percent_value.getText().toString()));
                        double totalPrice = (oldMrp * quantity) - ((sellPercentage * oldMrp * quantity) / 100);
                        sell_price_value.setText(String.valueOf(totalPrice));
                    } catch (Exception e) {
                        DialogCustom.showErrorMessage(StockOut.this, e.getMessage());
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        btnStockOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (date_value.getText().toString().trim().isEmpty()) {
                    date_value.requestFocus();
                    DialogCustom.showErrorMessage(StockOut.this, "Please Enter Date.");

                } else if (product_value.getText().toString().trim().isEmpty()) {
                    product_value.requestFocus();
                    DialogCustom.showErrorMessage(StockOut.this, "Please Enter Product Name.");

                } else if (color_value.getText().toString().trim().isEmpty()) {
                    color_value.requestFocus();
                    DialogCustom.showErrorMessage(StockOut.this, "Please Enter Product Color.");

                } else if (product_mrp_value.getText().toString().trim().isEmpty()) {
                    product_mrp_value.requestFocus();
                    DialogCustom.showErrorMessage(StockOut.this, "Please Enter Product MRP Value.");

                } else if (product_percentage_value.getText().toString().trim().isEmpty()) {
                    product_percentage_value.requestFocus();
                    DialogCustom.showErrorMessage(StockOut.this, "Please Enter Product Percentage Value.");

                } else if (product_qty_value.getText().toString().trim().isEmpty()) {
                    product_qty_value.requestFocus();
                    DialogCustom.showErrorMessage(StockOut.this, "Please Enter Product Quantity.");

                } else if (product_qty_value.getText().toString().trim().equals("0")) {
                    product_qty_value.requestFocus();
                    DialogCustom.showErrorMessage(StockOut.this, "Please Enter Valid Product Quantity.");

                } else if (Double.parseDouble(previous_stock_value.getText().toString().trim()) < Double.parseDouble(product_qty_value.getText().toString().trim())) {
                    product_qty_value.requestFocus();
                    DialogCustom.showErrorMessage(StockOut.this, "You Enter Product Quantity Getter Than Stock Quantity.");
                } else if (!DialogCustom.isOnline(StockOut.this)) {
                    DialogCustom.showInternetConnectionMessage(StockOut.this);

                } else {
                    String id = databaseReferenceStock.push().getKey();
                    String productName = product_value.getText().toString().trim();
                    String productId = id_value.getSelectedItem().toString().trim();
                    String date = date_value.getText().toString().trim();
                    String color = color_value.getText().toString().trim();
                    String productMrp = product_mrp_value.getText().toString().trim();
                    String productPercent = product_percentage_value.getText().toString().trim();
                    String productQty = "-" + product_qty_value.getText().toString().trim();
                    String previousStock = previous_stock_value.getText().toString().trim();
                    String stockflg = "OUT";
                    String flag = "Y";
                    String updateBy = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail();
                    String sellPercent = sell_percent_value.getText().toString().trim();
                    String totalPrice = sell_price_value.getText().toString().trim();
                    Sell sell = new Sell(id, productName, productId, date, color, productMrp, productPercent, productQty.trim(), sellPercent, totalPrice, flag, updateBy);
                    Stock stock = new Stock(id, productName, productId, date, color, productMrp, productPercent, productQty.trim(), previousStock, stockflg, flag, updateBy);

                    loadingDialog.startDialoglog();
                    stockOut(id, stock, sell);

                }
            }
        });

    }

    private void stockOut(String id, Stock stock, Sell sell) {
        try {
            assert id != null;
            databaseReferenceStock.child(id)
                    .setValue(stock)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // product_qty_value.setText("");
                                //  sell_percent_value.setText("");
                                // sell_price_value.setText("");
                                // loadingDialog.dismisstDialoglog();
                                // DialogCustom.showSuccessMessage(StockOut.this, "Your Product Stock In Successfully.");
                                sellProduct(id, sell);

                            } else {
                                DialogCustom.showErrorMessage(StockOut.this, task.getResult() + "Unsuccessful");
                                loadingDialog.dismisstDialoglog();

                            }
                            loadingDialog.dismisstDialoglog();
                        }
                    });
        } catch (Exception e) {
            DialogCustom.showErrorMessage(StockOut.this, e.getMessage());
        }
    }

    private void sellProduct(String id, Sell sell) {
        try {
            assert id != null;
            databaseReferenceSell.child(id)
                    .setValue(sell)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                product_qty_value.setText("");
                                sell_percent_value.setText("");
                                sell_price_value.setText("");
                                loadingDialog.dismisstDialoglog();
                                DialogCustom.showSuccessMessage(StockOut.this, "Your Product Stock Out Successfully.");
                                loadingDialog.dismisstDialoglog();

                            } else {
                                DialogCustom.showErrorMessage(StockOut.this, task.getResult() + "Unsuccessful");
                                loadingDialog.dismisstDialoglog();

                            }
                            loadingDialog.dismisstDialoglog();
                        }
                    });
        } catch (Exception e) {
            DialogCustom.showErrorMessage(StockOut.this, e.getMessage());
        }
    }

    private void getProductList() {
        //spinerList.clear();
        try {
            spinerList = new ArrayList<>();

            listener = databaseReferenceProduct.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        if (item.exists() && item.child("flag").getValue().equals("Y")) {
                            String id = "" + item.child("productId").getValue();
                            spinerList.add(id);
                        }


                    }
                    adapter = new ArrayAdapter<String>(StockOut.this, android.R.layout.simple_spinner_dropdown_item, spinerList);

                    id_value.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getProductInfo() {
        if (!DialogCustom.isOnline(StockOut.this)) {
            DialogCustom.showInternetConnectionMessage(StockOut.this);
        } else {
            //loadingDialog.startDialoglog();
            Query queryt = databaseReferenceProduct.orderByChild("productId").equalTo(id_value.getSelectedItem().toString().trim());
            queryt.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        //id = "" + ds.child("id").getValue();
                        date = "" + ds.child("date").getValue();
                        productName = "" + ds.child("productName").getValue();
                        productId = "" + ds.child("productId").getValue();
                        flag = "" + ds.child("flag").getValue();
                        color = "" + ds.child("color").getValue();
                        if (ds.child("productMrp").getValue() == null || ds.child("productMrp").getValue() == "") {
                            productMrp = "0";
                        } else {
                            productMrp = "" + ds.child("productMrp").getValue();
                        }
                        if (ds.child("productPercent").getValue() == null || ds.child("productPercent").getValue() == "") {
                            productPercent = "0";
                        } else {
                            productPercent = "" + ds.child("productPercent").getValue();
                        }

                    }

                    product_value.setText(productName);
                    color_value.setText(color);
                    product_mrp_value.setText(productMrp);
                    product_percentage_value.setText(productPercent);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    DialogCustom.showErrorMessage(StockOut.this, databaseError.getMessage());
                }
            });
        }


    }

    private void getPreviousStock() {
        if (!DialogCustom.isOnline(StockOut.this)) {
            DialogCustom.showInternetConnectionMessage(StockOut.this);
        } else {
            loadingDialog.startDialoglog();
            Query query = databaseReferenceStock.orderByChild("productId").equalTo(id_value.getSelectedItem().toString().trim());
            query.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    loadingDialog.dismisstDialoglog();
                    double total = 0;
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        assert map != null;
                        if (!map.isEmpty() && Objects.equals(map.get("flag"), "Y")) {
                            Object amount = map.get("productQty");
                            try {
                                double pvalue = Double.parseDouble(String.valueOf(amount));
                                total += pvalue;
                                previous_stock_value.setText(String.valueOf(total));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        } else {
                            previous_stock_value.setText(ValidationUtil.replacecomma(String.valueOf(0)));
                        }

                    }
                    if (total < 1) {
                        previous_stock_value.setText(ValidationUtil.replacecomma(String.valueOf(0)));

                    }
                    globalVariable.setTotalAmount(String.valueOf(total));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    loadingDialog.dismisstDialoglog();
                    //throw databaseError.toException(); // don't ignore errors
                    DialogCustom.showErrorMessage(StockOut.this, databaseError.getMessage());
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(StockOut.this, DashboardActivity.class);
        DialogCustom.doClearActivity(intent, StockOut.this);
    }

}