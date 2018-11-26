package com.zhengdao.zqb.utils;

import android.content.Context;
import android.text.TextUtils;

import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.UserHomeBean;
import com.zhengdao.zqb.entity.UserInfo;

import static com.zhengdao.zqb.config.Constant.SP.ACCOUNT;
import static com.zhengdao.zqb.config.Constant.SP.ACCOUNTTYPE;
import static com.zhengdao.zqb.config.Constant.SP.ALIPAYACCOUNT;
import static com.zhengdao.zqb.config.Constant.SP.CURRENTCALENDAR;
import static com.zhengdao.zqb.config.Constant.SP.CURRENT_ALIPAY_CALENDAR;
import static com.zhengdao.zqb.config.Constant.SP.CURRENT_INTRODUCE_CALENDAR;
import static com.zhengdao.zqb.config.Constant.SP.CURRENT_RECOMMEND_REWARD_CALENDAR;
import static com.zhengdao.zqb.config.Constant.SP.IGNORE;
import static com.zhengdao.zqb.config.Constant.SP.IMG_SETTING;
import static com.zhengdao.zqb.config.Constant.SP.IS_FRIST_INSTALL;
import static com.zhengdao.zqb.config.Constant.SP.IS_LOGIN;
import static com.zhengdao.zqb.config.Constant.SP.MESSAGECOUNT;
import static com.zhengdao.zqb.config.Constant.SP.PHONE_NUM;
import static com.zhengdao.zqb.config.Constant.SP.RECEIVE_COUNT;
import static com.zhengdao.zqb.config.Constant.SP.REWARDMESSAGECOUNT;
import static com.zhengdao.zqb.config.Constant.SP.TOTAL_INCOME;
import static com.zhengdao.zqb.config.Constant.SP.USER_ADDRESS_SET;
import static com.zhengdao.zqb.config.Constant.SP.USER_BIRTHDAY;
import static com.zhengdao.zqb.config.Constant.SP.USER_ID;
import static com.zhengdao.zqb.config.Constant.SP.USER_SEX;
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
public class SettingUtils {
    private static final String APPNAME = Constant.APP_NAME;

    /**
     * 判断是否只在wifi下加载图片
     *
     * @param context
     * @return
     */

    public static boolean isFristInstall(Context context) {
        return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getBoolean(IS_FRIST_INSTALL, true);
    }

