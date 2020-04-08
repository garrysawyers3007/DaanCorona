package com.lendeasy.daancorona;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PaymentModeActivity extends AppCompatActivity {

    private Button upi,bank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_mode);

        upi = findViewById(R.id.upi);
        bank = findViewById(R.id.bank);

        upi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentModeActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        bank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentModeActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

    }
}
