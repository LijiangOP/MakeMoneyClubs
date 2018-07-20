package com.zhengdao.zqb.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zhengdao.zqb.application.ClientAppLike;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.utils.AppUtils;
import com.zhengdao.zqb.utils.FileUtils;

import java.io.File;

/**
 * @Author lijiangop
 * @CreateTime 2018/7/2 10:22
 */
public class AppUpdateNotificationReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        AppUtils.install(ClientAppLike.getContext(), new File(FileUtils.getDownloadPath(Constant.Download.APK_FILE_NAME)));
    }
}
