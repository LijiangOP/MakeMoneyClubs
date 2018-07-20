package com.zhengdao.zqb.view.activity.withdraw;

import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.UserHomeBean;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;


public class WithDrawContract {
    interface View extends BaseView {

        void onWithDrawResult(HttpResult httpResult);

        void ReLogin();

        void showView(UserHomeBean httpResult);
    }

    interface Presenter extends BasePresenter<View> {
        void doWithDraw(String type, String money);

        void getUserData();

    }
}
