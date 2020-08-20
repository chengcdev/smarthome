package com.mili.smarthome.tkj.setting.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.CommSysDef;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.setting.adapter.ItemSelectorAdapter;
import com.mili.smarthome.tkj.setting.adapter.SetAlarmParamAdapter;
import com.mili.smarthome.tkj.setting.adapter.SetBodyDetectionAdapter;
import com.mili.smarthome.tkj.setting.adapter.SetEventPlatformAdapter;
import com.mili.smarthome.tkj.setting.adapter.SetOpenByScanAdapter;
import com.mili.smarthome.tkj.setting.adapter.SetPhotoParamAdapter;
import com.mili.smarthome.tkj.setting.adapter.SetPowerSavingAdapter;
import com.mili.smarthome.tkj.setting.adapter.SetPwdDynamicAdapter;
import com.mili.smarthome.tkj.setting.adapter.SetPwdModeAdapter;
import com.mili.smarthome.tkj.setting.adapter.SetScreenSaverAdapter;
import com.mili.smarthome.tkj.setting.adapter.SetSoundAdapter;
import com.mili.smarthome.tkj.setting.entities.SettingFunc;
import com.mili.smarthome.tkj.setting.interf.IOnItemClickListener;
import com.mili.smarthome.tkj.utils.AppUtils;
import com.mili.smarthome.tkj.view.SetOperateView;

/**
 *
 */
public class ItemSelectorFragment extends BaseFragment implements IOnItemClickListener, SetOperateView.IOperateListener {

    private RecyclerView mRecyclerView;
    private String mFuncCode;
    private ItemSelectorAdapter mAdapter;
    private SetOperateView mOperateView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_item_selector;
    }

    @Override
    protected void bindView() {
        mOperateView = findView(R.id.rootview);
        mRecyclerView = findView(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mOperateView.setSuccessListener(this);
    }

    @Override
    protected void bindData() {
        Bundle args = getArguments();
        if (args != null) {
            mFuncCode = args.getString(FragmentFactory.ARGS_FUNCCODE, SettingFunc.SET_BODY_DETECTION);
            switch (mFuncCode) {
                case SettingFunc.SET_BODY_DETECTION:
                    mAdapter = new SetBodyDetectionAdapter(mContext, this);
                    break;
                //扫码开门
                case SettingFunc.SET_OPEN_BY_SCAN:
                    initScanOpen();
                    break;

                case SettingFunc.SET_PROMPT_TONE:
                case SettingFunc.SET_KEY_TONE:
                case SettingFunc.SET_MEDIA_MUTE:
                    mAdapter = new SetSoundAdapter(mContext, mFuncCode, this);
                    break;

                case SettingFunc.SET_OPEN_PWD_MODE:
                    mAdapter = new SetPwdModeAdapter(mContext, this);
                    break;

                case SettingFunc.SET_ALARM_PARAM:
                    mAdapter = new SetAlarmParamAdapter(mContext, this);
                    break;

                case SettingFunc.SET_PHOTO_VISITOR:
                case SettingFunc.SET_PHOTO_ERR_PWD:
                case SettingFunc.SET_PHOTO_HOLD_PWD:
                case SettingFunc.SET_PHOTO_CALL_CENTER:
                case SettingFunc.SET_PHOTO_FACE_OPEN:
                case SettingFunc.SET_PHOTO_FINGER_OPEN:
                case SettingFunc.SET_PHOTO_CARD_OPEN:
                case SettingFunc.SET_PHOTO_PWD_OPEN:
                case SettingFunc.SET_QR_CODE_OPEN:
                case SettingFunc.SET_PHOTO_FACE_STRANGER:
                    mAdapter = new SetPhotoParamAdapter(mContext, mFuncCode, this);
                    break;

                case SettingFunc.SET_POWER_SAVING:
                    mAdapter = new SetPowerSavingAdapter(mContext, this);
                    break;

                case SettingFunc.SET_SCREEN_SAVER:
                    mAdapter = new SetScreenSaverAdapter(mContext, this);
                    break;

                case SettingFunc.SET_PWD_DYNAMIC:
                    mAdapter = new SetPwdDynamicAdapter(mContext, this);
                    break;

                case SettingFunc.SET_EVENT_PLATFORM:
                    mAdapter = new SetEventPlatformAdapter(mContext, this);
                    break;
            }
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    private void initScanOpen() {
        int sweepCodeOpen = AppConfig.getInstance().getQrScanEnabled();
        mAdapter = new SetOpenByScanAdapter(mContext, this);
        mAdapter.setSelection(sweepCodeOpen);
    }


    @Override
    public void OnItemListener(int position) {
        mOperateView.operateBackState(getString(R.string.set_success));
        setBackVisibility(View.GONE);
    }

    @Override
    public void success() {
        switch (mFuncCode) {
            case SettingFunc.SET_POWER_SAVING:
            case SettingFunc.SET_SCREEN_SAVER:
                //屏保和关屏服务
                AppUtils.getInstance().startScreenService();
                break;
            case SettingFunc.SET_OPEN_PWD_MODE:
                //刷新设置左边列表
                notifySetList();
                break;
            case SettingFunc.SET_ALARM_PARAM:
                AppUtils.getInstance().sendReceiver(CommSysDef.BROADCAST_FORCEDOPENDOOR);
                break;
            //启用访客拍照
            case SettingFunc.SET_PHOTO_VISITOR:
                //错误密码开门拍照
            case SettingFunc.SET_PHOTO_ERR_PWD:
                //挟持密码开门拍照
            case SettingFunc.SET_PHOTO_HOLD_PWD:
                //呼叫中心拍照
            case SettingFunc.SET_PHOTO_CALL_CENTER:
                //人脸开门拍照
            case SettingFunc.SET_PHOTO_FACE_OPEN:
                //指纹开门拍照
            case SettingFunc.SET_PHOTO_FINGER_OPEN:
                notifySetList();
                //刷卡开门拍照
            case SettingFunc.SET_PHOTO_CARD_OPEN:
                //密码开门拍照
            case SettingFunc.SET_PHOTO_PWD_OPEN:
                //扫码开门拍照
            case SettingFunc.SET_QR_CODE_OPEN:
                //陌生人脸拍照
            case SettingFunc.SET_PHOTO_FACE_STRANGER:
                notifySetList();
                AppUtils.getInstance().sendReceiver(CommSysDef.BROADCAST_CAMERAPARAM);
                break;
        }
        setBackVisibility(View.VISIBLE);
        requestBack();
    }

    @Override
    public void fail() {
        setBackVisibility(View.VISIBLE);
    }

}
