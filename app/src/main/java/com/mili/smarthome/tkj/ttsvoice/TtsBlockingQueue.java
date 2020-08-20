package com.mili.smarthome.tkj.ttsvoice;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import android.app.Application;
import android.util.Log;

import com.mili.smarthome.tkj.app.App;

public class TtsBlockingQueue {
	
	private static final String TAG="TtsBlockingQueue";
	private static App mMscttsApp = null;
	public static BlockingQueue<TtsParamData> VoiceBox = new ArrayBlockingQueue<TtsParamData>(20);
	
	public static void produceVoice(TtsParamData ttsData){
		try {
		  	VoiceBox.add(ttsData);
	      	Log.v(TAG, "VoiceBox Add ");
		} catch (Exception e) {
			// TODO: handle exception
			Log.v(TAG, "TtsBlockingQueue IS FULL !");
		}
    
    }  
	
    public static TtsParamData consumeVoice(){
    	try {
        	Log.v(TAG, "VoiceBox take");
			return VoiceBox.take();
		} catch (Exception e) {
			// TODO: handle exception
			Log.v(TAG, "TtsBlockingQueue take Error !");
			e.printStackTrace();
		}
		return null;
    }
    
    public void SetMscttsApp(App mscttsapp){
    	mMscttsApp = mscttsapp;
    }
    
	public  void startSpeakThread(){
		new ttsSpeakThread().start();
	}
	
	public class ttsSpeakThread extends Thread{

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			super.run();
			while(true){
				try {
					sleep(2000);
					Log.v(TAG, "TtsBlockingQueue="+Thread.currentThread());
					if(mMscttsApp != null){
						if(mMscttsApp.IsSpeaking()==false){
							TtsParamData ttsData=null;
							ttsData=TtsBlockingQueue.consumeVoice();
							if(ttsData==null)
								continue;
							if(ttsData.SpeakMode==TtsComDefine.SpeakMode.defaultStartSpeaking){
								mMscttsApp.defaultStartSpeaking( ttsData.subModuleStr,TtsComDefine.NoPutInQueue,
										ttsData.speakStr, ttsData.speed,ttsData.pitch, ttsData.volume,
										ttsData.stream_type);			
							}else if(ttsData.SpeakMode==TtsComDefine.SpeakMode.StartSpeaking){
								mMscttsApp.StartSpeaking(ttsData.subModuleStr,TtsComDefine.NoPutInQueue, 
										ttsData.speakStr, ttsData.speed, ttsData.pitch, ttsData.volume, 
										ttsData.stream_type, ttsData.Mode, ttsData.times);
							}else if(ttsData.SpeakMode==TtsComDefine.SpeakMode.StartSpeakingurl){
								mMscttsApp.StartSpeakingUrl(ttsData.subModuleStr,TtsComDefine.NoPutInQueue, 
										ttsData.speakUrl, ttsData.postfix, ttsData.speed,ttsData.pitch, 
										ttsData.volume, ttsData.stream_type, ttsData.Mode, ttsData.times);
							}
						}
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
}
