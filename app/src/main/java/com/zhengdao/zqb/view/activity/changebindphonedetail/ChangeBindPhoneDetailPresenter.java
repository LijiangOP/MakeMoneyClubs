package com.zhengdao.zqb.view.activity.changebindphonedetail;

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

public class ChangeBindPhoneDetailPresenter extends BasePresenterImpl<ChangeBindPhoneDetailContract.View> implements ChangeBindPhoneDetailContract.Presenter {

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
    public void BindNewPhone(String phone, String confirmCode) {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(UserApi.class)
                .bindNewPhone(phone, confirmCode, SettingUtils.getUserToken(mView.getContext()))
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
                        mView.onChangePhoneNumResult(httpResult);
                    }
                });
        addSubscription(subscribe);
    }
}
