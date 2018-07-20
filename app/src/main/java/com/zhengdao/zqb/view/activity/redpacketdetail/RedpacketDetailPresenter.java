package com.zhengdao.zqb.view.activity.redpacketdetail;

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

public class RedpacketDetailPresenter extends BasePresenterImpl<RedpacketDetailContract.View> implements RedpacketDetailContract.Presenter {

    @Override
    public void getData() {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(UserApi.class)
                .getRebPocketDetail(SettingUtils.getUserToken(mView.getContext()))
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
                        mView.onGetDataFinished(httpResult);
                    }
                });
        addSubscription(subscribe);
    }
}
