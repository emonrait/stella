package com.raihan.stella.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.raihan.stella.R;
import com.raihan.stella.model.AutoLogout;
import com.raihan.stella.model.DialogCustom;
import com.raihan.stella.model.GlobalVariable;
import com.raihan.stella.model.LogoutService;

import androidx.annotation.RequiresApi;

public class ContactActivity extends AutoLogout {

    private ImageView ivLogout;
    private ImageView ivBack;
    private TextView tv_genereal_header_title;
    GlobalVariable globalVariable;

    @SuppressLint("RestrictedApi")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        //getSupportActionBar().setTitle("Contact");

        globalVariable = ((GlobalVariable) getApplicationContext());
        ivLogout = findViewById(R.id.ivLogout);
        ivBack = findViewById(R.id.ivBack);
        tv_genereal_header_title = findViewById(R.id.tv_genereal_header_title);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactActivity.this, DashboardActivity.class);
                DialogCustom.doClearActivity(intent, ContactActivity.this);
            }
        });

        tv_genereal_header_title.setText("Contact Information");

        ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogCustom.englishcustomLogout(ContactActivity.this);
            }
        });

        LogoutService.logout(this);
    }

}

