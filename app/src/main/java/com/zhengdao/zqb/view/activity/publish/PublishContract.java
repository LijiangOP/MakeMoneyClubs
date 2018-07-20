package com.zhengdao.zqb.view.activity.publish;

import com.zhengdao.zqb.entity.DictionaryHttpEntity;
import com.zhengdao.zqb.entity.HomeWantedDetailEntity;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.RequestBody;

public class PublishContract {
    interface View extends BaseView {
        void onGetDataFinished(HomeWantedDetailEntity httpResult);

        void showView(DictionaryHttpEntity result, String key);

        void onSaveOrPublishResult(HttpResult httpResult);

        void onImgUploadError();

        void onImgUploadResult(HttpResult<ArrayList<String>> result);

        void onIconImgUploadError();

        void onIconImgUploadResult(HttpResult<ArrayList<String>> result);

    }

    interface Presenter extends BasePresenter<View> {

        void getData(String key);

        void getWantedData(int id);

        void doPublish(int i, String json);

        void uploadImages(RequestBody type, Map<String, RequestBody> file);

        void uploadIconImages(RequestBody type, Map<String, RequestBody> file);
    }
}
