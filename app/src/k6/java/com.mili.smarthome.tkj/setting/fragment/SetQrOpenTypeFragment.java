package com.mili.smarthome.tkj.setting.fragment;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.setting.adapter.ItemSelectorAdapter;
import com.mili.smarthome.tkj.utils.AppUtils;
import com.mili.smarthome.tkj.view.SetOperateView;

/**
 * 二维码开门
 */
public class SetQrOpenTypeFragment extends BaseFragment implements SetOperateView.IOperateListener, View.OnClickListener {

    private static final int SCAN_OPEN = 0x030401;
    private static final int BLUTOOTH_OPEN = 0x030402;
    private static final int QR_OPEN = 0x0304;

    private TextView mTvTitle;
    private SetOperateView mOperateView;
    private RecyclerView mRecyclerView;
    private SetListAdapter listAdapter;
    private LinearLayout mLinTitle;
    private ImageView mImaBack;
    private int mSetFunc = 0;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_qr_open_type;
    }

    @Override
    protected void bindView() {
        mRecyclerView = findView(R.id.recyclerview);
        mOperateView = findView(R.id.rootview);
        mTvTitle = findView(R.id.tv_title);
        mLinTitle = findView(R.id.ll_title);
        mImaBack = findView(R.id.iv_back);
        setBackVisibility(View.GONE);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    protected void bindData() {
        mOperateView.setSuccessListener(this);
        mImaBack.setOnClickListener(this);
        mImaBack.setVisibility(View.VISIBLE);
     }

    @Override
    public void onResume() {
        super.onResume();
        initListAdapter();
    }

    private void initListAdapter() {
        listAdapter = new SetListAdapter(getContext());
        mRecyclerView.setAdapter(listAdapter);
        //二维码开门方式
        int openDoorType = AppConfig.getInstance().getQrOpenType();
        listAdapter.setSelection(openDoorType);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                setBackVisibility(View.VISIBLE);
                requestBack();
                break;
        }
    }


    private class SetListAdapter extends ItemSelectorAdapter {


        private ScanAdapter scanAdapter;
        private SetOpenByBluetoothFragment bluetoothFragment;

        public SetListAdapter(Context context) {
            super(context);
        }

        @Override
        protected int getStringArrayId() {
            return R.array.setting_qr_open_type;
        }

        @Override
        protected void onItemClick(int position) {
            switch (position) {
                //扫码开门
                case 0:
                    showTitle(getString(R.string.setting_030401));
                    mSetFunc = SCAN_OPEN;
                    scanAdapter = new ScanAdapter(getContext());
                    mRecyclerView.setAdapter(scanAdapter);
                    int sweepCodeOpen = AppConfig.getInstance().getQrScanEnabled();
                    scanAdapter.setSelection(sweepCodeOpen);
                    break;
                //蓝牙开门器
                case 1:
                    mSetFunc = BLUTOOTH_OPEN;
                    if (bluetoothFragment == null) {
                        bluetoothFragment = new SetOpenByBluetoothFragment();
                    }
                    AppUtils.getInstance().replaceFragment(getActivity(), bluetoothFragment, R.id.fl_container,
                            "bluetoothFragment");
                    break;
            }
        }

    }

    private class ScanAdapter extends ItemSelectorAdapter {

        public ScanAdapter(Context context) {
            super(context);
        }

        @Override
        protected int getStringArrayId() {
            return R.array.setting_enabled2;
        }

        @Override
        protected void onItemClick(int position) {
            switch (position) {
                //禁用
                case 0:
                    AppConfig.getInstance().setQrScanEnabled(0);
                    break;
                //启用
                case 1:
                    AppConfig.getInstance().setQrScanEnabled(1);
                    break;
            }
            //保存二维码开门模式
            AppConfig.getInstance().setQrOpenType(0);
            mOperateView.operateBackState(getString(R.string.set_success));
            mImaBack.setVisibility(View.GONE);
            notifySetList();
        }

    }



    @Override
    public void success() {
        if (isAdded()) {
            hideTitle();
            initListAdapter();
            mImaBack.setVisibility(View.VISIBLE);
        }
        //刷新设置列表
        notifySetList();
    }

    @Override
    public void fail() {
        requestBack();
    }

    public void showTitle(String str) {
        mLinTitle.setVisibility(View.VISIBLE);
        mTvTitle.setText(str);
    }

    public void hideTitle() {
        mLinTitle.setVisibility(View.GONE);
    }
}
