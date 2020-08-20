package com.mili.smarthome.tkj.setting.fragment;

import com.android.interf.IKeyEventListener;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.SysTimeSetUtils;
import com.mili.smarthome.tkj.widget.FormatInputView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * 时间设置
 */
public class SetTimeFragment extends BaseSetFragment {

    private FormatInputView tvDate;
    private FormatInputView tvTime;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_time;
    }

    @Override
    protected void bindView() {
        tvDate = findView(R.id.tv_date);
        tvTime = findView(R.id.tv_time);
    }

    @Override
    protected void bindData() {
        tvDate.requestFocus();
    }

    @Override
    public boolean onKeyEvent(int keyCode, int keyState) {
        if (keyState == IKeyEventListener.KEYSTATE_DOWN)
            return false;
        switch (keyCode) {
            case IKeyEventListener.KEYCODE_0:
                inputNum(0);
                break;
            case IKeyEventListener.KEYCODE_1:
            case IKeyEventListener.KEYCODE_2:
            case IKeyEventListener.KEYCODE_3:
            case IKeyEventListener.KEYCODE_4:
            case IKeyEventListener.KEYCODE_5:
            case IKeyEventListener.KEYCODE_6:
            case IKeyEventListener.KEYCODE_7:
            case IKeyEventListener.KEYCODE_8:
            case IKeyEventListener.KEYCODE_9:
                inputNum(keyCode);
                break;
            case IKeyEventListener.KEYCODE_BACK:
                backspace();
                break;
            case IKeyEventListener.KEYCODE_CALL:
                save();
                break;
        }
        return true;
    }

    private void save() {
        StringBuilder datetime = new StringBuilder()
                .append(tvDate.getText())
                .append(" ")
                .append(tvTime.getText());
        try {
            final String pattern = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
            SysTimeSetUtils.setSysTime(mContext, sdf.parse(datetime.toString()));
            showResultAndBack(R.string.setting_suc);
        } catch (ParseException e) {
            LogUtils.printThrowable(e);
        }
    }
}
