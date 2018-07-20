package com.zhengdao.zqb.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import com.zhengdao.zqb.utils.AppUtils;

import java.util.ArrayList;

/**
 * @Author lijiangop
 * @CreateTime 2018/5/22 16:59
 */
public class WatchDogService extends Service {

    private static final String MYTAG = "007";
    private ActivityManager mActivityManager;
    private boolean           flag          = true;// 线程退出的标记
    private MyBinder          myBinder      = new MyBinder();
    private ArrayList<String> mPackageLists = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (flag) {
                    synchronized (WatchDogService.class) {
                        try {
                            String runningApp = AppUtils.getRunningApp(WatchDogService.this);
                            Log.i(MYTAG, runningApp);

                            //                            List<ActivityManager.RunningServiceInfo> mRunningService = mActivityManager.getRunningServices(1000);
                            //                            for (ActivityManager.RunningServiceInfo amService : mRunningService) {
                            //                                Log.i(MYTAG, amService.service.getPackageName());
                            //                                if (mPackageLists.contains(amService.service.getPackageName())) {
                            //                                    LogUtils.i("在后台运行了" + amService.service.getPackageName());
                            //                                    mPackageLists.remove(amService.service.getPackageName());
                            //                                    break;
                            //                                }
                            //                            }
                        } catch (SecurityException e) {
                            e.printStackTrace();
                        }
                        SystemClock.sleep(500);
                    }
                }
            }
        }.start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        flag = false;
    }

    public interface IMyBinder {
        void setPackageNames(ArrayList<String> packageNames);
    }

    public class MyBinder extends Binder implements IMyBinder {

        @Override
        public void setPackageNames(ArrayList<String> packageNames) {
            mPackageLists.clear();
            mPackageLists = packageNames;
        }
    }


}
