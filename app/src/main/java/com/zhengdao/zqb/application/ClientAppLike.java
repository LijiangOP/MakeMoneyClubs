package com.zhengdao.zqb.application;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.multidex.MultiDex;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.sdk.android.BaseAlibabaSDK;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreater;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreater;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.tencent.bugly.Bugly;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;
import com.tencent.tinker.loader.app.DefaultApplicationLike;
import com.yatoooon.screenadaptation.ScreenAdapterTools;
import com.youle.androidsdk.utils.YLUtils;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.receiver.NetworkConnectChangedReceiver;
import com.zhengdao.zqb.utils.LogUtils;

import java.io.File;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;


/**
 * @Author lijiangop
 * @CreateTime 2017/9/15 18:11
 */
public class ClientAppLike extends DefaultApplicationLike {

    public static String WECHAT_APPID;
    public static String WECHAT_SECRET_ID = "";
    public static String  QQ_APPID;
    public static String  APK_FILE_NAME;
    public static Tencent mTencent;
    public static Context context;
    public static IWXAPI  mWxApi;


    public        NetworkConnectChangedReceiver mReceiver;
    public static int                           AppType;
    private String[] mStrings = new String[]{Constant.ADVIEW.KeySet};
    private static ClientAppLike mClientAppLike;

    public ClientAppLike(Application application, int tinkerFlags, boolean tinkerLoadVerifyFlag,
                         long applicationStartElapsedTime, long applicationStartMillisTime, Intent tinkerResultIntent) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime, applicationStartMillisTime, tinkerResultIntent);
        context = getApplication();
    }

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mClientAppLike = this;
        RetrofitManager.init(getApplication().getApplicationContext());
        initMultipleApp();
        initNetWorkListener();
        initTencent();
        initJPush();
        initTaobaoAppLink();
        initSmartRefreshLayout();
        ScreenAdapterTools.init(getApplication().getApplicationContext());
        initYoule();
    }

    private void initMultipleApp() {
        String packageName = getApplication().getPackageName();
        if (!TextUtils.isEmpty(packageName) && packageName.equals("com.wlg.wlgmall")) {//兼职呗
            AppType = Constant.App.Wlgfl;
            WECHAT_APPID = "wxde190ac2e913d0b6";
            WECHAT_SECRET_ID = "b5a583be719680768dbd3dcfde69c49b";
            QQ_APPID = "101430001";
            APK_FILE_NAME = "jzb.apk";
        } else if (!TextUtils.isEmpty(packageName) && packageName.equals("com.wlgfl.wlgflmall")) {//兼职呗(华为市场)
            AppType = Constant.App.Wlgfl_hy;
            WECHAT_APPID = "wxde190ac2e913d0b6";
            WECHAT_SECRET_ID = "b5a583be719680768dbd3dcfde69c49b";
            QQ_APPID = "101430001";
            APK_FILE_NAME = "jzb.apk";
        } else if (!TextUtils.isEmpty(packageName) && packageName.equals("com.lc.lccenter")) {
            AppType = Constant.App.Lczj;
            WECHAT_APPID = "wx6e3365a437734988";
            QQ_APPID = "101435276";
            APK_FILE_NAME = "lczj.apk";
        } else {//赚钱吧
            AppType = Constant.App.Zqb;
            WECHAT_APPID = "wx08fef6b801503f47";
            WECHAT_SECRET_ID = "c34fe497c26175d1aa394a4300e09c33";//微信登录才用
            QQ_APPID = "101464836";
            APK_FILE_NAME = "zqb.apk";
        }
    }

    private void initNetWorkListener() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mReceiver = new NetworkConnectChangedReceiver();
        getApplication().registerReceiver(mReceiver, filter);
    }

    private void initTencent() {
        //Bugly
        Bugly.init(getApplication(), "4ca7900ce9", true); // 调试时，将第三个参数改为true
        Bugly.setIsDevelopmentDevice(getApplication(), true); //启用调试设备
        //QQ
        mTencent = Tencent.createInstance(QQ_APPID, context);//qq登录
        //微信
        mWxApi = WXAPIFactory.createWXAPI(context, WECHAT_APPID, false);
        mWxApi.registerApp(WECHAT_APPID);
    }

    private void initStrikeMode() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()   // or .detectAll() for all detectable problems
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());
    }

    //tinker中 bugly的集成
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);
        // you must install multiDex whatever tinker is installed!
        MultiDex.install(base);
        // 安装tinker
        // TinkerManager.installTinker(this); 替换成下面Bugly提供的方法
    }

    private void initJPush() {
        JPushInterface.setDebugMode(true);// 设置开启日志,发布时请关闭日志
        JPushInterface.init(getApplication()); // 初始化 JPush
        LogUtils.i("RegistrationID=" + JPushInterface.getRegistrationID(getApplication()));
        //定义通知样式
        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(context);
        builder.statusBarDrawable = R.drawable.icon_small; //通知图标
        builder.notificationFlags = Notification.FLAG_AUTO_CANCEL
                | Notification.FLAG_SHOW_LIGHTS; //设置为自动消失和呼吸灯闪烁
        builder.notificationDefaults = Notification.DEFAULT_SOUND
                | Notification.DEFAULT_VIBRATE
                | Notification.DEFAULT_LIGHTS; // 设置为铃声、震动、呼吸灯闪烁都要
        JPushInterface.setPushNotificationBuilder(1, builder);
    }

    private void initSmartRefreshLayout() {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreater(new DefaultRefreshHeaderCreater() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.color_444444, android.R.color.white);//全局设置主题颜色
                return new MaterialHeader(context);
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreater(new DefaultRefreshFooterCreater() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                return new ClassicsFooter(context).setSpinnerStyle(SpinnerStyle.Translate);
            }
        });
    }

    private void initTaobaoAppLink() {
        BaseAlibabaSDK.asyncInit(context, new com.alibaba.sdk.android.callback.InitResultCallback() {
            @Override
            public void onSuccess() {
                Log.d("alibaba", "BaseAlibabaSDK init successed");
            }

            @Override
            public void onFailure(int i, String s) {
                Log.e("alibaba", "BaseAlibabaSDK init failed");
            }
        });
    }

    private void initYoule() {
        YLUtils.getInstance(context).init(Constant.YouLe.YOULE_CHANNEL_ID, Constant.YouLe.YOUELE_APPSECRET);
    }

    public static ClientAppLike getInstance() {
        return mClientAppLike;
    }

    public String getDownLoadCacheDirectory() {
        return getCachePath() + File.separator + "download" + File.separator;
    }

    /**
     * 获取app缓存路径
     * 先获取外部存储 外部存储不可用用内部存储  没有使用sd卡权限的使用内部缓存
     *
     * @return
     */
    private String getCachePath() {
        String cachePath;
        int checkCallPhonePermission = 1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //检查是否拥有权限
            checkCallPhonePermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || checkCallPhonePermission == PackageManager.PERMISSION_GRANTED) {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
                //外部存储可用
                cachePath = Environment.getExternalStorageDirectory().getPath() + File.separator + "cmodichina" + File.separator + "cmedicaliot";
                //设置磁盘缓存目录（和创建的缓存目录相同）
                File file = new File(cachePath);
                if (!file.exists()) {
                    file.mkdirs();
                }
            } else {
                //外部存储不可用
                cachePath = getApplication().getCacheDir().getPath();
            }
        } else {
            cachePath = getCacheDirectory(getContext(), null);
        }
        return cachePath;
    }

    /**
     * 获取应用专属缓存目录
     * android 4.4及以上系统不需要申请SD卡读写权限
     * 因此也不用考虑6.0系统动态申请SD卡读写权限问题，切随应用被卸载后自动清空 不会污染用户存储空间
     *
     * @param context 上下文
     * @param type    文件夹类型 可以为空，为空则返回API得到的一级目录
     * @return 缓存文件夹 如果没有SD卡或SD卡有问题则返回内存缓存目录，否则优先返回SD卡缓存目录
     */
    private static String getCacheDirectory(Context context, String type) {
        File appCacheDir = getExternalCacheDirectory(context, type);
        if (appCacheDir == null) {
            appCacheDir = getInternalCacheDirectory(context, type);
        }

        if (appCacheDir == null) {
            Log.e("getCacheDirectory", "getCacheDirectory fail ,the reason is mobile phone unknown exception !");
        } else {
            if (!appCacheDir.exists() && !appCacheDir.mkdirs()) {
                Log.e("getCacheDirectory", "getCacheDirectory fail ,the reason is make directory fail !");
            }
        }
        return appCacheDir.getAbsolutePath();
    }

    /**
     * 获取SD卡缓存目录
     *
     * @param context 上下文
     * @param type    文件夹类型 如果为空则返回 /storage/emulated/0/Android/data/app_package_name/cache
     *                否则返回对应类型的文件夹如Environment.DIRECTORY_PICTURES 对应的文件夹为 .../data/app_package_name/files/Pictures
     *                {@link Environment#DIRECTORY_MUSIC},
     *                {@link Environment#DIRECTORY_PODCASTS},
     *                {@link Environment#DIRECTORY_RINGTONES},
     *                {@link Environment#DIRECTORY_ALARMS},
     *                {@link Environment#DIRECTORY_NOTIFICATIONS},
     *                {@link Environment#DIRECTORY_PICTURES}, or
     *                {@link Environment#DIRECTORY_MOVIES}.or 自定义文件夹名称
     * @return 缓存目录文件夹 或 null（无SD卡或SD卡挂载失败）
     */
    private static File getExternalCacheDirectory(Context context, String type) {
        File appCacheDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            if (TextUtils.isEmpty(type)) {
                appCacheDir = context.getExternalCacheDir();
            } else {
                appCacheDir = context.getExternalFilesDir(type);
            }

            if (appCacheDir == null) {// 有些手机需要通过自定义目录
                appCacheDir = new File(Environment.getExternalStorageDirectory(), "Android/data/" + context.getPackageName() + "/cache/" + type);
            }

            if (appCacheDir == null) {
                Log.e("getExternalDirectory", "getExternalDirectory fail ,the reason is sdCard unknown exception !");
            } else {
                if (!appCacheDir.exists() && !appCacheDir.mkdirs()) {
                    Log.e("getExternalDirectory", "getExternalDirectory fail ,the reason is make directory fail !");
                }
            }
        } else {
            Log.e("getExternalDirectory", "getExternalDirectory fail ,the reason is sdCard nonexistence or sdCard mount fail !");
        }
        return appCacheDir;
    }

    /**
     * 获取内存缓存目录
     *
     * @param type 子目录，可以为空，为空直接返回一级目录
     * @return 缓存目录文件夹 或 null（创建目录文件失败）
     * 注：该方法获取的目录是能供当前应用自己使用，外部应用没有读写权限，如 系统相机应用
     */
    private static File getInternalCacheDirectory(Context context, String type) {
        File appCacheDir = null;
        if (TextUtils.isEmpty(type)) {
            appCacheDir = context.getCacheDir();// /data/data/app_package_name/cache
        } else {
            appCacheDir = new File(context.getFilesDir(), type);// /data/data/app_package_name/files/type
        }

        if (!appCacheDir.exists() && !appCacheDir.mkdirs()) {
            Log.e("getInternalDirectory", "getInternalDirectory fail ,the reason is make directory fail !");
        }
        return appCacheDir;
    }
}
