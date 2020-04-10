package com.lendeasy.daancorona;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

class LoadingDialog {

    private Activity activity;
    private AlertDialog dialog;
    LoadingDialog(Activity activity){
        this.activity=activity;
    }

    void startloadingDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(activity);

        LayoutInflater layoutInflater=activity.getLayoutInflater();
        builder.setView(layoutInflater.inflate(R.layout.loading,null));
        builder.setCancelable(false);

        dialog=builder.create();
        dialog.show();
    }

    void dismissDialog(){
        dialog.dismiss();
    }
}
