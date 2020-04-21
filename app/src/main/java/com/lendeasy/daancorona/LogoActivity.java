package com.lendeasy.daancorona;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LogoActivity extends AppCompatActivity {

    Button btnStart;
    String access,refresh;
    LoadingDialog dialog;
    @RequiresApi(api = Build.VERSION_CODES.O)


    public void onClick(View view) {
        view.requestPointerCapture();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        SharedPreferences sharedPref=getSharedPreferences("User",MODE_PRIVATE);
        access=sharedPref.getString("Token","");
        refresh=sharedPref.getString("Token1","");

        btnStart = findViewById(R.id.btn_start);
        dialog=new LoadingDialog(this);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.startloadingDialog();

                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .header("Authorization","JWT "+access)
                        .url("https://daancorona.tech/api/auth/")
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {

                        if(access.equals("")){
                            Intent i = new Intent(LogoActivity.this, LanguageSelectActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                        if(response.code()==200 || response.code()==201) {
                            dialog.dismissDialog();
                            Log.d("Ekdum sahi","yayyy");
                            Intent i;
                            if(sharedPref.getString("Lang","").equals(""))
                                i=new Intent(LogoActivity.this,LanguageSelectActivity.class);
                            else if(!sharedPref.getBoolean("Page1",false))
                                i=new Intent(LogoActivity.this,LoginActivity.class);
                            else if(!sharedPref.getBoolean("Page2",false))
                                i=new Intent(LogoActivity.this,ShopInfoActivity.class);
                            else if(!sharedPref.getBoolean("Page3",false))
                                i=new Intent(LogoActivity.this,PaymentModeActivity.class);
                            else
                                i = new Intent(LogoActivity.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        }
                        else {
                            OkHttpClient client1 = new OkHttpClient();

                            RequestBody formbody = new FormBody.Builder()
                                    .addEncoded("refresh", refresh)
                                    .build();

                            Request request = new Request.Builder()
                                    .url("https://daancorona.tech/api/refresh/")
                                    .post(formbody)
                                    .build();

                            client1.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                    dialog.dismissDialog();
                                }

                                @Override
                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                                    if(response.code()==200 || response.code()==201) {
                                        try {

                                            JSONObject jsonObject = new JSONObject(response.body().string());
                                            access = jsonObject.getString("access");
                                            SharedPreferences.Editor editor = sharedPref.edit();

                                            if (access != null) {
                                                editor.putString("Token", access);
                                                editor.apply();

                                                Intent i;
                                                Log.d("Ekdum sahi", "kinda");
                                                if(!sharedPref.getBoolean("Page1",false))
                                                    i=new Intent(LogoActivity.this,LanguageSelectActivity.class);
                                                else if(!sharedPref.getBoolean("Page2",false))
                                                    i=new Intent(LogoActivity.this,ShopInfoActivity.class);
                                                else if(!sharedPref.getBoolean("Page3",false))
                                                    i=new Intent(LogoActivity.this,PaymentModeActivity.class);
                                                else
                                                    i = new Intent(LogoActivity.this, MainActivity.class);

                                                dialog.dismissDialog();
                                                startActivity(i);
                                                finish();
                                            }

                                        } catch (JSONException ex) {

                                            LogoActivity.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    dialog.dismissDialog();
                                                    Toast.makeText(LogoActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                            ex.printStackTrace();

                                        }
                                    }
                                    else{
                                        Log.d("Ekdum sahi","galat");
                                        dialog.dismissDialog();

                                        Intent i = new Intent(LogoActivity.this, LanguageSelectActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                }
                            });
                        }
                    }
                });

            }
        });
    }
}
