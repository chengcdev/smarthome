package com.mili.smarthome.tkj.setting.adapter;

import android.content.Context;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.constant.Constant;
import com.mili.smarthome.tkj.setting.interf.IOnItemClickListener;

/**
 * 省电模式
 */
public class SetPowerSavingAdapter extends ItemSelectorAdapter {

    private IOnItemClickListener itemClickListener;

    public SetPowerSavingAdapter(Context context) {
        super(context);
        setSelection(1);
    }

    public SetPowerSavingAdapter(Context context, IOnItemClickListener itemClickListener) {
        super(context);
        this.itemClickListener = itemClickListener;
        setSelection(AppConfig.getInstance().getPowerSaving());
    }

    @Override
    protected int getStringArrayId() {
        return R.array.setting_switch;
    }

    @Override
    protected void onItemClick(int position) {
        switch (position) {
            //关闭
            case 0:
                AppConfig.getInstance().setPowerSaving(0);
                Constant.ScreenId.SCREEN_CLOSE = false;
                break;
            //启用
            case 1:
                AppConfig.getInstance().setPowerSaving(1);
                Constant.ScreenId.SCREEN_CLOSE = true;
                break;
        }
        if (itemClickListener != null) {
            itemClickListener.OnItemListener(position);
        }
    }
}
