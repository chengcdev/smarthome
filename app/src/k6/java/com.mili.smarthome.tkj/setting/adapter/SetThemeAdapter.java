package com.mili.smarthome.tkj.setting.adapter;

import android.content.Context;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.AppPreferences;

/**
 * {@link com.mili.smarthome.tkj.setting.entities.SettingFunc.SET_THEME}: 界面风格
 */
public class SetThemeAdapter extends ItemSelectorAdapter {

    private static final int BLACK = 0;
    private static final int BLUE = 1;

    public SetThemeAdapter(Context context) {
        super(context);
        int theme = AppPreferences.getAppTheme();
        switch (theme) {
            case R.style.AppTheme:
                setSelection(BLACK);
                break;
            case R.style.AppTheme_Blue:
                setSelection(BLUE);
                break;
        }
    }

    @Override
    protected int getStringArrayId() {
        return R.array.setting_theme;
    }

    @Override
    protected void onItemClick(int position) {
        switch (position) {
            case BLACK:
                AppPreferences.setAppTheme( R.style.AppTheme);
                break;
            case BLUE:
                AppPreferences.setAppTheme(R.style.AppTheme_Blue);
                break;
        }
    }

}
