package com.zhengdao.zqb.view.activity.mywallet;

import com.zhengdao.zqb.entity.WalletHttpEntity;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

public class MyWalletContract {
    interface View extends BaseView {

        void onGetDataResult(WalletHttpEntity httpResult);
    }

    interface Presenter extends BasePresenter<View> {

        void getData();

    }
}
