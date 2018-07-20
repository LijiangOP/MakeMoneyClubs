package com.zhengdao.zqb.view.activity.bindnewphone;

import com.zhengdao.zqb.entity.ConfirmCodeEntity;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.UserInfo;
import com.zhengdao.zqb.entity.WechatBaseEntity;
import com.zhengdao.zqb.entity.WechatCheckEntity;
import com.zhengdao.zqb.entity.WechatUserEntity;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

public class BindNewPhoneContract {
    interface View extends BaseView {
        void getConfirmCodeSuccess();

        void onGetConfirmCodeResult(ConfirmCodeEntity httpResult);

        void onCheckConfirmCodeResult(HttpResult httpResult);

        void onBindThirdLoginDataOver(HttpResult httpResult);

        void onThirdLoginOver(HttpResult<UserInfo> httpResult);

        void loginSuccess(UserInfo bean);

        void onWechatcheckToken(WechatCheckEntity wechatCheckEntity);

        void onWechatRefreshToken(WechatBaseEntity WechatBaseEntity);

        void onGetWechatUserData(WechatUserEntity wechatUserEntity);
    }

    interface Presenter extends BasePresenter<View> {
        void getConfirmCode(String phone);

        void checkConfirmCode(String phone, String code);

        void refreshToken(String wechatAppid, String grant_type, String refresh_token);//微信

        void checkTokenAvailable(String access_token, String openid);//微信

        void getWechatUserData(String access_token, String openid);//微信

        void BindThirdLoginData(String phone, String nickname, String qqIcon, String gender, int channel, String openId, int loginTypeQq);//QQ

        void doThirdLogin(String openId);

        void saveUserInfo(UserInfo data);
    }
}
