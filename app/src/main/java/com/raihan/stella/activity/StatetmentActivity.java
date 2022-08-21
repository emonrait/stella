package com.raihan.stella.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.raihan.stella.R;
import com.raihan.stella.model.AutoLogout;
import com.raihan.stella.model.DialogCustom;
import com.raihan.stella.model.GlobalVariable;
import com.raihan.stella.model.ListItem;
import com.raihan.stella.model.LogoutService;
import com.raihan.stella.model.MyAdpterNew;
import com.raihan.stella.model.Sell;
import com.raihan.stella.model.SellAdapter;
import com.raihan.stella.model.StatementListAdapter;
import com.raihan.stella.model.StatementStockAdapter;
import com.raihan.stella.model.Stock;

import java.util.ArrayList;
import java.util.Objects;

public class StatetmentActivity extends AutoLogout {
    GlobalVariable globalVariable;
    RecyclerView recyclerView;
    StatementListAdapter statementListAdapter;
    StatementStockAdapter statementStockAdapter;
    SellAdapter sellAdapter;
    FirebaseUser user;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReferenceTransaction;
    DatabaseReference databaseReferenceStock;
    DatabaseReference databaseReferenceProduct;
    DatabaseReference databaseReferenceSell;
    String check;
    private ImageView ivLogout;
    private ImageView ivBack;
    private TextView tv_genereal_header_title;
    private TabLayout tabLayout;


    ArrayList<ListItem> listdata = new ArrayList<>();
    ArrayList<Sell> selldata = new ArrayList<>();
    ArrayList<Stock> stockIn = new ArrayList<>();
    LoadingDialog loadingDialog = new LoadingDialog(this);


