package com.zhengdao.zqb.view.activity.announcementdeatil;

import com.zhengdao.zqb.api.HomeApi;
import com.zhengdao.zqb.entity.AnnouncementDetailBean;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class AnnouncementDeatilPresenter extends BasePresenterImpl<AnnouncementDeatilContract.View> implements AnnouncementDeatilContract.Presenter{

    @Override
    public void getData(int id) {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(HomeApi.class)
                .getNoticeDetail(id)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgress();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HttpResult<AnnouncementDetailBean>>() {
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
                    public void onNext(HttpResult<AnnouncementDetailBean> httpResult) {
                        mView.hideProgress();
                        mView.showView(httpResult);
                    }
                });
        addSubscription(subscribe);
    }
}
