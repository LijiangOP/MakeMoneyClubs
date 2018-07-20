package com.zhengdao.zqb.view.activity.pay;

import android.text.TextUtils;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.api.WalletApi;
import com.zhengdao.zqb.entity.WalletHttpEntity;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.utils.ToastUtil;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;


public class PayPresenter extends BasePresenterImpl<PayContract.View> implements PayContract.Presenter {

    @Override
    public void getBalance() {
        String token = SettingUtils.getUserToken(mView.getContext());
        if (TextUtils.isEmpty(token))
            return;
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(WalletApi.class)
                .getWalletInfo(token)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgress();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<WalletHttpEntity>() {
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
                    public void onNext(WalletHttpEntity httpResult) {
                        mView.hideProgress();
                        mView.onGetDataResult(httpResult);
                    }
                });
        addSubscription(subscribe);
    }

    @Override
    public void pay(int payType, Integer integer) {
        ToastUtil.showToast(mView.getContext(), mView.getContext().getString(R.string.unOpen));
    }
}
