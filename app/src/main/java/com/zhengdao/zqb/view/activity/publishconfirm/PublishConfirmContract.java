package com.zhengdao.zqb.view.activity.publishconfirm;

import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.WalletHttpEntity;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.RequestBody;

public class PublishConfirmContract {

    interface View extends BaseView {
        void onGetDataResult(WalletHttpEntity httpResult);

        void onPublishResult(HttpResult<String> httpResult,int payType);

        void onImgUploadError();

        void onImgUploadResult(HttpResult<ArrayList<String>> result);

        void onIconImgUploadError();

        void onIconImgUploadResult(HttpResult<ArrayList<String>> result);
    }

    interface Presenter extends BasePresenter<View> {

        void getBalance();

        void doPublish(int payType, String json);

        void uploadImages(RequestBody type, Map<String, RequestBody> file);

        void uploadIconImages(RequestBody type, Map<String, RequestBody> file);
    }
}
