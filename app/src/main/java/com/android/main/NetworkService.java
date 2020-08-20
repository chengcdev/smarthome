package com.android.main;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

import com.android.client.MainClient;
import com.android.client.SetDriverSinglechipClient;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.MobileNetworkStateFileUtils;
import com.mili.smarthome.tkj.utils.PingNetworkUtils;


public class NetworkService extends Service {

    private static final String TAG = "NetworkService >>> ";

    public static final String ACTION_NETWORK_CONNECTIVITY = "com.android.main.networkconnect";
    public static final int SIGNAL_LEVEL_NONE = 0;
    public static final int SIGNAL_LEVEL_1 = 1;
    public static final int SIGNAL_LEVEL_2 = 2;
    public static final int SIGNAL_LEVEL_3 = 3;
    public static final int SIGNAL_LEVEL_4 = 4;
    public static final int SIGNAL_LEVEL_5 = 5;

    public static final int NETWORK_TYPE_NONE = 0;
    public static final int NETWORK_TYPE_NULL = 1;
    public static final int NETWORK_TYPE_ETHERNET_ONLINE = 2;
    public static final int NETWORK_TYPE_ETHERNET_OFFLINE = 3;
    public static final int NETWORK_TYPE_MOBILE_ONLINE = 4;
    public static final int NETWORK_TYPE_MOBILE_OFFLINE = 5;

    private TelephonyManager mTelephonyManager;
    private NetworkBroadcastReceiver receiver;
    private int networkType = ConnectivityManager.TYPE_NONE;
    private int mobileSignalLevel = SIGNAL_LEVEL_NONE;
    private int mobileUnconnectCount = 0;

    private boolean mResMobileNetworkIsLink = false;
    private MobileNetworkStateFileUtils mMobileNetworkStateFileUtils;

    /* 是否正在检测网络状态 */
    private boolean isRunning = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SinglechipClientProxy.getInstance().setCloudReset(false);
        setNetworkState(getApplicationContext(), NETWORK_TYPE_NONE);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkBroadcastReceiver();
        registerReceiver(receiver, filter);
        LogUtils.d(TAG + " [onCreate] register receiver of broadcast.");

        mMobileNetworkStateFileUtils = new MobileNetworkStateFileUtils();
        if (-1 != mMobileNetworkStateFileUtils.readNetState()) {//避免APP闪退导致4G网络不能用
            long startTime = SystemClock.elapsedRealtime();
            LogUtils.w(TAG + "WirelessNetworkView startTime = " + startTime / 1000 + "S");
            if (startTime > 60 * 1000) {  // 避免APP闪退导致4G没网络
                if (!mResMobileNetworkIsLink) {
                    new Thread(new ResMobileNetworkRunnable()).start();
                }
            }
        }

