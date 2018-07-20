package com.zhengdao.zqb.utils;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.zhengdao.zqb.api.ADVApi;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.BaiDuApiAdvEntity;
import com.zhengdao.zqb.manager.OkHttpManager;
import com.zhengdao.zqb.manager.RetrofitManager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @Author lijiangop
 * @CreateTime 2018/7/4 11:25
 */
public class BaiDuAPiAdvUtils {

    public interface BaiDuApiAdvListener {
        void onAdvGet(BaiDuApiAdvEntity baiDuApiAdvEntity);

        void onAdvFail();
    }

    public interface DownloadDataListener {
        void onDataGet(String responseStr);

    }

    public static void getAdv(Context context, final BaiDuApiAdvListener listener) throws UnsupportedEncodingException {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String IMEI = TextUtils.isEmpty(tm.getDeviceId()) ? "" : tm.getDeviceId();//IMEI号
        String subscriberId = TextUtils.isEmpty(tm.getSubscriberId()) ? "" : tm.getSubscriberId();//imsi号
        String ANDROID_ID = Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);//ANDROIDID - 安卓ID
        String mac = PhoneUtils.getWifiMac(context);//设备wifi网卡MAC地址
        String systemVersion = TextUtils.isEmpty(PhoneUtils.getSystemVersion()) ? "" : PhoneUtils.getSystemVersion();//操作系统版本
        String phoneBrand = TextUtils.isEmpty(PhoneUtils.getDeviceBrand()) ? "" : PhoneUtils.getDeviceBrand();
        phoneBrand = new String(phoneBrand.getBytes(), "UTF-8");//设备厂商名称
        String phoneModel = TextUtils.isEmpty(PhoneUtils.getSystemModel()) ? "" : PhoneUtils.getSystemModel();
        phoneModel = new String(phoneModel.getBytes(), "UTF-8");//设备型号
        int[] screenSize = DensityUtil.getScreenSize(context);//设备屏幕宽高
        String localIpAddress = PhoneUtils.getLocalIpAddress();
        localIpAddress = TextUtils.isEmpty(localIpAddress) ? "" : localIpAddress;//用户客户端IP
        String userAgent = PhoneUtils.getUserAgent();//用户客户端的user-Agent
        String networkState = PhoneUtils.getNetworkState(context);//网络连接类型
        String operatorName = PhoneUtils.getOperatorName(context);//手机运营商代号
        String netType = PhoneUtils.getNetType(context);//网络类型
        String[] strings = {Constant.APIADV.ACCOUNT, "api644tang", IMEI, subscriberId, ANDROID_ID, mac, "zh", "us", systemVersion, phoneBrand, phoneModel,
                screenSize[0] + "x" + screenSize[1], localIpAddress, userAgent, networkState, operatorName, netType, context.getPackageName()
        };
        RetrofitManager.getInstance().noCache().create(ADVApi.class)
                .getBaiDuAdv(strings[0], strings[1], strings[2], strings[3], strings[4], strings[5], strings[6], strings[7], strings[8],
                        strings[9], strings[10], strings[11], strings[12], strings[13], strings[14], strings[15], strings[16], strings[17])
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BaiDuApiAdvEntity>() {
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
                    public void onNext(BaiDuApiAdvEntity baiDuApiAdvEntity) {
                        if (null != listener)
                            listener.onAdvGet(baiDuApiAdvEntity);
                    }
                });
    }

    /**
     * 广告的上报
     *
     * @param value
     */
    public static void ReportAdv(String value) {
        Log.d("banana", "上报信息=" + value);
        OkHttpManager.getInstance().newCall(OkHttpManager.get().url(value).build()).enqueue(new Callback() {
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

    /**
     * 请求url获取下载及其他数据
     *
     * @param down_url
     */
    public static void getDownloadData(String down_url, final DownloadDataListener listener) {
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

}
