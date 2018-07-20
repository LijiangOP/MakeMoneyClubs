package com.zhengdao.zqb.view.activity.chargeaccount;

import android.text.TextUtils;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.api.WalletApi;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.utils.ToastUtil;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class ChargeAccountPresenter extends BasePresenterImpl<ChargeAccountContract.View> implements ChargeAccountContract.Presenter {

    @Override
    public void doCharge(int chargeType, String number) {
        String userToken = SettingUtils.getUserToken(mView.getContext());
        switch (chargeType) {
            case 0:
                ToastUtil.showToast(mView.getContext(), mView.getContext().getResources().getString(R.string.unOpen));
                break;
            case 1:
                if (!TextUtils.isEmpty(userToken)) {
                    Subscription subscribe = RetrofitManager.getInstance().noCache().create(WalletApi.class)
                            .doAlipayCharge(userToken, number)
                            .subscribeOn(Schedulers.io())
                            .doOnSubscribe(new Action0() {
                                @Override
                                public void call() {
                                    mView.showProgress();
                                }
                            }).subscribeOn(AndroidSchedulers.mainThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<HttpResult<String>>() {
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
                                public void onNext(HttpResult<String> httpResult) {
                                    mView.hideProgress();
                                    mView.onChargeResult(httpResult);
                                }
                            });
                    addSubscription(subscribe);
                    break;
                }
        }
    }
}