        /* 定时3分钟查询网络状态 */
        new Thread(new Runnable() {
            @Override
            public void run() {
                /* 开机后延迟1分钟等待网络稳定 */
                try {
                    Thread.sleep(60*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                while (true) {
                    LogUtils.d(TAG + " ========== refresh network ========= ");
                    try {
                        if (!isRunning) {
                            dealNetwork(getApplicationContext());
                        }
                        Thread.sleep(3*60*1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private class NetworkBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, Intent intent) {
            LogUtils.d(TAG + " network action is " + intent.getAction());
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                if (!isRunning) {
                    dealNetwork(context);
                }
            }
        }
    }

    private void dealNetwork(Context context) {
        isRunning = true;
        int state = networkChange(context);
        if (state == NETWORK_TYPE_MOBILE_ONLINE) {
            /* 标记4G网络连接 */
            mResMobileNetworkIsLink = true;
            mMobileNetworkStateFileUtils.saveNetState(true);

            /* 解决网络状态显示正常，但是无法连接外网的情况， mqtt未连接时才检测 */
            if (MainClient.getInstance().Main_GetMlinkState() != 1) {
                PingNetworkUtils.setPingListener(pingNetWorkListener);
                PingNetworkUtils.startPing();
            } else {
                mobileUnconnectCount = 0;
            }
        } else {
            mobileUnconnectCount = 0;

            /* 4G网络状态异常时处理流程 */
            if (state == NETWORK_TYPE_MOBILE_OFFLINE || state == NETWORK_TYPE_NULL) {
                LogUtils.w(TAG + "mResMobileNetworkIsLink = " + mResMobileNetworkIsLink);
                if (mResMobileNetworkIsLink) {
                    mResMobileNetworkIsLink = false;
                    mMobileNetworkStateFileUtils.saveNetState(false);
                    new Thread(new ResMobileNetworkRunnable()).start();
                }
            }
        }
        isRunning = false;
    }

    /**
     * 检测网络状态
     * @param context context
     * @return  0 无网络 1 以太网络 2 4G网络
     */
    public int networkChange(Context context) {
        int netstate = NETWORK_TYPE_NONE;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null) {
            LogUtils.d(TAG + " network type is " + activeNetwork.getType() + ", connect is " + activeNetwork.isConnected());
            LogUtils.d(TAG + " available is " + activeNetwork.isAvailable());
            LogUtils.d(TAG + " state is " + activeNetwork.getState() + " ? " + NetworkInfo.State.CONNECTED);
            LogUtils.d(TAG + " isFailover is " + activeNetwork.isFailover());

            switch (activeNetwork.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                case ConnectivityManager.TYPE_ETHERNET:
                    if (activeNetwork.isConnected() && activeNetwork.isAvailable()) {
                        netstate = NETWORK_TYPE_ETHERNET_ONLINE;
                    } else {
                        netstate = NETWORK_TYPE_ETHERNET_OFFLINE;
                    }
                    break;
                case ConnectivityManager.TYPE_MOBILE:
                    if (activeNetwork.isConnected()  && activeNetwork.isAvailable()) {
                        netstate = NETWORK_TYPE_MOBILE_ONLINE;
                    } else {
                        netstate = NETWORK_TYPE_MOBILE_OFFLINE;
                    }
                    break;
            }
        } else {
            LogUtils.d(TAG + " activeNetwork is null.");
            netstate = NETWORK_TYPE_NULL;
        }

        LogUtils.d(TAG + "[networkChange] netstate is %d", netstate);
        setNetworkState(context, netstate);
        return netstate;
    }


    /**
     * 未连接网络时处理
     * @param type 网络类型 -1 无网络 0 有线网络 1 4G网络
     */
    private void setNetworkState(Context context, int type) {
        LogUtils.d(TAG + " ===========[setNetworkState] type = %d ======== ", type);
        switch (type) {
            case NETWORK_TYPE_NONE:
            case NETWORK_TYPE_NULL:
            case NETWORK_TYPE_ETHERNET_OFFLINE:
            case NETWORK_TYPE_MOBILE_OFFLINE:
                SinglechipClientProxy.getInstance().setNetworkOnlineType(-1);
                refreshNetwork(ConnectivityManager.TYPE_NONE, 0);
                /* 取消监听4G信号强度 */
                if (mTelephonyManager != null) {
                    mTelephonyManager.listen(mRssiListener, PhoneStateListener.LISTEN_NONE);
                }
                break;
            case NETWORK_TYPE_ETHERNET_ONLINE:
                SinglechipClientProxy.getInstance().setNetworkOnlineType(0);
                refreshNetwork(ConnectivityManager.TYPE_ETHERNET, 0);
                /* 取消监听4G信号强度 */
                if (mTelephonyManager != null) {
                    mTelephonyManager.listen(mRssiListener, PhoneStateListener.LISTEN_NONE);
                }
                break;
            case NETWORK_TYPE_MOBILE_ONLINE:
                SinglechipClientProxy.getInstance().setNetworkOnlineType(1);
                refreshNetwork(ConnectivityManager.TYPE_MOBILE, SIGNAL_LEVEL_NONE);
                /* 监听4G信号强度 */
                if (mTelephonyManager == null) {
                    mTelephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
                }
                mTelephonyManager.listen(mRssiListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
                break;
        }

    }

    /**
     * 4G信号强度监听器
     */
    private PhoneStateListener mRssiListener = new PhoneStateListener() {
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);

            int dbm = signalStrength.getDbm();
            LogUtils.d(TAG + " ========== dbm is " + dbm + ", total is " + signalStrength.toString());

            /*对应的level等级为{0, 4, 6, 8, 11, 14}， 根据dbm = -113 + 2*asu来转换 */
            int[] thresh = new int[]{-113, -105, -101, -97, -91, -85};
            int mobileLevel = SIGNAL_LEVEL_NONE;
            if (dbm != -1) {
                if (dbm >= thresh[5]) {
                    mobileLevel = SIGNAL_LEVEL_5;
                } else if (dbm >= thresh[4]) {
                    mobileLevel = SIGNAL_LEVEL_4;
                } else if (dbm >= thresh[3]) {
                    mobileLevel = SIGNAL_LEVEL_3;
                } else if (dbm >= thresh[2]) {
                    mobileLevel = SIGNAL_LEVEL_2;
                } else if (dbm >= thresh[1]) {
                    mobileLevel = SIGNAL_LEVEL_1;
                }
            }
            refreshNetwork(ConnectivityManager.TYPE_MOBILE, mobileLevel);
        }
    };

    private void refreshNetwork(int type, int level) {
        LogUtils.d(TAG + " [refreshNetwork] type is " + type + ", level is " + level);
        if (networkType == type && mobileSignalLevel == level) {
            LogUtils.d(TAG + "[refreshNetwork] the state is equal to before.");
            return;
        }
        networkType = type;
        mobileSignalLevel = level;
        SinglechipClientProxy.getInstance().setNetworkInfo(networkType, mobileSignalLevel);
        sendBroadcast(new Intent(ACTION_NETWORK_CONNECTIVITY));
    }

    /**
     * 4G网络复位
     */
    private class ResMobileNetworkRunnable implements Runnable {
        private int count = 0;
        private int resNum = 0;

        @Override
        public void run() {
            try {
                while (!mResMobileNetworkIsLink) {
                    LogUtils.d(TAG + "ResMobileNetworkRunnable count = " + count + " resNum = " + resNum);

                    count++;
                    if (count > 100) {  //100秒复位一次
                        count = 0;
                        resNum++;
                        if (resNum >= 5) {  //复位5次后还是没有信号重启设备
                            LogUtils.d(TAG + "ResMobileNetworkRunnable res err reboot!!!");
                            SetDriverSinglechipClient.getInstance().rebootSystem();
                            break;
                        }
                        SetDriverSinglechipClient.getInstance().resetOtgPhy();
                    }

                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 采用http方式判断外网连接状态
     */
    private PingNetworkUtils.PingNetWorkListener pingNetWorkListener = new PingNetworkUtils.PingNetWorkListener() {
        @Override
        public void onPingState(boolean isConnected) {
            if (isConnected) {
                mobileUnconnectCount = 0;
            } else {
                mobileUnconnectCount++;
            }
            LogUtils.d(TAG + "[pingNetWorkListener] isConnected[%b] mobileUnconnectCount[%d]", isConnected, mobileUnconnectCount);

            /* 3次网络未连接复位网络，5次网络未连接重启设备 */
            if (mobileUnconnectCount >= 5) {
                LogUtils.d(TAG + "[pingNetWorkListener] reboot!!!");
                SetDriverSinglechipClient.getInstance().rebootSystem();
            } else if (mobileUnconnectCount >= 3) {
                LogUtils.d(TAG + "[pingNetWorkListener] resetOtgPhy!!!");
                SetDriverSinglechipClient.getInstance().resetOtgPhy();
            }
        }
    };
}
