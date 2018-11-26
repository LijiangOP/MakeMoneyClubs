package com.zhengdao.zqb.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.webkit.WebSettings;

import com.google.gson.Gson;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.api.ADVApi;
import com.zhengdao.zqb.entity.JinChengAdvHttpResult;
import com.zhengdao.zqb.entity.JinChengReQuestEntity;
import com.zhengdao.zqb.entity.TiEntity;
import com.zhengdao.zqb.manager.OkHttpManager;
import com.zhengdao.zqb.manager.RetrofitManager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @Author lijiangop
 * @CreateTime 2018/11/21 0021 11:32
 */
public class JinChengUtils {

    public interface JinChengApiAdvListener {
        void onAdvGet(JinChengAdvHttpResult entity);

        void onAdvFail();
    }

    public interface DownloadDataListener {
        void onDataGet(String responseStr);

    }

    public static void getAdv(Context context, final JinChengApiAdvListener listener) throws UnsupportedEncodingException {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String IMEI = TextUtils.isEmpty(tm.getDeviceId()) ? "" : tm.getDeviceId();//IMEI号
        @SuppressLint("MissingPermission") String subscriberId = TextUtils.isEmpty(tm.getSubscriberId()) ? "" : tm.getSubscriberId();//imsi号
        String ANDROID_ID = Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);//ANDROIDID - 安卓ID
        String mac = PhoneUtils.getWifiMac(context);//设备wifi网卡MAC地址
        String systemVersion = TextUtils.isEmpty(PhoneUtils.getSystemVersion()) ? "" : PhoneUtils.getSystemVersion();//操作系统版本
        String phoneBrand = TextUtils.isEmpty(PhoneUtils.getDeviceBrand()) ? "" : PhoneUtils.getDeviceBrand();
        phoneBrand = new String(phoneBrand.getBytes(), "UTF-8");//设备厂商名称
        String devicebuildfactoryname = TextUtils.isEmpty(PhoneUtils.getDeviceBuildFactoryName()) ? "" : PhoneUtils.getDeviceBuildFactoryName();
        devicebuildfactoryname = new String(devicebuildfactoryname.getBytes(), "UTF-8");//获取设备制造商
        String phoneModel = TextUtils.isEmpty(PhoneUtils.getSystemModel()) ? "" : PhoneUtils.getSystemModel();
        phoneModel = new String(phoneModel.getBytes(), "UTF-8");//设备型号
        int[] screenSize = DensityUtil.getScreenSize(context);//设备屏幕宽高
        String localIpAddress = PhoneUtils.getLocalIpAddress();
        localIpAddress = TextUtils.isEmpty(localIpAddress) ? "" : localIpAddress;//用户客户端IP
        String userAgent = PhoneUtils.getUserAgent();//用户客户端的user-Agent
        String netType = PhoneUtils.getJinChengNetworkState(context);//网络类型
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        JinChengReQuestEntity reQuestEntity = new JinChengReQuestEntity(1, "1.0.0",
                MD5Utils.md5(String.valueOf(System.currentTimeMillis())), "A114301100001", 7, 50, 320,
                new TiEntity(phoneBrand, devicebuildfactoryname, phoneModel, 0, systemVersion, screenSize[0], screenSize[1], "p1143@zhengdao",
                        IMEI, subscriberId, netType, mac, ANDROID_ID, localIpAddress, userAgent, displayMetrics.densityDpi,
                        context.getPackageName(), URLEncoder.encode(context.getString(R.string.app_name), "UTF-8")));
        Gson gson = new Gson();
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), gson.toJson(reQuestEntity));

        RetrofitManager.getInstance().noCache().create(ADVApi.class)
                .getJinChengAdv(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JinChengAdvHttpResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e(e.getMessage());
                        if (null != listener)
                            listener.onAdvFail();
                    }

                    @Override
                    public void onNext(JinChengAdvHttpResult result) {
                        if (null != listener)
                            listener.onAdvGet(result);
                    }
                });
    }


    /**
     * 广告的上报
     *
     * @param value
     */
    public static void ReportAdv(Context context, ArrayList<String> value) {
        if (value != null && value.size() > 0) {
            for (String url : value) {
                Log.d("banana", "上报信息=" + value);
                Request request = new Request.Builder().url(url).removeHeader("User-Agent").addHeader("User-Agent", getUserAgent(context)).build();
                OkHttpManager.getInstance().newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        LogUtils.e(e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String responseStr = response.body().string();
                        LogUtils.d(responseStr);
                    }
                });
            }
        }
    }

    /**
     * 广告的上报
     *
     * @param value
     */
    public static void ReportAdv(Context context, String value) {
        if (!TextUtils.isEmpty(value)) {
            Log.d("banana", "上报信息=" + value);
            Request request = new Request.Builder().url(value).removeHeader("User-Agent").addHeader("User-Agent", getUserAgent(context)).build();
            OkHttpManager.getInstance().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    LogUtils.e(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String responseStr = response.body().string();
                    LogUtils.d(responseStr);
                }
            });
        }
    }

    /**
     * 请求url获取下载及其他数据
     *
     * @param down_url
     */
    public static void getDownloadData(String down_url, final BaiDuAPiAdvUtils.DownloadDataListener listener) {
        OkHttpManager.getInstance().newCall(OkHttpManager.get().url(down_url).build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtils.e(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseStr = response.body().string();
                if (null != listener)
                    listener.onDataGet(responseStr);
            }
        });
    }

    private static String getUserAgent(Context context) {
        String userAgent = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                userAgent = WebSettings.getDefaultUserAgent(context);
            } catch (Exception e) {
                userAgent = System.getProperty("http.agent");
            }
        } else {
            userAgent = System.getProperty("http.agent");
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0, length = userAgent.length(); i < length; i++) {
            char c = userAgent.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                sb.append(String.format("\\u%04x", (int) c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

}
