package com.zhengdao.zqb.view.activity.test;

import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

public class TestContract {
    interface View extends BaseView {

    }

    interface Presenter extends BasePresenter<View> {

    }
}
