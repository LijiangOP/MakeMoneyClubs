package com.zhengdao.zqb.view.activity.investinfoinput;

import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;
import com.zhengdao.zqb.view.adapter.InformationInputAdapter;

public class InvestInfoInputContract {
    interface View extends BaseView, InformationInputAdapter.ItemCallBack {
        void noData();

        void ReLogin();

        void setAdapter(boolean isHasNext, InformationInputAdapter adapter);

        void updateAdapter(boolean isHasNext, InformationInputAdapter adapter);
    }

    interface Presenter extends BasePresenter<View> {
        void getData();

        void updataData();

        void getMoreData();
    }
}
