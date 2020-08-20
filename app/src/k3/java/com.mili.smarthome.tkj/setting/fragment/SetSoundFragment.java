package com.mili.smarthome.tkj.setting.fragment;

import android.os.Bundle;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.setting.entities.SettingFunc;

/**
 * 声音设置
 * <p>{@link com.mili.smarthome.tkj.setting.entities.SettingFunc.SET_PROMPT_TONE}: 提示音
 * <p>{@link com.mili.smarthome.tkj.setting.entities.SettingFunc.SET_KEY_TONE}: 按键音
 * <p>{@link com.mili.smarthome.tkj.setting.entities.SettingFunc.SET_MEDIA_MUTE}: 媒体静音
 */
public class SetSoundFragment extends ItemSelectorFragment {

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
            mFuncCode = args.getString(FragmentFactory.ARGS_FUNCCODE, SettingFunc.SET_PROMPT_TONE);
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
                break;
            //媒体音
            case SettingFunc.SET_MEDIA_MUTE:
                AppConfig.getInstance().setMediaVolume(position);
                break;
            default:
                return;
        }
        showResultAndBack(R.string.setting_suc);
    }
}
