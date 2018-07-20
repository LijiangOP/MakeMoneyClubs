package com.zhengdao.zqb.view.activity.licaidetail;

import com.zhengdao.zqb.api.RebateAPi;
import com.zhengdao.zqb.entity.HttpLiCaiDetailEntity;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class LicaiDetailPresenter extends BasePresenterImpl<LicaiDetailContract.View> implements LicaiDetailContract.Presenter{

    @Override
    public void initData(int id) {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(RebateAPi.class)
                .getLiCaiDetail(id)
                .subscribeOn(Schedulers.io()).doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgress();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HttpLiCaiDetailEntity>() {
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
                    public void onNext(HttpLiCaiDetailEntity result) {
                        mView.hideProgress();
                        mView.showResult(result);
                    }
                });
        addSubscription(subscribe);
    }
}
