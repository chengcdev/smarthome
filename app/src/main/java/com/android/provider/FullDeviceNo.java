package com.android.provider;

import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;

import com.android.CommTypeDef;
import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.dao.DeviceNoDao;
import com.mili.smarthome.tkj.entities.deviceno.DeviceNoModel;

import java.io.FileInputStream;

public class FullDeviceNo implements Parcelable {

    public static final int DEFAULT_SUBSECTION = 224;
    /**
     * 小区编号
     */
    public int AreaNo = 1;
    /**
     * 设备号高位，不大于999999999
     */
    public int DeviceNo1 = 0;
    /**
     * 设备号低位，不大于999999999
     */
    public int DeviceNo2 = 010100001;
    /**
     * 总的设备编号
     */
    public String DeviceNo = "010100001";
    /**
     * 梯号长度
     */
    public byte StairNoLen = 4;
    /**
     * 房号长度
     */
    public byte RoomNoLen = 4;
    /**
     * 单元号长度，默认2
     */
    public byte CellNoLen = 2;
    /**
     * 启动单元标志，0－false 1－true， 默认1
     */
    public byte UseCellNo = 1;
    /**
     * 分段参数，0为不分段，默认224
     */
    public int Subsection = DEFAULT_SUBSECTION;
    /**
     * 设备类型
     */
    public byte DeviceType = CommTypeDef.DeviceType.DEVICE_TYPE_STAIR;
    /**
     * 设备编号长度
     */
    public byte DevNoLen = 9;
    /**
     *
     */
    public byte IsRight = 1;
    /**
     *
     */
    public String DeviceNoStr = "";
    /**
     *
     */
    public String SubRuleDesc = "";
    /**
     * 梯口号
     */
    public String StairNo = "01";
    /**
     * 当前设备号
     */
    public String CurrentDeviceNo = "0101";

    private ContentResolver dbresolver;
    private ContextWrapper dbwrapper;
    private Context dbcontext;
    private FileInputStream fis;
    private Resources myres;
    private DeviceNoDao deviceNoDao;


    public FullDeviceNo(Context context) {
        super();
        deviceNoDao = new DeviceNoDao();
        // TODO Auto-generated constructor stub
        dbcontext = context;
        dbwrapper = new ContextWrapper(context);
        myres = dbcontext.getResources();
        getvalue();
    }

    public FullDeviceNo(Parcel source) {
        // TODO Auto-generated constructor stub
        if (source != null) {
            AreaNo = source.readInt();
            DeviceNo1 = source.readInt();
            DeviceNo2 = source.readInt();
            DeviceNo = source.readString();
            StairNoLen = source.readByte();
            RoomNoLen = source.readByte();
            CellNoLen = source.readByte();
            UseCellNo = source.readByte();
            Subsection = source.readInt();
            DeviceType = source.readByte();
            DevNoLen = source.readByte();
            IsRight = source.readByte();
            DeviceNoStr = source.readString();
            SubRuleDesc = source.readString();
            StairNo = source.readString();
            CurrentDeviceNo = source.readString();
        }
    }

    public void getvalue() {
        DeviceNo = getDeviceNo();
        AreaNo = getAreaNo();
        StairNoLen = getStairNoLen();
        RoomNoLen = getRoomNoLen();
        CellNoLen = getCellNoLen();
        UseCellNo = getUseCellNo();
        Subsection = getSubsection();
        DeviceType = getDeviceType();
        DevNoLen = getDevNoLen();
        DeviceNo1 = getDeviceNo1();
        DeviceNo2 = getDeviceNo2();
        StairNo = getStairNo();
        /*DeviceNoStr = getDeviceNoStr();*/
    }

    public void setvalue(ContentResolver resolver) {
        setAreaNo(AreaNo);
        setCellNoLen(CellNoLen);
        setDeviceNo(DeviceNo);
        setDeviceType(DeviceType);
        setRoomNoLen(RoomNoLen);
        setStairNoLen(StairNoLen);
        setSubsection(Subsection);
        setUseCellNo(UseCellNo);
    }

    /**
     * 获取区口机号
     *
     * @return the areaNo
     */
    public int getAreaNo() {
        if (deviceNoDao != null) {
            DeviceNoModel deviceNoModel = deviceNoDao.queryDeviceNoModel();
            String areaNo = deviceNoModel.getAreaNo();
            AreaNo = Integer.valueOf(areaNo);
        }
        return AreaNo;
    }

    /**
     * 设置区口号
     *
     * @param areaNo the areaNo to set
     */
    public void setAreaNo(int areaNo) {
        AreaNo = areaNo;
        if (deviceNoDao != null) {
            deviceNoDao.setAreaNo(areaNo);
        }
    }

