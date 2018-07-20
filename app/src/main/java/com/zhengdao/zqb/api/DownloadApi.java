package com.zhengdao.zqb.api;

import com.zhengdao.zqb.entity.UpdateInfoEntity;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * @Author lijiangop
 * @CreateTime 2017/12/28 18:08
 */
public interface DownloadApi {

    @GET("version/checkUpdate")
    Observable<UpdateInfoEntity> getUpdateInfo(@Query("insideVersion") int insideVersion);

    @Streaming
    @GET
    Observable<ResponseBody> download(@Url String url);
}
