package com.mili.smarthome.tkj.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceView;

import com.android.MediaTypeDef;
import com.mili.smarthome.tkj.app.App;

import java.io.File;
import java.io.IOException;


public class MediaPlayerUtils {

    private static final String TAG = "MediaPlayerUtils";

    public static MediaPlayer mMediaPlayer;

    private static int mediaType = MediaTypeDef.Media_Type.MEDIA_NONE;
    private static int currentVolume = 0; //当前音量值
    private static AudioManager mAudioManager;
    private static Handler mHandler = new Handler();
    private static OnMediaStatusCompletionListener onMediaStatusCompletionListener;
    private static CountRun countRun;
    public static boolean isLoopRing;

    public interface OnMediaStatusCompletionListener {

        /**
         * 播放结束回调
         * @param flag  true：正常播放结束返回
         *              false：中断播放返回
         */
        void onMediaStatusCompletion(boolean flag);
    }

    /**
     * 播放assets路径下的声音
     * @param context
     * @param Type          媒体类型 Media_Type
     * @param ringPath      声音路径，在assets下的路径
     * @param isLoop        是否循环播放
     * @param isCloseSound  是否关闭提示音
     */
    public static boolean playAssetsSound(Context context, int Type, String ringPath, boolean isLoop,boolean isCloseSound,
                                       OnMediaStatusCompletionListener Listener ) {

        //是否关闭媒体音
        if (isCloseSound) {
            onMediaStatusCompletionListener = Listener;
            if (countRun == null) {
                countRun = new CountRun();
            }
            countRun.setCountRun(0);
            mHandler.post(countRun);
            return true;
        }


        Log.i(TAG, "1playAssetsSound Type = " + Type + " mediaType = " + mediaType);

        // 添加锁保护，防止一个线程在初始化MediaPlayer时，另一个线程调用stop
        synchronized(App.getInstance()) {
            Log.i(TAG, "1playAssetsSound start " );
            if (ringPath == null) {
                return false;
            }

            if (mMediaPlayer != null && mMediaPlayer.isPlaying() && mediaType == MediaTypeDef.Media_Type.MEDIA_ALARM) {
                return false;
            }

            if (Type != MediaTypeDef.Media_Type.MEDIA_ALARM) {
                if (mediaType == MediaTypeDef.Media_Type.MEDIA_MONITOR_TALK ||
                        mediaType == MediaTypeDef.Media_Type.MEDIA_CALL_OUT_TALK || mediaType == MediaTypeDef.Media_Type.MEDIA_CALL_IN_TALK) {
                    return false;
                }

                if (mediaType == MediaTypeDef.Media_Type.MEDIA_CALL_OUT || mediaType == MediaTypeDef.Media_Type.MEDIA_CALL_IN) {
//            if (Type == MediaTypeDef.Media_Type.MEDIA_RING) {
                    Type = mediaType;
//            }
                }
            }

            //释放资源
            clearMdeiaPlayer();

            try {
                AssetFileDescriptor afd = context.getAssets().openFd(ringPath);

                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                mMediaPlayer.prepare();
                mMediaPlayer.start();
                mMediaPlayer.setLooping(isLoop);
                isLoopRing = isLoop;
                mediaType = Type;
                onMediaStatusCompletionListener = Listener;
            } catch (Exception e) {
                e.printStackTrace();
            }

            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if (!mediaPlayer.isLooping()) {
                        if (onMediaStatusCompletionListener != null) {
                            onMediaStatusCompletionListener.onMediaStatusCompletion(true);
                            onMediaStatusCompletionListener = null;
                        }

                        if (mediaType != MediaTypeDef.Media_Type.MEDIA_CALL_IN && mediaType != MediaTypeDef.Media_Type.MEDIA_CALL_OUT) {
                            mediaType = MediaTypeDef.Media_Type.MEDIA_NONE;
                        }
                    }
                }
            });

        }

        return true;
    }

