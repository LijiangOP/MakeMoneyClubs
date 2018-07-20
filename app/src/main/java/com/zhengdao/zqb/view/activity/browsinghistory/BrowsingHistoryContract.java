package com.zhengdao.zqb.view.activity.browsinghistory;

import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.ScanInfoEntity;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

import java.util.ArrayList;

public class BrowsingHistoryContract {
    interface View extends BaseView {
        void onGetDataFinish(HttpResult<ScanInfoEntity> result);

        void onDeleteResult(HttpResult result);
    }

    interface Presenter extends BasePresenter<View> {
        void initData(int currentPage);

        void updataData(int currentPage);

        void getMoreData(boolean isHasNext,int currentPage);

        void deleteHistory(ArrayList<Integer> deleteList);

        void deleteAllHistory();
    }
}
