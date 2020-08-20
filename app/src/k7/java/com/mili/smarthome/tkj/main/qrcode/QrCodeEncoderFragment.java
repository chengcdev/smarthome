package com.mili.smarthome.tkj.main.qrcode;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.main.fragment.BaseMainFragment;
import com.mili.smarthome.tkj.utils.AppManage;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.widget.zxing.encode.QRCodeEncoder;

import java.util.Objects;

/**
 * 蓝牙开门器
 */

public class QrCodeEncoderFragment extends BaseMainFragment {

    private TextView mTvCountTime;
    private ImageView mImgQr;
    private int totalTime = 30;
    private Handler handler;
    private CountTimeRun countTimeRun;

    @Override
    public int getLayout() {
        return R.layout.fragment_qrcode_encoder;
    }

    @Override
    public void initView() {
        //关闭所有屏幕服务操作
        AppManage.getInstance().stopScreenService();

        mTvCountTime = (TextView)getContentView().findViewById(R.id.tv_count_time);
        mImgQr = (ImageView) getContentView().findViewById(R.id.img_qr);
    }

    @Override
    public void initAdapter() {
        //倒计时
        setCountTime();
        //生成二维码
        showQrCode();
    }

    @Override
    public void initListener() {

    }

    private void showQrCode() {
        String qrCodeString = AppConfig.getInstance().getBluetoothQrCode();
        if (qrCodeString != null && !qrCodeString.equals("")) {
            //生成二维码图片
            Bitmap bitmap = QRCodeEncoder.encodeAsBitmap(qrCodeString, 300,1);
            if (bitmap != null) {
                mImgQr.setImageBitmap(bitmap);
                LogUtils.w(" QrCodeEncoderFragment : showQrCode()");
            }
        }
    }

    class CountTimeRun implements Runnable {
        @Override
        public void run() {

            totalTime--;

            if (totalTime < 0) {
                handler.removeCallbacks(this);
                Objects.requireNonNull(getActivity()).finish();
                return;
            } else {
                setCountTime();
            }

            if (totalTime == 0) {
                mTvCountTime.setTextColor(Color.RED);
            } else {
                mTvCountTime.setTextColor(Color.GREEN);
            }

        }
    }

    @SuppressLint("SetTextI18n")
    private void setCountTime() {
        if (handler == null) {
            handler = new Handler();
        }
        mTvCountTime.setTextColor(Color.GREEN);
        mTvCountTime.setText(totalTime + "S");
        if (countTimeRun == null) {
            countTimeRun = new CountTimeRun();
        }
        handler.postDelayed(countTimeRun, 1000);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null) {
            if (countTimeRun != null) {
                handler.removeCallbacks(countTimeRun);
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        //开启所有屏幕服务操作
        AppManage.getInstance().startScreenService();
    }
}
