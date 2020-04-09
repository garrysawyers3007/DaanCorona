package com.lendeasy.daancorona;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class LanguageSelectActivity extends AppCompatActivity {

    Button langEng,langHindi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_select);

        langEng = findViewById(R.id.laguage_english);
        langHindi = findViewById(R.id.laguage_hindi);

        langEng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LanguageSelectActivity.this, WelcomePageActivity.class);
                startActivity(i);
            }
        });
        langHindi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LanguageSelectActivity.this, "Feature yet to be added", Toast.LENGTH_SHORT).show();

            }
        });

    }

}
