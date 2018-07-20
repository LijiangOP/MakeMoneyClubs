package com.zhengdao.zqb.view.activity.browsinghistory;

import com.zhengdao.zqb.api.UserApi;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.ScanInfoEntity;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;
import com.zhengdao.zqb.utils.SettingUtils;

import java.util.ArrayList;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class BrowsingHistoryPresenter extends BasePresenterImpl<BrowsingHistoryContract.View> implements BrowsingHistoryContract.Presenter {

    @Override
    public void initData(int currentPage) {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(UserApi.class)
                .getScanInfo(SettingUtils.getUserToken(mView.getContext()), "browse", currentPage)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgress();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HttpResult<ScanInfoEntity>>() {
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
                    public void onNext(HttpResult<ScanInfoEntity> result) {
                        mView.hideProgress();
                        mView.onGetDataFinish(result);
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
    public void deleteHistory(ArrayList<Integer> deleteList) {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(UserApi.class)
                .deleteHistory(SettingUtils.getUserToken(mView.getContext()), deleteList)
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
                    public void onNext(HttpResult result) {
                        mView.hideProgress();
                        mView.onDeleteResult(result);
                    }
                });
        addSubscription(subscribe);
    }

    @Override
    public void deleteAllHistory() {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(UserApi.class)
                .deleteAllHistory(SettingUtils.getUserToken(mView.getContext()))
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
                    public void onNext(HttpResult result) {
                        mView.hideProgress();
                        mView.onDeleteResult(result);
                    }
                });
        addSubscription(subscribe);
    }
}
