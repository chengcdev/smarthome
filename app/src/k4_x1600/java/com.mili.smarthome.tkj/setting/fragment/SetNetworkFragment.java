package com.mili.smarthome.tkj.setting.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.CommSysDef;
import com.android.CommTypeDef;
import com.android.Common;
import com.android.provider.FullDeviceNo;
import com.android.provider.NetworkHelp;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.App;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.base.K4BaseFragment;
import com.mili.smarthome.tkj.constant.Constant;
import com.mili.smarthome.tkj.widget.IpInputView;

/**
 * 网络设置
 */
public class SetNetworkFragment extends K4BaseFragment implements View.OnClickListener, View.OnTouchListener {

    private static final int NetType_Ip = 0;
    private static final int NetType_Mask = 1;
    private static final int NetType_Gateway = 2;
    private static final int NetType_Dns = 3;
    private static final int NetType_Manager = 4;
    private static final int NetType_Center = 5;
    private static final int NetType_Rtsp = 6;
    private static final int NetType_Elevator = 7;
    private static final int NetType_Face_Stair = 8;

    private static final String Tag = "NetworkFragment";
    private LinearLayout mLlcontent;
    private RelativeLayout mLlButton;
    private TextView mTvHint, mTvDesc;
    private IpInputView mIvNet;

    private int mNetType = NetType_Ip;
    private int mCursorMax = 15;
    private int mCursorIndex = 0;

    private NetworkHelp mNetworkHelp;
    private String mNetIp = "";
    private String mNetMask = "";
    private String mNetGateway = "";
    private String mNetDns = "";
    private String mNetManager = "";
    private String mNetCenter = "";
    private String mNetRtsp = "";
    private String mNetElevator = "";
    private String mNetFace = "";

    private int mLastType = NetType_Rtsp;
    private int mDeviceType = CommTypeDef.DeviceType.DEVICE_TYPE_STAIR;


    @Override
    public boolean onKeyCancel() {
        super.onKeyCancel();

        if (mCursorIndex > 0) {
            mIvNet.input(0);
            mCursorIndex--;
            if ((mCursorIndex+1)%4 == 0) {
                mCursorIndex--;
            }
            mIvNet.setCursorIndex(mCursorIndex);
        } else {
            mNetType--;
            if (mNetType < 0) {
                exitFragment();
            } else {
                showContent(mNetType);
            }
        }
        return true;
    }

