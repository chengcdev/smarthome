package com.mili.smarthome.tkj.utils;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.android.CommStorePathDef;
import com.android.CommSysDef;
import com.android.CommTypeDef;
import com.android.Common;
import com.android.MediaTypeDef;
import com.mili.smarthome.tkj.app.App;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.dao.DeviceNoDao;
import com.mili.smarthome.tkj.dao.DoorReminderDao;
import com.mili.smarthome.tkj.entities.DoorReminderModel;
import com.mili.smarthome.tkj.entities.deviceno.DeviceNoModel;

public class PlaySoundUtils {

    private static PlayHandler mPlayHandler = new PlayHandler();
    private static Thread mThread;

    /**
     * 播放assets下的声音文件
     * <p>
     * 不循环播放声音
     *
     * @param ringPath 声音路径 CommStorePathDef类
     */
    public static void playAssetsSound(final String ringPath) {
        MediaPlayerUtils.playAssetsSound(App.getInstance(), MediaTypeDef.Media_Type.MEDIA_AUDIO_PLAY, ringPath, false, isCloseSound(), null);
    }

    /**
     * 播放assets下的声音文件
     * <p>
     * 不循环播放声音
     *
     * @param ringPath 声音路径 CommStorePathDef类
     * @param roomNo   房号
     * @param param    备用参数
     */
    public static void playAssetsSound(final String ringPath, String roomNo, int param) {
        boolean playTTS = false;
        if (ringPath.equals(CommStorePathDef.VOICE_1501_PATH ) && roomNo != null && roomNo.length() > 0){
            DeviceNoDao deviceNoDao = new DeviceNoDao();
            if (deviceNoDao != null){
               DeviceNoModel deviceNoModel = deviceNoDao.queryDeviceNoModel();
                if (deviceNoModel.getDeviceType() == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR
                        && roomNo.length() == deviceNoModel.getRoomNoLen()){
                    String stairStr = deviceNoModel.getDeviceNo().substring(0, deviceNoModel.getStairNoLen());
                    if (stairStr != null){
                        roomNo =  stairStr + roomNo;
                    }
                }
            }
            LogUtils.e("roomNo...: "+roomNo);
            DoorReminderDao doorReminderDao = new DoorReminderDao();
            DoorReminderModel model = doorReminderDao.queryByFlagID(roomNo);
            if (model != null){
                int currTime = (int)(System.currentTimeMillis()/1000);
                int startTime = model.getStartTime();
                int endTime = model.getEndTime();
                LogUtils.d("currTime: "+currTime+" startTime: "+startTime+" endTime: "+endTime);
                if (model.getStartTime() >= startTime && currTime <= endTime){
                    playTTS = true;
                    Intent intent = new Intent(CommSysDef.TTSAPP_STARTSPEAK);
                    intent.putExtra(CommSysDef.TTS_READ_TEXT, model.getVoiceText());
                    intent.putExtra(CommSysDef.TTS_READ_TYPE, CommSysDef.TTS_WOMAN);
                    Common.SendBroadCast(App.getInstance().getApplicationContext(), intent);
                }
            }
        }

        if (!playTTS){
            MediaPlayerUtils.playAssetsSound(App.getInstance(), MediaTypeDef.Media_Type.MEDIA_AUDIO_PLAY, ringPath, false, isCloseSound(), null);
        }
    }

    /**
     * 播放assets下的声音文件
     * <p>
     * 循环播放声音
     *
     * @param ringPath 声音路径 CommStorePathDef类
     */
    public static void playAssetsSoundLoop(String ringPath) {
        MediaPlayerUtils.playAssetsSound(App.getInstance(), MediaTypeDef.Media_Type.MEDIA_AUDIO_PLAY, ringPath, true, isCloseSound(), null);
    }


