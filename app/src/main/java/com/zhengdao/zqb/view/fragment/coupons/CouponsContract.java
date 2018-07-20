package com.zhengdao.zqb.view.fragment.coupons;

import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;
import com.zhengdao.zqb.view.adapter.CouponsAdapter;

public class CouponsContract {

    interface View extends BaseView, CouponsAdapter.ItemSearchCallBack {

        void noData();

        void ReLogin();

        void setAdapter(CouponsAdapter adapter, boolean isHasNext);

        void updateAdapter(boolean isHasNext);
    }

    interface Presenter extends BasePresenter<View> {
        void initData();

        void updataData();

        void getMoreData();

        void getDataWithType(int type);

        void getDataWithSearch(String s);

        void getDataWithSort(String sortName, String orderType);
    }
}
