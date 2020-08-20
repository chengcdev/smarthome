package com.mili.smarthome.tkj.setting.fragment;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.android.CommTypeDef;
import com.android.Common;
import com.android.provider.FullDeviceNo;
import com.google.gson.reflect.TypeToken;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.auth.AuthManage;
import com.mili.smarthome.tkj.base.FragmentProxy;
import com.mili.smarthome.tkj.base.K4BaseFragment;
import com.mili.smarthome.tkj.base.KeyboardCtrl;
import com.mili.smarthome.tkj.base.KeyboardProxy;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;
import com.mili.smarthome.tkj.setting.entities.SettingFunc;
import com.mili.smarthome.tkj.setting.view.SetListView;
import com.mili.smarthome.tkj.utils.IOUtils;
import com.mili.smarthome.tkj.utils.JsonUtils;

import java.util.List;

public class SettingFragment extends K4BaseFragment implements AdapterView.OnItemClickListener{

    private static final String Tag = "SettingFragment";
    private SetListView myListView;
    KeyboardCtrl keyboardUtil;
    private List<SettingFunc> mDataList;
    private int mSetType = 0;
    private boolean mShowFragment = false;
    private boolean mIsApn = false;

    @Override
    public boolean onKeyCancel() {
        handleBack();
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_setting;
    }

    @Override
    protected void bindView() {
        super.bindView();
        myListView = findView(R.id.lv_content);
        if (myListView != null) {
            myListView.setOnItemClickListener(this);
        }
        keyboardUtil = findView(R.id.keyboard);
    }

    @Override
    protected void bindData() {
        if (keyboardUtil != null) {
            keyboardUtil.setMode(KeyboardCtrl.KEYMODE_SET);
            KeyboardProxy.getInstance().setKeyboard(keyboardUtil);
        }
        super.bindData();

        String jsonString = IOUtils.readFromAssets(mContext, "setting_func.json");
        mDataList = JsonUtils.fromJson(jsonString, new TypeToken<List<SettingFunc>>(){}.getType());

        // 门禁设置中人脸识别隐藏不显示
        boolean faceEnable = true;
        if (!AuthManage.isAuth() || AppConfig.getInstance().getFaceModule() != 1) {
            if (mDataList != null && mDataList.size() > 2) {
                SettingFunc func = mDataList.get(2);
                func.getChild().remove(2);
                faceEnable = false;
            }
        }

        // 门禁设置中指纹识隐藏不显示
        if (!SinglechipClientProxy.getInstance().isFingerWork()) {
            if (faceEnable) {
                mDataList.get(2).getChild().remove(4);
            } else {
                mDataList.get(2).getChild().remove(3);
            }
        }

        // 门禁设置中屏蔽APN设置
        if (mDataList != null && mDataList.size() > 2) {
            int lastIndex = mDataList.get(2).getChild().size() - 1;
            mDataList.get(2).getChild().remove(lastIndex);
        }

        //区口机时系统设置无住户设置，且梯口号设置改完区口号设置
        FullDeviceNo fullDeviceNo = new FullDeviceNo(getContext());
        int deviceType = fullDeviceNo.getDeviceType();
        if (deviceType == CommTypeDef.DeviceType.DEVICE_TYPE_AREA) {
            if (mDataList != null && mDataList.size() > 3) {
                SettingFunc func = mDataList.get(3);
                func.getChild().get(0).setCode("040101");
                func.getChild().remove(3);
            }
        }

        // 未插SD卡时 不显示SD卡容量选项
        if (!Common.hasExternalSdCard()) {
            if (mDataList != null && mDataList.size() > 4) {
                if (deviceType == CommTypeDef.DeviceType.DEVICE_TYPE_AREA) {
                    SettingFunc func = mDataList.get(3).getChild().get(5);
                    func.getChild().remove(1);
                } else {
                    SettingFunc func = mDataList.get(3).getChild().get(6);
                    func.getChild().remove(1);
                }
            }
        }

        if (mDataList != null && mSetType < mDataList.size()) {
            if (mSetType == 1 && AppConfig.getInstance().getPwdDoorMode() == 1) {
                // 若高级秘密模式，则上一级为卡管理
                showMenu(mDataList.get(0), true);
            } else {
                showMenu(mDataList.get(mSetType), true);
            }
        }
        Log.d(Tag, "bindData: settype is " + mSetType);
    }

    @Override
    protected void unbindView() {
        super.unbindView();
        mDataList.clear();
        mShowFragment = false;
        Log.d(Tag, " ======== unbindView =========");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(Tag, "======== onResume ========");
        myListView.setVisibility(View.VISIBLE);
        FragmentProxy.getInstance().setFragmentListener(mSetFragmentListener);
        FragmentProxy.getInstance().setFragmentManager(getChildFragmentManager());
        FragmentProxy.getInstance().exitFragmentAll();
        if (mIsApn) {
            mIsApn = false;
            setKeboardListener();
        }

        if (mDataList != null && mSetType < mDataList.size()) {
            //高级密码时只显示修改管理密码
            if (mSetType == 1 && AppConfig.getInstance().getPwdDoorMode() == 1) {
                SettingFunc func = mDataList.get(mSetType);
                int childCount = func.getChild().size();
                SettingFunc adminFunc = func.getChild().get(childCount-1);
                showFragment(adminFunc);
            }
        }
    }

