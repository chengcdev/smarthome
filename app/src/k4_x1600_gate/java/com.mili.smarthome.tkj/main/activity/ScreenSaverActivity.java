package com.mili.smarthome.tkj.main.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.AppPreferences;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.base.K4BaseActivity;
import com.mili.smarthome.tkj.utils.ExternalMemoryUtils;
import com.mili.smarthome.tkj.utils.LogUtils;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

public class ScreenSaverActivity extends K4BaseActivity implements View.OnClickListener, TextureView.SurfaceTextureListener {

    private static final String TAG = "ScreenSaverActivity ";

    private static final int[] DEFAULT_IMAGE = new int[] {
            R.drawable.screen_saver_1,
            R.drawable.screen_saver_2,
            R.drawable.screen_saver_3,
            R.drawable.screen_saver_4,
    };

    private static final long PLAY_INTERVAL = 10 * 1000;

    private static final int MODE_DEFAULT = 0;
    private static final int MODE_PHOTO = 1;
    private static final int MODE_VEDIO = 2;

    private ImageView ivScreen;

    private ScreenSaverReceiver mReceiver;
    private MediaPlayer mMediaPlayer;
    private PlayTask mPlayTask;
    private boolean isMediaPlay;

    /**是否启用媒体静音*/
    private boolean isMediaMute;

