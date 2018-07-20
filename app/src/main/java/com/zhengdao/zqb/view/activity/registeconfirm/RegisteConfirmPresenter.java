package com.zhengdao.zqb.view.activity.registeconfirm;

import com.zhengdao.zqb.api.LogInApi;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.UserInfo;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;
import com.zhengdao.zqb.utils.Base64Utils;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class RegisteConfirmPresenter extends BasePresenterImpl<RegisteConfirmContract.View> implements RegisteConfirmContract.Presenter {

    @Override
    public void doRegiste(String account, String psw, String pwdStrength) {
        psw = Base64Utils.getBase64(psw);
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(LogInApi.class)
                .doRegist(account, psw, pwdStrength)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgress();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HttpResult<UserInfo>>() {
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
                    public void onNext(HttpResult<UserInfo> httpResult) {
                        mView.hideProgress();
                        mView.onRegisteFinish(httpResult);
                    }
                });
        addSubscription(subscribe);
    }
}
