package com.zhengdao.zqb.view.fragment.news;

import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;
import com.zhengdao.zqb.view.adapter.NewsAdapter;

public class NewsContract {
    interface View extends BaseView, NewsAdapter.CallBack {
        void noData();

        void ReLogin();

        void setAdapter(NewsAdapter adapter, boolean isHasNext);

        void updateAdapter(boolean isHasNext);

        void onDownloadRewardGet(HttpResult httpResult, String packageName);

        void onGetAdvReward(HttpResult httpResult);
    }

    interface Presenter extends BasePresenter<View> {
        void initData(String type);

        void updataData(String type);

        void getMoreData(String type);

        void getDownloadAppData(int pageNo);

        void getDownloadReward(String uniquenessId, int id, final String packageName);

        void getSeeAdvReward(int address, int type);
    }
}
