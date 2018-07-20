package com.zhengdao.zqb.view.activity.wantednotpassdetail;

import android.text.TextUtils;

import com.zhengdao.zqb.api.WantedApi;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;
import com.zhengdao.zqb.utils.SettingUtils;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class WantedNotPassDetailPresenter extends BasePresenterImpl<WantedNotPassDetailContract.View> implements WantedNotPassDetailContract.Presenter {

    @Override
    public void doCommit(String adopt, int taskId, int rwId, String input, int userId) {
        String userToken = SettingUtils.getUserToken(mView.getContext());
        if (TextUtils.isEmpty(userToken))
            return;
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(WantedApi.class)
                .ConfirmCheck(userToken, adopt, taskId, rwId, input, userId)
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
                        mView.onConfirmCheckFinished(httpResult);
                    }
                });
        addSubscription(subscribe);
    }
}
