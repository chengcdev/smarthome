package com.android.main;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.MscTts;
import com.android.CommSysDef;
import com.mili.smarthome.tkj.app.App;
import com.mili.smarthome.tkj.ttsvoice.TtsComDefine;
import com.mili.smarthome.tkj.utils.LogUtils;

public class TTSServices extends Service {

	private static final String TAG = "TTSServices";
	private App ttsApp;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		IntentFilter filter = new IntentFilter();
		filter.addAction(CommSysDef.TTSAPP_STARTSPEAK);
		filter.addAction(CommSysDef.TTSAPP_UPDATETEXT);
		filter.addAction(CommSysDef.TTSAPP_STOPSPEAK);
		registerReceiver(mReceiver, filter);
		ttsApp = App.getInstance();
		ttsApp.setMscTtsSpeakIntf(CommSysDef.TTS_SUBWORKPLAN, new MscTts.MscTtsSpeakIntf(){
			@Override
			public void MscTtsSpeakListener(int arg0) {
				if(arg0 == 4){
					LogUtils.e("TTS Play End!!!");
				}
			}
		});
		ttsApp.ttsStartQuene();
	}

	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(CommSysDef.TTSAPP_STARTSPEAK)) {
				int read_type = intent.getIntExtra(CommSysDef.TTS_READ_TYPE, CommSysDef.TTS_WOMAN);
				String text = intent.getStringExtra(CommSysDef.TTS_READ_TEXT);
				if(text != null && text.length() > 0 && (read_type == CommSysDef.TTS_MAN || read_type == CommSysDef.TTS_WOMAN )){
					Log.d(TAG, "TTS Text: " + text);
					if(text != null){
						ttsApp.MscTtsStop();
						ttsApp.StartSpeaking(CommSysDef.TTS_SUBWORKPLAN, TtsComDefine.PutInQueue,
								text, CommSysDef.ttsSpeed, CommSysDef.ttsPitch,
								CommSysDef.ttsVolume, CommSysDef.ttsStreamType, read_type, CommSysDef.TTS_READ_TIMES);
					}
				}
			}
			else if (action.equals(CommSysDef.TTSAPP_STOPSPEAK)){
				ttsApp.MscTtsStop();
			}			
		}
	};
}
