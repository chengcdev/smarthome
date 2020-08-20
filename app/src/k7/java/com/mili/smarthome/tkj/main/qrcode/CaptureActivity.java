package com.mili.smarthome.tkj.main.qrcode;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.main.activity.BaseK7Activity;
import com.mili.smarthome.tkj.main.manage.CommonCallBackManage;
import com.mili.smarthome.tkj.main.widget.KeyBoardItemView;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;
import com.mili.smarthome.tkj.set.Constant;
import com.mili.smarthome.tkj.utils.AppManage;
import com.mili.smarthome.tkj.utils.LogUtils;


public class CaptureActivity extends BaseK7Activity implements View.OnClickListener, KeyBoardItemView.IOnKeyClickListener {

    private static final String TAG = CaptureActivity.class.getSimpleName();
    private KeyBoardItemView mBtnCancel;
    private KeyBoardItemView mBtnLast;
    private KeyBoardItemView mBtnNext;
    //是否显示底部三个按键
    private boolean showBottomBtn;
    private Fragment currentFrag;
    private boolean isHasFocus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        initView();

        if (AppConfig.getInstance().getQrScanEnabled() == 1 &&
                AppConfig.getInstance().getBluetoothDevId() != null &&
                !AppConfig.getInstance().getBluetoothDevId().equals("")) {
            mBtnLast.setVisibility(View.VISIBLE);
            mBtnNext.setVisibility(View.VISIBLE);
            showBottomBtn = true;
        } else {
            mBtnLast.setVisibility(View.GONE);
            mBtnNext.setVisibility(View.GONE);
            showBottomBtn = false;
        }

        Intent intent = getIntent();
        if (intent != null) {
            String extra = intent.getStringExtra(Constant.KEY_PARAM);
            if (extra != null) {
                switch (extra) {
                    case CommonCallBackManage.QR_DECODE:
                        currentFrag = new QrcodeDecoderFragment();
                        break;
                    case CommonCallBackManage.QR_ENCODE:
                        currentFrag = new QrCodeEncoderFragment();
                        break;
                }
                AppManage.getInstance().replaceFragment(this, currentFrag);
            } else {
                toFragment();
            }
        } else {
            toFragment();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        LogUtils.w(" CaptureActivity... onWindowFocusChanged: " + hasFocus);
        isHasFocus = hasFocus;
        super.onWindowFocusChanged(hasFocus);
    }

    private void toFragment() {
        if (AppConfig.getInstance().getQrScanEnabled() == 1) {
            currentFrag = new QrcodeDecoderFragment();
        } else {
            currentFrag = new QrCodeEncoderFragment();
        }
        AppManage.getInstance().replaceFragment(this, currentFrag);
    }


    private void initView() {
        mBtnCancel = (KeyBoardItemView) findViewById(R.id.key_cancle);
        mBtnLast = (KeyBoardItemView) findViewById(R.id.key_last);
        mBtnNext = (KeyBoardItemView) findViewById(R.id.key_next);


        mBtnCancel.setOnClickListener(this);
        KeyBoardItemView.setOnkeyClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.key_cancle:
                //主动退出，禁用人体感应10秒
                if (currentFrag instanceof QrcodeDecoderFragment) {
                    SinglechipClientProxy.getInstance().disableBodyInductionForTenSecond(2);
                } else {
                    SinglechipClientProxy.getInstance().disableBodyInductionForTenSecond(3);
                }
                finish();
                break;
            case 2:

                break;
            default:
                break;
        }
    }


    @Override
    public void OnViewDownClick(int code, View view) {
        LogUtils.w(" CaptureActivity OnViewDownClick...");
        int position = AppManage.getInstance().getPosition(code);
        if (showBottomBtn) {
            switch (position) {
                case Constant.KeyNumId.KEY_NUM_12:
                    AppManage.getInstance().keyBoardDown(mBtnLast);
                    break;
                case Constant.KeyNumId.KEY_NUM_13:
                    AppManage.getInstance().keyBoardDown(mBtnNext);
                    break;
                case Constant.KeyNumId.KEY_NUM_14:
                    AppManage.getInstance().keyBoardDown(mBtnCancel);
                    break;
            }
        } else {
            switch (position) {
                case Constant.KeyNumId.KEY_NUM_12:
                case Constant.KeyNumId.KEY_NUM_13:
                case Constant.KeyNumId.KEY_NUM_14:
                    AppManage.getInstance().keyBoardDown(mBtnCancel);
                    break;
            }
        }


    }

    @Override
    public void OnViewUpClick(int code, View view) {
        LogUtils.w(" CaptureActivity OnViewUpClick...");
        int position = AppManage.getInstance().getPosition(code);
        if (showBottomBtn) {
            switch (position) {
                case Constant.KeyNumId.KEY_NUM_12:
                    AppManage.getInstance().keyBoardUp(mBtnLast);
                    if (isFastDoubleUpClick() && !isHasFocus) {
                        return;
                    }
                    if (currentFrag instanceof QrcodeDecoderFragment) {
                        currentFrag = new QrCodeEncoderFragment();
                    } else {
                        currentFrag = new QrcodeDecoderFragment();
                    }
                    AppManage.getInstance().replaceFragment(this, currentFrag);
                    break;
                case Constant.KeyNumId.KEY_NUM_13:
                    AppManage.getInstance().keyBoardUp(mBtnNext);
                    if (isFastDoubleUpClick() && !isHasFocus) {
                        return;
                    }
                    if (currentFrag instanceof QrcodeDecoderFragment) {
                        currentFrag = new QrCodeEncoderFragment();
                    } else {
                        currentFrag = new QrcodeDecoderFragment();
                    }
                    AppManage.getInstance().replaceFragment(this, currentFrag);
                    break;
                case Constant.KeyNumId.KEY_NUM_14:
                    AppManage.getInstance().keyBoardUp(mBtnCancel);
                    if (isFastDoubleUpClick() && !isHasFocus) {
                        return;
                    }
                    finish();
                    break;
            }
        } else {
            switch (position) {
                case Constant.KeyNumId.KEY_NUM_12:
                case Constant.KeyNumId.KEY_NUM_13:
                case Constant.KeyNumId.KEY_NUM_14:
                    AppManage.getInstance().keyBoardUp(mBtnCancel);
                    if (isFastDoubleUpClick() && !isHasFocus) {
                        return;
                    }
                    finish();
                    break;
            }
        }
    }
}
