package com.zhengdao.zqb.application;

import android.annotation.TargetApi;
import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.StrictMode;
import android.support.multidex.MultiDex;
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
import com.tencent.bugly.beta.Beta;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;
import com.tencent.tinker.loader.app.DefaultApplicationLike;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.receiver.NetworkConnectChangedReceiver;
import com.zhengdao.zqb.utils.LogUtils;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;


/**
 * @Author lijiangop
 * @CreateTime 2017/9/15 18:11
 */
public class ClientAppLike extends DefaultApplicationLike {

    public static String                        WECHAT_APPID;
    public static String                        WECHAT_SECRET_ID;
    public static String                        QQ_APPID;
    public static Tencent                       mTencent;
    public static Context                       context;
    public static IWXAPI                        mWxApi;
    public        NetworkConnectChangedReceiver mReceiver;
    public static int                           AppType;
    private String[] mStrings = new String[]{Constant.ADVIEW.KeySet};

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
        RetrofitManager.init(getApplication().getApplicationContext());
        initMultipleApp();
        initNetWorkListener();
        initTencent();
        initJPush();
        initTaobaoAppLink();
        //        initStrikeMode();//TODO Release版本关掉
        initSmartRefreshLayout();
    }

    private void initMultipleApp() {
        String packageName = getApplication().getPackageName();
        if (!TextUtils.isEmpty(packageName) && packageName.equals("com.wlg.wlgmall")) {//玩乐购返利
            AppType = Constant.App.Wlgfl;
            WECHAT_APPID = "wxde190ac2e913d0b6";
            WECHAT_SECRET_ID = "b5a583be719680768dbd3dcfde69c49b";
            QQ_APPID = "101430001";
        } else {//赚钱吧
            AppType = Constant.App.Zqb;
            WECHAT_APPID = "wx08fef6b801503f47";
            WECHAT_SECRET_ID = "c34fe497c26175d1aa394a4300e09c33";
            QQ_APPID = "101464836";
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
        Beta.installTinker(this);
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
}
