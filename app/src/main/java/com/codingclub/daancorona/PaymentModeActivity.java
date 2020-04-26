package com.codingclub.daancorona;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class PaymentModeActivity extends AppCompatActivity {

    private Button upi,bank;
    SharedPreferences sharedPref;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_mode);

        upi = findViewById(R.id.upi);
        bank = findViewById(R.id.bank);
        sharedPref=getSharedPreferences("User",MODE_PRIVATE);
        token=sharedPref.getString("Token","");

        if(sharedPref.getString("Lang","").equals("hin")){
            upi.setText(getResources().getString(R.string.accept_UPI));
            bank.setText(getResources().getString(R.string.accept_bank));
        }

        upi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentModeActivity.this, UPIDetailsActivity.class);
                startActivity(intent);
            }
        });

        bank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentModeActivity.this, BankDetailsActvity.class);
                startActivity(intent);
            }
        });

    }
}
