package com.zhengdao.zqb.view.activity.chargeaccount;

import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

public class ChargeAccountContract {
    interface View extends BaseView {
        void onChargeResult(HttpResult<String> httpResult);
    }

    interface Presenter extends BasePresenter<View> {

        void doCharge(int chargeType, String number);
    }
}
