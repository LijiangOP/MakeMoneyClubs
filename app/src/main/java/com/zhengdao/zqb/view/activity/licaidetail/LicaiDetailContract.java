package com.zhengdao.zqb.view.activity.licaidetail;

import com.zhengdao.zqb.entity.HttpLiCaiDetailEntity;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;


public class LicaiDetailContract {

    interface View extends BaseView {
        void showResult(HttpLiCaiDetailEntity result);
    }

    interface Presenter extends BasePresenter<View> {

        void initData(int id);
    }
}
