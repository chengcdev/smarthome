package com.mili.smarthome.tkj.setting.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.android.CommSysDef;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.ContextProxy;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.appfunc.BuildConfigHelper;
import com.mili.smarthome.tkj.setting.entities.SettingFunc;
import com.mili.smarthome.tkj.setting.entities.SettingFuncManager;
import com.mili.smarthome.tkj.utils.FragmentUtils;

public class SetFaceFragment extends ItemSelectorFragment {

    @Override
    protected String[] getStringArray() {
        int faceRecognition = AppConfig.getInstance().getFaceRecognition();
        if (faceRecognition == 0) {
            return new String[] {
                    mContext.getString(R.string.pub_disable),
                    mContext.getString(R.string.pub_enable)
            };
        } else {
            if (BuildConfigHelper.isEnabledIPC()) {
                return new String[] {
                        mContext.getString(R.string.pub_disable),
                        mContext.getString(R.string.pub_enable),
                        mContext.getString(R.string.setting_030301),
                        mContext.getString(R.string.setting_030302),
                        mContext.getString(R.string.setting_030303),
                        mContext.getString(R.string.setting_030304)
                };
            } else {
                return new String[] {
                        mContext.getString(R.string.pub_disable),
                        mContext.getString(R.string.pub_enable),
                        mContext.getString(R.string.setting_030301),
                        mContext.getString(R.string.setting_030302),
                        mContext.getString(R.string.setting_030303)
                };
            }
        }
    }

    @Override
    protected void bindData() {
        super.bindData();
        setSelection(AppConfig.getInstance().getFaceRecognition());
    }

    @Override
    public void onItemClick(int position) {
        String funcCode;
        switch (position) {
            case 0: // 禁用
            case 1: // 启用
                AppConfig.getInstance().setFaceRecognition(position);
                //发送广播
                ContextProxy.sendBroadcast(CommSysDef.BROADCAST_ENABLE_FACE);
                //
                showResult(R.string.setting_suc, new Runnable() {
                    @Override
                    public void run() {
                        SettingFuncManager.notifyFaceRecogChanged();
                        requestBack();
                    }
                });
                return;
            case 2: // 清空人脸记录
                funcCode = SettingFunc.SET_FACE_CLEAR;
                break;
            case 3: // 安全级别
                funcCode = SettingFunc.SET_FACE_SECU_LEVEL;
                break;
            case 4: // 人脸活体检测
                funcCode = SettingFunc.SET_FACE_LIVENESS;
                break;
            case 5: // IPCamera地址
                funcCode = SettingFunc.SET_IPC_URL;
                break;
            default:
                return;
        }
        FragmentManager fm = getFragmentManager();
        Fragment fragment = FragmentFactory.create(funcCode);
        if (fm == null || fragment == null) {
            return;
        }
        FragmentUtils.replace(fm, R.id.fl_container, fragment, true);
    }
}
