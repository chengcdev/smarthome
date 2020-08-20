package com.mili.smarthome.tkj.setting.adapter;

import android.content.Context;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.setting.interf.IOnItemClickListener;

/**
 * 扫码开门
 */
public class SetOpenByScanAdapter extends ItemSelectorAdapter {


    private IOnItemClickListener itemClickListener;

    public SetOpenByScanAdapter(Context context) {
        super(context);
    }

    public SetOpenByScanAdapter(Context context, IOnItemClickListener itemClickListener) {
        super(context);
        this.itemClickListener = itemClickListener;
    }

    @Override
    protected int getStringArrayId() {
        return R.array.setting_enabled2;
    }

    @Override
    protected void onItemClick(int position) {
        switch (position) {
            case 0:
                AppConfig.getInstance().setQrScanEnabled(0);
                if (itemClickListener != null) {
                    itemClickListener.OnItemListener(0);
                }
                break;
            case 1:
                AppConfig.getInstance().setQrScanEnabled(1);
                if (itemClickListener != null) {
                    itemClickListener.OnItemListener(1);
                }
                break;
        }
    }



}
