package com.zhengdao.zqb.view.fragment.rewardticket;

import com.zhengdao.zqb.entity.RewardTicketHttpEntity;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

public class RewardTicketContract {
    interface View extends BaseView {

        void onDataGet(RewardTicketHttpEntity rewardTicketHttpEntity);

    }

    interface Presenter extends BasePresenter<View> {

        void getData(int type, int currentPage);
    }
}
