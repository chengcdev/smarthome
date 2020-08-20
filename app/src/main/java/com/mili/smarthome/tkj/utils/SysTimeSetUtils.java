package com.mili.smarthome.tkj.utils;

import android.app.AlarmManager;
import android.content.Context;
import android.provider.Settings;
import android.text.format.DateFormat;

import com.mili.smarthome.tkj.appfunc.BuildConfigHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class SysTimeSetUtils {
    //毫秒转秒
    public static String long2String(long time) {

        //毫秒转秒
        int sec = (int) time / 1000;
        int min = sec / 60;    //分钟
        sec = sec % 60;        //秒
        if (min < 10) {    //分钟补0
            if (sec < 10) {    //秒补0
                return "0" + min + ":0" + sec;
            } else {
                if (sec >= 10) {
                    sec = 10;
                }
                return "0" + min + ":" + sec;
            }
        } else {
            if (sec < 10) {    //秒补0
                return min + ":0" + sec;
            } else {
                if (sec >= 10) {
                    sec = 10;
                }
                return min + ":" + sec;
            }
        }
    }


    /**
     * 返回当前时间的格式为 yyyyMMdd
     */
    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(System.currentTimeMillis());
    }


    /**
     * 获取当前时区
     *
     * @return
     */
    public static String getCurrentTimeZone() {
        TimeZone tz = TimeZone.getDefault();
        if (tz.getID().equals("Asia/Taipei")) {
            String strTz = "GMT+08:00" + "," + tz.getDisplayName(false, TimeZone.LONG);
            return strTz;
        } else {
            String strTz = tz.getDisplayName(false, TimeZone.SHORT) + "," + tz.getDisplayName(false, TimeZone.LONG);
            return strTz;
        }
    }


    /**
     * 获取当前系统语言格式
     *
     * @param mContext
     * @return
     */
    public static String getCurrentLanguage(Context mContext) {
        Locale locale = mContext.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        String country = locale.getCountry();
        String lc = language + "_" + country;
        return lc;
    }


    /**
     * 修改日期
     *
     * @param year
     * @param month
     * @param day
     */
    public static void setSysDate(Context context, int year, int month, int day) {

        if (BuildConfigHelper.isPad()) {
            return;
        }

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month - 1);
        c.set(Calendar.DAY_OF_MONTH, day);
        long when = c.getTimeInMillis();
        if (when / 1000 < Double.MAX_VALUE) {
            ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).setTime(when);
        }
    }

    /**
     * 修改时间
     *
     * @param context
     * @param hour    小时
     * @param minute  分
     */
    public static void setSysTime(Context context, int hour, int minute, int second) {

        if (BuildConfigHelper.isPad()) {
            return;
        }

        boolean is24Hour = DateFormat.is24HourFormat(context);
        //使用24小时制
        if (!is24Hour) {
            setHourType(context, "24");
        }

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long when = c.getTimeInMillis();
        if (when / 1000 < Integer.MAX_VALUE) {
            ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).setTime(when);
        }
    }

    /**
     * 修改时区
     *
     * @param timeZone
     */
    public static void setTimeZone(Context context, String timeZone) {

        if (BuildConfigHelper.isPad()) {
            return;
        }

        if (isTimeZoneAuto(context)) {
            setAutoTimeZone(context, 0);
        }

        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.setTimeZone(timeZone);//参数时区ID
    }

    /**
     * 是否自动获取时区
     *
     * @param context
     * @return
     */
    public static boolean isTimeZoneAuto(Context context) {
        try {
            return android.provider.Settings.Global.getInt(context.getContentResolver(),
                    android.provider.Settings.Global.AUTO_TIME_ZONE) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 设置系统的时区是否自动获取
     *
     * @param context
     * @return
     */
    public static void setAutoTimeZone(Context context, int checked) {
        android.provider.Settings.Global.putInt(context.getContentResolver(),
                android.provider.Settings.Global.AUTO_TIME_ZONE, checked);
    }

    /**
     * 获取当前时区
     *
     * @return
     */
    public static String getDefaultTimeZone() {
        return TimeZone.getDefault().getDisplayName();
    }

    /**
     * 12小时制
     */
    public static void setHourType(Context context, String type) {
        android.provider.Settings.System.putString(context.getContentResolver(),
                android.provider.Settings.System.TIME_12_24, type);
    }

    /**
     * 设置系统日期时间
     */
    public static void setSysTime(Context context, Date date) {

        if (BuildConfigHelper.isPad()) {
            return;
        }

        boolean is24Hour = DateFormat.is24HourFormat(context);
        //使用24小时制
        if (!is24Hour) {
            setHourFormat(context, true);
        }
        ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).setTime(date.getTime());
    }

    /**
     * 设置时间格式
     *
     * @param is24Hour true为24小时制，否则为12小时制
     */
    public static void setHourFormat(Context context, boolean is24Hour) {
        String format = is24Hour ? "24" : "12";
        Settings.System.putString(context.getContentResolver(), Settings.System.TIME_12_24, format);
    }

    /**
     * 返回当前的年
     */
    public static int getCurrentYear() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String format = sdf.format(System.currentTimeMillis());
        return Integer.parseInt(format);
    }

    /**
     * 返回当前的月
     */
    public static int getCurrentMonth() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM");
        String format = sdf.format(System.currentTimeMillis());
        return Integer.parseInt(format);
    }

    /**
     * 返回当前的日
     */
    public static int getCurrentDay() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        String format = sdf.format(System.currentTimeMillis());
        return Integer.parseInt(format);
    }

    /**
     * 返回当前的时
     */
    public static int getCurrentHour() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH");
        String format = sdf.format(System.currentTimeMillis());
        return Integer.parseInt(format);
    }

    /**
     * 返回当前分钟
     */
    public static int getCurrentMin() {
        SimpleDateFormat sdf = new SimpleDateFormat("mm");
        String format = sdf.format(System.currentTimeMillis());
        return Integer.parseInt(format);
    }

    /**
     * 返回当前秒
     */
    public static int getCurrentSecond() {
        SimpleDateFormat sdf = new SimpleDateFormat("ss");
        String format = sdf.format(System.currentTimeMillis());
        return Integer.parseInt(format);
    }

    /**
     * @return 返回当前时间
     */
    public static String getTime() {
        return "" + getCurrentYear() + getCurrentMonth() + getCurrentDay();
    }

    /**
     * @param date 日期
     * @return date小于补零
     */
    public static String getDate(int date) {
        if (date > 0 && date < 10) {
            return "0" + date;
        }
        return String.valueOf(date);
    }


    public static boolean caculateTimeDiff(long oldTime, int compareDays) {
        long currentTimeMillis = System.currentTimeMillis();
//        String time = SimpleDateFormat.getDateTimeInstance().format(new Date());
        int curDays = (int) (currentTimeMillis / (1000 * 60 * 60 * 24));
        int oldDays = (int) (oldTime / (1000 * 60 * 60 * 24));
//        LogUtils.w(" time: " + time + " curDays>>>>: " + curDays + " oldDays>>>>: " + oldDays);
        return curDays - oldDays > compareDays;
    }
}
