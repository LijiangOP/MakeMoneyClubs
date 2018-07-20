package com.zhengdao.zqb.view.activity.activitycenter;

import com.zhengdao.zqb.api.UserApi;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.ActivityCenterDetailEntity;
import com.zhengdao.zqb.entity.ActivityCenterHttpEntity;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;
import com.zhengdao.zqb.view.adapter.ActivityCenterAdapter;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class ActivityCenterPresenter extends BasePresenterImpl<ActivityCenterContract.View> implements ActivityCenterContract.Presenter {

    private List<ActivityCenterDetailEntity> mData;
    private int mCurrentPage = 1;
    private boolean               mIsHasNext;
    private ActivityCenterAdapter mAdapter;

    @Override
    public void initData() {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(UserApi.class)
                .getActivityCenterData(mCurrentPage)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgress();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ActivityCenterHttpEntity>() {
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
                    public void onNext(ActivityCenterHttpEntity httpEntity) {
                        mView.hideProgress();
                        initListData(httpEntity);
                    }
                });
        addSubscription(subscribe);
    }

    private void initListData(ActivityCenterHttpEntity httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            mIsHasNext = httpResult.activityCenter.hasNextPage;
            ArrayList<ActivityCenterDetailEntity> result = httpResult.activityCenter.list;
            if (result.size() == 0 || result == null) {
                mView.noData();
                return;
            }
            if (mData == null)
                mData = new ArrayList<>();
            if (mCurrentPage == 1)
                mData.clear();
            mData.addAll(result);
            if (mAdapter == null) {
                mAdapter = new ActivityCenterAdapter(mView.getContext(), mData);
                mView.setAdapter(mAdapter, mIsHasNext);
            } else {
                mView.updateAdapter(mIsHasNext);
                mAdapter.notifyDataSetChanged();
            }
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            mView.ReLogin();
        } else
            mView.showErrorMessage(httpResult.msg);
    }

    @Override
    public void updataData() {
        mCurrentPage = 1;
        initData();
    }

    @Override
    public void getMoreData() {
        if (mIsHasNext) {
            mCurrentPage++;
            initData();
        }
    }
}