    @Override
    public boolean onKeyConfirm() {
        super.onKeyConfirm();

        setCurValue(mNetType);
        if (mNetType == mLastType) {
            saveNetparam();

            mTvHint.setText(R.string.set_ok);
            mLlcontent.setVisibility(View.INVISIBLE);
            mLlButton.setVisibility(View.INVISIBLE);
            mTvHint.setVisibility(View.VISIBLE);
            mMainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    exitFragment();
                }
            }, Constant.SET_HINT_TIMEOUT);
        } else {
            mNetType++;
            showContent(mNetType);
            mCursorIndex = 0;
            mIvNet.setCursorIndex(mCursorIndex);
        }
        return true;
    }

    @Override
    public boolean onKey(int code) {
        super.onKey(code);
        boolean ret = checkIp(code);
        if (ret) {
            mIvNet.input(code);
            if (mCursorIndex < mCursorMax-1) {
                mCursorIndex++;
                if ((mCursorIndex+1)%4 == 0) {
                    mCursorIndex++;
                }
            }
        }
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_network;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void bindView() {
        super.bindView();

        TextView head = findView(R.id.tv_head);
        if (head != null) {
            head.setText(R.string.setting_0402);
        }

        mLlcontent = findView(R.id.ll_content);
        mLlButton = findView(R.id.ll_button);
        mIvNet = findView(R.id.iv_net);
        mTvDesc = findView(R.id.tv_desc);
        mTvHint = findView(R.id.tv_hint);

        ImageButton ibDown = findView(R.id.ib_down);
        ImageButton ibUp = findView(R.id.ib_up);
        if (ibDown != null) {
            ibDown.setOnClickListener(this);
        }
        if (ibUp != null) {
            ibUp.setOnClickListener(this);
        }

        mIvNet.setOnTouchListener(this);
    }

    @Override
    protected void bindData() {
        super.bindData();

        mNetType = NetType_Ip;
        mCursorIndex = 0;
        mIvNet.requestFocus();
        mIvNet.setCursorIndex(mCursorIndex);

        initData();
        showContent(mNetType);

        // 只有梯口机才有电梯控制器设置
        FullDeviceNo fullDeviceNo = new FullDeviceNo(getContext());
        mDeviceType = fullDeviceNo.getDeviceType();
        mLastType = NetType_Rtsp;
        if (mDeviceType == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
            mLastType++;
        }
        if (AppConfig.getInstance().isFaceEnabled()) {
            mLastType++;
        }
    }

    @Override
    protected void unbindView() {
        super.unbindView();
        mMainHandler.removeCallbacksAndMessages(0);
    }

    private void initData() {
        mNetworkHelp = new NetworkHelp();
        mNetIp = Common.intToIP(mNetworkHelp.getIp());
        mNetMask = Common.intToIP(mNetworkHelp.getSubNet());
        mNetGateway = Common.intToIP(mNetworkHelp.getDefaultGateway());
        mNetDns = Common.intToIP(mNetworkHelp.getDNS1());
        mNetManager = Common.intToIP(mNetworkHelp.getManagerIP());
        mNetCenter = Common.intToIP(mNetworkHelp.getCenterIP());
        mNetRtsp = Common.intToIP(mNetworkHelp.getMediaServer());
        mNetElevator = Common.intToIP(mNetworkHelp.getElevatorIp());
        mNetFace = Common.intToIP(mNetworkHelp.getFaceIp());
    }

    private void showContent(int type) {
        int descResId = 0;
        String content = "";
        switch (type) {
            case NetType_Ip:
                descResId = R.string.setting_ip;
                content = mNetIp;
                break;
            case NetType_Mask:
                descResId = R.string.setting_mask;
                content = mNetMask;
                break;
            case NetType_Gateway:
                descResId = R.string.setting_gateway;
                content = mNetGateway;
                break;
            case NetType_Dns:
                descResId = R.string.setting_dns;
                content = mNetDns;
                break;
            case NetType_Manager:
                descResId = R.string.setting_admin;
                content = mNetManager;
                break;
            case NetType_Center:
                descResId = R.string.setting_center_server;
                content = mNetCenter;
                break;
            case NetType_Rtsp:
                descResId = R.string.setting_media_server;
                content = mNetRtsp;
                break;
            case NetType_Elevator:      // elevator or face
                if (mDeviceType == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
                    descResId = R.string.setting_elevator;
                    content = mNetElevator;
                } else {
                    descResId = R.string.setting_face_server;
                    content = mNetFace;
                }
                break;
            case NetType_Face_Stair:
                descResId = R.string.setting_face_server;
                content = mNetFace;
                break;
        }

        if (mTvDesc != null) {
            mTvDesc.setText(descResId);
        }
        if (mIvNet != null) {
            mIvNet.setText(content);
        }
    }

    private void setCurValue(int type) {
        String content = mIvNet.getText().toString();
        switch (type) {
            case NetType_Ip:
                mNetIp = content;
                break;
            case NetType_Mask:
                mNetMask = content;
                break;
            case NetType_Gateway:
                mNetGateway = content;
                break;
            case NetType_Dns:
                mNetDns = content;
                break;
            case NetType_Manager:
                mNetManager = content;
                break;
            case NetType_Center:
                mNetCenter = content;
                break;
            case NetType_Rtsp:
                mNetRtsp = content;
                break;
            case NetType_Elevator:  // elevator or face
                if (mDeviceType == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
                    mNetElevator = content;
                } else {
                    mNetFace = content;
                }
                break;
            case NetType_Face_Stair:
                mNetFace = content;
                break;
        }
    }

    private void saveNetparam() {
        mNetworkHelp.setIp(Common.ipToint(mNetIp));
        mNetworkHelp.setSubNet(Common.ipToint(mNetMask));
        mNetworkHelp.setDefaultGateway(Common.ipToint(mNetGateway));
        mNetworkHelp.setDNS1(Common.ipToint(mNetDns));
        mNetworkHelp.setManagerIP(Common.ipToint(mNetManager));
        mNetworkHelp.setCenterIP(Common.ipToint(mNetCenter));
        mNetworkHelp.setMediaServer(Common.ipToint(mNetRtsp));
        mNetworkHelp.setElevatorIp(Common.ipToint(mNetElevator));
        mNetworkHelp.setFaceIp(Common.ipToint(mNetFace));

        //设置网络
        Intent intent = new Intent(CommSysDef.BROADCAST_NAME_IP);
        App.getInstance().sendBroadcast(intent);
    }

    /*判断是否为有效的Ip地址*/
    private boolean checkIp(int code) {
        char value = (char)(code + '0');
        String content = ipPadding(mIvNet.getText().toString()).toString();
        StringBuilder buffer = new StringBuilder();
        buffer.append(content);
        buffer.setCharAt(mCursorIndex, value);
        String[] temp = buffer.toString().split("\\.");
        int index = mCursorIndex/4;
        int subIp = Integer.valueOf(temp[index]);
        Log.d(Tag, "subIp=" + subIp + ", buffer=" + buffer);
        return  (subIp>=0 && subIp <= 255);
    }

    /*将IP地址补0成完整格式000.000.000.000*/
    private CharSequence ipPadding(String ip) {
        String[] temp = ip.split("\\.");
        StringBuilder newIp = new StringBuilder();
        for (int i = 0; i < temp.length; i++) {
            if (i != 0) {
                newIp.append(".");
            }
            String seq = temp[i];
            if (seq.length() == 1) {
                newIp.append("00");
            } else if (seq.length() == 2) {
                newIp.append("0");
            }
            newIp.append(seq);
        }
        return newIp;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_up:
                if (mCursorIndex > 0) {
                    mCursorIndex--;
                    if ((mCursorIndex+1)%4 == 0) {
                        mCursorIndex--;
                    }
                    mIvNet.setCursorIndex(mCursorIndex);
                }
                break;
            case R.id.ib_down:
                if (mCursorIndex < mCursorMax-1) {
                    mCursorIndex++;
                    if ((mCursorIndex+1)%4 == 0) {
                        mCursorIndex++;
                    }
                    mIvNet.setCursorIndex(mCursorIndex);
                }
                break;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() != MotionEvent.ACTION_UP) {
            return false;
        }
        if (view.getId() == R.id.iv_net) {
            mIvNet.requestFocus();
            mIvNet.setCursorIndex(0);
            mCursorIndex = 0;
        }
        return false;
    }
}
