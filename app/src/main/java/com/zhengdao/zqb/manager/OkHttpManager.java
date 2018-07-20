package com.zhengdao.zqb.manager;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * @Author lijiangop
 * @CreateTime 2018/7/5 09:47
 */
public class OkHttpManager {

    private static OkHttpClient mHelper;
    private static final Object monitor = new Object();
    private static Request.Builder mBuilder;

    public static OkHttpClient getInstance() {
        if (mHelper == null) {
            synchronized (monitor) {
                if (mHelper == null) {
                    mHelper = new OkHttpClient();
                }
            }
        }
        return mHelper;
    }

    public static Request.Builder get() {
        if (mBuilder == null)
            mBuilder = new Request.Builder();
        mBuilder.get();
        return mBuilder;
    }

}
