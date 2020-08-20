package com.mili.smarthome.tkj.setting.fragment;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemProperties;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.CommTypeDef;
import com.android.client.MainClient;
import com.android.client.ScanQrClient;
import com.android.interf.IKeyEventListener;
import com.android.provider.FullDeviceNo;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.appfunc.BuildConfigHelper;
import com.mili.smarthome.tkj.appfunc.facefunc.FacePresenterProxy;
import com.mili.smarthome.tkj.auth.AuthManage;
import com.mili.smarthome.tkj.dao.ResidentSettingDao;
import com.mili.smarthome.tkj.dao.param.AlarmParamDao;
import com.mili.smarthome.tkj.dao.param.EntranceGuardDao;
import com.mili.smarthome.tkj.dao.param.NetworkParamDao;
import com.mili.smarthome.tkj.dao.param.ParamDao;
import com.mili.smarthome.tkj.dao.param.SnapParamDao;
import com.mili.smarthome.tkj.dao.param.VolumeParamDao;
import com.mili.smarthome.tkj.entities.ResidentSettingModel;
import com.mili.smarthome.tkj.entities.param.NetworkParam;
import com.mili.smarthome.tkj.entities.param.SnapParam;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;
import com.mili.smarthome.tkj.utils.EthernetUtils;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.SystemSetUtils;
import com.mili.smarthome.tkj.utils.ViewUtils;
import com.mili.smarthome.tkj.widget.LabelTextView;
import com.mili.widget.zxing.encode.QRCodeEncoder;
import com.wf.wffrapp;

import java.util.Locale;

import mcv.facepass.FacePassHandler;

public class DevInfoFragment extends BaseSetFragment implements ViewStub.OnInflateListener {

    private ViewStub[] vsPages;
    private TextView tvPage;
    private int mPageCount, mPageIndex;

    private LabelTextView tvDevType;
    private LabelTextView tvTkNo;
    private LabelTextView tvRoomStart;
    private LabelTextView tvFloorCount;
    private LabelTextView tvRoomCount;
    private LabelTextView tvUnlockType;
    private LabelTextView tvUnlockTime;
    private LabelTextView tvDoorCheck;
    private LabelTextView tvDoorAlarm;
    private LabelTextView tvDoorUpload;
    private LabelTextView tvCellNo;
    private LabelTextView tvCardFree;
    private LabelTextView tvFaceFree;
    private LabelTextView tvFingerprintFree;
    private LabelTextView tvDevNo;

    private LabelTextView tvPwdMode;
    private LabelTextView tvForcedOpen;
    private LabelTextView tvPowerSaving;
    private LabelTextView tvScreenSaver;
    private LabelTextView tvSensitivity;
    private LabelTextView tvCardNoLen;
    private LabelTextView tvTkNoLen;
    private LabelTextView tvRoomNoLen;
    private LabelTextView tvCellNoLen;
    private LabelTextView tvNoRule;
    private LabelTextView tvCallVolume;
    private LabelTextView tvPromptTone;
    private LabelTextView tvKeyTone;

    private LabelTextView tvPhotoVisitor;
    private LabelTextView tvPhotoErrPwd;
    private LabelTextView tvPhotoHoldPwd;
    private LabelTextView tvPhotoCallCenter;
    private LabelTextView tvPhotoFaceOpen;
    private LabelTextView tvPhotoFingerOpen;
    private LabelTextView tvPhotoCardOpen;
    private LabelTextView tvPhotoPwdOpen;
    private LabelTextView tvPhotoQrOpen;
    private LabelTextView tvPhotoFaceStranger;

    private LabelTextView tvLocalIp;
    private LabelTextView tvSubnetMask;
    private LabelTextView tvGatewayIp;
    private LabelTextView tvDnsIp;
    private LabelTextView tvManagerIp;
    private LabelTextView tvCenterIp;
    private LabelTextView tvMediaIp;
    private LabelTextView tvFaceIp;
    private LabelTextView tvElevatorIp;

    private LabelTextView tvOSver;
    private LabelTextView tvSoftVer;
    private LabelTextView tvHardVer;
    private LabelTextView tvBuildVer;
    private LabelTextView tvFaceVer;
    private LabelTextView tvMac;
    private LabelTextView tvSn;
    private ImageView ivSn;