//    private static boolean enableVolumeTip() {
//        if (setVolumeModelHelper == null) {
//            setVolumeModelHelper = new SetVolumeModelHelper();
//        }
//        int volumeTip = setVolumeModelHelper.getVolumeTip();
//        if (volumeTip == 0) {
//            return true;
//        }
//        return false;
//    }

    /**
     * 播放assets路径下的声音
     * @param context
     * @param Type          媒体类型 Media_Type
     * @param ringPath      声音路径，在assets下的路径
     * @param Volume        音量大小， Volume=0不设置
     * @param isLoop        是否循环播放
     * @param Listener      播放结束回调
     */
    public static boolean playAssetsSound(Context context, int Type, String ringPath, int Volume, boolean isLoop,
                                       OnMediaStatusCompletionListener Listener) {
        Log.i(TAG, "2playAssetsSound Type = " + Type + " mediaType = " + mediaType);

//        if (enableVolumeTip())
//            return true;

        // 添加锁保护，防止一个线程在初始化MediaPlayer时，另一个线程调用stop
        synchronized(App.getInstance()) {

            Log.i(TAG, "2playAssetsSound start " );

            if (ringPath == null) {
                return false;
            }

            if (mMediaPlayer != null && mMediaPlayer.isPlaying() && mediaType == MediaTypeDef.Media_Type.MEDIA_ALARM) {
                return false;
            }

            if (Type != MediaTypeDef.Media_Type.MEDIA_ALARM) {
                if (mediaType == MediaTypeDef.Media_Type.MEDIA_MONITOR_TALK ||
                        mediaType == MediaTypeDef.Media_Type.MEDIA_CALL_OUT_TALK || mediaType == MediaTypeDef.Media_Type.MEDIA_CALL_IN_TALK) {
                    return false;
                }

                if (mediaType == MediaTypeDef.Media_Type.MEDIA_CALL_IN || mediaType == MediaTypeDef.Media_Type.MEDIA_CALL_OUT ||
                        mediaType == MediaTypeDef.Media_Type.MEDIA_MONITOR) {
//            if (Type == MediaTypeDef.Media_Type.MEDIA_RING) {
                    Type = mediaType;
//            }
                }
            }

            try {
                // 释放资源
                clearMdeiaPlayer();

                // 获取AudioManager实例
                if (Volume != 0) {
                    mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                    int volumeMax = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                    int volumeFlag = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    if (Volume != volumeFlag && volumeMax >= Volume) {
                        currentVolume = volumeFlag;
                        //设置当前音量
                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, Volume, 0);
                    }
                }

                AssetFileDescriptor afd = context.getAssets().openFd(ringPath);

                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                mMediaPlayer.setLooping(isLoop);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
                isLoopRing = isLoop;
                mediaType = Type;
                onMediaStatusCompletionListener = Listener;
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 播放结束回调
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if (!mediaPlayer.isLooping()) {
                        if (currentVolume != 0) {
                            //设置当前音量
                            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
                            currentVolume = 0;
                        }
                        if (onMediaStatusCompletionListener != null) {
                            onMediaStatusCompletionListener.onMediaStatusCompletion(true);
                            onMediaStatusCompletionListener = null;
                        }
                        if (mediaType != MediaTypeDef.Media_Type.MEDIA_CALL_IN && mediaType != MediaTypeDef.Media_Type.MEDIA_CALL_OUT) {
                            mediaType = MediaTypeDef.Media_Type.MEDIA_NONE;
                        }
                    }
                }
            });
        }

        return true;
    }

    /**
     * 播放本地路径下的声音
     * @param ringPath      声音路径，在assets下的路径
     */
    public static boolean playLocalSound( String ringPath, OnMediaStatusCompletionListener Listener ) {

//        if (enableVolumeTip())
//            return true;


        int Type = MediaTypeDef.Media_Type.MEDIA_AUDIO_PLAY;

        // 添加锁保护，防止一个线程在初始化MediaPlayer时，另一个线程调用stop
        synchronized(App.getInstance()) {

            if (ringPath == null) {
                return false;
            }

            if (mMediaPlayer != null && mMediaPlayer.isPlaying() && mediaType == MediaTypeDef.Media_Type.MEDIA_ALARM) {
                return false;
            }

            if (Type != MediaTypeDef.Media_Type.MEDIA_ALARM) {
                if (mediaType == MediaTypeDef.Media_Type.MEDIA_MONITOR_TALK ||
                        mediaType == MediaTypeDef.Media_Type.MEDIA_CALL_OUT_TALK || mediaType == MediaTypeDef.Media_Type.MEDIA_CALL_IN_TALK) {
                    return false;
                }

                if (mediaType == MediaTypeDef.Media_Type.MEDIA_CALL_IN || mediaType == MediaTypeDef.Media_Type.MEDIA_CALL_OUT ||
                        mediaType == MediaTypeDef.Media_Type.MEDIA_MONITOR) {
//            if (Type == MediaTypeDef.Media_Type.MEDIA_RING) {
                    Type = mediaType;
//            }
                }
            }

            try {
                //释放资源
                clearMdeiaPlayer();

                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setDataSource(ringPath);
                mMediaPlayer.setLooping(false);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
                mediaType = Type;
                onMediaStatusCompletionListener = Listener;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if (!mediaPlayer.isLooping()) {
                        if (onMediaStatusCompletionListener != null) {
                            onMediaStatusCompletionListener.onMediaStatusCompletion(true);
                            onMediaStatusCompletionListener = null;
                        }
                        if (mediaType != MediaTypeDef.Media_Type.MEDIA_CALL_IN && mediaType != MediaTypeDef.Media_Type.MEDIA_CALL_OUT) {
                            mediaType = MediaTypeDef.Media_Type.MEDIA_NONE;
                        }
                    }
                }
            });
        }

        return true;
    }

    /**
     * 播放视频
     * @param context
     * @param videoPath
     * @param surfaceView
     * @param Listener
     * @return
     */
    public static boolean PlayVideo(Context context, String videoPath, final SurfaceView surfaceView,
                                    OnMediaStatusCompletionListener Listener) {
//        if (enableVolumeTip())
//            return true;


        if (videoPath == null) {
            return false;
        }

        if (mMediaPlayer != null && mMediaPlayer.isPlaying() && mediaType == MediaTypeDef.Media_Type.MEDIA_ALARM) {
            return false;
        }

        if (mediaType == MediaTypeDef.Media_Type.MEDIA_CALL_IN || mediaType == MediaTypeDef.Media_Type.MEDIA_CALL_OUT ||
                mediaType == MediaTypeDef.Media_Type.MEDIA_MONITOR || mediaType == MediaTypeDef.Media_Type.MEDIA_MONITOR_TALK ||
                mediaType == MediaTypeDef.Media_Type.MEDIA_CALL_OUT_TALK || mediaType == MediaTypeDef.Media_Type.MEDIA_CALL_IN_TALK ) {
            return false;
        }

        File file = new File(videoPath); // 获取要播放的文件
        if (file.exists()) {    // 文件存在

            try {
                //释放资源
                clearMdeiaPlayer();

                mMediaPlayer = new MediaPlayer();
//                FileInputStream fis = new FileInputStream(file);
//                mediaPlayer.setDataSource(fis.getFD());   //设置要播放的视频
                mMediaPlayer.setDataSource(videoPath);   //设置要播放的视频
                mMediaPlayer.prepare();  //预加载视频

                mediaType = MediaTypeDef.Media_Type.MEDIA_VIDEO_PLAY;
                onMediaStatusCompletionListener = Listener;

            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                return false;
            } catch (SecurityException e) {
                e.printStackTrace();
                return false;
            } catch (IllegalStateException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e){
                e.printStackTrace();
                return false;
            }

            // 当装载媒体完毕的时候回调
            //等待surfaceHolder初始化完成才能执行mPlayer.setDisplay(surfaceHolder)
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    // 把视频画面输出到SurfaceView
                    mp.setDisplay(surfaceView.getHolder());
                    mp.start();
                }
            });

            //媒体播放结束时回调
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (onMediaStatusCompletionListener != null) {
                        onMediaStatusCompletionListener.onMediaStatusCompletion(true);
                        onMediaStatusCompletionListener = null;
                    }
                    mediaType = MediaTypeDef.Media_Type.MEDIA_NONE;
                }
            });
        }

        return true;
    }

    /**
     * 清空媒体
     */
    private static void clearMdeiaPlayer() {
        // 正在录音，停止录音
        if (mediaType == MediaTypeDef.Media_Type.MEDIA_AUDIO_REC){
        }

        if (mMediaPlayer != null) {
            Log.i(TAG, "clearMdeiaPlayer");
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mediaType = MediaTypeDef.Media_Type.MEDIA_NONE;
            if (onMediaStatusCompletionListener != null) {
                onMediaStatusCompletionListener.onMediaStatusCompletion(false);
                onMediaStatusCompletionListener = null;
            }
        }
        if (currentVolume != 0 ) {
            //设置当前音量
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
            currentVolume = 0;
        }
    }

    /**
     * 暂停播放
     */
    public static void pausePlay() {

        if (mMediaPlayer == null){
            return;
        }

        if (!mMediaPlayer.isPlaying()) {
            return;
        }

        if (mediaType == MediaTypeDef.Media_Type.MEDIA_ALARM) {
            return;
        }

        mMediaPlayer.pause();
        Log.i(TAG, "mMediaPlayer.isPlaying() = " + mMediaPlayer.isPlaying());
    }

    /**
     * 继续播放
     */
    public static void continuePlay() {

        if (mMediaPlayer == null){
            return;
        }

        if (mMediaPlayer.isPlaying()) {
            return;
        }

        if (mediaType == MediaTypeDef.Media_Type.MEDIA_ALARM) {
            return;
        }

        mMediaPlayer.start();
    }

    /**
     * 停止播放
     */
    public static boolean stopPlay(int Type) {

        Log.i(TAG, "stopPlay Type = " + Type + " mediaType = " + mediaType);
        // 添加锁保护，防止一个线程在初始化MediaPlayer时，另一个线程调用stop
        synchronized(App.getInstance()) {
            Log.i(TAG, "stopPlay start " );
            if (mediaType == MediaTypeDef.Media_Type.MEDIA_ALARM) {
                if (Type == MediaTypeDef.Media_Type.MEDIA_ALARM) {
                    clearMdeiaPlayer();
                } else {
                    Log.e("stopPlay", "stop sound error, now play ring sound");
                    return false;
                }
            } else {
                clearMdeiaPlayer();
            }
        }
        return true;
    }

    /**
     * 获取当前媒体状态
     * @return
     */
    public static int getMdeiaType() {
        return mediaType;
    }

    /**
     * 设置当前媒体状态
     * @return
     */
    public static boolean setMdeiaType(int Type) {
        boolean ret;
        Log.i(TAG, "setMdeiaType Type = " + Type);
        if (Type == MediaTypeDef.Media_Type.MEDIA_CALL_IN || Type == MediaTypeDef.Media_Type.MEDIA_CALL_OUT ||
                Type == MediaTypeDef.Media_Type.MEDIA_MONITOR || Type == MediaTypeDef.Media_Type.MEDIA_MONITOR_TALK ||
                Type == MediaTypeDef.Media_Type.MEDIA_CALL_OUT_TALK || Type == MediaTypeDef.Media_Type.MEDIA_CALL_IN_TALK ) {
            clearMdeiaPlayer();
            mediaType = Type;
            ret = true;
        } else {
            ret = stopPlay(Type);
            if (ret) {
                mediaType = Type;
            }
        }
        return ret;
    }

    static class CountRun implements Runnable {

        int count;

        public void setCountRun(int count) {
            this.count = count;
        }

        @Override
        public void run() {
            if (count == 3) {
                if (onMediaStatusCompletionListener != null) {
                    onMediaStatusCompletionListener.onMediaStatusCompletion(true);
                }
                mHandler.removeCallbacks(this);
                return;
            }
            count++;
            mHandler.postDelayed(this, 1000);
        }
    }

    static void setmHandler(Handler handler) {
        if (mHandler == null) {
            mHandler = handler;
        }
    }

}
