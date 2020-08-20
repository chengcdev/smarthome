package com.android;

public class MediaTypeDef 
{
	public class Media_State{
		public static final int MEDIA_AUDIO_STOP		=	0x00;
		public static final int MEDIA_AUDIO_PLAY_START	=	0x01;
		public static final int MEDIA_AUDIO_REC_START	=	0x02;
		public static final int MEDIA_AUDIO_REC_STOP	=	0x03;
		public static final int MEDIA_AUDIO_TICK		=	0x04;
		public static final int MEDIA_VIDEO_REC_START	=	0x05;
		public static final int MEDIA_VIDEO_REC_STOP	=	0x06;
		public static final int MEDIA_VIDEO_PLAY_START  =   0x07;
		public static final int MEDIA_VIDEO_PLAY_STOP	=   0x08;
		public static final int MEDIA_VIDEO_TICK		=	0x09;
		public static final int MEDIA_SNAP				=	0x0A;
		public static final int MEDIA_VIDEO_DISPLAY		=	0x0B;
		public static final int MEDIA_VIDEO_PREPARE		=	0x0C;
		public static final int MEDIA_AUDIO_PREPARE		=	0x0D;
	}

	public class Media_Type{
		public static final int MEDIA_ROOT = 1;
		public static final int MEDIA_ALARM = 2;
		public static final int MEDIA_MONITOR = 3;
		public static final int MEDIA_CALL_OUT = 4;
		public static final int MEDIA_CALL_IN = 5;
		public static final int MEDIA_MONITOR_TALK = 6;
		public static final int MEDIA_CALL_OUT_TALK = 7;
		public static final int MEDIA_CALL_IN_TALK = 8;
		public static final int MEDIA_RING = 9;
		public static final int MEDIA_VIDEO_REC = 10;
		public static final int MEDIA_AUDIO_REC = 11;
		public static final int MEDIA_VIDEO_PLAY = 12;
		public static final int MEDIA_AUDIO_PLAY = 13;
		public static final int MEDIA_VIDEO_PREVIEW = 14;
		public static final int MEDIA_NONE = 15;
	}
	
	public class Media_Camera{
		public static final int  CAMERA_FRONT = 1;
		public static final int  CAMERA_BACK = 0;
	}
	
	public class Media_Audio{
		public static final int MEDIA_AUDIO_OUT_TALK 	=	0x00;
		public static final int MEDIA_AUDIO_OUT_PLAY	=	0x01;
		public static final int MEDIA_AUDIO_OUT_DOOR	=	0x02;

		public static final int MEDIA_AUDIO_IN_MIC		=	0x10;
		public static final int MEDIA_AUDIO_IN_CAPTURE	=	0x11;
		public static final int MEDIA_AUDIO_IN_DOOR		=	0x12;
	}
	
	public interface OnMediaListener{
		void OnMediaStatus(int state, int time, int Id);
	}
	
	public interface OnRtspStopReturnListener{
		void onRtspStopReturn(int state, int err);
	}
	
	public class Media_PlayType{
		public static final int MEDIA_PLAY_LOCAL = 0;
		public static final int MEDIA_PLAY_MEDIAPLAY = 1;
		public static final int MEDIA_PLAY_NET = 2;
	}
}
