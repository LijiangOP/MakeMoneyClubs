package com.zhengdao.zqb.view.activity.chargephone;

import android.text.TextUtils;

import com.zhengdao.zqb.api.WalletApi;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.PhoneChargeEntity;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;
import com.zhengdao.zqb.utils.SettingUtils;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class ChargePhonePresenter extends BasePresenterImpl<ChargePhoneContract.View> implements ChargePhoneContract.Presenter {

    @Override
    public void getData() {
        String userToken = SettingUtils.getUserToken(mView.getContext());
        if (!TextUtils.isEmpty(userToken)) {
            Subscription subscribe = RetrofitManager.getInstance().noCache().create(WalletApi.class)
                    .getPhoneChargeInfo()
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe(new Action0() {
                        @Override
                        public void call() {
                            mView.showProgress();
                        }
                    }).subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<PhoneChargeEntity>() {
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
                        public void onNext(PhoneChargeEntity httpResult) {
                            mView.hideProgress();
                            mView.onGetDataResult(httpResult);
                        }
                    });
            addSubscription(subscribe);
        }
    }

    @Override
    public void doCharge(int chargeType, int number) {
        String userToken = SettingUtils.getUserToken(mView.getContext());
        if (!TextUtils.isEmpty(userToken)) {
            Subscription subscribe = RetrofitManager.getInstance().noCache().create(WalletApi.class)
                    .doPhoneCharge(userToken,chargeType,number)
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
                            mView.onChargeResult(httpResult);
                        }
                    });
            addSubscription(subscribe);
        }
    }
}
