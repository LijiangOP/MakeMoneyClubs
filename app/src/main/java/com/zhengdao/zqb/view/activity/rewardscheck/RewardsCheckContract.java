package com.zhengdao.zqb.view.activity.rewardscheck;

import com.zhengdao.zqb.entity.KindOfWantedHttpEntity;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

public class RewardsCheckContract {
    interface View extends BaseView {
        void onGetDataResult(KindOfWantedHttpEntity httpResult);
    }

    interface Presenter extends BasePresenter<View> {

        void getData(int currentpage);
    }
}
