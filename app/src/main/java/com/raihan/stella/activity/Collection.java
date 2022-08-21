package com.raihan.stella.activity;

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

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.raihan.stella.R;
import com.raihan.stella.model.AutoLogout;
import com.raihan.stella.model.DialogCustom;
import com.raihan.stella.model.GlobalVariable;
import com.raihan.stella.model.ValidationUtil;

import java.util.ArrayList;
import java.util.Calendar;

public class Collection extends AutoLogout {
    GlobalVariable globalVariable;
    private ImageView ivLogout;
    private ImageView ivBack;
    private TextView tv_genereal_header_title;

    private EditText date_value;

    final Calendar myCalendar = Calendar.getInstance();


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


        spinerList = new ArrayList<>();


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceProduct = FirebaseDatabase.getInstance().getReference("Product");
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
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Collection.this, DashboardActivity.class);
        DialogCustom.doClearActivity(intent, Collection.this);
    }
}