package com.lendeasy.daancorona;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

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
    TextInputLayout editTxtPhone1,editTxtOtp1;
    Button btnSendotp, btnVerifyOtp,resend;
    LoadingDialog dialog;
    TextView timer;
    private int totalTimeCountInMilliseconds;
    OTPDialog otpDialog;
    private CountDownTimer countDownTimer;
  //  TextView textOtp,textPhone;
    String codeSent,code,phoneNumber;
    boolean newuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

       // textPhone = findViewById(R.id.txt_phone);
        editTxtPhone =findViewById(R.id.editTxt_phone);
        btnSendotp = findViewById(R.id.btn_send_otp);

        SharedPreferences sharedPref=getSharedPreferences("User",MODE_PRIVATE);

      //  textOtp = findViewById(R.id.txt_otp);
        editTxtOtp = findViewById(R.id.edit_txt_otp);
        btnVerifyOtp = findViewById(R.id.btn_verify_otp);
        editTxtOtp1=findViewById(R.id.edit_txt_otp1);
        editTxtPhone1=findViewById(R.id.editTxt_phone1);
        timer=findViewById(R.id.timer);
        resend=findViewById(R.id.resend);

        editTxtOtp.setVisibility(View.GONE);
        btnVerifyOtp.setVisibility(View.GONE);
       // textOtp.setVisibility(View.GONE);

        dialog=new LoadingDialog(this);
        if(sharedPref.getString("Lang","").equals("hin")){
            btnSendotp.setText(getResources().getString(R.string.getotp));
            btnVerifyOtp.setText(getResources().getString(R.string.verifyotp));
            editTxtPhone1.setHint(getResources().getString(R.string.enterphn));
            editTxtOtp1.setHint(getResources().getString(R.string.enterotp));
            resend.setText("पुनः भेजें");

        }

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btnSendotp.setVisibility(View.VISIBLE);
                editTxtPhone.setVisibility(View.VISIBLE);
                resend.setVisibility(View.GONE);
                editTxtOtp.setVisibility(View.GONE);
                btnVerifyOtp.setVisibility(View.GONE);
            }
        });

        btnSendotp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {



                phoneNumber = editTxtPhone.getText().toString().trim();
                if(phoneNumber.length()==10) {
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
                        .url("https://daancorona.tech/api/mobile/")
                        .post(formbody)
                        .build();

            try (Response response = httpClient.newCall(request).execute()) {

                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response);
                JSONObject jsonObject=new JSONObject(response.body().string());

//                codeSent=jsonObject.getString("otp");

                Log.d("Tag",response.body()+"");

//                JSONObject jsonObject=new JSONObject(response.body().string());
//                codeSent= jsonObject.getString("otp");

                return "Done";

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

            if(s==null)
                Toast.makeText(LoginActivity.this,"Error",Toast.LENGTH_SHORT).show();

            else {
                editTxtOtp.setVisibility(View.VISIBLE);
                btnVerifyOtp.setVisibility(View.VISIBLE);
                // textOtp.setVisibility(View.VISIBLE);
                editTxtPhone.setText("");
                btnSendotp.setVisibility(View.GONE);
                editTxtPhone.setVisibility(View.GONE);
                setTimer();
                // textPhone.setVisibility(View.GONE);
            }
        }
    }


    class VerifyOtpTask extends AsyncTask<String,Void,String[]>{

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String[] doInBackground(String... strings) {

            String access="",refresh="";

            final OkHttpClient httpClient = new OkHttpClient();

            RequestBody formbody=new FormBody.Builder()
                    .addEncoded("mobile",strings[0])
                    .addEncoded("token",strings[1])
                    .build();

            Request request = new Request.Builder()
                    .url("https://daancorona.tech/api/otp/")
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

            return new String[]{access, refresh};
        }

        @Override
        protected void onPostExecute(String... s) {

            super.onPostExecute(s);
            dialog.dismissDialog();

            if(s==null || s.equals("")) {
                Toast.makeText(LoginActivity.this, "Error!!!", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences sharedPref=getSharedPreferences("User",MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPref.edit();
            editor.putString("Token",s[0]);
            editor.putString("Token1",s[1]);
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

    private void setTimer() {
        totalTimeCountInMilliseconds=20;
        //Toast.makeText(getContext(), "Please Enter Minutes...",
        //      Toast.LENGTH_LONG).show();
        totalTimeCountInMilliseconds = totalTimeCountInMilliseconds * 1000;
        //totalTimeCountInMilliseconds = 60 * time * 1000;

        startTimer();
    }

    private void startTimer() {
        timer.setVisibility(View.VISIBLE);
        countDownTimer = new CountDownTimer(totalTimeCountInMilliseconds, 1000) {
            // 500 means, onTick function will be called at every 500
            // milliseconds
            @Override
            public void onFinish() {

                resend.setVisibility(View.VISIBLE);
                timer.setVisibility(View.GONE);
            }
            @Override
            public void onTick(long leftTimeInMilliseconds) {

                SharedPreferences sharedPref=getSharedPreferences("User",MODE_PRIVATE);
                if(sharedPref.getString("Lang","").equals("hin"))
                    timer.setText("आप " +leftTimeInMilliseconds/1000+ " सेकंड में otp फिर से भेज सकते हैं");
                else
                    timer.setText("You can resend otp in "+leftTimeInMilliseconds/1000+"s");
            }

        }.start();
    }

}
