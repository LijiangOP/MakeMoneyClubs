package com.zhengdao.zqb.view.activity.management;

import android.text.TextUtils;

import com.zhengdao.zqb.api.WantedApi;
import com.zhengdao.zqb.entity.RewardManagerHttpEntity;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;
import com.zhengdao.zqb.utils.SettingUtils;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class ManagementPresenter extends BasePresenterImpl<ManagementContract.View> implements ManagementContract.Presenter{

    @Override
    public void getData() {
        String userToken = SettingUtils.getUserToken(mView.getContext());
        if (!TextUtils.isEmpty(userToken)) {
            Subscription subscribe = RetrofitManager.getInstance().noCache().create(WantedApi.class)
                    .getRewardManagerInfo(userToken)
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe(new Action0() {
                        @Override
                        public void call() {
                            mView.showProgress();
                        }
                    }).subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<RewardManagerHttpEntity>() {
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
                        public void onNext(RewardManagerHttpEntity httpResult) {
                            mView.hideProgress();
                            mView.showView(httpResult);
                        }
                    });
            addSubscription(subscribe);
        }
    }
}
