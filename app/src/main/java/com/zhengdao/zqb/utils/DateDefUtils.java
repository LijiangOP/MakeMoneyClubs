package com.zhengdao.zqb.utils;

import android.content.Context;

import java.util.Calendar;

/**
 * @Author lijiangop
 * @CreateTime 2018/5/11 09:43
 */
public class DateDefUtils {

    public static boolean isAfter(Context context) {

        int dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);

        int lastShowDat = SettingUtils.getLastShowDat(context);

        if (dayOfYear > lastShowDat) {
            SettingUtils.setLastShowDate(context, dayOfYear);
            return true;
        } else {
            return false;
        }
    }

    public static boolean isCanShowRedPacketToDay(Context context) {

        int dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);

        int lastShowDat = SettingUtils.getAliuPayLastShowDat(context);

        if (dayOfYear > lastShowDat) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isCanShowRecommendRewardToDay(Context context) {

        int dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);

        int lastShowDat = SettingUtils.getRecommendRewardLastShowDat(context);

        if (dayOfYear > lastShowDat) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isCanShowIntroduceToDay(Context context) {

        int dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);

        int lastShowDat = SettingUtils.getIntroduceLastShowDat(context);

        if (dayOfYear > lastShowDat) {
            return true;
        } else {
            return false;
        }
    }
}
