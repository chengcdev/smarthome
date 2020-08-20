package com.mili.smarthome.tkj.setting.fragment;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.android.CommTypeDef;
import com.android.provider.FullDeviceNo;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.base.BaseFragment;
import com.mili.smarthome.tkj.constant.Constant;
import com.mili.smarthome.tkj.dao.param.ParamDao;
import com.mili.smarthome.tkj.main.activity.SetLanguageActivity;
import com.mili.smarthome.tkj.setting.adapter.ItemSelectorAdapter;
import com.mili.smarthome.tkj.setting.interf.IOnItemClickListener;
import com.mili.smarthome.tkj.utils.AppUtils;
import com.mili.smarthome.tkj.view.SetOperateView;

/**
 * 恢复出厂后设置界面
 */
public class ResetSelectorFragment extends BaseFragment implements IOnItemClickListener, SetOperateView.IOperateListener, View.OnClickListener {

    private static final int SET_LANGUAGE = 0x11;
    private static final int SET_CARD_NUM = 0x12;
    private static final int SET_DEVICE_TYPE = 0x13;

    private RecyclerView mRecyclerView;
    private String mFuncCode;
    private ItemSelectorAdapter mAdapter;
    private SetOperateView mOperateView;
    private TextView mTvTitle;
    private TextView mTvCancel;
    private TextView mTvConfirm;
    private int mSetFunc = SET_LANGUAGE;
    private int mCurrentLanPos = 0;
    private int mCurrentCardPos = 0;
    private int mCurrentDevicePos = 0;
    private int count = 0;
    private SetDevNoFragment setDevNoFragment;
    private FullDeviceNo fullDeviceNo;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_reset_selector;
    }

    @Override
    protected void bindView() {
        mOperateView = findView(R.id.rootview);
        mRecyclerView = findView(R.id.recyclerview);
        mTvTitle = findView(R.id.tv_title);
        mTvCancel = findView(R.id.tv_cancle);
        mTvConfirm = findView(R.id.tv_confirm);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mOperateView.setSuccessListener(this);
        mTvConfirm.setOnClickListener(this);
        mTvCancel.setOnClickListener(this);
    }

    @Override
    protected void bindData() {

        if (fullDeviceNo == null) {
            fullDeviceNo = new FullDeviceNo(getContext());
        }

        notifyList();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //取消
            case R.id.tv_cancle:
                count--;
                if (count < 0) {
                    count = 0;
                    AppUtils.getInstance().toActFinish(getActivity(), SetLanguageActivity.class);
                }
                notifyList();
                break;
            //确定
            case R.id.tv_confirm:
                if (count >= 2) {
                    count = 1;
                }
                count++;
                notifyList();
                break;
            default:
                break;
        }
    }

    public void notifyList() {
        switch (count) {
            case 0:
                mSetFunc = SET_CARD_NUM;
                int cardNum = ParamDao.getCardNoLen();
                mAdapter = new SetCardAdapter(getContext());
                mTvTitle.setText(getString(R.string.setting_card_num));
                if (cardNum == 6) {
                    mAdapter.setSelection(0);
                }else {
                    mAdapter.setSelection(1);
                }
                break;
            case 1:
                mSetFunc = SET_DEVICE_TYPE;
                byte deviceType = fullDeviceNo.getDeviceType();
                mAdapter = new SetDeviceTypeAdapter(getContext());
                mTvTitle.setText(getString(R.string.setting_device_type));
                if (deviceType == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
                    mAdapter.setSelection(0);
                }else {
                    mAdapter.setSelection(1);
                }
                break;
            case 2:
                count = 1;
                //设置梯口号
                if (setDevNoFragment == null) {
                    setDevNoFragment = new SetDevNoFragment();
                }
                AppUtils.getInstance().replaceFragment(getActivity(), setDevNoFragment, R.id.fl, "SetRuleNoFragment",
                        Constant.IntentId.INTENT_KEY, true);
                break;
        }

        mRecyclerView.setAdapter(mAdapter);
    }



    private class SetCardAdapter extends ItemSelectorAdapter {

        public SetCardAdapter(Context context) {
            super(context, true);
        }

        @Override
        protected int getStringArrayId() {
            return R.array.setting_card_num;
        }

        @Override
        protected void onItemClick(int position) {
            switch (position) {
                //6位
                case 0:
                    ParamDao.setCardNoLen(6);
                    break;
                //8位
                case 1:
                    ParamDao.setCardNoLen(8);
                    break;
                default:
                    break;
            }
        }

    }

    private class SetDeviceTypeAdapter extends ItemSelectorAdapter {


        public SetDeviceTypeAdapter(Context context) {
            super(context, true);
        }

        @Override
        protected int getStringArrayId() {
            return R.array.setting_device_type;
        }

        @Override
        protected void onItemClick(int position) {
            switch (position) {
                case 0:
                    fullDeviceNo.setDeviceType((byte) CommTypeDef.DeviceType.DEVICE_TYPE_STAIR);
                    break;
                case 1:
                    fullDeviceNo.setDeviceType((byte) CommTypeDef.DeviceType.DEVICE_TYPE_AREA);
                    break;
                default:
                    break;
            }
        }

    }


    @Override
    public void OnItemListener(int position) {
        mOperateView.operateBackState(getString(R.string.set_success));
    }

    @Override
    public void success() {

    }

    @Override
    public void fail() {

    }
}
