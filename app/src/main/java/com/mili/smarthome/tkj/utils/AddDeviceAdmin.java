package com.mili.smarthome.tkj.utils;

import android.app.Activity;
import android.app.admin.DeviceAdminInfo;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import com.mili.smarthome.tkj.receiver.ScreenOffAdminReceiver;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 添加设备管理权限（用于锁屏时没有权限问题）
 * Created by zhengxc on 2018/8/29 0029.
 * 注：参考mt8321_8.1_v1.63_ml8\packages\apps\Settings\src\com\android\settings\DeviceAdminAdd.java文件修改
 */

public class AddDeviceAdmin {
    private static final String TAG = "AddDeviceAdmin";

    public void addSystemAdmin(Context context) {

        String EXTRA_DEVICE_ADMIN_PACKAGE_NAME = "android.app.extra.DEVICE_ADMIN_PACKAGE_NAME";

        DevicePolicyManager mDPM;
        DeviceAdminInfo mDeviceAdmin;

        boolean mRefreshing;

        mDPM = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        PackageManager packageManager = context.getPackageManager();

        ComponentName who = new ComponentName(context, ScreenOffAdminReceiver.class);
        if (who == null) {
            Activity activity = (Activity) context;
            String packageName = activity.getIntent().getStringExtra(EXTRA_DEVICE_ADMIN_PACKAGE_NAME);
            for (ComponentName component : mDPM.getActiveAdmins()) {
                if (component.getPackageName().equals(packageName)) {
                    who = component;
                    break;
                }
            }
            if (who == null) {
                Log.w(TAG, "No component specified");
                return;
            }
        }

        ActivityInfo ai;
        try {
            ai = packageManager.getReceiverInfo(who, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, "Unable to retrieve device policy " + who, e);
            return;
        }

        // When activating, make sure the given component name is actually a valid device admin.
        // No need to check this when deactivating, because it is safe to deactivate an active
        // invalid device admin.
        if (!mDPM.isAdminActive(who)) {
            List<ResolveInfo> avail = packageManager.queryBroadcastReceivers(
                    new Intent(DeviceAdminReceiver.ACTION_DEVICE_ADMIN_ENABLED),
                    PackageManager.GET_DISABLED_UNTIL_USED_COMPONENTS);
            int count = avail == null ? 0 : avail.size();
            boolean found = false;
            for (int i=0; i<count; i++) {
                ResolveInfo ri = avail.get(i);
                if (ai.packageName.equals(ri.activityInfo.packageName)
                        && ai.name.equals(ri.activityInfo.name)) {
                    try {
                        // We didn't retrieve the meta data for all possible matches, so
                        // need to use the activity info of this specific one that was retrieved.
                        ri.activityInfo = ai;
                        DeviceAdminInfo dpi = new DeviceAdminInfo(context, ri);
                        found = true;
                    } catch (XmlPullParserException e) {
                        Log.w(TAG, "Bad " + ri.activityInfo, e);
                    } catch (IOException e) {
                        Log.w(TAG, "Bad " + ri.activityInfo, e);
                    }
                    break;
                }
            }
            if (!found) {
                Log.w(TAG, "Request to add invalid device admin: " + who);
                return;
            }
        }

        ResolveInfo ri = new ResolveInfo();
        ri.activityInfo = ai;
        try {
            mDeviceAdmin = new DeviceAdminInfo(context, ri);
        } catch (XmlPullParserException e) {
            Log.w(TAG, "Unable to retrieve device policy " + who, e);
            return;
        } catch (IOException e) {
            Log.w(TAG, "Unable to retrieve device policy " + who, e);
            return;
        }

        // This admin already exists, an we have two options at this point.  If new policy
        // bits are set, show the user the new list.  If nothing has changed, simply return
        // "OK" immediately.
        mRefreshing = false;
        if (mDPM.isAdminActive(who)) {
            if (mDPM.isRemovingAdmin(who, android.os.Process.myUserHandle().getIdentifier())) {
                Log.w(TAG, "Requested admin is already being removed: " + who);
                return;
            }

            ArrayList<DeviceAdminInfo.PolicyInfo> newPolicies = mDeviceAdmin.getUsedPolicies();
            for (int i = 0; i < newPolicies.size(); i++) {
                DeviceAdminInfo.PolicyInfo pi = newPolicies.get(i);
                if (!mDPM.hasGrantedPolicy(who, pi.ident)) {
                    mRefreshing = true;
                    break;
                }
            }
            if (!mRefreshing) {
                // Nothing changed (or policies were removed) - return immediately
                return;
            }
        }

        try {
//        int logCategory = true ? MetricsProto.MetricsEvent.APP_SPECIAL_PERMISSION_ADMIN_ALLOW : MetricsProto.MetricsEvent.APP_SPECIAL_PERMISSION_ADMIN_DENY;
//        FeatureFactory.getFactory(this).getMetricsFeatureProvider().action(this, logCategory, mDeviceAdmin.getComponent().getPackageName());
            mDPM.setActiveAdmin(mDeviceAdmin.getComponent(), mRefreshing);
//            EventLog.writeEvent(EventLogTags.EXP_DET_DEVICE_ADMIN_ACTIVATED_BY_USER,
//                    mDeviceAdmin.getActivityInfo().applicationInfo.uid);
        } catch (RuntimeException e) {
            // Something bad happened...  could be that it was
            // already set, though.
            Log.w(TAG, "Exception trying to activate admin " + mDeviceAdmin.getComponent(), e);
        }
    }
}
