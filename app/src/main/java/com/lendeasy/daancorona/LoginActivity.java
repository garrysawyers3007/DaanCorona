package com.lendeasy.daancorona;

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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    EditText editTxtPhone, editTxtOtp;
    Button btnSendotp, btnVerifyOtp;
    LoadingDialog dialog;
    OTPDialog otpDialog;
  //  TextView textOtp,textPhone;
    String codeSent,code,phoneNumber,url="localhost:3000";
    boolean newuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

       // textPhone = findViewById(R.id.txt_phone);
        editTxtPhone =findViewById(R.id.editTxt_phone);
        btnSendotp = findViewById(R.id.btn_send_otp);


      //  textOtp = findViewById(R.id.txt_otp);
        editTxtOtp = findViewById(R.id.edit_txt_otp);
        btnVerifyOtp = findViewById(R.id.btn_verify_otp);

        editTxtOtp.setVisibility(View.GONE);
        btnVerifyOtp.setVisibility(View.GONE);
       // textOtp.setVisibility(View.GONE);

        dialog=new LoadingDialog(this);

        btnSendotp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {



                phoneNumber = "+91" + editTxtPhone.getText().toString().trim();
                if(phoneNumber.length()==13) {
                    dialog.startloadingDialog();
                    new GetOtpTask().execute(phoneNumber);
                }
                else
                    Toast.makeText(getApplicationContext(),"Invalid phone number",Toast.LENGTH_SHORT).show();
            }
        });

        btnVerifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.startloadingDialog();
                code= editTxtOtp.getText().toString();
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
                        .url("http://daancorona.herokuapp.com/api/mobile/")
                        .post(formbody)
                        .build();

            try (Response response = httpClient.newCall(request).execute()) {

                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response);
                JSONObject jsonObject=new JSONObject(response.body().string());

                codeSent=jsonObject.getString("otp");

                Log.d("Tag",response.body()+"");

//                JSONObject jsonObject=new JSONObject(response.body().string());
//                codeSent= jsonObject.getString("otp");

                return codeSent;

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            //Toast.makeText(getApplicationContext(),"code:"+s,Toast.LENGTH_LONG).show();
            super.onPostExecute(s);
            dialog.dismissDialog();

            if(!s.equals(null)){
                otpDialog=new OTPDialog(s,LoginActivity.this);
                otpDialog.setCancelable(true);
                otpDialog.show();

            }
            else
                Toast.makeText(LoginActivity.this,"Error",Toast.LENGTH_SHORT).show();

            editTxtOtp.setVisibility(View.VISIBLE);
            btnVerifyOtp.setVisibility(View.VISIBLE);
           // textOtp.setVisibility(View.VISIBLE);
            editTxtPhone.setText("");
            btnSendotp.setVisibility(View.GONE);
            editTxtPhone.setVisibility(View.GONE);
           // textPhone.setVisibility(View.GONE);
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
                    .url("http://daancorona.herokuapp.com/api/otp/")
                    .post(formbody)
                    .build();

            try (okhttp3.Response response = httpClient.newCall(request).execute()) {

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
            dialog.dismissDialog();

            if(s==null || s.equals("")) {
                Toast.makeText(LoginActivity.this, "Error!!!", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences sharedPref=getSharedPreferences("User",MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPref.edit();
            editor.putString("Token",s);
            editor.apply();
//            Toast.makeText(getApplicationContext(), "Token: "+s, Toast.LENGTH_SHORT).show();

            if(!newuser) {

                Intent i;

                if(!sharedPref.getBoolean("Page1",false))
                    i=new Intent(LoginActivity.this,PersonalInfoActivity.class);
                else if(!sharedPref.getBoolean("Page2",false))
                    i=new Intent(LoginActivity.this,ShopInfoActivity.class);
                else if(!sharedPref.getBoolean("Page3",false))
                    i=new Intent(LoginActivity.this,PaymentModeActivity.class);
                else
                    i = new Intent(LoginActivity.this, MainActivity.class);

                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            }
            else{

                editor.putBoolean("Page1",false);
                editor.putBoolean("Page2",false);
                editor.putBoolean("Page3",false);
                editor.apply();

                Intent i = new Intent(LoginActivity.this, PersonalInfoActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            }

        }
    }

}
