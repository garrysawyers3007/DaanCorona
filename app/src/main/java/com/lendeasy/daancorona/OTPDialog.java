package com.lendeasy.daancorona;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class OTPDialog extends Dialog {



    private Context context;
    private AlertDialog dialog;
    private String otptxt;
    OTPDialog(String otptxt, Context context){
        super(context);

        this.otptxt=otptxt;
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_dialog);

        TextView otp=findViewById(R.id.otp);
        otp.setText(otptxt);
        setCancelable(true);
    }
}
