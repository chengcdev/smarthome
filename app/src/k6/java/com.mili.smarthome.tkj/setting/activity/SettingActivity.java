package com.mili.smarthome.tkj.setting.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.CommTypeDef;
import com.android.provider.FullDeviceNo;
import com.google.gson.reflect.TypeToken;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.constant.Constant;
import com.mili.smarthome.tkj.main.activity.BaseMainActivity;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;
import com.mili.smarthome.tkj.setting.entities.SettingFunc;
import com.mili.smarthome.tkj.setting.fragment.DevInfoFragment;
import com.mili.smarthome.tkj.setting.fragment.SetAdminPwdFragment;
import com.mili.smarthome.tkj.setting.fragment.SettingFragment;
import com.mili.smarthome.tkj.utils.AppUtils;
import com.mili.smarthome.tkj.utils.IOUtils;
import com.mili.smarthome.tkj.utils.JsonUtils;
import com.mili.smarthome.tkj.utils.ViewUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SettingActivity extends BaseMainActivity implements View.OnClickListener {

    private SettingFragment fSetting;
    private DevInfoFragment fDevInfo;

    private Fragment fCurrent;
    private RecyclerView rvLeft;
    private TabAdapter tabAdapter;
    private SetAdminPwdFragment adminPwdFragment;
    private SettingReceiver settingReceiver;
    private List<SettingFunc> list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        regiseter();

        rvLeft = findView(R.id.rv_tab);
        rvLeft.setLayoutManager(new LinearLayoutManager(
                mContext, LinearLayoutManager.VERTICAL, false));
        findView(R.id.iv_back).setOnClickListener(this);


        fSetting = new SettingFragment();

        fragmentReplace(fSetting);

        initDatas();
    }

    private void regiseter() {
        settingReceiver = new SettingReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.Action.SETTING_NOTIFYCHANGE);
        registerReceiver(settingReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(settingReceiver);
        super.onDestroy();
    }

    public void initDatas() {
        String jsonString = IOUtils.readFromAssets(mContext, "setting_func.json");
        list = JsonUtils.fromJson(jsonString, new TypeToken<List<SettingFunc>>() {
        }.getType());

        setLeftList(list);
        setList(list);
        tabAdapter = new TabAdapter(list);
        rvLeft.setAdapter(tabAdapter);
    }

    public void notifyChange() {
        setLeftList(list);
        setList(list);
        tabAdapter.setList(list);
        tabAdapter.notifyDataSetChanged();
    }

    private void setList(List<SettingFunc> list) {

//        修改list数据，如果是区口号设置，如果是区口号把梯口号设置改成区口号设置，隐藏住户设置
        FullDeviceNo fullDeviceNo = new FullDeviceNo(this);
        byte deviceType = fullDeviceNo.getDeviceType();
        if (deviceType != CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
            for (int i = 0; i < list.size(); i++) {
                List<SettingFunc> childList = list.get(i).getChild();
                if (childList != null && childList.size() > 0) {
                    for (int j = 0; j < childList.size(); j++) {
                        String code = childList.get(j).getCode();
                        if (SettingFunc.SET_DEVNO.equals(code)) {
                            childList.get(j).setCode(SettingFunc.SET_AREA_NO);
                            childList.get(j).setName(getString(R.string.setting_area_no));
                        }
                        if (SettingFunc.SET_ROOM.equals(code)) {
                            childList.remove(j);
                        }
                    }
                }
            }
        }


        int qrOpenDoorType = AppConfig.getInstance().getQrOpenType();
        String registerId = AppConfig.getInstance().getBluetoothDevId();

//        修改list数据，人脸识别、扫码开门禁用，人体感应选项隐藏
        if ((AppConfig.getInstance().getFaceModule() ==0 || AppConfig.getInstance().getFaceRecognition() == 0) &&
                ((qrOpenDoorType == 0 && AppConfig.getInstance().getQrScanEnabled() == 0) ||
                (qrOpenDoorType == 1 && (registerId == null || registerId.equals(""))))) {
            for (int i = 0; i < list.size(); i++) {
                List<SettingFunc> childList = list.get(i).getChild();
                if (childList != null && childList.size() > 0) {
                    for (int j = 0; j < childList.size(); j++) {
                        String code = childList.get(j).getCode();
                        if (SettingFunc.SET_BODY_DETECTION.equals(code)) {
                            childList.remove(j);
                        }

                    }
                }
            }
        } else {
//            添加体感应选项
            List<String> codeList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                if (SettingFunc.SET_DOOR_GUARD.equals(list.get(i).getCode())) {
                    List<SettingFunc> childList = list.get(i).getChild();
                    if (childList != null && childList.size() > 0) {
                        for (SettingFunc settingFunc : childList) {
                            codeList.add(settingFunc.getCode());
                        }
                        if (!codeList.contains(SettingFunc.SET_BODY_DETECTION)) {
                            childList.add(new SettingFunc(SettingFunc.SET_BODY_DETECTION, getString(R.string.setting_0306)));
                        }
                        break;
                    }
                }

            }
        }

        //是否隐藏人脸识别选项
        if (AppConfig.getInstance().getFaceModule() == 0) {
            removePhotoItem(SettingFunc.SET_FACE_RECOGNITION);
        } else {
            addPhotoItem(SettingFunc.SET_FACE_RECOGNITION,getString(R.string.setting_0303));
        }

        //是否隐藏指纹选项
        if (!SinglechipClientProxy.getInstance().isFingerWork() || SinglechipClientProxy.getInstance().getFingerSurplus() == -1) {
            removePhotoItem(SettingFunc.SET_FINGERPRINT);
        } else {
            addPhotoItem(SettingFunc.SET_FINGERPRINT,getString(R.string.setting_0305));
        }

        //是否启用人脸识别
        if (AppConfig.getInstance().getFaceModule() == 1 && AppConfig.getInstance().getFaceRecognition() == 1) {
           addPhotoItem(SettingFunc.SET_PHOTO_FACE_OPEN,getString(R.string.setting_050305));
        } else {
            removePhotoItem(SettingFunc.SET_PHOTO_FACE_OPEN);
        }

        //是否启用指纹识别
        if (AppConfig.getInstance().getFingerprint() == 1) {
            addPhotoItem(SettingFunc.SET_PHOTO_FINGER_OPEN,getString(R.string.setting_050306));
        } else {
            removePhotoItem(SettingFunc.SET_PHOTO_FINGER_OPEN);
        }

        //是否启用扫码开门拍照识别
        if (AppConfig.getInstance().getQrScanEnabled() == 1) {
            addPhotoItem(SettingFunc.SET_QR_CODE_OPEN,getString(R.string.setting_050309));
        } else {
            removePhotoItem(SettingFunc.SET_QR_CODE_OPEN);
        }

        //是否启用陌生人脸抓拍
        if (AppConfig.getInstance().getFaceModule() == 1 && AppConfig.getInstance().getFaceRecognition() == 1) {
            addPhotoItem(SettingFunc.SET_PHOTO_FACE_STRANGER,getString(R.string.setting_050310));
        } else {
            removePhotoItem(SettingFunc.SET_PHOTO_FACE_STRANGER);
        }
    }

    private void setLeftList(List<SettingFunc> list) {
//        /** 修改list数据，高级密码模式，左边显示修改管理密码，否则显示密码管理*/
//        int pwdDoorModel = ParamDao.getPwdDoorMode();
//        if (pwdDoorModel == 1) {
//            for (int i = 0; i < list.size(); i++) {
//                String code = list.get(i).getCode();
//                if (SettingFunc.PWD_MANAGE.equals(code)) {
//                    list.get(i).setCode(SettingFunc.ADMIN_PWD_CHANGE);
////                    list.get(i).setName(getString(R.string.setting_02));
//                }
//            }
//        } else {
//            for (int i = 0; i < list.size(); i++) {
//                String code = list.get(i).getCode();
//                if (SettingFunc.ADMIN_PWD_CHANGE.equals(code)) {
//                    list.get(i).setCode(SettingFunc.PWD_MANAGE);
//                    list.get(i).setName(getString(R.string.setting_02));
//                }
//            }
//        }
    }

    private void removePhotoItem(String code) {
        for (int i = 0; i < list.size(); i++) {
            if (SettingFunc.SET_SENIOR.equals(list.get(i).getCode())) {
                List<SettingFunc> childList = list.get(i).getChild();
                for (int j = 0; j < childList.size(); j++) {
                    if (SettingFunc.SET_PHOTO_PARAM.equals(childList.get(j).getCode())) {
                        List<SettingFunc> child = childList.get(j).getChild();
                        if (child != null && child.size() > 0) {
                            for (int k = 0; k < child.size(); k++) {
                                if (code.equals(child.get(k).getCode())) {
                                    child.remove(k);
                                }
                            }
                        }
                    }
                }
            }
            if (SettingFunc.SET_DOOR_GUARD.equals(list.get(i).getCode())) {
                List<SettingFunc> childList = list.get(i).getChild();
                for (int j = 0; j < childList.size(); j++) {
                    String c = childList.get(j).getCode();
                    if (code.equals(c)) {
                        childList.remove(j);
                    }
                }
            }
        }
    }

    private void addPhotoItem(String code,String codeName) {
        List<String> codeList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (SettingFunc.SET_SENIOR.equals(list.get(i).getCode())) {
                List<SettingFunc> childList = list.get(i).getChild();
                for (int j = 0; j < childList.size(); j++) {
                    if (SettingFunc.SET_PHOTO_PARAM.equals(childList.get(j).getCode())) {
                        List<SettingFunc> child = childList.get(j).getChild();
                        if (child != null && child.size() > 0) {
                            for (SettingFunc settingFunc : child) {
                                codeList.add(settingFunc.getCode());
                            }
                            if (!codeList.contains(code)) {
                                child.add(new SettingFunc(code, codeName));
                                Collections.sort(child);
                            }
                        }
                    }
                }
            }else if (SettingFunc.SET_DOOR_GUARD.equals(list.get(i).getCode())) {
                List<SettingFunc> childList = list.get(i).getChild();
                if (childList != null && childList.size() > 0) {
                    for (SettingFunc settingFunc : childList) {
                        codeList.add(settingFunc.getCode());
                    }
                    if (!codeList.contains(SettingFunc.SET_FACE_RECOGNITION)) {
                        if (!code.equals(SettingFunc.SET_FINGERPRINT) && !code.equals(SettingFunc.SET_PHOTO_FINGER_OPEN) && !code.equals(SettingFunc.SET_QR_CODE_OPEN)) {
                            childList.add(new SettingFunc(code, codeName));
                        }
                    }
                }
            }
        }
    }

    private void fragmentReplace(Fragment fragment) {
        if (fCurrent != fragment) {
            AppUtils.getInstance().replaceFragment(this, fragment, R.id.fl_container);
            fCurrent = fragment;
        }
    }

    private void onSelectionChanged(SettingFunc func) {

        FragmentManager fm = getSupportFragmentManager();
        if (fm != null) {
            List<Fragment> fragments = fm.getFragments();
            for (int i = 0; i < fragments.size(); i++) {
                fm.popBackStack();
            }
        }

        setBackVisibility(View.VISIBLE);
        if (SettingFunc.SET_DEV_INFO.equals(func.getCode())) {
            if (fDevInfo == null)
                fDevInfo = new DevInfoFragment();
            fragmentReplace(fDevInfo);
        } else if (SettingFunc.PWD_MANAGE.equals(func.getCode()) && AppConfig.getInstance().getOpenPwdMode() == 1) {
            if (adminPwdFragment == null)
                adminPwdFragment = new SetAdminPwdFragment();
            fragmentReplace(adminPwdFragment);
        } else {
            fragmentReplace(fSetting);
            fSetting.resetFunc(func);
        }
    }

    @Override
    public void onBackPressed() {
        if (fCurrent == fSetting && fSetting.handleBack()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_back) {
            if (fCurrent instanceof SettingFragment || fCurrent instanceof SetAdminPwdFragment || fCurrent instanceof DevInfoFragment) {
                sendBroadcast(new Intent(Constant.Action.MAIN_REFRESH_ACTION));
                if (fCurrent instanceof DevInfoFragment) {
                    finish();
                    return;
                }
            }
            onBackPressed();
        }
    }

    public void setBackVisibility(int visibility) {
        findView(R.id.iv_back).setVisibility(visibility);
    }


    private class TabVH extends RecyclerView.ViewHolder {

        private TextView textView;

        private TabVH(View itemView) {
            super(itemView);
            textView = ViewUtils.findView(itemView, R.id.textview);
        }
    }

    private class TabAdapter extends RecyclerView.Adapter<TabVH> {

        private List<SettingFunc> mList;
        private int mSelection;

        private TabAdapter(List<SettingFunc> list) {
            mList = list;
            mSelection = 0;
            mMainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onSelectionChanged(mList.get(0));
                }
            }, 50);
        }

        public void setList(List<SettingFunc> list) {
            mList = list;
        }

        @NonNull
        @Override
        public TabVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_setting_tab, parent, false);
            return new TabVH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final TabVH holder, final int position) {
            final SettingFunc func = mList.get(position);


            holder.textView.setText(func.getName());
            final TypedValue typedValue = new TypedValue();
            if (position == mSelection) {
                mContext.getTheme().resolveAttribute(R.attr.btn_bg_checked, typedValue, true);
            } else {
                mContext.getTheme().resolveAttribute(R.attr.btn_bg, typedValue, true);
            }
            holder.itemView.setBackgroundColor(typedValue.data);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int oldIndex = mSelection;
                    int newIndex = holder.getAdapterPosition();
                    mSelection = newIndex;
                    notifyItemChanged(oldIndex);
                    notifyItemChanged(newIndex);
                    onSelectionChanged(func);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

    class SettingReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constant.Action.SETTING_NOTIFYCHANGE.equals(action)) {
                notifyChange();
            }
        }
    }

}
