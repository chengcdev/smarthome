package com.android.main;

import android.content.Context;
import android.os.PowerManager;

public class SysArbitration {
	
	public static final String tag = "SysArbitration";
	public Context mContext = null;
	public PowerManager.WakeLock mLock = null;
	public MainJni mMainJni = null;
	public SysArbitration(Context context,MainJni jni)
	{
		mContext = context;	
		mMainJni = jni;
	}
	public SysArbitration(Context context)
	{
		mContext = context;	
	}
	public  void OpenLcd(){
	}
	
	public void OnInterCallStateChange(int active, int state, int DevType, String DevNo, int VideoState)
	{
	}
	
	public void OnSecuStateChange(Boolean uiState, int showFlag)
	{
	}
}

