package com.lendeasy.daancorona;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

public class Contact extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        TextView contact;
        contact=findViewById(R.id.contact);
        SharedPreferences sharedPref=getSharedPreferences("User",MODE_PRIVATE);
        if(sharedPref.getString("Lang","").equals("hin")){
            contact.setText("संपर्क करें");
        }
    }
}
