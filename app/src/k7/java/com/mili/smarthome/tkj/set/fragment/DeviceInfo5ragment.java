package com.mili.smarthome.tkj.set.fragment;


import android.graphics.Bitmap;
import android.os.Build;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.client.MainClient;
import com.android.client.ScanQrClient;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.BuildConfigHelper;
import com.mili.smarthome.tkj.auth.AuthManage;
import com.mili.smarthome.tkj.dao.param.EntranceGuardDao;
import com.mili.smarthome.tkj.main.fragment.BaseKeyBoardFragment;
import com.mili.smarthome.tkj.set.widget.DeviceInfoTextView;
import com.mili.smarthome.tkj.utils.EthernetUtils;
import com.mili.widget.zxing.encode.QRCodeEncoder;

import java.util.ArrayList;
import java.util.List;

/**
 * 设备信息5
 */

public class DeviceInfo5ragment extends BaseKeyBoardFragment {


    private LinearLayout mLinRoot;
    private List<String> dataList = new ArrayList<>();

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

        dataList.clear();
        mLinRoot.removeAllViews();

        String osVer = "Android " + Build.VERSION.RELEASE;
        int cloudTalk = EntranceGuardDao.getCloudTalk();
        int authState = AuthManage.getAuthState();

        //获取数据
        dataList.add(getString(R.string.device_info_os_ver)+osVer);
        dataList.add(getString(R.string.device_info_version)+ BuildConfigHelper.getSoftWareVer());
        dataList.add("ChipVer："+BuildConfigHelper.getHardWareVer());
        dataList.add("MAC："+ EthernetUtils.getMacAddress());
        if (cloudTalk == 1 && authState == 1) {
            dataList.add(getCloudInfo());
        }

        for (int i = 0; i < dataList.size(); i++) {
            DeviceInfoTextView textView = new DeviceInfoTextView(getContext());
            textView.setText(dataList.get(i));
            mLinRoot.addView(textView);
        }


        //二维码显示
        String code = ScanQrClient.getInstance().GetDeviceInfoQR();
        if (code != null) {
            Bitmap bitmap = QRCodeEncoder.encodeAsBitmap(code, 300, 1);
            if (bitmap != null) {
                //添加二维码
                ImageView imageView = new ImageView(getContext());
                imageView.setImageBitmap(bitmap);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(120,20,0,0);//4个参数按顺序分别是左上右下
                imageView.setLayoutParams(layoutParams);
                mLinRoot.addView(imageView);
            }
        }
    }


    @Override
    public void initListener() {

    }

    /**
     * 获取云端信息描述
     *
     * @return 云端信息描述
     */
    private String getCloudInfo() {
        StringBuilder builder = new StringBuilder();
        builder.append(getString(R.string.device_info_sn));

        if (MainClient.getInstance() == null) {
            return builder.toString() + getString(R.string.cloud_state_fail);
        }

        String sn = MainClient.getInstance().Main_getCloudSn();
        if (sn != null && !sn.contains("WRONG")) {
            builder.append(sn);
        } else {
            return builder.toString();
        }
        if (MainClient.getInstance().Main_getCloudState() == 1) {
            builder.append(" (");
            builder.append(getString(R.string.cloud_state_ok));
            builder.append(")");
        } else {
            builder.append(" (");
            builder.append(getString(R.string.cloud_state_fail));
            builder.append(")");
        }
        return builder.toString();
    }

}
