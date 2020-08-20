package com.mili.smarthome.tkj.setting.fragment;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.CommSysDef;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.App;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.base.K4BaseFragment;
import com.mili.smarthome.tkj.constant.Constant;
import com.mili.smarthome.tkj.face.horizon.HorizonFacePresenter;
import com.mili.smarthome.tkj.setting.adapter.ItemSelectorAdapter;

public class SetFaceFragment extends K4BaseFragment implements ItemSelectorAdapter.OnItemClickListener, View.OnClickListener {

    private static final int FACE_FUNC_STATUS = 0x030301;
    private static final int FACE_FUNC_CLEAR = 0x030302;
    private static final int FACE_FUNC_LEVEL = 0x030303;
    private static final int FACE_FUNC_DETECT = 0x030304;

    private RecyclerView mRecyclerView;
    private ItemSelectorAdapter mAdapter;
    private TextView mTvHead, mTvHint;
    private View mLine;
    private RelativeLayout mLlButton;

    private int mSetFunc = FACE_FUNC_STATUS;

    @Override
    public boolean onKeyCancel() {
        super.onKeyCancel();
        if (mSetFunc == FACE_FUNC_STATUS) {
            exitFragment();
        } else {
            showFaceStatus();
        }
        return true;
    }

    @Override
    public boolean onKeyConfirm() {
        super.onKeyConfirm();
        if (mSetFunc == FACE_FUNC_CLEAR) {
            //清空人脸记录
            boolean success = new HorizonFacePresenter().clearFaceInfo();
            showSetHint(success);
        } else {
            int position = mAdapter.getSelection();
            onItemClick(position);
        }
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_face;
    }

    @Override
    protected void bindView() {
        super.bindView();
        mTvHead = findView(R.id.tv_head);
        mTvHint = findView(R.id.tv_hint);
        mLine = findView(R.id.v_line);
        mLlButton = findView(R.id.ll_button);

        ImageButton ibUp = findView(R.id.ib_up);
        ImageButton ibDown = findView(R.id.ib_down);
        assert ibUp != null;
        ibUp.setOnClickListener(this);
        assert ibDown != null;
        ibDown.setOnClickListener(this);

        mRecyclerView = findView(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    protected void bindData() {
        super.bindData();
        mAdapter = new ItemSelectorAdapter(mContext);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        showFaceStatus();
    }

    protected void setHead(int resId) {
        if (mTvHead != null) {
            mTvHead.setText(resId);
        }
    }

    private void showFaceStatus() {
        mSetFunc = FACE_FUNC_STATUS;
        int arrayId;
        int enable;
        if (AppConfig.getInstance().getFaceRecognition() != 1) {
            enable = 0;
            arrayId = R.array.setting_enabled2;
        } else {
            enable = 1;
            arrayId = R.array.setting_face;
        }
        mAdapter.setStringArray(mContext.getResources().getStringArray(arrayId));
        mAdapter.setSelection(enable);

        setHead(R.string.setting_0303);
        mRecyclerView.setVisibility(View.VISIBLE);
        mLlButton.setVisibility(View.VISIBLE);
        mTvHint.setVisibility(View.INVISIBLE);
        mLine.setVisibility(View.INVISIBLE);
    }

    private void showFaceClear() {
        mSetFunc = FACE_FUNC_CLEAR;

        mTvHint.setText(R.string.face_manage_clear_confirm);
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLlButton.setVisibility(View.INVISIBLE);
        mTvHint.setVisibility(View.VISIBLE);
        mLine.setVisibility(View.VISIBLE);

    }

    private void showFaceLevel() {
        mSetFunc = FACE_FUNC_LEVEL;
        mAdapter.setStringArray(mContext.getResources().getStringArray(R.array.setting_face_level));
        mAdapter.setSelection(AppConfig.getInstance().getFaceSafeLevel());

        setHead(R.string.setting_030302);
        mRecyclerView.setVisibility(View.VISIBLE);
        mLlButton.setVisibility(View.INVISIBLE);
        mTvHint.setVisibility(View.INVISIBLE);
        mLine.setVisibility(View.INVISIBLE);
    }

    private void showFaceDetect() {
        mSetFunc = FACE_FUNC_DETECT;
        mAdapter.setStringArray(mContext.getResources().getStringArray(R.array.setting_enabled2));
        mAdapter.setSelection(AppConfig.getInstance().getFaceLiveCheck());

        setHead(R.string.setting_030303);
        mRecyclerView.setVisibility(View.VISIBLE);
        mLlButton.setVisibility(View.INVISIBLE);
        mTvHint.setVisibility(View.INVISIBLE);
        mLine.setVisibility(View.INVISIBLE);
    }

    private void showSetHint(boolean success) {
        if (success) {
            mTvHint.setText(R.string.set_success);
        } else {
            mTvHint.setText(R.string.set_fail);
        }

        mRecyclerView.setVisibility(View.INVISIBLE);
        mLlButton.setVisibility(View.INVISIBLE);
        mTvHint.setVisibility(View.VISIBLE);
        mLine.setVisibility(View.VISIBLE);

        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                exitFragment();
            }
        }, Constant.SET_HINT_TIMEOUT);
    }

    @Override
    public void onItemClick(int position) {
        switch (mSetFunc) {
            case FACE_FUNC_STATUS:
                if (position < 2) {
                    AppConfig.getInstance().setFaceRecognition(position);
                    showSetHint(true);
                    // 发送广播
                    Intent intent = new Intent(CommSysDef.BROADCAST_ENABLE_FACE);
                    App.getInstance().sendBroadcast(intent);
                } else if (position == 2) {
                    showFaceClear();
                } else if (position == 3) {
                    showFaceLevel();
                } else {
                    showFaceDetect();
                }
                break;

            case FACE_FUNC_LEVEL:
                AppConfig.getInstance().setFaceSafeLevel(position);
                showSetHint(true);
                break;

            case FACE_FUNC_DETECT:
                AppConfig.getInstance().setFaceLiveCheck(position);
                showSetHint(true);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_up:
                if (mSetFunc == FACE_FUNC_STATUS) {
                    mAdapter.prePage();
                }
                break;
            case R.id.ib_down:
                if (mSetFunc == FACE_FUNC_STATUS) {
                    mAdapter.nextPage();
                }
                break;
        }
    }
}
