package com.zhengdao.zqb.view.activity.questionsurvery;

import android.text.TextUtils;

import com.zhengdao.zqb.api.HomeApi;
import com.zhengdao.zqb.api.UserApi;
import com.zhengdao.zqb.api.WantedApi;
import com.zhengdao.zqb.entity.DictionaryHttpEntity;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.SurveyHttpResult;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.utils.ToastUtil;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * @Author lijiangop
 * @CreateTime 2018/10/15 22:20
 */
public class QuestionSurveryPresenter extends BasePresenterImpl<QuestionSurveryContract.View> implements QuestionSurveryContract.Presenter {

    @Override
    public void getDictionaryByKey(final String key) {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(WantedApi.class)
                .getTypesByKey(key)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<DictionaryHttpEntity>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.hideProgress();
                        mView.showErrorMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(DictionaryHttpEntity result) {
                        mView.hideProgress();
                        mView.onDictionaryDataGet(result, key);
                    }
                });
        addSubscription(subscribe);
    }

    @Override
    public void ChangeUserInfo(String json) {
        if (TextUtils.isEmpty(json))
            return;
        String token = SettingUtils.getUserToken(mView.getContext());
        if (TextUtils.isEmpty(token))
            return;
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(UserApi.class)
                .changeUserInfo(token,json)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgress();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HttpResult>() {
                    @Override
                    public void onCompleted() {
                        mView.hideProgress();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.hideProgress();
                        mView.showErrorMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(HttpResult httpResult) {
                        mView.hideProgress();
                        mView.onChangeResult(httpResult);
                    }
                });
        addSubscription(subscribe);
    }

    /**
     * 获取问卷调查链接
     */
    @Override
    public void getSurveyLink() {
        try {
            String token = SettingUtils.getUserToken(mView.getContext());
            if (TextUtils.isEmpty(token)) {
                ToastUtil.showToast(mView.getContext(), "请先登录");
                return;
            }
            Subscription subscribe = RetrofitManager.getInstance().noCache().create(HomeApi.class)
                    .getSurveyLink(token)
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe(new Action0() {
                        @Override
                        public void call() {
                            if (mView != null) {
                                mView.showProgress();
                            }
                        }
                    }).subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<SurveyHttpResult>() {
                        @Override
                        public void onCompleted() {
                            if (mView != null) {
                                mView.hideProgress();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (mView != null) {
                                mView.hideProgress();
                                mView.showErrorMessage(e.getMessage());
                            }
                        }

                        @Override
                        public void onNext(SurveyHttpResult httpResult) {
                            if (mView != null) {
                                mView.hideProgress();
                                mView.onSurveyLinkGet(httpResult);
                            }
                        }
                    });
            addSubscription(subscribe);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