    private String mWffrVer;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_dev_info;
    }

    @Override
    protected void bindView() {
        tvPage = findView(R.id.tv_page);
        vsPages = new ViewStub[]{
                findView(R.id.stub_devinfo1),
                findView(R.id.stub_devinfo2),
                findView(R.id.stub_devinfo3),
                findView(R.id.stub_devinfo4),
                findView(R.id.stub_devinfo5)
        };
        mPageCount = vsPages.length;

        for (ViewStub stub : vsPages) {
            stub.setOnInflateListener(this);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SinglechipClientProxy.getInstance().setCcdLedTimeLimit(0);
        showCore(mPageIndex = 0);

        /* 先获取一次，解决序列号变更后不会实时更新（获取两次）问题 */
        MainClient.getInstance().Main_getCloudSn();

        String version = wffrapp.getVersion();
        int index = version.indexOf(',');
        mWffrVer = version.substring(0, index);
    }

    @Override
    public boolean onKeyEvent(int keyCode, int keyState) {
        if (keyState == IKeyEventListener.KEYSTATE_DOWN)
            return false;
        switch (keyCode) {
            case IKeyEventListener.KEYCODE_UP:
                mPageIndex--;
                if (mPageIndex < 0)
                    mPageIndex = mPageCount - 1;
                showCore(mPageIndex);
                break;
            case IKeyEventListener.KEYCODE_DOWN:
                mPageIndex++;
                if (mPageIndex >= mPageCount)
                    mPageIndex = 0;
                showCore(mPageIndex);
                break;
            case IKeyEventListener.KEYCODE_BACK:
                requestBack();
                break;
        }
        return true;
    }

    private void showCore(int pageIndex) {
        tvPage.setText(String.format(Locale.getDefault(), "%d/%d", pageIndex + 1, mPageCount));
        for (int i = 0; i < mPageCount; i++) {
            ViewStub viewStub = vsPages[i];
            if (i == pageIndex) {
                viewStub.setVisibility(View.VISIBLE);
                switch (viewStub.getId()) {
                    case R.id.stub_devinfo1:
                        bindData1();
                        break;
                    case R.id.stub_devinfo2:
                        bindData2();
                        break;
                    case R.id.stub_devinfo3:
                        bindData3();
                        break;
                    case R.id.stub_devinfo4:
                        bindData4();
                        break;
                    case R.id.stub_devinfo5:
                        bindData5();
                        break;
                }
            } else {
                viewStub.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onInflate(ViewStub viewStub, View view) {
        switch (viewStub.getId()) {
            case R.id.stub_devinfo1:
                bindView1(view);
                break;
            case R.id.stub_devinfo2:
                bindView2(view);
                break;
            case R.id.stub_devinfo3:
                bindView3(view);
                break;
            case R.id.stub_devinfo4:
                bindView4(view);
                break;
            case R.id.stub_devinfo5:
                bindView5(view);
                break;
        }
    }

    private void bindView1(View view) {
        tvDevType = ViewUtils.findView(view, R.id.tv_devtype);
        tvTkNo = ViewUtils.findView(view, R.id.tv_tk_no);
        tvRoomStart = ViewUtils.findView(view, R.id.tv_room_start);
        tvFloorCount = ViewUtils.findView(view, R.id.tv_floor_count);
        tvRoomCount = ViewUtils.findView(view, R.id.tv_room_count);
        tvUnlockType = ViewUtils.findView(view, R.id.tv_unlock_type);
        tvUnlockTime = ViewUtils.findView(view, R.id.tv_unlock_time);
        tvDoorCheck = ViewUtils.findView(view, R.id.tv_door_check);
        tvDoorAlarm = ViewUtils.findView(view, R.id.tv_door_alarm);
        tvDoorUpload = ViewUtils.findView(view, R.id.tv_door_upload);
        tvCellNo = ViewUtils.findView(view, R.id.tv_cell_no);
        tvCardFree = ViewUtils.findView(view, R.id.tv_card_free);
        tvFaceFree = ViewUtils.findView(view, R.id.tv_face_free);
        tvFingerprintFree = ViewUtils.findView(view, R.id.tv_fingerprint_free);
        tvDevNo = ViewUtils.findView(view, R.id.tv_dev_no);
    }

    private void bindData1() {
        //设备类型
        FullDeviceNo fullDeviceNo = new FullDeviceNo(getContext());
        int type = fullDeviceNo.getDeviceType();
        if (type == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
            //梯口
            tvDevType.setText(R.string.intercall_dev_stair);
            tvTkNo.setLabel(R.string.device_info_tkh);
            tvTkNo.setText(fullDeviceNo.getStairNo());
        } else {
            //区口
            tvDevType.setText(R.string.intercall_dev_area);
            tvTkNo.setLabel(R.string.device_info_qkh);
            tvTkNo.setText(fullDeviceNo.getStairNo());
        }

        //住户设置
        ResidentSettingDao residentSettingDao = new ResidentSettingDao();
        ResidentSettingModel residentSettingModel = residentSettingDao.queryModel();
        tvRoomStart.setText(residentSettingModel.getRoomNoStart());
        tvFloorCount.setText(residentSettingModel.getFloorCount());
        tvRoomCount.setText(residentSettingModel.getFloorHouseNum());

        //开锁类型
        if (EntranceGuardDao.getOpenLockType() == 0) {
            tvUnlockType.setText(R.string.setting_close_often);
        } else {
            tvUnlockType.setText(R.string.setting_open_often);
        }
        //开锁时间
        tvUnlockTime.setText(EntranceGuardDao.getOpenLockTime() + "S");
        //门检测状态
        if (EntranceGuardDao.getDoorStateCheck() == 0) {
            tvDoorCheck.setText(R.string.pub_no);
        } else {
            tvDoorCheck.setText(R.string.pub_yes);
        }
        //报警输出
        if (EntranceGuardDao.getAlarmOut() == 0) {
            tvDoorAlarm.setText(R.string.pub_no);
        } else {
            tvDoorAlarm.setText(R.string.pub_yes);
        }
        //上报中心
        if (EntranceGuardDao.getUpdateCenter() == 0) {
            tvDoorUpload.setText(R.string.pub_no);
        } else {
            tvDoorUpload.setText(R.string.pub_yes);
        }
        //是否启用单元号
        if (fullDeviceNo.getUseCellNo() == 0) {
            tvCellNo.setText(R.string.pub_no);
        } else {
            tvCellNo.setText(R.string.pub_yes);
        }
        //剩余卡数
        tvCardFree.setText(String.valueOf(SinglechipClientProxy.getInstance().getCardFreeCount()));
        //剩余用户数
        if (AppConfig.getInstance().isFaceEnabled()) {
            tvFaceFree.setVisibility(View.VISIBLE);
            tvFaceFree.setText(String.valueOf(FacePresenterProxy.getSurplus()));
        } else {
            tvFaceFree.setVisibility(View.GONE);
        }
        //剩余指纹数
        if (AppConfig.getInstance().isFingerEnabled()) {
            tvFingerprintFree.setVisibility(View.VISIBLE);
            tvFingerprintFree.setText(String.valueOf(SinglechipClientProxy.getInstance().getFingerSurplus()));
        } else {
            tvFingerprintFree.setVisibility(View.GONE);
        }
        //设备号
        tvDevNo.setText(fullDeviceNo.getCurrentDeviceNo());
    }

    private void bindView2(View view) {
        tvPwdMode = ViewUtils.findView(view, R.id.tv_pwd_mode);
        tvForcedOpen = ViewUtils.findView(view, R.id.tv_forced_open);
        tvPowerSaving = ViewUtils.findView(view, R.id.tv_power_saving);
        tvScreenSaver = ViewUtils.findView(view, R.id.tv_screen_saver);
        tvSensitivity = ViewUtils.findView(view, R.id.tv_sensitivity);
        tvCardNoLen = ViewUtils.findView(view, R.id.tv_card_no_len);
        tvTkNoLen = ViewUtils.findView(view, R.id.tv_tk_no_len);
        tvRoomNoLen = ViewUtils.findView(view, R.id.tv_room_no_len);
        tvCellNoLen = ViewUtils.findView(view, R.id.tv_cell_no_len);
        tvNoRule = ViewUtils.findView(view, R.id.tv_no_rule);
        tvCallVolume = ViewUtils.findView(view, R.id.tv_call_volume);
        tvPromptTone = ViewUtils.findView(view, R.id.tv_prompt_tone);
        tvKeyTone = ViewUtils.findView(view, R.id.tv_key_tone);
    }

    private void bindData2() {
        //密码进门模式
        if (ParamDao.getPwdDoorMode() == 0) {
            tvPwdMode.setText(R.string.setting_pwd_door1);
        } else {
            tvPwdMode.setText(R.string.setting_pwd_door2);
        }
        //强行开门报警
        if (AlarmParamDao.getForceOpen() == 0) {
            tvForcedOpen.setText(R.string.setting_close);
        } else {
            tvForcedOpen.setText(R.string.setting_enable);
        }
        //省电模式
        if (ParamDao.getPowerSave() == 0) {
            tvPowerSaving.setText(R.string.setting_close);
        } else {
            tvPowerSaving.setText(R.string.setting_enable);
        }
        //屏保
        if (ParamDao.getScreenPro() == 0) {
            tvScreenSaver.setText(R.string.setting_close);
        } else {
            tvScreenSaver.setText(R.string.setting_enable);
        }
        //按键灵敏度
        switch (ParamDao.getTouchSensitivity()) {
            case 0:
                tvSensitivity.setText(R.string.key_sensitivity_0);
                break;
            case 1:
                tvSensitivity.setText(R.string.key_sensitivity_1);
                break;
            case 2:
                tvSensitivity.setText(R.string.key_sensitivity_2);
                break;
        }

        //卡号长度
        tvCardNoLen.setText(String.valueOf(ParamDao.getCardNoLen()));

        FullDeviceNo fullDeviceNo = new FullDeviceNo(getContext());
        //梯口号长度
        tvTkNoLen.setText(String.valueOf(fullDeviceNo.getStairNoLen()));
        //房号长度
        tvRoomNoLen.setText(String.valueOf(fullDeviceNo.getRoomNoLen()));
        //单元号长度
        tvCellNoLen.setText(String.valueOf(fullDeviceNo.getCellNoLen()));
        //分段描述
        tvNoRule.setText(String.valueOf(fullDeviceNo.getSubsection()));

        //通话音量
        tvCallVolume.setText(String.valueOf(VolumeParamDao.getCallVolume()));
        //提示音
        if (AppConfig.getInstance().getTipVolume() == 0) {
            tvPromptTone.setText(R.string.setting_close);
        } else {
            tvPromptTone.setText(R.string.setting_enable);
        }
        //按键音
        if (AppConfig.getInstance().getKeyVolume() == 0) {
            tvKeyTone.setText(R.string.setting_close);
        } else {
            tvKeyTone.setText(R.string.setting_enable);
        }
    }

    private void bindView3(View view) {
        tvPhotoVisitor = ViewUtils.findView(view, R.id.tv_photo_visitor);
        tvPhotoErrPwd = ViewUtils.findView(view, R.id.tv_photo_err_pwd);
        tvPhotoHoldPwd = ViewUtils.findView(view, R.id.tv_photo_hold_pwd);
        tvPhotoCallCenter = ViewUtils.findView(view, R.id.tv_photo_call_center);
        tvPhotoFaceOpen = ViewUtils.findView(view, R.id.tv_photo_face_open);
        tvPhotoFingerOpen = ViewUtils.findView(view, R.id.tv_photo_finger_open);
        tvPhotoCardOpen = ViewUtils.findView(view, R.id.tv_photo_card_open);
        tvPhotoPwdOpen = ViewUtils.findView(view, R.id.tv_photo_pwd_open);
        tvPhotoQrOpen = ViewUtils.findView(view, R.id.tv_photo_qr_open);
        tvPhotoFaceStranger = ViewUtils.findView(view, R.id.tv_photo_face_stranger);
    }

    private void bindData3() {
        SnapParam snapParam = SnapParamDao.getSnapParam();
        //访客呼叫拍照
        if (snapParam.getVisitorSnap() == 0) {
            tvPhotoVisitor.setText(R.string.setting_close);
        } else {
            tvPhotoVisitor.setText(R.string.setting_enable);
        }
        //错误密码拍照
        if (snapParam.getErrorPwdSnap() == 0) {
            tvPhotoErrPwd.setText(R.string.setting_close);
        } else {
            tvPhotoErrPwd.setText(R.string.setting_enable);
        }
        //挟持密码开门拍照
        if (snapParam.getHijackPwdSnap() == 0) {
            tvPhotoHoldPwd.setText(R.string.setting_close);
        } else {
            tvPhotoHoldPwd.setText(R.string.setting_enable);
        }
        //呼叫中心拍照
        if (snapParam.getCallCenterSnap() == 0) {
            tvPhotoCallCenter.setText(R.string.setting_close);
        } else {
            tvPhotoCallCenter.setText(R.string.setting_enable);
        }
        //人脸开门拍照
        if (AppConfig.getInstance().isFaceEnabled()) {
            tvPhotoFaceOpen.setVisibility(View.VISIBLE);
            if (snapParam.getFaceOpenSnap() == 0) {
                tvPhotoFaceOpen.setText(R.string.setting_close);
            } else {
                tvPhotoFaceOpen.setText(R.string.setting_enable);
            }
        } else {
            tvPhotoFaceOpen.setVisibility(View.GONE);
        }
        //指纹开门拍照
        if (AppConfig.getInstance().isFingerEnabled()) {
            tvPhotoFingerOpen.setVisibility(View.VISIBLE);
            if (snapParam.getFingerOpenSnap() == 0) {
                tvPhotoFingerOpen.setText(R.string.setting_close);
            } else {
                tvPhotoFingerOpen.setText(R.string.setting_enable);
            }
        } else {
            tvPhotoFingerOpen.setVisibility(View.GONE);
        }
        //刷卡开门拍照
        if (snapParam.getCardOpenSnap() == 0) {
            tvPhotoCardOpen.setText(R.string.setting_close);
        } else {
            tvPhotoCardOpen.setText(R.string.setting_enable);
        }
        //密码开门拍照
        if (snapParam.getPwdOpenSnap() == 0) {
            tvPhotoPwdOpen.setText(R.string.setting_close);
        } else {
            tvPhotoPwdOpen.setText(R.string.setting_enable);
        }
        //扫码开门拍照
        if (AppConfig.getInstance().isQrCodeEnabled()) {
            tvPhotoQrOpen.setVisibility(View.VISIBLE);
            if (snapParam.getQrcodeOpenSnap() == 0) {
                tvPhotoQrOpen.setText(R.string.setting_close);
            } else {
                tvPhotoQrOpen.setText(R.string.setting_enable);
            }
        } else {
            tvPhotoQrOpen.setVisibility(View.GONE);
        }
        //陌生人脸拍照
        if (AppConfig.getInstance().isFaceEnabled()) {
            tvPhotoFaceStranger.setVisibility(View.VISIBLE);
            if (snapParam.getFaceStrangerSnap() == 0) {
                tvPhotoFaceStranger.setText(R.string.setting_close);
            } else {
                tvPhotoFaceStranger.setText(R.string.setting_enable);
            }
        } else {
            tvPhotoFaceStranger.setVisibility(View.GONE);
        }
    }

    private void bindView4(View view) {
        tvLocalIp = ViewUtils.findView(view, R.id.tv_local_ip);
        tvSubnetMask = ViewUtils.findView(view, R.id.tv_subnet_mask);
        tvGatewayIp = ViewUtils.findView(view, R.id.tv_gateway_ip);
        tvDnsIp = ViewUtils.findView(view, R.id.tv_dns_ip);
        tvManagerIp = ViewUtils.findView(view, R.id.tv_manager_ip);
        tvCenterIp = ViewUtils.findView(view, R.id.tv_center_ip);
        tvMediaIp = ViewUtils.findView(view, R.id.tv_media_ip);
        tvFaceIp = ViewUtils.findView(view, R.id.tv_face_ip);
        tvElevatorIp = ViewUtils.findView(view, R.id.tv_elevator_ip);
    }

    private void bindData4() {
        int deviceType = AppConfig.getInstance().getDevType();
        if (deviceType == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
            tvElevatorIp.setVisibility(View.VISIBLE);
        } else {
            tvElevatorIp.setVisibility(View.GONE);
        }
        NetworkParam networkParam = NetworkParamDao.getNetWorkParam();
        tvLocalIp.setText(networkParam.getLocalIp());
        tvSubnetMask.setText(networkParam.getSubNet());
        tvGatewayIp.setText(networkParam.getGateway());
        tvDnsIp.setText(networkParam.getDNS1());
        tvManagerIp.setText(networkParam.getAdminIp());
        tvCenterIp.setText(networkParam.getCenterIp());
        tvMediaIp.setText(networkParam.getMediaIp());

        if (AppConfig.getInstance().isFaceEnabled()) {
            tvFaceIp.setVisibility(View.VISIBLE);
            tvFaceIp.setText(networkParam.getFaceIp());
        } else {
            tvFaceIp.setVisibility(View.GONE);
        }
        tvElevatorIp.setText(networkParam.getElevatorIp());
    }

    private void bindView5(View view) {
        tvOSver = ViewUtils.findView(view, R.id.tv_os_ver);
        tvSoftVer = ViewUtils.findView(view, R.id.tv_soft_ver);
        tvHardVer = ViewUtils.findView(view, R.id.tv_hard_ver);
        tvBuildVer = ViewUtils.findView(view, R.id.tv_build_ver);
        tvFaceVer = ViewUtils.findView(view, R.id.tv_face_ver);
        tvMac = ViewUtils.findView(view, R.id.tv_mac);
        tvSn = ViewUtils.findView(view, R.id.tv_sn);
        ivSn = ViewUtils.findView(view, R.id.iv_sn);
        View btnUpgrade = ViewUtils.findView(view, R.id.btn_upgrade);
        btnUpgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SystemSetUtils.systemUpgrade(2);
            }
        });
    }

    private void bindData5() {
        String osVer = "Android " + Build.VERSION.RELEASE;
        tvOSver.setText(osVer);

        /* 程序版本 */
        tvSoftVer.setText(BuildConfigHelper.getSoftWareVer());
        tvHardVer.setText(BuildConfigHelper.getHardWareVer());

        /* 编译版本 */
        String buildVer = SystemProperties.get("ro.product.model") + " " + SystemProperties.get("ro.product.version");
        tvBuildVer.setText(buildVer);

        /* 人脸库版本 */
        if (AppConfig.getInstance().getFaceManufacturer() == 1) {
            tvFaceVer.setLabel(R.string.device_info_face_ver);
            tvFaceVer.setText(FacePassHandler.getVersion());
        } else {
            tvFaceVer.setLabel("");
            tvFaceVer.setText(mWffrVer);
        }

        /* mac码后面增加mlink平台注册状态 */
        String mlinkState = getString(R.string.mlink_state_fail);
        if (MainClient.getInstance().Main_GetMlinkState() == 1) {
            mlinkState = getString(R.string.mlink_state_ok);
        }
        String mac = EthernetUtils.getMacAddress() + " (" + mlinkState + ")";
        tvMac.setText(mac);

        int cloudTalk = EntranceGuardDao.getCloudTalk();
        int authState = AuthManage.getAuthState();
        if (cloudTalk == 1 && authState == 1) {
            tvSn.setVisibility(View.VISIBLE);
            tvSn.setText(getCloudInfo());
        } else {
            tvSn.setVisibility(View.GONE);
        }

        //二维码生成字符串
        String qrcode = ScanQrClient.getInstance().GetDeviceInfoQR();
        if (qrcode != null) {
            LogUtils.d("qrcode encode: " + qrcode);
            int dimension = mContext.getResources().getDimensionPixelOffset(R.dimen.dp_120);
            Bitmap bitmap = QRCodeEncoder.encodeAsBitmap(qrcode, dimension, 0);
            if (bitmap != null) {
                ivSn.setImageBitmap(bitmap);
            }
        }
    }

    /**
     * 获取云端信息描述
     * @return  云端信息描述
     */
    private String getCloudInfo() {
        StringBuilder builder = new StringBuilder();

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
