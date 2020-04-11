package com.lendeasy.daancorona;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ItemAdapter itemAdapter;
    String nametxt,net,maxcred;
    TextView name,maxcredit,netamt;
    String token;
    ImageView edit;
    SharedPreferences sharedPref;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        sharedPref = getSharedPreferences("User",MODE_PRIVATE);
        token=sharedPref.getString("Token","");

        name=findViewById(R.id.name);
        maxcredit=findViewById(R.id.target);
        netamt=findViewById(R.id.balance);
        edit=findViewById(R.id.edit);

        mSwipeRefreshLayout=findViewById(R.id.swiperefresh_items);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new SetProfile().execute();
                new SetRecyclerView().execute();
            }
        });

        recyclerView=findViewById(R.id.recyclerview);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,EditProfile.class));
            }
        });

        new SetProfile().execute();
        new SetRecyclerView().execute();

    }


    class SetProfile extends AsyncTask<Void,Void,String[]>{

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String[] doInBackground(Void... voids) {

            final OkHttpClient httpClient = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("http://daancorona.herokuapp.com/api/recipient_details/")
                    .addHeader("Authorization","JWT "+token)
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {

                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response);

                JSONObject jsonObject=new JSONObject(response.body().string());
                nametxt=jsonObject.getString("name");
                net=jsonObject.getString("total_amt");
                maxcred=jsonObject.getString("max_credit");

                Log.d("Values",nametxt+net+maxcred+"");
                return new String[]{nametxt,net,maxcred};
                // Get response body

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String... s) {

            super.onPostExecute(s);
            if(s!=null){
                name.setText(s[0]);
                netamt.setText(s[1]);
                maxcredit.setText(s[2]);
               // if(sharedPref.getString("Lang","").equals("hin")){
//                    name.setText(TranslateTo.getTranslation(name.getText().toString(),MainActivity.this));
//                    maxcredit.setText(TranslateTo.getTranslation(maxcredit.getText().toString(),MainActivity.this));
//                    netamt.setText(TranslateTo.getTranslation(netamt.getText().toString(),MainActivity.this));
                //}
            }
            if(mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    class SetRecyclerView extends AsyncTask<Void,Void,JSONArray>{
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected JSONArray doInBackground(Void... voids) {


            final OkHttpClient httpClient = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("http://daancorona.herokuapp.com/api/recipient_details/")
                    .addHeader("Authorization", "JWT "+token)
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {

                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response);

                JSONObject jsonObject=new JSONObject(response.body().string());

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
                        list.add(new Item(jsonObject.getString("name"),jsonObject.getString("donor_id"),jsonObject.getString("amount")));
                    }

                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    itemAdapter=new ItemAdapter(list,MainActivity.this);

                    recyclerView.setAdapter(itemAdapter);
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }
            if(mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }
}

