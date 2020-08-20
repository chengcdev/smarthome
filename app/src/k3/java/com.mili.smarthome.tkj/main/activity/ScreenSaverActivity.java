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
import com.mili.smarthome.tkj.base.K3BaseActivity;
import com.mili.smarthome.tkj.utils.ExternalMemoryUtils;
import com.mili.smarthome.tkj.utils.LogUtils;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class ScreenSaverActivity extends K3BaseActivity implements View.OnClickListener,
        TextureView.SurfaceTextureListener, MediaPlayer.OnCompletionListener {

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
    private MediaPlayer mMediaPlayer;

    private ScreenSaverReceiver mReceiver;
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
        findView(R.id.iv_back).setOnClickListener(this);

        TextureView textureView = findView(R.id.textureview);
        textureView.setSurfaceTextureListener(this);

        isMediaMute = (AppConfig.getInstance().getMediaVolume() == 1);

        isMediaPlay = AppPreferences.isMediaPlay();
        mPlayTask = new PlayTask();
        mMainHandler.post(mPlayTask);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mReceiver == null) {
            mReceiver = new ScreenSaverReceiver();
        }
        mReceiver.register(mContext);
        LogUtils.d(" [ScreenSaverActivity >>> onResume] ok");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.d(" [ScreenSaverActivity >>> onPause] ok");
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
        LogUtils.d(" [ScreenSaverActivity >>> onStop] ok");
    }

    @Override
    public void onBodyInduction() {
        super.onBodyInduction();
        int mainFunc;
        switch (AppConfig.getInstance().getBodyInduction()) {
            case 1:
                mainFunc = MainActivity.FUNC_FACE;
                break;
            case 2:
            case 3:
                mainFunc = MainActivity.FUNC_QRCODE;
                break;
            default:
                return;
        }
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.putExtra(MainActivity.ARGS_FUNC, mainFunc);
        startActivity(intent);
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
            if (action == null || action.length()== 0)
                return;
            switch (action) {
                case Const.ActionId.ACTION_MULTI_MEDIA:
                    isMediaPlay = AppPreferences.isMediaPlay();
                    if (!isMediaPlay && mPlayMode != MODE_DEFAULT) {
                        if (mMediaPlayer.isPlaying()) {
                            mMediaPlayer.stop();
                            mMediaPlayer.reset();
                        }
                        //mMainHandler.post(mPlayTask);
                        finish();
                    }
                    break;
                case Const.ActionId.SCREEN_SAVER_EXIT:
                    finish();
                    break;
            }
        }
    }

    // ==================================================================//

    private class PlayTask implements Runnable {

        @Override
        public void run() {
            try {
                if (isMediaPlay && (playVedio() || playPhoto())) {
                    return;
                }
                playDefault();
            } catch (Exception e) {
                LogUtils.printThrowable(e);
                mMainHandler.postDelayed(PlayTask.this, 10);
            }
        }

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
            LogUtils.d("play vedio: index=" + mPlayIndex + ", path=" + vedios[mPlayIndex].getPath());
            return true;
        }

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
            LogUtils.d("play photo: index=" + mPlayIndex + ", path=" + photos[mPlayIndex].getPath());
            mMainHandler.postDelayed(PlayTask.this, PLAY_INTERVAL);
            return true;
        }

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
            LogUtils.d("play default: index=" + mPlayIndex);
            mMainHandler.postDelayed(PlayTask.this, PLAY_INTERVAL);
        }
    }

    // ==================================================================//

    /** 每天22:00到06:00媒体静音，只播放视频 */
    private boolean needMute() {
        if (isMediaMute)
            return true;
        Calendar calendarNow = Calendar.getInstance();
        int hourOfDay = calendarNow.get(Calendar.HOUR_OF_DAY);
        return (hourOfDay < 6) || (hourOfDay >= 22);
    }

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

                float volume = needMute() ? 0f : 1f;
                mMediaPlayer.setVolume(volume, volume);

                mMediaPlayer.prepare();
                mMediaPlayer.start();
            } catch (IOException e) {
                LogUtils.printThrowable(e);
            }
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setSurface(new Surface(surface));
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnCompletionListener(this);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.reset();
        mMainHandler.post(mPlayTask);
    }
}
