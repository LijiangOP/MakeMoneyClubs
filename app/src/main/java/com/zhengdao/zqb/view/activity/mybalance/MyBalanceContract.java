package com.zhengdao.zqb.view.activity.mybalance;

import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;
import com.zhengdao.zqb.view.adapter.BalanceAdapter;

public class MyBalanceContract {

    interface View extends BaseView {

        void noData();

        void ReLogin();

        void setAdapter(BalanceAdapter adapter, boolean isHasNext, Double balance);

        void updateAdapter(boolean isHasNext, Double balance);
    }

    interface Presenter extends BasePresenter<View> {
        void initData(int type);

        void updataData();

        void getMoreData();
    }
}
