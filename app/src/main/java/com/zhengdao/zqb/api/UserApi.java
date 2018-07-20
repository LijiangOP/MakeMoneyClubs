package com.zhengdao.zqb.api;

import com.zhengdao.zqb.entity.ActivityCenterHttpEntity;
import com.zhengdao.zqb.entity.AttentionEntity;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.InvitedHttpEntity;
import com.zhengdao.zqb.entity.RelevanceAccountEntity;
import com.zhengdao.zqb.entity.ScanInfoEntity;
import com.zhengdao.zqb.entity.ShareHttpEntity;
import com.zhengdao.zqb.entity.SnatchRedPackeEntity;
import com.zhengdao.zqb.entity.UserAccountSafeBean;
import com.zhengdao.zqb.entity.UserHomeBean;
import com.zhengdao.zqb.entity.UserInfoBean;

import java.util.List;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * @Author lijiangop
 * @CreateTime 2018/1/3 10:48
 */
public interface UserApi {

    /**
     * 用户fragment页面的信息获取
     *
     * @param token
     * @return
     */
    @GET("userInfo/personalInfo")
    Observable<UserHomeBean> getUserHomeInfo(@Query("token") String token);

    /**
     * 獲取用戶个人信息
     *
     * @param token
     * @return
     */
    @GET("userInfo/getUserInfo")
    Observable<UserInfoBean> getUserInfo(@Query("token") String token);


    /**
     * 修改个人信息
     *
     * @param token
     * @param data  修改的数据（json格式）
     * @return
     */
    @FormUrlEncoded
    @POST("user/updateUserInfo")
    Observable<HttpResult> changeUserInfo(@Field("token") String token, @Field("data") String data);

    /**
     * 修改个人资料
     *
     * @param token
     * @param data  修改的数据（json格式）
     * @return
     */
    @FormUrlEncoded
    @POST("userSetting/updateUserInfo")
    Observable<HttpResult> changeUserIdentity(@Field("token") String token, @Field("data") String data);

    /**
     * 獲取用戶账号安全信息
     *
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST("changePsw")
    Observable<HttpResult<UserAccountSafeBean>> getUserAccountSafeInfo(@Field("token") String token);

    /**
     * 修改登录密码
     *
     * @param password
     * @return
     */
    @FormUrlEncoded
    @POST("changePsw")
    Observable<HttpResult> changeLoginPsw(@Field("password") String password);

    /**
     * 修改支付密码
     *
     * @param password
     * @return
     */
    @FormUrlEncoded
    @POST("changePsw")
    Observable<HttpResult> changePayPsw(@Field("password") String password);

    /**
     * 10.绑定新手机号码
     *
     * @param phone
     * @param code
     * @return
     */
    @FormUrlEncoded
    @POST("user/updateUserPhone")
    Observable<HttpResult> bindNewPhone(@Field("phone") String phone, @Field("code") String code, @Field("token") String token);

    /**
     * 11.验证支付密码
     *
     * @param token
     * @param pwd
     * @return
     */
    @FormUrlEncoded
    @POST("user/sendCode")
    Observable<HttpResult> CheckPayPsw(@Field("token") String token, @Field("pwd") String pwd);


    /**
     * 获取用户红包详情
     *
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST("get_reb_pocket")
    Observable<HttpResult> getRebPocketDetail(@Field("token") String token);


    /**
     * 取消关注
     *
     * @param token
     * @return
     */
    @GET("userInfo/cancelFollow")
    Observable<HttpResult> cancleAttention(@Query("token") String token, @Query("fid") int fid);

    /**
     * 17.查询用户收藏,关注，浏览信息
     *
     * @param token
     * @param flag  flagfollow（关注）  collection（收藏） browse（浏览）
     * @return
     */
    @GET("userInfo/footprint")
    Observable<HttpResult<ScanInfoEntity>> getScanInfo(@Query("token") String token, @Query("flag") String flag, @Query("pageNo") int currentPage);

    /**
     * 18.查询用户关注
     *
     * @param token
     * @param flag  flagfollow（关注）
     * @return
     */
    @GET("userInfo/footprint")
    Observable<AttentionEntity> getAttention(@Query("token") String token, @Query("flag") String flag, @Query("pageNo") int currentPage);

    /**
     * 获取活动中心数据
     *
     * @param pageNo
     * @return
     */
    @GET("find/activityCenter")
    Observable<ActivityCenterHttpEntity> getActivityCenterData(@Query("pageNo") int pageNo);

    /**
     * 获取抢红包数据
     *
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST("get_reb_pocket")
    Observable<HttpResult<SnatchRedPackeEntity>> getSnatchRedPacketData(@Field("token") String token, @Field("currentPage") int currentPage);

    /**
     * 意见反馈
     *
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST("feedback/opinion")
    Observable<HttpResult> feedbackOpinion(@Field("token") String token, @Field("type") String type,
                                           @Field("description") String description, @Field("userId") Long userId,
                                           @Field("subType") String subtype, @Field("picture") List<String> picture);

    /**
     * 用户提现
     *
     * @param token
     * @param type  提现方式(1.支付宝2.微信)
     * @param money
     * @return
     */
    @GET("withdraw/addWithdraw")
    Observable<HttpResult> withDraw(@Query("token") String token, @Query("type") String type,
                                    @Query("money") String money);

    /**
     * 18.删除浏览记录
     *
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST("userInfo/delBrowse")
    Observable<HttpResult> deleteHistory(@Field("token") String token, @Field("ids") List<Integer> ids);

    /**
     * 删除全部浏览记录
     *
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST("userInfo/delBrowse")
    Observable<HttpResult> deleteAllHistory(@Field("token") String token);

    /**
     * 获取邀请信息
     *
     * @param token
     * @return
     */
    @GET("recommend/recode")
    Observable<InvitedHttpEntity> getUserInvitedInfo(@Query("token") String token, @Query("currentPage") int currentPage);

    /**
     * 获取分享信息
     *
     * @param token
     * @return
     */
    @GET("recommend/share")
    Observable<ShareHttpEntity> getShareInfo(@Query("token") String token);

    /**
     * 获取账号关联信息
     *
     * @param token
     * @return
     */
    @GET("thirdpartylogin/getRelations")
    Observable<RelevanceAccountEntity> getRelations(@Query("token") String token);

    /**
     * 关联QQ,微信
     *
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST("thirdpartylogin/binding")
    Observable<HttpResult> bindAccount(@Field("token") String token, @Field("openid") String openId, @Field("type") int type);

    /**
     * 取消关联QQ,微信
     *
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST("thirdpartylogin/unBind")
    Observable<HttpResult> unBindAccount(@Field("token") String token, @Field("id") int id);

    /**
     * 激活卡券
     *
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST("recommend/couponsShareCallback")
    Observable<HttpResult> doActivateTicket(@Field("token") String token, @Field("cid") int cid);


    /**
     * 获取邀请好友奖励
     *
     * @return
     */
    @FormUrlEncoded
    @POST("recommend/invitationReward")
    Observable<HttpResult> getInvitedReward(@Field("inviteCode") String inviteCode, @Field("token") String token);

    /**
     * 获取点击广告奖励
     *
     * @return
     */
    @FormUrlEncoded
    @POST("user/clickAdvertReward")
    Observable<HttpResult> getSeeAdvReward(@Field("token") String token, @Field("address") Integer address, @Field("type") Integer type);

}
