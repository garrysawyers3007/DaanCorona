package com.lendeasy.daancorona;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class UPIDetailsActivity extends AppCompatActivity {
    Button btnproceedUpi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u_p_i_details);

        btnproceedUpi = findViewById(R.id.proceed_upi_details);
        btnproceedUpi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UPIDetailsActivity.this, MOUActivity.class);
                startActivity(i);
            }
        });
    }
}
