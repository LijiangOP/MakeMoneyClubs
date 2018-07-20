package com.zhengdao.zqb.application;

import android.app.Activity;
import android.app.Application;

import com.umeng.analytics.MobclickAgent;

import java.util.LinkedList;
import java.util.List;

/**
 * @Author lijiangop
 * @CreateTime 2017/7/24 09:22
 */
public class ExitApplication extends Application {
    private static ExitApplication instance;
    private List<Activity> activityList = new LinkedList<>();

    private ExitApplication() {
    }

    public static ExitApplication getInstance() {
        if (instance == null) {
            instance = new ExitApplication();
        }
        return instance;
    }

    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    public void exit() {
        if (activityList != null && !activityList.isEmpty())
            for (int i = 0; i < activityList.size(); i++) {
                activityList.get(i).finish();
            }
        MobclickAgent.onKillProcess(instance);
        System.exit(0);
    }
}
