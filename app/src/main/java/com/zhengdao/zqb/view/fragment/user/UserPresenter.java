package com.zhengdao.zqb.view.fragment.user;

import android.text.TextUtils;

import com.zhengdao.zqb.api.HomeApi;
import com.zhengdao.zqb.api.UserApi;
import com.zhengdao.zqb.entity.AdvertisementHttpEntity;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.SurveyHttpResult;
import com.zhengdao.zqb.entity.UserHomeBean;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.utils.ToastUtil;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class UserPresenter extends BasePresenterImpl<UserContract.View> implements UserContract.Presenter {

    @Override
    public void getUserData() {
        String userToken = SettingUtils.getUserToken(mView.getContext());
        if (TextUtils.isEmpty(userToken)) {
            mView.ReLogin();
            return;
        }
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(UserApi.class)
                .getUserHomeInfo(userToken)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgress();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UserHomeBean>() {
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
                    public void onNext(UserHomeBean httpResult) {
                        mView.hideProgress();
                        mView.showView(httpResult);
                    }
                });
        addSubscription(subscribe);
    }

    @Override
    public void getAdvReplace(int position) {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(HomeApi.class)
                .getAdvInfo(position)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgress();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AdvertisementHttpEntity>() {
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
                    public void onNext(AdvertisementHttpEntity httpResult) {
                        mView.hideProgress();
                        mView.onGetAdvReplace(httpResult);
                    }
                });
        addSubscription(subscribe);
    }

    @Override
    public void getInvitedReward(String investCode) {
        String userToken = SettingUtils.getUserToken(mView.getContext());
        if (TextUtils.isEmpty(userToken)) {
            mView.ReLogin();
            return;
        }
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(UserApi.class)
                .getInvitedReward(investCode, userToken)
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
                        mView.onGetInvitedReward(httpResult);
                    }
                });
        addSubscription(subscribe);
    }

    @Override
    public void getSeeAdvReward(int address, int type) {
        String userToken = SettingUtils.getUserToken(mView.getContext());
        if (TextUtils.isEmpty(userToken)) {
            return;
        }
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(UserApi.class)
                .getSeeAdvReward(userToken, address, type)
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
                        mView.onGetAdvReward(httpResult);
                    }
                });
        addSubscription(subscribe);
    }

    @Override
    public void getSurveyLink() {
        try {
            String token = SettingUtils.getUserToken(mView.getContext());
            if (TextUtils.isEmpty(token)) {
                ToastUtil.showToast(mView.getContext(), "请先登录");
                mView.ReLogin();
                return;
            }
            Subscription subscribe = RetrofitManager.getInstance().noCache().create(HomeApi.class)
                    .getSurveyLink(token)
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe(new Action0() {
                        @Override
                        public void call() {
                            if (mView != null) {
                                mView.showProgress();
                            }
                        }
                    }).subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<SurveyHttpResult>() {
                        @Override
                        public void onCompleted() {
                            if (mView != null) {
                                mView.hideProgress();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (mView != null) {
                                mView.hideProgress();
                                mView.showErrorMessage(e.getMessage());
                            }
                        }

                        @Override
                        public void onNext(SurveyHttpResult httpResult) {
                            if (mView != null) {
                                mView.hideProgress();
                                mView.onSurveyLinkGet(httpResult);
                            }
                        }
                    });
            addSubscription(subscribe);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
