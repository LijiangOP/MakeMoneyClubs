package com.zhengdao.zqb.view.activity.newhandmission;

import com.zhengdao.zqb.entity.GameListHttpResult;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.NewBieHttpEntity;
import com.zhengdao.zqb.entity.SurveyHttpResult;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

public class NewHandMissionContract {
    interface View extends BaseView {
        void showView(NewBieHttpEntity httpResult);

        void onGetAdvReward(HttpResult httpResult);

        void onRewardGet(HttpResult httpResult);

        void onSurveyLinkGet(SurveyHttpResult httpResult);

        void onGetGameList(GameListHttpResult httpResult);
    }

    interface Presenter extends BasePresenter<View> {

        void getData();

        void getSeeAdvReward(int address, int type);

        void getGameReward(int type);

        void getSurveyLink();

        void getGameList();
    }
}
