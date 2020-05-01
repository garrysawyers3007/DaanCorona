package com.codingclub.daancorona;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

import static java.security.AccessController.getContext;

public class LogoActivity extends AppCompatActivity {

    private static final int MY_GALLERY_REQUEST_CODE = 1;
    Button btnStart;
    String access,refresh,VERSION_APP="1.1";
    LoadingDialog dialog;
    SharedPreferences sharedPref;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);
//
//        AppUpdater appUpdater = new AppUpdater(this)
//                .setUpdateFrom();
//        appUpdater.start();

        sharedPref = getSharedPreferences("User",MODE_PRIVATE);
        access=sharedPref.getString("Token","");
        refresh=sharedPref.getString("Token1","");//Update this for every commit

        btnStart = findViewById(R.id.btn_start);
        dialog=new LoadingDialog(this);

        checkPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}[0], MY_GALLERY_REQUEST_CODE);
        //updateapk();

        //checkToken();
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

    private void checkToken(){

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

                            }
                        }
                    });
                }
            }
        });
    }

    private void updateapk(){
        OkHttpClient client = new OkHttpClient();
        dialog.startloadingDialog();
        Request request = new Request.Builder()
                .url("https://daancorona.tech/api/app_update/")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                LogoActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismissDialog();
                        Toast.makeText(LogoActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    JSONObject jsonObject=new JSONObject(response.body().string());
                    if(VERSION_APP.equals(jsonObject.getString("version")))
                        checkToken();
                    else {
                        downloadapk();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    LogoActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismissDialog();
                            Toast.makeText(LogoActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void downloadapk(){

        LogoActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {


                String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
                String fileName = "DaanCorona.apk";
                destination += fileName;

                Log.d("file",destination);

                //Delete update file if exists
                File file = new File(destination);
                if (file.exists())
                    file.delete(); //- test this, I think sometimes it doesnt work

                uri=Uri.fromFile(file);

                String url="https://daancorona.tech/download/DaanCorona.apk";
                //get url of app on server
                final String dest=destination;

                //set downloadmanager
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setDescription("Update the app");
                request.setTitle("App Update");

                if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                    uri=FileProvider.getUriForFile(LogoActivity.this,BuildConfig.APPLICATION_ID +".provider",file);
                }
                //set destination
                Log.d("Uri",uri.toString());
                //request.setDestinationUri(uri);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"DaanCorona.apk");

                // get download service and enqueue file
                final DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                final long downloadId = manager.enqueue(request);

                //set BroadcastReceiver to install app when .apk is downloaded
                BroadcastReceiver onComplete = new BroadcastReceiver() {
                    public void onReceive(Context ctxt, Intent intent) {
                        dialog.dismissDialog();
                        Intent install = new Intent(Intent.ACTION_VIEW);
                        install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        else
                            install.setDataAndType(uri,manager.getMimeTypeForDownloadedFile(downloadId));

                        install.setData(uri);
                        startActivity(install);
                        Log.d("Yaay","Mofos!!!");
                        unregisterReceiver(this);
                        finish();

                    }
                };
                //register receiver for when .apk download is compete
                registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            }
        });

    }

    public void checkPermission(String permission, int requestCode)
    {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(
                this,
                permission)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat
                    .requestPermissions(
                            this,
                            new String[] { permission },
                            requestCode);
        }
        else{
            updateapk();
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,
                        "Location Permission Granted",
                        Toast.LENGTH_SHORT)
                        .show();
                updateapk();

            }
            else {
                Toast.makeText(this,
                        "Location Permission Denied",
                        Toast.LENGTH_SHORT)
                        .show();
                finishAffinity();
            }

    }
}
