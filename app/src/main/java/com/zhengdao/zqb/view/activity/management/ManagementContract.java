package com.zhengdao.zqb.view.activity.management;

import com.zhengdao.zqb.entity.RewardManagerHttpEntity;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

public class ManagementContract {
    interface View extends BaseView {

        void showView(RewardManagerHttpEntity httpResult);
    }

    interface  Presenter extends BasePresenter<View> {

        void getData();
    }
}
