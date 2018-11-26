package com.zhengdao.zqb.view.fragment.yearrankinglist;

import com.zhengdao.zqb.entity.RankingListEntity;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

public class YearrankinglistContract {
    interface View extends BaseView {

        void onGetDataFinished(RankingListEntity httpResult);
    }

    interface Presenter extends BasePresenter<View> {

        void getData();
    }
}
