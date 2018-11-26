package com.zhengdao.zqb.view.activity.advertisementlist;

import com.zhengdao.zqb.entity.AdvertisementHttpEntity;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

public class AdvertisementListContract {
    interface View extends BaseView {
        void onGetAdvReward(HttpResult httpResult);

        void onGetAdvReplace(AdvertisementHttpEntity httpResult);
    }

    interface Presenter extends BasePresenter<View> {
        void getSeeAdvReward(int address, int type);

        void getAdvReplace(int position);
    }
}
