package com.android.client;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

/**
 * Created by zhengxc on 2019/1/2 0002.
 */

public class CardClient extends BaseClient {

    private final static String TAG = "ScanQrClient";
    private Context mContext;

    private static final CardClient cardClient = new CardClient();

    public static CardClient getInstance() {
        return cardClient;
    }

    public CardClient() {
    }

    public CardClient(Context context) {
        super(context);
        mContext = context;
    }

    /*************************************************
     Function:    		AddCard
     Description:		添加卡
     Input:
     Return:		0x01: 添加成功
     0xF0: 添加失败
     Others:
     *************************************************/
    public int AddCard(String roomString, String cardString, int cardType, int roomNoState, String keyID, int startTime, int endTime, int lifecycle) {
        if (null == mMainService) {
            Log.d(TAG, "AddCard: mMainService is null....");
            return -1;
        }
        try {
            return mMainService.Main_addCard(roomString, cardString, cardType, roomNoState, keyID, startTime, endTime, lifecycle);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /*************************************************
     Function:    		DelCard
     Description:		删除卡
     Input:
     Return:	0x01 删除成功
     0xF0 无法删除
     Others:
     *************************************************/
    public int DelCard(String roomString, String cardString) {
        if (null == mMainService) {
            Log.d(TAG, "DelCard: mMainService is null....");
            return -1;
        }
        try {
            return mMainService.Main_delCard(roomString, cardString);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /*************************************************
     Function:    		DelUserCard
     Description:		删除某住户的所有卡
     Input:
     1.data	    数据: 房号4B
     2.datalen:   	长度
     Return:	        0x01 删除成功
     0xF0 无法删除
     Others:
     *************************************************/
    public int DelUserCard(String roomString) {
        if (null == mMainService) {
            Log.d(TAG, "DelUserCard: mMainService is null....");
            return -1;
        }
        try {
            return mMainService.Main_delUserCard(roomString);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /*************************************************
     Function:    		ClearCard
     Description:		清空卡
     Input:
     1.data			数据: 无
     2.datalen:   	长度
     Return:	0x01 清空成功
     0xF0 无法清空
     Others:
     *************************************************/
    public int ClearCard() {
        if (null == mMainService) {
            Log.d(TAG, "ClearCard: mMainService is null....");
            return -1;
        }
        try {
            return mMainService.Main_clearCard();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /*************************************************
     Function: 		GetCardCount
     Description:		获取卡个数
     Input:			无
     Output:			无
     Return:
     Others:
     *************************************************/
    public int GetCardCount() {
        if (null == mMainService) {
            Log.d(TAG, "GetCardCount: mMainService is null....");
            return -1;
        }
        try {
            return mMainService.Main_getCardCount();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /*************************************************
     Function: 		GetCardFreeCount
     Description:		获取剩余卡个数
     Input:			无
     Output:			无
     Return:			密码个数
     Others:
     *************************************************/
    public int GetCardFreeCount() {
        if (null == mMainService) {
            Log.d(TAG, "GetCardFreeCount: mMainService is null....");
            return -1;
        }
        try {
            return mMainService.Main_getCardFreeCount();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /*************************************************
     Function:    		setCardState
     Description:		设置卡处理状态
     Input:
     1.status	  	00:    处于刷卡进门状态
     01:    处于设置卡状态;
     02:    处于人脸刷卡编辑状态
     03:	   处于添加指纹状态
     04:	   处于修改密码状态
     Return:			无
     Others:
     *************************************************/
    public int SetCardState(int state) {
        if (null == mMainService) {
            Log.d(TAG, "SetCardState: mMainService is null....");
            return -1;
        }
        try {
            mMainService.Mian_setCardState(state);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

}
