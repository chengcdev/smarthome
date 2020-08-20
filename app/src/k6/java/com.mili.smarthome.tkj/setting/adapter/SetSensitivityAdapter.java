package com.mili.smarthome.tkj.setting.adapter;

import android.content.Context;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.dao.param.ParamDao;
import com.mili.smarthome.tkj.setting.interf.IOnItemClickListener;

/**
 * 灵敏度设置
 */
public class SetSensitivityAdapter extends ItemSelectorAdapter {

    private static final int HIGH = 0;
    private static final int MID = 1;
    private static final int LOW = 2;
    private IOnItemClickListener itemClickListener;

    public SetSensitivityAdapter(Context context) {
        super(context);
        setSelection(MID);
    }

    public SetSensitivityAdapter(Context context, IOnItemClickListener itemClickListener) {
        super(context);
        this.itemClickListener = itemClickListener;
        setSelection(ParamDao.getTouchSensitivity());
    }


    @Override
    protected int getStringArrayId() {
        return R.array.setting_sensitivity;
    }

    @Override
    protected void onItemClick(int position) {
        switch (position) {
            case HIGH:
                ParamDao.setTouchSensitivity(0);
                break;
            case MID:
                ParamDao.setTouchSensitivity(1);
                break;
            case LOW:
                ParamDao.setTouchSensitivity(2);
                break;
        }
        if (itemClickListener != null) {
            itemClickListener.OnItemListener(position);
        }
    }

}
