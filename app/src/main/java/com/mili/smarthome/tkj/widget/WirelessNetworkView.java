package com.mili.smarthome.tkj.widget;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.view.View;

import com.android.main.NetworkService;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;
import com.mili.smarthome.tkj.utils.LogUtils;

public class WirelessNetworkView extends AppCompatImageView {

    private static final String TAG = "WirelessNetworkView >>> ";
//    private static BroadcastReceiver mReceiver;
    private TelephonyManager mTelephonyManager;
    private String mImgResType;
    private String RES_TYPE_K7 = "k7";

    public WirelessNetworkView(Context context) {
        this(context, null, 0);
    }

    public WirelessNetworkView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WirelessNetworkView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        @SuppressLint("Recycle") TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WirelessNetworkView);
        mImgResType = typedArray.getString(R.styleable.WirelessNetworkView_imageResType);
        if (mImgResType != null && mImgResType.equals(RES_TYPE_K7)) {
            setImageResource(R.drawable.net_error);
        }else {
            setImageResource(R.drawable.main_location);
        }
        refreshNetworkIcon();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        registerReceiver();
        refreshNetworkIcon();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        unregisterReceiver();
    }

    private void onNetworkChanged(boolean connected) {
        if (mImgResType != null && mImgResType.equals(RES_TYPE_K7)) {
            setImageResource(R.drawable.net_error);
            if (connected) {
                setVisibility(View.GONE);
            } else {
                setVisibility(View.VISIBLE);
            }
        }else {
            setImageResource(R.drawable.main_location);
            if (connected) {
                setColorFilter(Color.WHITE);
            } else {
                clearColorFilter();
            }
        }
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(NetworkService.ACTION_NETWORK_CONNECTIVITY);
        getContext().registerReceiver(mReceiver, filter);
    }

    private void unregisterReceiver() {
        if (mReceiver != null) {
            getContext().unregisterReceiver(mReceiver);
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(NetworkService.ACTION_NETWORK_CONNECTIVITY)) {
                LogUtils.d(TAG + " =========== ACTION_NETWORK_CONNECTIVITY =========");
                refreshNetworkIcon();
            }
        }
    };

    /**
     * 刷新网络图标
     */
    private void refreshNetworkIcon() {
        int networkType = SinglechipClientProxy.getInstance().getNetworkType();
        int networkState = SinglechipClientProxy.getInstance().getNetworkState();
        LogUtils.d(TAG + "[refreshNetworkIcon] networkType is " + networkType + ", networkState is " + networkState);

        switch (networkType) {
            case ConnectivityManager.TYPE_ETHERNET:
                onNetworkChanged(true);
                break;

            case ConnectivityManager.TYPE_MOBILE:
                setImageResource(getResid(networkState));
                break;

            case ConnectivityManager.TYPE_NONE:
                onNetworkChanged(false);
                break;
        }
    }

    /**
     * 获取网络状态图标
     * @param state 网络状态
     * @return      图标id
     */
    private int getResid(int state) {
        int resId = R.drawable.main_wifi_disable;
        switch (state) {
            case NetworkService.SIGNAL_LEVEL_1:
                resId = R.drawable.main_wifi_0;
                break;
            case NetworkService.SIGNAL_LEVEL_2:
                resId = R.drawable.main_wifi_1;
                break;
            case NetworkService.SIGNAL_LEVEL_3:
                resId = R.drawable.main_wifi_2;
                break;
            case NetworkService.SIGNAL_LEVEL_4:
                resId = R.drawable.main_wifi_3;
                break;
            case NetworkService.SIGNAL_LEVEL_5:
                resId = R.drawable.main_wifi_4;
                break;
        }
        return resId;
    }
}
