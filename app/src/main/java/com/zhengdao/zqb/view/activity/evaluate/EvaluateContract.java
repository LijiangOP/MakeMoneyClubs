package com.zhengdao.zqb.view.activity.evaluate;

import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

public class EvaluateContract {
    interface View extends BaseView {
        void onDoPublicResult(HttpResult httpResult);
    }

    interface Presenter extends BasePresenter<View> {

        void doPublish(int wantedId);
    }
}
