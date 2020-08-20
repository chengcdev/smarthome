package com.mili.smarthome.tkj.setting.adapter;

import android.content.Context;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.setting.interf.IOnItemClickListener;

/**
 * 动态密码
 */
public class SetPwdDynamicAdapter extends ItemSelectorAdapter {


    private IOnItemClickListener itemClickListener;

    public SetPwdDynamicAdapter(Context context) {
        super(context);
    }

    public SetPwdDynamicAdapter(Context context, IOnItemClickListener itemClickListener) {
        super(context);
        this.itemClickListener = itemClickListener;
        setSelection(AppConfig.getInstance().getPwdDynamic());
    }

    @Override
    protected int getStringArrayId() {
        return R.array.setting_switch;
    }

    @Override
    protected void onItemClick(int position) {
        switch (position) {
            case 0:
                AppConfig.getInstance().setPwdDynamic(0);
                break;
            case 1:
                AppConfig.getInstance().setPwdDynamic(1);
                break;
        }
        if (itemClickListener != null) {
            itemClickListener.OnItemListener(position);
        }
    }

}
