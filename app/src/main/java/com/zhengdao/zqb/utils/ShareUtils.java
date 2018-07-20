package com.zhengdao.zqb.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;

import static com.zhengdao.zqb.application.ClientAppLike.QQ_APPID;
import static com.zhengdao.zqb.application.ClientAppLike.WECHAT_APPID;

/**
 * @Author cairui
 * @CreateTime 2017/6/8 10:52
 */
public class ShareUtils {

    public static void shareToWX(Context context, String url, String title, String desc, int bimmapRes, int scene) {
        try {
            IWXAPI iwxapi = WXAPIFactory.createWXAPI(context,WECHAT_APPID, false);
            iwxapi.registerApp(WECHAT_APPID);

            WXWebpageObject webpage = new WXWebpageObject();
            webpage.webpageUrl = url;

            WXMediaMessage msg = new WXMediaMessage(webpage);
            msg.title = title;
            msg.description = desc;
            Bitmap thumb = BitmapFactory.decodeResource(context.getResources(), bimmapRes);
            msg.thumbData = WxUtil.bmpToByteArray(thumb, true);

            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = WxUtil.buildTransaction("webpage");
            req.message = msg;
            req.scene = scene;
            iwxapi.sendReq(req);
        } catch (Exception ex) {
            LogUtils.e(ex.getMessage());
        }
    }

    public static void shareToQQ(Activity activity, Bundle bundle, IUiListener listener) {
        try {
            Tencent tencent = Tencent.createInstance(QQ_APPID, activity);
            tencent.shareToQQ(activity, bundle, listener);
        } catch (Exception ex) {
            LogUtils.e(ex.getMessage());
        }
    }

    public static void shareToQZone(Activity activity, Bundle bundle, IUiListener listener) {
        try {
            Tencent tencent = Tencent.createInstance(QQ_APPID, activity);
            tencent.shareToQzone(activity, bundle, listener);
        } catch (Exception ex) {
            LogUtils.e(ex.getMessage());
        }
    }


    public static void shareToWXWithImg(Context context, Bitmap bitmap, String transaction) {
        try {
            IWXAPI iwxapi = WXAPIFactory.createWXAPI(context, WECHAT_APPID, false);
            iwxapi.registerApp(WECHAT_APPID);

            WXImageObject wxImageObject = new WXImageObject(bitmap);
            WXMediaMessage msg = new WXMediaMessage();
            msg.mediaObject = wxImageObject;

            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = transaction;
            req.message = msg;
            req.scene = SendMessageToWX.Req.WXSceneTimeline;
            iwxapi.sendReq(req);
        } catch (Exception ex) {
            LogUtils.e(ex.getMessage());
        }
    }
}
