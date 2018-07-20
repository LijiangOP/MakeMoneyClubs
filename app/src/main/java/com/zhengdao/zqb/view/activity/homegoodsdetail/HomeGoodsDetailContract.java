package com.zhengdao.zqb.view.activity.homegoodsdetail;

import com.zhengdao.zqb.entity.DictionaryEntity;
import com.zhengdao.zqb.entity.HomeWantedDetailEntity;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.LessTimeHttpResult;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.RequestBody;

public class HomeGoodsDetailContract {
    interface View extends BaseView {
        void noData();

        void onGetDataFinished(HomeWantedDetailEntity httpResult);

        void onGetCheckTimeFinish(DictionaryEntity httpResult);

        void onAddAttentionFinished(HttpResult httpResult);

        void onAddCollectFinished(HttpResult httpResult);

        void onGetWantedFinished(LessTimeHttpResult httpResult);

        void onCancleAttentionFinished(HttpResult httpResult);

        void onImgUploadError();

        void onImgUploadResult(HttpResult<ArrayList<String>> result);

        void onCommitWantedFinished(HttpResult httpResult);

    }

    interface Presenter extends BasePresenter<View> {

        void getData(int id);

        void getWanted(int id);

        void doCollect(int id);

        void getCheckTime(int mode);

        void AddAttention(int wantedId);

        void CancleAttention(int id);

        void uploadImages(RequestBody type, Map<String, RequestBody> file);

        void CommitWanted(ArrayList<String> uploadImages, ArrayList<String> jsons, int Id);
    }
}
