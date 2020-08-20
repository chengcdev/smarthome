package com.mili.smarthome.tkj.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;


public class MyDialog extends DialogFragment {

    private View view;
    private TextView tvContent;
    private static MyDialog myDialog;
    private String content;
    private static final String KEY_TEXT = "key_text";
    private static final String KEY_IMG = "key_img";
    private Handler handler = new Handler();
    private DialogRun dialogRun;
    private int showTime = 5000;
    private String MYDIALOG_LODING = "dialog";
    private ImageView mIma;
    private int resId;
    private IDissDialogListener dialogListener;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //背景透明
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //外部点击弹窗不取消
        getDialog().setCanceledOnTouchOutside(false);

        view = inflater.inflate(R.layout.fragment_dialog, null);

        initView();

        initDatas();

        return view;

    }

    @SuppressLint("NewApi")
    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        //设置背景
//        window.setBackgroundDrawable(getContext().getDrawable(R.drawable.backgroup));
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setLayout(DisplayUtils.getScreenWidth(getContext()),  DisplayUtils.getScreenHeight(getContext()));
        }

    }

    private void initDatas() {
        content = getArguments().getString(KEY_TEXT);
        resId = getArguments().getInt(KEY_IMG);
        tvContent.setText(content);
        mIma.setImageResource(resId);
    }

    private void initView() {
        tvContent = (TextView) view.findViewById(R.id.tv);
        mIma = (ImageView) view.findViewById(R.id.img);
    }


    public void showDialog(FragmentManager fm) {
        this.show(fm, MYDIALOG_LODING);
        dismissDialog();
    }

    public void showDialog(FragmentActivity activity){
        this.show(activity.getSupportFragmentManager(), MYDIALOG_LODING);
        dismissDialog();
    }

    public void showDialog(FragmentActivity activity, IDissDialogListener dissDialogListener){
        this.show(activity.getSupportFragmentManager(), MYDIALOG_LODING);
        this.dialogListener = dissDialogListener;
        dismissDialog();
    }

    public void setDialog(String text,int resId) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TEXT, text);
        bundle.putInt(KEY_IMG, resId);
        setArguments(bundle);
    }


    public void dismissDialog() {
        if (dialogRun == null) {
            dialogRun = new DialogRun();
        }
        handler.postDelayed(dialogRun, getShowTime());
    }


    @Override
    public void dismiss() {
        super.dismiss();
        if (dialogRun != null) {
            handler.removeCallbacks(dialogRun);
            if (dialogListener != null) {
                dialogListener.dismiss();
            }
        }
    }

    class DialogRun implements Runnable {

        @Override
        public void run() {
            handler.removeCallbacks(this);
            dismiss();
        }
    }


    public int getShowTime() {
        return showTime;
    }

    public void setShowTime(int showTime) {
        this.showTime = showTime;
    }

    public void setDialogListener(IDissDialogListener dialogListener){
        this.dialogListener = dialogListener;
    }

    public interface IDissDialogListener{
        void dismiss();
    }
}
