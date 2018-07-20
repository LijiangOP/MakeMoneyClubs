package com.zhengdao.zqb.view.activity.dailywechatshare;

import android.text.TextUtils;

import com.zhengdao.zqb.api.MissionApi;
import com.zhengdao.zqb.api.UserApi;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.ShareHttpEntity;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;
import com.zhengdao.zqb.utils.SettingUtils;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class DailyWeChatSharePresenter extends BasePresenterImpl<DailyWeChatShareContract.View> implements DailyWeChatShareContract.Presenter {

    @Override
    public void getData() {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(MissionApi.class)
                .getCodeData(SettingUtils.getUserToken(mView.getContext()))
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgress();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ShareHttpEntity>() {
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
                    public void onNext(ShareHttpEntity httpResult) {
                        mView.hideProgress();
                        mView.onDataGet(httpResult);
                    }
                });
        addSubscription(subscribe);
    }

    @Override
    public void getDailyShareReward() {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(MissionApi.class)
                .getShareReward(SettingUtils.getUserToken(mView.getContext()))
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
                        mView.onGetDailyShareReward(httpResult);
                    }
                });
        addSubscription(subscribe);
    }

    @Override
    public void doActivateTicket(int cid) {
        String userToken = SettingUtils.getUserToken(mView.getContext());
        if (TextUtils.isEmpty(userToken))
            return;
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(UserApi.class)
                .doActivateTicket(userToken, cid)
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
                        mView.onActivateTicketResult(httpResult);
                    }
                });
        addSubscription(subscribe);
    }
}
