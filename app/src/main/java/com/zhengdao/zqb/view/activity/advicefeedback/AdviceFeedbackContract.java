package com.zhengdao.zqb.view.activity.advicefeedback;

import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.RequestBody;

public class AdviceFeedbackContract {
    interface View extends BaseView {

        void onImgUploadError();

        void onImgUploadResult(HttpResult<ArrayList<String>> result);

        void onUploadResult(HttpResult result);
    }

    interface Presenter extends BasePresenter<View> {

        void uploadImages(RequestBody type, Map<String, RequestBody> file);

        void FeedbackOpinion(int type, String describe, long userId, int i, ArrayList<String> strings);
    }
}
