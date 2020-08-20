package com.mili.smarthome.tkj.set.fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import com.android.CommSysDef;
import com.android.CommTypeDef;
import com.android.Common;
import com.android.provider.FullDeviceNo;
import com.android.provider.NetworkHelp;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.AppPreferences;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.main.entity.NetWorkSettingEntity;
import com.mili.smarthome.tkj.main.fragment.BaseKeyBoardFragment;
import com.mili.smarthome.tkj.main.fragment.MainFragment;
import com.mili.smarthome.tkj.main.interf.ISetCallBackListener;
import com.mili.smarthome.tkj.main.widget.SetSuccessView;
import com.mili.smarthome.tkj.set.Constant;
import com.mili.smarthome.tkj.set.widget.inputview.CustomInputAdapter;
import com.mili.smarthome.tkj.set.widget.inputview.CustomInputView;
import com.mili.smarthome.tkj.utils.AppManage;

import java.util.ArrayList;
import java.util.List;

/**
 * 网络设置
 */

public class SetNetFragment extends BaseKeyBoardFragment implements ISetCallBackListener {


    private TextView tvTitle;
    private TextView tvName;
    private CustomInputView inputView;
    private String net_ip = "";
    private String net_mask = "";
    private String net_gateway = "";
    private String net_admin = "";
    private String net_center = "";
    private String net_media = "";
    private String net_elevator = "";
    private String net_dns = "";
    private String net_face = "";

    private int index = 0;
    private NetworkHelp networkHelp;
    private SetSuccessView successView;
    //是否第一次安装跳转
    private boolean isFirst;
    private final String NET_IP_ID = "net_ip_id";
    private final String NET_MASK_ID = "net_mask_id";
    private final String NET_GATEWAY_ID = "net_gateway_id";
    private final String NET_ADMIN_ID = "net_admin_id";
    private final String NET_CENTER_ID = "net_center_id";
    private final String NET_MEDIA_ID = "net_media_id";
    private final String NET_ELEVATOR_ID = "net_elevator_id";
    private final String NET_FACE_ID = "net_face_id";
    private final String NET_DNS_ID = "net_dns_id";

