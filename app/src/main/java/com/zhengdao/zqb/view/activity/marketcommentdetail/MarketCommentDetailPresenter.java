package com.zhengdao.zqb.view.activity.marketcommentdetail;

import android.text.TextUtils;

import com.zhengdao.zqb.api.HomeApi;
import com.zhengdao.zqb.api.UpLoadApi;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.MarketCommentHttpEntity;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;
import com.zhengdao.zqb.utils.SettingUtils;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.RequestBody;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class MarketCommentDetailPresenter extends BasePresenterImpl<MarketCommentDetailContract.View> implements MarketCommentDetailContract.Presenter {

    @Override
    public void uploadImages(RequestBody type, Map<String, RequestBody> file) {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(UpLoadApi.class)
                .uploadImages(type, file)
                .subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.io()).doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgress();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HttpResult<ArrayList<String>>>() {
                    @Override
                    public void onCompleted() {
                        mView.hideProgress();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.hideProgress();
                        mView.onImgUploadError();
                    }

                    @Override
                    public void onNext(HttpResult<ArrayList<String>> result) {
                        mView.hideProgress();
                        mView.onImgUploadResult(result);
                    }
                });
        addSubscription(subscribe);
    }

    @Override
    public void doCommit(int mid, String url1, String url2) {
        String userToken = SettingUtils.getUserToken(mView.getContext());
        if (TextUtils.isEmpty(userToken))
            return;
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(HomeApi.class)
                .commitMarketComment(mid, userToken, url1, url2)
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
                        mView.onCommitResult(httpResult);
                    }
                });
        addSubscription(subscribe);
    }

    @Override
    public void getData() {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(HomeApi.class)
                .getMarketComment(SettingUtils.getUserToken(mView.getContext()))
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgress();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MarketCommentHttpEntity>() {
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
                    public void onNext(MarketCommentHttpEntity httpResult) {
                        mView.hideProgress();
                        mView.showView(httpResult);
                    }
                });
        addSubscription(subscribe);
    }


}
