package com.mili.smarthome.tkj.setting.fragment;

import android.view.View;

import com.android.CommSysDef;
import com.android.CommTypeDef;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.ContextProxy;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.dao.param.NetworkParamDao;
import com.mili.smarthome.tkj.entities.param.NetworkParam;
import com.mili.smarthome.tkj.widget.FormatInputView;

public class ResetNetworkFragment extends ResetBaseFragment implements View.OnClickListener {

    private FormatInputView tvLocal;
    private FormatInputView tvSubnet;
    private FormatInputView tvGateway;
    private FormatInputView tvDns;
    private FormatInputView tvManager;
    private FormatInputView tvCenter;
    private FormatInputView tvMedia;
    private FormatInputView tvFace;
    private FormatInputView tvElevator;

    private View vgLocal;
    private View vgSubnet;
    private View vgGateway;
    private View vgDns;
    private View vgManager;
    private View vgCenter;
    private View vgMedia;
    private View vgFace;
    private View vgElevator;
    private View[] vwList;

    private int mPage = 0, mMaxPage;
    private NetworkParam mNetworkParam;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_reset_network;
    }

    @Override
    protected void bindView() {
        tvLocal = findView(R.id.tv_local_ip);
        tvSubnet = findView(R.id.tv_subnet_mask);
        tvGateway = findView(R.id.tv_gateway_ip);
        tvDns = findView(R.id.tv_dns_ip);
        tvManager = findView(R.id.tv_manager_ip);
        tvCenter = findView(R.id.tv_center_ip);
        tvMedia = findView(R.id.tv_media_ip);
        tvFace = findView(R.id.tv_face_ip);
        tvElevator = findView(R.id.tv_elevator_ip);

        vgLocal = findView(R.id.vg_local);
        vgSubnet = findView(R.id.vg_subnet);
        vgGateway = findView(R.id.vg_gateway);
        vgDns = findView(R.id.vg_dns);
        vgManager = findView(R.id.vg_manager);
        vgCenter = findView(R.id.vg_center);
        vgMedia = findView(R.id.vg_media);
        vgFace = findView(R.id.vg_face);
        vgElevator = findView(R.id.vg_elevator);

        findView(R.id.btn_cancel).setOnClickListener(this);
        findView(R.id.btn_confirm).setOnClickListener(this);
    }

    @Override
    protected void bindData() {
        mNetworkParam = NetworkParamDao.getNetWorkParam();
        tvLocal.setText(mNetworkParam.getLocalIp());
        tvSubnet.setText(mNetworkParam.getSubNet());
        tvGateway.setText(mNetworkParam.getGateway());
        tvDns.setText(mNetworkParam.getDNS1());
        tvManager.setText(mNetworkParam.getAdminIp());
        tvCenter.setText(mNetworkParam.getCenterIp());
        tvMedia.setText(mNetworkParam.getMediaIp());
        tvFace.setText(mNetworkParam.getFaceIp());
        tvElevator.setText(mNetworkParam.getElevatorIp());

        int deviceType = AppConfig.getInstance().getDevType();
        if (deviceType == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
            vwList = new View[]{vgLocal, vgSubnet, vgGateway, vgDns, vgManager, vgCenter, vgMedia, vgElevator};
        } else {
            vgElevator.setVisibility(View.GONE);
            vwList = new View[]{vgLocal, vgSubnet, vgGateway, vgDns, vgManager, vgCenter, vgMedia};
        }
        mMaxPage = (vwList.length - 1) / 4 + 1;
        showPage(0);
    }

    private void showPage(int page) {
        int start = page * 4;
        int end = (page + 1) * 4 - 1;
        for (int i = 0; i < vwList.length; i++) {
            if (i >= start && i <= end) {
                vwList[i].setVisibility(View.VISIBLE);
            } else {
                vwList[i].setVisibility(View.GONE);
            }
        }
        vwList[start].requestFocus();
        mPage = page;
    }

    private void prePage() {
        if (mPage == 0)
            return;
        showPage(--mPage);
    }

    private void nextPage() {
        if (mPage >= mMaxPage - 1)
            return;
        showPage(++mPage);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                requestBack();
                break;
            case R.id.btn_confirm:
                onConfirm();
                break;
        }
    }

    @Override
    public boolean onKeyEvent(int keyCode, int keyState) {
        if (keyState == KEYSTATE_DOWN)
            return false;
        switch (keyCode) {
            case KEYCODE_UP:
                prePage();
                break;
            case KEYCODE_DOWN:
                nextPage();
                break;
            case KEYCODE_0:
                inputNum(0);
                break;
            case KEYCODE_1:
            case KEYCODE_2:
            case KEYCODE_3:
            case KEYCODE_4:
            case KEYCODE_5:
            case KEYCODE_6:
            case KEYCODE_7:
            case KEYCODE_8:
            case KEYCODE_9:
                inputNum(keyCode);
                break;
            case KEYCODE_BACK:
                backspace();
                break;
            case KEYCODE_CALL:
                onConfirm();
                break;
        }
        return true;
    }

    private void onConfirm() {
        mNetworkParam.setLocalIp(tvLocal.getTrimText().toString());
        mNetworkParam.setSubNet(tvSubnet.getTrimText().toString());
        mNetworkParam.setGateway(tvGateway.getTrimText().toString());
        mNetworkParam.setDNS1(tvDns.getTrimText().toString());
        mNetworkParam.setAdminIp(tvManager.getTrimText().toString());
        mNetworkParam.setCenterIp(tvCenter.getTrimText().toString());
        mNetworkParam.setMediaIp(tvMedia.getTrimText().toString());
        mNetworkParam.setFaceIp(tvFace.getTrimText().toString());
        mNetworkParam.setElevatorIp(tvElevator.getTrimText().toString());
        NetworkParamDao.setNetworkParam(mNetworkParam);

        //设置网络
        ContextProxy.sendBroadcast(CommSysDef.BROADCAST_NAME_IP);

        gotoNextFragment();
    }
}
