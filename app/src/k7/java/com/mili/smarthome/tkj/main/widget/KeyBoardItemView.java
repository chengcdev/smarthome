package com.mili.smarthome.tkj.main.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.utils.AppManage;

public class KeyBoardItemView extends RelativeLayout {

    public static IOnKeyClickListener mOnkeyClickListener;
    private Context mContext;
    private ImageView mImg;
    private LinearLayout linRoot;
    private boolean isCallView;
    private LinearLayout linBianma;
    private RelativeLayout mRlDirect;
    private boolean showBtnCall;

    public KeyBoardItemView(Context context) {
        this(context, null);
    }

    public KeyBoardItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KeyBoardItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }


    public void initView(Context context, AttributeSet attrs) {
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.key_board_item_view, this);

        linBianma = (LinearLayout) findViewById(R.id.lin_bianma);
        mRlDirect = (RelativeLayout) findViewById(R.id.rl_direct);
        mImg = (ImageView) findViewById(R.id.iv_icon);
        linRoot = (LinearLayout) findViewById(R.id.lin_root);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.KeyBoardItemView);
        try {
            int resourceId = typedArray.getResourceId(R.styleable.KeyBoardItemView_imageSouces, 0);
            int resourceId_long = typedArray.getResourceId(R.styleable.KeyBoardItemView_backGround, R.drawable.keyboard_btn_bg);
            isCallView = typedArray.getBoolean(R.styleable.KeyBoardItemView_callView, false);
            showBtnCall = typedArray.getBoolean(R.styleable.KeyBoardItemView_showBtnCall, false);
            if (isCallView) {
                linBianma.setVisibility(View.GONE);
                mRlDirect.setVisibility(View.VISIBLE);
            } else {
                linBianma.setVisibility(View.VISIBLE);
                mRlDirect.setVisibility(View.GONE);
                if (showBtnCall) {
                    ViewGroup.LayoutParams layoutParams = mImg.getLayoutParams();
                    layoutParams.height = LayoutParams.MATCH_PARENT;
                    layoutParams.width = LayoutParams.MATCH_PARENT;
                    mImg.setLayoutParams(layoutParams);
                    mImg.setScaleType(ImageView.ScaleType.FIT_XY);
                }
                linRoot.setBackgroundResource(resourceId_long);
                mImg.setImageResource(resourceId);
            }
        } finally {
            typedArray.recycle();
        }

    }


    public void setImgBg(int imgId) {
        mImg.setImageResource(imgId);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

//        LogUtils.w(" KeyBoardItemView onKeyDown... saveKeycode: " + AppManage.getInstance().getmCurrentKeycode() + "  currentKeycode: " + keyCode);

        if (AppManage.getInstance().getmCurrentKeycode() != keyCode) {
            if (mOnkeyClickListener != null) {
                mOnkeyClickListener.OnViewDownClick(keyCode, this);
                AppManage.getInstance().setmCurrentKeycode(keyCode);
//                LogUtils.w(" KeyBoardItemView onKeyDown... keyCode: " + keyCode);
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        LogUtils.w(" KeyBoardItemView onKeyUp... keyCode: " + keyCode);
        AppManage.getInstance().setmDefaultKeycode();
        if (mOnkeyClickListener != null) {
            mOnkeyClickListener.OnViewUpClick(keyCode, this);
        }
        return super.onKeyUp(keyCode, event);
    }


    public interface IOnKeyClickListener {
        void OnViewDownClick(int code, View view);

        void OnViewUpClick(int code, View view);
    }

    public static void setOnkeyClickListener(IOnKeyClickListener onkeyClickListener) {
        mOnkeyClickListener = onkeyClickListener;
    }


}
