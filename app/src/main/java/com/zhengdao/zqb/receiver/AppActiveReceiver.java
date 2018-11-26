package com.zhengdao.zqb.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.zhengdao.zqb.event.ApkInstallEvent;
import com.zhengdao.zqb.utils.RxBus;

/**
 * @Author lijiangop
 * @CreateTime 2018/5/22 11:18
 */
public class AppActiveReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (intent != null) {
                String action = intent.getAction(); //区分接收到的是哪种广播
                Uri uri = intent.getData();  //获取广播中包含的应用包名
                if (!TextUtils.isEmpty(action) && action.equals("android.intent.action.PACKAGE_ADDED")) {
                    String packageName = uri.toString();
                    if (!TextUtils.isEmpty(packageName)) {
                        if (packageName.contains("package:")) {
                            packageName = packageName.replace("package:", "");
                        }
                        RxBus.getDefault().post(new ApkInstallEvent(packageName));
                    }
                    System.out.println(uri + "被安装了");
                } else if (!TextUtils.isEmpty(action) && action.equals("android.intent.action.PACKAGE_REPLACED")) {
                    System.out.println(uri + "被更新了");
                } else if (!TextUtils.isEmpty(action) && action.equals("android.intent.action.PACKAGE_REMOVED")) {
                    System.out.println(uri + "被卸载了");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
