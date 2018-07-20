package com.zhengdao.zqb.view.activity.identitycertify;

import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

/**
 * MVPPlugin
 * 邮箱 784787081@qq.com
 */

public class IdentityCertifyContract {
    interface View extends BaseView {

        void showEditResult(HttpResult httpResult);
    }

    interface Presenter extends BasePresenter<View> {
        void editUserInfo(String json);
    }
}
