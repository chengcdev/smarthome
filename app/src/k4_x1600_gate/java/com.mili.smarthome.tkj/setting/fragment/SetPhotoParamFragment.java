package com.mili.smarthome.tkj.setting.fragment;

import android.content.Intent;
import android.os.Bundle;

import com.android.CommSysDef;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.App;
import com.mili.smarthome.tkj.dao.param.SnapParamDao;
import com.mili.smarthome.tkj.setting.entities.SettingFunc;

public class SetPhotoParamFragment extends ItemSelectorFragment {

    private String mFuncCode;

    @Override
    public boolean onKeyCancel() {
        super.onKeyCancel();
        exitFragment();
        return true;
    }

    @Override
    public boolean onKeyConfirm() {
        super.onKeyConfirm();
        int position = getSelection();
        onItemClick(position);
        return true;
    }

    @Override
    protected String[] getStringArray() {
        return mContext.getResources().getStringArray(R.array.setting_enabled);
    }

    @Override
    protected void bindData() {
        super.bindData();

        Bundle args = getArguments();
        if (args != null) {
            mFuncCode = args.getString(FragmentFactory.ARGS_FUNCCODE, SettingFunc.SET_PROMPT_TONE);
            String head = SettingFunc.getNameByCode(mFuncCode);
            setHead(head);
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
        }
        showSetHint(R.string.set_success);

        //发送广播
        Intent intent = new Intent(CommSysDef.BROADCAST_CAMERAPARAM);
        App.getInstance().sendBroadcast(intent);
    }
}
