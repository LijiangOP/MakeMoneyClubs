package com.zhengdao.zqb.view.activity.wanted;

import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;
import com.zhengdao.zqb.view.adapter.WantedAdapter;

public class WantedContract {
    interface View extends BaseView {

        void noData();

        void updataAdapter(WantedAdapter adapter, boolean isHasNext);

        void onRemindCheckResult(HttpResult httpResult);

        void onCancleMissionResult(HttpResult httpResult);

        void showContentState(boolean isHasNext);
    }

    interface Presenter extends BasePresenter<View> {

        void getData(int type);

        void updataData();

        void getMoreData();
    }
}
