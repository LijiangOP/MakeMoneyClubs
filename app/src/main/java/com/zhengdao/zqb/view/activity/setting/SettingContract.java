package com.zhengdao.zqb.view.activity.setting;

import com.zhengdao.zqb.entity.UserInfoBean;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

public class SettingContract {
    interface View extends BaseView {

        void ShowView(UserInfoBean httpResult);
    }

    interface  Presenter extends BasePresenter<View> {

        void getUserData();
    }
}
