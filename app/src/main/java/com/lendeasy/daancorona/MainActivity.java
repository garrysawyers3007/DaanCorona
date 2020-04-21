package com.lendeasy.daancorona;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private Button btnDownload;
    String TnC_URL = "https://docs.google.com/document/u/0/export?format=pdf&id=1iZAIG22IW48KSJjEUEsulrv8JPCkFt0UAsCw5vI151Y&token=AC4w5VhKtYN9MZjeZ8jN43QLxy-nNS69ZQ%3A1587457520168&includes_info_params=true";
    RecyclerView recyclerView;
    ItemAdapter itemAdapter;
    String nametxt, net, maxcred;
    TextView name, maxcredit, netamt, maxcredittxt, netamttxt, donation,nodonation;
    String token;
    ImageView edit;
    Button transaction;
    SharedPreferences sharedPref;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnDownload = (Button) findViewById(R.id.btn_download);
        //btnDownload = (Button) findViewById(R.id.btnDownload);
        btnDownload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(TnC_URL); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                Toast.makeText(MainActivity.this, "Downloading DaanCorona TnC...", Toast.LENGTH_LONG).show();
            }
        });

        sharedPref = getSharedPreferences("User", MODE_PRIVATE);
        token = sharedPref.getString("Token", "");

        name = findViewById(R.id.name);
        maxcredittxt = findViewById(R.id.tgttext);
        netamttxt = findViewById(R.id.blctext);
        donation = findViewById(R.id.text);
        transaction = findViewById(R.id.transc);
        nodonation=findViewById(R.id.nodon);

        maxcredit = findViewById(R.id.target);
        netamt = findViewById(R.id.balance);
        edit = findViewById(R.id.edit);

        if (sharedPref.getString("Lang", "").equals("hin")) {
            maxcredittxt.setText(getResources().getString(R.string.maxcredit));
            netamttxt.setText(getResources().getString(R.string.netamt));
            donation.setText(getResources().getString(R.string.donations));
            transaction.setText(getResources().getString(R.string.transactions));
        }

        mSwipeRefreshLayout = findViewById(R.id.swiperefresh_items);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new SetProfile().execute();
                new SetRecyclerView().execute();
            }
        });

        recyclerView = findViewById(R.id.recyclerview);
        transaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Transactions.class));
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, EditProfile.class));
            }
        });

        new SetProfile().execute();
        new SetRecyclerView().execute();

    }


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.alert)
                .setTitle("Alert")
                .setMessage("Sure to exit DaanCorona ???")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent a = new Intent(Intent.ACTION_MAIN);
                        a.addCategory(Intent.CATEGORY_HOME);
                        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(a);
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }


    class SetProfile extends AsyncTask<Void, Void, String[]> {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String[] doInBackground(Void... voids) {

            final OkHttpClient httpClient = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://daancorona.tech/api/recipient_details/")
                    .addHeader("Authorization", "JWT " + token)
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {

                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response);

                JSONObject jsonObject = new JSONObject(response.body().string());
                nametxt = jsonObject.getString("name");
                net = jsonObject.getString("total_amt");
                maxcred = jsonObject.getString("max_credit");

                Log.d("Values", nametxt + net + maxcred + "");
                return new String[]{nametxt, net, maxcred};
                // Get response body

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String... s) {

            super.onPostExecute(s);
            if (s != null) {
                name.setText(s[0]);
                netamt.setText(s[1]);
                maxcredit.setText(s[2]);
                if (sharedPref.getString("Lang", "").equals("hin")) {
                    name.setText(name.getText().toString());
                    maxcredit.setText(TranslateTo.getTranslation(maxcredit.getText().toString(), MainActivity.this));
                    netamt.setText(TranslateTo.getTranslation(netamt.getText().toString(), MainActivity.this));
                }
            }
            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    class SetRecyclerView extends AsyncTask<Void, Void, JSONArray> {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected JSONArray doInBackground(Void... voids) {


            final OkHttpClient httpClient = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://daancorona.tech/api/recipient_details/")
                    .addHeader("Authorization", "JWT " + token)
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {

                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response);

                JSONObject jsonObject = new JSONObject(response.body().string());

                return jsonObject.getJSONArray("donors");

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);

            try {
                ArrayList<Item> list = new ArrayList<>();
                if (jsonArray != null) {

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        list.add(new Item(jsonObject.getString("name"), jsonObject.getString("amount")));
                    }

                    if(jsonArray.length()==0){
                        if(sharedPref.getString("Lang","").equals("hin"))
                            nodonation.setText(getResources().getString(R.string.nodon));
                        recyclerView.setVisibility(View.GONE);
                        nodonation.setVisibility(View.VISIBLE);
                    }
                    else {
                        recyclerView.setVisibility(View.VISIBLE);
                        nodonation.setVisibility(View.GONE);
                        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                        itemAdapter = new ItemAdapter(list, MainActivity.this);

                        recyclerView.setAdapter(itemAdapter);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }
}

