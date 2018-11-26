package com.zhengdao.zqb.view.activity.welfarewechatshare;

import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.ShareHttpEntity;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

/**
 * @Author lijiangop
 * @CreateTime 2018/9/5 0005 14:32
 */
public class WelfareWeChatShareContract {
    interface View extends BaseView {
        void onDataGet(ShareHttpEntity httpResult);

        void onGetWelfareShareReward(HttpResult httpResult);
    }

    interface Presenter extends BasePresenter<View> {
        void getData();

        void getWelfareShareReward();
    }
}
