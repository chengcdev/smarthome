package com.mili.smarthome.tkj.set.fragment;


import android.os.Bundle;
import android.widget.TextView;

import com.android.CommSysDef;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.main.entity.SettingModel;
import com.mili.smarthome.tkj.main.fragment.BaseKeyBoardFragment;
import com.mili.smarthome.tkj.main.interf.ISetCallBackListener;
import com.mili.smarthome.tkj.main.widget.KeyBoardRecyclerView;
import com.mili.smarthome.tkj.main.widget.SetSuccessView;
import com.mili.smarthome.tkj.set.Constant;
import com.mili.smarthome.tkj.utils.AppManage;

import java.util.List;

/**
 * 人脸识别设置
 */

public class SetFaceFragment extends BaseKeyBoardFragment {
    private TextView tvTitle;
    private KeyBoardRecyclerView rv;
    private List<SettingModel> mLists;
    private SetSuccessView successView;
    private int currentSafeLevel;

    @Override
    public int getLayout() {
        return R.layout.fragment_setting;
    }

    @Override
    public void initView() {
        rv = (KeyBoardRecyclerView) getContentView().findViewById(R.id.rv);
        tvTitle = (TextView) getContentView().findViewById(R.id.tv_title);
        successView = (SetSuccessView) getContentView().findViewById(R.id.root);

    }

    @Override
    public void initAdapter() {
        int selecPosition;

        tvTitle.setText(getString(R.string.setting_face));
        //不启用人脸识别，清空人脸记录,安全级别,人脸活体检测三项不显示
        if (AppConfig.getInstance().getFaceRecognition() == 0) {
            mLists = Constant.getFaceList2();
            selecPosition = 0;
        } else {
            mLists = Constant.getFaceList1();
            selecPosition = 1;
        }

        rv.initAdapter(mLists);
        rv.setItemPosition(selecPosition);
    }


    @Override
    public void initListener() {

    }

    @Override
    public void setKeyBoard(int viewId, String keyId) {
        String sId = mLists.get(rv.getItemPosition()).getkId();
        String title = tvTitle.getText().toString();
        switch (keyId) {
            case Constant.KEY_CANCLE:
                toCancle(sId,title);
                break;
            case Constant.KEY_CONFIRM:
                toFragment(sId, title);
                break;
            case Constant.KEY_UP:
                rv.preScroll();
                break;
            case Constant.KEY_DELETE:

                break;
            case Constant.KEY_NEXT:
                rv.nextScroll();
                break;
        }
    }


