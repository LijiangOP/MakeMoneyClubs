package com.zhengdao.zqb.view.activity.message;

import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;
import com.zhengdao.zqb.view.adapter.MessageAdapter;

public class MessageContract {

    interface View extends BaseView {
        void updataAdapter(MessageAdapter adapter, int total, boolean isHasNext);

        void noData();

        void ReLogin();

        void showViewState(boolean isHasNext);
    }

    interface Presenter extends BasePresenter<View> {

        void getData();

        void updateData();

        void getMoreData();
    }
}
