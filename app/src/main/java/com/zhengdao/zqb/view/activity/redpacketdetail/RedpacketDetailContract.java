package com.zhengdao.zqb.view.activity.redpacketdetail;

import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

public class RedpacketDetailContract {
    interface View extends BaseView {

        void onGetDataFinished(HttpResult httpResult);
    }

    interface Presenter extends BasePresenter<View> {

        void getData();
    }
}
