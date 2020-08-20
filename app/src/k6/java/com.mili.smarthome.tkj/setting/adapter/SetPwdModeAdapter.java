package com.mili.smarthome.tkj.setting.adapter;

import android.content.Context;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.setting.interf.IOnItemClickListener;

/**
 * 密码开门模式
 */
public class SetPwdModeAdapter extends ItemSelectorAdapter {

    /** 简易模式 */
    private static final int SIMPLE  = 0;
    /** 高级模式 */
    private static final int ADVANCED = 1;
    private IOnItemClickListener itemClickListener;

    public SetPwdModeAdapter(Context context) {
        super(context);
        setSelection(SIMPLE);
    }

    public SetPwdModeAdapter(Context context, IOnItemClickListener itemClickListener) {
        super(context);
        this.itemClickListener = itemClickListener;
        setSelection(AppConfig.getInstance().getOpenPwdMode());
    }

    @Override
    protected int getStringArrayId() {
        return R.array.setting_pwd_mode;
    }

    @Override
    protected void onItemClick(int position) {
        switch (position) {
            case SIMPLE:
                AppConfig.getInstance().setOpenPwdMode(0);
                break;
            case ADVANCED:
                AppConfig.getInstance().setOpenPwdMode(1);
                break;
        }
        if (itemClickListener != null) {
            itemClickListener.OnItemListener(position);
        }
    }

}
