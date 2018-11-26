package com.zhengdao.zqb.api;

import com.zhengdao.zqb.entity.BaiDuApiAdvEntity;
import com.zhengdao.zqb.entity.JinChengAdvHttpResult;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * @Author lijiangop
 * @CreateTime 2018/7/4 11:36
 */
public interface ADVApi {
    @GET("http://adapi.yiticm.com:7702/adv/dgfly")
    Observable<BaiDuApiAdvEntity> getBaiDuAdv(@Query("cp") String cp, @Query("pan") String pan, @Query("did") String did,
                                              @Query("imsi") String imsi, @Query("aid") String aid, @Query("mac") String mac,
                                              @Query("l") String l, @Query("action") String action, @Query("rel") String rel,
                                              @Query("brnd") String brnd, @Query("mdl") String mdl, @Query("dm") String dm,
                                              @Query("client_ip") String client_ip, @Query("client_ua") String client_ua, @Query("nt") String nt,
                                              @Query("no") String no, @Query("n") String n, @Query("an") String an);

    @Headers("Content-Type:application/json; charset=UTF-8")
    @POST("http://icm001.oicp.net:9999/105003")
    Observable<JinChengAdvHttpResult> getJinChengAdv(@Body RequestBody data);
}
