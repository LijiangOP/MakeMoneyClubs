package com.zhengdao.zqb.view.activity.register;

import com.zhengdao.zqb.entity.ConfirmCodeEntity;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

public class RegisterContract {
    interface View extends BaseView {

        void getConfirmCodeSuccess();

        void onRegistFinished(HttpResult httpResult);

        void onGetConfirmCodeResult(ConfirmCodeEntity httpResult);
    }

    interface Presenter extends BasePresenter<View> {

        void getConfirmCode(String account);

        void doRegist(String account, String confirmCode, String recommend, int chanel);
    }
}
