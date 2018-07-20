package com.zhengdao.zqb.view.activity.newhandmission;

import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.NewBieHttpEntity;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

public class NewHandMissionContract {
    interface View extends BaseView {
        void showView(NewBieHttpEntity httpResult);

        void onGetAdvReward(HttpResult httpResult);
    }

    interface Presenter extends BasePresenter<View> {

        void getData();

        void getSeeAdvReward(int address, int type);
    }
}
