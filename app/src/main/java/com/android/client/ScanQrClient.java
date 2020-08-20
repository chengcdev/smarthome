package com.android.client;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import com.android.CommSysDef;
import com.android.Common;
import com.android.IntentDef;
import com.mili.smarthome.tkj.appfunc.BuildConfigHelper;

/**
 * Created by Administrator on 2019/1/2 0002.
 */

public class ScanQrClient extends BaseClient implements IntentDef.OnNetCommDataReportListener {

    private final static String TAG = "ScanQrClient";
    private Context mContext = null;
    private OnScanQrClientListener mOnScanQrClientListener = null;

    private static final ScanQrClient scanQr = new ScanQrClient();

    public static ScanQrClient getInstance() {
        return scanQr;
    }

    public ScanQrClient() {
    }

    public ScanQrClient(Context context) {
        super(context);
        // 启动broadcast接收
        String[] list = new String[]{IntentDef.MODULE_SCANQR};
        startReceiver(context, list);
        // 启动服务
        StartIPC_Main(context);
        // 设置回调接口
        setmDataReportListener(this);
        // 设置界面回调接口
        mContext = context;
    }

    public void stopScanQrClient() {
        if (mContext == null) {
            return;
        }
        stopReceiver(mContext, IntentDef.MODULE_SCANQR);
        StopIPC(mContext, CommSysDef.SERVICE_NAME_MAIN, IntentDef.MODULE_SCANQR);
    }

    /**
     * 设置安防客户端回调函数
     *
     * @param listener
     */
    public void setOnScanQrClientListener(OnScanQrClientListener listener) {
        mOnScanQrClientListener = listener;
    }

    @Override
    public void OnDataReport(String action, int type, byte[] data) {
         Log.d(TAG, "OnDataReport: action=" + action + ", type=" + type);
        if (!action.equals(IntentDef.MODULE_SCANQR)) {
            return;
        }

        switch (type) {
            case IntentDef.PubIntentTypeE.ScanQr_Recognize_CallBack:    //0:有效二维码  1:无效二维码  2:过期二维码
                String roomNo = null;
                int state = Common.bytes2int(data, 0);
                if (data.length > 4){
                    byte[] roomno = new byte[20];
                    System.arraycopy(data, 4, roomno, 0, 20);
                    roomNo = Common.byteToString(roomno);
                }
                if (mOnScanQrClientListener != null) {
                    mOnScanQrClientListener.OnClientStateChanged(state, roomNo);
                }
                break;
            default:
                break;
        }
    }

    public int ScanQrDreal(String qrString, int len) {
        if (null == mMainService) {
            Log.d(TAG, "ScanQrDreal: mMainService is null....");
            return -1;
        }
        try {
            return mMainService.Main_scanqrOpendoorDreal(qrString, len);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public String GetQRencode(String devid) {
        if (null == mMainService) {
            Log.d(TAG, "GetQRencode: mMainService is null....");
            return null;
        }
        try {
            return mMainService.Main_getQRencode(devid);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String GetDeviceInfoQR() {
        if (null == mMainService) {
//            Log.d(TAG, "GetDeviceInfoQR: mMainService is null....");
            return null;
        }
        try {
            int device = 1;
            if (BuildConfigHelper.isK3()) {
                device = 1;
            } else if (BuildConfigHelper.isK4()) {
                device = 2;
            } else if (BuildConfigHelper.isK6()) {
                device = 3;
            } else if (BuildConfigHelper.isK7()) {
                device = 4;
            }
            return mMainService.Main_getDeviceInfoQR(device);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }


    public interface OnScanQrClientListener{
        void OnClientStateChanged(int qrState, String roomNo);
    }
}
