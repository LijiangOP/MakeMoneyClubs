package com.zhengdao.zqb.view.fragment.home;

import android.text.TextUtils;

import com.zhengdao.zqb.api.HomeApi;
import com.zhengdao.zqb.api.UserApi;
import com.zhengdao.zqb.entity.EarnEntity;
import com.zhengdao.zqb.entity.HomeInfo;
import com.zhengdao.zqb.entity.MessageEntity;
import com.zhengdao.zqb.entity.SurveyHttpResult;
import com.zhengdao.zqb.entity.UserHomeBean;
import com.zhengdao.zqb.entity.WelfareHttpData;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.utils.ToastUtil;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class HomePresenter extends BasePresenterImpl<HomeContract.View> implements HomeContract.Presenter {

    @Override
    public void initData() {
        try {
            Subscription subscribe = RetrofitManager.getInstance().noCache().create(HomeApi.class)
                    .getData().subscribeOn(Schedulers.io()).doOnSubscribe(new Action0() {
                        @Override
                        public void call() {
                            if (mView != null) {
                                mView.showProgress();
                            }
                        }
                    })
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<HomeInfo>() {
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
                        public void onNext(HomeInfo homeInfoHttpResult) {
                            if (mView != null) {
                                mView.hideProgress();
                                mView.buildData(homeInfoHttpResult);
                            }
                        }
                    });
            addSubscription(subscribe);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getMore(int currentPage) {
        try {
            Subscription subscribe = RetrofitManager.getInstance().noCache().create(HomeApi.class)
                    .getEarnData1(currentPage, "order", "desc", 10)
                    .subscribeOn(Schedulers.io()).doOnSubscribe(new Action0() {
                        @Override
                        public void call() {
                            if (mView != null) {
                                mView.showProgress();
                            }
                        }
                    })
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<EarnEntity>() {
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
                        public void onNext(EarnEntity result) {
                            if (mView != null) {
                                mView.hideProgress();
                                mView.onGetMoreData(result);
                            }
                        }
                    });
            addSubscription(subscribe);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getWelfareData() {
        try {
            Subscription subscribe = RetrofitManager.getInstance().noCache().create(HomeApi.class)
                    .getWelfareData().subscribeOn(Schedulers.io()).doOnSubscribe(new Action0() {
                        @Override
                        public void call() {
                        }
                    })
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<WelfareHttpData>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onNext(WelfareHttpData welfareDataBean) {
                            if (mView != null) {
                                mView.showWelfareWindow(welfareDataBean);
                            }
                        }
                    });
            addSubscription(subscribe);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getMessageCount() {
        try {
            String token = SettingUtils.getUserToken(mView.getContext());
            if (TextUtils.isEmpty(token))
                return;
            Subscription subscribe = RetrofitManager.getInstance().noCache().create(HomeApi.class)
                    .getMessage(1, token)
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
                    .subscribe(new Subscriber<MessageEntity>() {
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
                        public void onNext(MessageEntity httpResult) {
                            if (mView != null) {
                                mView.hideProgress();
                                mView.onMessageCountGet(httpResult);
                            }
                        }
                    });
            addSubscription(subscribe);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getUserData() {
        try {
            String userToken = SettingUtils.getUserToken(mView.getContext());
            if (!TextUtils.isEmpty(userToken)) {
                Subscription subscribe = RetrofitManager.getInstance().noCache().create(UserApi.class)
                        .getUserHomeInfo(userToken)
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
                        .subscribe(new Subscriber<UserHomeBean>() {
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
                            public void onNext(UserHomeBean httpResult) {
                                if (mView != null) {
                                    mView.hideProgress();
                                    mView.showRewardWindow(httpResult);
                                }
                            }
                        });
                addSubscription(subscribe);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getSurveyLink() {
        try {
            String token = SettingUtils.getUserToken(mView.getContext());
            if (TextUtils.isEmpty(token)) {
                ToastUtil.showToast(mView.getContext(), "请先登录");
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
