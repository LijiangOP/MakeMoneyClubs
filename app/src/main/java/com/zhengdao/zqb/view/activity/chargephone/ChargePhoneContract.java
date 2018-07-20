package com.zhengdao.zqb.view.activity.chargephone;

import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.PhoneChargeEntity;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

public class ChargePhoneContract {
    interface View extends BaseView {

        void onGetDataResult(PhoneChargeEntity httpResult);

        void onChargeResult(HttpResult httpResult);
    }

    interface Presenter extends BasePresenter<View> {

        void getData();

        void doCharge(int chargeType, int selectNumber);

    }
}
