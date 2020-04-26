package com.codingclub.daancorona;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MOUActivity extends AppCompatActivity {
    Button btnMou;
    TextView MOU;
    SharedPreferences sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mou);
        btnMou = findViewById(R.id.btn_mou_accepted);
        MOU=findViewById(R.id.MOU);
        Date c = Calendar.getInstance().getTime();
        sharedPref = getSharedPreferences("User", MODE_PRIVATE);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c);

        SharedPreferences sharedPref=getSharedPreferences("User",MODE_PRIVATE);

        if(sharedPref.getString("Lang","").equals("hin")){
            btnMou.setText(getResources().getString(R.string.agree));
            MOU.setText(getResources().getString(R.string.HinMou1)+" "+sharedPref.getString("shopName","")+" "+
                    getResources().getString(R.string.HinMou2)+" "+formattedDate+" "+
                    getResources().getString(R.string.HinMou3)+" "+formattedDate+"\n"
                    +"हस्ताक्षर :\n"+sharedPref.getString("Name","")+"\n"
                    +sharedPref.getString("shopName","")+"\n\n"+
                    "DaanCorona");
        }
        else{
            MOU.setText(getResources().getString(R.string.mouDetails1)+" "+sharedPref.getString("shopName","")+" "+
                            getResources().getString(R.string.mouDetails2)+" "+formattedDate+" "+
                    getResources().getString(R.string.mouDetails3)+" "+formattedDate+"\n"
            +"Signed by:\n"+sharedPref.getString("Name","")+"\n"
            +sharedPref.getString("shopName","")+"\n\n"+
                    "DaanCorona");
        }

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
