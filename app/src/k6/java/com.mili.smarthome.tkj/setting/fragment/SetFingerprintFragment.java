package com.mili.smarthome.tkj.setting.fragment;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.CommSysDef;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.setting.adapter.ItemSelectorAdapter;
import com.mili.smarthome.tkj.setting.entities.SettingFunc;
import com.mili.smarthome.tkj.utils.AppUtils;
import com.mili.smarthome.tkj.view.SetOperateView;

/**
 * 设置指纹识别
 */
public class SetFingerprintFragment extends BaseFragment implements SetOperateView.IOperateListener, View.OnClickListener {

    private static final int FINGER_PRINT_ADD = 0x030501;
    private static final int FINGER_PRINT_DELETE = 0x030502;
    private static final int FINGER_PRINT_CLEAR = 0x030503;
    private static final int FINGER_PRINT_CODE = 0x0305;

    private TextView mTvTitle;
    private SetOperateView mOperateView;
    private RecyclerView mRecyclerView;
    private SetFingerListAdapter fingerListAdapter;
    private LinearLayout mLinTitle;
    private ImageView mImaBack;
    private int mSetFunc = FINGER_PRINT_CODE;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_fing_print;
    }

    @Override
    protected void bindView() {
        mRecyclerView = findView(R.id.recyclerview);
        mOperateView = findView(R.id.rootview);
        mTvTitle = findView(R.id.tv_title);
        mLinTitle = findView(R.id.ll_title);
        mImaBack = findView(R.id.iv_back);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    protected void bindData() {
        mOperateView.setSuccessListener(this);
        mImaBack.setOnClickListener(this);
        mImaBack.setVisibility(View.VISIBLE);

        initFingerPrintAdapter();
     }

    @Override
    public void onResume() {
        super.onResume();
        setBackVisibility(View.GONE);
        mSetFunc = FINGER_PRINT_CODE;
    }

    private void initFingerPrintAdapter() {
        fingerListAdapter = new SetFingerListAdapter(mContext);
        mRecyclerView.setAdapter(fingerListAdapter);
        //是否启用指纹识别
        int faceRecognition = AppConfig.getInstance().getFingerprint();
        if (faceRecognition == 1) {
            //启用指纹识别
            fingerListAdapter.setStringArray(getResources().getStringArray(R.array.setting_finger_print));
        }else {
            //禁用指纹识别
            fingerListAdapter.setStringArray(getResources().getStringArray(R.array.setting_enabled2));
        }
        fingerListAdapter.setSelection(faceRecognition);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                if (mSetFunc == FINGER_PRINT_ADD || mSetFunc == FINGER_PRINT_DELETE || mSetFunc == FINGER_PRINT_CLEAR) {
                    mSetFunc = FINGER_PRINT_CODE;
                    hideTitle();
                    initFingerPrintAdapter();
                }else {
                    setBackVisibility(View.VISIBLE);
                    requestBack();
                }
                break;
        }
    }


    private class SetFingerListAdapter extends ItemSelectorAdapter {


        private SetClearDatasFragment clearFaceFragment;
        private FingerPrintManageFragment fingerPrintFragment;

        public SetFingerListAdapter(Context context) {
            super(context);
        }

        @Override
        protected int getStringArrayId() {
            return R.array.setting_face;
        }

        @Override
        protected void onItemClick(int position) {
            switch (position) {
                //禁用
                case 0:
                    AppConfig.getInstance().setFingerprint(0);
                    mOperateView.operateBackState(getString(R.string.set_success));
                    mImaBack.setVisibility(View.GONE);
                    AppUtils.getInstance().sendReceiver(CommSysDef.BROADCAST_ENABLE_FINGER);
                    notifySetList();
                    break;
                //启用
                case 1:
                    AppConfig.getInstance().setFingerprint(1);
                    mOperateView.operateBackState(getString(R.string.set_success));
                    mImaBack.setVisibility(View.GONE);
                    AppUtils.getInstance().sendReceiver(CommSysDef.BROADCAST_ENABLE_FINGER);
                    notifySetList();
                    break;
                //指纹识别添加
                case 2:
                    initFingerPrintAdapter();
                    mSetFunc = FINGER_PRINT_ADD;
                    if (fingerPrintFragment == null) {
                        fingerPrintFragment = new FingerPrintManageFragment();
                    }
                    AppUtils.getInstance().replaceFragment(getActivity(), fingerPrintFragment, R.id.fl_container,
                            "addfinger", FragmentFactory.ARGS_FUNCCODE, SettingFunc.SET_FINGER_PRINT_ADD);
                    break;
                //指纹识别删除
                case 3:
                    initFingerPrintAdapter();
                    mSetFunc = FINGER_PRINT_DELETE;
                    if (fingerPrintFragment == null) {
                        fingerPrintFragment = new FingerPrintManageFragment();
                    }
                    AppUtils.getInstance().replaceFragment(getActivity(), fingerPrintFragment, R.id.fl_container,
                            "fingerPrintFragment", FragmentFactory.ARGS_FUNCCODE, SettingFunc.SET_FINGER_PRINT_DELETE);
                    break;
                //清空指纹识别
                case 4:
                    initFingerPrintAdapter();
                    mSetFunc = FINGER_PRINT_CLEAR;
                    if (clearFaceFragment == null) {
                        clearFaceFragment = new SetClearDatasFragment();
                    }
                    AppUtils.getInstance().replaceFragment(getActivity(), clearFaceFragment, R.id.fl_container,
                            "SetClearFaceFragment", FragmentFactory.ARGS_FUNCCODE, SettingFunc.SET_FINGER_PRINT_CLEAR);
                    break;
            }
        }

    }



    @Override
    public void success() {
        if (isAdded()) {
            initFingerPrintAdapter();
            mImaBack.setVisibility(View.VISIBLE);
        }
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
