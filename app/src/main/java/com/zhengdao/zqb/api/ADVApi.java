package com.zhengdao.zqb.api;

import com.zhengdao.zqb.entity.BaiDuApiAdvEntity;

import retrofit2.http.GET;
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

}
