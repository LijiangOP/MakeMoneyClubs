package com.zhengdao.zqb.view.activity.login;

import com.zhengdao.zqb.api.LogInApi;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.UserInfo;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;
import com.zhengdao.zqb.utils.Base64Utils;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.SettingUtils;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;


public class LoginPresenter extends BasePresenterImpl<LoginContract.View> implements LoginContract.Presenter {

    @Override
    public void login(String account, String password) {
        password = Base64Utils.getBase64(password);
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(LogInApi.class)
                .login(account, password)
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
                .subscribe(new Subscriber<HttpResult<UserInfo>>() {
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
                    public void onNext(HttpResult<UserInfo> userInfoHttpResult) {
                        if (mView != null) {

                            mView.hideProgress();
                            mView.onLoginOver(userInfoHttpResult);
                        }
                    }
                });
        addSubscription(subscribe);
    }

    @Override
    public void saveUserInfo(final UserInfo bean) {
        Subscription subscribe = Observable.create(new Observable.OnSubscribe<UserInfo>() {
            @Override
            public void call(Subscriber<? super UserInfo> subscriber) {
                SettingUtils.SaveAfterLogin(mView.getContext(),bean);
                subscriber.onNext(bean);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UserInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e(e.getMessage());
                    }

                    @Override
                    public void onNext(UserInfo bean) {
                        if (mView != null) {

                            mView.loginSuccess(bean);
                        }
                    }
                });
        addSubscription(subscribe);
    }

    @Override
    public void doThirdLogin(String openId) {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(LogInApi.class)
                .thirdLogin(openId)
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
                .subscribe(new Subscriber<HttpResult<UserInfo>>() {
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
                    public void onNext(HttpResult<UserInfo> httpResult) {
                        if (mView != null) {

                            mView.hideProgress();
                            mView.onThirdLoginOver(httpResult);
                        }
                    }
                });
        addSubscription(subscribe);
    }
}
