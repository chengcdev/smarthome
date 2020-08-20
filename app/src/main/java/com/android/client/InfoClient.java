package com.android.client;

import android.content.Context;

import com.android.CommSysDef;
import com.android.IntentDef;


public class InfoClient extends BaseClient implements IntentDef.OnNetCommDataReportListener {

	private static final String tag = "InfoClient";
	private Context mContext = null;

	public InfoClient() {
	}

	public InfoClient(Context context)
	{
		super(context);
		String[] list = new String[] { IntentDef.MODULE_INFO };
		startReceiver(context, list);
		StartIPC_Main(context);
		setmDataReportListener(this);
		mContext = context;
	}

	public void stopInfoClient()
	{
		if (mContext == null) {
			return;
		}
		stopReceiver(mContext, IntentDef.MODULE_INFO);
		StopIPC(mContext, CommSysDef.SERVICE_NAME_MAIN, IntentDef.MODULE_INFO);
	}
	
	@Override
	public void OnDataReport(String action, int type, byte[] data) {

		if (false == action.equals(IntentDef.MODULE_INFO))
		{
			return;
		}
		
		switch (type)
		{
			case IntentDef.PubIntentTypeE.Info_NewInfoNofity:	
				break;
	
			case IntentDef.PubIntentTypeE.Info_ClearInfoNofity:
				break;

			default:
				break;
		}
	}

}

