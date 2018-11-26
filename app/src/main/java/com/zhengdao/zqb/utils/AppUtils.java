package com.zhengdao.zqb.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.USAGE_STATS_SERVICE;

/**
 * @创建者 cairui
 * @创建时间 2016/12/15 10:40
 * @描述 获取App相关信息的工具类
 * @更新者 $Author$
 * @更新时间 $Date$
 * @版本 $Revision$
 * @更新描述 ${des}
 */
public class AppUtils {
    private AppUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");

    }

    /**
     * 获取应用程序名称
     */
    public static String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * [获取应用程序版本名称信息]
     *
     * @param context
     * @return 当前应用的版本名称
     */
    public static String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * [获取应用程序版本名称信息]
     *
     * @param context
     * @return 当前应用的版本号
     */
    public static int getVersionCode(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * 安装APP
     *
     * @param context 接收外部传进来的context
     */
    public static void install(Context context, File file) {
        if (file == null || !file.exists()) {
            return;
        }
        // 核心是下面几句代码
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void install(Context context, File file, String packageName) {
        if (file == null || !file.exists()) {
            return;
        }
        // 核心是下面几句代码
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static boolean checkFile(File file, String md5) {
        if (md5 == null || file == null || !file.exists()) {
            return false;
        }
        String md5ByFile = MD5Utils.getMd5ByFile(file);
        if (TextUtils.isEmpty(md5ByFile)) {
            LogUtils.e("md5ByFile=null");
            return false;
        } else {
            LogUtils.e("md5ByFile:" + md5ByFile + "md5:" + md5);
            return md5ByFile.equals(md5);
        }
    }

    /**
     * 获取application中指定的meta-data
     *
     * @return 如果没有获取成功(没有对应值 ， 或者异常)，则返回值为空
     */
    public static String getAppMetaDataString(Context ctx, String key) {
        if (ctx == null || TextUtils.isEmpty(key)) {
            return null;
        }
        String resultData = null;
        try {
            PackageManager packageManager = ctx.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null && applicationInfo.metaData != null) {
                    String result = applicationInfo.metaData.getString(key);
                    if (!TextUtils.isEmpty(result) && result.contains("zqb_")) {
                        resultData = result.replace("zqb_", "");
                    } else if (!TextUtils.isEmpty(result) && result.contains("jzb_")) {
                        resultData = result.replace("jzb_", "");
                    } else if (!TextUtils.isEmpty(result) && result.contains("lczj_")) {
                        resultData = result.replace("lczj_", "");
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return resultData;
    }

    public static boolean isServiceStarted(Context context, String PackageName) {
        boolean isStarted = false;
        try {
            ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningServiceInfo> mRunningService = mActivityManager.getRunningServices(1000);
            for (ActivityManager.RunningServiceInfo amService : mRunningService) {
                if (0 == amService.service.getPackageName().compareTo(PackageName)) {
                    isStarted = true;
                    break;
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return isStarted;
    }

    /**
     * 获取当前运行的栈顶Activity
     *
     * @param context
     * @return
     */
    public static String getRunningApp(Context context) {
        String mPackageName = "";
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > 20) {// 5.0
            List<ActivityManager.RunningAppProcessInfo> tasks = mActivityManager.getRunningAppProcesses();
            if (null != tasks && tasks.size() > 0) {
                mPackageName = tasks.get(0).processName;
            }
        } else if (Build.VERSION.SDK_INT > 21) {// 5.1及5.1之后
            if (isNoSwitch(context)) {
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                context.startActivity(intent);
            }
            long ts = System.currentTimeMillis();

            UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(USAGE_STATS_SERVICE);

            List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, ts - 5000, ts);//5秒内

            if (queryUsageStats == null || queryUsageStats.isEmpty()) {
                return null;
            }
            UsageStats recentStats = null;
            for (UsageStats usageStats : queryUsageStats) { //取得最近运行的一个app，即当前运行的app
                if (recentStats == null || recentStats.getLastTimeUsed() < usageStats.getLastTimeUsed()) {
                    recentStats = usageStats;
                }
            }
            mPackageName = recentStats.getPackageName();
        } else {// 5.0及5.0之前
            mPackageName = mActivityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
        }
        return mPackageName;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static boolean isNoSwitch(Context context) {
        long ts = System.currentTimeMillis();
        @SuppressLint("WrongConstant") UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService("usagestats");
        List queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, 0, ts);
        if (queryUsageStats == null || queryUsageStats.isEmpty()) {
            return false;
        }
        return true;
    }
    //然后就是跳转的代码了：


    /**
     * 检查手机上是否安装了指定的软件
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isAvilible(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        List<String> packageNames = new ArrayList<>();
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        // 判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName);
    }

    /**
     * 打开App
     *
     * @param context
     * @param packageName
     */
    public static void openApp(Context context, String packageName) {
        try {
            if (TextUtils.isEmpty(packageName))
                LogUtils.e("包名为空");
            final PackageManager pm = context.getPackageManager();
            if (checkPackInfo(context, packageName)) {
                Intent intent = pm.getLaunchIntentForPackage(packageName);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else {
                ToastUtil.showToast(context, "没有安装" + packageName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查包是否存在
     *
     * @param packname
     * @return
     */
    private static boolean checkPackInfo(Context context, String packname) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packname, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo != null;
    }

    public static String getPackageNameByFile(Context context, File file) {
        if (file == null || !file.exists())
            return "";
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageArchiveInfo(file.getAbsolutePath(), PackageManager.GET_ACTIVITIES);
        return packageInfo.packageName;
    }

    /**
     * 获取手机IMEI
     *
     * @param context
     * @return
     */
    public static String getIMEI(Context context) {
        try {
            //实例化TelephonyManager对象
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            //获取IMEI号
            @SuppressLint("MissingPermission") String imei = telephonyManager.getDeviceId();
            //在次做个验证，也不是什么时候都能获取到的啊
            if (imei == null) {
                imei = "";
            }
            return imei;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }
}