    /**
     * 播放assets下的声音文件
     * <p>
     * 不循环播放声音
     *
     * @param ringPath 声音路径 CommStorePathDef类
     * @param Listener 声音结束回调 MediaPlayerUtils.OnMediaStatusCompletionListener
     */
    public static void playAssetsSound(final String ringPath, final MediaPlayerUtils.OnMediaStatusCompletionListener Listener) {
//        MediaPlayerUtils.playAssetsSound(App.getInstance(), MediaTypeDef.Media_Type.MEDIA_AUDIO_PLAY, ringPath, false, isCloseSound(), Listener);
        if (mThread != null) {
            mThread.interrupt();
            mThread = null;
        }
        MediaPlayerUtils.setmHandler(new Handler());
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                MediaPlayerUtils.playAssetsSound(App.getInstance(), MediaTypeDef.Media_Type.MEDIA_AUDIO_PLAY, ringPath, false, isCloseSound(), new MediaPlayerUtils.OnMediaStatusCompletionListener() {
                    @Override
                    public void onMediaStatusCompletion(boolean flag) {
                        if (flag) {
                            mPlayHandler.setParam(flag, Listener);
                            mPlayHandler.sendEmptyMessage(1);
                        }
                    }
                });
            }
        });
        mThread.start();
    }


    /**
     * 播放assets下的声音文件
     * <p>
     * 不循环播放声音
     *
     * @param ringPath 声音路径 CommStorePathDef类
     * @param roomNo 房号
     * @param ttsCallbk 状态反馈
     * @param Listener 声音结束回调 MediaPlayerUtils.OnMediaStatusCompletionListener
     */
    public static void playAssetsSound(final String ringPath, final String roomNo, final boolean ttsCallbk, final MediaPlayerUtils.OnMediaStatusCompletionListener Listener) {

        if (mThread != null) {
            mThread.interrupt();
            mThread = null;
        }
        MediaPlayerUtils.setmHandler(new Handler());
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String roomNum = roomNo;
                boolean playTTS = false;
                if (ringPath.equals(CommStorePathDef.VOICE_1501_PATH) && roomNum != null && roomNum.length() > 0) {
                    DeviceNoDao deviceNoDao = new DeviceNoDao();
                    if (deviceNoDao != null) {
                        DeviceNoModel deviceNoModel = deviceNoDao.queryDeviceNoModel();
                        if (deviceNoModel.getDeviceType() == CommTypeDef.DeviceType.DEVICE_TYPE_STAIR
                                && roomNum.length() == deviceNoModel.getRoomNoLen()) {
                            String stairStr = deviceNoModel.getDeviceNo().substring(0, deviceNoModel.getStairNoLen());
                            if (stairStr != null) {
                                roomNum = stairStr + roomNum;
                            }
                        }
                    }
                    LogUtils.e("roomNum...: " + roomNum);
                    DoorReminderDao doorReminderDao = new DoorReminderDao();
                    DoorReminderModel model = doorReminderDao.queryByFlagID(roomNum);
                    if (model != null) {
                        int currTime = (int) (System.currentTimeMillis() / 1000);
                        int startTime = model.getStartTime();
                        int endTime = model.getEndTime();
                        LogUtils.d("currTime: " + currTime + " startTime: " + startTime + " endTime: " + endTime);
                        if (model.getStartTime() >= startTime && currTime <= endTime) {
                            playTTS = true;
                            Intent intent = new Intent(CommSysDef.TTSAPP_STARTSPEAK);
                            intent.putExtra(CommSysDef.TTS_READ_TEXT, model.getVoiceText());
                            intent.putExtra(CommSysDef.TTS_READ_TYPE, CommSysDef.TTS_WOMAN);
                            Common.SendBroadCast(App.getInstance().getApplicationContext(), intent);
                            if (ttsCallbk) {
                                try {
                                    mThread.sleep(2000);
                                    mPlayHandler.setParam(true, Listener);
                                    mPlayHandler.sendEmptyMessage(1);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }

                if (!playTTS) {
                    MediaPlayerUtils.playAssetsSound(App.getInstance(), MediaTypeDef.Media_Type.MEDIA_AUDIO_PLAY, ringPath, false, isCloseSound(), new MediaPlayerUtils.OnMediaStatusCompletionListener() {
                        @Override
                        public void onMediaStatusCompletion(boolean flag) {
                            if (flag) {
                                mPlayHandler.setParam(flag, Listener);
                                mPlayHandler.sendEmptyMessage(1);
                            }
                        }
                    });
                }
            }
            });
        mThread.start();
    }

    /**
     * 播放assets下的声音文件
     * <p>
     * 循环播放声音
     *
     * @param ringPath 声音路径 CommStorePathDef类
     * @param Listener 声音结束回调 MediaPlayerUtils.OnMediaStatusCompletionListener
     */
    public static void playAssetsSoundLoop(String ringPath, MediaPlayerUtils.OnMediaStatusCompletionListener Listener) {
        MediaPlayerUtils.playAssetsSound(App.getInstance(), MediaTypeDef.Media_Type.MEDIA_AUDIO_PLAY, ringPath, true, isCloseSound(), Listener);
    }

    /**
     * 播放报警声
     */
    public static void playAlarmSound() {
        MediaPlayerUtils.playAssetsSound(App.getInstance(), MediaTypeDef.Media_Type.MEDIA_AUDIO_PLAY, CommStorePathDef.ALARM_TIPS_PATH, 4, false, null);
    }

    /**
     * 播放报警声
     *
     * @param Listener 声音结束回调 MediaPlayerUtils.OnMediaStatusCompletionListener
     */
    public static void playAlarmSound(MediaPlayerUtils.OnMediaStatusCompletionListener Listener) {
        MediaPlayerUtils.playAssetsSound(App.getInstance(), MediaTypeDef.Media_Type.MEDIA_AUDIO_PLAY, CommStorePathDef.ALARM_TIPS_PATH, 4, false, Listener);
    }

    /**
     * 停止播放assets下的声音文件
     */
    public static void stopPlayAssetsSound() {
        MediaPlayerUtils.stopPlay(MediaTypeDef.Media_Type.MEDIA_AUDIO_PLAY);
    }

    /**
     * 当前播放的声音是否循环播放
     */
    public static boolean isLoopSound() {
        return MediaPlayerUtils.isLoopRing;
    }


    private static boolean isCloseSound() {
        boolean isCloseSound;
        int mediaVoume = AppConfig.getInstance().getTipVolume();
        if (mediaVoume == 0) {
            isCloseSound = true;
        } else {
            isCloseSound = false;
        }
        return isCloseSound;
    }


    public static class PlayHandler extends Handler {

        private MediaPlayerUtils.OnMediaStatusCompletionListener listener;
        private boolean flag;

        public void setParam(boolean flag, final MediaPlayerUtils.OnMediaStatusCompletionListener Listener) {
            this.listener = Listener;
            this.flag = flag;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    break;
                case 1:
                    if (listener != null) {
                        listener.onMediaStatusCompletion(flag);
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    }
}