    /**
     * 获取设备编号
     */
    public String getDeviceNo() {
        if (deviceNoDao != null) {
            DeviceNoModel deviceNoModel = deviceNoDao.queryDeviceNoModel();
            String deviceNo = deviceNoModel.getDeviceNo();
            DeviceNo = deviceNo;
        }
        return DeviceNo;
    }

    /**
     * 设置设备编号
     *
     * @param deviceNo the deviceNo to set
     */
    public void setDeviceNo(final String deviceNo) {
        DeviceNo = deviceNo;
        if (deviceNoDao != null) {
            deviceNoDao.setDeviceNo(deviceNo);
        }
    }


    public String getDeviceNo(final String stairNo,String roomNo) {
        String currentStair;
        int stair = Integer.valueOf(stairNo)-1;
        currentStair = String.valueOf(stair);
        String deviceNo = roomNo + currentStair;
        int deviceNoisright = StairNoLen + RoomNoLen + 1;
        int value = deviceNoisright - deviceNo.length();
        //小于最大长度 补0
        for (int i = 0; i < value; i++) {
            roomNo = roomNo + "0";
        }
        deviceNo = roomNo + currentStair;
        return deviceNo;
    }

    /**
     * 获取梯号长度
     *
     * @return the stairNoLe
     */
    public byte getStairNoLen() {
        if (deviceNoDao != null) {
            DeviceNoModel deviceNoModel = deviceNoDao.queryDeviceNoModel();
            int stairNoLen = deviceNoModel.getStairNoLen();
            StairNoLen = (byte) stairNoLen;
        }
        return StairNoLen;
    }

    /**
     * 设置梯号长度
     */
    public void setStairNoLen(final byte stairNoLen) {
        StairNoLen = stairNoLen;
        if (deviceNoDao != null) {
            deviceNoDao.setStairNoLen(stairNoLen);
        }
    }

    /**
     * 获取房号长度
     *
     * @return the roomNoLen
     */
    public byte getRoomNoLen() {
        if (deviceNoDao != null) {
            DeviceNoModel deviceNoModel = deviceNoDao.queryDeviceNoModel();
            int roomNoLen = deviceNoModel.getRoomNoLen();
            RoomNoLen = (byte) roomNoLen;
        }
        return RoomNoLen;
    }

    /**
     * 设置房号长度
     */
    public void setRoomNoLen(final byte roomNoLen) {
        RoomNoLen = roomNoLen;
        if (deviceNoDao != null) {
            deviceNoDao.setRoomNoLen(roomNoLen);
        }
    }

    /**
     * 单元号长度
     *
     * @return the cellNoLen
     */
    public byte getCellNoLen() {
        if (deviceNoDao != null) {
            DeviceNoModel deviceNoModel = deviceNoDao.queryDeviceNoModel();
            int cellNoLen = deviceNoModel.getCellNoLen();
            CellNoLen = (byte) cellNoLen;
        }
        return CellNoLen;
    }

    /**
     * 设置单元号长度
     */
    public void setCellNoLen(final byte cellNoLen) {
        CellNoLen = cellNoLen;
        if (deviceNoDao != null) {
            deviceNoDao.setCellNoLen(cellNoLen);
        }
    }

    /**
     * 是否启用单元号
     *
     * @return the useCellNo
     */
    public byte getUseCellNo() {
        if (deviceNoDao != null) {
            DeviceNoModel deviceNoModel = deviceNoDao.queryDeviceNoModel();
            int useCellNo = deviceNoModel.getUseCellNo();
            UseCellNo = (byte) useCellNo;
        }
        return UseCellNo;
    }

    /**
     * 设置是否启用单元号
     *
     * @param useCellNo the useCellNo to set
     */
    public void setUseCellNo(final byte useCellNo) {
        UseCellNo = useCellNo;
        if (deviceNoDao != null) {
            deviceNoDao.setUseCellNo(useCellNo);
        }
    }

    /**
     * 获取分段参数
     *
     * @return
     */
    public int getSubsection() {
        int value = 224;
        Subsection = value;
        if (deviceNoDao != null) {
            DeviceNoModel deviceNoModel = deviceNoDao.queryDeviceNoModel();
            int subSection = deviceNoModel.getSubSection();
            Subsection = subSection;
        }
        return Subsection;
    }

    /**
     * 设置分段参数
     *
     * @param subsection
     */
    public void setSubsection(final int subsection) {
        Subsection = subsection;
        if (deviceNoDao != null) {
            deviceNoDao.setSubsection(subsection);
        }
    }

