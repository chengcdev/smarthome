package com.mili.smarthome.tkj.view;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;

import java.util.regex.Pattern;


public class CustomNum extends RelativeLayout implements Runnable{
    private Context mContext;
    private Handler handler = new Handler();
    private boolean isFlash;
    private TextView mTV;
    private ImageView mImaLine;
    private boolean isChange;
    private ImageView mImaXing;

    public CustomNum(Context context) {
        super(context);
        init(context);
    }


    public CustomNum(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomNum(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.num_view_layout, this);
        mTV = (TextView) findViewById(R.id.tv_text);
        mImaLine = (ImageView) findViewById(R.id.img_line);
        mImaXing = (ImageView) findViewById(R.id.img_xing);
    }


    public boolean isText(){
        if (mTV.getText().toString() == null || mTV.getText().toString().equals("")) {
            return true;
        }else {
            return false;
        }
    }


    public void hideLine(){
        mImaLine.setVisibility(View.INVISIBLE);
        if (handler != null) {
            handler.removeCallbacks(this);
        }
    }

    public void showLine(){
        mImaLine.setVisibility(View.VISIBLE);
        if (handler != null) {
            handler.removeCallbacks(this);
        }
    }

    public void setFlash(){
        if (handler != null) {
            handler.removeCallbacks(this);
            handler = new Handler();
        }
        handler.postDelayed(this, 500);
    }

    public void setTextNoLine(String text){
        isNumeric(text);
        mTV.setText(text);
        isChange = true;
        mImaLine.setVisibility(View.INVISIBLE);
        mImaXing.setVisibility(View.GONE);
        if (handler != null) {
            handler.removeCallbacks(this);
        }
    }

    public void setTextXing(String text){
        isNumeric(text);
        mTV.setText(text);
        isChange = true;
        mImaLine.setVisibility(View.INVISIBLE);
        mTV.setVisibility(View.INVISIBLE);
        mImaXing.setVisibility(View.VISIBLE);
        if (handler != null) {
            handler.removeCallbacks(this);
        }
    }

    public void setTextLine(String text){
        isNumeric(text);
        mTV.setText(text);
        mImaLine.setVisibility(View.VISIBLE);

    }

    public String getTextContent(){
        return mTV.getText().toString();
    }

    public boolean isEdit(){
        return isChange;
    }

    /**
     * 关闭闪烁不带下划线
     */
    public void closeAllFalsh(){
        if (handler != null) {
            handler.removeCallbacks(this);
            mImaLine.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 关闭闪烁带下划线
     */
    public void closeAllFalshLine(){
        if (handler != null) {
            handler.removeCallbacks(this);
            mImaLine.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void run() {
        if (!isFlash) {
            isFlash = true;
            mImaLine.setVisibility(View.INVISIBLE);
        }else {
            isFlash = false;
            mImaLine.setVisibility(View.VISIBLE);
        }
        setFlash();
    }


    //判断是否未中文字符
    public void isNumeric(String str){
        if (str != null && !str.equals("")) {
            Pattern pattern = Pattern.compile("[0-9]*[.]*");
            boolean isStr = pattern.matcher(str).matches();
            if (!isStr) {
                //修改textview的宽度
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(mContext.getResources().getDimensionPixelOffset(R.dimen.dp_16),RelativeLayout.LayoutParams.WRAP_CONTENT);
                mTV.setLayoutParams(layoutParams);
            }
        }
    }
}
