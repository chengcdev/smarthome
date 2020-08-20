
package com.android.main;

import android.content.Context;
import android.content.Intent;
import android.os.Looper;

import com.android.IntentDef;
import com.mili.smarthome.tkj.appfunc.BuildConfigHelper;
import com.mili.smarthome.tkj.appfunc.service.RestartService;

/**   
 * @ClassName:  MainLogic   
 * @Description:
 * @author: huangxf
 *      
 */

public class MainLogic extends ServiceLogic implements IntentDef.OnNetCommDataReportListener {

	private static final String tag = "MainLogic";
	private InterCommLogic mInterCommLogic = null;
	private SysArbitration mSysArbitration = null;
	private InfoLogic mInfoLogic = null;
	private SetDriverSinglechipLogic mSetDriverSinglechipLogic = null;
	private MultimediaLogic mMultimediaLogic = null;
	private MainData mMainData = null;
	private MainJni mMainJni =  null;
	/**
	 * @param action
	 */
	public MainLogic(Context context, MainJni Jni, String action) {
		super(action);
		// TODO Auto-generated constructor stub

		if (!BuildConfigHelper.isGate()) {
			// 非闸机版才启动咚咚服务
			Intent ddservice = new Intent(context, DDService.class);
			context.startService(ddservice);
		}

		mMainJni = Jni;
		if (action.equals(IntentDef.MODULE_MAIN))
			MainJni.setmMainListerner(this);
		
		mSysArbitration = new SysArbitration(context,mMainJni);
		mMainData = new MainData(context, Jni);
		new Thread(new mainDataStart()).start();//解决开机初始化jni层数据导致主线程卡住
		
		mInterCommLogic = new InterCommLogic(IntentDef.MODULE_INTERCOMM);
		if (null != mInterCommLogic){
			mInterCommLogic.InterCommLogicStart(mSysArbitration,Jni,context);
		}
		
		mInfoLogic = new InfoLogic(IntentDef.MODULE_INFO);
		if (null != mInfoLogic)
		{
			mInfoLogic.InfoLogicStart(context);
		}

		mSetDriverSinglechipLogic = new SetDriverSinglechipLogic(IntentDef.MODULE_SINGLECHIP);
		if ( null != mSetDriverSinglechipLogic )
		{
			mSetDriverSinglechipLogic.SetDriverSinglechipStart(mSysArbitration, context, Jni);
		}

		mMultimediaLogic = new MultimediaLogic(IntentDef.MODULE_MULTIMEDIA);
		if ( null != mMultimediaLogic )
		{
			mMultimediaLogic.MultimediaLogicStart(context, Jni);
		}

		Intent restartservice = new Intent(context, RestartService.class);
		context.startService(restartservice);

		Intent networkservice = new Intent(context, NetworkService.class);
		context.startService(networkservice);

		Intent ttsservice = new Intent(context, TTSServices.class);
		context.startService(ttsservice);
	}
	
	public void DestoryOther()
	{
		mInterCommLogic = null;
		mInfoLogic = null;
	}
	
	@Override
	public void OnDataReport(String action, int type, byte[] data) {
		// TODO Auto-generated method stub
		super.OnDataReport(action, type, data);
		
		if (false == action.equals(IntentDef.MODULE_MAIN))
		{
			return;
		}
				
	}

	private class mainDataStart implements Runnable {
		public void run() {
			Looper.prepare();
			mMainData.Start();
			Looper.loop();
		}
	}

}


