package com.android.client;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import com.android.CommSysDef;
import com.android.Common;
import com.android.IntentDef;
import com.android.interf.IFingerEventListener;
import com.mili.smarthome.tkj.utils.LogUtils;

/**
 * 指纹模块接口
 * Created by zhengxc on 2019/1/2 0002.
 */

public class FingerClient extends BaseClient implements IntentDef.OnNetCommDataReportListener {

    private final static String TAG = "FingerClient";
    private Context mContext;

    private static final FingerClient fingerClient = new FingerClient();

    public static FingerClient getInstance() {
        return fingerClient;
    }

    private IFingerEventListener mFingerEventListener;

    public FingerClient() {
    }

    public FingerClient(Context context) {
        super(context);
        // 设置界面回调接口
        mContext = context;
        // 启动broadcast接收
        String[] list = new String[]{IntentDef.MODULE_FINGER};
        startReceiver(context, list);
        // 启动服务
        StartIPC_Main(context);
        // 设置回调接口
        setmDataReportListener(this);
    }

    public void stopFingerClient() {
        if (mContext == null) {
            return;
        }
        stopReceiver(mContext, IntentDef.MODULE_FINGER);
        StopIPC(mContext, CommSysDef.SERVICE_NAME_MAIN, IntentDef.MODULE_FINGER);
    }

    /**
     * 设置指纹事件回调
     */
    public void setFingerEventListener(IFingerEventListener listener) {
        mFingerEventListener = listener;
    }

    /**
     * 指纹存储初始化
     * @param fingerId  指纹ID
     * @param valid     是否有效
     * @param roomString    房号
     * @param fingerInfo    指纹特征值
     */
    public void FingerStorageInitAdd(int fingerId, int valid, String roomString, byte[] fingerInfo) {
        if (null == mMainService) {
            Log.d(TAG, "FingerStorageInitAdd mMainService = null");
            return ;
        }
        try {
            mMainService.Main_fingerStorageInitAdd(fingerId, valid, roomString, fingerInfo);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加指纹
     * @param roomString    房号
     * @return  成功：1    失败：0
     */
    public int FingerAdd(String roomString) {
        if (null == mMainService) {
            return 0;
        }
        try {
            return mMainService.Main_fingerAdd(roomString);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * 停止指纹操作
     */
    public void FingerOperStop() {
        if (null == mMainService) {
            return ;
        }
        try {
            mMainService.Main_fingerOperStop();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除住户的所有指纹
     * @param roomString    房号
     * @return  返回删除的指纹数量 -1:删除失败
     */
    public int FingerDelUser(String roomString) {
        if (null == mMainService) {
            return -1;
        }
        try {
            return mMainService.Main_fingerDelUser(roomString);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * 清空指纹
     */
    public void FingerClear() {
        if (null == mMainService) {
            return ;
        }
        try {
            mMainService.Main_fingerClear();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 按照房号识别指纹
     * @param roomString    房号
     */
    public void FingerSetDevnoDistinguish(String roomString) {
        if (null == mMainService) {
            return ;
        }
        try {
            mMainService.Main_fingerSetDevnoDistinguish(roomString);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取剩余指纹数
     * 如果指纹模块异常返回-1
     */
    public int FingerGetCount() {
        if (null == mMainService) {
            return -1;
        }
        try {
            return mMainService.Main_fingerGetCount();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * 获取指纹模块是否异常
     */
    public int FingerGetWorkState() {
        if (null == mMainService) {
            return -1;
        }
        try {
            return mMainService.Main_fingerGetWorkState();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * 设置人脸状态
     * 1:在人脸界面  0:不在人脸界面
     */
    public void FingerSetFaceState(int state) {
        if (null == mMainService) {
            return ;
        }
        try {
            mMainService.Main_fingerSetFaceState(state);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnDataReport(String action, int type, byte[] data) {
        Log.d(TAG, "OnDataReport: action=" + action + ", type=" + type+">>>>>data："+ data);
        if (!action.equals(IntentDef.MODULE_FINGER)) {
            return;
        }
        if (mFingerEventListener == null) {
            return;
        }

        switch (type) {
            case IntentDef.PubIntentTypeE.Finger_AddState:   //发送指纹模板添加数据上报
                int ret = Common.bytes2int(data,0);
                if (ret == 1) { // 添加成功
                    int fingerId = Common.bytes2int(data,4);
                    //byte[] devNo = new byte[12];//设备编号
                    //System.arraycopy(data, 4+4, devNo, 0, 12);
                    byte[] fingerInfo = new byte[384];
                    System.arraycopy(data, 4+4+12, fingerInfo, 0, 384);
                    int valid = Common.bytes2int(data,4+4+12+384);
                    mFingerEventListener.onFingerAdd(ret, fingerId, valid, fingerInfo);
                } else {    //3-指纹库满  else-添加失败
                    mFingerEventListener.onFingerAdd(ret, 0, 0, null);
                }
                break;

            case IntentDef.PubIntentTypeE.Finger_OpenDoor:  // 指纹开门回调
                String roomNo = null;
                int state = Common.bytes2int(data,0);   //  0x01: 指纹正确开锁  0x00: 指纹错误  2:保持手指按下  3:请稍候  4:请重按手指
                if (data.length > 4){
                    byte[] roomno = new byte[20];
                    System.arraycopy(data, 4, roomno, 0, 20);
                    roomNo = Common.byteToString(roomno);
                }
                mFingerEventListener.onFingerOpen(state, roomNo);
                LogUtils.d(TAG+" OnDataReport: Finger_OpenDoor state： " + state);
                break;

            case IntentDef.PubIntentTypeE.Finger_State:  // 指纹状态回调

                LogUtils.d(TAG+" OnDataReport: Finger_State");

                int fingerCheckFlag = data[0];  //0:抬起    1:按下
                int count = data[1];    //次数
                int wetFinger = data[2] & 0xff;    // 手指干湿
                int fingerMigration = data[3] & 0xff;  //手指偏移
                int code;
                if (fingerMigration == 0 || fingerMigration == 0xff) {
                    code = fingerMigration;
                } else {
                    int bit1 = fingerMigration & 0x01;
                    int top = fingerMigration & 0x02;
                    int bottom = fingerMigration & 0x04;
                    int left = fingerMigration & 0x08;
                    int right = fingerMigration & 0x10;
                    int bit20 = fingerMigration & 0x20;
                    if (bit1 != 0) {
                        code = IFingerEventListener.FINGER_NONE;
                    } else if (left != 0) {
                        if (top != 0) {
                            code = IFingerEventListener.FINGER_LEFT_TOP;
                        } else if (bottom != 0) {
                            code = IFingerEventListener.FINGER_LEFT_BOTTOM;
                        } else {
                            code = IFingerEventListener.FINGER_LEFT;
                        }
                    } else if (right != 0) {
                        if (top != 0) {
                            code = IFingerEventListener.FINGER_RIGHT_TOP;
                        } else if (bottom != 0) {
                            code = IFingerEventListener.FINGER_RIGHT_BOTTOM;
                        } else {
                            code = IFingerEventListener.FINGER_RIGHT;
                        }
                    } else if (top != 0) {
                        code = IFingerEventListener.FINGER_TOP;
                    } else if (bottom != 0) {
                        code = IFingerEventListener.FINGER_BOTTOM;
                    } else if (bit20 != 0) {
                        code = IFingerEventListener.FINGER_SMALL;
                    } else {
                        code = IFingerEventListener.FINGER_UNKNOWN;
                    }
                }
                mFingerEventListener.onFingerCollect(code, fingerCheckFlag, count);
                break;

            default:
                break;
        }
    }

}
