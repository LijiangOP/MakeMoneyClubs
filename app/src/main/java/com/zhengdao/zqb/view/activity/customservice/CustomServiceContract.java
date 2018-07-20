package com.zhengdao.zqb.view.activity.customservice;

import com.zhengdao.zqb.entity.CustomHttpEntity;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

public class CustomServiceContract {
    interface View extends BaseView {

        void onGetDataResult(CustomHttpEntity httpResult);
    }

    interface  Presenter extends BasePresenter<View> {

        void getData();
    }
}
