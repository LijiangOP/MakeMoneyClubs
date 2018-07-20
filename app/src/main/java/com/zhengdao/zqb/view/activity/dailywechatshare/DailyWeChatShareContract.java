package com.zhengdao.zqb.view.activity.dailywechatshare;

import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.ShareHttpEntity;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

public class DailyWeChatShareContract {
    interface View extends BaseView {

        void onDataGet(ShareHttpEntity httpResult);

        void onGetDailyShareReward(HttpResult httpResult);

        void onActivateTicketResult(HttpResult httpResult);
    }

    interface  Presenter extends BasePresenter<View> {
        void getData();

        void getDailyShareReward();

        void doActivateTicket(int cid);
    }
}
