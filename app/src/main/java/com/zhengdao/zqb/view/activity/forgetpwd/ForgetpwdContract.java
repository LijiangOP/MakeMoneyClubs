package com.zhengdao.zqb.view.activity.forgetpwd;

import com.zhengdao.zqb.entity.ConfirmCodeEntity;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

public class ForgetpwdContract {
    interface View extends BaseView {

        void getConfirmCodeSuccess();

        void getConfirmCodeResult(ConfirmCodeEntity httpResult);

        void onCheckConfirmCodeResult(HttpResult httpResult);
    }

    interface Presenter extends BasePresenter<View> {

        void getConfirmCode(String account);

        void checkConfirmCode(String account, String confirmCode);
    }
}
