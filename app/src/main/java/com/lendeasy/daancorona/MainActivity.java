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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;

    RecyclerView recyclerView;
    ItemAdapter itemAdapter;
    String nametxt, net, maxcred;
    TextView name, maxcredit, netamt, maxcredittxt, netamttxt, donation,nodonation;
    String token;
    Button transaction;
    SharedPreferences sharedPref;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private DrawerLayout dl;
    private NavigationView nv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        dl = (DrawerLayout)findViewById(R.id.dl);
        setSupportActionBar(toolbar);


        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        nv = (NavigationView)findViewById(R.id.nv);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id)
                {
                    case R.id.edit_profile:
                        item.setChecked(true);
                        startActivity(new Intent(MainActivity.this,EditProfile.class));
                        dl.closeDrawers();
                        break;
                    case R.id.contact_us:
                        item.setChecked(true);
                        startActivity(new Intent(MainActivity.this,Contact.class));
                        dl.closeDrawers();
                        break;
                    case R.id.info:
                        item.setChecked(true);
                        startActivity(new Intent(MainActivity.this,InfoNew.class));
                        dl.closeDrawers();
                        break;
                    case R.id.terms:
                        item.setChecked(true);
                        startActivity(new Intent(MainActivity.this,PDFActivity.class));
                        dl.closeDrawers();
                        break;
                    default:
                        return true;
                }
                return true;

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

        if (sharedPref.getString("Lang", "").equals("hin")) {
            maxcredittxt.setText("अधिकतम क्रेडिट");
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

        new SetProfile().execute();
        new SetRecyclerView().execute();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                dl.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
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
                        finishAffinity();
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
                        list.add(new Item(jsonObject.getString("name"), jsonObject.getString("amount"), jsonObject.getString("donor_id")));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}

