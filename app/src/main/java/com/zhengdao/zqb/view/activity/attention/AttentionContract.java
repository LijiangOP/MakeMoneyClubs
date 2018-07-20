package com.zhengdao.zqb.view.activity.attention;

import com.zhengdao.zqb.entity.AttentionEntity;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

public class AttentionContract {
    interface View extends BaseView {

        void onGetDataResult(AttentionEntity httpResult);

        void onCancleAttentionResult(HttpResult httpResult);

    }

    interface Presenter extends BasePresenter<View> {
        void initData(int currentPage);

        void updataData(int currentPage);

        void getMoreData(boolean isHasNext, int currentPage);

        void cancleAttention(int id);
    }
}
