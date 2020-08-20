package com.mili.smarthome.tkj.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.utils.DisplayUtils;

public class NumberView extends LinearLayout {

    private Context mContext;
    private INumViewListener numListener;
    private StringBuffer stb;
    private String result = "";
    private int mMaxLen;
    private boolean isAddNum = true;
    public static int NUM_TYPE_ADMIN = 0;
    public static int NUM_TYPE_CALL = 1;
    //简易密码
    public static int NUM_TYPE_RESIDENT_PWD_1 = 2;
    //高级密码
    public static int NUM_TYPE_RESIDENT_PWD_2 = 3;
    public int count;
    private TextView textView;
    private String mTipText;
    //是否输入为管理员密码
    private boolean isAdminPwd;

    public NumberView(Context context) {
        this(context,null);
    }

    public NumberView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public NumberView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER);
    }

    public void init(String tip,int maxLen){
        removeAllViews();
        mMaxLen = maxLen;
        mTipText = tip;
        result = "";
        count = 0;
        showText(mTipText);
    }

    /**
     * 添加数字
     * @param num
     */
    public void addNum(String num,int type){
        count = 0;
        if (textView != null) {
            removeView(textView);
        }
        int childCount = getChildCount();
        if (childCount < mMaxLen) {
            ImageView imageView = new ImageView(mContext);
            if (NUM_TYPE_ADMIN == type) {
                imageView.setImageResource(showImgNum("*"));
            }else if (NUM_TYPE_RESIDENT_PWD_1 == type) {
                imageView.setImageResource(showImgNum("*"));
            }else {
                imageView.setImageResource(showImgNum(num));
            }
            imageView.setPadding(5,0,5,0);
            addView(imageView);
            LinearLayout.LayoutParams layoutParams = (LayoutParams)imageView.getLayoutParams();
            layoutParams.weight = 1;
            //拼接数字
            stb = new StringBuffer();
            result = stb.append(result).append(num).toString();
            if (numListener != null) {
                numListener.getNum(result);
            }
        }
    }

    /**
     * 高级密码添加
     */
    public void addSeniorNum(String num,int roomLen){
        count = 0;
        if (textView != null) {
            removeView(textView);
        }
        int childCount = getChildCount();
        if (childCount < mMaxLen) {
//            ImageView imageView = new ImageView(mContext);
            TextView tv_num = new TextView(getContext());
            tv_num.setTextSize(getResources().getDimensionPixelSize(R.dimen.sp_36));
            tv_num.setTextColor(Color.WHITE);
            if (result.length() < roomLen) {
//                imageView.setImageResource(showImgNum(num));
                tv_num.setText(num);
            }else {
//                imageView.setImageResource(showImgNum("*"));
                tv_num.setText("*");
            }
//            imageView.setPadding(5,0,5,0);
            addView(tv_num);
            LinearLayout.LayoutParams layoutParams = (LayoutParams)tv_num.getLayoutParams();
            layoutParams.weight = 1;
            //拼接数字
            stb = new StringBuffer();
            result = stb.append(result).append(num).toString();
            if (numListener != null) {
                numListener.getNum(result);
            }
        }
    }

    /**
     * 删除数字
     */
    public boolean removeNum(){
        count = 0;
        if (result.equals("") || result.length() == 0) {
            return true;
        }else if (result.length() == 1) {
            removeAllViews();
            showText(mTipText);
            result = "";
            if (numListener != null) {
                numListener.getNum(result);
            }
            return false;
        }
        result = result.substring(0, result.length() - 1);
        int length = result.length();
        removeView(getChildAt(length));
        if (numListener != null) {
            numListener.getNum(result);
        }
        return false;
    }

    /**
     * 请输入管理密码
     */
    public boolean inputAdminNum(){
        if (count == 0) {
            result = "";
            removeAllViews();
            showText(mTipText);
        }
        count++;
        if (count == 5) {
            result = "";
            removeAllViews();
            mTipText = mContext.getString(R.string.set_input_admin_pwd);
            showText(mTipText);
            mMaxLen = 8;
            if (numListener != null) {
                numListener.getNum(result);
            }
        }
        return count >= 5;
    }

    private void showText(String tip) {
        textView = new TextView(mContext);
        textView.setTextColor(getResources().getColor(R.color.txt_white));
        textView.setTextSize(DisplayUtils.px2sp(mContext,70));
        textView.setText(tip);
        addView(textView);
    }


    public int showImgNum(String num) {
        int resId = 0;
        switch (num) {
            case "0":
                resId = R.drawable.call_0;
                break;
            case "1":
                resId = R.drawable.call_1;
                break;
            case "2":
                resId = R.drawable.call_2;
                break;
            case "3":
                resId = R.drawable.call_3;
                break;
            case "4":
                resId = R.drawable.call_4;
                break;
            case "5":
                resId = R.drawable.call_5;
                break;
            case "6":
                resId = R.drawable.call_6;
                break;
            case "7":
                resId = R.drawable.call_7;
                break;
            case "8":
                resId = R.drawable.call_8;
                break;
            case "9":
                resId = R.drawable.call_9;
                break;
            case "*":
                resId = R.drawable.char_xing;
                break;
        }
        return resId;
    }

    public interface INumViewListener {
        void getNum(String num);
    }

    public void setNumListener(INumViewListener numListener){
        this.numListener = numListener;
    }
}
