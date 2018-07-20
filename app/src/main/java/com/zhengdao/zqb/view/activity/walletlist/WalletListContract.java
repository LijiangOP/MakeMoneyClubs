package com.zhengdao.zqb.view.activity.walletlist;

import com.zhengdao.zqb.entity.BalanceEntity;
import com.zhengdao.zqb.entity.BalanceHttpEntity;
import com.zhengdao.zqb.entity.IntegralEntity;
import com.zhengdao.zqb.entity.IntegralHttpEntity;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;
import com.zhengdao.zqb.view.adapter.WalletListAdapter;

public class WalletListContract {
    interface View extends BaseView {

        void noData();

        void showListView(WalletListAdapter adapter, boolean isHasNext);

        void showBalanceView(BalanceHttpEntity<BalanceEntity> httpResult);

        void showIntegralView(IntegralHttpEntity<IntegralEntity> httpResult);

        void ReLogin();

        void showViewState(boolean isHasNext);
    }

    interface Presenter extends BasePresenter<View> {

        void getData(String date, String type);

        void getMore(String date, String type);

        void updataData(String date, String type);
    }
}