    /**
     * 获取设备类型
     *
     * @return the deviceType
     */
    public byte getDeviceType() {
        if (deviceNoDao != null) {
            DeviceNoModel deviceNoModel = deviceNoDao.queryDeviceNoModel();
            int deviceType = deviceNoModel.getDeviceType();
            DeviceType = (byte) deviceType;
        }
        return DeviceType;
    }

    /**
     * 设置设备类型
     *
     * @param deviceType the deviceType to set
     */
    public void setDeviceType(final byte deviceType) {
        DeviceType = deviceType;
        if (deviceNoDao != null) {
            deviceNoDao.setDeviceType(deviceType);
        }
    }

    /**
     * 获取梯口号
     */
    public String getStairNo() {
        if (deviceNoDao != null) {
            DeviceNoModel deviceNoModel = deviceNoDao.queryDeviceNoModel();
            String stairNo = deviceNoModel.getStairNo();
            StairNo = stairNo;
        }
        return StairNo;
    }

    /**
     * 设置梯口号
     */
    public void setStairNo(final String stairNo) {
        StairNo = stairNo;
        if (deviceNoDao != null) {
            deviceNoDao.setStairNo(stairNo);
        }
    }

    /**
     * 获取当前界面显示的设备号
     */
    public String getCurrentDeviceNo() {
        if (deviceNoDao != null) {
            DeviceNoModel deviceNoModel = deviceNoDao.queryDeviceNoModel();
            String currentDeviceNo = deviceNoModel.getCurrentDeviceNo();
            CurrentDeviceNo = currentDeviceNo;
        }
        return CurrentDeviceNo;
    }

    /**
     * 设置当前界面显示的设备号
     */
    public void setCurrentDeviceNo(String currentDeviceNo) {
        if (currentDeviceNo.equals("")) {
            currentDeviceNo = "0";
        }
        CurrentDeviceNo = currentDeviceNo;
        if (deviceNoDao != null) {
            deviceNoDao.setCurrentDeviceNo(currentDeviceNo);
        }
    }

    /**
     * 设备编号长度 *
     *
     * @return the devNoLen
     */
    public byte getDevNoLen() {
        DevNoLen = (byte) DeviceNo.length();
        return DevNoLen;
    }

    /**
     * @param devNoLen the devNoLen to set
     */
    public void setDevNoLen(byte devNoLen) {
        // DevNoLen = devNoLen;
    }

    /**
     * @return the deviceNo1
     */
    public int getDeviceNo1() {

        if (DevNoLen > 9) {
            char[] dnl = new char[DevNoLen - 9];
            DeviceNo.getChars(0, DevNoLen - 9, dnl, 0);
            String deviceno = String.valueOf(dnl);
            DeviceNo1 = Integer.valueOf(deviceno).intValue();
        } else {
            DeviceNo1 = 0;
        }
        return DeviceNo1;
    }

    /**
     * @return the deviceNo2
     */
    public int getDeviceNo2() {
        if (DevNoLen > 9) {
            char[] dnl = new char[9];
            DeviceNo.getChars(DevNoLen - 9, DevNoLen, dnl, 0);
            String deviceno = String.valueOf(dnl);
            DeviceNo2 = Integer.valueOf(deviceno).intValue();
        } else {
            DeviceNo2 = Integer.valueOf(DeviceNo).intValue();
        }
        return DeviceNo2;
    }

    /**
     * 设备编号规则改变更新设备编号
     */
    public void notifyDeviceNo() {
        setDeviceNo(getDeviceNo(StairNo,CurrentDeviceNo));
    }


    /**
     * @return the deviceNoStr
     */
    public String getDeviceNoStr() {

        if (DeviceNo == null || DeviceNo.length() <= 4)
            return DeviceNo;
        int number = Integer.valueOf(DeviceNo).intValue();

        if (number >= 51 && number < 90) {
            /*
             * DeviceNoStr = String.valueOf(number - 50) +
             * dbwrapper.getResources().getString(R.string.cancel);
             */
            return DeviceNoStr;
        }

        char[] stair = {0}, cell = {0}, room = {0};
        // char des1[11]={0},des2[11]={0},des3[11]={0};
        String devno;
        int i, j = 0;
        int sub = Subsection;
        String strsub;
        strsub = String.valueOf(sub);
        int subLen = strsub.length();
        int nsub = 0;
        String des;
        des = getSubRuleDesc();
        if (subLen < 2)
            return DeviceNo;
        char[] num1 = null, des1 = null;
        char[] num = {0};
        int index = 0, numIndex = 0, numCount = 0;
        devno = DeviceNo;
        String tmp = null;
        int numTextLen = devno.length();

        for (i = subLen - 1; i >= 0; i--) {
            num[0] = strsub.charAt(subLen - 1 - i);
            num[1] = 0;
            nsub = Integer.valueOf(String.valueOf(num)).intValue();

            des.getChars(10 * (subLen - 1 - i), 10 * (subLen - 1 - i) + 10,
                    des1, 0);
            numIndex = 0;
            for (j = index; j < nsub + numCount; j++) {

                num1[numIndex++] = devno.charAt(j);
                index++;
            }
            numCount = index;
            num1[numIndex] = 0;
            tmp = tmp + String.valueOf(num1) + des1;
            if (j + 1 == numTextLen || j == numTextLen) {
                if (j + 1 == numTextLen) {
                    num1[0] = devno.charAt(j + 1);
                    num[1] = 0;
                    tmp = tmp + String.valueOf(num1);
                }
                break;
            }

        }
        DeviceNoStr = tmp;
        return DeviceNoStr;
    }


