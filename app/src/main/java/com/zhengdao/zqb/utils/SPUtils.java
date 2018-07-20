package com.zhengdao.zqb.utils;

import android.content.Context;

import com.zhengdao.zqb.config.Constant;

import static com.zhengdao.zqb.config.Constant.SP.IS_LOGIN;
import static com.zhengdao.zqb.config.Constant.SP.PHONE_NUM;
import static com.zhengdao.zqb.config.Constant.SP.USER_TOKEN;

/**
 * @创建者 cairui
 * @创建时间 2016/11/22 10:00
 * @描述 ${des}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @版本 $Revision$
 * @更新描述 ${des}
 */
public class SPUtils {
    private static final String APPNAME = Constant.APP_NAME;

    public static boolean isLogin(Context context) {
        return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getBoolean(IS_LOGIN, false);
    }

    public static void setLoginState(Context context, boolean isLogin) {
        context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).edit().putBoolean(IS_LOGIN, isLogin).apply();
    }

    public static String getUserToken(Context context) {
        return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getString(USER_TOKEN, null);
    }

    public static void setUserToken(Context context, String token) {
        context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).edit().putString(USER_TOKEN, token).apply();
    }

    public static String getPhoneNum(Context context) {
        return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getString(PHONE_NUM, null);
    }

    public static void setPhoneNum(Context context, String phoneNum) {
        context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).edit().putString(PHONE_NUM, phoneNum).apply();
    }
}
