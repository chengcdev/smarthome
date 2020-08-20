package com.mili.smarthome.tkj.set.widget.inputview;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;


public class InputItemView extends RelativeLayout implements Runnable {
    private Context mContext;
    private Handler handler = new Handler();
    private boolean isFlash;
    private TextView mTvNum;
    private TextView mTvLine;
    private boolean isChange;
    private LinearLayout mLinRoot;

    public InputItemView(Context context) {
        super(context);
        init(context);
    }


    public InputItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public InputItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.custom_input_item_layout, this);
        mTvNum = (TextView) findViewById(R.id.tv_num);
        mTvLine = (TextView) findViewById(R.id.tv_line);
        mLinRoot = (LinearLayout) findViewById(R.id.root);
    }

    public boolean isText() {
        if (mTvNum.getText().toString() == null || mTvNum.getText().toString().equals("")) {
            return true;
        } else {
            return false;
        }
    }

    public void hideLine() {
        mTvLine.setVisibility(View.INVISIBLE);
        if (handler != null) {
            handler.removeCallbacks(this);
        }
    }

    public void showLine() {
        mTvLine.setVisibility(View.VISIBLE);
        if (handler != null) {
            handler.removeCallbacks(this);
        }
    }

    public void setFlash() {
        if (handler != null) {
            handler.removeCallbacks(this);
            handler = new Handler();
        }
        isFlash = true;
        handler.post(this);
    }

    public void setTextNoLine(String text) {
        mTvNum.setText(text);
        isChange = true;
        mTvLine.setVisibility(View.INVISIBLE);
        if (handler != null) {
            handler.removeCallbacks(this);
        }
    }

    public void setTextXing(String text) {
        mTvNum.setText(text);
        isChange = true;
        mTvLine.setVisibility(View.INVISIBLE);
        mTvNum.setVisibility(View.INVISIBLE);
        if (handler != null) {
            handler.removeCallbacks(this);
        }
    }

    public void setTextLine(String text) {
        mTvNum.setText(text);
        mTvLine.setVisibility(View.VISIBLE);
    }

    public String getTextContent() {
        return mTvNum.getText().toString();
    }

    public boolean isEdit() {
        return isChange;
    }

    /**
     * 关闭闪烁不带下划线
     */
    public void closeAllFalsh() {
        if (handler != null) {
            handler.removeCallbacks(this);
            mTvLine.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 关闭闪烁带下划线
     */
    public void closeAllFalshLine() {
        if (handler != null) {
            handler.removeCallbacks(this);
            mTvLine.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void run() {

        if (mTvNum.getText().toString().equals(".")) {
            mTvLine.setVisibility(View.INVISIBLE);
            return;
        }

        if (!isFlash) {
            isFlash = true;
            mTvLine.setVisibility(View.INVISIBLE);
        } else {
            isFlash = false;
            mTvLine.setVisibility(View.VISIBLE);
        }
        handler.postDelayed(this, 500);
    }

}