    public String getDeviceStr(int DeviceType, String Deviceno) {
        String str = null;
        int length;
        char num;
        int flag, flag_1 = 0;
        switch (DeviceType) {
            case CommTypeDef.DeviceType.DEVICE_TYPE_MANAGER:
                length = Deviceno.length();
                int number = Integer.valueOf(Deviceno.substring(0, length - 1))
                        .intValue();
                if (number >= 51 && number < 90) {
                    DeviceNoStr = String.valueOf(number - 50);
                }
                str = myres.getString(R.string.intercall_dev_center) + DeviceNoStr;
                break;
            case CommTypeDef.DeviceType.DEVICE_TYPE_AREA:
                str = myres.getString(R.string.intercall_dev_area) + Deviceno;
                break;
            case CommTypeDef.DeviceType.DEVICE_TYPE_STAIR:
                length = Deviceno.length();
                num = Deviceno.charAt(length - 3);
                str = myres.getString(R.string.intercall_dev_stair) + String.valueOf(num);
                break;
            case CommTypeDef.DeviceType.DEVICE_TYPE_ROOM:
                length = Deviceno.length();
                num = Deviceno.charAt(length - 2);
                flag = Integer.valueOf(String.valueOf(num));
                if (flag < 6) {
                    str = getDeviceNoString(Deviceno);
                }
                if (flag == 6 || flag == 7) {
                    flag_1 = flag - 5;
                    // str = myres.getString(R.string.msg_device_door)+
                    // String.valueOf(flag_1);
                } else if (flag == 8 || flag == 9) {
                    flag_1 = flag - 7;
                    // str = myres.getString(R.string.msg_device_netdoor)+
                    // String.valueOf(flag_1);
                }
                break;
            case CommTypeDef.DeviceType.DEVICE_TYPE_DOOR_PHONE:
                length = Deviceno.length();
                num = Deviceno.charAt(length - 2);
                flag = Integer.valueOf(String.valueOf(num));
                if (flag < 5) {
                    flag_1 = flag;
                } else {
                    flag_1 = flag - 5;
                }
                str = myres.getString(R.string.intercall_dev_door) + String.valueOf(flag_1);
                break;
            case CommTypeDef.DeviceType.DEVICE_TYPE_DOOR_NET:
                length = Deviceno.length();
                num = Deviceno.charAt(length - 2);
                flag = Integer.valueOf(String.valueOf(num));
                if (flag < 5) {
                    flag_1 = flag;
                } else {
                    flag_1 = flag - 7;
                }
                str = myres.getString(R.string.intercall_dev_door) + String.valueOf(flag_1);
                break;
            default:
                break;
        }
        return str;
    }


    public String getDeviceNoString(String no) {
        String str = null;
        int stair = StairNoLen;
        int room = RoomNoLen;
        int cell = CellNoLen;

        if (UseCellNo != 0) {
            char stairarry[] = new char[stair - cell];
            char roomarry[] = new char[room];
            char cellarry[] = new char[cell];
            for (int i = 0; i < stair - cell; i++) {
                stairarry[i] = no.charAt(i);
            }
            for (int i = 0; i < room; i++) {
                roomarry[i] = no.charAt(stair + i);
            }
            for (int i = 0; i < cell; i++) {
                cellarry[i] = no.charAt(stair - cell + i);
            }
        } else {
            char stairarry[] = new char[stair];
            char roomarry[] = new char[room];

            for (int i = 0; i < stair; i++) {
                stairarry[i] = no.charAt(i);
            }
            for (int i = 0; i < room; i++) {
                roomarry[i] = no.charAt(stair + i);
            }
        }
        return str;
    }


