
package com.android.client;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.android.CommSysDef;
import com.android.Common;
import com.android.IntentDef;
import com.android.main.IMainService;
import com.android.main.MainService;

public class BaseClient{

	private static final String tag="BaseClient";
	
	private Handler mHandler = null;

	
	protected Context mContext = null;
	protected static IMainService mMainService = null;
	protected String mServiceName = null;
	
	protected IntentDef.OnNetCommDataReportListener mDataReportListener=null;
	protected ClientReceiver mClientReceiver = null;

	public BaseClient(){}

	public BaseClient(Context mContext,String serviceName) {
		this.mContext = mContext;
		mServiceName = serviceName;
		
	}
	
	public BaseClient(Context context)
	{
		mContext = context;
	}
	

	protected void StartIPC(Context context,String serviceName,String moduleName)
	{
		mServiceName = serviceName;
		Intent serviceIntent = new Intent(context, MainService.class);
		serviceIntent.setPackage("com.android.main");
		serviceIntent.putExtra(IntentDef.MODULE_NAME, moduleName);
		context.startService(serviceIntent);
		context.bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
	}
	

	protected void StopIPC(Context context, String serviceName, String moduleName)
	{
	    if ( null != mServiceConn )
	    {
	        context.unbindService(mServiceConn);
	    }
        Intent serviceIntent = new Intent(serviceName);
	    serviceIntent.setPackage("com.android.main");
        serviceIntent.putExtra(IntentDef.MODULE_NAME, moduleName);
        context.stopService(serviceIntent);
	}
	

	protected void StartIPC_Main(Context context)
	{
		StartIPC(context,CommSysDef.SERVICE_NAME_MAIN,IntentDef.MODULE_MAIN);
	}


	protected  ServiceConnection mServiceConn = new ServiceConnection(){

		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			if(mMainService == null){
				mMainService = IMainService.Stub.asInterface(service);
			}
			Log.d(tag, "service connected mainservice..."+mMainService+" service "+service);
		}

		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			mMainService = null;
			Log.d(tag, "service Disconnected mainservice...");
		}
		
	};
	

	protected void startReceiver(Context context, String broadcastName) {
		mHandler = new NetCommDataHandler(Looper.myLooper());
		mClientReceiver = new ClientReceiver();
		IntentFilter filter = new IntentFilter(broadcastName);
		context.registerReceiver(mClientReceiver, filter);
	}
	
	protected void stopReceiver(Context context, String broadcastName) {
	    if ( null != mClientReceiver )
	    {
	        context.unregisterReceiver(mClientReceiver);
	    }
    }


	protected void startReceiver(Context context, String[] broadcastList) {
		mHandler = new NetCommDataHandler(Looper.getMainLooper());
		mClientReceiver = new ClientReceiver();
		IntentFilter filter = new IntentFilter();
		for (String s : broadcastList) {
			filter.addAction(s);
		}
		context.registerReceiver(mClientReceiver, filter);
	}
	

	public boolean sendBroadcast(String action,int type,byte[] data)
	{
		if ((action==null) || (action.equals("")))
		{
			Log.e(tag, "sendBroadcast:action is null");
			return false;
		}
		Intent intent=new Intent(action);
		intent.putExtra(IntentDef.INTENT_NETCOMM_TYPE,type);
		if (data!=null)
		  intent.putExtra(IntentDef.INTENT_NETCOMM_DATA,data);
		Common.SendBroadCast(mContext, intent);
		return true;
	}
	 
	
	public void setmDataReportListener(
			IntentDef.OnNetCommDataReportListener mDataReportListener) {
		this.mDataReportListener = mDataReportListener;
	}


	class ClientReceiver extends BroadcastReceiver {
		 
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub			
			if (mHandler != null) {
				Message m = mHandler.obtainMessage(1, 1, 1, intent);
				mHandler.sendMessage(m);
			}
		}
	}


	class NetCommDataHandler extends Handler {
		public NetCommDataHandler(Looper looper) {
			super(looper);
		}

		 
		public void handleMessage(Message msg) {
			Intent intent=(Intent)msg.obj;
			if (intent==null)
			{
				Log.e(tag, "msg.obj is null");
				return;
			}
			
			String action=intent.getAction();
			
			int keyType=intent.getIntExtra(IntentDef.INTENT_NETCOMM_TYPE,IntentDef.INTENT__TYPE_INVALID);
			
			byte[] data=intent.getByteArrayExtra(IntentDef.INTENT_NETCOMM_DATA);
			if (mDataReportListener==null)
			{
				Log.e(tag, "mDataReportListener is null");
				return;
			}
			mDataReportListener.OnDataReport(action, keyType, data);
    	}
	}
}





