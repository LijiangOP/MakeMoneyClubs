package com.zhengdao.zqb.view.activity.wantednotpassdetail;

import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

public class WantedNotPassDetailContract {
    interface View extends BaseView {

        void onConfirmCheckFinished(HttpResult httpResult);
    }

    interface  Presenter extends BasePresenter<View> {

        void doCommit(String adopt, int taskId, int cbType, String input, int userId);
    }
}
