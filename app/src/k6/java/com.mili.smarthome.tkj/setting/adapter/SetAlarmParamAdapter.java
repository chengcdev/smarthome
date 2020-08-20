package com.mili.smarthome.tkj.setting.adapter;

import android.content.Context;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.dao.param.AlarmParamDao;
import com.mili.smarthome.tkj.setting.interf.IOnItemClickListener;

/**
 * 报警参数
 */
public class SetAlarmParamAdapter extends ItemSelectorAdapter {

    private IOnItemClickListener itemClickListener;

    public SetAlarmParamAdapter(Context context) {
        super(context);

    }

    public SetAlarmParamAdapter(Context context, IOnItemClickListener itemClickListener) {
        super(context);
        this.itemClickListener = itemClickListener;
        setSelection(AlarmParamDao.getForceOpen());
    }

    @Override
    protected int getStringArrayId() {
        return R.array.setting_enabled;
    }

    @Override
    protected void onItemClick(int position) {
        switch (position) {
            //不启用
            case 0:
                AlarmParamDao.setForceOpen(0);
                break;
            //启用
            case 1:
                AlarmParamDao.setForceOpen(1);
                break;
        }
        if (itemClickListener != null) {
            itemClickListener.OnItemListener(position);
        }
    }

}
