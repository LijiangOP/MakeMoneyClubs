package com.zhengdao.zqb.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;

/**
 * @Author lijiangop
 * @CreateTime 2018/5/14 09:51
 */
public class ServiceUtils {

    /**
     * 判断服务是否开启
     *
     * @return
     */
    public static boolean isServiceRunning(Context context, String ServiceName) {
        if (TextUtils.isEmpty(ServiceName))
            return false;
        ActivityManager myManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager.getRunningServices(256);
        for (int i = 0; i < runningService.size(); i++) {
            String name = runningService.get(i).service.getClassName().toString();
            if (!TextUtils.isEmpty(name) && name.equals(ServiceName)) {
                LogUtils.i("当前服务 i=" + i + "_服务全名=" + name);
                return true;
            }
        }
        return false;
    }
}
