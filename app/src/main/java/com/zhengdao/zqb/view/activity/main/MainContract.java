package com.zhengdao.zqb.view.activity.main;

import com.zhengdao.zqb.entity.GoodsCommandHttpEntity;
import com.zhengdao.zqb.entity.HttpLiCaiDetailEntity;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

public class MainContract {
    interface View extends BaseView {
        void onGetZeroEarnGoodsCommandResult(GoodsCommandHttpEntity httpResult);

        void onGetRebateGoodsCommandResult(HttpLiCaiDetailEntity result);

        void onGetExitCommandResult(GoodsCommandHttpEntity httpResult);

        void onGetExitCommandError();
    }

    interface Presenter extends BasePresenter<View> {

        void getZeroEarnGoodsCommand(int i);

        void getRebateGoodsCommand(int i);

        void getOutReward();
    }
}
