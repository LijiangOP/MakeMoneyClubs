package com.zhengdao.zqb.view.activity.pay;

import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.WalletHttpEntity;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

public class PayContract {
    interface View extends BaseView {
        void onGetDataResult(WalletHttpEntity httpResult);

        void onPayResult(HttpResult httpResult);
    }

    interface Presenter extends BasePresenter<View> {

        void getBalance();

        void pay(int payType, Integer integer);

    }
}
