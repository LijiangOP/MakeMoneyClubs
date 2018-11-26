package com.zhengdao.zqb.view.fragment.focus;

import com.zhengdao.zqb.entity.DictionaryHttpEntity;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

public class FocusContract {
    interface View extends BaseView {
        void onSwitchStateGet(DictionaryHttpEntity result);
    }

    interface Presenter extends BasePresenter<View> {
        void getSwitchState();
    }
}
