package com.mili.smarthome.tkj.main.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.mili.smarthome.tkj.R;

public class NetworkView extends AppCompatImageView {

    private BroadcastReceiver mReceover;

    public NetworkView(Context context) {
        this(context, null, 0);
    }

    public NetworkView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NetworkView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setImageResource(R.drawable.main_location);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        registerReceiver();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        unregisterReceiver();
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mReceover = new NetworkBroadcastReceiver();
        getContext().registerReceiver(mReceover, filter);
    }

    private void unregisterReceiver() {
        if (mReceover != null) {
            getContext().unregisterReceiver(mReceover);
        }
    }

    private void OnNetworkChanged(boolean connected) {
        if (connected) {
            setColorFilter(Color.WHITE);
        } else {
            clearColorFilter();
        }
    }

    private class NetworkBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = manager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI
                            || networkInfo.getType() == ConnectivityManager.TYPE_ETHERNET
                            || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE ) {
                        OnNetworkChanged(true);
                        return;
                    }
                }
                OnNetworkChanged(false);
            }
        }
    }
}
