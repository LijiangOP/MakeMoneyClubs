package com.zhengdao.zqb.view.activity.changepsw;

import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

public class ChangePswContract {
    interface View extends BaseView {
        void onChangeLoginPswResult(HttpResult httpResult);

        void onChangePayPswResult(HttpResult httpResult);
    }

    interface Presenter extends BasePresenter<View> {

        void changeLoginPsw(String token, String psw);

        void changePayPsw(String token, String psw);
    }
}
