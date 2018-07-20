package com.zhengdao.zqb.view.activity.attention;

import android.text.TextUtils;

import com.zhengdao.zqb.api.UserApi;
import com.zhengdao.zqb.entity.AttentionEntity;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;
import com.zhengdao.zqb.utils.SettingUtils;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class AttentionPresenter extends BasePresenterImpl<AttentionContract.View> implements AttentionContract.Presenter {

    @Override
    public void initData(int currentPage) {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(UserApi.class)
                .getAttention(SettingUtils.getUserToken(mView.getContext()), "follow", currentPage)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgress();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AttentionEntity>() {
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
                    public void onNext(AttentionEntity httpResult) {
                        mView.hideProgress();
                        mView.onGetDataResult(httpResult);
                    }
                });
        addSubscription(subscribe);
    }

    @Override
    public void updataData(int currentPage) {
        initData(currentPage);
    }

    @Override
    public void getMoreData(boolean isHasNext, int currentPage) {
        if (isHasNext) {
            initData(currentPage);
        }
    }

    @Override
    public void cancleAttention(int id) {
        String userToken = SettingUtils.getUserToken(mView.getContext());
        if (TextUtils.isEmpty(userToken))
            return;
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(UserApi.class)
                .cancleAttention(userToken, id)
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
                        mView.onCancleAttentionResult(httpResult);
                    }
                });
        addSubscription(subscribe);
    }
}
