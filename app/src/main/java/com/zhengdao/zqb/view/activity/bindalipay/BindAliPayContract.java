package com.zhengdao.zqb.view.activity.bindalipay;

import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;


public class BindAliPayContract {
    interface View extends BaseView {
        void onSetAliPayResult(HttpResult httpResult);
    }

    interface Presenter extends BasePresenter<View> {

        void setAliPay(String userName, String aliPay);
    }
}
