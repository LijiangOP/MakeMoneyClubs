package com.zhengdao.zqb.view.activity.registeconfirm;

import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.UserInfo;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

public class RegisteConfirmContract {
    interface View extends BaseView {
        void onRegisteFinish(HttpResult<UserInfo> httpResult);
    }

    interface Presenter extends BasePresenter<View> {

        void doRegiste(String account, String psw, String pwdStrength);
    }
}
