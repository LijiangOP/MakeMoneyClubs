package com.zhengdao.zqb.view.activity.zeroearn;

import android.text.TextUtils;

import com.zhengdao.zqb.api.HomeApi;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.EarnEntity;
import com.zhengdao.zqb.entity.HomeItemEntity;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.view.adapter.GoodsAdapter;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class ZeroEarnPresenter extends BasePresenterImpl<ZeroEarnContract.View> implements ZeroEarnContract.Presenter {

    private GoodsAdapter              mAdapter;
    private ArrayList<HomeItemEntity> mDatas;
    private boolean                   mIsHasNext;
    private int                       mCurrentPage = 1;
    private String                    mSortName    = "order";//(joincount =人气,money = 奖励 默认空)
    private String                    mSortOrder   = "desc";//排序方式(正序 = asc,倒序 = desc 默认空)
    private int                       mClassify    = -1;//业务类型(默认 ID为-1)
    private int                       mCategory    = -1;//悬赏类型(默认 ID为-1)
    private String                    mSearch      = "";//搜索字段(默认 空)
    private int                       mType        = -1;//请求类型(默认 -1)
    private int                       mBlock       = 0;//导航类型(默认 0)

    public void setCurrentPage(int currentPage) {
        mCurrentPage = currentPage;
    }

    /**
     * 3,赚钱大厅基本搜索（综合,人气,奖励）
     */
    public void getDataWithBaseSearch(String sortName, String sortOrder, int type) {
        mCurrentPage = 1;
        this.mSortName = sortName;
        this.mSortOrder = sortOrder;
        this.mType = type;
        this.mSearch = "";
        this.initData();
    }

    /**
     * 4,赚钱大厅筛选搜索（筛选模块）
     */
    public void getDataWithFilter(int classify, int category) {
        mCurrentPage = 1;
        this.mClassify = classify;
        this.mCategory = category;
        this.initData();
    }

    @Override
    public void getMoreData() {
        if (mIsHasNext) {
            mCurrentPage++;
            initData();
        }
    }

    @Override
    public void updateData() {
        mCurrentPage = 1;
        initData();
    }

    @Override
    public void initData() {
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
                            buildData(result);
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
                            buildData(result);
                        }
                    });
        }
        addSubscription(subscribe);
    }


    private void buildData(EarnEntity result) {
        if (result.code == Constant.HttpResult.SUCCEED) {
            if (result.rewards == null || result.rewards.list == null || result.rewards.list.size() == 0) {
                mView.noData();
                return;
            }
            mIsHasNext = result.rewards.hasNextPage;
            List<HomeItemEntity> dataList = result.rewards.list;
            if (mDatas == null)
                mDatas = new ArrayList<>();
            if (mCurrentPage == 1)
                mDatas.clear();
            mDatas.addAll(dataList);
            if (mAdapter == null) {
                mAdapter = new GoodsAdapter(mView.getContext(), mDatas);
                mView.updataAdapter(mAdapter, mIsHasNext);
            } else {
                mView.showViewState(mIsHasNext);
                mAdapter.notifyDataSetChanged();
            }
        } else if (result.code == Constant.HttpResult.RELOGIN) {
            mView.ReLogin();
        } else
            mView.showErrorMessage(result.msg);
    }
}
