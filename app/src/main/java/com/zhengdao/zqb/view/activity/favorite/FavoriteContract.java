package com.zhengdao.zqb.view.activity.favorite;

import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;
import com.zhengdao.zqb.view.adapter.MyBrowsingHistoryAdapter;

public class FavoriteContract {

    interface View extends BaseView {

        void showListView(MyBrowsingHistoryAdapter adapter, boolean isHasNext);

        void noData();

        void ReLogin();

        void showViewState(boolean isHasNext);
    }

    interface Presenter extends BasePresenter<View> {
        void initData();

        void updataData();

        void getMoreData();
    }
}
