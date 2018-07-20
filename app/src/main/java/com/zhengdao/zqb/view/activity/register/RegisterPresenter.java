package com.zhengdao.zqb.view.activity.register;

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

public class RegisterPresenter extends BasePresenterImpl<RegisterContract.View> implements RegisterContract.Presenter {

    @Override
    public void getConfirmCode(String account) {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(LogInApi.class)
                .getConfirmCode(account)
                .subscribeOn(Schedulers.io())
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
                        mView.onGetConfirmCodeResult(httpResult);
                    }
                });
        addSubscription(subscribe);
    }

    @Override
    public void doRegist(String account, String confirmCode, String recommend, int chanel) {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(LogInApi.class)
                .doCheck(account, confirmCode, recommend, chanel)
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
                        mView.showErrorMessage(e.getMessage());
                        mView.hideProgress();
                    }

                    @Override
                    public void onNext(HttpResult httpResult) {
                        mView.hideProgress();
                        mView.onRegistFinished(httpResult);
                    }
                });
        addSubscription(subscribe);
    }
}
