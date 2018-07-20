package com.zhengdao.zqb.view.activity.publishedrewards;

import android.text.TextUtils;

import com.zhengdao.zqb.api.WantedApi;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.ManagementWantedHttpEntity;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.view.adapter.PublishedWantedAdapter;

import java.util.ArrayList;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class PublishedRewardsPresenter extends BasePresenterImpl<PublishedRewardsContract.View> implements PublishedRewardsContract.Presenter, PublishedWantedAdapter.CallBack {

    private boolean mIsHasNext;
    private int mCurrentPage = 1;
    private ArrayList<ManagementWantedHttpEntity.RewardPageDetail> mDatas;
    private PublishedWantedAdapter                                 mAdapter;


    public void getData(int state, int currentPage) {
        this.mCurrentPage = currentPage;
        this.getData(state);
    }

    /**
     * @param state -1 全部 0.暂存，1.审核中，2.已发布，3.已结束，4.下架中，5.已下架
     */
    @Override
    public void getData(int state) {
        String token = SettingUtils.getUserToken(mView.getContext());
        if (TextUtils.isEmpty(token))
            return;
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(WantedApi.class)
                .getManagementWanted(mCurrentPage, state, token)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgress();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ManagementWantedHttpEntity>() {
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
                    public void onNext(ManagementWantedHttpEntity httpResult) {
                        mView.hideProgress();
                        buildData(httpResult);
                    }
                });
        addSubscription(subscribe);
    }

    @Override
    public void updataData(int state) {
        mCurrentPage = 1;
        getData(state);
    }

    @Override
    public void getMore(int state) {
        if (mIsHasNext) {
            mCurrentPage++;
            getData(state);
        }
    }

    private void buildData(ManagementWantedHttpEntity result) {
        if (result.code == Constant.HttpResult.SUCCEED) {
            if (result.rewardPage == null || result.rewardPage.list == null || result.rewardPage.list.size() == 0) {
                mView.noData();
                return;
            }
            mIsHasNext = result.rewardPage.hasNextPage;
            ArrayList<ManagementWantedHttpEntity.RewardPageDetail> message = result.rewardPage.list;
            if (mDatas == null)
                mDatas = new ArrayList<>();
            if (mCurrentPage == 1)
                mDatas.clear();
            mDatas.addAll(message);
            if (mAdapter == null) {
                mAdapter = new PublishedWantedAdapter(mView.getContext(), mDatas, this);
                mView.updataAdapter(mAdapter, mIsHasNext);
            } else {
                mView.showContentState(mIsHasNext);
                mAdapter.notifyDataSetChanged();
            }
        } else
            mView.showErrorMessage(result.msg);
    }

    @Override
    public void cancle(int id) {
        String token = SettingUtils.getUserToken(mView.getContext());
        if (TextUtils.isEmpty(token))
            return;
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(WantedApi.class)
                .cancleWanted(token, id)
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
                        mView.onCancleWantedResult(httpResult);
                    }
                });
        addSubscription(subscribe);
    }
}
