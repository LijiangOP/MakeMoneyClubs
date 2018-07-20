package com.zhengdao.zqb.view.activity.changepsw;

import android.text.TextUtils;

import com.zhengdao.zqb.api.UserApi;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class ChangePswPresenter extends BasePresenterImpl<ChangePswContract.View> implements ChangePswContract.Presenter {

    @Override
    public void changeLoginPsw(String token, String json) {
        if (TextUtils.isEmpty(json))
            return;
        if (!TextUtils.isEmpty(token)) {
            Subscription subscribe = RetrofitManager.getInstance().noCache().create(UserApi.class)
                    .changeUserInfo(token, json)
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
                            mView.onChangeLoginPswResult(httpResult);
                        }
                    });
            addSubscription(subscribe);
        }
    }

    @Override
    public void changePayPsw(String token, String json) {
        if (TextUtils.isEmpty(json))
            return;
        if (!TextUtils.isEmpty(token)) {
            Subscription subscribe = RetrofitManager.getInstance().noCache().create(UserApi.class)
                    .changeUserInfo(token, json)
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
                            mView.onChangePayPswResult(httpResult);
                        }
                    });
            addSubscription(subscribe);
        }
    }
}
