package com.zhengdao.zqb.view.activity.keywords;

import com.zhengdao.zqb.entity.DictionaryHttpEntity;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

public class KeyWordsContract {
    interface View extends BaseView {
        void showView(DictionaryHttpEntity result, String key);
    }

    interface Presenter extends BasePresenter<View> {
        void getData(final String key);
    }
}
