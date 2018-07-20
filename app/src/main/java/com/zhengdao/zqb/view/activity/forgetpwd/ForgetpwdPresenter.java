package com.zhengdao.zqb.view.activity.forgetpwd;

import com.zhengdao.zqb.api.LogInApi;
import com.zhengdao.zqb.entity.ConfirmCodeEntity;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class ForgetpwdPresenter extends BasePresenterImpl<ForgetpwdContract.View> implements ForgetpwdContract.Presenter {

    @Override
    public void getConfirmCode(String account) {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(LogInApi.class)
                .getConfirmCode(account)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ConfirmCodeEntity>() {
                    @Override
                    public void onCompleted() {
                        mView.getConfirmCodeSuccess();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ConfirmCodeEntity httpResult) {
                        mView.getConfirmCodeResult(httpResult);
                    }
                });
        addSubscription(subscribe);
    }

    @Override
    public void checkConfirmCode(String account, String confirmCode) {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(LogInApi.class)
                .CheckConfirmCode(account, confirmCode)
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
                        mView.onCheckConfirmCodeResult(httpResult);
                    }
                });
        addSubscription(subscribe);
    }
}