    private int mPlayMode = MODE_DEFAULT;
    private int mPlayIndex = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_saver);

        ivScreen = findView(R.id.iv_screen);
        ImageView ivEnter = findView(R.id.iv_enter);
        ivEnter.setOnClickListener(this);
        TextureView textureView = findView(R.id.textureview);
        textureView.setSurfaceTextureListener(this);

        // 不同语言设置不同图标
        Locale mLocale = Locale.getDefault();
        if (Locale.SIMPLIFIED_CHINESE.equals(mLocale)) {
            ivEnter.setImageResource(R.drawable.main_enter_btn_cn);
        } else if (Locale.TRADITIONAL_CHINESE.equals(mLocale)) {
            ivEnter.setImageResource(R.drawable.main_enter_btn_tw);
        } else if (Locale.US.equals(mLocale)) {
            ivEnter.setImageResource(R.drawable.main_enter_btn_en);
        }

        int mediaVolume = AppConfig.getInstance().getMediaVolume();
        isMediaMute = (mediaVolume == 1);
        LogUtils.d(TAG + "[onCreate] isMediaMute = " + isMediaMute + ", volume=" + mediaVolume);

        //开始播放屏保
        isMediaPlay = AppPreferences.isMediaPlay();
        mPlayTask = new PlayTask();
        mMainHandler.post(mPlayTask);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mReceiver == null) {
            mReceiver = new ScreenSaverReceiver();
        }
        mReceiver.register(mContext);
        LogUtils.d(TAG + "[onResume] ok");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.d(TAG + "[onPause] ok");
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMainHandler.removeCallbacksAndMessages(0);
        mReceiver.unregister(mContext);
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
        }
        LogUtils.d(TAG + "[onPause] ok");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_enter) {
            finish();
        }
    }

    @Override
    public void onCardState(int state, String roomNo) {
        super.onCardState(state, roomNo);
        finish();
    }

    @Override
    public void onFingerOpen(int code, String roomNo) {
        super.onFingerOpen(code, roomNo);
        finish();
    }

    @Override
    public void onBodyInduction() {
        super.onBodyInduction();
        int type = AppConfig.getInstance().getBodyInduction();
        LogUtils.d(TAG + "[onBodyInduction] ===== type[" + type + "] ===== ");
        int mainFunc;
        switch (type) {
            case 1:
                mainFunc = MainActivity.FUNCTION_FACE;
                break;
            case 2:
            case 3:
                mainFunc = MainActivity.FUNCTION_QRCODE;
                break;
            default:
                return;
        }
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.putExtra(MainActivity.ARGS_FUNC, mainFunc);
        startActivity(intent);
    }

    // 广播接收器
    private class ScreenSaverReceiver extends BroadcastReceiver {

        public void register(Context context) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Const.ActionId.ACTION_MULTI_MEDIA);
            filter.addAction(Const.ActionId.SCREEN_SAVER_EXIT);
            context.registerReceiver(this, filter);
        }

        public void unregister(Context context) {
            context.unregisterReceiver(this);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtils.d(TAG + "[onReceive] action is " + action);
            if (action == null || action.length()== 0)
                return;
            if (action.equals(Const.ActionId.ACTION_MULTI_MEDIA)) {
                isMediaPlay = AppPreferences.isMediaPlay();
                if (!isMediaPlay && mPlayMode != MODE_DEFAULT) {
                    if (mMediaPlayer.isPlaying()) {
                        mMediaPlayer.stop();
                        mMediaPlayer.reset();
                    }
                    //mMainHandler.post(mPlayTask);
                    finish();
                }
            } else if (action.equals(Const.ActionId.SCREEN_SAVER_EXIT)) {
                finish();
            }
        }
    }

    // 播放屏保任务
    private class PlayTask implements Runnable {

        @Override
        public void run() {
            try {
                LogUtils.d(TAG + "[PlayTask.run] isMediaPlay is " + isMediaPlay);
                if (isMediaPlay && (playVedio() || playPhoto())) {
                    return;
                }
                playDefault();
            } catch (Exception e) {
                LogUtils.e(e);
                mMainHandler.postDelayed(PlayTask.this, 10);
            }
        }

        /** 播放下载的屏保视频 */
        private boolean playVedio() {
            File[] vedios = ExternalMemoryUtils.queryVedioList();
            if (vedios == null || vedios.length == 0) {
                return false;
            }
            if (mPlayMode != MODE_VEDIO) {
                mPlayMode = MODE_VEDIO;
                mPlayIndex = 0;
            } else {
                mPlayIndex++;
                mPlayIndex = mPlayIndex % vedios.length;
            }
            ivScreen.setVisibility(View.GONE);
            playVedioFile(vedios[mPlayIndex]);
            LogUtils.d(TAG + "[playVedio] index=" + mPlayIndex + ", path=" + vedios[mPlayIndex].getPath());
            return true;
        }

        /** 播放下载的屏保图片 */
        private boolean playPhoto() {
            File[] photos = ExternalMemoryUtils.queryPhotoList();
            if (photos == null || photos.length == 0) {
                return false;
            }
            if (mPlayMode != MODE_PHOTO) {
                mPlayMode = MODE_PHOTO;
                mPlayIndex = 0;
            } else {
                mPlayIndex++;
                mPlayIndex = mPlayIndex % photos.length;
            }
            ivScreen.setVisibility(View.VISIBLE);
            ivScreen.setImageURI(Uri.fromFile(photos[mPlayIndex]));
            LogUtils.d(TAG + "[playPhoto] index=" + mPlayIndex + ", path=" + photos[mPlayIndex].getPath());
            mMainHandler.postDelayed(PlayTask.this, PLAY_INTERVAL);
            return true;
        }

        /** 播放默认屏保图片 */
        private void playDefault() {
            if (mPlayMode != MODE_DEFAULT) {
                mPlayMode = MODE_DEFAULT;
                mPlayIndex = 0;
            } else {
                mPlayIndex++;
                mPlayIndex = mPlayIndex % DEFAULT_IMAGE.length;
            }
            ivScreen.setVisibility(View.VISIBLE);
            ivScreen.setImageResource(DEFAULT_IMAGE[mPlayIndex]);
            LogUtils.d(TAG + "[playDefault] index=" + mPlayIndex);
            mMainHandler.postDelayed(PlayTask.this, PLAY_INTERVAL);
        }
    }


    /**
     *  每天22:00到06:00媒体静音，只播放视频
     * @return true-静音处理
     */
    private boolean isMute() {
        if (isMediaMute)
            return true;
        Calendar calendarNow = Calendar.getInstance();
        int hourOfDay = calendarNow.get(Calendar.HOUR_OF_DAY);
        return (hourOfDay < 6) || (hourOfDay >= 22);
    }

    // 播放视频文件
    private void playVedioFile(final File vedioFile) {
        if (mMediaPlayer == null) {
            mMainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    playVedioFile(vedioFile);
                }
            }, 10);
        } else {
            try {
                mMediaPlayer.setDataSource(vedioFile.getPath());

                float volume = isMute() ? 0f : 1f;
                mMediaPlayer.setVolume(volume, volume);

                mMediaPlayer.prepare();
                mMediaPlayer.start();
            } catch (IOException e) {
                LogUtils.e(e);
            }
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        LogUtils.d(TAG + "[onSurfaceTextureAvailable] width is " + width + ", height is " + height);
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setSurface(new Surface(surfaceTexture));
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                LogUtils.d(TAG + " ========= onCompletion =========");
                mediaPlayer.reset();
                mMainHandler.post(mPlayTask);
            }
        });
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
        LogUtils.d(TAG + " ========= onSurfaceTextureSizeChanged =========");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        LogUtils.d(TAG + " ========= onSurfaceTextureDestroyed =========");
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }
}
