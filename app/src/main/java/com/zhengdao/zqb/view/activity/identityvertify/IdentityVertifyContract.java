package com.zhengdao.zqb.view.activity.identityvertify;

import com.zhengdao.zqb.entity.ConfirmCodeEntity;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

public class IdentityVertifyContract {
    interface View extends BaseView {

        void getConfirmCodeSuccess();

        void onGetConfirmCodeResult(ConfirmCodeEntity httpResult);

        void onCheckConfirmCodeResult(HttpResult httpResult);

        void onCheckPayPswResult(HttpResult httpResult);
    }

    interface  Presenter extends BasePresenter<View> {

        void getConfirmCode(String phone);

        void checkConfirmCode(String phone,String code);

        void checkPayPsw(String payPsw);
    }
}
