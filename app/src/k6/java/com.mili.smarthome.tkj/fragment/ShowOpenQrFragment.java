package com.mili.smarthome.tkj.fragment;


import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.utils.AppUtils;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.widget.zxing.encode.QRCodeEncoder;

import java.util.Locale;

public class ShowOpenQrFragment extends BaseMainFragment{


    private TextView mTvCount;
    private ImageView mImaQr;
    private CountdownTask mCountdownTask = new CountdownTask();

    @Override
    public void initView(View view) {
        mTvCount = (TextView) view.findViewById(R.id.tv_count);
        mImaQr = (ImageView) view.findViewById(R.id.img_qr);
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_show_qr;
    }

    @Override
    public void onResume() {
        super.onResume();

        AppUtils.getInstance().stopScreenService();

        String qrCodeString = AppConfig.getInstance().getBluetoothQrCode();
        if (qrCodeString != null && !qrCodeString.equals("")) {
            //生成二维码图片
            Bitmap bitmap = QRCodeEncoder.encodeAsBitmap(qrCodeString, (int)getResources().getDimension(R.dimen.dp_180),1);
            if (bitmap != null) {
                mImaQr.setImageBitmap(bitmap);
            }
        }

        //开始倒计时
        initCount();
    }

    private void initCount() {
        mCountdownTask.mTime = 30;
        mCountdownTask.run();
    }


    private class CountdownTask implements Runnable {
        private int mTime;
        @Override
        public void run() {
            mTime--;
            if (mTime >= 0) {
                mTvCount.setText(String.format(Locale.getDefault(), "%dS", mTime));
                mMainHandler.postDelayed(this, 1000);
            } else {
                LogUtils.e(" ShowOpenQrFragment CountdownTask");
                //回到主界面
                backMainActivity();
            }

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AppUtils.getInstance().startScreenService();
        mMainHandler.removeCallbacks(mCountdownTask);
    }
}
