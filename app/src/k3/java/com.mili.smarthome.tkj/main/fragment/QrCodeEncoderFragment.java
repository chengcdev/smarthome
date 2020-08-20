package com.mili.smarthome.tkj.main.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.appfunc.FreeObservable;
import com.mili.smarthome.tkj.base.K3BaseFragment;
import com.mili.smarthome.tkj.main.widget.GotoMainDefaultTask;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;
import com.mili.widget.zxing.encode.QRCodeEncoder;

import java.util.Locale;

public class QrCodeEncoderFragment extends K3BaseFragment {

    private ImageView ivQrCode;
    private TextView tvTime;

    private CountdownTask mCountdownTask;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_qrcode_encoder;
    }

    @Override
    protected void bindView() {
        ivQrCode = findView(R.id.iv_qrcode);
        tvTime = findView(R.id.tv_time);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FreeObservable.getInstance().cancelObserveFree();
        String qrCodeString = AppConfig.getInstance().getBluetoothQrCode();
        if (qrCodeString != null && !qrCodeString.equals("")) {
            //生成二维码图片
            Bitmap bitmap = QRCodeEncoder.encodeAsBitmap(qrCodeString, (int)getResources().getDimension(R.dimen.dp_180),0);
            if (bitmap != null) {
                ivQrCode.setImageBitmap(bitmap);
            }
        }

        mCountdownTask = new CountdownTask();
        mCountdownTask.run();
    }

    @Override
    public void onDestroyView() {
        mMainHandler.removeCallbacks(mCountdownTask);
        FreeObservable.getInstance().observeFree();
        super.onDestroyView();
    }

    @Override
    public boolean onKeyEvent(int keyCode, int keyState) {
        if (keyState == KEYSTATE_DOWN || keyCode == KEYCODE_UNLOCK)
            return false;
        switch (keyCode) {
            case KEYCODE_BACK:
                GotoMainDefaultTask.getInstance().run();
                SinglechipClientProxy.getInstance().disableBodyInductionForTenSecond(3);
                break;
        }
        return true;
    }

    private class CountdownTask implements Runnable {

        private int mTime = 30;

        @Override
        public void run() {
            if (mTime >= 0) {
                tvTime.setText(String.format(Locale.getDefault(), "%dS", mTime));
                mMainHandler.postDelayed(CountdownTask.this, 1000);
                mTime--;
            } else {
                GotoMainDefaultTask.getInstance().run();
            }
        }
    }
}
