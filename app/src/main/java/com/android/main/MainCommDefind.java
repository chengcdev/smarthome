package com.android.main;

import com.android.client.MainClient;
import com.mili.smarthome.tkj.appfunc.WelcomeActivity;

public class MainCommDefind {

	public static MainClient mainClient;
	public static WelcomeActivity mainActivity;

	public static int center_isonline = 0;	// 是否注册上位机

	// 室内机
	public final static int DEVICE_TYPE_V2_REL = 0;
	public final static int DEVICE_TYPE_V10_REL = 1;
	public final static int DEVICE_TYPE_F9_REL = 2;
	public final static int DEVICE_TYPE_F10_REL = 3;

	// PAD
	public final static int DEVICE_TYPE_PAD_REL = 1000;
	public final static int DEVICE_TYPE_PAD_SZCW = 1001;

	// 梯口机
	public final static int DEVICE_TYPE_K3_REL = 2000;
	public final static int DEVICE_TYPE_K4_REL = 2001;
	public final static int DEVICE_TYPE_K6_REL = 2002;
	public final static int DEVICE_TYPE_K7_REL = 2003;
	public final static int DEVICE_TYPE_K4_X1600_REL = 2004;
}
