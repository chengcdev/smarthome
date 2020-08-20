package com.mili.smarthome.tkj.set.fragment;


import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.dao.param.VolumeParamDao;
import com.mili.smarthome.tkj.main.adapter.SettingAdapter;
import com.mili.smarthome.tkj.main.entity.SettingModel;
import com.mili.smarthome.tkj.main.fragment.BaseKeyBoardFragment;
import com.mili.smarthome.tkj.main.interf.ISetCallBackListener;
import com.mili.smarthome.tkj.main.widget.KeyBoardRecyclerView;
import com.mili.smarthome.tkj.main.widget.SetSuccessView;
import com.mili.smarthome.tkj.set.Constant;
import com.mili.smarthome.tkj.set.widget.inputview.CustomInputAdapter;
import com.mili.smarthome.tkj.set.widget.inputview.CustomInputView;

import java.util.List;


/**
 * 声音设置
 */

public class SetVolumeFragment extends BaseKeyBoardFragment implements ISetCallBackListener {
    private TextView tvTitle;
    private KeyBoardRecyclerView rv;
    private List<SettingModel> datas;
    private SettingAdapter adapter;
    private LinearLayoutManager manager;
    private SetSuccessView successView;
    private LinearLayout mLinVolumeCall;
    private LinearLayout mLinList;
    private CustomInputView inputView;
    private TextView tvLeftName;
    private boolean isSave;
    private int currentVolume;

    @Override
    public int getLayout() {
        return R.layout.fragment_setting;
    }

    @Override
    public void initView() {
        rv = (KeyBoardRecyclerView) getContentView().findViewById(R.id.rv);
        tvTitle = (TextView) getContentView().findViewById(R.id.tv_title);
        mLinList = (LinearLayout) getContentView().findViewById(R.id.lin_list);
        mLinVolumeCall = (LinearLayout) getContentView().findViewById(R.id.lin_volume_call);
        successView = (SetSuccessView) getContentView().findViewById(R.id.root);
        inputView = (CustomInputView) getContentView().findViewById(R.id.it_content);
        tvLeftName = (TextView) getContentView().findViewById(R.id.tv_name);
    }

    @Override
    public void initAdapter() {
        tvTitle.setText(getString(R.string.setting_volume));

        datas = Constant.getVolumeList();
        initData();
    }

    void initData() {
        rv.initAdapter(datas);
    }


    @Override
    public void initListener() {

    }

    @Override
    public void onResume() {
        super.onResume();
        successView.setSuccessListener(this);
    }


    @Override
    public void setKeyBoard(int viewId, String keyId) {
        switch (keyId) {
            case Constant.KEY_CANCLE:
                //退出界面
                exitFragment(this);
                break;
            case Constant.KEY_CONFIRM:
                String sId = datas.get(rv.getItemPosition()).getkId();
                if (!isSave) {
                    toNotifyView(sId);
                } else {
                    //保存数据
                    toSaveData(sId);
                }
                break;
            case Constant.KEY_UP:
                rv.preScroll();
                break;
            case Constant.KEY_DELETE:

                break;
            case Constant.KEY_NEXT:
                rv.nextScroll();
                break;
            default:
                //通话音量 0，9不能设置
                String title = tvTitle.getText().toString();
                if (getString(R.string.setting_volume).equals(title)) {
                    if ("0".equals(keyId) || "9".equals(keyId)) {
                        return;
                    }
                }
                currentVolume = Integer.parseInt(keyId);
                inputView.addNum(keyId);
                inputView.setCount(0);
                inputView.setFirstFlash(true).notifychange();
                break;
        }
    }

    private void toNotifyView(String sId) {
        switch (sId) {
            //通话音量
            case Constant.SetVolumeId.VOLUME_CALL:
                mLinList.setVisibility(View.GONE);
                mLinVolumeCall.setVisibility(View.VISIBLE);
                tvTitle.setText(getString(R.string.setting_volume));
                tvLeftName.setText(getString(R.string.setting_volume_call));

                //通话音量
                currentVolume = VolumeParamDao.getCallVolume();
                inputView.setFirstFlash(true).init(String.valueOf(currentVolume), 1, CustomInputAdapter.INPUT_TYPE_OTHER);
                inputView.notifychange();
                isSave = true;
                break;
            //提示音
            case Constant.SetVolumeId.VOLUME_TIP:
                mLinList.setVisibility(View.VISIBLE);
                mLinVolumeCall.setVisibility(View.GONE);
                tvTitle.setText(getString(R.string.setting_volume_tip));
                datas = Constant.getTipList();
                initData();
                rv.setItemPosition(AppConfig.getInstance().getTipVolume());
                isSave = true;
                break;
            //按键音
            case Constant.SetVolumeId.VOLUME_KEY:
                mLinList.setVisibility(View.VISIBLE);
                mLinVolumeCall.setVisibility(View.GONE);
                tvTitle.setText(getString(R.string.setting_volume_key));
                datas = Constant.getTipList();
                initData();
                rv.setItemPosition(AppConfig.getInstance().getKeyVolume());
                isSave = true;
                break;
        }
    }

    private void toSaveData(String sId) {
        //设置通话音量
        switch (sId) {
            //通话音量
            case Constant.SetVolumeId.VOLUME_CALL:
                VolumeParamDao.setCallVolume(currentVolume);
                break;
            //否
            case Constant.SetVolumeId.VOLUME_NO:
                if (tvTitle.getText().toString().equals(getString(R.string.setting_volume_tip))) {
                    AppConfig.getInstance().setTipVolume(0);
                } else if (tvTitle.getText().toString().equals(getString(R.string.setting_volume_key))) {
                    AppConfig.getInstance().setKeyVolume(0);
                }
                break;
            //是
            case Constant.SetVolumeId.VOLUME_YES:
                if (tvTitle.getText().toString().equals(getString(R.string.setting_volume_tip))) {
                    AppConfig.getInstance().setTipVolume(1);
                } else if (tvTitle.getText().toString().equals(getString(R.string.setting_volume_key))) {
                    AppConfig.getInstance().setKeyVolume(1);
                }
                break;
        }
        successView.showSuccessView(getString(R.string.setting_success), 1000, this);
    }

    @Override
    public void success() {
        exitFragment(this);
    }

    @Override
    public void fail() {

    }
}
