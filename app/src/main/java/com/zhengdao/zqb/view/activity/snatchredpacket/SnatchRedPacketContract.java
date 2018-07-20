package com.zhengdao.zqb.view.activity.snatchredpacket;

import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;
import com.zhengdao.zqb.view.adapter.SnatchRedPacketAdapter;

public class SnatchRedPacketContract {

    interface View extends BaseView {

        void showListView(SnatchRedPacketAdapter adapter, boolean isLast);

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
