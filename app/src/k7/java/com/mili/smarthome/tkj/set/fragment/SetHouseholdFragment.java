package com.mili.smarthome.tkj.set.fragment;


import android.annotation.SuppressLint;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.dao.ResidentSettingDao;
import com.mili.smarthome.tkj.main.fragment.BaseKeyBoardFragment;
import com.mili.smarthome.tkj.main.interf.ISetCallBackListener;
import com.mili.smarthome.tkj.main.widget.SetSuccessView;
import com.mili.smarthome.tkj.set.Constant;
import com.mili.smarthome.tkj.set.resident.ResidentListManage;
import com.mili.smarthome.tkj.set.widget.inputview.CustomInputAdapter;
import com.mili.smarthome.tkj.set.widget.inputview.CustomInputView;
import com.mili.smarthome.tkj.utils.AppManage;

import java.util.Objects;

/**
 * 住户设置
 */

public class SetHouseholdFragment extends BaseKeyBoardFragment implements ISetCallBackListener {


    private TextView tvTitle;
    private TextView tvName;
    private CustomInputView tvContent;
    private int index = 0;
    private SetSuccessView successView;
    private String roomNumStart;
    private String floorNum;
    private String floorHouseNum;
    private final int roomNumStartId = 1001;
    private final int floorNumId = 1002;
    private final int floorHouseNumId = 1003;
    private ResidentSettingDao residentSettingDao;


    @Override
    public int getLayout() {
        return R.layout.fragment_setting_household;
    }

    @Override
    public void initView() {
        tvTitle = (TextView) getContentView().findViewById(R.id.tv_title);
        tvName = (TextView) getContentView().findViewById(R.id.tv_name);
        tvContent = (CustomInputView) getContentView().findViewById(R.id.it_content);
        successView = (SetSuccessView) getContentView().findViewById(R.id.root);

    }


    @SuppressLint("SetTextI18n")
    @Override
    public void initAdapter() {
        tvTitle.setText(getString(R.string.setting_household));
        tvName.setText(getString(R.string.setting_household_start));

        if (residentSettingDao == null) {
            residentSettingDao = new ResidentSettingDao();
        }

        roomNumStart = residentSettingDao.getRoomStart();
        floorNum = residentSettingDao.getFloorCount();
        floorHouseNum = residentSettingDao.getFloorHouseNum();

        tvContent.setFirstFlash(true).init(roomNumStart, 2, CustomInputAdapter.INPUT_TYPE_OTHER);
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
                index--;
                changeContent();
                break;
            case Constant.KEY_CONFIRM:
                index++;
                changeContent();
                break;
            case Constant.KEY_UP:

                break;
            case Constant.KEY_DELETE:
                tvContent.deleteNum("0");
                switch (index) {
                    case 0:
                        roomNumStart = tvContent.getNum();
                        break;
                    case 1:
                        floorNum = tvContent.getNum();
                        break;
                    case 2:
                        floorHouseNum = tvContent.getNum();
                        break;
                }
                break;
            case Constant.KEY_NEXT:

                break;
            default:
                if (index == 0 && tvContent.getCount() == 1 && keyId.equals("0")) {
                    return;
                }
                tvContent.addNum(keyId);
                switch (index) {
                    case 0:
                        roomNumStart = tvContent.getNum();
                        break;
                    case 1:
                        floorNum = tvContent.getNum();
                        break;
                    case 2:
                        floorHouseNum = tvContent.getNum();
                        break;
                }
                if (tvContent.getCount() >= tvContent.getNum().length()) {
                    tvContent.setCount(0);
                    tvContent.setFirstFlash(true);
                    tvContent.notifychange();
                }
                break;
        }
    }


    private void changeContent() {
        switch (index) {
            case -1:
                //退出界面
                exitFragment(this);
                break;
            case 0:
                tvName.setText(getString(R.string.setting_household_start));
                tvContent.init(roomNumStart, 2, CustomInputAdapter.INPUT_TYPE_OTHER);
                break;
            case 1:
                tvName.setText(getString(R.string.setting_household_floor_num));
                tvContent.init(floorNum, 2, CustomInputAdapter.INPUT_TYPE_OTHER);
                break;
            case 2:
                tvName.setText(getString(R.string.setting_household_floor_user));
                tvContent.init(floorHouseNum, 2, CustomInputAdapter.INPUT_TYPE_OTHER);
                break;
            case 3:
                if (Integer.valueOf(roomNumStart) == 0) {
                    roomNumStart = "01";
                }
                residentSettingDao.setRoomStart(roomNumStart);
                residentSettingDao.setFloorCount(floorNum);
                residentSettingDao.setFloorHouseNum(floorHouseNum);
                //跳转到编辑住户名称界面
                if (AppConfig.getInstance().getCallType() == 1) {
                    //初始化住户列表
                    ResidentListManage.getInstance().addResidentList();
                    AppManage.getInstance().sendReceiver(Constant.ActionId.ACTION_DIRECT_EDIT_VIEW);
                    successView.showSuccessView(getString(R.string.setting_success), 2000, new ISetCallBackListener() {
                        @Override
                        public void success() {
                            Objects.requireNonNull(getActivity()).finish();
                        }

                        @Override
                        public void fail() {

                        }
                    });
                } else {
                    successView.showSuccessView(getString(R.string.setting_success), 1000, this);
                }
                break;
        }
    }

    @Override
    public void success() {
        exitFragment(this);
    }

    @Override
    public void fail() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
