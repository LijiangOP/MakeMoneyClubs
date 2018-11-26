package com.zhengdao.zqb.view.fragment.daily;

import android.text.TextUtils;

import com.zhengdao.zqb.api.HomeApi;
import com.zhengdao.zqb.api.MissionApi;
import com.zhengdao.zqb.api.UserApi;
import com.zhengdao.zqb.entity.GameListHttpResult;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.NewBieHttpEntity;
import com.zhengdao.zqb.entity.SurveyHttpResult;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.utils.ToastUtil;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class DailyPresenter extends BasePresenterImpl<DailyContract.View> implements DailyContract.Presenter {
    @Override
    public void getData() {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(MissionApi.class)
                .getNewHandData(SettingUtils.getUserToken(mView.getContext()))
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgress();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<NewBieHttpEntity>() {
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
                    public void onNext(NewBieHttpEntity httpResult) {
                        mView.hideProgress();
                        mView.showView(httpResult);
                    }
                });
        addSubscription(subscribe);
    }

    @Override
    public void getSeeAdvReward(int address, int type) {
        String userToken = SettingUtils.getUserToken(mView.getContext());
        if (TextUtils.isEmpty(userToken)) {
            //            mView.ReLogin();用户体验不好
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
    public void getGameReward(int type) {
        String userToken = SettingUtils.getUserToken(mView.getContext());
        if (TextUtils.isEmpty(userToken)) {
            return;
        }
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(UserApi.class)
                .getReward(userToken, type)
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
                        mView.onRewardGet(httpResult);
                    }
                });
        addSubscription(subscribe);
    }

    /**
     * 获取问卷调查链接
     */
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

    @Override
    public void getGameList() {
        try {
            Subscription subscribe = RetrofitManager.getInstance().noCache().create(HomeApi.class)
                    .getGameList()
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
                    .subscribe(new Subscriber<GameListHttpResult>() {
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
                        public void onNext(GameListHttpResult httpResult) {
                            if (mView != null) {
                                mView.hideProgress();
                                mView.onGetGameList(httpResult);
                            }
                        }
                    });
            addSubscription(subscribe);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
