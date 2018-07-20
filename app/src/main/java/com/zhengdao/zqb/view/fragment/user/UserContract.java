package com.zhengdao.zqb.view.fragment.user;

import com.zhengdao.zqb.entity.AdvertisementHttpEntity;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.UserHomeBean;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

public class UserContract {

    interface View extends BaseView {
        void ReLogin();

        void showView(UserHomeBean httpResult);

        void onGetAdvReplace(AdvertisementHttpEntity httpResult);

        void onGetInvitedReward(HttpResult httpResult);

        void onGetAdvReward(HttpResult httpResult);
    }

    interface Presenter extends BasePresenter<View> {

        void getUserData();

        void getAdvReplace(int position);

        void getInvitedReward(String investCode);

        void getSeeAdvReward(int address, int type);
    }
}
