package com.mili.smarthome.tkj.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.mili.smarthome.tkj.R;

public class NetworkView extends AppCompatImageView {

    private BroadcastReceiver mReceiver;

    public NetworkView(Context context) {
        this(context, null, 0);
    }

    public NetworkView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NetworkView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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

    private void onNetworkChanged(boolean connected) {
        if (connected) {
            setColorFilter(Color.WHITE);
        } else {
            clearColorFilter();
        }
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mReceiver = new NetworkBroadcastReceiver();
        getContext().registerReceiver(mReceiver, filter);
    }

    private void unregisterReceiver() {
        getContext().unregisterReceiver(mReceiver);
    }

    private class NetworkBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                if (activeNetwork != null && activeNetwork.isConnected()) {
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI
                            || activeNetwork.getType() == ConnectivityManager.TYPE_ETHERNET
                            || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE ) {
                        onNetworkChanged(true);
                        return;
                    }
                }
                onNetworkChanged(false);
            }
        }
    }
}
