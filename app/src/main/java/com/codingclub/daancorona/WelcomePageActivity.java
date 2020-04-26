package com.codingclub.daancorona;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class WelcomePageActivity extends AppCompatActivity {

    Button register;
    TextView txtQuote;
    SharedPreferences sharedPref;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);

       register = findViewById(R.id.register);
       txtQuote = findViewById(R.id.quote);
        sharedPref=getSharedPreferences("User",MODE_PRIVATE);
        token=sharedPref.getString("Token","");

        if(sharedPref.getString("Lang","").equals("hin")){
            register.setText(getResources().getString(R.string.register_new_user));
            txtQuote.setText(getResources().getString(R.string.quote));
        }

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(WelcomePageActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

    }
}
