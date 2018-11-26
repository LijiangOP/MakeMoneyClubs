package com.zhengdao.zqb.view.fragment.home;

import android.text.TextUtils;

import com.zhengdao.zqb.api.HomeApi;
import com.zhengdao.zqb.api.UserApi;
import com.zhengdao.zqb.api.WantedApi;
import com.zhengdao.zqb.entity.DictionaryHttpEntity;
import com.zhengdao.zqb.entity.EarnEntity;
import com.zhengdao.zqb.entity.GoodsCommandHttpEntity;
import com.zhengdao.zqb.entity.HomeInfo;
import com.zhengdao.zqb.entity.MessageEntity;
import com.zhengdao.zqb.entity.ScreenLoadEntity;
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

    /**
     * 首页数据
     */
    @Override
    public void initData() {
        try {
            mCurrentPage = 1;
            String userToken = SettingUtils.getUserToken(mView.getContext());
            Subscription subscribe = RetrofitManager.getInstance().noCache().create(HomeApi.class)
                    .getData(TextUtils.isEmpty(userToken) ? "" : userToken).subscribeOn(Schedulers.io()).doOnSubscribe(new Action0() {
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
    public void getMoreData() {
        mCurrentPage++;
        getSelectionData();
    }

    /**
     * 新人福利获取
     */
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

    /**
     * 获取用户的消息数目数据
     */
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
                        }
                    }).subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<MessageEntity>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (mView != null) {
                                mView.showErrorMessage(e.getMessage());
                            }
                        }

                        @Override
                        public void onNext(MessageEntity httpResult) {
                            if (mView != null) {
                                mView.onMessageCountGet(httpResult);
                            }
                        }
                    });
            addSubscription(subscribe);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取用户个人数据
     */
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
                            }
                        }).subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<UserHomeBean>() {
                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                                if (mView != null) {
                                    mView.showErrorMessage(e.getMessage());
                                }
                            }

                            @Override
                            public void onNext(UserHomeBean httpResult) {
                                if (mView != null) {
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


    private int    mCurrentPage = 1;
    private String mSortName    = "order";//(joincount =人气,money = 奖励 默认空)
    private String mSortOrder   = "desc";//排序方式(正序 = asc,倒序 = desc 默认空)
    private int    mClassify    = -1;//业务类型(默认 ID为-1)
    private int    mCategory    = -1;//悬赏类型(默认 ID为-1)
    private String mSearch      = "";//搜索字段(默认 空)
    private int    mType        = -1;//请求类型(默认 -1)
    private int    mBlock       = 0;//导航类型(默认 0)

    @Override
    public void getDataWithBaseSearch(String sortName, String sortOrder, int type) {
        mCurrentPage = 1;
        this.mSortName = sortName;
        this.mSortOrder = sortOrder;
        this.mType = type;
        this.mSearch = "";
        this.getSelectionData();
    }

    @Override
    public void getDataWithFilter(int classify, int category) {
        mCurrentPage = 1;
        this.mClassify = classify;
        this.mCategory = category;
        this.getSelectionData();
    }


    public void getSelectionData() {
        String token = SettingUtils.getUserToken(mView.getContext());
        Subscription subscribe;
        if (TextUtils.isEmpty(token)) {
            subscribe = RetrofitManager.getInstance().noCache().create(HomeApi.class)
                    .getEarnData(mBlock, mCurrentPage, mSortName, mSortOrder, mClassify, mCategory, mType, mSearch)
                    .subscribeOn(Schedulers.io()).doOnSubscribe(new Action0() {
                        @Override
                        public void call() {
                            mView.showProgress();
                        }
                    })
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<EarnEntity>() {
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
                        public void onNext(EarnEntity result) {
                            mView.hideProgress();
                            if (mCurrentPage == 1)
                                mView.onRefreshZeroEarn(result);
                            else
                                mView.onGetMoreData(result);
                        }
                    });
        } else {
            subscribe = RetrofitManager.getInstance().noCache().create(HomeApi.class)
                    .getEarnData(mBlock, mCurrentPage, mSortName, mSortOrder, mClassify, mCategory, mType, mSearch, token)
                    .subscribeOn(Schedulers.io()).doOnSubscribe(new Action0() {
                        @Override
                        public void call() {
                            mView.showProgress();
                        }
                    })
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<EarnEntity>() {
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
                        public void onNext(EarnEntity result) {
                            mView.hideProgress();
                            if (mCurrentPage == 1)
                                mView.onRefreshZeroEarn(result);
                            else
                                mView.onGetMoreData(result);
                        }
                    });
        }
        addSubscription(subscribe);
    }

    @Override
    public void getSelectTypeData() {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(HomeApi.class)
                .getSelectedData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ScreenLoadEntity>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showErrorMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(ScreenLoadEntity result) {
                        mView.showSelectTypeView(result);
                    }
                });
        addSubscription(subscribe);
    }

    @Override
    public void getAliPayRedPacket(final String key) {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(WantedApi.class)
                .getTypesByKey(key)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<DictionaryHttpEntity>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.hideProgress();
                        mView.showErrorMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(DictionaryHttpEntity result) {
                        mView.hideProgress();
                        mView.onDictionaryDataGet(result, key);
                    }
                });
        addSubscription(subscribe);
    }

    @Override
    public void getRecommendReward() {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(HomeApi.class)
                .getRecommendReward()
                .subscribeOn(Schedulers.io()).doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GoodsCommandHttpEntity>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.hideProgress();
                    }

                    @Override
                    public void onNext(GoodsCommandHttpEntity httpResult) {
                        mView.hideProgress();
                        mView.onGetReCommandResult(httpResult);
                    }
                });
        addSubscription(subscribe);
    }
}
