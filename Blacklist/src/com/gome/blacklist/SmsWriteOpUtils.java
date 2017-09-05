/* Copyright Statement:
 *  newly created for blacklist ops by wenpd on 2017/03/23 
 */

package com.gome.blacklist;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class SmsWriteOpUtils {
    public static final String TAG="SmsWriteOpUtils";
    private static final int OP_WRITE_SMS = 15;

    public static boolean isWriteEnabled(Context context) {
        int uid = getUid(context);
        Object opRes = checkOp(context, OP_WRITE_SMS, uid);

        if (opRes instanceof Integer) {
            return (Integer) opRes == AppOpsManager.MODE_ALLOWED;
        }
        return false;
    }

    public static boolean setWriteEnabled(Context context, boolean enabled) {
        int uid = getUid(context);
        int mode = enabled ? AppOpsManager.MODE_ALLOWED
                : AppOpsManager.MODE_IGNORED;

        return setMode(context, OP_WRITE_SMS, uid, mode);
    }

	@SuppressWarnings("rawtypes")
	private static Object checkOp(Context context, int code, int uid) {
        AppOpsManager appOpsManager = (AppOpsManager) context
                .getSystemService(Context.APP_OPS_SERVICE);
        Class[] ArgsClass = {Integer.TYPE, Integer.TYPE, String.class};
        Object [] params = {code, uid, context.getPackageName()};
		Object obj = null;
        try {
			obj = ReflectUtils.LoadMethod(null, appOpsManager, "checkOp", params, ArgsClass);
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

	@SuppressWarnings("rawtypes")
	private static boolean setMode(Context context, int code, int uid, int mode) {
        AppOpsManager appOpsManager = (AppOpsManager) context
                .getSystemService(Context.APP_OPS_SERVICE);
        Class[] ArgsClass = {Integer.TYPE, Integer.TYPE, String.class, Integer.TYPE};
        Object [] params = {code, uid, context.getPackageName(), mode};
        try {
			ReflectUtils.LoadMethod(null, appOpsManager, "setMode", params, ArgsClass);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
	}

	private static int getUid(Context context) {
        try {
            int uid = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_SERVICES).uid;
            return uid;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
	}
}
