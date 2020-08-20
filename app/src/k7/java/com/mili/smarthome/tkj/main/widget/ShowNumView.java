package com.mili.smarthome.tkj.main.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.main.interf.ShowNumListener;
import com.mili.smarthome.tkj.set.Constant;

public class ShowNumView extends LinearLayout {

    private Context mContext;
    private String nums = "";
    private static int roomNumLen;
    private static int maxLen;
    private static int intputType = Constant.KEY_INPUT_DEVICE_NO;
    private int index;
    private ShowNumListener showNumListener;
    private int mesuWidth;
    private int width;


    public ShowNumView(Context context) {
        super(context);
        init(context);
    }

    public ShowNumView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ShowNumView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER);
    }

    public void setNumLen(int intputType, int roomNumLen, int maxLen) {
        this.intputType = intputType;
        this.roomNumLen = roomNumLen;
        this.maxLen = maxLen;
    }


    /**
     * 添加显示数字
     */
    public void setCallNum(String num) {
        int mesureWidth = 502/maxLen;
        TextView textView = new TextView(mContext);
        LayoutParams layoutParams = new LayoutParams(mesureWidth,LayoutParams.WRAP_CONTENT);
        layoutParams.width = mesureWidth;
        textView.setLayoutParams(layoutParams);
        textView.setTextSize(mesureWidth);
        textView.setTextColor(Color.WHITE);
        textView.setGravity(Gravity.CENTER);

        switch (intputType) {
            //4位房号
            case Constant.KEY_INPUT_DEVICE_NO:
                textView.setText(num);
                break;
            //开门密码
            case Constant.KEY_INPUT_OPEN_PWD:
                //密码进门模式
                if (AppConfig.getInstance().getPwdDoorMode() == 0) {
                    //简易模式
                    textView.setTextSize(100);
                    textView.setText("*");
                } else {
                    //高级模式
                    if (nums.length() > roomNumLen - 1) {
                        textView.setText("*");
                    } else {
                        textView.setText(num);
                    }
                }
                break;
            //管理员密码
            case Constant.KEY_INPUT_ADMIN_PWD:
                //获取屏幕宽度
                textView.setTextSize(100);
                textView.setText("*");
                break;
        }


        if (getChildCount() >= maxLen) {
            return;
        }

        addView(textView);

        if (showNumListener != null) {
            nums = nums + num;
            showNumListener.getNum(nums, intputType, index);
        }


    }

    /**
     * 删除最后一位数字
     */
    public int removeNum() {
        int childCount = getChildCount();
        index = childCount - 1;

        if (index < 0) {
            showNumListener.getNum(nums, intputType, index);
            return index;
        }

        removeView(getChildAt(childCount - 1));
        if (showNumListener != null) {
            nums = nums.substring(0, nums.length() - 1);
            showNumListener.getNum(nums, intputType, index);
        }

        return index;
    }

    /**
     * 删除所有数字
     */
    public void removeALlNum() {

        removeAllViews();

        if (showNumListener != null) {
            nums = "";
            showNumListener.getNum(nums, intputType, index);
        }
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
                resId = R.drawable.char_xing2;
                break;
        }
        return resId;
    }

    public void setShowNumListener(ShowNumListener showNumListener) {
        this.showNumListener = showNumListener;
    }

}
