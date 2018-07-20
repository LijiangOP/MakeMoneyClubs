package com.zhengdao.zqb.view.activity.changeinfo;

import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

public class ChangeInfoContract {
    interface View extends BaseView {
        void onChangeResult(HttpResult httpResult);
    }

    interface Presenter extends BasePresenter<View> {

        void ChangeUserInfo(String value);
    }
}
