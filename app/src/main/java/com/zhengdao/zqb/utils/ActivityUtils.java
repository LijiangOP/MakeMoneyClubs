package com.zhengdao.zqb.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.view.activity.activitycenter.ActivityCenterActivity;
import com.zhengdao.zqb.view.activity.dailysign.DailySignActivity;
import com.zhengdao.zqb.view.activity.homegoodsdetail.HomeGoodsDetailActivity;
import com.zhengdao.zqb.view.activity.login.LoginActivity;
import com.zhengdao.zqb.view.activity.main.MainActivity;
import com.zhengdao.zqb.view.activity.newhandmission.NewHandMissionActivity;
import com.zhengdao.zqb.view.activity.rewardticket.RewardTicketActivity;
import com.zhengdao.zqb.view.activity.webview.WebViewActivity;

import static com.zhengdao.zqb.config.Constant.Skip.Type_Eight;
import static com.zhengdao.zqb.config.Constant.Skip.Type_Five;
import static com.zhengdao.zqb.config.Constant.Skip.Type_Four;
import static com.zhengdao.zqb.config.Constant.Skip.Type_None;
import static com.zhengdao.zqb.config.Constant.Skip.Type_One;
import static com.zhengdao.zqb.config.Constant.Skip.Type_Seven;
import static com.zhengdao.zqb.config.Constant.Skip.Type_Six;
import static com.zhengdao.zqb.config.Constant.Skip.Type_Three;
import static com.zhengdao.zqb.config.Constant.Skip.Type_Tow;
import static com.zhengdao.zqb.config.Constant.Skip.Type_Zero;

/**
 * @Author lijiangop
 * @CreateTime 2018/5/14 10:38
 */
public class ActivityUtils {
    public static void doSkip(Context context, int type, String url, int id) {
        try {
            switch (type) {
                case Type_Zero:
                    if (!TextUtils.isEmpty(url)) {
                        Intent intent_zero = new Intent(context, WebViewActivity.class);
                        intent_zero.putExtra(Constant.WebView.TITLE, "详情");
                        intent_zero.putExtra(Constant.WebView.URL, url);
                        Utils.StartActivity(context, intent_zero);
                    }
                    break;
                case Type_One:
                    Utils.StartActivity(context, new Intent(context, NewHandMissionActivity.class));
                    break;
                case Type_Tow:
                    try {
                        MainActivity activity = (MainActivity) context;
                        activity.setCurrentPosition(1);
                    } catch (Exception ex) {
                        LogUtils.e(ex.getMessage());
                    }
                    break;
                case Type_Three:
                    Intent intent_three;
                    if (SettingUtils.isLogin(context)) {
                        intent_three = new Intent(context, WebViewActivity.class);
                        intent_three.putExtra(Constant.WebView.TITLE, "邀请有礼");
                        intent_three.putExtra(Constant.WebView.URL, Constant.IP.Invited + "/?token=" + SettingUtils.getUserToken(context));
                    } else {
                        intent_three = new Intent(context, LoginActivity.class);
                    }
                    Utils.StartActivity(context, intent_three);
                    break;
                case Type_Four:
                    if (!TextUtils.isEmpty(url)) {
                        Uri uri = Uri.parse(url);
                        Intent four = new Intent(Intent.ACTION_VIEW, uri);
                        context.startActivity(four);
                    }
                    break;
                case Type_Five:
                    Intent intent_five;
                    if (SettingUtils.isLogin(context)) {
                        intent_five = new Intent(context, HomeGoodsDetailActivity.class);
                        Integer integer = new Integer(url);
                        intent_five.putExtra(Constant.Activity.Data, integer);
                    } else {
                        intent_five = new Intent(context, LoginActivity.class);
                    }
                    context.startActivity(intent_five);
                    break;
                case Type_Six:
                    Intent intent_six = new Intent(context, DailySignActivity.class);
                    context.startActivity(intent_six);
                    break;
                case Type_Seven:
                    Intent intent_seven = new Intent(context, ActivityCenterActivity.class);
                    context.startActivity(intent_seven);
                    break;
                case Type_Eight:
                    Intent intent_eight = new Intent(context, RewardTicketActivity.class);
                    context.startActivity(intent_eight);
                    break;
                case Type_None:
                default:
                    LogUtils.i("不做跳转");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
