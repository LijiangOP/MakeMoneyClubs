package com.zhengdao.zqb.view.activity.kindofwanteddetail;

import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.KindOfWantedDetailHttpEntity;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

public class KindOfWantedDetailContract {
    interface View extends BaseView {
        void onGetDataResult(KindOfWantedDetailHttpEntity httpResult);

        void onAddAttentionFinished(HttpResult httpResult);

        void onConfirmCheckFinished(HttpResult httpResult);
    }

    interface Presenter extends BasePresenter<View> {

        void getData(int id);

        void AddAttention(int userId);

        void ConfirmCheck(String adopt, int taskId, int rwId, String remarks, int userId);
    }
}
