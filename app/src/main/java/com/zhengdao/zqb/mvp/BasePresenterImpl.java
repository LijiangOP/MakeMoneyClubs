package com.zhengdao.zqb.mvp;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class BasePresenterImpl<V extends BaseView> implements BasePresenter<V> {
    private   CompositeSubscription mCompositeSubscription;
    protected V mView;

    @Override
    public void attachView(V view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
    }

    public void addSubscription(Subscription s) {
        if (this.mCompositeSubscription == null) {
            this.mCompositeSubscription = new CompositeSubscription();
        }
        this.mCompositeSubscription.add(s);
    }

    public void unsubcrible() {
        if (this.mCompositeSubscription != null) {
            this.mCompositeSubscription.unsubscribe();
        }
    }
}
