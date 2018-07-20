package com.zhengdao.zqb.view.activity.kindofwanteddetail;

import android.text.TextUtils;

import com.zhengdao.zqb.api.HomeApi;
import com.zhengdao.zqb.api.WantedApi;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.KindOfWantedDetailHttpEntity;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;
import com.zhengdao.zqb.utils.SettingUtils;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class KindOfWantedDetailPresenter extends BasePresenterImpl<KindOfWantedDetailContract.View> implements KindOfWantedDetailContract.Presenter {

    @Override
    public void getData(int id) {
        String token = SettingUtils.getUserToken(mView.getContext());
        if (TextUtils.isEmpty(token))
            return;
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(WantedApi.class)
                .getWantedListDetail(id)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgress();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<KindOfWantedDetailHttpEntity>() {
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
                    public void onNext(KindOfWantedDetailHttpEntity httpResult) {
                        mView.hideProgress();
                        mView.onGetDataResult(httpResult);
                    }
                });
        addSubscription(subscribe);
    }

    @Override
    public void AddAttention(int userId) {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(HomeApi.class)
                .AddACF(SettingUtils.getUserToken(mView.getContext()), userId, Constant.HttpResult.Attention)
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
                        mView.onAddAttentionFinished(httpResult);
                    }
                });
        addSubscription(subscribe);
    }

    /**
     * 30.悬赏主审核
     *
     * @param adopt
     * @param taskId
     * @param rwId
     * @param remarks
     * @param userId
     */
    @Override
    public void ConfirmCheck(String adopt, int taskId, int rwId, String remarks, int userId) {
        String userToken = SettingUtils.getUserToken(mView.getContext());
        if (TextUtils.isEmpty(userToken))
            return;
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(WantedApi.class)
                .ConfirmCheck(userToken, adopt, taskId, rwId, remarks, userId)
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
                        mView.onConfirmCheckFinished(httpResult);
                    }
                });
        addSubscription(subscribe);
    }
}
