package com.mili.smarthome.tkj.setting.adapter;

import android.content.Context;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.constant.Constant;
import com.mili.smarthome.tkj.setting.interf.IOnItemClickListener;

/**
 * 屏保设置
 */
public class SetScreenSaverAdapter extends ItemSelectorAdapter {

    private IOnItemClickListener itemClickListener;

    public SetScreenSaverAdapter(Context context) {
        super(context);
        setSelection(1);
    }

    public SetScreenSaverAdapter(Context context, IOnItemClickListener itemClickListener) {
        super(context);
        this.itemClickListener = itemClickListener;
        setSelection(AppConfig.getInstance().getScreenSaver());
    }


    @Override
    protected int getStringArrayId() {
        return R.array.setting_switch;
    }

    @Override
    protected void onItemClick(int position) {
        switch (position) {
            case 0:
                AppConfig.getInstance().setScreenSaver(position);
                Constant.ScreenId.SCREEN_PROTECT = false;
                break;
            case 1:
                AppConfig.getInstance().setScreenSaver(position);
                Constant.ScreenId.SCREEN_PROTECT = true;
                break;
        }
        if (itemClickListener != null) {
            itemClickListener.OnItemListener(position);
        }
    }
}
