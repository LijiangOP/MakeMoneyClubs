package com.zhengdao.zqb.view.activity.dailysign;

import com.zhengdao.zqb.entity.AdvertisementHttpEntity;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

public class DailySignContract {
    interface View extends BaseView {
        void onSignResult(HttpResult httpResult);

        void onGetAdvReplace(AdvertisementHttpEntity httpResult);
    }

    interface Presenter extends BasePresenter<View> {

        void doSign();

        void getAdvReplace(int position);
    }
}
