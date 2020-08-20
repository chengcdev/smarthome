package com.mili.smarthome.tkj.set.fragment;


import android.widget.TextView;

import com.android.CommSysDef;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.dao.param.SnapParamDao;
import com.mili.smarthome.tkj.main.entity.SettingModel;
import com.mili.smarthome.tkj.main.fragment.BaseKeyBoardFragment;
import com.mili.smarthome.tkj.main.interf.ISetCallBackListener;
import com.mili.smarthome.tkj.main.widget.KeyBoardRecyclerView;
import com.mili.smarthome.tkj.main.widget.SetSuccessView;
import com.mili.smarthome.tkj.set.Constant;
import com.mili.smarthome.tkj.utils.AppManage;

import java.util.List;

/**
 * 拍照参数
 */

public class SetPhotoParamFragment extends BaseKeyBoardFragment implements ISetCallBackListener {


    private TextView tvTitle;
    private KeyBoardRecyclerView rv;
    private SetSuccessView successView;
    private List<SettingModel> mList;
    private String photoId;

    @Override
    public int getLayout() {
        return R.layout.fragment_setting;
    }

    @Override
    public void setKeyBoard(int viewId, String keyId) {

    }

    @Override
    public void initView() {
        rv = (KeyBoardRecyclerView) getContentView().findViewById(R.id.rv);
        tvTitle = (TextView) getContentView().findViewById(R.id.tv_title);
        successView = (SetSuccessView) getContentView().findViewById(R.id.root);
    }

    @Override
    public void initAdapter() {
        mList = Constant.getSetPhotoParamList();
        tvTitle.setText(getString(R.string.setting_photo_param));
        rv.initAdapter(mList);
        rv.setItemPosition(0);
    }


    @Override
    public void initListener() {
        successView.setSuccessListener(this);
    }


    @Override
    public void onKeyBoard(int viewId, String kid) {
        SettingModel settingModel = mList.get(rv.getItemPosition());
        switch (kid) {
            case Constant.KEY_CANCLE:
                if (settingModel.getkId().equals(Constant.SettinSeniorId.SENIOR_ENABLE_NOT) || settingModel.getkId().equals(Constant.SettinSeniorId.SENIOR_ENABLE)) {
                    initAdapter();
                } else {
                    exitFragment(this);
                }
                break;
            case Constant.KEY_UP:
                rv.preScroll();
                break;
            case Constant.KEY_DELETE:
                break;
            case Constant.KEY_NEXT:
                rv.nextScroll();
                break;
            case Constant.KEY_CONFIRM:
                int param = 0;
                if (settingModel.getkId().equals(Constant.SettinSeniorId.SENIOR_ENABLE_NOT)) {
                    //设置不启用
                    saveDatas(0);
                    successView.showSuccessView(getString(R.string.setting_success));
                } else if (settingModel.getkId().equals(Constant.SettinSeniorId.SENIOR_ENABLE)) {
                    //设置启用
                    saveDatas(1);
                    successView.showSuccessView(getString(R.string.setting_success));
                } else {
                    switch (settingModel.getkId()) {
                        case Constant.SetPhotoParamId.SET_PHOTO_VISITOR:
                            tvTitle.setText(R.string.setting_senior_camera_param);
                            param = SnapParamDao.getVisitorSnap();
                            break;
                        case Constant.SetPhotoParamId.SET_PHOTO_ERR_PWD:
                            tvTitle.setText(R.string.setting_senior_camera_error);
                            param = SnapParamDao.getErrorPwdSnap();
                            break;
                        case Constant.SetPhotoParamId.SET_PHOTO_HOLD_PWD:
                            tvTitle.setText(R.string.setting_senior_camera_xc);
                            param = SnapParamDao.getHijackPwdSnap();
                            break;
                        case Constant.SetPhotoParamId.SET_PHOTO_CALL_CENTER:
                            tvTitle.setText(R.string.setting_call_center_photo);
                            param = SnapParamDao.getCallCenterSnap();
                            break;
                        case Constant.SetPhotoParamId.SET_PHOTO_FACE_OPEN:
                            tvTitle.setText(R.string.setting_face_photo);
                            param = SnapParamDao.getFaceOpenSnap();
                            break;
                        case Constant.SetPhotoParamId.SET_PHOTO_FINGER_OPEN:
                            tvTitle.setText(R.string.setting_finger_photo);
                            param = SnapParamDao.getFingerOpenSnap();
                            break;
                        case Constant.SetPhotoParamId.SET_PHOTO_CARD_OPEN:
                            tvTitle.setText(R.string.setting_card_photo);
                            param = SnapParamDao.getCardOpenSnap();
                            break;
                        case Constant.SetPhotoParamId.SET_PHOTO_PWD_OPEN:
                            tvTitle.setText(R.string.setting_pwd_photo);
                            param = SnapParamDao.getPwdOpenSnap();
                            break;
                        case Constant.SetPhotoParamId.SET_QR_CODE_OPEN:
                            tvTitle.setText(R.string.setting_qr_photo);
                            param = SnapParamDao.getQrcodeOpenSnap();
                            break;
                    }
                    photoId = settingModel.getkId();
                    mList = Constant.getEnableList();
                    rv.initAdapter(mList);
                    rv.setItemPosition(param);
                }

                break;
        }
    }

    @Override
    public void success() {
        initAdapter();
    }

    @Override
    public void fail() {

    }

    private void saveDatas(int param) {
        if (photoId == null) {
            return;
        }
        switch (photoId) {
            case Constant.SetPhotoParamId.SET_PHOTO_VISITOR:
                SnapParamDao.setVisitorSnap(param);
                break;
            case Constant.SetPhotoParamId.SET_PHOTO_ERR_PWD:
                SnapParamDao.setErrorPwdSnap(param);
                break;
            case Constant.SetPhotoParamId.SET_PHOTO_HOLD_PWD:
                SnapParamDao.setHijackPwdSnap(param);
                break;
            case Constant.SetPhotoParamId.SET_PHOTO_CALL_CENTER:
                SnapParamDao.setCallCenterSnap(param);
                break;
            case Constant.SetPhotoParamId.SET_PHOTO_FACE_OPEN:
                SnapParamDao.setFaceOpenSnap(param);
                break;
            case Constant.SetPhotoParamId.SET_PHOTO_FINGER_OPEN:
                SnapParamDao.setFingerOpenSnap(param);
                break;
            case Constant.SetPhotoParamId.SET_PHOTO_CARD_OPEN:
                SnapParamDao.setCardOpenSnap(param);
                break;
            case Constant.SetPhotoParamId.SET_PHOTO_PWD_OPEN:
                SnapParamDao.setPwdOpenSnap(param);
                break;
            case Constant.SetPhotoParamId.SET_QR_CODE_OPEN:
                SnapParamDao.setQrcodeOpenSnap(param);
                break;
        }
        AppManage.getInstance().sendReceiver(CommSysDef.BROADCAST_CAMERAPARAM);
    }

}
