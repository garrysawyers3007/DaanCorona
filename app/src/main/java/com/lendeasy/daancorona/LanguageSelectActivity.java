package com.lendeasy.daancorona;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.view.View;
import android.widget.RelativeLayout;

public class LanguageSelectActivity extends AppCompatActivity {

    Button langEng,langHindi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_select);

        langEng = findViewById(R.id.laguage_english);
        langHindi = findViewById(R.id.laguage_hindi);


        SharedPreferences sharedPref=getSharedPreferences("User",MODE_PRIVATE);
        if(!sharedPref.getString("Lang","").equals("")){
            startActivity(new Intent(LanguageSelectActivity.this,LoginActivity.class));
            finish();
        }

        SharedPreferences.Editor editor=sharedPref.edit();

        langEng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("Lang","eng");
                editor.apply();
                Intent i = new Intent(LanguageSelectActivity.this, WelcomePageActivity.class);
                startActivity(i);
                finish();
            }
        });
        langHindi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                editor.putString("Lang","hin");
//                editor.apply();
                Toast.makeText(LanguageSelectActivity.this, "Feature yet to be added", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(LanguageSelectActivity.this,LoginActivity.class));
//                finish();

            }
        });

    }

}