    @SuppressLint("RestrictedApi")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statement);

        globalVariable = ((GlobalVariable) getApplicationContext());

        ivLogout = findViewById(R.id.ivLogout);
        ivBack = findViewById(R.id.ivBack);
        tv_genereal_header_title = findViewById(R.id.tv_genereal_header_title);
        recyclerView = findViewById(R.id.recyler);
        tabLayout = findViewById(R.id.tabLayout);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        assert user != null;
        check = user.getEmail();
        databaseReferenceStock = FirebaseDatabase.getInstance().getReference("Stock");
        databaseReferenceProduct = FirebaseDatabase.getInstance().getReference("Product");
        databaseReferenceTransaction = FirebaseDatabase.getInstance().getReference("Transaction");
        databaseReferenceSell = FirebaseDatabase.getInstance().getReference("Sell");

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StatetmentActivity.this, DashboardActivity.class);
                DialogCustom.doClearActivity(intent, StatetmentActivity.this);
            }
        });

        tv_genereal_header_title.setText("Statement");

        ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogCustom.englishcustomLogout(StatetmentActivity.this);
            }
        });
        getAllStockIn();

        //  getFbddata();

        tabLayout.addTab(tabLayout.newTab().setText("Stock In"));
        tabLayout.addTab(tabLayout.newTab().setText("Stock Out"));
        tabLayout.addTab(tabLayout.newTab().setText("Sell"));
        tabLayout.addTab(tabLayout.newTab().setText("Collection"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {

                    if (!DialogCustom.isOnline(StatetmentActivity.this)) {
                        DialogCustom.showInternetConnectionMessage(StatetmentActivity.this);
                    } else {
                        getAllStockIn();
                    }

                } else if (tab.getPosition() == 1) {

                    if (!DialogCustom.isOnline(StatetmentActivity.this)) {
                        DialogCustom.showInternetConnectionMessage(StatetmentActivity.this);
                    } else {
                        getAllStockOut();
                    }

                } else if (tab.getPosition() == 2) {

                    if (!DialogCustom.isOnline(StatetmentActivity.this)) {
                        DialogCustom.showInternetConnectionMessage(StatetmentActivity.this);
                    } else {
                        getAllSell();
                    }

                } else if (tab.getPosition() == 3) {

                    if (!DialogCustom.isOnline(StatetmentActivity.this)) {
                        DialogCustom.showInternetConnectionMessage(StatetmentActivity.this);
                    } else {
                        getTranastionList();
                    }

                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        LogoutService.logout(this);


    }


    private void getTranastionList() {
        listdata.clear();
        loadingDialog.startDialoglog();
        databaseReferenceTransaction.orderByChild("flg").equalTo("Y").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String txn = ds.child("id").getValue(String.class);
                    String date = ds.child("date").getValue(String.class);
                    String amo = ds.child("amount").getValue(String.class);
                    String invoice = ds.child("invoiceno").getValue(String.class);
                    String email = ds.child("email").getValue(String.class);
                    // Log.d("TAG", date + " / "+txn);
                    ListItem listitem = new ListItem(txn, date, amo, invoice, email);
                    listdata.add(listitem);
                    // Log.e("Data--3", listitem.getTxnid());
                    loadingDialog.dismisstDialoglog();

                }

                statementListAdapter = new StatementListAdapter(StatetmentActivity.this, listdata, new StatementListAdapter.OnItemClickListener() {
                    @Override
                    public void onContactSelected(ListItem item) {
                        DialogCustom.showSuccessMessage(StatetmentActivity.this, item.getAmount() + item.getEmail());

                    }
                });
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
                recyclerView.setHasFixedSize(true);

                recyclerView.setAdapter(statementListAdapter);
                statementListAdapter.notifyDataSetChanged();
                loadingDialog.dismisstDialoglog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingDialog.dismisstDialoglog();
                DialogCustom.showErrorMessage(StatetmentActivity.this, databaseError.getMessage());
            }
        });
    }

    private void getAllStockIn() {
        stockIn.clear();
        loadingDialog.startDialoglog();
        databaseReferenceStock.orderByChild("stockflg").equalTo("IN").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    if (ds.exists() && ds.child("flag").getValue().equals("Y")) {
                        String id = ds.child("id").getValue(String.class);
                        String productName = ds.child("productName").getValue(String.class);
                        String productId = ds.child("productId").getValue(String.class);
                        String date = ds.child("date").getValue(String.class);
                        String color = ds.child("color").getValue(String.class);
                        String productMrp = ds.child("productMrp").getValue(String.class);
                        String productPercent = ds.child("productPercent").getValue(String.class);
                        String productQty = ds.child("productQty").getValue(String.class);
                        String previousStock = ds.child("previousStock").getValue(String.class);
                        String flag = ds.child("flag").getValue(String.class);
                        String stockflg = ds.child("stockflg").getValue(String.class);
                        String updateBy = ds.child("updateBy").getValue(String.class);
                        // Log.d("TAG", date + " / "+txn);
                        Stock listitem = new Stock(id,
                                productName, productId, date, color,
                                productMrp,
                                productPercent,
                                productQty,
                                previousStock,
                                flag,
                                stockflg,
                                updateBy
                        );
                        stockIn.add(listitem);
                        // Log.e("Data--3", listitem.getTxnid());
                    }
                    loadingDialog.dismisstDialoglog();

                }
                statementStockAdapter = new StatementStockAdapter(StatetmentActivity.this, stockIn, new StatementStockAdapter.OnItemClickListener() {
                    @Override
                    public void onContactSelected(Stock item) {
                        DialogCustom.showSuccessMessage(StatetmentActivity.this, item.getDate() + item.getProductName());

                    }
                });
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
                recyclerView.setHasFixedSize(true);

                recyclerView.setAdapter(statementStockAdapter);
                statementStockAdapter.notifyDataSetChanged();
                loadingDialog.dismisstDialoglog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingDialog.dismisstDialoglog();
                DialogCustom.showErrorMessage(StatetmentActivity.this, databaseError.getMessage());
            }
        });
    }

    private void getAllStockOut() {
        stockIn.clear();
        loadingDialog.startDialoglog();
        databaseReferenceStock.orderByChild("stockflg").equalTo("OUT").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.exists() && ds.child("flag").getValue().equals("Y")) {
                        String id = ds.child("id").getValue(String.class);
                        String productName = ds.child("productName").getValue(String.class);
                        String productId = ds.child("productId").getValue(String.class);
                        String date = ds.child("date").getValue(String.class);
                        String color = ds.child("color").getValue(String.class);
                        String productMrp = ds.child("productMrp").getValue(String.class);
                        String productPercent = ds.child("productPercent").getValue(String.class);
                        String productQty = ds.child("productQty").getValue(String.class);
                        String previousStock = ds.child("previousStock").getValue(String.class);
                        String flag = ds.child("flag").getValue(String.class);
                        String stockflg = ds.child("stockflg").getValue(String.class);
                        String updateBy = ds.child("updateBy").getValue(String.class);
                        // Log.d("TAG", date + " / "+txn);
                        Stock listitem = new Stock(id,
                                productName, productId, date, color,
                                productMrp,
                                productPercent,
                                productQty,
                                previousStock,
                                flag,
                                stockflg,
                                updateBy
                        );
                        stockIn.add(listitem);
                    }
                    loadingDialog.dismisstDialoglog();

                }
                statementStockAdapter = new StatementStockAdapter(StatetmentActivity.this, stockIn, new StatementStockAdapter.OnItemClickListener() {
                    @Override
                    public void onContactSelected(Stock item) {
                        DialogCustom.showSuccessMessage(StatetmentActivity.this, item.getDate() + item.getProductName());

                    }
                });
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
                recyclerView.setHasFixedSize(true);

                recyclerView.setAdapter(statementStockAdapter);
                statementStockAdapter.notifyDataSetChanged();
                loadingDialog.dismisstDialoglog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingDialog.dismisstDialoglog();
                DialogCustom.showErrorMessage(StatetmentActivity.this, databaseError.getMessage());
            }
        });
    }

    private void getAllSell() {
        selldata.clear();
        loadingDialog.startDialoglog();
        databaseReferenceSell.orderByChild("flag").equalTo("Y").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.exists() && Objects.equals(ds.child("flag").getValue(), "Y")) {
                        String id = ds.child("id").getValue(String.class);
                        String productName = ds.child("productName").getValue(String.class);
                        String productId = ds.child("productId").getValue(String.class);
                        String date = ds.child("date").getValue(String.class);
                        String color = ds.child("color").getValue(String.class);
                        String productMrp = ds.child("productMrp").getValue(String.class);
                        String productPercent = ds.child("productPercent").getValue(String.class);
                        String productQty = ds.child("productQty").getValue(String.class);
                        String sellPercent = ds.child("sellPercent").getValue(String.class);
                        String totalPrice = ds.child("totalPrice").getValue(String.class);
                        String flag = ds.child("flag").getValue(String.class);
                        String updateBy = ds.child("updateBy").getValue(String.class);
                        // Log.d("TAG", date + " / "+txn);
                        Sell sell = new Sell(id,
                                productName, productId, date, color,
                                productMrp,
                                productPercent,
                                productQty,
                                sellPercent,
                                totalPrice,
                                flag,
                                updateBy
                        );
                        selldata.add(sell);
                        // Log.e("Data--3", listitem.getTxnid());
                    }
                    loadingDialog.dismisstDialoglog();

                }
                sellAdapter = new SellAdapter(StatetmentActivity.this, selldata, new SellAdapter.OnItemClickListener() {
                    @Override
                    public void onContactSelected(Sell item) {
                        DialogCustom.showSuccessMessage(StatetmentActivity.this, item.getColor() + item.getProductName());

                    }
                });
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
                recyclerView.setHasFixedSize(true);

                recyclerView.setAdapter(sellAdapter);
                sellAdapter.notifyDataSetChanged();
                loadingDialog.dismisstDialoglog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingDialog.dismisstDialoglog();
                DialogCustom.showErrorMessage(StatetmentActivity.this, databaseError.getMessage());
            }
        });
    }


}
