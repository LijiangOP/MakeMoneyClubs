package com.zhengdao.zqb.view.activity.zeroearn;

import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;
import com.zhengdao.zqb.view.adapter.GoodsAdapter;

public class ZeroEarnContract {

    interface View extends BaseView {
        void updataAdapter(GoodsAdapter adapter, boolean isHasNext);

        void showViewState(boolean isHasNext);

        void noData();

        void ReLogin();
    }

    interface Presenter extends BasePresenter<View> {
        void initData();

        void updateData();

        void getMoreData();
    }
}
