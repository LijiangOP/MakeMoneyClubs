package com.zhengdao.zqb.view.activity.announcementdeatil;

import com.zhengdao.zqb.entity.AnnouncementDetailBean;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

public class AnnouncementDeatilContract {
    interface View extends BaseView {
        void showView(HttpResult<AnnouncementDetailBean> httpResult);
    }

    interface Presenter extends BasePresenter<View> {
        void getData(int id);
    }
}
