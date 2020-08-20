package com.mili.smarthome.tkj.setting.fragment;

import android.content.Intent;
import android.os.Bundle;

import com.android.CommSysDef;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.App;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.setting.entities.SettingFunc;

/**
 * 声音设置
 */
public class SetSoundFragment extends ItemSelectorFragment {

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
        return mContext.getResources().getStringArray(R.array.setting_switch);
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
            //提示音
            case SettingFunc.SET_PROMPT_TONE:
                int tipVolume = AppConfig.getInstance().getTipVolume();
                setSelection(tipVolume);
                break;
            //按键音
            case SettingFunc.SET_KEY_TONE:
                int keyVolume = AppConfig.getInstance().getKeyVolume();
                setSelection(keyVolume);
                break;
            //媒体音
            case SettingFunc.SET_MEDIA_MUTE:
                int mediaVoume = AppConfig.getInstance().getMediaVolume();
                setSelection(mediaVoume);
                break;

        }
    }

    @Override
    public void onItemClick(int position) {
        switch (mFuncCode) {
            //提示音
            case SettingFunc.SET_PROMPT_TONE:
                AppConfig.getInstance().setTipVolume(position);
                break;
            //按键音
            case SettingFunc.SET_KEY_TONE:
                AppConfig.getInstance().setKeyVolume(position);
                // 发广播
                Intent intent = new Intent(CommSysDef.BROADCAST_KEY_VOLUME);
                App.getInstance().sendBroadcast(intent);
                break;
            //媒体音
            case SettingFunc.SET_MEDIA_MUTE:
                AppConfig.getInstance().setMediaVolume(position);
                break;
            default:
                return;
        }
        showSetHint(R.string.set_success);
    }
}
