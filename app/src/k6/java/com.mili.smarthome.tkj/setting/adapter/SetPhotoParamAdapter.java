package com.mili.smarthome.tkj.setting.adapter;

import android.content.Context;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.dao.param.SnapParamDao;
import com.mili.smarthome.tkj.setting.entities.SettingFunc;
import com.mili.smarthome.tkj.setting.interf.IOnItemClickListener;

/**
 * 拍照参数
 * 启用访客拍照
 * 错误密码开门拍照
 * 挟持密码开门拍照
 */
public class SetPhotoParamAdapter extends ItemSelectorAdapter {

    private IOnItemClickListener itemClickListener;
    private String mFuncCode;

    public SetPhotoParamAdapter(Context context, String funcCode) {
        super(context);
        mFuncCode = funcCode;
        setSelection(1);

    }

    public SetPhotoParamAdapter(Context context, String mFuncCode, IOnItemClickListener itemClickListener) {
        super(context);
        this.mFuncCode = mFuncCode;
        this.itemClickListener = itemClickListener;
        switch (mFuncCode) {
            //启用访客拍照
            case SettingFunc.SET_PHOTO_VISITOR:
                setSelection(SnapParamDao.getVisitorSnap());
                break;
            //错误密码开门拍照
            case SettingFunc.SET_PHOTO_ERR_PWD:
                setSelection(SnapParamDao.getErrorPwdSnap());
                break;
            //挟持密码开门拍照
            case SettingFunc.SET_PHOTO_HOLD_PWD:
                setSelection(SnapParamDao.getHijackPwdSnap());
                break;
            //呼叫中心拍照
            case SettingFunc.SET_PHOTO_CALL_CENTER:
                setSelection(SnapParamDao.getCallCenterSnap());
                break;
            //人脸开门拍照
            case SettingFunc.SET_PHOTO_FACE_OPEN:
                setSelection(SnapParamDao.getFaceOpenSnap());
                break;
            //指纹开门拍照
            case SettingFunc.SET_PHOTO_FINGER_OPEN:
                setSelection(SnapParamDao.getFingerOpenSnap());
                break;
            //刷卡开门拍照
            case SettingFunc.SET_PHOTO_CARD_OPEN:
                setSelection(SnapParamDao.getCardOpenSnap());
                break;
            //密码开门拍照
            case SettingFunc.SET_PHOTO_PWD_OPEN:
                setSelection(SnapParamDao.getPwdOpenSnap());
                break;
            //扫码开门拍照
            case SettingFunc.SET_QR_CODE_OPEN:
                setSelection(SnapParamDao.getQrcodeOpenSnap());
                break;
            //陌生人脸拍照
            case SettingFunc.SET_PHOTO_FACE_STRANGER:
                setSelection(SnapParamDao.getFaceStrangerSnap());
                break;
        }
    }

    @Override
    protected int getStringArrayId() {
        return R.array.setting_enabled;
    }

    @Override
    protected void onItemClick(int position) {
        switch (mFuncCode) {
            //启用访客拍照
            case SettingFunc.SET_PHOTO_VISITOR:
                SnapParamDao.setVisitorSnap(position);
                break;
            //错误密码开门拍照
            case SettingFunc.SET_PHOTO_ERR_PWD:
                SnapParamDao.setErrorPwdSnap(position);
                break;
            //挟持密码开门拍照
            case SettingFunc.SET_PHOTO_HOLD_PWD:
                SnapParamDao.setHijackPwdSnap(position);
                break;
            //呼叫中心拍照
            case SettingFunc.SET_PHOTO_CALL_CENTER:
                SnapParamDao.setCallCenterSnap(position);
                break;
            //人脸开门拍照
            case SettingFunc.SET_PHOTO_FACE_OPEN:
                SnapParamDao.setFaceOpenSnap(position);
                break;
            //指纹开门拍照
            case SettingFunc.SET_PHOTO_FINGER_OPEN:
                SnapParamDao.setFingerOpenSnap(position);
                break;
            //刷卡开门拍照
            case SettingFunc.SET_PHOTO_CARD_OPEN:
                SnapParamDao.setCardOpenSnap(position);
                break;
            //密码开门拍照
            case SettingFunc.SET_PHOTO_PWD_OPEN:
                SnapParamDao.setPwdOpenSnap(position);
                break;
            //扫码开门拍照
            case SettingFunc.SET_QR_CODE_OPEN:
                SnapParamDao.setQrcodeOpenSnap(position);
                break;
            //陌生人脸拍照
            case SettingFunc.SET_PHOTO_FACE_STRANGER:
                SnapParamDao.setFaceStrangerSnap(position);
                break;
        }
        if (itemClickListener != null) {
            itemClickListener.OnItemListener(position);
        }
    }
}
