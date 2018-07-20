package com.zhengdao.zqb.utils;

import android.app.Activity;
import android.text.TextUtils;

import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;

import org.json.JSONObject;

import static com.zhengdao.zqb.application.ClientAppLike.mTencent;

/**
 * @Author lijiangop
 * @CreateTime 2018/3/8 11:41
 */
public class LoginUtils {

    /**
     * QQ登录第一步：获取token和openid
     */
    public static void loginQQ(Activity activity, IUiListener loginListener) {
        if (mTencent == null) {
            LogUtils.e("QQ登录初始化失败");
            return;
        }
        /** 判断是否登陆过 */
        if (!mTencent.isSessionValid()) {
            mTencent.login(activity, "all", loginListener);
        }/** 登陆过注销之后在登录 */
        else {
            mTencent.logout(activity);
            mTencent.login(activity, "all", loginListener);
        }
    }

    /**
     * QQ登录第二步：存储token和openid
     */
    public static void initOpenidAndToken(JSONObject jsonObject) {
        if (mTencent == null) {
            LogUtils.e("QQ登录初始化失败");
            return;
        }
        try {
            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires) && !TextUtils.isEmpty(openId)) {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);
            }
        } catch (Exception e) {
        }
    }
}
