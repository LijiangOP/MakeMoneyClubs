package com.zhengdao.zqb.view.activity.identitycertify;

import android.text.TextUtils;

import com.zhengdao.zqb.api.UserApi;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;
import com.zhengdao.zqb.utils.SettingUtils;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class IdentityCertifyPresenter extends BasePresenterImpl<IdentityCertifyContract.View> implements IdentityCertifyContract.Presenter {

    @Override
    public void editUserInfo(String json) {
        if (TextUtils.isEmpty(json))
            return;
        String token = SettingUtils.getUserToken(mView.getContext());
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(UserApi.class)
                .changeUserIdentity(token, json)
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
                        mView.showEditResult(httpResult);
                    }
                });
        addSubscription(subscribe);
    }
}
