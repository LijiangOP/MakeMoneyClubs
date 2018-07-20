package com.zhengdao.zqb.receiver;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.view.activity.main.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * @Author lijiangop
 * @CreateTime 2017/11/14 16:42
 */
public class JPushReceiver extends BroadcastReceiver {
    private static final String TAG = JPushReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        try {


            if (intent != null) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {//接受到推送下来的自定义消息
                        Log.i(TAG, "接收到了自定义消息");
                        //检测是否开启权限,并跳转到设置页面 非常影响用户体验
                        //            if (!NotificationUtils.isNotificationEnabled(context)) {
                        //                NotificationUtils.toSetting(context);
                        //            }
                        processCustomMessage(context, bundle);
                    } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {// 接受到推送下来的通知
                        Log.i(TAG, "接收到了通知");
                        //检测是否开启权限,并跳转到设置页面 非常影响用户体验
                        //            if (!NotificationUtils.isNotificationEnabled(context)){
                        //                NotificationUtils.toSetting(context);
                        //            }
                        //极光自带通知，别用
                    } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {//用户点击打开了通知
                        if (!isActivityTop(MainActivity.class, context)) {
                            Intent myintent = new Intent(context, MainActivity.class);
                            myintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(myintent);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processCustomMessage(Context context, Bundle bundle) {
        try {
            if (bundle != null) {
                String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
                String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
                Log.i(TAG, "内容：【" + message + "】，附加参数:【" + extra + "】");
                String title = null;
                try {
                    if (!TextUtils.isEmpty(extra)) {
                        JSONObject jsonObject = new JSONObject(extra);
                        title = jsonObject.getString("title");//标题
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (TextUtils.isEmpty(title))
                    title = context.getResources().getString(R.string.app_name);
                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                //通知栏构造器,创建通知栏样式
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
                //点击后跳转的intent
                Intent broadcastIntent = new Intent(context, NotificationReceiver.class);
                // 将来意图，用于点击通知之后的操作,内部的new intent()可用于跳转等操作
                @SuppressLint("WrongConstant") PendingIntent mPendingIntent = PendingIntent.getBroadcast(context, 1, broadcastIntent, Notification.FLAG_AUTO_CANCEL);
                //设置通知栏标题
                NotificationCompat.Builder builder = mBuilder
                        .setContentTitle(title)
                        //设置通知栏显示内容
                        .setContentText(message)
                        //设置通知栏点击意图
                        .setContentIntent(mPendingIntent)
                        //通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                        .setWhen(System.currentTimeMillis())
                        //设置该通知优先级
                        .setPriority(Notification.PRIORITY_DEFAULT)
                        //设置这个标志当用户单击面板就可以让通知将自动取消
                        .setAutoCancel(true)
                        //使用当前的用户默认设置
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        //设置通知小ICON(应用默认图标)
                        .setSmallIcon(R.drawable.icon_small)
                        //设置通知大ICON(通知栏下拉后显示的图标)
                        //                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon))
                        ;
                mNotificationManager.notify(001, mBuilder.build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isActivityTop(Class cls, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String name = manager.getRunningTasks(1).get(0).topActivity.getClassName();
        return name.equals(cls.getName());
    }
}
