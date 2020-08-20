package com.android.client;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import com.android.IntentDef;
import com.android.InterCommTypeDef;
import com.android.main.InterCommDistribute;
import com.android.main.MainJni;

public class PassWordClient extends BaseClient implements IntentDef.OnNetCommDataReportListener{
    private final static String TAG = "PassWordClient";
    private Context mContext;
    private InterCommDistribute mInterCommDistribute = null;

    public PassWordClient(Context context){
        super(context);
        mContext = context;
        mInterCommDistribute = new InterCommDistribute();
    }


    public void setPassWordDataCallBKListener(InterCommTypeDef.PassWordCmddListener Listener) {
        if (mInterCommDistribute != null) {
            mInterCommDistribute.setPassWordDataListener(Listener);
        }

        if (Listener != null){
            MainJni.setmPassWordListener(this);
        }
        else{
            MainJni.setmPassWordListener(null);
        }
    }

    @Override
    public void OnDataReport(String action, int type, byte[] data) {
        if (false == action.equals(IntentDef.MODULE_PASSWORD)) {
            return;
        }

        if (type == IntentDef.PubIntentTypeE.PassWord_DealNofity){
            mInterCommDistribute.InterCommDistributePassWord(mContext, data);
        }
    }

    /**
     * 处理高级密码
     * @param devno     房号
     * @param password  密码
     */
    public void DealAdvPassWord(String devno, String password){
        if (null == mMainService) {
            Log.d(TAG, "DealAdvPassWord: mMainService is null....");
            return;
        }
        try {
            mMainService.Main_dealAdvPassWord(devno, password);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return;
    }

    /**
     * 处理简易密码
     * @param devno     房号 (三次密码失败 梯口机："0000" 区口机：本机设备编号字符串)
     * @param result    成功：0 3次失败：1
     * 类型：住户密码:0x00 快递:0x01 外卖:0x02 其他:0x03
     * 连续3次密码错误，设备号 梯口机: "0000"   区口机：本机设备编号字符串
     *
     */
    public void  DealPassWord(String keyID, String devno, int result){
        if (null == mMainService) {
            Log.d(TAG, "DealPassWord: mMainService is null....");
            return;
        }
        try {
            mMainService.Main_dealPassWord(keyID, devno, result);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return;
    }

    /**
     * 处理简易密码
     * @param devno     房号 (梯口机："0000" 区口机："000000000")
     * @param password  密码
     * @param type      住户密码:0x00 快递:0x01 外卖:0x02 其他:0x03
     */
    public void  DealOtherPassWord(String devno, String password, int type){
        if (null == mMainService) {
            Log.d(TAG, "DealOtherPassWord: mMainService is null....");
            return;
        }
        try {
            mMainService.Main_dealOtherPassWord(devno, password, type);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return;
    }
}
