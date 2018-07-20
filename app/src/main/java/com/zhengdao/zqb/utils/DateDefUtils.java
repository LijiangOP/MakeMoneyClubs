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
}
