package com.zhengdao.zqb.view.fragment.focus;

import com.zhengdao.zqb.api.WantedApi;
import com.zhengdao.zqb.entity.DictionaryHttpEntity;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class FocusPresenter extends BasePresenterImpl<FocusContract.View> implements FocusContract.Presenter {
    @Override
    public void getSwitchState() {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(WantedApi.class)
                .getTypesByKey("NEWS_STATE")
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgress();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<DictionaryHttpEntity>() {
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
                    public void onNext(DictionaryHttpEntity result) {
                        mView.hideProgress();
                        mView.onSwitchStateGet(result);
                    }
                });
        addSubscription(subscribe);
    }
}
