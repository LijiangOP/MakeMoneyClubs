package com.zhengdao.zqb.view.activity.evaluate;

import com.zhengdao.zqb.mvp.BasePresenterImpl;

public class EvaluatePresenter extends BasePresenterImpl<EvaluateContract.View> implements EvaluateContract.Presenter {

    @Override
    public void doPublish(int wantedId) {
        //        Subscription subscribe = RetrofitManager.getInstance().noCache().create(UserApi.class)
        //                .doPublic(token, wantedId)
        //                .subscribeOn(Schedulers.io())
        //                .doOnSubscribe(new Action0() {
        //                    @Override
        //                    public void call() {
        //                        mView.showProgress();
        //                    }
        //                }).subscribeOn(AndroidSchedulers.mainThread())
        //                .observeOn(AndroidSchedulers.mainThread())
        //                .subscribe(new Subscriber<HttpResult>() {
        //                    @Override
        //                    public void onCompleted() {
        //                        mView.hideProgress();
        //                    }
        //
        //                    @Override
        //                    public void onError(Throwable e) {
        //                        mView.hideProgress();
        //                        mView.showErrorMessage(e.getMessage());
        //                    }
        //
        //                    @Override
        //                    public void onNext(HttpResult httpResult) {
        //                        mView.hideProgress();
        //                        mView.onDoPublicResult(httpResult);
        //                    }
        //                });
        //        addSubscription(subscribe);
    }
}
