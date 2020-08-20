package com.mili.smarthome.tkj.setting.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.CommTypeDef;
import com.android.client.MainClient;
import com.android.client.ScanQrClient;
import com.android.provider.FullDeviceNo;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.appfunc.BuildConfigHelper;
import com.mili.smarthome.tkj.auth.AuthManage;
import com.mili.smarthome.tkj.base.K4BaseFragment;
import com.mili.smarthome.tkj.dao.ResidentSettingDao;
import com.mili.smarthome.tkj.dao.param.AlarmParamDao;
import com.mili.smarthome.tkj.dao.param.EntranceGuardDao;
import com.mili.smarthome.tkj.dao.param.NetworkParamDao;
import com.mili.smarthome.tkj.dao.param.ParamDao;
import com.mili.smarthome.tkj.dao.param.SnapParamDao;
import com.mili.smarthome.tkj.entities.param.NetworkParam;
import com.mili.smarthome.tkj.entities.param.SnapParam;
import com.mili.smarthome.tkj.face.horizon.realm.HorizonFaceDao;
import com.mili.smarthome.tkj.proxy.SinglechipClientProxy;
import com.mili.smarthome.tkj.utils.EthernetUtils;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.SystemSetUtils;
import com.mili.widget.zxing.encode.QRCodeEncoder;

import java.util.Locale;


public class DevInfoFragment extends K4BaseFragment implements View.OnClickListener {

    private TextView tvPage;
    private View vwPage1;
    private View vwPage2;
    private View vwPage3;
    private View vwPage4;
    private View vwPage5;
    private LinearLayout llPage1;
    private LinearLayout llPage3;

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
    private TextView tvFaceFree;//剩余人脸数
    private TextView tvFingerprintFree;// 剩余指纹数
    private TextView tvDevNo;// 设备号

    private TextView tvPwdMode;// 密码开门模式
    private TextView tvForcedOpen;// 强行开门报警
    private TextView tvPowerSaving;// 省电模式
    private TextView tvScreenSaver;// 屏保
    private TextView tvPwdDynamic;  //动态密码
    private TextView tvCardNoLen;// 卡号长度
    private TextView tvTkNoLen;// 梯口号长度
    private TextView tvRoomNoLen;// 房号长度
    private TextView tvCellNoLen;// 单元号长度
    private TextView tvNoRule;// 分段描述
    private TextView tvCallVolume;// 通话音量
    private TextView tvPromptTone;// 提示音
    private TextView tvKeyTone;// 按键音

    private TextView tvPhotoVisitor;// 访客呼叫拍照
    private TextView tvPhotoErrPwd; // 错误密码开门拍照
    private TextView tvPhotoHoldPwd;// 挟持密码开门拍照
    private TextView tvPhotoCenter; // 呼叫中心拍照
    private TextView tvPhotoFace;   // 人脸开门拍照
    private TextView tvPhotoFinger; // 指纹开门拍照
    private TextView tvPhotoCard;   // 刷卡开门拍照
    private TextView tvPhotoPwd;    // 密码开门拍照
    private TextView tvPhotoQrcode; // 扫码开门拍照

    private TextView tvLocalIp;// 本机IP
    private TextView tvSubnetMask;// 子网掩码
    private TextView tvGatewayIp;// 网关
    private TextView tvDns;// DNS服务器
    private TextView tvManagerIp;// 管理员机
    private TextView tvCenterIp;// 中心服务器
    private TextView tvMediaIp;// 流媒体服务器
    private TextView tvElevatorIp;// 电梯控制器
    private TextView tvFaceIp;  // 人脸识别服务器

    private TextView tvOSver;// 系统版本
    private TextView tvSoftVer;// 软件
    private TextView tvHardVer; // 硬件版本
    private TextView tvMac;
    private TextView tvSn;
    private ImageView ivSn;

