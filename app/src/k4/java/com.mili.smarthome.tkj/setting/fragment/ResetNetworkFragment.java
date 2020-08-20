package com.mili.smarthome.tkj.setting.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.android.CommSysDef;
import com.android.CommTypeDef;
import com.android.provider.FullDeviceNo;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.App;
import com.mili.smarthome.tkj.dao.param.NetworkParamDao;
import com.mili.smarthome.tkj.entities.param.NetworkParam;
import com.mili.smarthome.tkj.widget.FormatInputView;
import com.mili.smarthome.tkj.widget.InputView;

public class ResetNetworkFragment extends ResetBaseFragment implements View.OnClickListener, View.OnTouchListener {

    private View llPage1, llPage2;
    private FormatInputView tvLocal;
    private FormatInputView tvSubnet;
    private FormatInputView tvGateway;
    private FormatInputView tvDns;
    private FormatInputView tvManager;
    private FormatInputView tvCenter;
    private FormatInputView tvMedia;
    private FormatInputView tvElevator;
    private FrameLayout flElevator;

    private NetworkParam mNetWorkParam;
    private int mPageIndex;
    private int mPageMax;

    @Override
    public boolean onKeyCancel() {
        View root = getView();
        if (root != null) {
            View focus = root.findFocus();
            if (focus instanceof InputView) {
                InputView iptView = (InputView) focus;
                if (iptView.backspace())
                    return true;
            }
        }
        gotoPreviousFragment();
        return true;
    }

    @Override
    public boolean onKeyConfirm() {
        onConfirm();
        return true;
    }

    @Override
    public boolean onKey(int code) {
        inputNum(code);
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_reset_network;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void bindView() {
        super.bindView();
        llPage1 = findView(R.id.ll_page1);
        llPage2 = findView(R.id.ll_page2);
        tvLocal = findView(R.id.tv_local_ip);
        tvSubnet = findView(R.id.tv_subnet_mask);
        tvGateway = findView(R.id.tv_gateway_ip);
        tvDns = findView(R.id.tv_dns);
        tvManager = findView(R.id.tv_manager_ip);
        tvCenter = findView(R.id.tv_center_ip);
        tvMedia = findView(R.id.tv_media_ip);
        tvElevator = findView(R.id.tv_elevator_ip);
        flElevator = findView(R.id.fl_elevator);

        ImageButton up = findView(R.id.ib_up);
        assert up != null;
        up.setOnClickListener(this);
        ImageButton down = findView(R.id.ib_down);
        assert down != null;
        down.setOnClickListener(this);

        tvLocal.setOnTouchListener(this);
        tvSubnet.setOnTouchListener(this);
        tvGateway.setOnTouchListener(this);
        tvDns.setOnTouchListener(this);
        tvManager.setOnTouchListener(this);
        tvCenter.setOnTouchListener(this);
        tvMedia.setOnTouchListener(this);
        tvElevator.setOnTouchListener(this);
    }

    @Override
    protected void bindData() {
        super.bindData();
        mNetWorkParam = NetworkParamDao.getNetWorkParam();
        tvLocal.setText(mNetWorkParam.getLocalIp());
        tvSubnet.setText(mNetWorkParam.getSubNet());
        tvGateway.setText(mNetWorkParam.getGateway());
        tvDns.setText(mNetWorkParam.getDNS1());
        tvManager.setText(mNetWorkParam.getAdminIp());
        tvCenter.setText(mNetWorkParam.getCenterIp());
        tvMedia.setText(mNetWorkParam.getMediaIp());
        tvElevator.setText(mNetWorkParam.getElevatorIp());

        // 区口机时不显示电梯控制器
        FullDeviceNo fullDeviceNo = new FullDeviceNo(getContext());
        if (fullDeviceNo.getDeviceType() == CommTypeDef.DeviceType.DEVICE_TYPE_AREA) {
            flElevator.setVisibility(View.GONE);
        } else {
            flElevator.setVisibility(View.VISIBLE);
        }
        mPageMax = 2;
        mPageIndex = 0;
        showPage(mPageIndex);
    }

    private void onConfirm() {
        mNetWorkParam.setLocalIp(tvLocal.getTrimText().toString());
        mNetWorkParam.setSubNet(tvSubnet.getTrimText().toString());
        mNetWorkParam.setGateway(tvGateway.getTrimText().toString());
        mNetWorkParam.setDNS1(tvDns.getTrimText().toString());
        mNetWorkParam.setAdminIp(tvManager.getTrimText().toString());
        mNetWorkParam.setCenterIp(tvCenter.getTrimText().toString());
        mNetWorkParam.setMediaIp(tvMedia.getTrimText().toString());
        mNetWorkParam.setElevatorIp(tvElevator.getTrimText().toString());
        NetworkParamDao.setNetworkParam(mNetWorkParam);

        //设置网络
        Intent intent = new Intent(CommSysDef.BROADCAST_NAME_IP);
        App.getInstance().sendBroadcast(intent);

        gotoNextFragment();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_down:
                if (mPageIndex < mPageMax-1) {
                    mPageIndex++;
                    showPage(mPageIndex);
                }
                break;
            case R.id.ib_up:
                if (mPageIndex > 0) {
                    mPageIndex--;
                    showPage(mPageIndex);
                }
                break;
        }
    }

    /**
     *  显示每页内容
     * @param page  当前页索引
     */
    private void showPage(int page) {
        switch (page) {
            case 0:
                llPage1.setVisibility(View.VISIBLE);
                llPage2.setVisibility(View.GONE);
                tvLocal.requestFocus();
                break;
            case 1:
                llPage1.setVisibility(View.GONE);
                llPage2.setVisibility(View.VISIBLE);
                tvManager.requestFocus();
                break;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() != MotionEvent.ACTION_UP) {
            return false;
        }
        switch(view.getId()) {
            case R.id.tv_local_ip:
                tvLocal.requestFocus();
                tvLocal.setCursorIndex(0);
                break;
            case R.id.tv_subnet_mask:
                tvSubnet.requestFocus();
                tvSubnet.setCursorIndex(0);
                break;
            case R.id.tv_gateway_ip:
                tvGateway.requestFocus();
                tvGateway.setCursorIndex(0);
                break;
            case R.id.tv_dns:
                tvDns.requestFocus();
                tvDns.setCursorIndex(0);
                break;
            case R.id.tv_manager_ip:
                tvManager.requestFocus();
                tvManager.setCursorIndex(0);
                break;
            case R.id.tv_center_ip:
                tvCenter.requestFocus();
                tvCenter.setCursorIndex(0);
                break;
            case R.id.tv_media_ip:
                tvMedia.requestFocus();
                tvMedia.setCursorIndex(0);
                break;
            case R.id.tv_elevator_ip:
                tvElevator.requestFocus();
                tvElevator.setCursorIndex(0);
                break;
        }
        return false;
    }
}
