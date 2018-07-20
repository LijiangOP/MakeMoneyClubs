package com.zhengdao.zqb.view.activity.webview;

import com.zhengdao.zqb.entity.ShareHttpEntity;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

public class WebViewContract {
    interface View extends BaseView {
        void showSahreData(ShareHttpEntity httpResult);
    }

    interface  Presenter extends BasePresenter<View> {
        void getShareData();
    }
}