    private static final int PAGE_MAX = 5;
    private int mPageIndex = 1;
    private int mDeviceType;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_set_devinfo;
    }

    @Override
    protected void bindView() {
        super.bindView();

        tvPage = findView(R.id.tv_page);
        vwPage1 = findView(R.id.devinfo_page_1);
        vwPage2 = findView(R.id.devinfo_page_2);
        vwPage3 = findView(R.id.devinfo_page_3);
        vwPage4 = findView(R.id.devinfo_page_4);
        vwPage5 = findView(R.id.devinfo_page_5);
        llPage1 = findView(R.id.ll_page1);
        llPage3 = findView(R.id.ll_page3);

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
        tvPwdDynamic = findView(R.id.tv_pwd_dynamic);
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
        tvPhotoCenter = findView(R.id.tv_photo_call_center);
        tvPhotoFace = findView(R.id.tv_photo_face_open);
        tvPhotoFinger = findView(R.id.tv_photo_finger_open);
        tvPhotoCard = findView(R.id.tv_photo_card_open);
        tvPhotoPwd = findView(R.id.tv_photo_pwd_open);
        tvPhotoQrcode = findView(R.id.tv_photo_qr_open);

        tvLocalIp = findView(R.id.tv_local_ip);
        tvSubnetMask = findView(R.id.tv_subnet_mask);
        tvGatewayIp = findView(R.id.tv_gateway_ip);
        tvDns = findView(R.id.tv_dns);
        tvManagerIp = findView(R.id.tv_manager_ip);
        tvCenterIp = findView(R.id.tv_center_ip);
        tvMediaIp = findView(R.id.tv_media_ip);
        tvElevatorIp = findView(R.id.tv_elevator_ip);
        tvFaceIp = findView(R.id.tv_face_ip);
        tvOSver = findView(R.id.tv_os_ver);
        tvSoftVer = findView(R.id.tv_soft_ver);
        tvHardVer = findView(R.id.tv_hard_ver);
        tvMac = findView(R.id.tv_mac);
        tvSn = findView(R.id.tv_sn);
        ivSn = findView(R.id.iv_sn);

        ImageView ivReturn = findView(R.id.iv_return);
        TextView tvLastpage = findView(R.id.tv_lastpage);
        TextView tvNextpage = findView(R.id.tv_nextpage);
        assert ivReturn != null;
        ivReturn.setOnClickListener(this);
        assert tvLastpage != null;
        tvLastpage.setOnClickListener(this);
        assert tvNextpage != null;
        tvNextpage.setOnClickListener(this);
    }

    @Override
    protected void bindData() {
        super.bindData();
        SinglechipClientProxy.getInstance().setCcdLedTimeLimit(0);
        initPage1();
        showCore(1);
        mPageIndex = 1;
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                initPage2();
                initPage3();
                initPage4();
                initPage5();
            }
        });
    }

    @Override
    protected void unbindView() {
        super.unbindView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_return:
                requestBack();
                break;

            case R.id.tv_lastpage:
                mPageIndex--;
                if (mPageIndex < 1)
                    mPageIndex = PAGE_MAX;
                showCore(mPageIndex);
                break;

            case R.id.tv_nextpage:
                mPageIndex++;
                if (mPageIndex > PAGE_MAX)
                    mPageIndex = 1;
                showCore(mPageIndex);
                break;
        }
    }

    private void showCore(int pageIndex) {
        tvPage.setText(String.format(Locale.getDefault(), "%d/%d", pageIndex, PAGE_MAX));
        switch (pageIndex) {
            case 1:
                showPage1();
                break;
            case 2:
                showPage2();
                break;
            case 3:
                showPage3();
                break;
            case 4:
                showPage4();
                break;
            case 5:
                showPage5();
                break;
        }
    }

    private void showPage1() {
        vwPage1.setVisibility(View.VISIBLE);
        vwPage2.setVisibility(View.GONE);
        vwPage3.setVisibility(View.GONE);
        vwPage4.setVisibility(View.GONE);
        vwPage5.setVisibility(View.GONE);
    }

    private void showPage2() {
        vwPage1.setVisibility(View.GONE);
        vwPage2.setVisibility(View.VISIBLE);
        vwPage3.setVisibility(View.GONE);
        vwPage4.setVisibility(View.GONE);
        vwPage5.setVisibility(View.GONE);
    }

    private void showPage3() {
        vwPage1.setVisibility(View.GONE);
        vwPage2.setVisibility(View.GONE);
        vwPage3.setVisibility(View.VISIBLE);
        vwPage4.setVisibility(View.GONE);
        vwPage5.setVisibility(View.GONE);
    }

    private void showPage4() {
        vwPage1.setVisibility(View.GONE);
        vwPage2.setVisibility(View.GONE);
        vwPage3.setVisibility(View.GONE);
        vwPage4.setVisibility(View.VISIBLE);
        vwPage5.setVisibility(View.GONE);
    }

    private void showPage5() {
        vwPage1.setVisibility(View.GONE);
        vwPage2.setVisibility(View.GONE);
        vwPage3.setVisibility(View.GONE);
        vwPage4.setVisibility(View.GONE);
        vwPage5.setVisibility(View.VISIBLE);
    }

    @SuppressLint("SetTextI18n")
    private void initPage1() {
        FullDeviceNo fullDeviceNo = new FullDeviceNo(getContext());
        //设备类型
        int type = fullDeviceNo.getDeviceType();
        mDeviceType = type;
        String devType, stairNo;
        if (type == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
            devType = getString(R.string.intercall_dev_stair);
            //梯口号
            stairNo = getString(R.string.device_info_tkh) + Integer.valueOf(fullDeviceNo.getStairNo());
        } else {
            devType = getString(R.string.intercall_dev_area);
            //区口号
            stairNo = getString(R.string.device_info_qkh) + Integer.valueOf(fullDeviceNo.getStairNo());
        }
        tvDevType.setText(getString(R.string.device_info_nature) + devType);
        tvTkNo.setText(stairNo);

        //住户设置
        if (type == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
            ResidentSettingDao residentSettingDao = new ResidentSettingDao();
            tvRoomStart.setText(getString(R.string.device_info_room_start) + residentSettingDao.getRoomStart());
            tvFloorCount.setText(getString(R.string.device_info_floor_num) + residentSettingDao.getFloorCount());
            tvRoomCount.setText(getString(R.string.device_info_floor_num_user) + residentSettingDao.getFloorHouseNum());
        } else {
            llPage1.removeView(tvRoomStart);
            llPage1.removeView(tvFloorCount);
            llPage1.removeView(tvRoomCount);
        }

        //开锁类型
        if (EntranceGuardDao.getOpenLockType() == 0) {
            tvUnlockType.setText(getString(R.string.device_info_lock_type) + getString(R.string.setting_close_often));
        } else {
            tvUnlockType.setText(getString(R.string.device_info_lock_type) + getString(R.string.setting_open_often));
        }
        //开锁时间
        tvUnlockTime.setText(getString(R.string.device_info_lock_time) + EntranceGuardDao.getOpenLockTime() + "s");
        //门检测状态
        if (EntranceGuardDao.getDoorStateCheck() == 0) {
            tvDoorCheck.setText(getString(R.string.device_info_lock_door_check) + getString(R.string.pub_no));
        } else {
            tvDoorCheck.setText(getString(R.string.device_info_lock_door_check) + getString(R.string.pub_yes));
        }
        //报警输出
        if (EntranceGuardDao.getAlarmOut() == 0) {
            tvDoorAlarm.setText(getString(R.string.device_info_alarm_out) + getString(R.string.pub_no));
        } else {
            tvDoorAlarm.setText(getString(R.string.device_info_alarm_out) + getString(R.string.pub_yes));
        }
        //上报中心
        if (EntranceGuardDao.getUpdateCenter() == 0) {
            tvDoorUpload.setText(getString(R.string.device_info_update_center) + getString(R.string.pub_no));
        } else {
            tvDoorUpload.setText(getString(R.string.device_info_update_center) + getString(R.string.pub_yes));
        }
        //是否启用单元号
        if (fullDeviceNo.getUseCellNo() == 0) {
            tvCellNo.setText(getString(R.string.device_info_unit_num) + getString(R.string.pub_no));
        } else {
            tvCellNo.setText(getString(R.string.device_info_unit_num) + getString(R.string.pub_yes));
        }

        //剩余卡数
        tvCardFree.setText(getString(R.string.device_info_card_num) + SinglechipClientProxy.getInstance().getCardFreeCount());

        //剩余人脸数
        if (AppConfig.getInstance().isFaceEnabled()) {
            tvFaceFree.setVisibility(View.VISIBLE);
            HorizonFaceDao faceInfoDao = new HorizonFaceDao();
            tvFaceFree.setText(getString(R.string.device_info_face_num) + faceInfoDao.getSurplus());
        } else {
            tvFaceFree.setVisibility(View.GONE);
        }

        //剩余指纹数
        if (AppConfig.getInstance().isFingerEnabled()) {
            tvFingerprintFree.setVisibility(View.VISIBLE);
            tvFingerprintFree.setText(getString(R.string.device_info_fingerprint_num) + SinglechipClientProxy.getInstance().getFingerSurplus());
        } else {
            tvFingerprintFree.setVisibility(View.GONE);
        }

        //设备号
        if (type == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
            tvDevNo.setText(getString(R.string.device_info_device_no) + fullDeviceNo.getCurrentDeviceNo());
        } else {
            llPage1.removeView(tvDevNo);
        }
    }

    @SuppressLint("SetTextI18n")
    private void initPage2() {
        //密码进门模式
        if (ParamDao.getPwdDoorMode() == 0) {
            tvPwdMode.setText(getString(R.string.device_info_1) + getString(R.string.setting_pwd_door1));
        } else {
            tvPwdMode.setText(getString(R.string.device_info_1) + getString(R.string.setting_pwd_door2));
        }
        //强行开门报警
        if (AlarmParamDao.getForceOpen() == 0) {
            tvForcedOpen.setText(getString(R.string.device_info_2) + getString(R.string.setting_close));
        } else {
            tvForcedOpen.setText(getString(R.string.device_info_2) + getString(R.string.setting_enable));
        }
        //省电模式
        if (ParamDao.getPowerSave() == 0) {
            tvPowerSaving.setText(getString(R.string.device_info_6) + getString(R.string.setting_close));
        } else {
            tvPowerSaving.setText(getString(R.string.device_info_6) + getString(R.string.setting_enable));
        }
        //屏保
        if (ParamDao.getScreenPro() == 0) {
            tvScreenSaver.setText(getString(R.string.device_info_7) + getString(R.string.setting_close));
        } else {
            tvScreenSaver.setText(getString(R.string.device_info_7) + getString(R.string.setting_enable));
        }
        //动态密保
        if (AppConfig.getInstance().getPwdDynamic() == 0) {
            tvPwdDynamic.setText(getString(R.string.device_info_17) + getString(R.string.setting_close));
        } else {
            tvPwdDynamic.setText(getString(R.string.device_info_17) + getString(R.string.setting_enable));
        }

        //卡号长度
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
        tvCallVolume.setText(getString(R.string.device_info_14) + SystemSetUtils.getCallVolume());

        //提示音
        if (AppConfig.getInstance().getTipVolume() == 0) {
            tvPromptTone.setText(getString(R.string.device_info_15) + getString(R.string.setting_close));
        } else {
            tvPromptTone.setText(getString(R.string.device_info_15) + getString(R.string.setting_enable));
        }
        //按键音
        if (AppConfig.getInstance().getKeyVolume() == 0) {
            tvKeyTone.setText(getString(R.string.device_info_16) + getString(R.string.setting_close));
        } else {
            tvKeyTone.setText(getString(R.string.device_info_16) + getString(R.string.setting_enable));
        }
    }

    @SuppressLint("SetTextI18n")
    private void initPage3() {
        SnapParam snapParam = SnapParamDao.getSnapParam();
        //访客呼叫拍照
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
            tvPhotoCenter.setText(getString(R.string.device_info_18) + getString(R.string.setting_close));
        } else {
            tvPhotoCenter.setText(getString(R.string.device_info_18) + getString(R.string.setting_enable));
        }
        //人脸开门拍照
        if (showFace()) {
            if (snapParam.getFaceOpenSnap() == 0) {
                tvPhotoFace.setText(getString(R.string.device_info_19) + getString(R.string.setting_close));
            } else {
                tvPhotoFace.setText(getString(R.string.device_info_19) + getString(R.string.setting_enable));
            }
        }
        //指纹开门拍照
        if (showFinger()) {
            if (snapParam.getFingerOpenSnap() == 0) {
                tvPhotoFinger.setText(getString(R.string.device_info_20) + getString(R.string.setting_close));
            } else {
                tvPhotoFinger.setText(getString(R.string.device_info_20) + getString(R.string.setting_enable));
            }
        }
        //刷卡开门拍照
        if (snapParam.getCardOpenSnap() == 0) {
            tvPhotoCard.setText(getString(R.string.device_info_21) + getString(R.string.setting_close));
        } else {
            tvPhotoCard.setText(getString(R.string.device_info_21) + getString(R.string.setting_enable));
        }
        //密码开门拍照
        if (snapParam.getPwdOpenSnap() == 0) {
            tvPhotoPwd.setText(getString(R.string.device_info_22) + getString(R.string.setting_close));
        } else {
            tvPhotoPwd.setText(getString(R.string.device_info_22) + getString(R.string.setting_enable));
        }
        //扫码开门拍照
        if (showQrcode()) {
            if (snapParam.getQrcodeOpenSnap() == 0) {
                tvPhotoQrcode.setText(getString(R.string.device_info_23) + getString(R.string.setting_close));
            } else {
                tvPhotoQrcode.setText(getString(R.string.device_info_23) + getString(R.string.setting_enable));
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void initPage4() {
        NetworkParam networkParam = NetworkParamDao.getNetWorkParam();
        tvLocalIp.setText(getString(R.string.setting_ip) + networkParam.getLocalIp());
        tvSubnetMask.setText(getString(R.string.setting_mask) + networkParam.getSubNet());
        tvGatewayIp.setText(getString(R.string.setting_gateway) + networkParam.getGateway());
        tvDns.setText(getString(R.string.setting_dns) + networkParam.getDNS1());
        tvManagerIp.setText(getString(R.string.setting_admin) + networkParam.getAdminIp());
        tvCenterIp.setText(getString(R.string.setting_center_server) + networkParam.getCenterIp());
        tvMediaIp.setText(getString(R.string.setting_media_server) + networkParam.getMediaIp());

        // 只有梯口机时才有电梯控制器设置
        if (mDeviceType == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR) {
            tvElevatorIp.setVisibility(View.VISIBLE);
            tvElevatorIp.setText(getString(R.string.setting_elevator) + networkParam.getElevatorIp());
        } else {
            tvElevatorIp.setVisibility(View.GONE);
        }

        if (AppConfig.getInstance().isFaceEnabled()) {
            tvFaceIp.setVisibility(View.VISIBLE);
            tvFaceIp.setText(getString(R.string.setting_face_server) + networkParam.getFaceIp());
        } else {
            tvFaceIp.setVisibility(View.GONE);
        }

    }

    private void initPage5() {
        String osVer = getString(R.string.device_info_os_ver) + "Android " + Build.VERSION.RELEASE;
        String softVer = getString(R.string.device_info_soft_ver) + BuildConfigHelper.getSoftWareVer();
        String hardVer = getString(R.string.device_info_hard_ver) + BuildConfigHelper.getHardWareVer();
        String mac = getString(R.string.device_info_mac) + EthernetUtils.getMacAddress();

        tvOSver.setText(osVer);
        tvSoftVer.setText(softVer);
        tvHardVer.setText(hardVer);
        tvMac.setText(mac);

        // 云对讲序列号
        int cloudTalk = EntranceGuardDao.getCloudTalk();
        int authState = AuthManage.getAuthState();
        LogUtils.d(" cloudtalk is " + cloudTalk + ", authState is " + authState);
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
            int dimension = mContext.getResources().getDimensionPixelOffset(R.dimen.dp_140);
            Bitmap bitmap = QRCodeEncoder.encodeAsBitmap(qrcode, dimension, 0);
            if (bitmap != null) {
                ivSn.setImageBitmap(bitmap);
            }
        }
    }

    /**
     *  判断是否启用人脸开门拍照
     * @return  true/false
     */
    private boolean showFace() {
        if (!AppConfig.getInstance().isFaceEnabled()) {
            llPage3.removeView(tvPhotoFace);
            return false;
        } else {
            if (llPage3.indexOfChild(tvPhotoFace) < 0) {
                llPage3.addView(tvPhotoFace, 4);
            }
            return true;
        }
    }

    /**
     *  判断是否显示指纹开门拍照
     * @return true/false
     */
    private boolean showFinger() {
        if (!AppConfig.getInstance().isFingerEnabled()) {
            llPage3.removeView(tvPhotoFinger);
            return false;
        } else {
            if (llPage3.indexOfChild(tvPhotoFinger) < 0) {
                if (!AppConfig.getInstance().isFaceEnabled()) {
                    llPage3.addView(tvPhotoFinger, 4);
                } else {
                    llPage3.addView(tvPhotoFinger, 5);
                }
            }
            return true;
        }
    }

    /**
     * 判断是否显示扫码开门拍照
     * @return  true/false
     */
    private boolean showQrcode() {
        boolean show = false;
        int openType = AppConfig.getInstance().getQrOpenType();
        if (openType == 0) {
            if (AppConfig.getInstance().getQrScanEnabled() == 1) {
                show = true;
            }
        } else {
            String registerId = AppConfig.getInstance().getBluetoothDevId();
            if (registerId != null && registerId.length() > 0) {
                show = true;
            }
        }

        if (show) {
            if (llPage3.indexOfChild(tvPhotoQrcode) < 0) {
                llPage3.addView(tvPhotoQrcode);
            }
        } else {
            llPage3.removeView(tvPhotoQrcode);
        }
        return show;
    }

    /**
     * 获取云端信息描述
     * @return  云端信息描述
     */
    private String getCloudInfo() {
        StringBuilder builder = new StringBuilder();
        builder.append(getString(R.string.device_info_sn));

        String sn = MainClient.getInstance().Main_getCloudSn();
        if (sn == null || sn.contains("WRONG")) {
            return builder.toString();
        }

        builder.append(sn);
        builder.append(" (");
        if (MainClient.getInstance().Main_getCloudState() == 1) {
            builder.append(getString(R.string.cloud_state_ok));
        } else {
            builder.append(getString(R.string.cloud_state_fail));
        }
        builder.append(")");
        return builder.toString();
    }
}
