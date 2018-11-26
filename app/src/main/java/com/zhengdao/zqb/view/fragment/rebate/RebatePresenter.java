package com.zhengdao.zqb.view.fragment.rebate;

import android.app.Activity;
import android.text.TextUtils;

import com.zhengdao.zqb.api.HomeApi;
import com.zhengdao.zqb.api.RebateAPi;
import com.zhengdao.zqb.api.UserApi;
import com.zhengdao.zqb.api.WantedApi;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.Coupons;
import com.zhengdao.zqb.entity.CouponsEntity;
import com.zhengdao.zqb.entity.DictionaryHttpEntity;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.RebateBean;
import com.zhengdao.zqb.entity.RebateEntity;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.view.adapter.CouponsAdapter;
import com.zhengdao.zqb.view.adapter.RebateAdapter;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class RebatePresenter extends BasePresenterImpl<RebateContract.View> implements RebateContract.Presenter {

    private boolean mRebateIsHasNext   = false;
    private boolean mTicketIsHasNext   = false;
    private int     mRebateCurrentPage = 1;
    private int     mTicketCurrentPage = 1;
    private List<RebateBean>    mRebateData;
    private List<CouponsEntity> mTicketData;
    private RebateAdapter       mRebateAdapter;
    private CouponsAdapter      mCouponsAdapter;
    private String mSearchValue = "";

    private int mSwitchState = 1;//0开启;1关闭 默认关闭

    public void setSwitchState(int switchState) {
        mSwitchState = switchState;
    }

    /**
     * 返利页面的开启和关闭状态获取
     */
    @Override
    public void getSwitchState() {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(WantedApi.class)
                .getTypesByKey("JUMP_STATE")
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

    /**
     * 获取页面数据
     */
    @Override
    public void initData() {
        if (mSwitchState == 0) {//返利
            Subscription subscribe = RetrofitManager.getInstance().noCache().create(RebateAPi.class)
                    .getRebateData(mRebateCurrentPage)
                    .subscribeOn(Schedulers.io()).doOnSubscribe(new Action0() {
                        @Override
                        public void call() {
                            mView.showProgress();
                        }
                    })
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<HttpResult<RebateEntity>>() {
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
                        public void onNext(HttpResult<RebateEntity> result) {
                            mView.hideProgress();
                            buildRebateData(result);
                        }
                    });
            addSubscription(subscribe);
        } else {//优惠券
            Subscription subscribe = RetrofitManager.getInstance().noCache().create(HomeApi.class)
                    .getCouponsData(mSearchValue, mTicketCurrentPage)
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe(new Action0() {
                        @Override
                        public void call() {
                            mView.showProgress();
                        }
                    }).subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<HttpResult<Coupons>>() {
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
                        public void onNext(HttpResult<Coupons> httpResult) {
                            mView.hideProgress();
                            buildTicketData(httpResult);
                        }
                    });
            addSubscription(subscribe);
        }

    }

    @Override
    public void refresh() {
        mRebateCurrentPage = 1;
        mTicketCurrentPage = 1;
        initData();
    }

    @Override
    public void getMore() {
        if (mSwitchState == 0) {
            if (mRebateIsHasNext) {
                mRebateCurrentPage++;
                initData();
            }
        } else {
            if (mTicketIsHasNext) {
                mTicketCurrentPage++;
                initData();
            }
        }
    }

    /**
     * 获取点击广告后的奖励
     * @param address 广告位置
     * @param type 广告类型
     */
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

    public void doSearch(String value) {
        mTicketCurrentPage = 1;
        mSearchValue = value;
        initData();
    }

    /**
     * 返利页面数据
     * @param result
     */
    private void buildRebateData(HttpResult result) {
        try {
            if (result.code == Constant.HttpResult.SUCCEED) {
                if (result.data == null) {
                    if (mRebateCurrentPage == 1)
                        mView.noData();
                    return;
                }
                RebateEntity data = (RebateEntity) result.data;
                if (data.list == null || data.list.size() == 0) {
                    if (mRebateCurrentPage == 1)
                        mView.noData();
                    return;
                }
                mRebateIsHasNext = data.hasNextPage;
                List<RebateBean> dataList = data.list;
                if (mRebateData == null)
                    mRebateData = new ArrayList<>();
                if (mRebateCurrentPage == 1) {
                    mRebateData.clear();
                    mRebateData.add(new RebateBean(1));
                }
                mRebateData.addAll(dataList);
                if (mRebateAdapter == null) {
                    mRebateAdapter = new RebateAdapter((Activity) mView.getContext(), mRebateData, mView);
                    mView.setAdapter(mRebateAdapter, mRebateIsHasNext);
                } else {
                    mView.refreshAdapter(mRebateIsHasNext);
                    mRebateAdapter.notifyDataSetChanged();
                }
            } else if (result.code == Constant.HttpResult.RELOGIN) {
                mView.ReLogin();
            } else
                mView.showErrorMessage(result.msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 优惠券页面数据
     * @param httpResult
     */
    private void buildTicketData(HttpResult<Coupons> httpResult) {
        try {
            if (httpResult.code == Constant.HttpResult.SUCCEED) {
                if (mTicketData == null)
                    mTicketData = new ArrayList<>();
                if (mTicketCurrentPage == 1) {
                    mTicketData.clear();
                    mTicketData.add(new CouponsEntity(Constant.Coupons.Head));
                }
                mTicketIsHasNext = httpResult.data.hasNextPage;
                ArrayList<CouponsEntity> result = httpResult.data.list;
                //集合和头部
                if (mTicketData == null)
                    mTicketData = new ArrayList<>();
                if (mTicketCurrentPage == 1) {
                    mTicketData.clear();
                    mTicketData.add(new CouponsEntity(Constant.Coupons.Head));
                }
                //商品
                if (result.size() == 0 || result == null) {
                    if (mTicketCurrentPage == 1)
                        mTicketData.add(new CouponsEntity(Constant.Coupons.Empty));//空类型
                } else {
                    mTicketData.addAll(result);
                }
                if (mCouponsAdapter == null) {
                    mCouponsAdapter = new CouponsAdapter(mView.getContext(), mTicketData, mView);
                    mView.setAdapter(mCouponsAdapter, mTicketIsHasNext);
                } else {
                    mView.refreshAdapter(mTicketIsHasNext);
                    mCouponsAdapter.notifyDataSetChanged();
                }
            } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
                mView.ReLogin();
            } else
                mView.showErrorMessage(httpResult.msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
