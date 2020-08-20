package com.mili.smarthome.tkj.setting.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.CommSysDef;
import com.android.CommTypeDef;
import com.android.Common;
import com.android.provider.FullDeviceNo;
import com.android.provider.NetworkHelp;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.adapter.KeyBoardAdapter;
import com.mili.smarthome.tkj.app.App;
import com.mili.smarthome.tkj.app.AppPreferences;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.constant.Constant;
import com.mili.smarthome.tkj.entity.KeyBoardBean;
import com.mili.smarthome.tkj.main.activity.MainActivity;
import com.mili.smarthome.tkj.setting.entities.NetWorkSettingEntity;
import com.mili.smarthome.tkj.utils.AppUtils;
import com.mili.smarthome.tkj.view.KeyBoardView;
import com.mili.smarthome.tkj.view.SetOperateView;
import com.mili.smarthome.tkj.widget.FormatInputView;
import com.mili.smarthome.tkj.widget.InputView;

import java.util.ArrayList;
import java.util.List;

/**
 * 网络设置
 */
public class SetNetworkFragment extends BaseFragment implements KeyBoardAdapter.IKeyBoardListener,
        SetOperateView.IOperateListener {

    private FormatInputView tvLocal;
    private KeyBoardView keyBoardView;
    private String net_ip = "";
    private String net_mask = "";
    private String net_gateway = "";
    private String net_admin = "";
    private String net_center = "";
    private String net_media = "";
    private String net_face = "";
    private String net_elevator = "";
    private String net_dns = "";
    private NetworkHelp networkHelp;
    private int index = 0;
    private TextView mTvLeftName;
    private SetOperateView mRooView;
    //恢复出厂第一次设置
    private boolean isFirstEnable;
    private LinearLayout mLinTitle;
    private final String NET_IP_ID = "net_ip_id";
    private final String NET_MASK_ID = "net_mask_id";
    private final String NET_GATEWAY_ID = "net_gateway_id";
    private final String NET_ADMIN_ID = "net_admin_id";
    private final String NET_CENTER_ID = "net_center_id";
    private final String NET_MEDIA_ID = "net_media_id";
    private final String NET_FACE_ID = "net_face_id";
    private final String NET_ELEVATOR_ID = "net_elevator_id";
    private final String NET_DNS_ID = "net_dns_id";

    private List<NetWorkSettingEntity> mNetList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_network;
    }

    @Override
    protected void bindView() {
        tvLocal = findView(R.id.tv_local_ip);
        keyBoardView = findView(R.id.keyboardview);
        mTvLeftName = findView(R.id.tv_left_name);
        mRooView = findView(R.id.rootview);
        mLinTitle = findView(R.id.lin_title);
    }

    @Override
    protected void bindData() {
        keyBoardView.init(KeyBoardView.KEY_BOARD_SET);
        keyBoardView.setKeyBoardListener(this);
        mRooView.setSuccessListener(this);

        networkHelp = new NetworkHelp();
        net_ip = Common.intToIP(networkHelp.getIp());
        net_mask = Common.intToIP(networkHelp.getSubNet());
        net_gateway = Common.intToIP(networkHelp.getDefaultGateway());
        net_dns = Common.intToIP(networkHelp.getDNS1());
        net_admin = Common.intToIP(networkHelp.getManagerIP());
        net_center = Common.intToIP(networkHelp.getCenterIP());
        net_media = Common.intToIP(networkHelp.getMediaServer());
        net_face = Common.intToIP(networkHelp.getFaceIp());
        net_elevator = Common.intToIP(networkHelp.getElevatorIp());

        mNetList.add(new NetWorkSettingEntity(NET_IP_ID, R.string.setting_ip, net_ip));
        mNetList.add(new NetWorkSettingEntity(NET_MASK_ID, R.string.setting_mask, net_mask));
        mNetList.add(new NetWorkSettingEntity(NET_GATEWAY_ID, R.string.setting_gateway, net_gateway));
        mNetList.add(new NetWorkSettingEntity(NET_DNS_ID, R.string.setting_dns, net_dns));
        mNetList.add(new NetWorkSettingEntity(NET_ADMIN_ID, R.string.setting_admin, net_admin));
        mNetList.add(new NetWorkSettingEntity(NET_CENTER_ID, R.string.setting_center_server, net_center));
        mNetList.add(new NetWorkSettingEntity(NET_MEDIA_ID, R.string.setting_media_server, net_media));
        FullDeviceNo fullDeviceNo = new FullDeviceNo(getContext());
        if (fullDeviceNo.getDeviceType() == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
            mNetList.add(new NetWorkSettingEntity(NET_ELEVATOR_ID, R.string.setting_elevator, net_elevator));
        }
        if (AppConfig.getInstance().isFaceEnabled()) {
            mNetList.add(new NetWorkSettingEntity(NET_FACE_ID, R.string.setting_face_server, net_face));
        }
        //添加ip
        tvLocal.setText(net_ip);
        tvLocal.requestFocus();
        //是否恢复出厂后第一次设置
        initFirstSet();
    }

    private void initFirstSet() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            isFirstEnable = bundle.getBoolean(Constant.IntentId.INTENT_KEY);
        }
        if (isFirstEnable) {
            mLinTitle.setVisibility(View.GONE);
        } else {
            mLinTitle.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onKeyBoardListener(View view, int potion, KeyBoardBean keyBoardBean) {
        switch (keyBoardBean.getkId()) {
            case Const.KeyBoardId.KEY_CANCEL:
                backspace();
                break;
            case Const.KeyBoardId.KEY_CONFIRM:
                String str = tvLocal.getText().toString();
                for (int i = 0; i < str.length(); i++) {
                    tvLocal.backspace();
                }
                tvLocal.requestFocus();
                if (index == mNetList.size()) {
                    index = -1;
                }
                index++;
                setNetParam();
                break;
            default:
                String num = keyBoardBean.getName();
                inputNum(Integer.parseInt(num));
                NetWorkSettingEntity netWorkSettingEntity = mNetList.get(index);
                if (netWorkSettingEntity == null) {
                    return;
                }
                switch (netWorkSettingEntity.getId()) {
                    //修改ip
                    case NET_IP_ID:
                        net_ip = tvLocal.getText().toString();
                        break;
                    //修改子网掩码
                    case NET_MASK_ID:
                        net_mask = tvLocal.getText().toString();
                        break;
                    //修改网关
                    case NET_GATEWAY_ID:
                        net_gateway = tvLocal.getText().toString();
                        break;
                    //修改dns
                    case NET_DNS_ID:
                        net_dns = tvLocal.getText().toString();
                        break;
                    //修改管理员ip
                    case NET_ADMIN_ID:
                        net_admin = tvLocal.getText().toString();
                        break;
                    //修改中心ip
                    case NET_CENTER_ID:
                        net_center = tvLocal.getText().toString();
                        break;
                    //修改流媒体ip
                    case NET_MEDIA_ID:
                        net_media = tvLocal.getText().toString();
                        break;
                    //修改人脸ip
                    case NET_FACE_ID:
                        net_face = tvLocal.getText().toString();
                        break;
                    //修改电梯控制器ip
                    case NET_ELEVATOR_ID:
                        net_elevator = tvLocal.getText().toString();
                        break;

                }
        }
    }

    @SuppressLint("SetTextI18n")
    private void setNetParam() {
        if (index >= mNetList.size()) {
            //保存
            saveDatas();
            return;
        }
        if (index <= -1) {
            //保存
            requestBack();
            return;
        }
        NetWorkSettingEntity netWorkSettingEntity = mNetList.get(index);
        if (netWorkSettingEntity == null) {
            return;
        }

        switch (netWorkSettingEntity.getId()) {
            //修改ip
            case NET_IP_ID:
                mTvLeftName.setText(getString(R.string.setting_ip));
                tvLocal.setText(net_ip);
                break;
            //修改子网掩码
            case NET_MASK_ID:
                mTvLeftName.setText(getString(R.string.setting_mask));
                tvLocal.setText(net_mask);
                break;
            //修改网关
            case NET_GATEWAY_ID:
                mTvLeftName.setText(getString(R.string.setting_gateway));
                tvLocal.setText(net_gateway);
                break;
            //修改设置dns
            case NET_DNS_ID:
                mTvLeftName.setText(getString(R.string.setting_dns));
                tvLocal.setText(net_dns);
                break;
            //修改管理员ip
            case NET_ADMIN_ID:
                mTvLeftName.setText(getString(R.string.setting_admin));
                tvLocal.setText(net_admin);
                break;
            //修改中心ip
            case NET_CENTER_ID:
                mTvLeftName.setText(getString(R.string.setting_center_server));
                tvLocal.setText(net_center);
                break;
            //修改流媒体ip
            case NET_MEDIA_ID:
                mTvLeftName.setText(getString(R.string.setting_media_server));
                tvLocal.setText(net_media);
                break;
            //修改人脸ip
            case NET_FACE_ID:
                mTvLeftName.setText(getString(R.string.setting_face_server));
                tvLocal.setText(net_face);
                break;
            //修改电梯控制器ip
            case NET_ELEVATOR_ID:
                mTvLeftName.setText(getString(R.string.setting_elevator));
                tvLocal.setText(net_elevator);
                break;
        }
    }

    private void saveDatas() {
        networkHelp.setIp(Common.ipToint(net_ip));
        networkHelp.setSubNet(Common.ipToint(net_mask));
        networkHelp.setDefaultGateway(Common.ipToint(net_gateway));
        networkHelp.setManagerIP(Common.ipToint(net_admin));
        networkHelp.setCenterIP(Common.ipToint(net_center));
        networkHelp.setMediaServer(Common.ipToint(net_media));
        networkHelp.setFaceIp(Common.ipToint(net_face));
        networkHelp.setElevatorIp(Common.ipToint(net_elevator));
        networkHelp.setDNS1(Common.ipToint(net_dns));

        //设置网络
        Intent intent = new Intent(CommSysDef.BROADCAST_NAME_IP);
        App.getInstance().sendBroadcast(intent);

        if (isFirstEnable) {
            //设置恢复出厂状态为false
            AppPreferences.setReset(false);
            //关闭当前的activity,启动主main
            AppUtils.getInstance().toActFinish(getActivity(), MainActivity.class);
        } else {
            mRooView.operateBackState(getString(R.string.set_success));
            setBackVisibility(View.GONE);
        }
    }

    @Override
    public void success() {
        setBackVisibility(View.VISIBLE);
        requestBack();
    }

    @Override
    public void fail() {

    }

    protected void backspace() {
        View root = getView();
        if (root != null) {
            View focus = root.findFocus();
            if (focus instanceof InputView) {
                InputView iptView = (InputView) focus;
                if (iptView.backspace())
                    return;
            }
        }
        tvLocal.requestFocus();
        if (index <= -1) {
            //退出
            requestBack();
        }
        index--;
        setNetParam();
    }
}
