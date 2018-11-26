package com.zhengdao.zqb.api;

import com.zhengdao.zqb.entity.DictionaryHttpEntity;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.KindOfWantedDetailHttpEntity;
import com.zhengdao.zqb.entity.KindOfWantedHttpEntity;
import com.zhengdao.zqb.entity.ManagementWantedHttpEntity;
import com.zhengdao.zqb.entity.MyWantedEntity;
import com.zhengdao.zqb.entity.RewardManagerHttpEntity;
import com.zhengdao.zqb.entity.WantedDetailHttpEntity;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * @Author lijiangop
 * @CreateTime 2018/2/1 11:23
 */
public interface WantedApi {

    /**
     * 获取悬赏
     *
     * @param token
     * @return
     */
    @GET("myReward/myRewardList")
    Observable<MyWantedEntity> getWanted(@Query("pageNo") int pageNo, @Query("state") int state, @Query("token") String token);

    /**
     * 审核提醒
     *
     * @param token
     * @return
     */
    @GET("message/reminders")
    Observable<HttpResult> remindCheck(@Query("token") String token, @Query("id") int id);

    /**
     * 取消任务
     *
     * @param token
     * @return
     */
    @GET("myReward/cancelTask")
    Observable<HttpResult> cancleMission(@Query("token") String token, @Query("tid") int id);

    /**
     * 悬赏详情
     *
     * @param id
     * @return
     */
    @GET("myReward/taskDetails")
    Observable<WantedDetailHttpEntity> getWantedDetailInfo(@Query("id") int id);

    /**
     * 悬赏详情
     *
     * @param token
     * @return
     */
    @GET("reward/rewardManager")
    Observable<RewardManagerHttpEntity> getRewardManagerInfo(@Query("token") String token);

    /**
     * 获取悬赏管理的条目信息
     *
     * @param token
     * @return
     */
    @GET("reward/releaseList")
    Observable<ManagementWantedHttpEntity> getManagementWanted(@Query("pageNo") int pageNo, @Query("state") int state, @Query("token") String token);

    /**
     * 申请下架
     *
     * @param token
     * @return
     */
    @GET("reward/applyShelf")
    Observable<HttpResult> cancleWanted(@Query("token") String token, @Query("id") int id);

    /**
     * 悬赏审核列表
     *
     * @param pageNo 当前页码
     * @param state  不通过 = 2 未提交 = 0 已提交 = 1(默认)
     * @param flag   flag=1 是否急需审核
     * @param token
     * @return
     */
    @GET("reward/rewardAudit")
    Observable<KindOfWantedHttpEntity> getWantedList(@Query("pageNo") int pageNo, @Query("state") int state,
                                                     @Query("flag") String flag, @Query("token") String token);

    /**
     * 33.审核详情
     *
     * @param id
     * @return
     */
    @GET("reward/auditDetails")
    Observable<KindOfWantedDetailHttpEntity> getWantedListDetail(@Query("id") int id);


    /**
     * 30.悬赏主审核
     *
     * @param flag
     * @param taskId
     * @param rwId
     * @param remarks
     * @param taskUserId
     * @return
     */
    @FormUrlEncoded
    @POST("reward/examine")
    Observable<HttpResult> ConfirmCheck(@Field("token") String token, @Field("flag") String flag, @Field("taskId") int taskId,
                                        @Field("rwId") int rwId, @Field("remarks") String remarks, @Field("taskUserId") int taskUserId);

    /**
     * 发布&保存悬赏
     *
     * @param token
     * @param data  修改的数据（json格式）
     * @return
     */
    @FormUrlEncoded
    @POST("reward/saveAndSubmitReward")
    Observable<HttpResult<String>> saveAndSubmitReward(@Field("token") String token, @Field("platform") int platform, @Field("data") String data);


    /**
     * 字典接口
     *
     * @param key
     * @return
     */
    @GET("dictionary/getTypesByKey")
    Observable<DictionaryHttpEntity> getTypesByKey(@Query("key") String key);
}