    private void toFragment(String sId, String title) {
        switch (sId) {
            //启用
            case Constant.SetEntranceGuardId.GUARD_ENABLE:
                if (getString(R.string.setting_face_living).equals(title)) {
                    AppConfig.getInstance().setFaceLiveCheck(1);
                } else {
                    AppConfig.getInstance().setFaceRecognition(1);
                    //人体感应，人脸识别启用
                    AppConfig.getInstance().setBodyInduction(1);
                }
                setSuccess(sId,title);
                break;
            //禁用
            case Constant.SetEntranceGuardId.GUARD_BAN:
                if (getString(R.string.setting_face_living).equals(title)) {
                    AppConfig.getInstance().setFaceLiveCheck(0);
                } else {
                    AppConfig.getInstance().setFaceRecognition(0);
                    //人体感应，人脸识别启用
                    AppConfig.getInstance().setBodyInduction(0);
                }
                setSuccess(sId,title);
                break;
            //清空人脸记录
            case Constant.SetEntranceGuardId.GUARD_FACE_CLEAR:
                SetClearFragment setClearFragment = new SetClearFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Constant.KEY_PARAM, Constant.ClearId.CLEAR_FACE);
                AppManage.getInstance().replaceFragment(getActivity(),setClearFragment,bundle);
                initAdapter();
                break;
            //安全级别
            case Constant.SetEntranceGuardId.GUARD_SAFE_LEVEL:
                int faceSafeLevel = AppConfig.getInstance().getFaceSafeLevel();
                mLists = Constant.getSecuryLevelList();
                rv.initAdapter(mLists);
                rv.setItemPosition(faceSafeLevel);
                tvTitle.setText(getString(R.string.setting_secury_level));
                break;
            //人脸活体检测
            case Constant.SetEntranceGuardId.GUARD_FACE_LIVING:
                int faceLiveCheck = AppConfig.getInstance().getFaceLiveCheck();
                mLists = Constant.getSaomaList();
                rv.initAdapter(mLists);
                rv.setItemPosition(faceLiveCheck);
                tvTitle.setText(getString(R.string.setting_face_living));
                break;
            //人脸识别阈值
            case Constant.SetEntranceGuardId.GUARD_FACE_THRESHOLD:

                break;
            //高
            case Constant.SetEntranceGuardId.GUARD_LEVLE_HIGH:
                currentSafeLevel = 0;
                AppConfig.getInstance().setFaceSafeLevel(currentSafeLevel);
                setSuccess(sId,title);
                break;
            //正常
            case Constant.SetEntranceGuardId.GUARD_LEVLE_NORMAL:
                currentSafeLevel = 1;
                AppConfig.getInstance().setFaceSafeLevel(currentSafeLevel);
                setSuccess(sId,title);
                break;
            //普通
            case Constant.SetEntranceGuardId.GUARD_LEVLE_ORDINARY:
                currentSafeLevel = 2;
                AppConfig.getInstance().setFaceSafeLevel(currentSafeLevel);
                setSuccess(sId,title);
                break;
        }
    }

    private void toCancle(String sId,String title) {
        switch (sId) {
            //启用
            case Constant.SetEntranceGuardId.GUARD_ENABLE:
                //禁用
            case Constant.SetEntranceGuardId.GUARD_BAN:
                if (getString(R.string.setting_face_living).equals(title)) {
                    initAdapter();
                } else {
                    exitFragment(this);
                }
                break;
            //清空人脸记录
            case Constant.SetEntranceGuardId.GUARD_FACE_CLEAR:
                //安全级别
            case Constant.SetEntranceGuardId.GUARD_SAFE_LEVEL:
                //人脸活体检测
            case Constant.SetEntranceGuardId.GUARD_FACE_LIVING:
                exitFragment(this);
                break;
                //高
            case Constant.SetEntranceGuardId.GUARD_LEVLE_HIGH:
                //正常
            case Constant.SetEntranceGuardId.GUARD_LEVLE_NORMAL:
                //普通
            case Constant.SetEntranceGuardId.GUARD_LEVLE_ORDINARY:
                initAdapter();
                break;
        }
    }

    private void setSuccess(final String sId, final String title) {
        successView.showSuccessView(getString(R.string.setting_success), 2000, new ISetCallBackListener() {
            @Override
            public void success() {
                if (isAdded()) {
                    switch (sId) {
                        case Constant.SetEntranceGuardId.GUARD_ENABLE:
                        case Constant.SetEntranceGuardId.GUARD_BAN:
                            AppManage.getInstance().sendReceiver(CommSysDef.BROADCAST_ENABLE_FACE);
                            if (getString(R.string.setting_face_living).equals(title)) {
                                initAdapter();
                            } else {
                                exitFragment(SetFaceFragment.this);
                            }
                            break;
                        default:
                            initAdapter();
                            break;
                    }
                }
            }

            @Override
            public void fail() {

            }
        });
    }

    /**
     * 获取安全等级的指向
     *
     * @param level 数据库保存的等级
     * @return 列表的指向
     */
    private int getSafeLevelDirecCount(int level) {
        switch (level) {
            case 0:
                return 2;
            case 1:
                return 1;
            case 2:
                return 0;
        }
        return 0;
    }
}
