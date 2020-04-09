package com.lendeasy.daancorona;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class BankDetailsActvity extends AppCompatActivity {

    Button btnproceedBnk;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_details_actvity);

        btnproceedBnk = findViewById(R.id.proceed_bank_details);
        btnproceedBnk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(BankDetailsActvity.this, MOUActivity.class);
                startActivity(i);
            }
        });
    }
}
