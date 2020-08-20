package com.example.authrolibrary.utils;


import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.example.authrolibrary.R;
import com.example.authrolibrary.view.CustomDialog;

public class DialogUtils {

    public AlertDialog loadingDialog(Context context) {
        CustomDialog customDialog = new CustomDialog(context,0);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
        AlertDialog alertDialog = customDialog.showDialog(view);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        return  alertDialog;
    }
}
