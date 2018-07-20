package com.zhengdao.zqb.view.activity.wanted;

import android.content.Intent;
import android.text.TextUtils;

import com.zhengdao.zqb.api.WantedApi;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.MyWantedDetailEntity;
import com.zhengdao.zqb.entity.MyWantedEntity;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.view.activity.evaluate.EvaluateActivity;
import com.zhengdao.zqb.view.activity.homegoodsdetail.HomeGoodsDetailActivity;
import com.zhengdao.zqb.view.adapter.WantedAdapter;

import java.util.ArrayList;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class WantedPresenter extends BasePresenterImpl<WantedContract.View> implements WantedContract.Presenter, WantedAdapter.ViewEventCallBack {

    private boolean mIsHasNext;
    private int mCurrentPage = 1;
    private ArrayList<MyWantedDetailEntity> mDatas;
    private WantedAdapter                   mAdapter;
    private int                             mCurrentType;

    public void getData(int state, int currentPage) {
        this.mCurrentPage = currentPage;
        this.getData(state);
    }

    @Override
    public void getData(int state) {
        mCurrentType = state;
        String token = SettingUtils.getUserToken(mView.getContext());
        if (TextUtils.isEmpty(token))
            return;
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(WantedApi.class)
                .getWanted(mCurrentPage, state, token)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgress();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MyWantedEntity>() {
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
                    public void onNext(MyWantedEntity httpResult) {
                        mView.hideProgress();
                        buildData(httpResult);
                    }
                });
        addSubscription(subscribe);

    }

    private void buildData(MyWantedEntity result) {
        if (result.code == Constant.HttpResult.SUCCEED) {
            if (result.pageTask == null || result.pageTask.list == null || result.pageTask.list.size() == 0) {
                mView.noData();
                return;
            }
            mIsHasNext = result.pageTask.hasNextPage;
            ArrayList<MyWantedDetailEntity> message = result.pageTask.list;
            if (mDatas == null)
                mDatas = new ArrayList<>();
            if (mCurrentPage == 1)
                mDatas.clear();
            mDatas.addAll(message);
            if (mAdapter == null) {
                mAdapter = new WantedAdapter(mView.getContext(), mDatas, this);
                mView.updataAdapter(mAdapter, mIsHasNext);
            } else {
                mView.showContentState(mIsHasNext);
                mAdapter.notifyDataSetChanged();
            }
        } else
            mView.showErrorMessage(result.msg);
    }

    @Override
    public void updataData() {
        mCurrentPage = 1;
        getData(mCurrentType);
    }

    @Override
    public void getMoreData() {
        if (mIsHasNext) {
            mCurrentPage++;
            getData(mCurrentType);
        }
    }

    @Override
    public void remindCheck(int wantedId) {
        String token = SettingUtils.getUserToken(mView.getContext());
        if (TextUtils.isEmpty(token))
            return;
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(WantedApi.class)
                .remindCheck(token, wantedId)
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
                        mView.onRemindCheckResult(httpResult);
                    }
                });
        addSubscription(subscribe);
    }

    @Override
    public void cancleMission(int wantedId) {
        String token = SettingUtils.getUserToken(mView.getContext());
        if (TextUtils.isEmpty(token))
            return;
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(WantedApi.class)
                .cancleMission(token, wantedId)
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
                        mView.onCancleMissionResult(httpResult);
                    }
                });
        addSubscription(subscribe);
    }

    @Override
    public void commite(int wantedId) {
        Intent intent = new Intent(mView.getContext(), HomeGoodsDetailActivity.class);
        intent.putExtra(Constant.Activity.Data, wantedId);
        intent.putExtra(Constant.Activity.Type, "commit");
        mView.getContext().startActivity(intent);
    }

    @Override
    public void evaluate(int wantedId) {
        Intent intent = new Intent(mView.getContext(), EvaluateActivity.class);
        intent.putExtra(Constant.Activity.Data, wantedId);
        mView.getContext().startActivity(intent);
    }
}
