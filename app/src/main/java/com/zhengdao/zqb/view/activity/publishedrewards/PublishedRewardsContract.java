package com.zhengdao.zqb.view.activity.publishedrewards;

import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;
import com.zhengdao.zqb.view.adapter.PublishedWantedAdapter;

public class PublishedRewardsContract {
    interface View extends BaseView {

        void noData();

        void updataAdapter(PublishedWantedAdapter adapter, boolean isHasNext);

        void onCancleWantedResult(HttpResult httpResult);

        void showContentState(boolean isHasNext);
    }

    interface Presenter extends BasePresenter<View> {

        void getData(int state);

        void updataData(int state);

        void getMore(int state);
    }
}
