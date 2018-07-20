package com.zhengdao.zqb.view.activity.rebaterecords;

import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;
import com.zhengdao.zqb.view.adapter.RebateRecordsAdapter;

public class RebateRecordsContract {

    interface View extends BaseView {

        void noData();

        void ReLogin();

        void setAdapter(RebateRecordsAdapter adapter, boolean isHasNext);

        void updateAdapter(boolean isHasNext);
    }

    interface Presenter extends BasePresenter<View> {
        void initData();

        void updataData();

        void getMoreData();
    }
}
