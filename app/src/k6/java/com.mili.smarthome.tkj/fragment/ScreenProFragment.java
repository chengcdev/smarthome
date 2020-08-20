package com.mili.smarthome.tkj.fragment;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.App;
import com.mili.smarthome.tkj.app.AppPreferences;
import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.constant.Constant;
import com.mili.smarthome.tkj.main.activity.MainActivity;
import com.mili.smarthome.tkj.utils.AppUtils;
import com.mili.smarthome.tkj.utils.ExternalMemoryUtils;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.widget.CustomVideoView;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ScreenProFragment extends BaseMainFragment implements View.OnClickListener {

    private ImageView ivScreen;
    private CloseActRececiver receciver;
    private CustomVideoView mVideoView;
    private List<String> videoPathList = new ArrayList<>();
    private List<String> imgPathList = new ArrayList<>();
    private int count;
    private Bitmap bitmap;
    private boolean isMediaMute;
    private ImageView mIvEnter;

    @Override
    public void initView(View view) {
        ivScreen = (ImageView) view.findViewById(R.id.iv_screen);
        mVideoView = (CustomVideoView) view.findViewById(R.id.videoview);
        mIvEnter = (ImageView) view.findViewById(R.id.iv_enter);
        mIvEnter.setVisibility(View.VISIBLE);
        mIvEnter.setOnClickListener(this);
        Constant.ScreenId.IS_SCREEN_SAVE = true;
//        AppUtils.getInstance().sendReceiver(Constant.Action.MAIN_REFRESH_ACTION);

        if (AppConfig.getInstance().getMediaVolume() == 1) {
            isMediaMute = true;
        }else {
            isMediaMute = false;
        }

        play();
        LogUtils.w(" ScreenProFragment onCreate");
    }

    @Override
    public int getLayout() {
        return R.layout.activity_screen_saver;
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.w(" ScreenProFragment onPause");
    }

    @Override
    public void onStart() {
        super.onStart();
        receciver = new CloseActRececiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.Action.CLOSE_SCREEN_PROTECT);
        intentFilter.addAction(Const.ActionId.ACTION_MULTI_MEDIA);
        intentFilter.addAction(Constant.Action.ACTION_HIDE_BOTTOM_BTN);
        getActivity().registerReceiver(receciver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        Constant.ScreenId.IS_SCREEN_SAVE = false;
        LogUtils.w(" ScreenProFragment onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(receciver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mVideoView != null && mVideoView.isPlaying()) {
            mVideoView.pause();
        }
        if (bitmap != null) {
            bitmap.recycle();
            System.gc();
        }
        //显示主界面底部功能界面
        AppUtils.getInstance().sendReceiver(Constant.Action.ACTION_SHOW_BOTTOM_BTN);
    }

    private void play() {
        setPathList();
        if (videoPathList.size() > 0) {
            //播放sd卡的视频
            startVideo();
        } else if (imgPathList.size() > 0) {
            new PictureTask().run();
        } else {
            new ScreenSaverTask().run();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_enter:
                Constant.ScreenId.IS_SCREEN_SAVE = false;
                mIvEnter.setVisibility(View.GONE);
                //显示主界面底部功能界面
                AppUtils.getInstance().sendReceiver(Constant.Action.ACTION_SHOW_BOTTOM_BTN);
                //15秒之后隐藏主界面底部按键
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Activity currentActivity = App.getInstance().getCurrentActivity();
                        if (currentActivity instanceof MainActivity && ((MainActivity) currentActivity).currentFrag instanceof ScreenProFragment) {
                            AppUtils.getInstance().sendReceiver(Constant.Action.ACTION_HIDE_BOTTOM_BTN);
                            Constant.ScreenId.IS_SCREEN_SAVE = true;
                        }
                    }
                }, 1000 * 15);
                break;
        }
    }

    private class ScreenSaverTask implements Runnable {

        private final long INTERVAL = 10 * 1000;
        private final int[] IMAGE_ARRAY = new int[]{
                R.drawable.screensaver0,
                R.drawable.screensaver1,
                R.drawable.screensaver2,
                R.drawable.screensaver3,
        };

        private int index = -1;

        @Override
        public void run() {
            index++;
            index = index % IMAGE_ARRAY.length;

            setPathList();
            if (videoPathList.size() > 0) {
                count = 0;
                //播放sd卡的视频
                startVideo();
            } else if (imgPathList.size() > 0) {
                count = 0;
                new PictureTask().run();
            } else {
                mVideoView.setVisibility(View.GONE);
                ivScreen.setVisibility(View.VISIBLE);
                ivScreen.setImageResource(IMAGE_ARRAY[index]);
                mMainHandler.postDelayed(ScreenSaverTask.this, INTERVAL);
            }

        }
    }

    private class PictureTask implements Runnable {

        private final long INTERVAL = 10 * 1000;

        @Override
        public void run() {

            setPathList();
            if (videoPathList.size() > 0) {
                count = 0;
                //播放sd卡的视频
                startVideo();
            } else if (imgPathList.size() > 0) {
                mVideoView.setVisibility(View.GONE);
                ivScreen.setVisibility(View.VISIBLE);
                //播放sd卡的图片
                if (count >= imgPathList.size()) {
                    count = 0;
                }
                bitmap = getSmallBitmap(imgPathList.get(count));
                ivScreen.setImageBitmap(bitmap);
                count++;
                mMainHandler.postDelayed(this, INTERVAL);
            } else {
                //播放资源图片
                new ScreenSaverTask().run();
            }

        }
    }

    class CloseActRececiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constant.Action.CLOSE_SCREEN_PROTECT.equals(action)) {
                backMainActivity();
            } else if (Const.ActionId.ACTION_MULTI_MEDIA.equals(action)) {
                if (videoPathList.size() > 0 || imgPathList.size() > 0) {
                    if (mVideoView != null) {
                        mVideoView.pause();
                    }
                    backMainActivity();
                }
            }else if (Constant.Action.ACTION_HIDE_BOTTOM_BTN.equals(action)) {
                mIvEnter.setVisibility(View.VISIBLE);
            }
        }
    }

    //开始播放视频
    public void startVideo() {

        mVideoView.setVisibility(View.VISIBLE);
        ivScreen.setVisibility(View.GONE);

        mVideoView.setVideoPath(videoPathList.get(count));
        //开始播放
        mVideoView.start();

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                if (needMute()) {
                    //静音
                    mediaPlayer.setVolume(0f, 0f);
                } else {
                    //打开媒体音
                    mediaPlayer.setVolume(1f, 1f);
                }
            }
        });

        //播放完成
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                count++;
                if (count >= videoPathList.size()) {
                    count = 0;
                }
                play();
            }
        });
    }


    /**
     * 获取占用小内存的bitmap
     *
     * @param imgPath 图片路径
     * @return Bitmap格式
     */
    public Bitmap getSmallBitmap(String imgPath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgPath, options);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        //避免出现内存溢出的情况，进行相应的属性设置。
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inDither = true;
        return BitmapFactory.decodeFile(imgPath, options);
    }

    public void setPathList() {
        File[] videoList = ExternalMemoryUtils.queryVedioList();
        File[] imgList = ExternalMemoryUtils.queryPhotoList();

        if (AppPreferences.isMediaPlay() && videoList != null && videoList.length > 0) {
            videoPathList.clear();
            for (File file : videoList) {
                videoPathList.add(file.getPath());
            }
        } else if (AppPreferences.isMediaPlay() && imgList != null && imgList.length > 0) {
            //播放sd卡的图片
            imgPathList.clear();
            for (File file : imgList) {
                imgPathList.add(file.getPath());
            }
        } else {
            videoPathList.clear();
            imgPathList.clear();
        }
    }

    /** 每天22:00到06:00媒体静音，只播放视频 */
    private boolean needMute() {
        if (isMediaMute)
            return true;
        Calendar calendarNow = Calendar.getInstance();
        int hourOfDay = calendarNow.get(Calendar.HOUR_OF_DAY);
        return (hourOfDay < 6) || (hourOfDay >= 22);
    }
}
