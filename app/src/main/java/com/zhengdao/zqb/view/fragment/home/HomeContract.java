package com.zhengdao.zqb.view.fragment.home;

import com.zhengdao.zqb.entity.EarnEntity;
import com.zhengdao.zqb.entity.HomeInfo;
import com.zhengdao.zqb.entity.MessageEntity;
import com.zhengdao.zqb.entity.SurveyHttpResult;
import com.zhengdao.zqb.entity.UserHomeBean;
import com.zhengdao.zqb.entity.WelfareHttpData;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

public class HomeContract {
    interface View extends BaseView {

        void showWelfareWindow(WelfareHttpData httpResult);

        void showRewardWindow(UserHomeBean httpResult);

        void onMessageCountGet(MessageEntity httpResult);

        void buildData(HomeInfo homeInfoHttpResult);

        void onGetMoreData(EarnEntity result);

        void onSurveyLinkGet(SurveyHttpResult httpResult);
    }

    interface Presenter extends BasePresenter<View> {

        void initData();

        void getMore(int currentPage);

        void getWelfareData();

        void getMessageCount();

        void getUserData();

        void getSurveyLink();
    }
}
