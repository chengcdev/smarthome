package com.mili.smarthome.tkj.setting.fragment;

import android.content.Intent;

import com.android.CommSysDef;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.App;
import com.mili.smarthome.tkj.dao.param.ParamDao;

public class ResetCardNumFragment extends ResetSelectorFragment {

    @Override
    public boolean onKeyCancel() {
        gotoPreviousFragment();
        return true;
    }

    @Override
    public boolean onKeyConfirm() {
        onItemClick(getSelection());
        return true;
    }

    @Override
    protected int getTitleId() {
        return R.string.reset_cardnum;
    }

    @Override
    protected int getStringArrayId() {
        return R.array.cardno_len;
    }

    @Override
    protected void bindData() {
        super.bindData();
        int cardnum = ParamDao.getCardNoLen();
        if (cardnum == 6) {
            setSelection(0);
        } else {
            setSelection(1);
        }
    }

    @Override
    protected void onItemClick(int position) {
        switch (position) {
            case 0:
                ParamDao.setCardNoLen(6);
                break;
            case 1:
                ParamDao.setCardNoLen(8);
                break;
            default:
                return;
        }

        Intent intent = new Intent(CommSysDef.BROADCAST_CARDNUMS);
        App.getInstance().sendBroadcast(intent);
        
        gotoNextFragment();
    }
}