    private List<NetWorkSettingEntity> mNetList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            isFirst = (boolean) bundle.getSerializable(Constant.SETTING_FIRST);
        }
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_setting_net;
    }


    @Override
    public void initView() {
        tvTitle = (TextView) getContentView().findViewById(R.id.tv_title);
        tvName = (TextView) getContentView().findViewById(R.id.tv_name);
        inputView = (CustomInputView) getContentView().findViewById(R.id.it_content);
        successView = (SetSuccessView) getContentView().findViewById(R.id.root);

        initDatas();
    }

    private void initDatas() {
        networkHelp = new NetworkHelp();
        net_ip = Common.intToIP(networkHelp.getIp());
        net_mask = Common.intToIP(networkHelp.getSubNet());
        net_gateway = Common.intToIP(networkHelp.getDefaultGateway());
        net_dns = Common.intToIP(networkHelp.getDNS1());
        net_admin = Common.intToIP(networkHelp.getManagerIP());
        net_center = Common.intToIP(networkHelp.getCenterIP());
        net_media = Common.intToIP(networkHelp.getMediaServer());
        net_elevator = Common.intToIP(networkHelp.getElevatorIp());
        net_face = Common.intToIP(networkHelp.getFaceIp());

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
        if (AppConfig.getInstance().isFaceEnabled()){
            mNetList.add(new NetWorkSettingEntity(NET_FACE_ID, R.string.setting_face_client, net_face));
        }
        initIpView(net_ip, R.string.setting_ip);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void initAdapter() {
        tvTitle.setText(getString(R.string.setting_net));
        tvName.setText(getString(R.string.setting_ip));
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
                if (index < 0) {
                    index = 1;
                }
                index--;
                saveNetParam();
                break;
            case Constant.KEY_CONFIRM:
                if (index == mNetList.size()) {
                    index = -1;
                }
                index++;
                saveNetParam();
                break;
            case Constant.KEY_UP:

                break;
            case Constant.KEY_DELETE:
                inputView.deleteNum("0");
                setNetparam();
                break;
            case Constant.KEY_NEXT:

                break;
            default:
                if (inputView.getCount() == inputView.getNum().length()-1) {
                inputView.setEndFlash(true);
            }
            inputView.addNum(keyId);
            setNetparam();
            break;
        }
    }


    private void setNetparam() {
        NetWorkSettingEntity netWorkSettingEntity = mNetList.get(index);
        if (netWorkSettingEntity == null) {
            return;
        }
        switch (netWorkSettingEntity.getId()) {
            //修改ip
            case NET_IP_ID:
                net_ip = inputView.getNum();
                break;
            //修改子网掩码
            case NET_MASK_ID:
                net_mask = inputView.getNum();
                break;
            //修改网关
            case NET_GATEWAY_ID:
                net_gateway = inputView.getNum();
                break;
            //修改dns
            case NET_DNS_ID:
                net_dns = inputView.getNum();
                break;
            //修改管理员ip
            case NET_ADMIN_ID:
                net_admin = inputView.getNum();
                break;
            //修改中心ip
            case NET_CENTER_ID:
                net_center = inputView.getNum();
                break;
            //修改流媒体ip
            case NET_MEDIA_ID:
                net_media = inputView.getNum();
                break;
            //修改电梯控制器ip
            case NET_ELEVATOR_ID:
                net_elevator = inputView.getNum();
                break;
            //修改人脸ip
            case NET_FACE_ID:
                net_face = inputView.getNum();
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    private void saveNetParam() {
        if (index >= mNetList.size()) {
            //保存
            saveDatas();
            return;
        }
        if (index <= -1) {
            //保存
            exitFragment(this);
            return;
        }
        NetWorkSettingEntity netWorkSettingEntity = mNetList.get(index);
        if (netWorkSettingEntity == null) {
            return;
        }

        switch (netWorkSettingEntity.getId()) {
            //修改ip
            case NET_IP_ID:
                initIpView(net_ip, R.string.setting_ip);
                break;
            //修改子网掩码
            case NET_MASK_ID:
                initIpView(net_mask, R.string.setting_mask);
                break;
            //修改网关
            case NET_GATEWAY_ID:
                initIpView(net_gateway, R.string.setting_gateway);
                break;
            //修改设置dns
            case NET_DNS_ID:
                initIpView(net_dns, R.string.setting_dns);
                break;
            //修改管理员ip
            case NET_ADMIN_ID:
                initIpView(net_admin, R.string.setting_admin);
                break;
            //修改中心ip
            case NET_CENTER_ID:
                initIpView(net_center, R.string.setting_center_server);
                break;
            //修改流媒体ip
            case NET_MEDIA_ID:
                initIpView(net_media, R.string.setting_media_server);
                break;
            //修改电梯控制器ip
            case NET_ELEVATOR_ID:
                initIpView(net_elevator, R.string.setting_elevator);
                break;
            //修改人脸ip
            case NET_FACE_ID:
                initIpView(net_face, R.string.setting_face_client);
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
        networkHelp.setElevatorIp(Common.ipToint(net_elevator));
        networkHelp.setFaceIp(Common.ipToint(net_face));
        networkHelp.setDNS1(Common.ipToint(net_dns));

        if (!isFirst) {
            successView.showSuccessView(getString(R.string.setting_success), 1000, this);
        } else {
            FullDeviceNo fullDeviceNo = new FullDeviceNo(getContext());
            if (fullDeviceNo.getDeviceType() == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
                //梯口机
                //跳转到呼叫方式
                SetCallTypeFragment setCallTypeFragment = new SetCallTypeFragment();
                AppManage.getInstance().replaceFragment(getActivity(), setCallTypeFragment);
            } else {
                //是否第一次安装软件
                AppPreferences.setReset(false);
                //跳转到主界面
                MainFragment mainFragment = new MainFragment();
                AppManage.getInstance().replaceFragment(getActivity(), mainFragment);
            }
        }
    }


    @Override
    public void success() {
        //退出界面
        exitFragment(this);
    }

    @Override
    public void fail() {

    }

    @Override
    public void onDestroyView() {
        AppManage.getInstance().sendReceiver(CommSysDef.BROADCAST_NAME_IP);
        super.onDestroyView();
    }

    private String editIp(String str) {
        String[] split = str.split("\\.");
        String s1;
        s1 = getStr(split[0]);

        String s2;
        s2 = getStr(split[1]);

        String s3;
        s3 = getStr(split[2]);

        String s4;
        s4 = getStr(split[3]);

        return s1 + "." + s2 + "." + s3 + "." + s4;
    }

    private String getStr(String s) {
        String s1;
        int num = Integer.valueOf(s);
        if (Integer.valueOf(s) < 10) {
            s1 = "00" + num;
        } else if (Integer.valueOf(s) > 9 && (Integer.valueOf(s) < 100)) {
            s1 = "0" + num;
        } else {
            s1 = s;
        }
        return s1;
    }

    private void initIpView(String ip, int strId) {
        inputView.setFirstFlash(true).setEndFlash(false).init(editIp(ip), 15, CustomInputAdapter.INPUT_TYPE_IP);
        inputView.notifychange();
        tvName.setText(strId);
    }
}
