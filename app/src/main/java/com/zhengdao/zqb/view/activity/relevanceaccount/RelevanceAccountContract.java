package com.zhengdao.zqb.view.activity.relevanceaccount;

import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.RelevanceAccountEntity;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;


public class RelevanceAccountContract {

    interface View extends BaseView {
        void onGetDataResult(RelevanceAccountEntity result);

        void onAccountBindResult(HttpResult result,int type);

        void onAccountUnBindResult(HttpResult result,int type);
    }

    interface Presenter extends BasePresenter<View> {

        void getData();

        void doAccountBind(String openId, int type);

        void doAccountUnBind(int id,int type);
    }
}
