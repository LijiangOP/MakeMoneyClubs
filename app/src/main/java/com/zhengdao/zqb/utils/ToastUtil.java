package com.zhengdao.zqb.utils;

import android.content.Context;
import android.content.res.Resources;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @创建者 lijiangop
 * @创建时间 2016/11/28 18:04
 * @描述 ${des}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @版本 $Revision$
 * @更新描述 ${des}
 */
public class ToastUtil {
    private static String oldMsg;
    private static Toast toast   = null;
    private static long  oneTime = 0;
    private static long  twoTime = 0;

    public static void showToast(Context context, int resId) {
        showToast(context, context.getString(resId));
    }

    public static void showToast(Context context, int resId, int gravity) {
        showToast(context, context.getString(resId), gravity, 0, 0);
    }

    public static void showToast(Context context, String s, int gravity) {
        showToast(context, s, gravity, 0, 0);
    }

    public static void showToast(Context context, int resId, int gravity, int offX, int offY) {
        showToast(context, context.getString(resId), gravity, offX, offY);
    }

    public static void showToast(Context context, String s) {
        if (toast == null) {
            toast = Toast.makeText(context.getApplicationContext(), s, Toast.LENGTH_SHORT);

            toast.show();
            oneTime = System.currentTimeMillis();
        } else {
            twoTime = System.currentTimeMillis();
            if (s.equals(oldMsg)) {
                if (twoTime - oneTime > Toast.LENGTH_SHORT) {
                    toast.show();
                }
            } else {
                oldMsg = s;
                toast.setText(s);
                toast.show();
            }
        }
        oneTime = twoTime;
    }


    public static void showToast(Context context, String s, int gravity, int offX, int offY) {
        if (toast == null) {
            toast = Toast.makeText(context, s, Toast.LENGTH_SHORT);
            toast.setGravity(gravity, offX, offY);
            toast.show();
            oneTime = System.currentTimeMillis();
        } else {
            twoTime = System.currentTimeMillis();
            if (s.equals(oldMsg)) {
                if (twoTime - oneTime > Toast.LENGTH_SHORT) {
                    toast.show();
                }
            } else {
                oldMsg = s;
                toast.setText(s);
                toast.show();
            }
        }
        oneTime = twoTime;
    }

    private static int textview_id;

    public static void showToastWithGravity(Context context, String str, int gravity) {
        if (toast == null)
            toast = Toast.makeText(context.getApplicationContext(), str, Toast.LENGTH_SHORT);
        else
            toast.setText(str);
        if (textview_id == 0)
            textview_id = Resources.getSystem().getIdentifier("message", "id", "android");
        ((TextView) toast.getView().findViewById(textview_id)).setGravity(gravity);
        toast.show();
    }
}
