package com.mili.smarthome.tkj.setting.adapter;

import android.content.Context;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.setting.entities.SettingFunc;
import com.mili.smarthome.tkj.setting.interf.IOnItemClickListener;
import com.mili.smarthome.tkj.utils.SystemSetUtils;

/**
 * 声音设置
 */
public class SetSoundAdapter extends ItemSelectorAdapter {

    private IOnItemClickListener itemClickListener;
    private String mFuncCode;
    private int tipVolume;

    public SetSoundAdapter(Context context, String funcCode) {
        super(context);
        mFuncCode = funcCode;
        setSelection(1);
    }

    public SetSoundAdapter(Context context, String funcCode, IOnItemClickListener itemClickListener) {
        super(context);
        mFuncCode = funcCode;
        this.itemClickListener = itemClickListener;

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
    protected int getStringArrayId() {
        return R.array.setting_switch;
    }

    @Override
    protected void onItemClick(int position) {
        switch (mFuncCode) {
            //提示音
            case SettingFunc.SET_PROMPT_TONE:
                AppConfig.getInstance().setTipVolume(position);
                break;
            //按键音
            case SettingFunc.SET_KEY_TONE:
                if (position == 0) {
                    SystemSetUtils.setEnableKeyVoice(false);
                }else {
                    SystemSetUtils.setEnableKeyVoice(true);
                }
                AppConfig.getInstance().setKeyVolume(position);
                break;
            //媒体音
            case SettingFunc.SET_MEDIA_MUTE:
                AppConfig.getInstance().setMediaVolume(position);
                break;

        }
        if (itemClickListener != null) {
            itemClickListener.OnItemListener(position);
        }
    }
}
