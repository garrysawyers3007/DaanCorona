package com.lendeasy.daancorona;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BankDetailsActvity extends AppCompatActivity {

    Button btnproceedBnk;
    EditText acc,ifsc;
    String acc_no,ifsc_no;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_details_actvity);

        acc=findViewById(R.id.acc_no);
        ifsc=findViewById(R.id.ifsc);

        btnproceedBnk = findViewById(R.id.proceed_bank_details);
        btnproceedBnk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sharedPref=getSharedPreferences("User",MODE_PRIVATE);
                String token=sharedPref.getString("Token","");

                acc_no=acc.getText().toString();
                ifsc_no=ifsc.getText().toString();

                if(acc_no.isEmpty() | ifsc_no.isEmpty())
                    Toast.makeText(BankDetailsActvity.this,"Enter Details",Toast.LENGTH_SHORT).show();
                else{
                    final OkHttpClient client = new OkHttpClient();

                    RequestBody formBody = new FormBody.Builder()
                            .addEncoded("account_no", acc_no)
                            .addEncoded("ifsc_code",ifsc_no)
                            .build();


                    Request request = new Request.Builder()
                            .url("http://daancorona.herokuapp.com/api/recipient_profile/")
                            .addHeader("Authorization", "JWT " + token)
                            .post(formBody)
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            BankDetailsActvity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(BankDetailsActvity.this,"Error!",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) {

                            BankDetailsActvity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(BankDetailsActvity.this,"Success!",Toast.LENGTH_SHORT).show();
                                }
                            });

                            SharedPreferences.Editor editor=sharedPref.edit();
                            editor.putBoolean("Page3",true);
                            editor.apply();

                            Intent i = new Intent(BankDetailsActvity.this, MOUActivity.class);
                            startActivity(i);
                            finish();
                        }
                    });

                }

            }
        });
    }
}
