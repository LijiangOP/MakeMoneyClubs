package com.zhengdao.zqb.view.activity.login;

import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.UserInfo;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

public class LoginContract {
    interface View extends BaseView {
        void onLoginOver(HttpResult<UserInfo> result);

        void loginSuccess(UserInfo bean);

        void onThirdLoginOver(HttpResult<UserInfo> httpResult);
    }

    interface Presenter extends BasePresenter<View> {

        void login(String account, String password);

        void saveUserInfo(UserInfo data);

        void doThirdLogin(String openId);
    }
}
