package com.zhengdao.zqb.view.activity.activitycenter;

import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;
import com.zhengdao.zqb.view.adapter.ActivityCenterAdapter;

public class ActivityCenterContract {

    interface View extends BaseView {

        void noData();

        void ReLogin();

        void setAdapter(ActivityCenterAdapter adapter, boolean isHasNext);

        void updateAdapter(boolean isHasNext);
    }

    interface Presenter extends BasePresenter<View> {
        void initData();

        void updataData();

        void getMoreData();
    }
}
