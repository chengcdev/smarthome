package com.mili.smarthome.tkj.setting.fragment;

import android.view.View;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.adapter.KeyBoardAdapter;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.entity.KeyBoardBean;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.SysTimeSetUtils;
import com.mili.smarthome.tkj.view.KeyBoardView;
import com.mili.smarthome.tkj.view.SetOperateView;
import com.mili.smarthome.tkj.widget.FormatInputView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * 时间设置
 */
public class SetTimeFragment extends BaseFragment implements KeyBoardAdapter.IKeyBoardListener, SetOperateView.IOperateListener {

    private KeyBoardView keyBoardView;
    private SetOperateView mOperateView;
    private FormatInputView mDate;
    private FormatInputView mTime;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_time;
    }

    @Override
    protected void bindView() {
        mDate = findView(R.id.tv_date);
        mTime = findView(R.id.tv_time);
        keyBoardView = findView(R.id.keyboardview);
        mOperateView = findView(R.id.rootview);
    }

    @Override
    protected void bindData() {
        keyBoardView.init(KeyBoardView.KEY_BOARD_SET);
        keyBoardView.setKeyBoardListener(this);
        mOperateView.setSuccessListener(this);

        initDateTime();
    }

    private void initDateTime() {
        mDate.requestFocus();
        //初始化日期
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String date = df.format(System.currentTimeMillis());
        mDate.setText(date);
        //初始化时间
        df.applyPattern("HH:mm:ss");
        String time = df.format(System.currentTimeMillis());
        mDate.setText(time);

    }


    @Override
    public void onKeyBoardListener(View view, int potion, KeyBoardBean keyBoardBean) {
        switch (keyBoardBean.getkId()) {
            case Const.KeyBoardId.KEY_CANCEL:
                if (mDate.getCursorIndex() == 2) {
                    requestBack();
                }else {
                    backspace();
                }
                break;
            case Const.KeyBoardId.KEY_CONFIRM:
                save();
                //设置成功
                mOperateView.operateBackState(getString(R.string.set_success));
                setBackVisibility(View.GONE);
                break;
            default:
                inputNum(Integer.parseInt(keyBoardBean.getName()));
                break;
        }
    }



    @Override
    public void success() {
        setBackVisibility(View.VISIBLE);
        mDate.requestFocus();
    }

    @Override
    public void fail() {

    }

    private void save() {
        StringBuilder datetime = new StringBuilder()
                .append(mDate.getText())
                .append(" ")
                .append(mTime.getText());
        try {
            final String pattern = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
            SysTimeSetUtils.setSysTime(mContext, sdf.parse(datetime.toString()));
        } catch (ParseException e) {
            LogUtils.e(e);
        }
    }

}
