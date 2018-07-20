package com.zhengdao.zqb.view.activity.wanteddetail;

import android.content.Intent;
import android.text.TextUtils;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.api.WantedApi;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.WantedDetailHttpEntity;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.view.activity.homegoodsdetail.HomeGoodsDetailActivity;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class WantedDetailPresenter extends BasePresenterImpl<WantedDetailContract.View> implements WantedDetailContract.Presenter{

    @Override
    public void getData(int wantedId) {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(WantedApi.class)
                .getWantedDetailInfo(wantedId)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgress();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<WantedDetailHttpEntity>() {
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
                    public void onNext(WantedDetailHttpEntity httpResult) {
                        mView.hideProgress();
                        mView.onGetDataResult(httpResult);
                    }
                });
        addSubscription(subscribe);
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
        mView.getContext().startActivity(intent);
    }

    @Override
    public void evaluate(int taskedId) {
        ToastUtil.showToast(mView.getContext(), mView.getContext().getString(R.string.unOpen));
        //        Intent intent = new Intent(mView.getContext(), EvaluateActivity.class);
        //        intent.putExtra(Constant.Activity.Data, wantedId);
        //        mView.getContext().startActivity(intent);
    }
}
