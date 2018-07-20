package com.zhengdao.zqb.api;

import com.zhengdao.zqb.entity.AdvHttpEntity;
import com.zhengdao.zqb.entity.BalanceEntity;
import com.zhengdao.zqb.entity.BalanceHttpEntity;
import com.zhengdao.zqb.entity.BarValueEntity;
import com.zhengdao.zqb.entity.ChargeRecordEntity;
import com.zhengdao.zqb.entity.CustomHttpEntity;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.IntegralEntity;
import com.zhengdao.zqb.entity.IntegralHttpEntity;
import com.zhengdao.zqb.entity.PhoneChargeEntity;
import com.zhengdao.zqb.entity.RebPocketEntity;
import com.zhengdao.zqb.entity.RewardTicketHttpEntity;
import com.zhengdao.zqb.entity.WalletHttpEntity;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * @Author lijiangop
 * @CreateTime 2018/2/1 16:44
 */
public interface WalletApi {

    /**
     * 我的钱包
     *
     * @param token
     * @return
     */
    @GET("myWallet/index")
    Observable<WalletHttpEntity> getWalletInfo(@Query("token") String token);


    /**
     * 获取用户账户流水
     *
     * @param fundMode 类型(-1 全部 3.悬赏收入 4.推荐奖励5.提现 7.广告收入 8.理财返利)
     * @return
     */
    @GET("record/fundRecord")
    Observable<BalanceHttpEntity<BalanceEntity>> getBalance(@Query("fundMode") int fundMode,
                                                            @Query("token") String token, @Query("pageNo") int pageNo);

    /**
     * 获取用户账户红包
     *
     * @param date
     * @return
     */
    @FormUrlEncoded
    @POST("get_balance")
    Observable<HttpResult<RebPocketEntity>> getRebPocket(@Field("date") String date);

    /**
     * 获取用户账户吧值
     *
     * @param date
     * @return
     */
    @FormUrlEncoded
    @POST("get_balance")
    Observable<HttpResult<BarValueEntity>> getBarValue(@Field("date") String date);

    /**
     * 获取用户账户积分
     *
     * @param date
     * @return
     */
    @GET("record/integraRecord")
    Observable<IntegralHttpEntity<IntegralEntity>> getIntegral(@Query("date") String date,
                                                               @Query("token") String token, @Query("pageNo") int pageNo);

    /**
     * 获取用户推广佣金
     *
     * @param date
     * @return
     */
    @GET("record/fundRecord")
    Observable<BalanceHttpEntity<BalanceEntity>> getBrokerage(@Query("time") String date, @Query("flag") String flag,
                                                              @Query("token") String token, @Query("pageNo") int pageNo);

    /**
     * 获取用户充值记录
     *
     * @param date
     * @return
     */
    @GET("payOder/rechargeRecord")
    Observable<HttpResult<ChargeRecordEntity>> getChargeRecord(@Query("date") String date,
                                                               @Query("token") String token, @Query("pageNo") int pageNo);

    /**
     * 获取手机充值数据
     *
     * @return
     */
    @GET("payOder/rechargeRecord")
    Observable<PhoneChargeEntity> getPhoneChargeInfo();

    /**
     * 39.支付宝充值
     *
     * @param token
     * @param amount
     * @return
     */
    @FormUrlEncoded
    @POST("pay/goAliPay")
    Observable<HttpResult<String>> doAlipayCharge(@Field("token") String token, @Field("amount") String amount);

    /**
     * 手机充值
     *
     * @return
     */
    @GET("payOder/rechargeRecord")
    Observable<HttpResult> doPhoneCharge(@Query("token") String token, @Query("chargeType") int chargeType, @Query("number") int number);


    /**
     * 获取客服中心数据
     *
     * @return
     */
    @GET("dictionary/customerService")
    Observable<CustomHttpEntity> getCustomData();

    /**
     * 赏金券列表
     *
     * @return
     */
    @GET("userInfo/getCouponsList")
    Observable<RewardTicketHttpEntity> getTicketData(@Query("token") String token, @Query("state") int state, @Query("pageNo") int pageNo);


    /**
     * 获取广告中心奖励数据
     *
     * @param pageNo
     * @return
     */
    @GET("record/advertRecord")
    Observable<AdvHttpEntity> getAdvData(@Query("token") String token, @Query("pageNo") int pageNo);
}
