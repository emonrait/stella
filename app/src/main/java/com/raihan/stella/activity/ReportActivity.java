
package com.raihan.stella.activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.Query;
import com.raihan.stella.R;
import com.raihan.stella.model.AutoLogout;
import com.raihan.stella.model.DialogCustom;
import com.raihan.stella.model.GlobalVariable;
import com.raihan.stella.model.Report;
import com.raihan.stella.model.ReportListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.raihan.stella.model.ValidationUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

public class ReportActivity extends AutoLogout {
    GlobalVariable globalVariable;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceStock;
    DatabaseReference databaseReferenceSell;
    DatabaseReference databaseReferenceProduct;

    private ImageView ivLogout;
    private ImageView ivBack;
    private TextView tv_genereal_header_title;
    RecyclerView recylerReport;
    private Spinner id_value;

    TextView tv_name;
    TextView tv_deposit_amount;

    ArrayList<Report> listdata = new ArrayList<>();
    ReportListAdapter reportListAdapter;
    ValueEventListener listener;
    ArrayList<String> spinerList = new ArrayList<>();
    ArrayAdapter<String> adapter;

    LoadingDialog loadingDialog = new LoadingDialog(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        globalVariable = ((GlobalVariable) getApplicationContext());

        ivLogout = findViewById(R.id.ivLogout);
        ivBack = findViewById(R.id.ivBack);
        tv_genereal_header_title = findViewById(R.id.tv_genereal_header_title);
        recylerReport = findViewById(R.id.recylerReport);
        id_value = findViewById(R.id.id_value);
        tv_name = findViewById(R.id.tv_name);
        tv_deposit_amount = findViewById(R.id.tv_deposit_amount);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceStock = FirebaseDatabase.getInstance().getReference("Stock");
        databaseReferenceProduct = FirebaseDatabase.getInstance().getReference("Product");
        databaseReferenceSell = FirebaseDatabase.getInstance().getReference("Sell");


        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportActivity.this, DashboardActivity.class);
                DialogCustom.doClearActivity(intent, ReportActivity.this);
            }
        });

        tv_genereal_header_title.setText(R.string.balance_informatiom);

        ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogCustom.englishcustomLogout(ReportActivity.this);
            }
        });

        getProductList();


        id_value.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!DialogCustom.isOnline(ReportActivity.this)) {
                    DialogCustom.showInternetConnectionMessage(ReportActivity.this);
                } else {

                    getProductInfo();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        // getFbddata();


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
                    adapter = new ArrayAdapter<String>(ReportActivity.this, android.R.layout.simple_spinner_dropdown_item, spinerList);

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
        if (!DialogCustom.isOnline(ReportActivity.this)) {
            DialogCustom.showInternetConnectionMessage(ReportActivity.this);
        } else {
            listdata.clear();
            //loadingDialog.startDialoglog();
            Query queryt = databaseReferenceSell.orderByChild("productId").equalTo(id_value.getSelectedItem().toString().trim());
            queryt.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //loadingDialog.dismisstDialoglog();
                    double totalproductQty = 0;
                    double totalbuyPrice = 0;
                    double totalsellPrice = 0;
                    String productName = "";
                    String productId = "";
                    String color = "";
                    String date = "";
                    String stockflg = "";
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        assert map != null;
                        if (!map.isEmpty() && Objects.equals(map.get("flag"), "Y")) {
                            Log.d("productQty", map.get("productQty").toString());
                            productName = String.valueOf(map.get("productName"));
                            productId = String.valueOf(map.get("productId"));
                            Object productQty = map.get("productQty");
                            Object totalPrice = map.get("totalPrice");
                            stockflg = String.valueOf(map.get("stockflg"));
                            color = String.valueOf(map.get("color"));
                            date = String.valueOf(map.get("date"));
                            try {
                                double productQtyvalue = Double.parseDouble(String.valueOf(productQty));
                                totalproductQty += productQtyvalue;
                                if (stockflg.equals("IN")) {
                                    double totalbuyvalue = Double.parseDouble(String.valueOf(totalPrice));
                                    totalbuyPrice += totalbuyvalue;
                                }
                                if (stockflg.equals("OUT")) {
                                    double totalSellvalue = Double.parseDouble(String.valueOf(totalPrice));
                                    totalsellPrice += totalSellvalue;
                                }


                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            tv_name.setText(productName);
                            tv_deposit_amount.setText(String.valueOf(totalproductQty));


                        } else {

                        }



                       /* Report report = new Report(
                                String.valueOf(productName),
                                String.valueOf(productId),
                                String.valueOf(date),
                                String.valueOf(color),
                                String.valueOf(totalproductQty),
                                String.valueOf(totalbuyPrice),
                                String.valueOf(totalsellPrice));
                        listdata.add(report);*/

                    }
                  /*  reportListAdapter = new ReportListAdapter(ReportActivity.this, listdata, new ReportListAdapter.OnItemClickListener() {
                        @Override
                        public void onContactSelected(Report item) {
                            // DialogCustom.showSuccessMessage(ReportActivity.this, item.getName() + item.getEmail());

                        }
                    });

                    recylerReport.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    recylerReport.setItemAnimator(new DefaultItemAnimator());
                    recylerReport.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));

                    recylerReport.setHasFixedSize(true);
                    recylerReport.setAdapter(reportListAdapter);
                    reportListAdapter.notifyDataSetChanged();*/


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    DialogCustom.showErrorMessage(ReportActivity.this, databaseError.getMessage());
                }
            });
        }


    }

    void getFbddata() {
        loadingDialog.startDialoglog();


        databaseReferenceStock.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    String productName = ds.child("productName").getValue(String.class);
                    String productId = ds.child("productId").getValue(String.class);
                    String date = ds.child("date").getValue(String.class);
                    String color = ds.child("color").getValue(String.class);
                    String productQty = ds.child("productQty").getValue(String.class);
                    String buyPrice = ds.child("buyPrice").getValue(String.class);
                    String sellPrice = ds.child("sellPrice").getValue(String.class);
                    // Log.d("TAG", date + " / "+txn);

                    Report report = new Report(productName, productId, date, color, productQty, buyPrice, sellPrice);


                    // Report report = new Report(member_name, date, amo, invoice, email, nick);
                    listdata.add(report);
                    // Log.e("Data--3", listitem.getTxnid());
                    loadingDialog.dismisstDialoglog();

                }

               /* Collections.sort(listdata, new Comparator<Report>() {
                    @Override
                    public int compare(Report m1, Report m2) {
                        return Integer.compare(ValidationUtil.replacePoet(m1.getNick()), ValidationUtil.replacePoet(m2.getNick()));
                    }

                });*/
                reportListAdapter = new ReportListAdapter(ReportActivity.this, listdata, new ReportListAdapter.OnItemClickListener() {
                    @Override
                    public void onContactSelected(Report item) {
                        //   DialogCustom.showSuccessMessage(ReportActivity.this, item.getName() + item.getEmail());

                    }
                });
                recylerReport.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recylerReport.setItemAnimator(new DefaultItemAnimator());
                recylerReport.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));

                recylerReport.setHasFixedSize(true);
                recylerReport.setAdapter(reportListAdapter);
                reportListAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                DialogCustom.showErrorMessage(ReportActivity.this, databaseError.getMessage());

            }
        });

    }
}