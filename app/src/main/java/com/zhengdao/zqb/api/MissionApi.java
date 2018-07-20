package com.zhengdao.zqb.api;

import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.NewBieHttpEntity;
import com.zhengdao.zqb.entity.ShareHttpEntity;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * @Author lijiangop
 * @CreateTime 2018/5/4 14:07
 */
public interface MissionApi {

    /**
     * 获取新手任务信息
     *
     * @param token
     * @return
     */
    @GET("newbieTask/getNewbieTask")
    Observable<NewBieHttpEntity> getNewHandData(@Query("token") String token);


    /**
     * 获取专属二维码
     *
     * @param token
     * @return
     */
    @GET("recommend/share")
    Observable<ShareHttpEntity> getCodeData(@Query("token") String token);

    /**
     * 签到
     *
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST("user/signIn")
    Observable<HttpResult> doDailySign(@Field("token") String token);

    /**
     * 获取分享奖励
     *
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST("recommend/shareFriend")
    Observable<HttpResult> getShareReward(@Field("token") String token);

}
