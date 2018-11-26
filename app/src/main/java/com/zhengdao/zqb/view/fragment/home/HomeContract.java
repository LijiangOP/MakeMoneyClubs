package com.zhengdao.zqb.view.fragment.home;

import com.zhengdao.zqb.entity.DictionaryHttpEntity;
import com.zhengdao.zqb.entity.EarnEntity;
import com.zhengdao.zqb.entity.GoodsCommandHttpEntity;
import com.zhengdao.zqb.entity.HomeInfo;
import com.zhengdao.zqb.entity.MessageEntity;
import com.zhengdao.zqb.entity.ScreenLoadEntity;
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

        void showSelectTypeView(ScreenLoadEntity result);

        void onDictionaryDataGet(DictionaryHttpEntity result, String key);

        void onGetReCommandResult(GoodsCommandHttpEntity httpResult);

        void onRefreshZeroEarn(EarnEntity result);
    }

    interface Presenter extends BasePresenter<View> {

        void initData();

        void getMoreData();

        void getWelfareData();

        void getMessageCount();

        void getUserData();

        void getSurveyLink();

        void getDataWithBaseSearch(String order, String desc, int i);

        void getDataWithFilter(int classify, int category);

        void getSelectTypeData();

        void getAliPayRedPacket(final String key);

        void getRecommendReward();
    }
}
