package com.zhengdao.zqb.view.activity.identityvertify;

import android.text.TextUtils;

import com.zhengdao.zqb.api.LogInApi;
import com.zhengdao.zqb.api.UserApi;
import com.zhengdao.zqb.entity.ConfirmCodeEntity;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;
import com.zhengdao.zqb.utils.SettingUtils;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class IdentityVertifyPresenter extends BasePresenterImpl<IdentityVertifyContract.View> implements IdentityVertifyContract.Presenter {

    @Override
    public void getConfirmCode(String phone) {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(LogInApi.class)
                .getConfirmCode(phone)
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
                        mView.onGetConfirmCodeResult(httpResult);
                    }
                });
        addSubscription(subscribe);
    }

    @Override
    public void checkConfirmCode(String phone, String code) {
        if (!TextUtils.isEmpty(phone)) {
            Subscription subscribe = RetrofitManager.getInstance().noCache().create(LogInApi.class)
                    .CheckConfirmCode(phone, code)
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe(new Action0() {
                        @Override
                        public void call() {

                        }
                    }).subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<HttpResult>() {
                        @Override
                        public void onCompleted() {
                            mView.getConfirmCodeSuccess();
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(HttpResult httpResult) {
                            mView.onCheckConfirmCodeResult(httpResult);
                        }
                    });
            addSubscription(subscribe);
        }
    }

    @Override
    public void checkPayPsw(String payPsw) {
        String token = SettingUtils.getUserToken(mView.getContext());
        if (!TextUtils.isEmpty(token)) {
            Subscription subscribe = RetrofitManager.getInstance().noCache().create(UserApi.class)
                    .CheckPayPsw(token, payPsw)
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe(new Action0() {
                        @Override
                        public void call() {

                        }
                    }).subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<HttpResult>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(HttpResult httpResult) {
                            mView.onCheckPayPswResult(httpResult);
                        }
                    });
            addSubscription(subscribe);
        }
    }
}
