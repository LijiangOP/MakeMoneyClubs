package com.zhengdao.zqb.api;

import com.zhengdao.zqb.entity.ConfirmCodeEntity;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.UserInfo;
import com.zhengdao.zqb.entity.WechatBaseEntity;
import com.zhengdao.zqb.entity.WechatCheckEntity;
import com.zhengdao.zqb.entity.WechatUserEntity;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * @Author lijiangop
 * @CreateTime 2018/2/1 11:26
 */
public interface LogInApi {

    /**
     * 获取验证码
     *
     * @param phone
     * @return
     */
    @FormUrlEncoded
    @POST("user/sendCode")
    Observable<ConfirmCodeEntity> getConfirmCode(@Field("phone") String phone);

    /**
     * 验证验证码
     *
     * @param phone
     * @return
     */
    @FormUrlEncoded
    @POST("user/checkSmsCode")
    Observable<HttpResult> CheckConfirmCode(@Field("phone") String phone, @Field("code") String code);

    /**
     * 注册检查
     *
     * @param phone
     * @param code
     * @param inviter
     * @return
     */
    @FormUrlEncoded
    @POST("user/register")
    Observable<HttpResult> doCheck(@Field("phone") String phone, @Field("code") String code, @Field("inviter") String inviter, @Field("refferee") int refferee);

    /**
     * 注册
     *
     * @param token
     * @param pwd
     * @param pwdStrength
     * @return
     */
    @FormUrlEncoded
    @POST("user/userPwd")
    Observable<HttpResult<UserInfo>> doRegist(@Field("token") String token, @Field("pwd") String pwd, @Field("pwdStrength") String pwdStrength);

    /**
     * 修改密码
     *
     * @param account
     * @param confirmCode
     * @return
     */
    @FormUrlEncoded
    @POST("changePsw")
    Observable<HttpResult> doChangePassword(@Field("account") String account, @Field("confirmCode") String confirmCode);


    /**
     * 登录
     *
     * @param userName
     * @param password
     * @return
     */
    @FormUrlEncoded
    @POST("user/login")
    Observable<HttpResult<UserInfo>> login(@Field("userName") String userName, @Field("pwd") String password);

    /**
     * 第三方登录数据绑定
     *
     * @param nickName
     * @param avatar
     * @param sex
     * @param appid
     * @param type
     * @return
     */
    @FormUrlEncoded
    @POST("thirdpartylogin/loginBinding")
    Observable<HttpResult> bindThirdLoginData(@Field("phone") String phone, @Field("nickName") String nickName, @Field("avatar") String avatar,
                                              @Field("sex") String sex, @Field("refferee") int refferee, @Field("appid") String appid, @Field("type") int type);

    /**
     * 第三方登录
     *
     * @param appid
     * @return
     */
    @FormUrlEncoded
    @POST("thirdpartylogin/login")
    Observable<HttpResult<UserInfo>> thirdLogin(@Field("appid") String appid);


    /**
     * 微信登录，获取用户微信数据
     *
     * @param wechatAppid
     * @param secret
     * @param code
     * @param authorization_code
     * @return
     */
    @GET("https://api.weixin.qq.com/sns/oauth2/access_token")
    Observable<WechatBaseEntity> getWechatBaseData(@Query("appid") String wechatAppid, @Query("secret") String secret, @Query("code") String code,
                                                   @Query("grant_type") String authorization_code);

    /**
     * 微信登录，刷新token有效期
     *
     * @param wechatAppid
     * @param type
     * @param token
     * @return
     */
    @GET("https://api.weixin.qq.com/sns/oauth2/refresh_token")
    Observable<WechatBaseEntity> refreshToken(@Query("appid") String wechatAppid, @Query("grant_type") String type, @Query("refresh_token") String token);


    /**
     * 微信登录，检查token是否有效
     *
     * @param access_token
     * @param openid
     * @return
     */
    @GET("https://api.weixin.qq.com/sns/auth")
    Observable<WechatCheckEntity> checkTokenAvailable(@Query("access_token") String access_token, @Query("openid") String openid);


    /**
     * 微信登录，获取用户个人信息
     *
     * @param access_token
     * @param openid
     * @return
     */
    @GET("https://api.weixin.qq.com/sns/userinfo")
    Observable<WechatUserEntity> getWechatUserData(@Query("access_token") String access_token, @Query("openid") String openid);

}
