package com.zhengdao.zqb.view.activity.rewardscheck;

import android.text.TextUtils;

import com.zhengdao.zqb.api.WantedApi;
import com.zhengdao.zqb.entity.KindOfWantedHttpEntity;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;
import com.zhengdao.zqb.utils.SettingUtils;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;


public class RewardsCheckPresenter extends BasePresenterImpl<RewardsCheckContract.View> implements RewardsCheckContract.Presenter {

    @Override
    public void getData(int currentpage) {
        String token = SettingUtils.getUserToken(mView.getContext());
        if (TextUtils.isEmpty(token))
            return;
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(WantedApi.class)
                .getWantedList(currentpage, 1, "", token)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgress();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<KindOfWantedHttpEntity>() {
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
                    public void onNext(KindOfWantedHttpEntity httpResult) {
                        mView.hideProgress();
                        mView.onGetDataResult(httpResult);
                    }
                });
        addSubscription(subscribe);
    }
}
