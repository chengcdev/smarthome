package com.mili.smarthome.tkj.face.megvii;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import com.mili.smarthome.tkj.app.Const;
import com.mili.smarthome.tkj.face.megvii.offline.FaceApi;
import com.mili.smarthome.tkj.face.megvii.utils.FacePassImageUtils;
import com.mili.smarthome.tkj.utils.LogUtils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import mcv.facepass.types.FacePassImage;

/**
 * <p>2020-01-17 15:52  create by zenghm
 */
public class MegviiEnrollThread extends Thread {

    public static final int MSG_ENROLL_SUC = 0x21;
    public static final int MSG_ENROLL_TIMEOUT = 0x22;

    private volatile boolean running = false;
    private ArrayBlockingQueue<FacePassImage> mEnrollQueue = new ArrayBlockingQueue<>(1);
    private Handler mHandler;

    public MegviiEnrollThread(Handler handler) {
        mHandler = handler;
    }

    @Override
    public synchronized void start() {
        running = true;
        super.start();
    }

    @Override
    public void interrupt() {
        running = false;
        super.interrupt();
    }

    public boolean isRunning() {
        return running;
    }

    public boolean offer(FacePassImage image) {
        if (running) {
            return mEnrollQueue.offer(image);
        }
        return false;
    }

    @Override
    public void run() {
        long startTime = SystemClock.elapsedRealtime();
        long spendTime;
        while (running) {
            try {
                FacePassImage image = mEnrollQueue.poll(1000, TimeUnit.MILLISECONDS);
                if (image != null) {
                    final String token = FaceApi.addAndBindLocalFace(FacePassImageUtils.decode(image));
                    if (token != null && token.length() != 0) {
                        LogUtils.d("FacePass--->>>ENROLL: faceToken=%s", token);
                        //注册成功
                        Message msg = Message.obtain();
                        msg.what = MSG_ENROLL_SUC;
                        msg.obj = token;
                        spendTime = SystemClock.elapsedRealtime() - startTime;
                        mHandler.sendMessageDelayed(msg, Math.max(0, 1000 - spendTime));
                        break;
                    }
                }
                spendTime = SystemClock.elapsedRealtime() - startTime;
                if (spendTime > Const.Config.FACE_ENROLL_TIMEOUT) {
                    mHandler.sendEmptyMessage(MSG_ENROLL_TIMEOUT);
                    break;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        running = false;
        LogUtils.d("FacePass--->>>ENROLL: END!");
    }
}
