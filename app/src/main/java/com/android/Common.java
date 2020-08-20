package com.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Parcel;
import android.os.UserHandle;
import android.util.Log;

import com.mili.smarthome.tkj.BuildConfig;
import com.mili.smarthome.tkj.face.FaceProtocolInfo;
import com.mili.smarthome.tkj.utils.LogUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.android.CommTypeDef.JudgeStatus.FAIL_STATE;
import static com.android.CommTypeDef.JudgeStatus.SUCCESS_STATE;
import static com.android.CommTypeDef.JudgeStatus.VAILD_FAIL_STATE;
import static com.android.CommTypeDef.LifecycleMode.VALID_LIFECYCLE_MODE;
import static com.android.CommTypeDef.LifecycleMode.VALID_NULL_MODE;
import static com.android.CommTypeDef.LifecycleMode.VALID_TIME_MODE;

public class Common {

    public static short bytes2short(byte[] data, int start) {
        short ret;
        if (data.length - start >= 2) {
            ret = (short) (((data[start + 1] & 0xff) << 8) | (data[start] & 0xff));
            return ret;
        } else
            return 0;
    }

    public static short bytes2short_2(byte[] data, int start) {
        short ret;
        if (data.length - start >= 2) {
            ret = (short) (((data[start] & 0xff) << 8) | (data[start + 1] & 0xff));
            return ret;
        } else
            return 0;
    }

    public static byte[] short2bytes(short s) {
        byte[] b = new byte[4];
        for (int i = 0; i < 2; i++) {
            b[i] = (byte) (s >>> (8 - i * 8));
        }
        return b;
    }

    public static int bytes2int(byte[] data, int start) {
        int ret;
        if (data.length - start >= 4) {
            ret = (int) (((data[start + 3] & 0xff) << 24)
                    | ((data[start + 2] & 0xff) << 16)
                    | ((data[start + 1] & 0xff) << 8) | (data[start] & 0xff));
            return ret;
        } else
            return 0;
    }

