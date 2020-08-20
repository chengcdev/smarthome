package com.example.authrolibrary.view;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.example.authrolibrary.R;


public class CustomDialog extends AlertDialog.Builder {

    private Context mContext;


    public CustomDialog(Context context) {
        super(context);
    }

    public CustomDialog(Context context, int themeResId) {
        super(context, R.style.dialog);

    }

    /**
     * 显示弹窗
     * 自定义view
     * 没标题
     */
    public AlertDialog showDialog(View view){

        setView(view);

        final AlertDialog dialog = show();

        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setBackgroundDrawableResource(R.color.main_fun_view_gray);
        dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
        dialog.getWindow().setAttributes(lp);
        return dialog;
    }
}
