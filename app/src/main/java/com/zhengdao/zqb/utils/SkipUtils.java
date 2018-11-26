package com.zhengdao.zqb.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.tbruyelle.rxpermissions.RxPermissions;
import com.xianwan.sdklibrary.util.XWUtils;
import com.youle.androidsdk.utils.YLUtils;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.view.activity.login.LoginActivity;
import com.zhengdao.zqb.view.activity.questionsurvery.QuestionSurveryActivty;
import com.zhengdao.zqb.view.activity.webview.WebViewActivity;
import com.zhengdao.zqb.view.activity.welfareget.WelfareGetActivity;
import com.zhengdao.zqb.view.activity.xiangwan.XiangWanActivity;

import rx.functions.Action1;

import static com.zhengdao.zqb.application.ClientAppLike.AppType;

/**
 * @Author lijiangop
 * @CreateTime 2018/10/19 0019 14:54
 */
public class SkipUtils {

    public static void SkipToShouTu(Context context) {
        Intent shoutu = new Intent(context, WebViewActivity.class);
        shoutu.putExtra(Constant.WebView.TITLE, "邀请有礼");
        shoutu.putExtra(Constant.WebView.URL, Constant.IP.Invited + "/?token=" + SettingUtils.getUserToken(context));
        Utils.StartActivity(context, shoutu);
    }

    /**
     * @param context
     * @param welfareState 1:未完成
     */
    public static void SkipToChouJiang(Context context, int welfareState) {
        if (SettingUtils.isLogin(context) && SettingUtils.IsAttributeAllExit(context)) {
            Intent welfare = new Intent(context, WelfareGetActivity.class);
            welfare.putExtra(Constant.Activity.Data, welfareState);
            Utils.StartActivity(context, welfare);
        } else {
            Intent survery = new Intent(context, QuestionSurveryActivty.class);
            survery.putExtra(Constant.Activity.Data, welfareState);
            survery.putExtra(Constant.Activity.Skip, "welfare");
            Utils.StartActivity(context, survery);
        }
    }

    public static void SkipGame(int type, Activity activity) {
        switch (type) {
            case 2://闲玩游戏
                SkipToXianWan(activity);
                break;
            case 7://有乐游戏
                YLUtils.getInstance(activity).jumpToAd();
                break;
            case 8://享玩游戏
                SkipToXiangWan(activity);
                break;
        }
    }

    public static void SkipToXiangWan(final Activity activity) {
        if (SettingUtils.isLogin(activity)) {
            RxPermissions rxPermissions = new RxPermissions(activity);
            rxPermissions.request(Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Action1<Boolean>() {
                @Override
                public void call(Boolean aBoolean) {
                    TelephonyManager tm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
                    @SuppressLint("MissingPermission")
                    String imei = tm.getDeviceId();//设备唯一标识(imei)
                    String userid = SettingUtils.getUserID(activity);
                    String ptype = "2";
                    String pid = Constant.XiangWan.PID;
                    String appkey = Constant.XiangWan.APP_KEY;
                    String keycode = pid + imei + ptype + userid + appkey;
                    keycode = MD5Utils.String2MD5(keycode).toLowerCase();

                    String url = Constant.XiangWan.BASE_URL + "userid=" + userid + "&deviceid=" + imei + "&ptype=" + ptype
                            + "&pid=" + pid + "&keycode=" + keycode;
                    LogUtils.e(url);

                    Intent intent = new Intent(activity, XiangWanActivity.class);
                    intent.putExtra(Constant.WebView.URL, url);
                    activity.startActivity(intent);
                }
            });
        } else {
            activity.startActivity(new Intent(activity, LoginActivity.class));
        }
    }

    public static void SkipToYouLe(Context context) {
        YLUtils.getInstance(context).jumpToAd();
    }

    public static void SkipToXianWan(Context context) {
        String userID = SettingUtils.getUserID(context);
        if (SettingUtils.isLogin(context) && !TextUtils.isEmpty(userID)) {
            try {
                XWUtils.getInstance(context).init(AppType == Constant.App.Zqb ? Constant.XianWan.Appid : Constant.XianWan.Appid1,
                        AppType == Constant.App.Zqb ? Constant.XianWan.AppSecret : Constant.XianWan.AppSecret1, userID);
                XWUtils.getInstance(context).setTitleBGColorString("#fc3135");
                XWUtils.getInstance(context).setTitle("闲玩");
                XWUtils.getInstance(context).jumpToAd();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            context.startActivity(new Intent(context, LoginActivity.class));
        }
    }

    public static void SkipToH5WithToken(String title, String url, Context context) {
        if (SettingUtils.isLogin(context)) {
            Intent intent = new Intent(context, WebViewActivity.class);
            intent.putExtra(Constant.WebView.TITLE, title);
            intent.putExtra(Constant.WebView.URL, url + "/?token=" + SettingUtils.getUserToken(context));
            context.startActivity(intent);
        } else {
            context.startActivity(new Intent(context, LoginActivity.class));
        }
    }
}
