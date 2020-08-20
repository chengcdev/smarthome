package com.mili.smarthome.tkj.setting.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.SystemProperties;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.CommTypeDef;
import com.android.Common;
import com.android.client.MainClient;
import com.android.client.ScanQrClient;
import com.android.provider.FullDeviceNo;
import com.android.provider.NetworkHelp;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.appfunc.BuildConfigHelper;
import com.mili.smarthome.tkj.appfunc.facefunc.FacePresenterProxy;
import com.mili.smarthome.tkj.auth.AuthManage;
import com.mili.smarthome.tkj.dao.ResidentSettingDao;
import com.mili.smarthome.tkj.dao.param.AlarmParamDao;
import com.mili.smarthome.tkj.dao.param.EntranceGuardDao;
import com.mili.smarthome.tkj.dao.param.ParamDao;
import com.mili.smarthome.tkj.dao.param.SnapParamDao;
import com.mili.smarthome.tkj.dao.param.VolumeParamDao;
import com.mili.smarthome.tkj.entities.param.SnapParam;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;
import com.mili.smarthome.tkj.utils.AppUtils;
import com.mili.smarthome.tkj.utils.EthernetUtils;
import com.mili.widget.zxing.encode.QRCodeEncoder;
import com.wf.wffrapp;

import mcv.facepass.FacePassHandler;

public class DevInfoFragment extends BaseFragment implements View.OnClickListener {

    private View vwPage1;
    private View vwPage2;
    private View vwPage3;
    private View vwPage4;
    private View vwPage5;

    private TextView tvDevType;// 设备属性
    private TextView tvTkNo;// 梯口号
    private TextView tvRoomStart;// 起始房号
    private TextView tvFloorCount;// 楼层数
    private TextView tvRoomCount;// 每层户数
    private TextView tvUnlockType;// 开锁类型
    private TextView tvUnlockTime;// 开锁时间
    private TextView tvDoorCheck;// 门状态检测
    private TextView tvDoorAlarm;// 报警输出
    private TextView tvDoorUpload;// 上报中心
    private TextView tvCellNo;// 单元号
    private TextView tvCardFree;// 剩余卡数
    private TextView tvFingerprintFree;// 剩余指纹数
    private TextView tvDevNo;// 设备号

    private TextView tvPwdMode;// 密码开门模式
    private TextView tvForcedOpen;// 强行开门报警
    private TextView tvPhotoVisitor;// 访客呼叫拍照
    private TextView tvPhotoErrPwd;// 错误密码开门拍照
    private TextView tvPhotoHoldPwd;// 挟持密码开门拍照
    private TextView tvPowerSaving;// 省电模式
    private TextView tvScreenSaver;// 屏保
    private TextView tvPwdDt;// 动态密保
    private TextView tvCardNoLen;// 卡号长度
    private TextView tvTkNoLen;// 梯口号长度
    private TextView tvRoomNoLen;// 房号长度
    private TextView tvCellNoLen;// 单元号长度
    private TextView tvNoRule;// 分段描述
    private TextView tvCallVolume;// 通话音量
    private TextView tvPromptTone;// 提示音
    private TextView tvKeyTone;// 按键音
    private TextView tvIp; //ip
    private TextView tvSub; //子网掩码
    private TextView tvGateway; //网关
    private TextView tvDns; //dns
    private TextView tvAdmin; //管理员机
    private TextView tvCenter; //中心机
    private TextView tvMedia; //流媒体
    private TextView tvFace; //人脸服务器
    private TextView tvElevator; //电梯控制器
    private TextView tvSystemVer; //系统版本
    private TextView tvFaceVer; //旷世人脸版本
    private TextView tvVersion; //版本
    private TextView tvChipver;
    private TextView tvBuildver;
    private TextView tvMac; //mac
    private TextView tvSn; //sn
    private ImageView mImaSn; //二维码
    //页数
    private int page = 1;
    private TextView mTvPage;
    private TextView tvPhotoCallCenter;
    private TextView tvPhotoFaceOpen;
    private TextView tvPhotoFingerOpen;
    private TextView tvPhotoCardOpen;
    private TextView tvPhotoPwdOpen;
    private TextView tvPhotoQrOpen;
    private TextView tvPhotoFaceStranger;
    private TextView tvFaceFree;
    @SuppressLint("HandlerLeak")

