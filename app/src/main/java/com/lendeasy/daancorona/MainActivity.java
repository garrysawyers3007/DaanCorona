package com.lendeasy.daancorona;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ItemAdapter itemAdapter;
    TextView name,maxcredit,netamt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name=findViewById(R.id.name);
        maxcredit=findViewById(R.id.target);
        netamt=findViewById(R.id.balance);


        recyclerView=findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemAdapter=new ItemAdapter();

        recyclerView.setAdapter(itemAdapter);

    }

    class SetProfile extends AsyncTask<Void,Void,String[]>{

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String[] doInBackground(Void... voids) {

            final OkHttpClient httpClient = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://www.daancorona.pythonanywhere.com/api/recipient_profile/")
                    .addHeader("Authentication", "token")
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {

                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response);
                // Get response body

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] s) {

            super.onPostExecute(s);
        }
    }
}
