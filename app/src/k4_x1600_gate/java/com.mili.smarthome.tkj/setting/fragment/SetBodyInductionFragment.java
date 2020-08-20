package com.mili.smarthome.tkj.setting.fragment;

import android.util.Log;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;

import java.util.ArrayList;
import java.util.List;

public class SetBodyInductionFragment extends ItemSelectorFragment{

    @Override
    public boolean onKeyCancel() {
        super.onKeyCancel();
        exitFragment();
        return true;
    }

    @Override
    public boolean onKeyConfirm() {
        super.onKeyConfirm();
        int  position = getSelection();
        onItemClick(position);
        return true;
    }

    @Override
    protected String[] getStringArray() {
        String[] list = mContext.getResources().getStringArray(R.array.setting_body_detection);
        Log.d("SetBodyInduction", "list.length = " + list.length);
        if (list.length < 4) {
            return list;
        }

        List<String> itemList = new ArrayList<>();
        itemList.add(list[0]);
        if (AppConfig.getInstance().isFaceEnabled()) {
            itemList.add(list[1]);
        }
        if (AppConfig.getInstance().getQrOpenType() == 0) {
             if (AppConfig.getInstance().getQrScanEnabled() == 1) {
                 itemList.add(list[2]);
             }
        } else if (AppConfig.getInstance().getQrOpenType() == 1){
            String code = AppConfig.getInstance().getBluetoothDevId();
            if (code.length() > 0) {
                itemList.add(list[3]);
            }
        }
        return (String[]) itemList.toArray(new String[itemList.size()]);
    }

    @Override
    public void onItemClick(int position) {
        int body = 0;
        switch (position) {
            case 0:
                body = 0;
                break;

            case 1:
                if (AppConfig.getInstance().isFaceEnabled()) {
                    body = 1;
                } else {
                    if (AppConfig.getInstance().getQrOpenType() == 0) {
                        body = 2;
                    } else {
                        body = 3;
                    }
                }
                break;

            case 2:
                if (AppConfig.getInstance().getQrOpenType() == 0) {
                    body = 2;
                } else {
                    body = 3;
                }
                break;
        }
        AppConfig.getInstance().setBodyInduction(body);
        showSetHint(R.string.set_success);
    }

    @Override
    protected void bindData() {
        super.bindData();
        setHead(getResources().getString(R.string.setting_0306));

        int body = AppConfig.getInstance().getBodyInduction();
        switch (body) {
            case 0:
                setSelection(0);
                break;

            case 1:
                setSelection(1);
                break;

            case 2:
            case 3:
                if (AppConfig.getInstance().isFaceEnabled()) {
                    setSelection(2);
                } else {
                    setSelection(1);
                }
                break;

            default:
                setSelection(0);
                break;
        }
    }
}
