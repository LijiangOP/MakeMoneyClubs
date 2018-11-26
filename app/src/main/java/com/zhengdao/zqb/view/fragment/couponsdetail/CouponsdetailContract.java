package com.zhengdao.zqb.view.fragment.couponsdetail;

import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;
import com.zhengdao.zqb.view.adapter.CouponsAdapter;

public class CouponsdetailContract {
    interface View extends BaseView {
        void noData();

        void ReLogin();

        void setAdapter(CouponsAdapter adapter, boolean isHasNext);

        void updateAdapter(boolean isHasNext);
    }

    interface Presenter extends BasePresenter<View> {
        void initData(int type);

        void updataData(int type);

        void getMoreData(int type);

        void getDataWithSearch(int type, String s);

        void getDataWithSort(int type, String sortName, String orderType);
    }
}
