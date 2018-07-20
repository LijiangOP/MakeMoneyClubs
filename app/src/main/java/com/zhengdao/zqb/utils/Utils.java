package com.zhengdao.zqb.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.text.TextUtils;

import com.zhengdao.zqb.view.activity.login.LoginActivity;


/**
 * @Author lijiangop
 * @CreateTime 2017/7/19 09:52
 */
public class Utils {

    /**
     * 判断SDCard是否存在
     *
     * @return 返回判断结果
     */
    public static boolean isSdCardExist() {
        String externalStorageState = Environment.getExternalStorageState();
        if (!TextUtils.isEmpty(externalStorageState) && externalStorageState.equals(Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    /**
     * 获取SDCard绝对路径
     *
     * @return 返回绝对路径
     */
    public static String getSdCardPath() {
        String sdDir = null;
        if (isSdCardExist())
            sdDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        return sdDir;
    }

    /**
     * 带登陆判断的StartActivity
     */
    public static void StartActivity(Context context, Intent intent) {
        if (SettingUtils.isLogin(context))
            context.startActivity(intent);
        else
            context.startActivity(new Intent(context, LoginActivity.class));
    }
}
