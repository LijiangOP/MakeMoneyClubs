package com.zhengdao.zqb.view.fragment.rebate;

import android.support.v7.widget.RecyclerView;

import com.zhengdao.zqb.entity.DictionaryHttpEntity;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;
import com.zhengdao.zqb.view.adapter.CouponsAdapter;
import com.zhengdao.zqb.view.adapter.RebateAdapter;

public class RebateContract {
    interface View extends BaseView, CouponsAdapter.ItemSearchCallBack, RebateAdapter.onItemClick {
        void onSwitchStateGet(DictionaryHttpEntity result);

        void noData();

        void ReLogin();

        void setAdapter(RecyclerView.Adapter adapter, boolean isHasNext);

        void refreshAdapter(boolean isHasNext);

        void onGetAdvReward(HttpResult httpResult);
    }

    interface Presenter extends BasePresenter<View> {
        void getSwitchState();

        void initData();

        void refresh();

        void getMore();

        void getSeeAdvReward(int address, int type);
    }
}