    public static byte[] int2bytes(int num) {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (num >>> (24 - i * 8));
        }
        return b;
    }

    public static String int2IPAddr(int num) {
        byte[] b = int2bytes(num);
        int[] iAry = new int[4];
        for (int i = 0; i < 4; i++) {
            iAry[i] = b[i] & 0xff;
        }
        String IPAddr = String.format("%d.%d.%d.%d", iAry[0], iAry[1], iAry[2],
                iAry[3]);
        return IPAddr;
    }

    public static int IPAddr2int(String IPAddr) {
        byte[] ip = new byte[4];
        int position1 = IPAddr.indexOf(".");
        int position2 = IPAddr.indexOf(".", position1 + 1);
        int position3 = IPAddr.indexOf(".", position2 + 1);
        ip[0] = Byte.parseByte(IPAddr.substring(0, position1));
        ip[1] = Byte.parseByte(IPAddr.substring(position1 + 1, position2));
        ip[2] = Byte.parseByte(IPAddr.substring(position2 + 1, position3));
        ip[3] = Byte.parseByte(IPAddr.substring(position3 + 1));
        return bytes2int(ip, 0);
    }

    public static int ipToint(String strIp) {
        int[] ip = new int[4];

        int position1 = strIp.indexOf(".");
        int position2 = strIp.indexOf(".", position1 + 1);
        int position3 = strIp.indexOf(".", position2 + 1);

        ip[0] = Integer.parseInt(strIp.substring(0, position1));
        ip[1] = Integer.parseInt(strIp.substring(position1 + 1, position2));
        ip[2] = Integer.parseInt(strIp.substring(position2 + 1, position3));
        ip[3] = Integer.parseInt(strIp.substring(position3 + 1));
        return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
    }

    public static String intToIP(int intIp) {
        StringBuffer sb = new StringBuffer("");
//		 sb.append(String.valueOf((intIp & 0x000000FF)));
//		 sb.append(".");
//		 sb.append(String.valueOf((intIp & 0x0000FFFF) >>> 8));
//		 sb.append(".");
//		 sb.append(String.valueOf((intIp & 0x00FFFFFF) >>> 16));
//		 sb.append(".");
//		 sb.append(String.valueOf((intIp >>> 24)));

        sb.append(String.valueOf((intIp >>> 24)));
        sb.append(".");
        sb.append(String.valueOf((intIp & 0x00FFFFFF) >>> 16));
        sb.append(".");
        sb.append(String.valueOf((intIp & 0x0000FFFF) >>> 8));
        sb.append(".");
        sb.append(String.valueOf((intIp & 0x000000FF)));

        return sb.toString();
    }

    public static int bitCount(int var0) {
        var0 -= var0 >>> 1 & 1431655765;
        var0 = (var0 & 858993459) + (var0 >>> 2 & 858993459);
        var0 = var0 + (var0 >>> 4) & 252645135;
        var0 += var0 >>> 8;
        var0 += var0 >>> 16;
        return var0 & 63;
    }


    public static String byteToString(byte[] data) {
        int index = data.length;
        for (int i = 0; i < data.length; i++) {
            if (data[i] == 0) {
                index = i;
                break;
            }
        }
        byte[] temp = new byte[index];
        Arrays.fill(temp, (byte) 0);
        System.arraycopy(data, 0, temp, 0, index);
        String str;
        try {
            str = new String(temp, "GBK");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
        return str;
    }

    public static String byteToStringUTF8(byte[] data) {
        int index = data.length;
        for (int i = 0; i < data.length; i++) {
            if (data[i] == 0) {
                index = i;
                break;
            }
        }
        byte[] temp = new byte[index];
        Arrays.fill(temp, (byte) 0);
        System.arraycopy(data, 0, temp, 0, index);
        String str;
        try {
            str = new String(temp, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
        return str;
    }

    public static String byteToString(byte[] b, int form) {
        String str = "";
        int i = 0;
        for (i = 0; i < b.length - form; i++) {
            if (b[i + form] == '\0')
                break;
        }
        if (i != 0) {
            byte[] bb = new byte[i];
            for (int j = 0; j < i; j++) {
                bb[j] = b[form + j];
            }
            try {
                str = new String(bb, "GB2312");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return str;
    }

    public static String getNewString(String str) {
        byte[] data = str.getBytes();
        int index = data.length;
        for (int i = 0; i < data.length; i++) {
            if (data[i] < 48 || data[i] > 57) {
                index = i;
                break;
            }
        }
        byte[] temp = new byte[index];
        System.arraycopy(data, 0, temp, 0, index);
        String res;
        try {
            res = new String(temp, "GBK");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
        return res;
    }

    public static long getfilenumber(String path) {
        long size = 0;
        File file = new File(path);
        size = getfile(file);
        return size;
    }

    public static long getfile(File f) {

        long size = 0;
        File flist[] = f.listFiles();
        if (flist != null) {
            size = flist.length;
            for (int i = 0; i < flist.length; i++) {
                if (flist[i].isDirectory()) {
                    size = size + getfile(flist[i]);
                    size--;
                }
            }
        } else {
            size = -1;
        }
        return size;
    }

    public static String getlastfile(String Path) {
        File file = new File(Path);
        if (!file.exists())
            file.mkdir();
        File flist[] = file.listFiles();
        long[] arr = new long[flist.length];
        long max = 0;
        int i = 0;
        String name;
        String path;
        if (flist.length == 0) {
            return null;
        }
        for (i = 0; i < flist.length; i++) {
            name = flist[i].getName();
            StringTokenizer st = new StringTokenizer(name, ".");
            arr[i] = Long.valueOf(st.nextToken());
        }
        max = getMax(arr);
        path = Path + Long.toString(max) + ".jpg";
        return path;
    }

    public static String getfristfile(String Path) {
        File file = new File(Path);
        if (!file.exists())
            file.mkdir();
        File flist[] = file.listFiles();
        long[] arr = new long[flist.length];
        long min = 0;
        int i = 0;
        String name;
        String path;
        for (i = 0; i < flist.length; i++) {
            name = flist[i].getName();
            StringTokenizer st = new StringTokenizer(name, ".");
            arr[i] = Long.valueOf(st.nextToken());
        }
        min = getMin(arr);
        path = Path + Long.toString(min) + ".jpg";
        return path;

    }

    public static String getlastfile(String filepath_1, String filepath_2) {
        String lastpath = null;
        long long_1, long_2;
        if (filepath_1 == null && filepath_2 == null) {
            return null;
        } else if (filepath_1 == null && filepath_2 != null) {
            return filepath_2;
        } else if (filepath_1 != null && filepath_2 == null) {
            return filepath_1;
        } else {
            String[] strings_1 = filepath_1.split("/");
            StringTokenizer st_1 = new StringTokenizer(strings_1[strings_1.length - 1], ".");
            long_1 = Long.valueOf(st_1.nextToken());
            String[] strings_2 = filepath_2.split("/");
            StringTokenizer st_2 = new StringTokenizer(strings_2[strings_2.length - 1], ".");
            long_2 = Long.valueOf(st_2.nextToken());
            if (long_1 > long_2) {
                lastpath = filepath_1;
            } else if (long_1 == long_2) {
                lastpath = filepath_1;
            } else {
                lastpath = filepath_2;
            }
            return lastpath;
        }
    }

    public static long getMax(long[] arr) {
        long max = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
            }
        }
        return max;
    }

    public static double getMax(double[] arr) {
        double max = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
            }
        }
        return max;
    }

    public static int getMax(int[] arr) {
        int max = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
            }
        }
        return max;
    }

    public static long getMin(long[] arr) {
        long min = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] < min) {
                min = arr[i];
            }
        }
        return min;
    }

    public static void delete_file(String path) {
        File file = new File(path);
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            }
        }
    }

    public static void RecursionDeleteFile(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                RecursionDeleteFile(f);
            }
            file.delete();
        }
    }


    public static void isExist(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static boolean moveFileToDir(String srcFileName, String destDirName) {

        File srcFile = new File(srcFileName);
        if (!srcFile.exists() || !srcFile.isFile())
            return false;

        File destDir = new File(destDirName);
        if (!destDir.exists())
            destDir.mkdirs();
        Log.d("moveFile", "srcFileName is " + srcFileName + "::destDirName is "
                + destDirName + "::destfile is " + destDirName + srcFile.getName());
        boolean ret = srcFile.renameTo(new File(destDirName + srcFile.getName()));
        Log.d("moveFile", "renameTo is " + ret);
        return ret;
    }

    public static boolean moveFileToFile(String srcFileName, String destFileName) {

        if (srcFileName == null || destFileName == null) {
            Log.d("moveFile", "srcFileName or destFileName is null");
            return false;
        }
        File srcFile = new File(srcFileName);
        if (!srcFile.exists() || !srcFile.isFile())
            return false;

        boolean ret = srcFile.renameTo(new File(destFileName));
        Log.d("moveFile", "renameTo is " + ret);
        return ret;
    }

    public static boolean copyFile(String oldPath, String newPath) {
        try {
            File oldfile = new File(oldPath);
            if (oldfile.exists()) {
                InputStream inStream = new FileInputStream(oldPath);
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[4096];
                int length;
                while ((length = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, length);
                }
                Log.d("common.copyFile ", "copy finish!!");
                inStream.close();
                fs.close();
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("common.copyFile ", "ret is " + false);
            return false;

        }
        return true;
    }
    public static boolean hasExternalSdCard()
    {
        boolean sResult = false;
        String sdpath = CommStorePathDef.EXTERNAL_SD_PATH;
        File filepath = new File(sdpath);
        Log.d("hasSdCard", "filepath is "+filepath.exists());
        if (filepath.exists())
        {
            String pathstring = CommStorePathDef.EX_MULTIMEDIA_DIR_PATH;
            Log.d("hasSdCard", "pathstring is " + pathstring);
            File file = new File(pathstring);
            Log.d("hasSdCard", "file is "+file.exists());
            if (file.exists())
            {
                sResult = true;
            }
            else
            {
                sResult = file.mkdirs();
                Log.d("hasSdCard", "file.mkdirs is "+sResult);
            }
        }
        else
        {
            sResult = false;
        }
        return sResult;
    }

    public static String[] StrCom(String str[]) {
        String temp;
        int i = 0, j = 0, k = 0;
        int len = str.length;
        for (i = 0; i < len - 1; i++) {
            k = i;
            for (j = i + 1; j < len; j++) {
                Log.d("StrCom=====111111", str[i] + str[j]);
                if (str[i].compareTo(str[j]) > 0) {
                    k = j;
                }
                if (i != k) {
                    temp = str[i];
                    str[i] = str[k];
                    str[k] = temp;
                    k = i;
                    //Log.d("StrCom=====", str[i]+str[j]);
                }
            }
        }
        return str;
    }

    public static int computeSampleSize(BitmapFactory.Options options,
                                        int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
                Math.floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    public static String formatIpaddr(String strIp) {
        String newIPStr = null;
        int[] ip = new int[4];
        int position1 = strIp.indexOf(".");
        int position2 = strIp.indexOf(".", position1 + 1);
        int position3 = strIp.indexOf(".", position2 + 1);
        ip[0] = Integer.parseInt(strIp.substring(0, position1));
        ip[1] = Integer.parseInt(strIp.substring(position1 + 1, position2));
        ip[2] = Integer.parseInt(strIp.substring(position2 + 1, position3));
        ip[3] = Integer.parseInt(strIp.substring(position3 + 1));
        newIPStr = String.valueOf(ip[0]) + "." + String.valueOf(ip[1]) +
                "." + String.valueOf(ip[2]) + "." + String.valueOf(ip[3]);
        return newIPStr;
    }

    @SuppressLint("MissingPermission")
    public static void SendBroadCast(Context context, Intent intent) {
        Parcel in = Parcel.obtain();
        in.writeInt(-1);
        UserHandle userHandle = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            userHandle = new UserHandle(in);
            context.sendBroadcastAsUser(intent, userHandle);
        }

    }

    public static boolean isNavigationbarShow = false;

    public static void HideNavigationbar(Context context) {
        if (isNavigationbarShow == true) {
            SendBroadCast(context, new Intent("com.android.action.hide_navigationbar"));
            isNavigationbarShow = false;
        }
    }

    public static void DisplayNavigationbar(Context context) {
        if (isNavigationbarShow == false) {
            SendBroadCast(context, new Intent("com.android.action.display_navigationbar"));
            isNavigationbarShow = true;
        }
    }

    public static void DoScreenShot(Context context) {
        SendBroadCast(context, new Intent("com.android.action.do_screenshot"));
    }


    public static String gettimenow() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss     ");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        return str;
    }

    public static boolean isfriststart() {
        File file = new File("/mnt/sdcard/isfrist.dat");
        if (file.exists()) {
            return false;
        } else {
            return true;
        }
    }

    public static void setisfriststart() throws IOException {
        File file = new File("/mnt/sdcard/isfrist.dat");
//			String string = SystemInfoHelper.getSoftWareVer()+"/"+ SystemInfoHelper.getHardWareVer();
        String string = "V 01.01.0001s/TKK714-100101";
        if (!file.exists()) {
            FileOutputStream fos = new FileOutputStream(file);
            byte[] bytes = string.getBytes();

            fos.write(bytes);
            fos.close();
        }
    }

    /**
     * 密码、人脸时间、次数判断
     * @param lifecycle  有效性状态
     */
    public static int judge_validity(int lifecycle, int startTime, int endTime){
        int result = SUCCESS_STATE;
        LogUtils.d("Face lifecycle: "+lifecycle);
        switch (lifecycle){
            case VALID_LIFECYCLE_MODE:
                result = VAILD_FAIL_STATE;
                break;

            case VALID_TIME_MODE: {
                int currTime = (int)(System.currentTimeMillis()/1000);
                LogUtils.d("currTime: "+currTime+" startTime: "+startTime+" endTime: "+endTime);
                if (currTime >= startTime && currTime <= endTime){
                    result = SUCCESS_STATE;
                }
                else {
                    result = VAILD_FAIL_STATE;
                }
            }
                break;

            case VALID_NULL_MODE:
                result = SUCCESS_STATE;
                break;
        }
        LogUtils.d("result: "+result);
        return result;
    }

    public static int validity(FaceProtocolInfo faceModel) {
        int result = SUCCESS_STATE;
        if (faceModel != null) {
            if (BuildConfig.isEnabledFaceValid) {
                if (faceModel.getKeyID() == null && faceModel.getLifecycle() == 0) {
                    faceModel.setLifecycle(-2);
                }
                result = Common.judge_validity(faceModel.getLifecycle(), faceModel.getStartTime(), faceModel.getEndTime());
            }
        }
        else{
            result = FAIL_STATE;
        }
        return result;
    }

    public static String GetSystemTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        return str;
    }


    public static String ReadTxtFile(String strFilePath) {
        String path = strFilePath;
        String content = "";
        File file = new File(path);
        if (file.isDirectory()) {
            Log.d("TestFile", "The File doesn't not exist.");
        } else {
            try {
                InputStream instream = new FileInputStream(file);
                if (instream != null) {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    while ((line = buffreader.readLine()) != null) {
                        content += line;
                    }
                    instream.close();
                }
            } catch (java.io.FileNotFoundException e) {
                Log.d("TestFile", "The File doesn't not exist.");
            } catch (IOException e) {
                Log.d("TestFile", e.getMessage());
            }
        }
        return content;
    }

    public static String addZeroForNum(String str, int strLength) {
        int strLen = str.length();
        StringBuffer sb = null;
        while (strLen < strLength) {
            sb = new StringBuffer();
            sb.append("0").append(str);
            // sb.append(str).append("0");
            str = sb.toString();
            strLen = str.length();
        }
        return str;
    }

    public static <T> List<T> compare(T[] t1, T[] t2) {
        List<T> list1 = Arrays.asList(t1);
        List<T> list2 = new ArrayList<T>();
        for (T t : t2) {
            if (!list1.contains(t)) {
                list2.add(t);
            }
        }
        return list2;
    }


    public static void setLcdFlag() throws IOException {
        File file = new File("/mnt/sdcard/isNightReboot.dat");
        String string = "1";
        if (!file.exists()) {
            FileOutputStream fos = new FileOutputStream(file);
            byte[] bytes = string.getBytes();
            fos.write(bytes);
            fos.flush();
            fos.getFD().sync();
            fos.close();
        }
        Log.d("Common", "/mnt/sdcard/isNightReboot.dat = " + file.exists());
    }

    public static int getLcdFlag() throws IOException {
        File file = new File("/mnt/sdcard/isNightReboot.dat");
        if (file.exists()) {
            file.delete();
            return 1;
        } else {
            return 0;
        }
    }

    public static boolean isIP(String addr) {
        if (addr.length() < 7 || addr.length() > 15 || "".equals(addr)) {
            return false;
        }
        /**
         * 判断IP格式和范围
         */
        String rexp = "([0-9]|[0-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";

        Pattern pat = Pattern.compile(rexp);

        Matcher mat = pat.matcher(addr);

        boolean ipAddress = mat.find();

        //============对之前的ip判断的bug在进行判断
        if (ipAddress) {
            String ips[] = addr.split("\\.");

            if (ips.length == 4) {
                try {
                    for (String ip : ips) {
                        if (Integer.parseInt(ip) < 0 || Integer.parseInt(ip) > 255) {
                            return false;
                        }

                    }
                } catch (Exception e) {
                    return false;
                }

                return true;
            } else {
                return false;
            }
        }

        return ipAddress;
    }

    private byte[] StringToMac(String MacStr) {
        byte[] mac1 = null;
        byte[] Mac = new byte[6];

        for (int i = 0; i < 6; i++) {
            Mac[i] = 0;
        }

        if (MacStr != null) {
            if (MacStr.length() != 17) {
                return Mac;
            } else {
                mac1 = MacStr.getBytes();

                for (int i = 0; i < 6; i++) {
                    if (mac1[3 * i] >= 'A') {
                        mac1[3 * i] = (byte) (0x0a + mac1[3 * i] - 'A');
                    } else {
                        mac1[3 * i] = (byte) (mac1[3 * i] - '0');
                    }

                    if (mac1[3 * i + 1] >= 'A') {
                        mac1[3 * i + 1] = (byte) (0x0a + mac1[3 * i + 1] - 'A');
                    } else {
                        mac1[3 * i + 1] = (byte) (mac1[3 * i + 1] - '0');
                    }
                }
            }

        } else {
            return Mac;
        }

        Mac[0] = (byte) ((mac1[0]) * 0x10 + (mac1[1]));
        Mac[1] = (byte) ((mac1[3]) * 0x10 + (mac1[4]));
        Mac[2] = (byte) ((mac1[6]) * 0x10 + (mac1[7]));
        Mac[3] = (byte) ((mac1[9]) * 0x10 + (mac1[10]));
        Mac[4] = (byte) ((mac1[12]) * 0x10 + (mac1[13]));
        Mac[5] = (byte) ((mac1[15]) * 0x10 + (mac1[16]));

        Log.d(MacStr, "MAC " + Mac[0] + "  " + Mac[1] + "  " + Mac[2] + "  " + Mac[3] + "  " + Mac[4] + "  " + Mac[5]);
        return Mac;
    }

  public static boolean checkDate(String str) {
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");//括号内为日期格式，y代表年份，M代表年份中的月份（为避免与小时中的分钟数m冲突，此处用M），d代表月份中的天数
        try {
            sd.setLenient(false);//此处指定日期/时间解析是否不严格，在true是不严格，false时为严格
            sd.parse(str);//从给定字符串的开始解析文本，以生成一个日期
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean checTime(String str) {
        SimpleDateFormat sd = new SimpleDateFormat("HH:mm:ss");//括号内为日期格式，y代表年份，M代表年份中的月份（为避免与小时中的分钟数m冲突，此处用M），d代表月份中的天数
        try {
            sd.setLenient(false);//此处指定日期/时间解析是否不严格，在true是不严格，false时为严格
            sd.parse(str);//从给定字符串的开始解析文本，以生成一个日期
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static void WriteYuvData(byte[] data, int num){
        String path = "/mnt/sdcard/DCIM/video/"+num+".yuv";
        File file = new File(path);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data, 0, data.length);
            fos.flush();
            fos.close();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        Log.d("WriteYuvData", "write success!!!");
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        String tmp = null;
        for (byte b : bytes) {
            tmp = Integer.toHexString(0xFF & b);
            if (tmp.length() == 1) {
                tmp = "0" + tmp;
            }
            sb.append(tmp);
        }
        return sb.toString();

    }

    /**
     * I420(YU12)转NV21
     */
    public static byte[] I420ToNV21(byte[] dataI420, int width, int height) {
        byte[] dataNV21 = new byte[width*height*3/2];
        int ySize = width * height;
        int uSize = ySize/4;
        System.arraycopy(dataI420, 0, dataNV21, 0, ySize);

        for(int i = 0; i < uSize; ++i) {
            dataNV21[ySize + i * 2] = dataI420[ySize+uSize+i];
            dataNV21[ySize + i * 2 + 1] = dataI420[ySize+i];
        }
        return  dataNV21;
    }

    public static boolean WriteNV21ToJpg(byte[] data, int width, int height, String filename) {
        YuvImage image = new YuvImage(data, ImageFormat.NV21, width, height, null);
        File file = new File(filename);
        if (file.exists()) {
            file.delete();
        }
        LogUtils.d("[WriteNv21ToJpg] datalen is " + data.length + ", width is " + width + ",height is " + height);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            image.compressToJpeg(new Rect(0, 0, image.getWidth(), image.getHeight()), 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        String photoName = file.getPath();
        LogUtils.d("[WriteNv21ToJpg] photoName is " + photoName);
        return true;
    }

    /**
     *通过FirstName获取注册类型
     */
    public static int getFaceFirstNameType(String firstname){
        if (firstname != null){
            if (firstname.startsWith("dev-")){
                return CommTypeDef.FaceAddType.FACETYPE_DEV;
            }
            else if (firstname.startsWith("pc-")){
                return CommTypeDef.FaceAddType.FACETYPE_PC;
            }
        }
        return CommTypeDef.FaceAddType.FACETYPE_NONE;
    }

    /**
     *通过FirstName获取房号或卡号
     */
    public static String getFaceFirstNameStr(String firstname){
        if (firstname != null){
            String[] faceArry = firstname.split("-");
            if (faceArry[1] != null && faceArry[1].length() > 1){
                return faceArry[1];
            }
        }
        return null;
    }
}
