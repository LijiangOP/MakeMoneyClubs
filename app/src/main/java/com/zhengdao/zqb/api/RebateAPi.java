package com.zhengdao.zqb.api;

import com.zhengdao.zqb.entity.HttpLiCaiDetailEntity;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.InvestRecordsHttpEntity;
import com.zhengdao.zqb.entity.PlatformHttpEntity;
import com.zhengdao.zqb.entity.RebateEntity;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * @Author lijiangop
 * @CreateTime 2018/6/13 15:01
 */
public interface RebateAPi {


    /**
     * 网赚列表
     *
     * @return
     */
    @GET("wangzhuan/getWangZhuanList")
    Observable<HttpResult<RebateEntity>> getRebateData(@Query("pageNo") int pageNo);


    /**
     * 网赚详情
     *
     * @param id
     * @return
     */
    @GET("wangzhuan/getWangzhuanDetails")
    Observable<HttpLiCaiDetailEntity> getLiCaiDetail(@Query("id") int id);


    /**
     * 获取投资平台列表
     *
     * @return
     */
    @GET("wangzhuan/getPlatformList")
    Observable<PlatformHttpEntity> getPlatformList();


    /**
     * 添加投资记录
     *
     * @param userToken
     * @param s         //投资姓名
     * @param s1        //投资电话
     * @param s2        //平台id
     * @param s3        //投资金额
     * @param s4        //投资周期
     * @param s5        //投资时间
     * @return
     */
    @FormUrlEncoded
    @POST("wangzhuan/addRebateRecord")
    Observable<HttpResult> addInvestItem(@Field("token") String userToken, @Field("investmentName") String s, @Field("investmentPhone") String s1,
                                         @Field("platform") String s2, @Field("amount") String s3, @Field("cycle") String s4,
                                         @Field("investmentTime") String s5, @Field("cycleCompany") String s6);


    /**
     * 获取投资记录
     *
     * @param token
     * @param pageNo
     * @return
     */
    @GET("wangzhuan/getRebateRecordList")
    Observable<InvestRecordsHttpEntity> getInvestRecord(@Query("token") String token, @Query("pageNo") int pageNo);


    /**
     * 删除投资记录
     *
     * @param token
     * @param id
     * @return
     */
    @FormUrlEncoded
    @POST("wangzuan/delWangzuanInfo")
    Observable<HttpResult> deleteItem(@Field("token") String token, @Field("id") int id);

}
