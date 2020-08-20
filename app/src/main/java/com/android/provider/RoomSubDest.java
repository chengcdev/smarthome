package com.android.provider;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.CommTypeDef.DeviceType;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.dao.DeviceNoRuleSubsectionDao;
import com.mili.smarthome.tkj.entities.deviceno.DeviceNoRuleSubsectionModel;

import java.util.List;


public class RoomSubDest {
	public static String ROOMSUBDES = "RoomSubDest";
	public static String SUBCOUNT = "SubCount";
	public static String SUBDEST = "SubDest";
	private SharedPreferences mSharedPreferences = null;
	private int SubIndex = 3;
	private String nSubString[];
	private Context mContext;
	private boolean mUsebestManager = false;

	public RoomSubDest(Context context){
		mContext = context;
        DeviceNoRuleSubsectionDao subsectionDao = new DeviceNoRuleSubsectionDao();
        DeviceNoRuleSubsectionModel subsectionModel = subsectionDao.queryModel(0);
        int subCount = subsectionModel.getSubCount();
        List<DeviceNoRuleSubsectionModel> list = subsectionDao.queryModel();
        SubIndex = subCount;
        nSubString = new String[SubIndex];
        if (list.size() >= SubIndex) {
            for (int i = 0; i < SubIndex; i++) {
				nSubString[i] =  list.get(i + 1).getSubsection();
            }
        }
	}

	public void updata(){
		SubIndex = mSharedPreferences.getInt(SUBCOUNT, 0);
		nSubString = new String[SubIndex];
		for (int i = 0; i < SubIndex; i++){
			nSubString[i] = mSharedPreferences.getString(SUBDEST+i, "");
		}
	}

	public int getSubCount(){
		return SubIndex;
	}

	public String getSubDest(int index){
		if(index > (SubIndex-1))
		{
			return null;
		}

		return nSubString[index];
	}

