package com.lendeasy.daancorona;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MOUActivity extends AppCompatActivity {
    Button btnMou;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mou);
        btnMou = findViewById(R.id.btn_mou_accepted);
        btnMou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MOUActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}
