package com.android.main;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.dddevice.app.DongDeviceService;

public class DDService extends Service {

	private final static String TAG = "DDSerivce >>> ";
	private Context mContext;
	private static DongDeviceService mDongDongService;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mContext = this;
		Log.d(TAG, " DDSerivce onCreate...");
//		mDongDongService = new DongDeviceService();
//		Log.d(TAG, "DDSerivce onCreate initService ");
//		mDongDongService.initService();
		new RuningThread().start();
	}

	private class RuningThread extends Thread{

		@Override
		public void run() {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			Log.d(TAG, " new DongDeviceService...");
			mDongDongService = new DongDeviceService();
			Log.d(TAG, " DDSerivce onCreate initService... ");
			mDongDongService.initService();
		}
	}
	
	public void DDServiceRestart(){
		Log.d(TAG, " DDServiceRestart......");		
		new RestartThread().start();
	}
	
	private class RestartThread extends Thread{

		public RestartThread() {
			// TODO Auto-generated constructor stub
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			Log.d(TAG, " RestartThread finishService start......");
			mDongDongService.finishService();
			Log.d(TAG, " RestartThread finishService end......");
			
			try {
				sleep(30000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d(TAG, " RestartThread initService start......");
			mDongDongService.initService();
			Log.d(TAG, " RestartThread initService end......");
		}
		
	}
}
