package com.raihan.stella.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.raihan.stella.R;
import com.raihan.stella.model.AutoLogout;
import com.raihan.stella.model.DialogCustom;
import com.raihan.stella.model.GlobalVariable;
import com.raihan.stella.model.Product;
import com.raihan.stella.model.Transaction;
import com.raihan.stella.model.ValidationUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class Collection extends AutoLogout {
    GlobalVariable globalVariable;
    private ImageView ivLogout;
    private ImageView ivBack;
    private TextView tv_genereal_header_title;

    private EditText date_value;
    private EditText customer_name_value;
    private EditText customer_mobile_novalue;
    private EditText remarks_value;
    private EditText amount_value;
    private Button btnSubmit;

    final Calendar myCalendar = Calendar.getInstance();


    ValueEventListener listener;
    ArrayList<String> spinerList;
    ArrayAdapter<String> adapter;


    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceTransaction;

    String id = "";
    String productName = "";
    String productId = "";
    String flag = "";
    String color = "";
    String productMrp = "0";
    String productPercent = "0";
    String date = "";

    final LoadingDialog loadingDialog = new LoadingDialog(Collection.this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        globalVariable = ((GlobalVariable) getApplicationContext());

        ivLogout = findViewById(R.id.ivLogout);
        ivBack = findViewById(R.id.ivBack);
        tv_genereal_header_title = findViewById(R.id.tv_genereal_header_title);

        date_value = findViewById(R.id.date_value);
        customer_name_value = findViewById(R.id.customer_name_value);
        customer_mobile_novalue = findViewById(R.id.customer_mobile_novalue);
        remarks_value = findViewById(R.id.remarks_value);
        amount_value = findViewById(R.id.amount_value);
        btnSubmit = findViewById(R.id.btnSubmit);


        spinerList = new ArrayList<>();


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceTransaction = FirebaseDatabase.getInstance().getReference("Transaction");
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Collection.this, DashboardActivity.class);
                DialogCustom.doClearActivity(intent, Collection.this);
            }
        });

        tv_genereal_header_title.setText(R.string.master_product);

        ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogCustom.englishcustomLogout(Collection.this);
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
                new DatePickerDialog(Collection.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (date_value.getText().toString().trim().isEmpty()) {
                    date_value.requestFocus();
                    DialogCustom.showErrorMessage(Collection.this, "Please Enter Date.");

                } else if (customer_name_value.getText().toString().trim().isEmpty()) {
                    customer_name_value.requestFocus();
                    DialogCustom.showErrorMessage(Collection.this, "Please Enter Customer Name.");

                } else if (customer_mobile_novalue.getText().toString().trim().isEmpty()) {
                    customer_mobile_novalue.requestFocus();
                    DialogCustom.showErrorMessage(Collection.this, "Please Enter Customer Mobile No.");

                } else if (amount_value.getText().toString().trim().isEmpty()) {
                    amount_value.requestFocus();
                    DialogCustom.showErrorMessage(Collection.this, "Please Enter Amount.");

                } else if (amount_value.getText().toString().trim().equals("0")) {
                    amount_value.requestFocus();
                    DialogCustom.showErrorMessage(Collection.this, "Please Enter Valid Amount.");

                } else if (remarks_value.getText().toString().trim().isEmpty()) {
                    remarks_value.requestFocus();
                    DialogCustom.showErrorMessage(Collection.this, "Please Enter Remarks.");

                } else if (!DialogCustom.isOnline(Collection.this)) {
                    DialogCustom.showInternetConnectionMessage(Collection.this);

                } else {
                    String id = databaseReferenceTransaction.push().getKey();
                    String date = date_value.getText().toString().trim();
                    String customerName = customer_name_value.getText().toString().trim();
                    String customerMobile = customer_mobile_novalue.getText().toString().trim();
                    String amount = amount_value.getText().toString().trim();
                    String remarks = remarks_value.getText().toString().trim();
                    String flag = "Y";
                    String updateBy = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail();

                    Transaction transaction = new Transaction(id, date, customerName, customerMobile, amount, remarks, flag, updateBy);
                    final LoadingDialog loadingDialog = new LoadingDialog(Collection.this);
                    loadingDialog.startDialoglog();
                    try {
                        assert id != null;
                        databaseReferenceTransaction.child(id)
                                .setValue(transaction)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            customer_name_value.setText("");
                                            customer_mobile_novalue.setText("");
                                            amount_value.setText("");
                                            remarks_value.setText("");
                                            loadingDialog.dismisstDialoglog();
                                            DialogCustom.showSuccessMessage(Collection.this, "Your Collection Amount Add Successfully.");


                                        } else {
                                            DialogCustom.showErrorMessage(Collection.this, task.getResult() + "Unsuccessful");
                                            loadingDialog.dismisstDialoglog();

                                        }
                                        loadingDialog.dismisstDialoglog();
                                    }
                                });
                    } catch (Exception e) {
                        DialogCustom.showErrorMessage(Collection.this, e.getMessage());
                    }

                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Collection.this, DashboardActivity.class);
        DialogCustom.doClearActivity(intent, Collection.this);
    }
}