    public static void setFristInstall(Context context, boolean isFristInstall) {
        context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).edit().putBoolean(IS_FRIST_INSTALL, isFristInstall).apply();
    }

    public static boolean getOnlyWifiLoadImg(Context context) {
        return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getBoolean(IMG_SETTING, false);
    }

    public static void setOnlyWifiLoadImg(Context context, boolean flag) {
        context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).edit().putBoolean(IMG_SETTING, flag).apply();
    }

    public static float getTotalIncome(Context context) {
        return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getFloat(TOTAL_INCOME, 0);
    }

    public static void setTotalIncome(Context context, float token) {
        context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).edit().putFloat(TOTAL_INCOME, token).apply();
    }

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

    public static String getUserID(Context context) {
        return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getString(USER_ID, null);
    }

    public static void setUserID(Context context, String userId) {
        context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).edit().putString(USER_ID, userId).apply();
    }

    public static String getPhoneNum(Context context) {
        return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getString(PHONE_NUM, null);
    }

    public static void setPhoneNum(Context context, String phoneNum) {
        context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).edit().putString(PHONE_NUM, phoneNum).apply();
    }

    public static void setAccount(Context context, String account) {
        context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).edit().putString(ACCOUNT, account).apply();
    }

    public static String getAccount(Context context) {
        return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getString(ACCOUNT, "");
    }

    public static void setIgnoreVersion(Context context, String insideVersion) {
        context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).edit().putString(IGNORE, insideVersion).apply();
    }

    public static String getIgnoreVersion(Context context) {
        return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getString(IGNORE, "");
    }

    public static void setAlipayAccount(Context context, String account) {
        context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).edit().putString(ALIPAYACCOUNT, account).apply();
    }

    public static String getAlipayAccount(Context context) {
        return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getString(ALIPAYACCOUNT, "");
    }

    public static void setMessageCount(Context context, int account) {
        context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).edit().putInt(MESSAGECOUNT, account).apply();
    }

    public static int getMessageCount(Context context) {
        return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getInt(MESSAGECOUNT, 0);
    }

    public static void setRewardMessageCount(Context context, int account) {
        context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).edit().putInt(REWARDMESSAGECOUNT, account).apply();
    }

    public static int getRewardMessageCount(Context context) {
        return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getInt(REWARDMESSAGECOUNT, 0);
    }

    public static void setAccountType(Context context, int type) {
        context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).edit().putInt(ACCOUNTTYPE, type).apply();
    }

    public static int getAccountType(Context context) {
        return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getInt(ACCOUNTTYPE, 0);
    }

    public static void setLastShowDate(Context context, int currentDay) {
        context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).edit().putInt(CURRENTCALENDAR, currentDay).apply();
    }

    public static int getLastShowDat(Context context) {
        return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getInt(CURRENTCALENDAR, 0);
    }

    public static void setAliPayLastShowDate(Context context, int currentDay) {
        context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).edit().putInt(CURRENT_ALIPAY_CALENDAR, currentDay).apply();
    }

    public static int getAliuPayLastShowDat(Context context) {
        return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getInt(CURRENT_ALIPAY_CALENDAR, 0);
    }

    public static void setRecommendRewardLastShowDate(Context context, int currentDay) {
        context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).edit().putInt(CURRENT_RECOMMEND_REWARD_CALENDAR, currentDay).apply();
    }

    public static int getRecommendRewardLastShowDat(Context context) {
        return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getInt(CURRENT_RECOMMEND_REWARD_CALENDAR, 0);
    }

    public static void setIntroduceLastShowDate(Context context, int currentDay) {
        context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).edit().putInt(CURRENT_INTRODUCE_CALENDAR, currentDay).apply();
    }

    public static int getIntroduceLastShowDat(Context context) {
        return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getInt(CURRENT_INTRODUCE_CALENDAR, 0);
    }

    public static int getReceiveCount(Context context) {
        return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getInt(RECEIVE_COUNT, 0);
    }

    public static void setReceiveCount(Context context, int type) {
        context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).edit().putInt(RECEIVE_COUNT, type).apply();
    }


    private static void setAddressSet(Context context, int addressSet) {
        context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).edit().putInt(USER_ADDRESS_SET, addressSet).apply();
    }

    public static int getAddressSet(Context context) {
        return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getInt(USER_ADDRESS_SET, 0);
    }

    private static void setSex(Context context, int sex) {
        context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).edit().putInt(USER_SEX, sex).apply();
    }

    public static int getSex(Context context) {
        return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getInt(USER_SEX, 0);
    }

    private static void setBirthday(Context context, String birthday) {
        context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).edit().putString(USER_BIRTHDAY, birthday).apply();
    }

    public static String getBirthday(Context context) {
        return context.getSharedPreferences(APPNAME, Context.MODE_PRIVATE).getString(USER_BIRTHDAY, "");
    }


    public static void SaveAfterLogin(Context context, UserInfo bean) {
        if (context != null && bean != null) {
            SettingUtils.setLoginState(context, true);
            SettingUtils.setPhoneNum(context, bean.phone);
            SettingUtils.setUserToken(context, bean.token);
            SettingUtils.setUserID(context, "" + bean.id);
            SettingUtils.setAccount(context, bean.userName);
            SettingUtils.setAccountType(context, bean.type);
        }
    }

    public static void ClearAfterLogOut(Context context) {
        if (context != null) {
            SettingUtils.setLoginState(context, false);
            SettingUtils.setPhoneNum(context, "");
            SettingUtils.setUserToken(context, "");
            SettingUtils.setUserID(context, "0");
            SettingUtils.setAccount(context, "");
            SettingUtils.setAlipayAccount(context, "");
        }
    }

    public static void SaveAfterGetUSerData(Context context, UserHomeBean bean) {
        if (bean != null && bean.userInfo != null) {
            SettingUtils.setAlipayAccount(context, bean.userInfo.zfb);
            SettingUtils.setReceiveCount(context, bean.userInfo.receiveCount);
            SettingUtils.setBirthday(context, bean.userInfo.birthday);
            SettingUtils.setSex(context, bean.userInfo.sex);
            SettingUtils.setAddressSet(context, bean.userInfo.addressSet);

        }
    }

    public static boolean IsAttributeAllExit(Context context) {
        int addressSet = getAddressSet(context);
        int sex = getSex(context);
        String birthday = getBirthday(context);
        if (addressSet == 0 || sex == 0 || TextUtils.isEmpty(birthday))
            return false;
        else
            return true;
    }
}
