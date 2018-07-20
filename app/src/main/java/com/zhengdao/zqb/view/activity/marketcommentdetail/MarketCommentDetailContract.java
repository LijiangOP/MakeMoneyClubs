package com.zhengdao.zqb.view.activity.marketcommentdetail;

import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.MarketCommentHttpEntity;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.RequestBody;

public class MarketCommentDetailContract {
    interface View extends BaseView {

        void onImgUploadError();

        void onImgUploadResult(HttpResult<ArrayList<String>> result);

        void onCommitResult(HttpResult result);

        void showView(MarketCommentHttpEntity httpResult);
    }

    interface Presenter extends BasePresenter<View> {

        void uploadImages(RequestBody type, Map<String, RequestBody> file);

        void doCommit(int mid, String url1, String url2);

        void getData();
    }
}
