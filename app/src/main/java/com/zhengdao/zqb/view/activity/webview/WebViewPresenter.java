package com.zhengdao.zqb.view.activity.webview;

import android.text.TextUtils;

import com.zhengdao.zqb.api.UserApi;
import com.zhengdao.zqb.entity.ShareHttpEntity;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;
import com.zhengdao.zqb.utils.SettingUtils;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class WebViewPresenter extends BasePresenterImpl<WebViewContract.View> implements WebViewContract.Presenter {
    @Override
    public void getShareData() {
        try {
            String userToken = SettingUtils.getUserToken(mView.getContext());
            if (!TextUtils.isEmpty(userToken)) {
                Subscription subscribe = RetrofitManager.getInstance().noCache().create(UserApi.class)
                        .getShareInfo(userToken)
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe(new Action0() {
                            @Override
                            public void call() {
                                mView.showProgress();
                            }
                        }).subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<ShareHttpEntity>() {
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
                            public void onNext(ShareHttpEntity httpResult) {
                                mView.hideProgress();
                                mView.showSahreData(httpResult);
                            }
                        });
                addSubscription(subscribe);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
