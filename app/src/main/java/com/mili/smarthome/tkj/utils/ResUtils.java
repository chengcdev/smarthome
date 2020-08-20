package com.mili.smarthome.tkj.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

/**
 * 资源工具类
 */
public final class ResUtils {

	private ResUtils() {}

	/** 根据资源名获取字符串ID */
	public static int getStringId(Context context, String resName) {
		if(context == null) {
			return 0;
		}
		Resources r = context.getResources();
		return r.getIdentifier(resName, "string", context.getPackageName());
	}

	/** 根据资源名获取字符串 */
	public static String getString(Context context, String resName) {
		int resId = getStringId(context, resName);
		if (resId == 0)
		    return "";
		return context.getString(resId);
	}
	
	/** 根据资源名获取图片ID */
	public static int getImageId(Context context, String resName) {
		if(context == null) {
			return 0;
		}
		Resources r = context.getResources();
		return r.getIdentifier(resName, "drawable", context.getPackageName());
	}

	/** 根据资源名获取图片 */
	public static Drawable getDrawable(Context context, String resName) {
		int resId = getImageId(context, resName);
		if (resId == 0)
		    return null;
		return context.getDrawable(resId);
	}

}