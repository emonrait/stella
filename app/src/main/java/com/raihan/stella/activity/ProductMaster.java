package com.raihan.stella.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
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
import com.raihan.stella.model.Product;
import com.raihan.stella.model.ValidationUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class ProductMaster extends AutoLogout {
    GlobalVariable globalVariable;
    private ImageView ivLogout;
    private ImageView ivBack;
    private TextView tv_genereal_header_title;

    private EditText date_value;
    private EditText product_value;
    private EditText product_id_value;
    private EditText color_value;
    private EditText product_mrp_value;
    private EditText product_percentage_value;
    private Button btnSubmit;
    private TabLayout tabLayout;
    private LinearLayout updateProductLayout;
    private LinearLayout addProductLayout;
    private Spinner id_value;
    final Calendar myCalendar = Calendar.getInstance();

    private EditText updatecolor_value;
    private EditText updatedate_value;
    private EditText updateproduct_value;
    private EditText updateproduct_id_value;
    private EditText updateflag_value;
    private EditText updateproduct_mrp_value;
    private EditText updateproduct_percentage_value;
    private Button btnUpdate;

    ValueEventListener listener;
    ArrayList<String> spinerList;
    ArrayAdapter<String> adapter;


    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceProduct;

    String id = "";
    String productName = "";
    String productId = "";
    String flag = "";
    String color = "";
    String productMrp = "0";
    String productPercent = "0";
    String date = "";

    final LoadingDialog loadingDialog = new LoadingDialog(ProductMaster.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_master);
        globalVariable = ((GlobalVariable) getApplicationContext());

        ivLogout = findViewById(R.id.ivLogout);
        ivBack = findViewById(R.id.ivBack);
        tv_genereal_header_title = findViewById(R.id.tv_genereal_header_title);

        date_value = findViewById(R.id.date_value);
        product_value = findViewById(R.id.product_value);
        product_id_value = findViewById(R.id.product_id_value);
        color_value = findViewById(R.id.color_value);
        btnSubmit = findViewById(R.id.btnSubmit);
        tabLayout = findViewById(R.id.tabLayout);
        updateProductLayout = findViewById(R.id.updateProductLayout);
        addProductLayout = findViewById(R.id.addProductLayout);
        id_value = findViewById(R.id.id_value);
        updatecolor_value = findViewById(R.id.updatecolor_value);
        updatedate_value = findViewById(R.id.updatedate_value);
        updateproduct_value = findViewById(R.id.updateproduct_value);
        updateproduct_id_value = findViewById(R.id.updateproduct_id_value);
        updateflag_value = findViewById(R.id.updateflag_value);
        btnUpdate = findViewById(R.id.btnUpdate);
        product_mrp_value = findViewById(R.id.product_mrp_value);
        product_percentage_value = findViewById(R.id.product_percentage_value);
        updateproduct_mrp_value = findViewById(R.id.updateproduct_mrp_value);
        updateproduct_percentage_value = findViewById(R.id.updateproduct_percentage_value);

        spinerList = new ArrayList<>();


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceProduct = FirebaseDatabase.getInstance().getReference("Product");
        updateProductLayout.setVisibility(View.GONE);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductMaster.this, DashboardActivity.class);
                DialogCustom.doClearActivity(intent, ProductMaster.this);
            }
        });

        tv_genereal_header_title.setText(R.string.master_product);

        ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogCustom.englishcustomLogout(ProductMaster.this);
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
                new DatePickerDialog(ProductMaster.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });


        id_value.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!DialogCustom.isOnline(ProductMaster.this)) {
                    DialogCustom.showInternetConnectionMessage(ProductMaster.this);
                } else {
                    getProductInfo();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (date_value.getText().toString().trim().isEmpty()) {
                    date_value.requestFocus();
                    DialogCustom.showErrorMessage(ProductMaster.this, "Please Enter Date.");

                } else if (product_value.getText().toString().trim().isEmpty()) {
                    product_value.requestFocus();
                    DialogCustom.showErrorMessage(ProductMaster.this, "Please Enter Product Name.");

                } else if (product_id_value.getText().toString().trim().isEmpty()) {
                    product_id_value.requestFocus();
                    DialogCustom.showErrorMessage(ProductMaster.this, "Please Enter Product ID.");

                } else if (color_value.getText().toString().trim().isEmpty()) {
                    color_value.requestFocus();
                    DialogCustom.showErrorMessage(ProductMaster.this, "Please Enter Product Color.");

                } else if (product_mrp_value.getText().toString().trim().isEmpty()) {
                    product_mrp_value.requestFocus();
                    DialogCustom.showErrorMessage(ProductMaster.this, "Please Enter Product MRP value.");

                } else if (product_percentage_value.getText().toString().trim().isEmpty()) {
                    product_percentage_value.requestFocus();
                    DialogCustom.showErrorMessage(ProductMaster.this, "Please Enter Product Percentage value.");

                } else if (!DialogCustom.isOnline(ProductMaster.this)) {
                    DialogCustom.showInternetConnectionMessage(ProductMaster.this);

                } else {
                    String id = databaseReferenceProduct.push().getKey();
                    String date = date_value.getText().toString().trim();
                    String updateBy = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail();
                    String productNmae = product_value.getText().toString().trim();
                    String productId = product_id_value.getText().toString().trim();
                    String color = color_value.getText().toString().trim();
                    String productMrp = product_mrp_value.getText().toString().trim();
                    String productPercent = product_percentage_value.getText().toString().trim();

                    String flag = "Y";
                    Product product = new Product(id, productNmae, productId, date, color, productMrp, productPercent, flag, updateBy);
                    final LoadingDialog loadingDialog = new LoadingDialog(ProductMaster.this);
                    loadingDialog.startDialoglog();
                    try {
                        assert id != null;
                        databaseReferenceProduct.child(id)
                                .setValue(product)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            product_value.setText("");
                                            color_value.setText("");
                                            product_mrp_value.setText("");
                                            product_percentage_value.setText("");
                                            loadingDialog.dismisstDialoglog();
                                            DialogCustom.showSuccessMessage(ProductMaster.this, "Your Product Add Successfully.");


                                        } else {
                                            DialogCustom.showErrorMessage(ProductMaster.this, task.getResult() + "Unsuccessful");
                                            loadingDialog.dismisstDialoglog();

                                        }
                                        loadingDialog.dismisstDialoglog();
                                    }
                                });
                    } catch (Exception e) {
                        DialogCustom.showErrorMessage(ProductMaster.this, e.getMessage());
                    }

                }
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (date_value.getText().toString().trim().isEmpty()) {
                    date_value.requestFocus();
                    DialogCustom.showErrorMessage(ProductMaster.this, "Please Enter Date.");

                } else if (updateproduct_value.getText().toString().trim().isEmpty()) {
                    updateproduct_value.requestFocus();
                    DialogCustom.showErrorMessage(ProductMaster.this, "Please Enter Product Name.");

                } else if (updateproduct_id_value.getText().toString().trim().isEmpty()) {
                    updateproduct_id_value.requestFocus();
                    DialogCustom.showErrorMessage(ProductMaster.this, "Please Enter Product ID.");

                } else if (updatecolor_value.getText().toString().trim().isEmpty()) {
                    updateproduct_id_value.requestFocus();
                    DialogCustom.showErrorMessage(ProductMaster.this, "Please Enter Product Color.");

                } else if (updateflag_value.getText().toString().trim().isEmpty()) {
                    updateflag_value.requestFocus();
                    DialogCustom.showErrorMessage(ProductMaster.this, "Please Enter Product Status.");

                } else if (updateproduct_mrp_value.getText().toString().trim().isEmpty()) {
                    updateproduct_mrp_value.requestFocus();
                    DialogCustom.showErrorMessage(ProductMaster.this, "Please Enter Product MRP Value.");

                } else if (updateproduct_percentage_value.getText().toString().trim().isEmpty()) {
                    updateproduct_percentage_value.requestFocus();
                    DialogCustom.showErrorMessage(ProductMaster.this, "Please Enter Product Percentage Value.");

                } else if (!DialogCustom.isOnline(ProductMaster.this)) {
                    DialogCustom.showInternetConnectionMessage(ProductMaster.this);

                } else {

                    try {
                        loadingDialog.startDialoglog();
                        updateProduct();
                    } catch (Exception e) {
                        DialogCustom.showErrorMessage(ProductMaster.this, e.getMessage());
                        loadingDialog.dismisstDialoglog();
                    }

                }
            }
        });

        tabLayout.addTab(tabLayout.newTab().setText("Add Product"));
        tabLayout.addTab(tabLayout.newTab().setText("Product Update"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    addProductLayout.setVisibility(View.VISIBLE);
                    updateProductLayout.setVisibility(View.GONE);

                } else if (tab.getPosition() == 1) {
                    addProductLayout.setVisibility(View.GONE);
                    updateProductLayout.setVisibility(View.VISIBLE);

                    if (!DialogCustom.isOnline(ProductMaster.this)) {
                        DialogCustom.showInternetConnectionMessage(ProductMaster.this);
                    } else {
                        //spinerList.clear();
                        getProductList();
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


    }

    private void getProductInfo() {
        if (!DialogCustom.isOnline(ProductMaster.this)) {
            DialogCustom.showInternetConnectionMessage(ProductMaster.this);
        } else {
            //loadingDialog.startDialoglog();
            Query queryt = databaseReferenceProduct.orderByChild("id").equalTo(id_value.getSelectedItem().toString().trim());
            queryt.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        id = "" + ds.child("id").getValue();
                        date = "" + ds.child("date").getValue();
                        productName = "" + ds.child("productName").getValue();
                        productId = "" + ds.child("productId").getValue();
                        flag = "" + ds.child("flag").getValue();
                        color = "" + ds.child("color").getValue();
                        if (ds.child("productMrp").getValue() == null || ds.child("productMrp").getValue() == "0") {
                            productMrp = "0";
                        } else {
                            productMrp = "" + ds.child("productMrp").getValue();
                        }
                        if (ds.child("productPercent").getValue() == null || ds.child("productPercent").getValue() == "0") {
                            productPercent = "0";
                        } else {
                            productPercent = "" + ds.child("productPercent").getValue();
                        }

                    }

                    updateproduct_value.setText(productName);
                    updatedate_value.setText(date);
                    updateproduct_id_value.setText(productId);
                    updateflag_value.setText(flag);
                    updatecolor_value.setText(color);
                    updateproduct_mrp_value.setText(productMrp);
                    updateproduct_percentage_value.setText(productPercent);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    DialogCustom.showErrorMessage(ProductMaster.this, databaseError.getMessage());
                }
            });
        }
    }

    private void updateProduct() {
        Query editQuery = FirebaseDatabase.getInstance().getReference("Product").orderByChild("id").equalTo(id);

        editQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot edtData : dataSnapshot.getChildren()) {
                    Log.d("data-->", edtData.toString());
                    edtData.getRef().child("id").setValue(id);
                    edtData.getRef().child("date").setValue(ValidationUtil.getTransactionCurrentDate());
                    edtData.getRef().child("productName").setValue(updateproduct_value.getText().toString().trim());
                    edtData.getRef().child("productId").setValue(updateproduct_id_value.getText().toString().trim());
                    edtData.getRef().child("color").setValue(updatecolor_value.getText().toString().trim());
                    edtData.getRef().child("flag").setValue(updateflag_value.getText().toString().trim());
                    edtData.getRef().child("updateBy").setValue(firebaseAuth.getCurrentUser().getEmail());
                    edtData.getRef().child("productMrp").setValue(updateproduct_mrp_value.getText().toString().trim());
                    edtData.getRef().child("productPercent").setValue(updateproduct_percentage_value.getText().toString().trim());

                }
                Toast.makeText(ProductMaster.this, "Product Information Update Successfully....", Toast.LENGTH_LONG).show();
                loadingDialog.dismisstDialoglog();
                // spinerList.clear();
                getProductList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProductMaster.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                loadingDialog.dismisstDialoglog();
            }
        });


    }

    private void getProductList() {
        //spinerList.clear();
        try {
            spinerList = new ArrayList<>();

            listener = databaseReferenceProduct.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        String id = "" + item.child("id").getValue();
                        spinerList.add(id);

                    }
                    adapter = new ArrayAdapter<String>(ProductMaster.this, android.R.layout.simple_spinner_dropdown_item, spinerList);

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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ProductMaster.this, DashboardActivity.class);
        DialogCustom.doClearActivity(intent, ProductMaster.this);
    }
}