package com.zhengdao.zqb.view.activity.kindofwanted;

import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;
import com.zhengdao.zqb.view.adapter.KindOfWantedAdapter;

public class KindOfWantedContract {
    interface View extends BaseView {

        void showListView(KindOfWantedAdapter adapter, boolean isHasNext);

        void noData();

        void ReLogin();

        void showViewState(boolean isHasNext);
    }

    interface Presenter extends BasePresenter<View> {

        void getData(int type, String flag);

        void updateData(int type, String flag);

        void getMoreData(int type, String flag);

    }
}
