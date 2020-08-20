package com.mili.smarthome.tkj.setting.fragment;

import android.content.Intent;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.AppPreferences;

/**
 * {@link com.mili.smarthome.tkj.setting.entities.SettingFunc.SET_THEME}: 界面风格
 */
public class SetThemeFragment extends ItemSelectorFragment {

    private static final int BLACK = 0;
    private static final int BLUE = 1;

    @Override
    protected String[] getStringArray() {
        return mContext.getResources().getStringArray(R.array.setting_theme);
    }

    @Override
    protected void bindData() {
        super.bindData();
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
    public void onItemClick(int position) {
        switch (position) {
            case BLACK:
                AppPreferences.setAppTheme(R.style.AppTheme);
                break;
            case BLUE:
                AppPreferences.setAppTheme(R.style.AppTheme_Blue);
                break;
            default:
                return;
        }
        final Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        System.exit(0);
    }
}
