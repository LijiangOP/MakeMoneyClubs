package com.zhengdao.zqb.wxapi;

import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.UserInfo;
import com.zhengdao.zqb.entity.WechatBaseEntity;
import com.zhengdao.zqb.entity.WechatCheckEntity;
import com.zhengdao.zqb.entity.WechatUserEntity;

/**
 * @Author lijiangop
 * @CreateTime 2018/4/23 15:16
 */
public class WXEntryContract {

    interface View {

        void showProgress();

        void hideProgress();

        void showErrorMessage(String message);

        void onGetWechatBaseData(WechatBaseEntity WechatBaseEntity,String state);

        void onWechatcheckToken(WechatCheckEntity wechatCheckEntity);

        void onWechatRefreshToken(WechatBaseEntity WechatBaseEntity);

        void onGetWechatUserData(WechatUserEntity wechatUserEntity);

        void onThirdLoginOver(HttpResult<UserInfo> httpResult);

        void loginSuccess(UserInfo bean);

        void onAccountBindResult(HttpResult result,int type);
    }

    interface Presenter {

        void getWechatBaseData(String wechatAppid, String secret, String code, String authorization_code,String state);

        void refreshToken(String wechatAppid, String grant_type, String refresh_token);

        void checkTokenAvailable(String access_token, String openid);

        void getWechatUserData(String access_token, String openid);

        void doThirdLogin(String openId);

        void saveUserInfo(final UserInfo bean);

        void doAccountBind(String openId, int type);
    }
}
