package com.lendeasy.daancorona;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LogoActivity extends AppCompatActivity {

    Button btnStart;
    @RequiresApi(api = Build.VERSION_CODES.O)


    public void onClick(View view) {
        view.requestPointerCapture();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        btnStart = findViewById(R.id.btn_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(LogoActivity.this, LanguageSelectActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}
