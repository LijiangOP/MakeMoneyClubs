package com.zhengdao.zqb.manager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.utils.NetWorkUtil;

import java.io.File;
import java.io.IOException;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @创建者 cairui
 * @创建时间 2016/11/22 15:29
 * @描述 Retrofit请求工具类
 * @更新者 $Author$
 * @更新时间 $Date$
 * @版本 $Revision$
 * @更新描述 ${des}
 */
public class RetrofitManager {

    private final  OkHttpClient     mCachedClient;
    private final  OkHttpClient     mNoCacheClient;
    private final  Retrofit.Builder mBuilder;
    private static Context          sContext;
    private static final String                       CACHE_CONTROL  = "Cache-Control";
    private static final Object                       monitor        = new Object();
    //日志显示级别 可以在app的中放在统一的类中管理
    public static final  HttpLoggingInterceptor.Level HTTP_LOG_LEVEL = HttpLoggingInterceptor.Level.BODY;
    private static File httpCacheDirectory;
    private static int cacheSize = 10 * 1024 * 1024; // 10 MiB
    private static Cache cache;
    private             StringBuffer mBuffer  = new StringBuffer();
    private             boolean      useCache = true;
    public static final String       TAG      = "lijiangop";

    private RetrofitManager() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {

            @Override
            public void log(String message) {
                mBuffer.append(message).append("\n");
                if (message.contains("http")) {
                    Log.e(TAG, mBuffer.toString());
                    mBuffer.setLength(0);
                }
            }
        });
        loggingInterceptor.setLevel(HTTP_LOG_LEVEL);
        //不添加离线缓存无效
        httpCacheDirectory = new File(sContext.getCacheDir(), "AppCache");
        cache = new Cache(httpCacheDirectory, cacheSize);
        mCachedClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(cacheControlInterceptor)
                .addInterceptor(cacheControlInterceptor)//不添加离线缓存无效
                .addInterceptor(loggingInterceptor)
                .cache(cache)
                .build();

        mNoCacheClient = new OkHttpClient().newBuilder()
                .addInterceptor(loggingInterceptor)
                .build();

        mBuilder = new Retrofit.Builder()
                .baseUrl(Constant.Url.BASEURL)
                .client(mCachedClient) //设置自定义的OkHttpClient
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())//通过RxJavaCallAdapterFactory为Retrofit添加RxJava支持(对call的转换);
                .addConverterFactory(GsonConverterFactory.create());//通过GsonConverterFactory为Retrofit2添加Gson支持(转换数据类型);
        ;
    }

    public OkHttpClient getOkHttpClient() {
        return mCachedClient;
    }

    //传入application的context
    public static void init(Context context) {
        sContext = context;
    }

    private Interceptor cacheControlInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());
            if (NetWorkUtil.isNetWorkAvailable(sContext)) {
                int maxAge = 60; // 在线缓存在多久内可读取
                return originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .removeHeader(CACHE_CONTROL)
                        .header(CACHE_CONTROL, "public, max-age=" + maxAge)
                        .build();
            } else {
                int maxStale = 60 * 60 * 24 * 28; // 离线时缓存保存4周
                return originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .removeHeader(CACHE_CONTROL)
                        .header(CACHE_CONTROL, "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
        }
    };

    private static RetrofitManager mHelper;

    public static RetrofitManager getInstance() {
        if (mHelper == null) {
            synchronized (monitor) {
                if (mHelper == null) {
                    mHelper = new RetrofitManager();
                }
            }
        }
        return mHelper;
    }

    public RetrofitManager noCache() {
        useCache = false;
        mBuilder.client(mNoCacheClient);
        return this;
    }


    public RetrofitManager baseUrl(@NonNull String baseUrl) {
        mBuilder.baseUrl(baseUrl);
        return this;
    }

    public <T> T create(final Class<T> service) {
        if (useCache) {
            mBuilder.client(mCachedClient);
        }
        useCache = true;//重置使用缓存
        return mBuilder.build().create(service);
    }
}
