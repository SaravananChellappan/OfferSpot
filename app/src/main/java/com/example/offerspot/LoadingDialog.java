package com.example.offerspot;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

class LoadingDialog {

    Activity activity;
    AlertDialog alertDialog;

    LoadingDialog(Activity activity){
        this.activity=activity;
    }


    public void ShowDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.loading_dialog,null));
        builder.setCancelable(false);

        alertDialog = builder.create();
        alertDialog.show();
    }

    public void HideDialog(){
        alertDialog.dismiss();
    }

}
