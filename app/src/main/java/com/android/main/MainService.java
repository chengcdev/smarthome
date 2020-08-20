
package com.android.main;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.android.IntentDef;

import org.linphone.LinphoneManager;

public class MainService extends Service {

	private static final String tag = "MainService";
	private MainBinder mMainBinder= null;
	private MainLogic mMainLogic = null;
	private String TAG = "MainService";

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		String action = arg0.getAction();
		String moduleName = arg0.getStringExtra(IntentDef.MODULE_NAME);
		Log.d(tag, "bind MainService. intent: action: "+action+" modulename: "+moduleName);
	
		if (moduleName.equals(IntentDef.MODULE_MAIN)) {
			if	(mMainLogic == null)
				mMainLogic = new MainLogic(this.getApplicationContext(), mMainBinder.getMainJni(),IntentDef.MODULE_MAIN); 
		}		
		return mMainBinder;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		Log.d(tag, "create MainService....");
        if	(mMainBinder == null)
            mMainBinder = new MainBinder(this.getApplicationContext());

		if (LinphoneManager.getInstanceState() == false) {
			LinphoneManager.createAndStart(MainService.this);
			LinphoneManager.getLc().enableSpeaker(true);
		}
		mMainBinder.MainLogicJniInit();

		super.onCreate();
	}


	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.d(tag, "destory mainService");
		mMainBinder = null;
		super.onDestroy();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		Log.d(tag, "unbind mainService: "+intent);
		//String action = intent.getAction();
		String moduleName = intent.getStringExtra(IntentDef.MODULE_NAME);
		if (moduleName.equals(IntentDef.MODULE_MAIN)) 
		{
			mMainLogic.DestoryOther();
			mMainLogic = null;
			
		}
		return super.onUnbind(intent);
	}



}

