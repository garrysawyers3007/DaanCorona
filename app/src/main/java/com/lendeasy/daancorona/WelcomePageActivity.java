package com.lendeasy.daancorona;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class WelcomePageActivity extends AppCompatActivity {

    Button login,register;
    SharedPreferences sharedPref;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);

        login = findViewById(R.id.login);
        register = findViewById(R.id.register);
        sharedPref=getSharedPreferences("User",MODE_PRIVATE);
        token=sharedPref.getString("Token","");

        if(sharedPref.getString("Lang","").equals("hin")){
            login.setText(getResources().getString(R.string.login));
            register.setText(getResources().getString(R.string.register_new_user));
        }

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(WelcomePageActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(WelcomePageActivity.this, "Feature yet to be added", Toast.LENGTH_SHORT).show();
//                Intent i = new Intent(WelcomePageActivity.this, MainActivity.class);
//                startActivity(i);
            }
        });

    }
}
