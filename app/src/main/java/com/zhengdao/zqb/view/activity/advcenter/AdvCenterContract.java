package com.zhengdao.zqb.view.activity.advcenter;

import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;
import com.zhengdao.zqb.view.adapter.WalletListAdapter;

public class AdvCenterContract {
    interface View extends BaseView {

        void noData();

        void ReLogin();

        void setAdapter(WalletListAdapter adapter, boolean isHasNext, Double advertAmount);

        void updateAdapter(boolean isHasNext, Double advertAmount);
    }

    interface Presenter extends BasePresenter<View> {
        void initData();

        void updataData();

        void getMoreData();
    }
}
