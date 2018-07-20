package com.zhengdao.zqb.view.activity.dailysign;

import com.zhengdao.zqb.api.HomeApi;
import com.zhengdao.zqb.api.MissionApi;
import com.zhengdao.zqb.entity.AdvertisementHttpEntity;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;
import com.zhengdao.zqb.utils.SettingUtils;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

import static android.R.attr.type;

public class DailySignPresenter extends BasePresenterImpl<DailySignContract.View> implements DailySignContract.Presenter {

    @Override
    public void getAdvReplace(int position) {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(HomeApi.class)
                .getAdvInfo(position)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgress();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AdvertisementHttpEntity>() {
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
                    public void onNext(AdvertisementHttpEntity httpResult) {
                        mView.hideProgress();
                        mView.onGetAdvReplace(httpResult);
                    }
                });
        addSubscription(subscribe);
    }

    @Override
    public void doSign() {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(MissionApi.class)
                .doDailySign(SettingUtils.getUserToken(mView.getContext()))
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
                        mView.onSignResult(httpResult);
                    }
                });
        addSubscription(subscribe);
    }
}