    /**
     * 抓拍列表中判断是否显示功能
     * @param funcCode  功能码
     * @return          true/false
     */
    private boolean photoState(String funcCode) {
        switch (funcCode) {
            case SettingFunc.SET_PHOTO_FACE_OPEN:
                if (!AppConfig.getInstance().isFaceEnabled()) {
                    return false;
                }
                break;
            case SettingFunc.SET_PHOTO_FINGER_OPEN:
                if (!AppConfig.getInstance().isFingerEnabled()) {
                    return false;
                }
                break;
            case SettingFunc.SET_PHOTO_QRCODE_OPEN:
                int openType = AppConfig.getInstance().getQrOpenType();
                if (openType == 0) {
                    if (AppConfig.getInstance().getQrScanEnabled() == 0) {
                        return false;
                    }
                } else {
                    String registerId = AppConfig.getInstance().getBluetoothDevId();
                    if (registerId != null && registerId.length() == 0) {
                        return false;
                    }
                }
                break;
        }
        return true;
    }

    /**
     *  拍照参数动态变化
     */
    private void showPhotoParam() {
        if (mDataList != null && mDataList.size() > 4) {
            SettingFunc newFunc = new SettingFunc();
            newFunc.setCode(mDataList.get(4).getCode());
            newFunc.setName(mDataList.get(4).getName());
            for (SettingFunc child : mDataList.get(4).getChild()) {
                newFunc.addChild(child);
            }

            SettingFunc childFunc = new SettingFunc();
            childFunc.setCode(newFunc.getChild().get(2).getCode());
            childFunc.setName(newFunc.getChild().get(2).getName());
            List<SettingFunc> childList = newFunc.getChild().get(2).getChild();
            for (SettingFunc child: childList) {
                if (photoState(child.getCode())) {
                    childFunc.addChild(child);
                }
            }
            newFunc.getChild().set(2, childFunc);
            showMenu(newFunc, true);
        }
    }

    public void resetFunc(int type) {
        if (myListView != null) {
            myListView.setVisibility(View.VISIBLE);
        }
        if (mShowFragment) {
            mShowFragment = false;
            FragmentProxy.getInstance().exitFragmentAll();
        }

        mSetType = type;
        if (mDataList != null && mSetType < mDataList.size()) {
            //高级密码时只显示修改管理密码
            if (mSetType == 1 && AppConfig.getInstance().getPwdDoorMode() == 1) {
                SettingFunc func = mDataList.get(mSetType);
                int childCount = func.getChild().size();
                SettingFunc adminFunc = func.getChild().get(childCount-1);
                showFragment(adminFunc);
//                SettingFunc func = mDataList.get(mSetType);
//                int childCount = func.getChild().size();
//                SettingFunc newFunc = new SettingFunc();
//                newFunc.setCode(func.getCode());
//                newFunc.setName(func.getName());
//                newFunc.addChild(func.getChild().get(childCount-1));
                return;
            }

            // 重新更新拍照参数
            if (mSetType == 4) {
                showPhotoParam();
                return;
            }
            showMenu(mDataList.get(mSetType), true);
        }
    }

    public void handleBack() {
        Log.d(Tag, "handleBack");
        if (myListView != null) {
            SettingFunc func = myListView.getParentItem();
            if (func == null) {
                requestBack();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Log.d(Tag, "id is" + view.getId() + ",pos is " + position + ",id is" +id);

        SettingFunc func = myListView.getItem();
        if (func == null || !func.hasChild()) {
            Log.d(Tag, "onItemClick: list is null.");
            return;
        }

        int index = (int)id;
        if (index < 0 || index >= func.getChild().size()) {
            Log.d(Tag, "onItemClick: id is out of range, " + id);
            return;
        }

        SettingFunc childFunc = func.getChild().get(index);
        if (childFunc.hasChild()) {
            showMenu(childFunc, false);
        } else {
            showFragment(childFunc);
        }
    }

    private void showMenu(SettingFunc func, boolean root) {
        if (myListView != null) {
            if (root) {
                myListView.resetData(func);
            } else {
                myListView.putItem(func);
            }
        }
    }

    private void showFragment(SettingFunc func) {
        mIsApn = false;
        if (func.getCode().equals(SettingFunc.SET_APN)) {
            mIsApn = true;
            startActivity(new Intent(android.provider.Settings.ACTION_APN_SETTINGS));
            return;
        }
        K4BaseFragment fragment = FragmentFactory.create(func.getCode());
        if (fragment != null) {
            mShowFragment = true;
            FragmentProxy.getInstance().showFragment(fragment);
            myListView.setVisibility(View.INVISIBLE);
        }
    }

    private void exitSetFragment() {
        FragmentProxy.getInstance().exitFragment();
        myListView.setVisibility(View.VISIBLE);
    }

    private FragmentProxy.FragmentListener mSetFragmentListener = new FragmentProxy.FragmentListener() {
        @Override
        public void onExitFragment() {
            exitSetFragment();
            if (myListView != null) {
                myListView.freshData();
            }
        }

        @Override
        public void setClickable(boolean clickable) {

        }
    };
}
