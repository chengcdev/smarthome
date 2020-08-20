package com.mili.smarthome.tkj.setting.fragment;

import com.android.CommSysDef;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.ContextProxy;
import com.mili.smarthome.tkj.dao.param.ParamDao;

public class ResetCardNumFragment extends ResetSelectorFragment {

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
        setSelection(position);
    }

    @Override
    protected void onCancel() {
        requestBack();
    }

    @Override
    protected void onConfirm() {
        int position = getSelection();
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

        //发送广播
        ContextProxy.sendBroadcast(CommSysDef.BROADCAST_CARDNUMS);

        gotoNextFragment();
    }
}
