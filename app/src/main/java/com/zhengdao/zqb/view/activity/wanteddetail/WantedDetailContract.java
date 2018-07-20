package com.zhengdao.zqb.view.activity.wanteddetail;

import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.WantedDetailHttpEntity;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

public class WantedDetailContract {
    interface View extends BaseView {

        void onGetDataResult(WantedDetailHttpEntity httpResult);

        void onRemindCheckResult(HttpResult httpResult);

        void onCancleMissionResult(HttpResult httpResult);
    }

    interface Presenter extends BasePresenter<View> {

        void getData(int wantedId);

        void remindCheck(int wantedId);

        void cancleMission(int wantedId);

        void commite(int wantedId);

        void evaluate(int wantedId);
    }
}
