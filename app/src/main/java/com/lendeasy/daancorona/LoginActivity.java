package com.lendeasy.daancorona;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    EditText phone,otp;
    Button sendotp,verifyotp;
    String codeSent,phoneNumber,url="127.08.01.20";
    FirebaseAuth mAuth=FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        phone=findViewById(R.id.phone);
        otp=findViewById(R.id.otp);

        sendotp=findViewById(R.id.sendotp);
        verifyotp=findViewById(R.id.verifyotp);

        sendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //sendVerificationCode();
            }
        });

        verifyotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(otp.getText().toString().equals("123456") && phone.getText().toString().length()==10){
                    Intent i = new Intent(LoginActivity.this, InfoActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                            finish();
                }
                //verifySignIn();
            }
        });
    }

//    private void verifySignIn(){
//        String code=otp.getText().toString();
//        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, code);
//        signInWithPhoneAuthCredential(credential);
//    }
//
//    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d("TAG", "signInWithCredential:success");
//
//                            FirebaseUser user = task.getResult().getUser();
//                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
//                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            startActivity(i);
//                            finish();
//                            // ...
//                        } else {
//                            // Sign in failed, display a message and update the UI
//                            Log.w("TAG", "signInWithCredential:failure", task.getException());
//                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
//                                // The verification code entered was invalid
//                            }
//                        }
//                    }
//                });
//    }
//
//    private void sendVerificationCode(){
//        phoneNumber=phone.getText().toString();
//        //phoneNumber="+91"+phoneNumber;
//
//        if(phoneNumber.isEmpty()){
//            Toast.makeText(this,"Invalid",Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                phoneNumber,        // Phone number to verify
//                60,                 // Timeout duration
//                TimeUnit.SECONDS,   // Unit of timeout
//                this,               // Activity (for callback binding)
//                mCallbacks);        // OnVerificationStateChangedCallbacks
//    }
//
//    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//        @Override
//        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//            Intent i = new Intent(LoginActivity.this, MainActivity.class);
//            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(i);
//            finish();
//        }
//
//        @Override
//        public void onVerificationFailed(@NonNull FirebaseException e) {
//            Log.d("TAG","Nahi");
//
//        }
//
//        @Override
//        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//            super.onCodeSent(s, forceResendingToken);
//
//            Log.d("TAG","Gaya");
//            codeSent=s;
//        }
//    };


}
