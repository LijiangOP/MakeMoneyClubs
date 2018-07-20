package com.zhengdao.zqb.utils;

import android.content.Context;

import com.zhengdao.zqb.config.Constant;

import static com.zhengdao.zqb.config.Constant.SP.REFRESH_TOKEN;
import static com.zhengdao.zqb.config.Constant.SP.WECHAT_OPEN_ID;
import static com.zhengdao.zqb.config.Constant.SP.WECHAT_TOKEN;

/**
 * @Author lijiangop
 * @CreateTime 2018/4/24 10:54
 */
public class WechatUtils {

    public static String getWechatRefreshToken(Context context) {
        return context.getSharedPreferences(Constant.APP_NAME, Context.MODE_PRIVATE).getString(REFRESH_TOKEN, null);
    }

    public static void setWechatRefreshToken(Context context, String token) {
        context.getSharedPreferences(Constant.APP_NAME, Context.MODE_PRIVATE).edit().putString(REFRESH_TOKEN, token).apply();
    }

    public static String getWechatAccessToken(Context context) {
        return context.getSharedPreferences(Constant.APP_NAME, Context.MODE_PRIVATE).getString(WECHAT_TOKEN, null);
    }

    public static void setWechatAccessToken(Context context, String token) {
        context.getSharedPreferences(Constant.APP_NAME, Context.MODE_PRIVATE).edit().putString(WECHAT_TOKEN, token).apply();
    }

    public static String getWechatOpenId(Context context) {
        return context.getSharedPreferences(Constant.APP_NAME, Context.MODE_PRIVATE).getString(WECHAT_OPEN_ID, null);
    }

    public static void setWechatOpenId(Context context, String token) {
        context.getSharedPreferences(Constant.APP_NAME, Context.MODE_PRIVATE).edit().putString(WECHAT_OPEN_ID, token).apply();
    }
}
