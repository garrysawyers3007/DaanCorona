package com.lendeasy.daancorona;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    EditText phone,otp;
    Button sendotp,verifyotp;
    String codeSent,code,phoneNumber,url="localhost:3000";
    FirebaseAuth mAuth=FirebaseAuth.getInstance();
    boolean newuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        phone=findViewById(R.id.phone);
        otp=findViewById(R.id.otp);

        sendotp=findViewById(R.id.sendotp);
        verifyotp=findViewById(R.id.verifyotp);

        otp.setVisibility(View.GONE);
        verifyotp.setVisibility(View.GONE);

        sendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                phoneNumber=phone.getText().toString();
//                phoneNumber=phoneNumber.trim();
                if(phoneNumber.length()==13)
                    new GetOtpTask().execute(phoneNumber);
                else
                    Toast.makeText(getApplicationContext(),"Invalid phone number",Toast.LENGTH_SHORT).show();
            }
        });

        verifyotp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                code=otp.getText().toString();
                    new VerifyOtpTask().execute(phoneNumber,code);
                //verifySignIn();
            }
        });
    }
    class GetOtpTask extends AsyncTask<String,Void,String>{

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... strings) {

             final OkHttpClient httpClient = new OkHttpClient();
             Log.d("Ph No.",strings[0]);
                RequestBody formbody=new FormBody.Builder()
                        .addEncoded("mobile",strings[0])
                        .build();

                Request request = new Request.Builder()
                        .url("http://daancorona.pythonanywhere.com/api/mobile/")
                        .post(formbody)
                        .build();

            try (Response response = httpClient.newCall(request).execute()) {

                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response);

                Log.d("Tag",response.body()+"");

//                JSONObject jsonObject=new JSONObject(response.body().string());
//                codeSent= jsonObject.getString("otp");

                return codeSent;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            //Toast.makeText(getApplicationContext(),"code:"+s,Toast.LENGTH_LONG).show();
            super.onPostExecute(s);

            otp.setVisibility(View.VISIBLE);
            verifyotp.setVisibility(View.VISIBLE);

            phone.setVisibility(View.GONE);
            sendotp.setVisibility(View.GONE);
        }
    }


    class VerifyOtpTask extends AsyncTask<String,Void,String>{

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... strings) {

            String access="",refresh="";

            final OkHttpClient httpClient = new OkHttpClient();

            RequestBody formbody=new FormBody.Builder()
                    .addEncoded("mobile",strings[0])
                    .addEncoded("token",strings[1])
                    .build();

            Request request = new Request.Builder()
                    .url("http://daancorona.pythonanywhere.com/api/otp/")
                    .post(formbody)
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {

                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response);

                Log.d("Tag",response.body()+"");

                JSONObject jsonObject=new JSONObject(response.body().string());
                JSONObject jsonObject1=jsonObject.getJSONObject("token");

                access=jsonObject1.getString("access");
                refresh=jsonObject1.getString("refresh");

                newuser=jsonObject.getBoolean("newUser");
                Log.d("NewUser",newuser+"");

            } catch (IOException | JSONException e) {
                access=null;
                e.printStackTrace();
            }

            return access;
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);

            if(s==null || s.equals("")) {
                Toast.makeText(LoginActivity.this, "Error!!!", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences sharedPref=getSharedPreferences("User",MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPref.edit();
            editor.putString("Token",s);
            editor.apply();
            Toast.makeText(getApplicationContext(), "Token: "+s, Toast.LENGTH_SHORT).show();

            if(!newuser) {
                Intent i = new Intent(LoginActivity.this, InfoActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
            else{
                Intent i = new Intent(LoginActivity.this, InfoActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }

        }
    }

}
