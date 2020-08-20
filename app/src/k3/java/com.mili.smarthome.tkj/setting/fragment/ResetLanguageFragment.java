package com.mili.smarthome.tkj.setting.fragment;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.utils.SystemSetUtils;

import java.util.Locale;

public class ResetLanguageFragment extends ResetSelectorFragment {

    private Locale mLocale;

    @Override
    protected int getTitleId() {
        return R.string.reset_language;
    }

    @Override
    protected int getStringArrayId() {
        return R.array.language_list;
    }

    @Override
    protected void bindData() {
        super.bindData();
        mLocale = Locale.getDefault();
        if (Locale.SIMPLIFIED_CHINESE.equals(mLocale)) {
            setSelection(0);
        } else if (Locale.TRADITIONAL_CHINESE.equals(mLocale)) {
            setSelection(1);
        } else if (Locale.US.equals(mLocale)) {
            setSelection(2);
        }
    }

    @Override
    protected void onItemClick(int position) {
        setSelection(position);
    }

    @Override
    protected void onCancel() {

    }

    @Override
    protected void onConfirm() {
        int selection = getSelection();
        Locale sLocale;
        switch (selection) {
            case 0:
                sLocale = Locale.SIMPLIFIED_CHINESE;
                break;
            case 1:
                sLocale = Locale.TRADITIONAL_CHINESE;
                break;
            case 2:
                sLocale = Locale.US;
                break;
            default:
                return;
        }
        if (sLocale.equals(mLocale)) {
            gotoNextFragment();
        } else {
            // ResetActivity.onConfigurationChanged(Configuration) 进行跳转
            SystemSetUtils.setSystemLanguage(selection);
        }
    }
}
