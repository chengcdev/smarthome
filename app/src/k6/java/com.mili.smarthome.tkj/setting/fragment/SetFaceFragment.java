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
import com.mili.smarthome.tkj.appfunc.BuildConfigHelper;
import com.mili.smarthome.tkj.setting.adapter.ItemSelectorAdapter;
import com.mili.smarthome.tkj.setting.entities.SettingFunc;
import com.mili.smarthome.tkj.utils.AppUtils;
import com.mili.smarthome.tkj.view.SetOperateView;

import java.util.Arrays;

/**
 * 设置人脸识别
 */
public class SetFaceFragment extends BaseFragment implements SetOperateView.IOperateListener, View.OnClickListener {

    private static final int FACE_SECU_LEVEL = 0x030301;
    private static final int LOCK_LIVING_CHECK = 0x030302;
    private static final int FACE_CLEAR = 0x030303;
    private static final int IPC_SET = 0x030304;
    private static final int FACE_CODE = 0x0303;

    private TextView mTvTitle;
    private SetOperateView mOperateView;
    private RecyclerView mRecyclerView;
    private SetFaceListAdapter faceListAdapter;
    private LinearLayout mLinTitle;
    private ImageView mImaBack;
    private View llPage;
    private TextView mTvPrePage;
    private TextView mTvNextPage;
    private int mSetFunc = FACE_CODE;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_face;
    }

    @Override
    protected void bindView() {
        mRecyclerView = findView(R.id.recyclerview);
        mOperateView = findView(R.id.rootview);
        mTvTitle = findView(R.id.tv_title);
        mLinTitle = findView(R.id.ll_title);
        mImaBack = findView(R.id.iv_back);
        llPage = findView(R.id.ll_page);
        mTvPrePage = findView(R.id.tv_pre_page);
        mTvNextPage = findView(R.id.tv_next_page);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    protected void bindData() {
        mOperateView.setSuccessListener(this);
        mTvPrePage.setOnClickListener(this);
        mTvNextPage.setOnClickListener(this);
        mImaBack.setOnClickListener(this);
        mImaBack.setVisibility(View.VISIBLE);

        initFaceAdapter();
     }

    @Override
    public void onResume() {
        super.onResume();
        mSetFunc = FACE_CODE;
        setBackVisibility(View.GONE);
    }

    private void initFaceAdapter() {
        faceListAdapter = new SetFaceListAdapter(mContext);
        mRecyclerView.setAdapter(faceListAdapter);
        //是否启用人脸
        int faceRecognition = AppConfig.getInstance().getFaceRecognition();
        String[] array;
        if (faceRecognition == 1) {
            //启用人脸
            array = getResources().getStringArray(R.array.setting_face);
            if (BuildConfigHelper.isEnabledIPC()) {
                int newLen = array.length + 1;
                array = Arrays.copyOf(array, newLen);
                array[newLen - 1] = mContext.getString(R.string.setting_030304);
            }
        }else {
            //禁用人脸
            array = getResources().getStringArray(R.array.setting_enabled2);
        }
        faceListAdapter.setStringArray(array);
        faceListAdapter.setSelection(faceRecognition);
        llPage.setVisibility(faceListAdapter.isTurnable() ? View.VISIBLE : View.GONE);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                if (mSetFunc == FACE_SECU_LEVEL || mSetFunc == LOCK_LIVING_CHECK || mSetFunc == FACE_CLEAR) {
                    mSetFunc = FACE_CODE;
                    hideTitle();
                    initFaceAdapter();
                }else {
                    setBackVisibility(View.VISIBLE);
                    requestBack();
                }
                break;
            case R.id.tv_pre_page:
                faceListAdapter.prePage();
                break;
            case R.id.tv_next_page:
                faceListAdapter.nextPage();
                break;
        }
    }


    private class SetFaceListAdapter extends ItemSelectorAdapter {

        private SetRtspFragment setRtspFragment;
        private SetClearDatasFragment clearFaceFragment;
        private SecuLevelAdapter secuLevelAdapter;
        private LivingCheckAdapter livingCheckAdapter;

        public SetFaceListAdapter(Context context) {
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
                    AppConfig.getInstance().setFaceRecognition(0);
                    //人体感应选择到触发开屏
                    AppConfig.getInstance().setBodyInduction(0);
                    mOperateView.operateBackState(getString(R.string.set_success));
                    mImaBack.setVisibility(View.GONE);
                    AppUtils.getInstance().sendReceiver(CommSysDef.BROADCAST_ENABLE_FACE);
                    break;
                //启用
                case 1:
                    AppConfig.getInstance().setFaceRecognition(1);
                    //人体感应选择到人脸识别
                    AppConfig.getInstance().setBodyInduction(1);
                    mOperateView.operateBackState(getString(R.string.set_success));
                    mImaBack.setVisibility(View.GONE);
                    AppUtils.getInstance().sendReceiver(CommSysDef.BROADCAST_ENABLE_FACE);
                    break;
                //清空人脸记录
                case 2:
                    //initFaceAdapter();
                    mSetFunc = FACE_CLEAR;
                    if (clearFaceFragment == null) {
                        clearFaceFragment = new SetClearDatasFragment();
                    }
                    AppUtils.getInstance().replaceFragment(getActivity(), clearFaceFragment, R.id.fl_container,
                            "SetConfirmFragment", FragmentFactory.ARGS_FUNCCODE, SettingFunc.SET_FACE_CLEAR);
                    break;
                //安全级别
                case 3:
                    mSetFunc = FACE_SECU_LEVEL;
                    showTitle(getString(R.string.setting_030302));
                    if (secuLevelAdapter == null) {
                        secuLevelAdapter = new SecuLevelAdapter(getContext());
                    }
                    mRecyclerView.setAdapter(secuLevelAdapter);
                    int faceSafeLevel = AppConfig.getInstance().getFaceSafeLevel();
                    secuLevelAdapter.setSelection(faceSafeLevel);
                    llPage.setVisibility(View.GONE);
                    break;
                //人脸活体检测
                case 4:
                    mSetFunc = LOCK_LIVING_CHECK;
                    showTitle(getString(R.string.setting_030303));
                    if (livingCheckAdapter == null) {
                        livingCheckAdapter = new LivingCheckAdapter(getContext());
                    }
                    mRecyclerView.setAdapter(livingCheckAdapter);
                    int faceLiveCheck = AppConfig.getInstance().getFaceLiveCheck();
                    livingCheckAdapter.setSelection(faceLiveCheck);
                    llPage.setVisibility(View.GONE);
                    break;
                //IPC地址
                case 5:
                    //initFaceAdapter();
                    mSetFunc = IPC_SET;
                    if (setRtspFragment == null) {
                        setRtspFragment = new SetRtspFragment();
                    }
                    AppUtils.getInstance().replaceFragment(getActivity(), setRtspFragment, R.id.fl_container,
                            "SetRtspFragment");
                    break;
            }
        }

    }

    private class SecuLevelAdapter extends ItemSelectorAdapter {

        private SecuLevelAdapter(Context context) {
            super(context);
        }

        @Override
        protected int getStringArrayId() {
            return R.array.setting_face_secu_level;
        }

        @Override
        protected void onItemClick(int position) {
            switch (position) {
                //高
                case 0:
                    AppConfig.getInstance().setFaceSafeLevel(0);
                    break;
                //正常
                case 1:
                    AppConfig.getInstance().setFaceSafeLevel(1);
                    break;
                //普通
                case 2:
                    AppConfig.getInstance().setFaceSafeLevel(2);
                    break;
            }
            mOperateView.operateBackState(getString(R.string.set_success));
            mImaBack.setVisibility(View.GONE);
        }

    }

    private class LivingCheckAdapter extends ItemSelectorAdapter {

        private LivingCheckAdapter(Context context) {
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
                    AppConfig.getInstance().setFaceLiveCheck(0);
                    break;
                //启用
                case 1:
                    AppConfig.getInstance().setFaceLiveCheck(1);
                    break;
            }
            mOperateView.operateBackState(getString(R.string.set_success));
            mImaBack.setVisibility(View.GONE);
        }
    }

    @Override
    public void success() {
        if (isAdded()) {
            hideTitle();
            initFaceAdapter();
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
