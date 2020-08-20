package com.mili.smarthome.tkj.setting.fragment;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.utils.SystemSetUtils;

import java.util.Locale;

public class ResetLanguageFragment extends ResetSelectorFragment{

    private int mLanguage;

    @Override
    public boolean onKeyCancel() {
        return true;
    }

    @Override
    public boolean onKeyConfirm() {
        onItemClick(getSelection());
        return true;
    }

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
        Locale mLocale = Locale.getDefault();
        if (Locale.SIMPLIFIED_CHINESE.equals(mLocale)) {
            mLanguage = 0;
        } else if (Locale.TRADITIONAL_CHINESE.equals(mLocale)) {
            mLanguage = 1;
        } else if (Locale.US.equals(mLocale)) {
            mLanguage = 2;
        }
        setSelection(mLanguage);
    }

    @Override
    protected void onItemClick(int position) {
        if (mLanguage != position) {
            mLanguage = position;
            SystemSetUtils.setSystemLanguage(position);
        }
        gotoNextFragment();
    }
}
