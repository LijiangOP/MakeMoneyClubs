package com.zhengdao.zqb.view.activity.questionsurvery;

import com.zhengdao.zqb.entity.DictionaryHttpEntity;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.SurveyHttpResult;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

/**
 * @Author lijiangop
 * @CreateTime 2018/10/15 22:20
 */
public class QuestionSurveryContract {

    interface View extends BaseView {

        void onDictionaryDataGet(DictionaryHttpEntity result, String key);

        void onChangeResult(HttpResult httpResult);

        void onSurveyLinkGet(SurveyHttpResult httpResult);
    }

    interface Presenter extends BasePresenter<View> {
        void getDictionaryByKey(final String key);

        void ChangeUserInfo(String value);

        void getSurveyLink();
    }
}
