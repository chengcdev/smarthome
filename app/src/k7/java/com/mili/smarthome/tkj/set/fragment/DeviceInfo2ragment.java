package com.mili.smarthome.tkj.set.fragment;


import android.widget.LinearLayout;

import com.android.CommTypeDef;
import com.android.Common;
import com.android.provider.FullDeviceNo;
import com.android.provider.NetworkHelp;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.main.fragment.BaseKeyBoardFragment;
import com.mili.smarthome.tkj.set.widget.DeviceInfoTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 设备信息2
 */

public class DeviceInfo2ragment extends BaseKeyBoardFragment {


    private LinearLayout mLinRoot;
    private List<String> dataList = new ArrayList<>();
    private FullDeviceNo fullDeviceNo;

    public int getLayout() {
        return R.layout.fragment_decice_info1;
    }

    @Override
    public void setKeyBoard(int viewId, String keyId) {

    }

    @Override
    public void initView() {
        mLinRoot = (LinearLayout) getContentView().findViewById(R.id.root);

    }

    @Override
    public void initAdapter() {

        fullDeviceNo = new FullDeviceNo(getContext());

        dataList.clear();
        mLinRoot.removeAllViews();

        NetworkHelp networkHelp = new NetworkHelp();
        //本机IP
        String ip = Common.intToIP(networkHelp.getIp());
        //子网掩码
        String subNet = Common.intToIP(networkHelp.getSubNet());
        //网关
        String gateWay = Common.intToIP(networkHelp.getDefaultGateway());
        //Dns
        String dns = Common.intToIP(networkHelp.getDNS1());
        //管理员机
        String manageIp = Common.intToIP(networkHelp.getManagerIP());
        //中心服务器
        String centerIp = Common.intToIP(networkHelp.getCenterIP());
        //流媒体服务器
        String mediaServer = Common.intToIP(networkHelp.getMediaServer());
        //电梯控制器
        String mElevator = Common.intToIP(networkHelp.getElevatorIp());
        //人脸识别终端
        String faceIp = Common.intToIP(networkHelp.getFaceIp());

        //获取数据
        dataList.add(getString(R.string.setting_ip));
        dataList.add(ip);
        dataList.add(getString(R.string.setting_mask));
        dataList.add(subNet);
        dataList.add(getString(R.string.setting_gateway));
        dataList.add(gateWay);
        dataList.add(getString(R.string.setting_dns));
        dataList.add(dns);
        dataList.add(getString(R.string.setting_admin));
        dataList.add(manageIp);
        dataList.add(getString(R.string.setting_center_server));
        dataList.add(centerIp);
        dataList.add(getString(R.string.setting_media_server));
        dataList.add(mediaServer);
        if (fullDeviceNo.getDeviceType() == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
            dataList.add(getString(R.string.setting_elevator));
            dataList.add(mElevator);
            dataList.add(getString(R.string.setting_face_client));
            dataList.add(faceIp);
        }

        for (int i = 0; i < dataList.size(); i++) {
            DeviceInfoTextView textView = new DeviceInfoTextView(getContext());
            textView.setText(dataList.get(i));
            mLinRoot.addView(textView);
        }
    }


    @Override
    public void initListener() {

    }

}
