package com.mili.smarthome.tkj.setting.fragment;

/**
 *
 */
public abstract class ItemSelectorFragment extends SetSelectorFragment {

    @Override
    protected void bindData() {
        super.bindData();
        setOptions(getStringArray());
    }

    protected abstract String[] getStringArray();

}
