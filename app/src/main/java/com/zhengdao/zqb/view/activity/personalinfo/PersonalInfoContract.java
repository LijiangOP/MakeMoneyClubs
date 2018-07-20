package com.zhengdao.zqb.view.activity.personalinfo;

import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.UserInfoBean;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.RequestBody;

public class PersonalInfoContract {
    interface View extends BaseView {
        void ShowView(UserInfoBean httpResult);

        void onUploadAvatarResult(HttpResult<ArrayList<String>> result);

        void showEditResult(HttpResult httpResult);
    }

    interface Presenter extends BasePresenter<View> {
        void getUserData();

        void uploadImages(RequestBody type, Map<String, RequestBody> file);

        void editUserInfo(String json);
    }
}
