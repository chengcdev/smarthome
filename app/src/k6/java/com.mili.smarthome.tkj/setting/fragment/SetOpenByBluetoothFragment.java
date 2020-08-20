package com.mili.smarthome.tkj.setting.fragment;

import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.adapter.KeyBoardAdapter;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.entity.KeyBoardBean;
import com.mili.smarthome.tkj.view.KeyBoardView;
import com.mili.smarthome.tkj.view.SetOperateView;
import com.mili.smarthome.tkj.widget.NumInputView;

/**
 * 蓝牙开门器
 */
public class SetOpenByBluetoothFragment extends BaseFragment implements KeyBoardAdapter.IKeyBoardListener, SetOperateView.IOperateListener, View.OnClickListener {

    private NumInputView tvDevId;
    private KeyBoardView keyBoardView;
    private SetOperateView mOperateView;
    private String currentRegisterId = "";
    private ImageView mImaBack;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_open_by_bluetooth;
    }

    @Override
    protected void bindView() {
        tvDevId = findView(R.id.tv_devid);
        keyBoardView = findView(R.id.keyboardview);
        mOperateView = findView(R.id.rootview);
        mImaBack = findView(R.id.iv_back);
    }

    @Override
    protected void bindData() {
        keyBoardView.init(KeyBoardView.KEY_BOARD_SET);
        mOperateView.setSuccessListener(this);
        keyBoardView.setKeyBoardListener(this);
        mImaBack.setOnClickListener(this);
        setBackVisibility(View.GONE);
        mImaBack.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        currentRegisterId = "";
        tvDevId.setMaxLength(8);
        //获取设备注册Id
        String registerId = AppConfig.getInstance().getBluetoothDevId();
        tvDevId.setText(registerId);
        tvDevId.requestFocus();
    }

    @Override
    protected void unbindView() {
        setBackVisibility(View.VISIBLE);
    }

    @Override
    public void onKeyBoardListener(View view, int potion, KeyBoardBean keyBoardBean) {
        switch (keyBoardBean.getkId()) {
            case Const.KeyBoardId.KEY_CANCEL:
                if (tvDevId.getCursorIndex() == 0) {
                    exitFragment(this);
                } else {
                    backspace();
                }
                break;
            case Const.KeyBoardId.KEY_CONFIRM:
                currentRegisterId = tvDevId.getText().toString();
                //保存数据
                AppConfig.getInstance().setBluetoothDevId(currentRegisterId);
                //保存二维码开门模式
                AppConfig.getInstance().setQrOpenType(1);
                mOperateView.operateBackState(getString(R.string.set_success));
                mImaBack.setVisibility(View.GONE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exitFragment(SetOpenByBluetoothFragment.this);
                    }
                }, 1000);
                break;
            default:
                String id = keyBoardBean.getkId();
                inputNum(Integer.valueOf(id));
                break;
        }
    }

    @Override
    public void success() {

    }

    @Override
    public void fail() {

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_back) {
            exitFragment(this);
        }
    }
}
