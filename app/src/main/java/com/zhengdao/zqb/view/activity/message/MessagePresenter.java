package com.zhengdao.zqb.view.activity.message;

import android.text.TextUtils;

import com.zhengdao.zqb.api.HomeApi;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.MessageEntity;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.view.adapter.MessageAdapter;

import java.util.ArrayList;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;


public class MessagePresenter extends BasePresenterImpl<MessageContract.View> implements MessageContract.Presenter{

    private boolean mIsHasNext;
    private int mCurrentPage = 1;
    private ArrayList<MessageEntity.MessageDeatilEntity> mDatas;
    private MessageAdapter                               mAdapter;

    @Override
    public void getData() {
        String token = SettingUtils.getUserToken(mView.getContext());
        if (TextUtils.isEmpty(token))
            return;
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(HomeApi.class)
                .getMessage(mCurrentPage, token)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgress();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MessageEntity>() {
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
                    public void onNext(MessageEntity httpResult) {
                        mView.hideProgress();
                        buildData(httpResult);
                    }
                });
        addSubscription(subscribe);
    }

    private void buildData(MessageEntity result) {
        if (result.code == Constant.HttpResult.SUCCEED) {
            mIsHasNext = result.message.hasNextPage;
            int total = result.message.total;
            ArrayList<MessageEntity.MessageDeatilEntity> message = result.message.list;
            if (message == null) {
                mView.noData();
                return;
            }
            if (mDatas == null)
                mDatas = new ArrayList<>();
            if (mCurrentPage == 1)
                mDatas.clear();
            mDatas.addAll(message);
            if (mAdapter == null) {
                mAdapter = new MessageAdapter(mView.getContext(), mDatas);
                mView.updataAdapter(mAdapter, total,mIsHasNext);
            } else{
                mView.showViewState(mIsHasNext);
                mAdapter.notifyDataSetChanged();
            }
        } else if (result.code == Constant.HttpResult.RELOGIN) {
            mView.ReLogin();
        } else
            mView.showErrorMessage(result.msg);
    }

    @Override
    public void updateData() {
        mCurrentPage = 1;
        getData();
    }

    @Override
    public void getMoreData() {
        if (mIsHasNext) {
            mCurrentPage++;
            getData();
        }
    }
}