    public String getDeviceNoStr(String callno) {

        if (DeviceNo == null || DeviceNo.length() <= 4)
            return DeviceNo;
        int number = Integer.valueOf(DeviceNo).intValue();

        if (number >= 51 && number < 90) {
            return DeviceNoStr;
        }

        char[] stair = {0}, cell = {0}, room = {0};
        String devno;
        int i, j = 0;
        int sub = Subsection;
        String strsub;
        strsub = String.valueOf(sub);
        int subLen = strsub.length();
        int nsub = 0;
        String des;
        des = getSubRuleDesc();
        if (subLen < 2)
            return DeviceNo;
        char[] num1 = null, des1 = null;
        char[] num = {0};
        int index = 0, numIndex = 0, numCount = 0;
        devno = DeviceNo;
        String tmp = null;
        int numTextLen = devno.length();

        for (i = subLen - 1; i >= 0; i--) {
            num[0] = strsub.charAt(subLen - 1 - i);
            num[1] = 0;
            nsub = Integer.valueOf(num.toString()).intValue();

            des.getChars(10 * (subLen - 1 - i), 10 * (subLen - 1 - i) + 10,
                    des1, 0);
            numIndex = 0;
            for (j = index; j < nsub + numCount; j++) {

                num1[numIndex++] = devno.charAt(j);
                index++;
            }
            numCount = index;
            num1[numIndex] = 0;
            tmp = tmp + num1.toString() + des1;
            if (j + 1 == numTextLen || j == numTextLen) {
                if (j + 1 == numTextLen) {
                    num1[0] = devno.charAt(j + 1);
                    num[1] = 0;
                    tmp = tmp + num1.toString();
                }
                break;
            }

        }
        DeviceNoStr = tmp;
        return DeviceNoStr;
    }

    /**
     * 编号规则是否正确
     * @param stairNoLen 梯号长度
     * @param roomNoLen 房号长度
     * @param cellNolen 单元号长度
     * @return 0：正确
     */
    public int isCheckDeviceNo(int stairNoLen,int roomNoLen,int cellNolen) {
        if (cellNolen > 2) {
            return 1;
        }
        if (stairNoLen < cellNolen || stairNoLen > 9) {
            return 2;
        }
        if (roomNoLen < 3 || roomNoLen > 9) {
            return 3;
        }
        if ((stairNoLen + roomNoLen) > 17) {
            return 4;
        }
        return 0;
    }

    /**
     * 获取当前分段参数
     * @param stairNoLen 房号长度
     * @param roomNoLen 梯号长度
     * @param cellNolen 单元号长度
     * @return 分段参数
     */
    public int getCurrentSubsection(int stairNoLen,int roomNoLen,int cellNolen){
        return ((stairNoLen-cellNolen)*100)+(cellNolen*10)+roomNoLen;
    }


    public int DeviceNoisright(String no) {
        String sub = no.substring(no.length() - 2);
        if (sub.equals("-1")) {
            return StairNoLen + RoomNoLen + 1;
        }
        if (no.length() != (StairNoLen + RoomNoLen + 1)) {
            return StairNoLen + RoomNoLen + 1;
        } else {
            return 0;
        }
    }


    public String getSubRuleDesc() {
        return SubRuleDesc;
    }

    /**
     * @param subRuleDesc the subRuleDesc to set
     */
    public void setSubRuleDesc(String subRuleDesc) {
        SubRuleDesc = subRuleDesc;
    }

    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    public void writeToParcel(Parcel arg0, int arg1) {
        // TODO Auto-generated method stub
        arg0.writeInt(AreaNo);
        arg0.writeInt(DeviceNo1);
        arg0.writeInt(DeviceNo2);
        arg0.writeString(DeviceNo);
        arg0.writeByte(StairNoLen);
        arg0.writeByte(RoomNoLen);
        arg0.writeByte(CellNoLen);
        arg0.writeByte(UseCellNo);
        arg0.writeInt(StairNoLen);
        arg0.writeByte(DeviceType);
        arg0.writeByte(DevNoLen);
        arg0.writeByte(IsRight);
        arg0.writeString(DeviceNoStr);
        arg0.writeString(SubRuleDesc);
    }

    public static final Parcelable.Creator<FullDeviceNo> CREATOR = new Parcelable.Creator<FullDeviceNo>() {

        public FullDeviceNo createFromParcel(Parcel source) {
            // TODO Auto-generated method stub
            return new FullDeviceNo(source);
        }

        public FullDeviceNo[] newArray(int size) {
            // TODO Auto-generated method stub
            return new FullDeviceNo[size];
        }

    };

}
