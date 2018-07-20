package com.zhengdao.zqb.view.activity.questionclassify;

import com.zhengdao.zqb.entity.CustomHttpEntity;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

public class QuestionClassifyContract {
    interface View extends BaseView {
        void onGetDataResult(CustomHttpEntity httpResult);
    }

    interface  Presenter extends BasePresenter<View> {

        void getData();
    }
}
