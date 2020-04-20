package com.lendeasy.daancorona;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MOUActivity extends AppCompatActivity {
    Button btnMou;
    TextView MOU;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mou);
        btnMou = findViewById(R.id.btn_mou_accepted);
        MOU=findViewById(R.id.MOU);

        SharedPreferences sharedPref=getSharedPreferences("User",MODE_PRIVATE);

        if(sharedPref.getString("Lang","").equals("hin")){
            btnMou.setText(getResources().getString(R.string.agree));
            MOU.setText(getResources().getString(R.string.mou));
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
