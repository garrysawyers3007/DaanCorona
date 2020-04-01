package com.lendeasy.daancorona;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPref=getSharedPreferences("User",MODE_PRIVATE);
        token=sharedPref.getString("Token","");

        name=findViewById(R.id.name);
        maxcredit=findViewById(R.id.target);
        netamt=findViewById(R.id.balance);

        recyclerView=findViewById(R.id.recyclerview);

        new SetProfile().execute();
        new SetRecyclerView().execute();

    }

    class SetProfile extends AsyncTask<Void,Void,String[]>{

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String[] doInBackground(Void... voids) {

            final OkHttpClient httpClient = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("http://www.daancorona.pythonanywhere.com/api/recipient_details/")
                    .addHeader("Authorization","JWT "+token)
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {

                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response);

                JSONObject jsonObject=new JSONObject(response.body().string());
                nametxt=jsonObject.getString("first_name");
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
            }
        }
    }

    class SetRecyclerView extends AsyncTask<Void,Void,JSONArray>{
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected JSONArray doInBackground(Void... voids) {


            final OkHttpClient httpClient = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("http://www.daancorona.pythonanywhere.com/api/recipient_details/")
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
        }
    }
}

