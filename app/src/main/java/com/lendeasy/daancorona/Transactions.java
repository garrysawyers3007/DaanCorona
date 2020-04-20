package com.lendeasy.daancorona;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Transactions extends AppCompatActivity {

    RecyclerView recyclerView;
    TransactionAdapter transactionAdapter;
    String access;
    ArrayList<TransactionModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        recyclerView=findViewById(R.id.recyclerview);
        SharedPreferences sharedPref=getSharedPreferences("User",MODE_PRIVATE);
        access=sharedPref.getString("Token","");

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .header("Authorization","JWT "+access)
                .url("https://daancorona.tech/api/recipient_transactions/")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Transactions.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Transactions.this,"Error",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                JSONObject jsonObject;
                list=new ArrayList<>();
                try {
                    jsonObject = new JSONObject(response.body().string());
                    Log.d("resp",response.body()+"");
                    JSONArray jsonArray=jsonObject.getJSONArray("transactions");

                    if (jsonArray != null) {

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            list.add(new TransactionModel(jsonObject1.getString("name"), jsonObject1.getString("amount"),jsonObject1.getInt("type")));
                        }

                        Transactions.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.setLayoutManager(new LinearLayoutManager(Transactions.this));
                                transactionAdapter=new TransactionAdapter(list,Transactions.this);

                                recyclerView.setAdapter(transactionAdapter);
                            }
                        });

                    }

                }catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }
}