	public String getSubDestDevNumber(){

		// TODO: 2019/3/25
		FullDeviceNo mFullDeviceNo = new FullDeviceNo(mContext);
		int devtype = mFullDeviceNo.getDeviceType();
		String devno = mFullDeviceNo.getDeviceNo();

		char buffer[] = new char[20];
		int len,dev = 0;
		String devString = null;

		Log.d("RoomSubDest", "getSubDestDevNumber devtype ["+devtype+"] devno = ["+devno+"]");
		switch(devtype){
			case DeviceType.DEVICE_TYPE_MANAGER:
                devString = mContext.getString(R.string.intercall_dev_center);
				Log.d("getSubDestDevNumber", "Device is "+devString);
				break;

			case DeviceType.DEVICE_TYPE_AREA:
//				dev = Integer.valueOf(devno);
				devString = devno;
				break;

			case DeviceType.DEVICE_TYPE_STAIR: {

                String rule = String.valueOf(mFullDeviceNo.getSubsection());

                Log.d("RoomSubDest", "rule [ " + rule + "] devno [" + devno + "]");
                int rule_length = mFullDeviceNo.getStairNoLen() + mFullDeviceNo.getRoomNoLen();
                Log.d("RoomSubDest", "rule_length [ " + rule_length + "] devno [" + devno.length() + "]");
                if (rule_length > devno.length()) {
                    devString = devno;
                } else {
                    if (devno.length() == 2) {
                        int devnum = Integer.parseInt(devno);
                        devString = mContext.getString(R.string.intercall_dev_stair)+devnum;
                    } else {

                        Log.e("RoomSubDest", "devno.length() = " + devno.length() + "  mFullDeviceNo.getDevNoLen() = " + mFullDeviceNo.getDevNoLen());
                        if (devno.length() == mFullDeviceNo.getDevNoLen() || devno.length() == (mFullDeviceNo.getDevNoLen() - 1)) {
                            rule.getChars(0, rule.length(), buffer, 0);

                            dev = buffer[0] - '0';
                            len = dev;
                            if (len > devno.length()) {
                                devString = devno;
                                break;
                            }
                            devString = devno.substring(0, dev) + nSubString[0];
                            for (int i = 1; i < rule.length(); i++) {
                                dev = buffer[i] - '0';
                                // 防止分段描述总和大于设备编号长度
                                if ((dev + len) > devno.length()) {
                                    devString = devno;
                                    break;
                                }

                                if (i > (SubIndex - 1)) {
                                    devString += devno.substring(len, len + dev);
                                } else {
                                    devString += devno.substring(len, len + dev) + nSubString[i];
                                }
                                len += dev;

                                if (len >= mFullDeviceNo.getStairNoLen()) {
                                    break;
                                }
                            }

                            //分机描述
//                            devString += mFullDeviceNo.getStairNo() + mContext.getString(R.string.intercall_dev_fenji);
                            Log.e("RoomSubDest", "devString = " + devString);

                        } else {
                            devString = devno;
                        }
                    }
                }
            }
				break;

			case DeviceType.DEVICE_TYPE_ROOMFJ:
				if (devno.length() == 1) {
					int devnum = Integer.parseInt(devno);
					if (devnum == 0)
					{
						devString = mContext.getString(R.string.intercall_dev_roommain);
					}else {
						devString = mContext.getString(R.string.intercall_dev_fenji)+(devnum);
					}
				} else {
					devno.getChars(0, devno.length(), buffer, 0);
					dev = buffer[devno.length()-1]-'0';
					switch(dev)
					{
						case 0:
							devString = mContext.getString(R.string.intercall_dev_roommain);
							break;
						default:
							devString = mContext.getString(R.string.intercall_dev_fenji)+dev;
							break;
					}
				}
				break;

			case DeviceType.DEVICE_TYPE_ROOM: {
                String rule = String.valueOf(mFullDeviceNo.getSubsection());

                Log.d("", "rule [ " + rule + "] devno [" + devno + "]");
                int rule_length = mFullDeviceNo.getStairNoLen() + mFullDeviceNo.getRoomNoLen();
                Log.d("", "rule_length [ " + rule_length + "] devno [" + devno.length() + "]");
                if (rule_length > devno.length()) {
                    devString = devno;
                } else {
                    if (devno.length() == 1) {
                        int devnum = Integer.parseInt(devno);
                        if (devnum == 0) {
                            devString = mContext.getString(R.string.intercall_dev_roommain);
                        } else {
                            devString = mContext.getString(R.string.intercall_dev_fenji) + devnum;
                        }
                    } else {

                        if (devno.length() == mFullDeviceNo.getDevNoLen() || devno.length() == (mFullDeviceNo.getDevNoLen() - 1)) {
                            rule.getChars(0, rule.length(), buffer, 0);

                            dev = buffer[0] - '0';
                            len = dev;
                            if (len > devno.length()) {
                                devString = devno;
                                break;
                            }
                            devString = devno.substring(0, dev) + nSubString[0];
                            for (int i = 1; i < rule.length(); i++) {
                                dev = buffer[i] - '0';
                                // 防止分段描述总和大于设备编号长度
                                if ((dev + len) > devno.length()) {
                                    devString = devno;
                                    break;
                                }

                                if (i > (SubIndex - 1)) {
                                    devString += devno.substring(len, len + dev);
                                } else {
                                    devString += devno.substring(len, len + dev) + nSubString[i];
                                }
                                len += dev;
                            }
                            if (devno.length() == mFullDeviceNo.getDevNoLen()) {
                                String laString = devno.substring(devno.length() - 1, devno.length());
                                Log.d("", "laString is " + laString);
                                if (laString.equals("0")) {
                                } else {
                                    devString += mContext.getString(R.string.intercall_dev_fenji) + laString;
                                }
                            }
                        } else {
                            devString = devno;
                        }
                    }
                }
            }
				break;

			case DeviceType.DEVICE_TYPE_DOOR_PHONE:
			case DeviceType.DEVICE_TYPE_DOOR_NET:
				if(devno.length() > 0){
					devno.getChars(devno.length()-1, devno.length(), buffer, 0);
					dev = buffer[0]-'6'+1;
					Log.d("RoomSubDest", "getSubDestDevNumber dev ["+dev+"]");
					if(dev == 3 || dev == 4){
						dev = dev-2;
					}else{
						dev = dev+2;
					}
					devString = mContext.getString(R.string.intercall_dev_door)+dev;
				}
				break;

			default:
				break;
		}

		return devString;
	}

	public int getSubDestDevLen(int devtype)
	{
		int len = 0;
		switch(devtype){
			case DeviceType.DEVICE_TYPE_MANAGER:
				len = 2;
				break;

			case DeviceType.DEVICE_TYPE_AREA:
				len = 2;
				break;

			case DeviceType.DEVICE_TYPE_STAIR:
				break;

			case DeviceType.DEVICE_TYPE_ROOMFJ:
			case DeviceType.DEVICE_TYPE_DOOR_PHONE:
			case DeviceType.DEVICE_TYPE_DOOR_NET:
			case DeviceType.DEVICE_TYPE_ROOM:
				FullDeviceNo mFullDeviceNo = new FullDeviceNo(mContext);
				len = mFullDeviceNo.DevNoLen;
				break;

			default:
				break;
		}

		return len;
	}
}

