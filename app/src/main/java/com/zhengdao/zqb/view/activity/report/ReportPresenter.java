package com.zhengdao.zqb.view.activity.report;

import android.text.TextUtils;

import com.zhengdao.zqb.api.UpLoadApi;
import com.zhengdao.zqb.api.UserApi;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;
import com.zhengdao.zqb.utils.SettingUtils;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.RequestBody;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class ReportPresenter extends BasePresenterImpl<ReportContract.View> implements ReportContract.Presenter{

    @Override
    public void uploadImages(RequestBody type, Map<String, RequestBody> file) {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(UpLoadApi.class)
                .uploadImages(type, file)
                .subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.io()).doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgress();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HttpResult<ArrayList<String>>>() {
                    @Override
                    public void onCompleted() {
                        mView.hideProgress();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.hideProgress();
                        mView.onImgUploadError();
                    }

                    @Override
                    public void onNext(HttpResult<ArrayList<String>> result) {
                        mView.hideProgress();
                        mView.onImgUploadResult(result);
                    }
                });
        addSubscription(subscribe);
    }


    @Override
    public void FeedbackOpinion(int type, String describe, long userId, int i, ArrayList<String> strings) {
        String userToken = SettingUtils.getUserToken(mView.getContext());
        if (TextUtils.isEmpty(userToken))
            return;
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(UserApi.class)
                .feedbackOpinion(userToken, String.valueOf(type), describe, userId, String.valueOf(i), strings)
                .subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.io()).doOnSubscribe(new Action0() {
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
                    public void onNext(HttpResult result) {
                        mView.hideProgress();
                        mView.onUploadResult(result);
                    }
                });
        addSubscription(subscribe);
    }
}
