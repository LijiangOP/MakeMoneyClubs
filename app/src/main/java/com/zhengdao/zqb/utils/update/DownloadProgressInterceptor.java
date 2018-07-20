package com.zhengdao.zqb.utils.update;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * @创建者 cairui
 * @创建时间 2017/2/16 16:13
 */
public class DownloadProgressInterceptor implements Interceptor {

    private DownloadProgressListener listener;

    public DownloadProgressInterceptor(DownloadProgressListener listener) {
        this.listener = listener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());

        return originalResponse.newBuilder()
                .body(new DownloadProgressResponseBody(originalResponse.body(), listener))
                .build();
    }
}
