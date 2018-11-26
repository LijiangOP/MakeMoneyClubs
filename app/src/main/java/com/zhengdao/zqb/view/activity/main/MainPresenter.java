package com.zhengdao.zqb.view.activity.main;

import com.zhengdao.zqb.api.HomeApi;
import com.zhengdao.zqb.api.RebateAPi;
import com.zhengdao.zqb.entity.GoodsCommandHttpEntity;
import com.zhengdao.zqb.entity.HttpLiCaiDetailEntity;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;
import com.zhengdao.zqb.utils.SettingUtils;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;


public class MainPresenter extends BasePresenterImpl<MainContract.View> implements MainContract.Presenter {

    @Override
    public void getZeroEarnGoodsCommand(int i) {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(HomeApi.class)
                .getGoodsCommand(i)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgress();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GoodsCommandHttpEntity>() {
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
                    public void onNext(GoodsCommandHttpEntity httpResult) {
                        mView.hideProgress();
                        mView.onGetZeroEarnGoodsCommandResult(httpResult);
                    }
                });
        addSubscription(subscribe);
    }

    @Override
    public void getRebateGoodsCommand(int id) {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(RebateAPi.class)
                .getLiCaiDetail(id)
                .subscribeOn(Schedulers.io()).doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgress();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HttpLiCaiDetailEntity>() {
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
                    public void onNext(HttpLiCaiDetailEntity result) {
                        mView.hideProgress();
                        mView.onGetRebateGoodsCommandResult(result);
                    }
                });
        addSubscription(subscribe);
    }

    @Override
    public void getOutReward() {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(HomeApi.class)
                .getOutReward(SettingUtils.getUserToken(mView.getContext()))
                .subscribeOn(Schedulers.io()).doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgress();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GoodsCommandHttpEntity>() {
                    @Override
                    public void onCompleted() {
                        mView.hideProgress();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.hideProgress();
                        mView.onGetExitCommandError();
                    }

                    @Override
                    public void onNext(GoodsCommandHttpEntity httpResult) {
                        mView.hideProgress();
                        mView.onGetExitCommandResult(httpResult);
                    }
                });
        addSubscription(subscribe);
    }
}
