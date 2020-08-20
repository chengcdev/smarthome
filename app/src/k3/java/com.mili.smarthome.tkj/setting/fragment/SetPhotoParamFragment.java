package com.mili.smarthome.tkj.setting.fragment;

import android.os.Bundle;

import com.android.CommSysDef;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.ContextProxy;
import com.mili.smarthome.tkj.dao.param.SnapParamDao;
import com.mili.smarthome.tkj.setting.entities.SettingFunc;

/**
 * 拍照参数
 * <p>{@link com.mili.smarthome.tkj.setting.entities.SettingFunc.SET_PHOTO_VISITOR}: 启用访客拍照
 * <p>{@link com.mili.smarthome.tkj.setting.entities.SettingFunc.SET_PHOTO_ERR_PWD}: 错误密码开门拍照
 * <p>{@link com.mili.smarthome.tkj.setting.entities.SettingFunc.SET_PHOTO_HOLD_PWD}: 挟持密码开门拍照
 * <p>{@link com.mili.smarthome.tkj.setting.entities.SettingFunc.SET_PHOTO_CALL_CENTER}: 呼叫中心拍照
 * <p>{@link com.mili.smarthome.tkj.setting.entities.SettingFunc.SET_PHOTO_FACE_OPEN}: 人脸开门拍照
 * <p>{@link com.mili.smarthome.tkj.setting.entities.SettingFunc.SET_PHOTO_FINGER_OPEN}: 指纹开门拍照
 * <p>{@link com.mili.smarthome.tkj.setting.entities.SettingFunc.SET_PHOTO_CARD_OPEN}: 刷卡开门拍照
 * <p>{@link com.mili.smarthome.tkj.setting.entities.SettingFunc.SET_PHOTO_PWD_OPEN}: 密码开门拍照
 * <p>{@link com.mili.smarthome.tkj.setting.entities.SettingFunc.SET_PHOTO_QRCODE_OPEN}: 扫码开门抓拍
 * <p>{@link com.mili.smarthome.tkj.setting.entities.SettingFunc.SET_PHOTO_FACE_STRANGER}: 陌生人人脸抓拍
 */
public class SetPhotoParamFragment extends ItemSelectorFragment {

    private String mFuncCode;

    @Override
    protected String[] getStringArray() {
        return new String[] {
                mContext.getString(R.string.setting_disable),
                mContext.getString(R.string.setting_enable)
        };
    }

    @Override
    protected void bindData() {
        super.bindData();
        Bundle args = getArguments();
        if (args != null) {
            mFuncCode = args.getString(FragmentFactory.ARGS_FUNCCODE, SettingFunc.SET_PHOTO_VISITOR);
        }
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
            case SettingFunc.SET_PHOTO_QRCODE_OPEN:
                setSelection(SnapParamDao.getQrcodeOpenSnap());
                break;
            //陌生人人脸拍照
            case SettingFunc.SET_PHOTO_FACE_STRANGER:
                setSelection(SnapParamDao.getFaceStrangerSnap());
                break;
        }
    }

    @Override
    public void onItemClick(int position) {
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
            case SettingFunc.SET_PHOTO_QRCODE_OPEN:
                SnapParamDao.setQrcodeOpenSnap(position);
                break;
            //陌生人人脸拍照
            case SettingFunc.SET_PHOTO_FACE_STRANGER:
                SnapParamDao.setFaceStrangerSnap(position);
                break;
            default:
                return;
        }

        //发送广播
        ContextProxy.sendBroadcast(CommSysDef.BROADCAST_CAMERAPARAM);

        showResultAndBack(R.string.setting_suc);
    }
}
