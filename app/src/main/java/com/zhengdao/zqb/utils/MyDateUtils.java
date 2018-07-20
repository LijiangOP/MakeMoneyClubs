package com.zhengdao.zqb.utils;

/**
 * @Author lijiangop
 * @CreateTime 2018/4/13 10:54
 */
public class MyDateUtils {

    /**
     * 悬赏详情里小时转换成年月日
     *
     * @param ms
     * @return
     */
    public static String formatTime(Long ms) {
        Long year;
        Long month;
        Long day;
        Long hour;
        Integer dd = 24;
        Integer mm = 24 * 30;
        Integer yy = 24 * 30 * 12;
        if (ms > dd * 3) {
            if (ms > yy) {//大于一年
                year = ms / yy;
                month = (ms - year * yy) / mm;
                day = (ms - year * yy - month * mm) / dd;
                hour = ms - -year * yy - month * mm - day * dd;
            } else if (ms > mm) {//大于一个月
                year = 0l;
                month = ms / mm;
                day = (ms - month * mm) / dd;
                hour = ms - month * mm - day * dd;
            } else {//大于72小时
                year = 0l;
                month = 0l;
                day = ms / dd;
                hour = ms - day * dd;
            }
        } else {
            year = 0l;
            month = 0l;
            day = 0l;
            hour = ms;
        }
        StringBuffer sb = new StringBuffer();
        if (year > 0) {
            sb.append(year + "年");
        }
        if (month > 0) {
            sb.append(month + "月");
        }
        if (day > 0) {
            sb.append(day + "天");
        }
        if (hour > 0) {
            sb.append(hour + "小时");
        }
        return sb.toString();
    }

    /**
     * 毫秒转换成年月日分秒
     *
     * @param ms
     * @return
     */
    public static String formatTimeMin(Long ms) {
        Integer ss = 1000;
        Integer mi = ss * 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;

        Long day = ms / dd;
        Long hour = (ms - day * dd) / hh;
        Long minute = (ms - day * dd - hour * hh) / mi;
        Long second = (ms - day * dd - hour * hh - minute * mi) / ss;

        StringBuffer sb = new StringBuffer();
        if (day > 0) {
            sb.append(day + "天");
        }
        if (hour > 0) {
            sb.append(hour + "小时");
        }
        if (minute > 0) {
            sb.append(minute + "分");
        }
        if (second > 0) {
            sb.append(second + "秒");
        }
        return sb.toString();
    }

    public static String HomeformatTime(Long ms) {
        Integer ss = 1000;//1秒
        Integer mi = ss * 60;//1分钟
        Integer hh = mi * 30;//30分钟
        StringBuffer sb = new StringBuffer();
        if (ms < mi) {
            sb.append(ms / ss + "秒");
        } else if (ms > mi && ms < hh) {
            sb.append(ms / mi + "分钟");
        } else {
            sb.append("30分钟");
        }
        return sb.toString();
    }
}
