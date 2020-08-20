package com.mili.smarthome.tkj.set.resident;

import com.android.CommStorePathDef;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.ContextProxy;
import com.mili.smarthome.tkj.dao.DirectResidentsDao;
import com.mili.smarthome.tkj.dao.ResidentSettingDao;
import com.mili.smarthome.tkj.dao.param.ParamDao;
import com.mili.smarthome.tkj.main.entity.ResidentListEntity;
import com.mili.smarthome.tkj.utils.AppManage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 住户列表
 */

public class ResidentListManage {

    private static ResidentListManage residentListManage;

    private ResidentSettingDao residentSettingDao;

    private List<ResidentListEntity> mResidentList = new ArrayList<>();

    private Map<String, ResidentListEntity> mCacheResident = new HashMap<>();

    private List<String> mResidentRoomList;
    //保存住户列表的路径
    private static final String RESIDENT_LIST_PATH = CommStorePathDef.PARAM_PATH + "/resident.dat";

    public static ResidentListManage getInstance() {
        if (residentListManage == null) {
            residentListManage = new ResidentListManage();
        }
        return residentListManage;
    }

    /**
     * 添加住户列表
     */
    public void addResidentList() {

        if (mCacheResident.size() == 0) {
            //读取保存的列表文件
            List list = AppManage.getInstance().readFlieList(RESIDENT_LIST_PATH);
            if (list != null) {
                List<ResidentListEntity> residentListEntityList = list;
                for (int i = 0; i < residentListEntityList.size(); i++) {
                    if (residentListEntityList.get(i) != null) {
                        String roomNo = residentListEntityList.get(i).getRoomNo();
                        if (roomNo != null) {
                            mCacheResident.put(residentListEntityList.get(i).getRoomNo(), residentListEntityList.get(i));
                        }
                    }
                }
            }
        }

        if (residentSettingDao == null) {
            residentSettingDao = new ResidentSettingDao();
        }

        mResidentRoomList = new ArrayList<>();

        //添加管理中心
        if (ParamDao.getEnableCenter() == 1) {
            mResidentRoomList.add(DirectResidentsDao.ROOM_NO_MANAGE);
        }
        mResidentRoomList.addAll(residentSettingDao.getResidentRoomList());


        for (int i = 0; i < mResidentRoomList.size(); i++) {
            //管理员机
            String roomNo = mResidentRoomList.get(i);
            if (mCacheResident.get(roomNo) == null) {
                if (roomNo.equals(DirectResidentsDao.ROOM_NO_MANAGE)) {
                    mCacheResident.put(roomNo, new ResidentListEntity(roomNo, ContextProxy.getString(R.string.manage_center)));
                } else {
                    mCacheResident.put(roomNo, new ResidentListEntity(roomNo, roomNo));
                }
            }
        }


        mResidentList.clear();
        for (int i = 0; i < mResidentRoomList.size(); i++) {
            mResidentList.add(mCacheResident.get(mResidentRoomList.get(i)));
        }

        if (mResidentList.size() % 4 != 0) {
            int p = 4 - (mResidentList.size() % 4);
            for (int i = 0; i < p; i++) {
                //空行 补齐列表
                mResidentList.add(new ResidentListEntity("", ""));
            }
        }

        toSaveDatas();
    }

    private void toSaveDatas() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //保存列表
                List<ResidentListEntity> list = new ArrayList<>(mCacheResident.values());
                AppManage.getInstance().writeFileList(RESIDENT_LIST_PATH,list);
            }
        }).start();
    }

    /**
     * 获取住户列表
     */
    public List<ResidentListEntity> getResidentList() {
        return mResidentList;
    }

    /**
     * 修改住户信息
     */
    public void setResidentList(int position, ResidentListEntity residentListEntity) {
        mResidentList.set(position, residentListEntity);
        mCacheResident.put(mResidentList.get(position).getRoomNo(), residentListEntity);
        toSaveDatas();
    }

}