    private FullDeviceNo fullDeviceNo;
    private ResidentSettingDao residentSettingDao;
    private NetworkHelp networkHelp;

    private String mWffrVer;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_dev_info;
    }

    @Override
    protected void bindView() {
        vwPage1 = findView(R.id.devinfo_page_1);
        vwPage2 = findView(R.id.devinfo_page_2);
        vwPage3 = findView(R.id.devinfo_page_3);
        vwPage4 = findView(R.id.devinfo_page_4);
        vwPage5 = findView(R.id.devinfo_page_5);

        tvDevType = findView(R.id.tv_devtype);
        tvTkNo = findView(R.id.tv_tk_no);
        tvRoomStart = findView(R.id.tv_room_start);
        tvFloorCount = findView(R.id.tv_floor_count);
        tvRoomCount = findView(R.id.tv_room_count);
        tvUnlockType = findView(R.id.tv_unlock_type);
        tvUnlockTime = findView(R.id.tv_unlock_time);
        tvDoorCheck = findView(R.id.tv_door_check);
        tvDoorAlarm = findView(R.id.tv_door_alarm);
        tvDoorUpload = findView(R.id.tv_door_upload);
        tvCellNo = findView(R.id.tv_cell_no);
        tvCardFree = findView(R.id.tv_card_free);
        tvFaceFree = findView(R.id.tv_face_free);
        tvFingerprintFree = findView(R.id.tv_fingerprint_free);
        tvDevNo = findView(R.id.tv_dev_no);

        tvPwdMode = findView(R.id.tv_pwd_mode);
        tvForcedOpen = findView(R.id.tv_forced_open);
        tvPowerSaving = findView(R.id.tv_power_saving);
        tvScreenSaver = findView(R.id.tv_screen_saver);
        tvPwdDt = findView(R.id.tv_pwd_dt);
        tvCardNoLen = findView(R.id.tv_card_no_len);
        tvTkNoLen = findView(R.id.tv_tk_no_len);
        tvRoomNoLen = findView(R.id.tv_room_no_len);
        tvCellNoLen = findView(R.id.tv_cell_no_len);
        tvNoRule = findView(R.id.tv_no_rule);
        tvCallVolume = findView(R.id.tv_call_volume);
        tvPromptTone = findView(R.id.tv_prompt_tone);
        tvKeyTone = findView(R.id.tv_key_tone);

        tvPhotoVisitor = findView(R.id.tv_photo_visitor);
        tvPhotoErrPwd = findView(R.id.tv_photo_err_pwd);
        tvPhotoHoldPwd = findView(R.id.tv_photo_hold_pwd);
        tvPhotoCallCenter = findView(R.id.tv_photo_call_center);
        tvPhotoFaceOpen = findView(R.id.tv_photo_face_open);
        tvPhotoFingerOpen = findView(R.id.tv_photo_finger_open);
        tvPhotoCardOpen = findView(R.id.tv_photo_card_open);
        tvPhotoPwdOpen = findView(R.id.tv_photo_pwd_open);
        tvPhotoQrOpen = findView(R.id.tv_photo_qr_open);
        tvPhotoFaceStranger = findView(R.id.tv_photo_face_stranger);

        tvIp = findView(R.id.tv_local_ip);
        tvSub = findView(R.id.tv_sub);
        tvGateway = findView(R.id.tv_gateway);
        tvDns = findView(R.id.tv_dns);
        tvAdmin = findView(R.id.tv_admin);
        tvCenter = findView(R.id.tv_center);
        tvMedia = findView(R.id.tv_media);
        tvFace = findView(R.id.tv_face);
        tvElevator = findView(R.id.tv_elevator);
        tvSystemVer = findView(R.id.tv_system_version);
        tvFaceVer = findView(R.id.tv_face_version);
        tvVersion = findView(R.id.tv_version);
        tvChipver = findView(R.id.tv_chipver);
        tvBuildver = findView(R.id.tv_buildver);
        tvMac = findView(R.id.tv_mac);
        tvSn = findView(R.id.tv_sn);
        mImaSn = findView(R.id.img_sn);

        mTvPage = findView(R.id.tv_page);
        TextView mTvPrePage = findView(R.id.tv_pre_page);
        TextView mTvNextPage = findView(R.id.tv_next_page);
        mTvNextPage.setOnClickListener(this);
        mTvPrePage.setOnClickListener(this);
    }

    @Override
    protected void bindData() {
        //启用红外补关灯
        AppUtils.getInstance().setEnableLed(true);

        fullDeviceNo = new FullDeviceNo(getContext());
        residentSettingDao = new ResidentSettingDao();

        vwPage1.setVisibility(View.VISIBLE);
        vwPage2.setVisibility(View.GONE);
        vwPage3.setVisibility(View.GONE);
        vwPage4.setVisibility(View.GONE);
        vwPage5.setVisibility(View.GONE);

//        RefreshHandle.sendEmptyMessage(0);
        page1Datas();

        /* 先获取一次，解决序列号变更后不会实时更新（获取两次）问题 */
        MainClient.getInstance().Main_getCloudSn();

        /* EI人脸库版本信息 */
        String version = wffrapp.getVersion();
        int index = version.indexOf(',');
        mWffrVer = version.substring(0, index);
    }

    @Override
    public void onResume() {
        super.onResume();
        setBackVisibility(View.VISIBLE);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //上一页
            case R.id.tv_pre_page:
                page--;
                if (page < 1) {
                    page = 5;
                }
                break;
            //下一页
            case R.id.tv_next_page:
                page++;
                if (page > 5) {
                    page = 1;
                }
                break;
        }
        mTvPage.setText(page + "/5");
        if (page == 1) {
            page1Datas();
            vwPage1.setVisibility(View.VISIBLE);
            vwPage2.setVisibility(View.GONE);
            vwPage3.setVisibility(View.GONE);
            vwPage4.setVisibility(View.GONE);
            vwPage5.setVisibility(View.GONE);
        } else if (page == 2) {
            page2Datas();
            vwPage1.setVisibility(View.GONE);
            vwPage2.setVisibility(View.VISIBLE);
            vwPage3.setVisibility(View.GONE);
            vwPage4.setVisibility(View.GONE);
            vwPage5.setVisibility(View.GONE);
        } else if (page == 3) {
            page3Datas();
            vwPage1.setVisibility(View.GONE);
            vwPage2.setVisibility(View.GONE);
            vwPage3.setVisibility(View.VISIBLE);
            vwPage4.setVisibility(View.GONE);
            vwPage5.setVisibility(View.GONE);
        } else if (page == 4) {
            page4Datas();
            vwPage1.setVisibility(View.GONE);
            vwPage2.setVisibility(View.GONE);
            vwPage3.setVisibility(View.GONE);
            vwPage4.setVisibility(View.VISIBLE);
            vwPage5.setVisibility(View.GONE);
        } else if (page == 5) {
            page5Datas();
            vwPage1.setVisibility(View.GONE);
            vwPage2.setVisibility(View.GONE);
            vwPage3.setVisibility(View.GONE);
            vwPage4.setVisibility(View.GONE);
            vwPage5.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("SetTextI18n")
    public boolean page1Datas() {
        //设备类型
        int type = fullDeviceNo.getDeviceType();
        String devType;
        String stairNo;
        if (type == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
            devType = getString(R.string.intercall_dev_stair);
            //梯口号
            stairNo = getString(R.string.setting_tkh) + Integer.valueOf(fullDeviceNo.getStairNo());
            tvRoomStart.setVisibility(View.VISIBLE);
            tvFloorCount.setVisibility(View.VISIBLE);
            tvRoomCount.setVisibility(View.VISIBLE);
        } else {
            devType = getString(R.string.intercall_dev_area);
            //区口号
            stairNo = getString(R.string.setting_qkh) + Integer.valueOf(fullDeviceNo.getStairNo());
            tvRoomStart.setVisibility(View.GONE);
            tvFloorCount.setVisibility(View.GONE);
            tvRoomCount.setVisibility(View.GONE);
        }
        tvDevType.setText(getString(R.string.device_info_nature) + devType);
        tvTkNo.setText(stairNo);
        //住户设置
        tvRoomStart.setText(getString(R.string.device_info_room_start) + residentSettingDao.getRoomStart());
        tvFloorCount.setText(getString(R.string.device_info_floor_num) + residentSettingDao.getFloorCount());
        tvRoomCount.setText(getString(R.string.device_info_floor_num_user) + residentSettingDao.getFloorHouseNum());

        //锁类型
        if (EntranceGuardDao.getOpenLockType() == 0) {
            tvUnlockType.setText(getString(R.string.device_info_lock_type) + getString(R.string.setting_close_often));
        } else {
            tvUnlockType.setText(getString(R.string.device_info_lock_type) + getString(R.string.setting_open_often));
        }
        //锁时间
        tvUnlockTime.setText(getString(R.string.device_info_lock_time) + EntranceGuardDao.getOpenLockTime() + "s");
        //门检测状态
        if (EntranceGuardDao.getDoorStateCheck() == 0) {
            tvDoorCheck.setText(getString(R.string.device_info_lock_door_check) + getString(R.string.setting_no));
        } else {
            tvDoorCheck.setText(getString(R.string.device_info_lock_door_check) + getString(R.string.setting_yes));
        }
        //报警输出
        if (EntranceGuardDao.getAlarmOut() == 0) {
            tvDoorAlarm.setText(getString(R.string.device_info_alarm_out) + getString(R.string.setting_no));
        } else {
            tvDoorAlarm.setText(getString(R.string.device_info_alarm_out) + getString(R.string.setting_yes));
        }
        //上报中心
        if (EntranceGuardDao.getUpdateCenter() == 0) {
            tvDoorUpload.setText(getString(R.string.device_info_update_center) + getString(R.string.setting_no));
        } else {
            tvDoorUpload.setText(getString(R.string.device_info_update_center) + getString(R.string.setting_yes));
        }
        //是否启动单元号
        if (fullDeviceNo.getUseCellNo() == 0) {
            tvCellNo.setText(getString(R.string.device_info_unit_num) + getString(R.string.setting_no));
        } else {
            tvCellNo.setText(getString(R.string.device_info_unit_num) + getString(R.string.setting_yes));
        }


        //剩余人脸数
        long surplus = FacePresenterProxy.getSurplus();
        tvFaceFree.setText(getString(R.string.device_info_face_num) + surplus);

        //是否启用人脸
        if (AppConfig.getInstance().getFaceModule() == 1 && AppConfig.getInstance().getFaceRecognition() == 1) {
            tvFaceFree.setVisibility(View.VISIBLE);
        } else {
            tvFaceFree.setVisibility(View.GONE);
        }

        //剩余卡数
        int getCardFreeCount = SinglechipClientProxy.getInstance().getCardFreeCount();
        if (getCardFreeCount == -1) {
            tvCardFree.setVisibility(View.GONE);
        } else {
            tvCardFree.setVisibility(View.VISIBLE);
        }
        tvCardFree.setText(getString(R.string.device_info_card_num) + getCardFreeCount);

        int fingerGetCount = SinglechipClientProxy.getInstance().getFingerSurplus();
        //剩余指纹数
        tvFingerprintFree.setText(getString(R.string.device_info_fingerprint_num) + fingerGetCount);

        //是否启用指纹
        if (SinglechipClientProxy.getInstance().isFingerWork() && fingerGetCount != -1 && EntranceGuardDao.getFingerprint() == 1 ) {
            tvFingerprintFree.setVisibility(View.VISIBLE);
        } else {
            tvFingerprintFree.setVisibility(View.GONE);
        }


        //设备号
        tvDevNo.setText(getString(R.string.device_info_device_no) + fullDeviceNo.getCurrentDeviceNo());
        //梯口机显示设备号，区口机不显示
        if (fullDeviceNo.getDeviceType() == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
            tvDevNo.setVisibility(View.VISIBLE);
        } else {
            tvDevNo.setVisibility(View.GONE);
        }

        return true;
    }

    @SuppressLint("SetTextI18n")
    public void page2Datas() {

        //密码进门模式
        if (AppConfig.getInstance().getOpenPwdMode() == 0) {
            tvPwdMode.setText(getString(R.string.device_info_1) + getString(R.string.setting_pwd_door1));
        } else {
            tvPwdMode.setText(getString(R.string.device_info_1) + getString(R.string.setting_pwd_door2));
        }
        //开门报警
        if (AlarmParamDao.getForceOpen() == 0) {
            tvForcedOpen.setText(getString(R.string.device_info_2) + getString(R.string.setting_close));
        } else {
            tvForcedOpen.setText(getString(R.string.device_info_2) + getString(R.string.setting_enable));
        }
        //省电模式
        if (AppConfig.getInstance().getPowerSaving() == 0) {
            tvPowerSaving.setText(getString(R.string.device_info_6) + getString(R.string.setting_close));
        } else {
            tvPowerSaving.setText(getString(R.string.device_info_6) + getString(R.string.setting_enable));
        }
        //屏保
        if (AppConfig.getInstance().getScreenSaver()== 0) {
            tvScreenSaver.setText(getString(R.string.device_info_7) + getString(R.string.setting_close));
        } else {
            tvScreenSaver.setText(getString(R.string.device_info_7) + getString(R.string.setting_enable));
        }
        //动态密保
        if (AppConfig.getInstance().getPwdDynamic() == 0) {
            tvPwdDt.setText(getString(R.string.device_info_17) + getString(R.string.setting_close));
        } else {
            tvPwdDt.setText(getString(R.string.device_info_17) + getString(R.string.setting_enable));
        }
        tvCardNoLen.setText(getString(R.string.device_info_9) + ParamDao.getCardNoLen());
        //梯口号长度
        FullDeviceNo fullDeviceNo = new FullDeviceNo(getContext());
        tvTkNoLen.setText(getString(R.string.device_info_10) + fullDeviceNo.getStairNoLen());
        //房号长度
        tvRoomNoLen.setText(getString(R.string.device_info_11) + fullDeviceNo.getRoomNoLen());
        //单元号长度
        tvCellNoLen.setText(getString(R.string.device_info_12) + fullDeviceNo.getCellNoLen());
        //分段描述
        tvNoRule.setText(getString(R.string.device_info_13) + fullDeviceNo.getSubsection());

        //通话音量
        tvCallVolume.setText(getString(R.string.device_info_14) + VolumeParamDao.getCallVolume());
        if (AppConfig.getInstance().getTipVolume() == 0) {
            //提示音
            tvPromptTone.setText(getString(R.string.device_info_15) + getString(R.string.setting_close));
        } else {
            //提示音
            tvPromptTone.setText(getString(R.string.device_info_15) + getString(R.string.setting_enable));
        }
        if (AppConfig.getInstance().getKeyVolume() == 0) {
            //按键音
            tvKeyTone.setText(getString(R.string.device_info_16) + getString(R.string.setting_close));
        } else {
            //按键音
            tvKeyTone.setText(getString(R.string.device_info_16) + getString(R.string.setting_enable));
        }

    }

    @SuppressLint("SetTextI18n")
    public void page3Datas() {
        SnapParam snapParam = SnapParamDao.getSnapParam();
        //访客拍照
        if (snapParam.getVisitorSnap() == 0) {
            tvPhotoVisitor.setText(getString(R.string.device_info_3) + getString(R.string.setting_close));
        } else {
            tvPhotoVisitor.setText(getString(R.string.device_info_3) + getString(R.string.setting_enable));
        }
        //错误密码拍照
        if (snapParam.getErrorPwdSnap() == 0) {
            tvPhotoErrPwd.setText(getString(R.string.device_info_4) + getString(R.string.setting_close));
        } else {
            tvPhotoErrPwd.setText(getString(R.string.device_info_4) + getString(R.string.setting_enable));
        }
        //挟持密码开门拍照
        if (snapParam.getHijackPwdSnap() == 0) {
            tvPhotoHoldPwd.setText(getString(R.string.device_info_5) + getString(R.string.setting_close));
        } else {
            tvPhotoHoldPwd.setText(getString(R.string.device_info_5) + getString(R.string.setting_enable));
        }
        //呼叫中心拍照
        if (snapParam.getCallCenterSnap() == 0) {
            tvPhotoCallCenter.setText(getString(R.string.device_info_18) + getString(R.string.setting_close));
        } else {
            tvPhotoCallCenter.setText(getString(R.string.device_info_18) + getString(R.string.setting_enable));
        }
        //是否启用人脸
        if (AppConfig.getInstance().getFaceModule() == 1 && AppConfig.getInstance().getFaceRecognition() == 1) {
            tvPhotoFaceOpen.setVisibility(View.VISIBLE);
        } else {
            tvPhotoFaceOpen.setVisibility(View.GONE);
        }

        //人脸开门拍照
        if (snapParam.getFaceOpenSnap() == 0) {
            tvPhotoFaceOpen.setText(getString(R.string.device_info_19) + getString(R.string.setting_close));
        } else {
            tvPhotoFaceOpen.setText(getString(R.string.device_info_19) + getString(R.string.setting_enable));
        }

        //是否启用指纹
        int fingerGetCount = SinglechipClientProxy.getInstance().getFingerSurplus();
        if (SinglechipClientProxy.getInstance().isFingerWork() && fingerGetCount != -1 && AppConfig.getInstance().getFingerprint() == 1) {
            tvPhotoFingerOpen.setVisibility(View.VISIBLE);
        } else {
            tvPhotoFingerOpen.setVisibility(View.GONE);
        }

        //指纹开门拍照
        if (snapParam.getFingerOpenSnap() == 0) {
            tvPhotoFingerOpen.setText(getString(R.string.device_info_20) + getString(R.string.setting_close));
        } else {
            tvPhotoFingerOpen.setText(getString(R.string.device_info_20) + getString(R.string.setting_enable));
        }
        //刷卡开门拍照
        if (snapParam.getCardOpenSnap() == 0) {
            tvPhotoCardOpen.setText(getString(R.string.device_info_21) + getString(R.string.setting_close));
        } else {
            tvPhotoCardOpen.setText(getString(R.string.device_info_21) + getString(R.string.setting_enable));
        }
        //密码开门拍照
        if (snapParam.getPwdOpenSnap() == 0) {
            tvPhotoPwdOpen.setText(getString(R.string.device_info_22) + getString(R.string.setting_close));
        } else {
            tvPhotoPwdOpen.setText(getString(R.string.device_info_22) + getString(R.string.setting_enable));
        }

        //是否启用扫码开门
        if (AppConfig.getInstance().getQrScanEnabled() == 0) {
            tvPhotoQrOpen.setVisibility(View.GONE);
        } else {
            tvPhotoQrOpen.setVisibility(View.VISIBLE);
        }

        //扫码开门拍照
        if (snapParam.getQrcodeOpenSnap() == 0) {
            tvPhotoQrOpen.setText(getString(R.string.device_info_23) + getString(R.string.setting_close));
        } else {
            tvPhotoQrOpen.setText(getString(R.string.device_info_23) + getString(R.string.setting_enable));
        }

        //是否启用人脸
        if (AppConfig.getInstance().getFaceModule() == 1 && AppConfig.getInstance().getFaceRecognition() == 1) {
            tvPhotoFaceStranger.setVisibility(View.VISIBLE);
        } else {
            tvPhotoFaceStranger.setVisibility(View.GONE);
        }

        //陌生人脸拍照
        if (snapParam.getFaceStrangerSnap() == 0) {
            tvPhotoFaceStranger.setText(getString(R.string.device_info_24) + getString(R.string.setting_close));
        } else {
            tvPhotoFaceStranger.setText(getString(R.string.device_info_24) + getString(R.string.setting_enable));
        }
    }


    @SuppressLint("SetTextI18n")
    public void page4Datas() {

        if (networkHelp == null) {
            networkHelp = new NetworkHelp();
        }

        //本机IP
        String ip = Common.intToIP(networkHelp.getIp());
        //子网掩码
        String subNet = Common.intToIP(networkHelp.getSubNet());
        //网关
        String gateWay = Common.intToIP(networkHelp.getDefaultGateway());
        //管理员机
        String manageIp = Common.intToIP(networkHelp.getManagerIP());
        //中心服务器
        String centerIp = Common.intToIP(networkHelp.getCenterIP());
        //流媒体服务器
        String mediaServer = Common.intToIP(networkHelp.getMediaServer());
        //人脸服务器
        String faceServer = Common.intToIP(networkHelp.getFaceIp());
        //电梯控制器
        String elevatorIp = Common.intToIP(networkHelp.getElevatorIp());
        //dns
        String dns = Common.intToIP(networkHelp.getDNS1());
        tvIp.setText(getString(R.string.setting_ip) + ip);
        tvSub.setText(getString(R.string.setting_subnet_mask) + subNet);
        tvGateway.setText(getString(R.string.setting_gateway) + gateWay);
        tvDns.setText(getString(R.string.setting_dns) + dns);
        tvAdmin.setText(getString(R.string.setting_manager_ip) + manageIp);
        tvCenter.setText(getString(R.string.setting_center_ip) + centerIp);
        tvMedia.setText(getString(R.string.setting_media_ip) + mediaServer);


        if (AppConfig.getInstance().getFaceModule() == 1 && AppConfig.getInstance().getFaceRecognition() == 1) {
            tvFace.setVisibility(View.VISIBLE);
            tvFace.setText(getString(R.string.setting_face_server) + faceServer);
        } else {
            tvFace.setVisibility(View.GONE);
        }

        if (fullDeviceNo.getDeviceType() == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
            tvElevator.setVisibility(View.VISIBLE);
            tvElevator.setText(getString(R.string.setting_elevator_ip) + elevatorIp);
        } else {
            tvElevator.setVisibility(View.GONE);
        }
    }

    @SuppressLint("SetTextI18n")
    public void page5Datas() {
        String osVer = "Android " + Build.VERSION.RELEASE;
        tvSystemVer.setText(getString(R.string.device_info_os_ver) + osVer);

        /* 程序版本 */
        tvVersion.setText(getString(R.string.setting_version) + BuildConfigHelper.getSoftWareVer());
        tvChipver.setText("ChipVer：" + BuildConfigHelper.getHardWareVer());

        /* 编译版本 */
        String buildVer = getString(R.string.device_info_build_ver) + SystemProperties.get("ro.product.model")
                + " " + SystemProperties.get("ro.product.version");
        tvBuildver.setText(buildVer);

        /* mac码后面增加mlink平台注册状态 */
        String mlinkState = getString(R.string.mlink_state_fail);
        if (MainClient.getInstance().Main_GetMlinkState() == 1) {
            mlinkState = getString(R.string.mlink_state_ok);
        }
        String macStr = "MAC：" + EthernetUtils.getMacAddress() + " (" + mlinkState + ")";
        tvMac.setText(macStr);

        /* 人脸库版本 */
        if (AppConfig.getInstance().getFaceManufacturer() == 1) {
            tvFaceVer.setText(getString(R.string.device_info_face_ver) + FacePassHandler.getVersion());
        } else {
            tvFaceVer.setText(mWffrVer);
        }

        /* 云端序列号，带云端注册状态 */
        int cloudTalk = EntranceGuardDao.getCloudTalk();
        int authState = AuthManage.getAuthState();
//        LogUtils.d(" cloudtalk is " + cloudTalk + ", authState is " + authState);
        if (cloudTalk == 1 && authState == 1) {
            tvSn.setVisibility(View.VISIBLE);
            tvSn.setText(getCloudInfo());
        } else {
            tvSn.setVisibility(View.GONE);
        }

        //二维码生成字符串
        String code = ScanQrClient.getInstance().GetDeviceInfoQR();
        if (code != null) {
            Bitmap bitmap = QRCodeEncoder.encodeAsBitmap(code, getResources().getDimensionPixelSize(R.dimen.dp_130), getResources().getDimensionPixelSize(R.dimen.dp_0_5));
            if (bitmap != null) {
                mImaSn.setImageBitmap(bitmap);
            }
        }
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
