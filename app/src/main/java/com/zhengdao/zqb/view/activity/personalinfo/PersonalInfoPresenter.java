package com.zhengdao.zqb.view.activity.personalinfo;

import android.text.TextUtils;

import com.zhengdao.zqb.api.UpLoadApi;
import com.zhengdao.zqb.api.UserApi;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.UserInfoBean;
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

public class PersonalInfoPresenter extends BasePresenterImpl<PersonalInfoContract.View> implements PersonalInfoContract.Presenter {

    @Override
    public void getUserData() {
        String token = SettingUtils.getUserToken(mView.getContext());
        if (TextUtils.isEmpty(token))
            return;
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(UserApi.class)
                .getUserInfo(token)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgress();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UserInfoBean>() {
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
                    public void onNext(UserInfoBean httpResult) {
                        mView.hideProgress();
                        mView.ShowView(httpResult);
                    }
                });
        addSubscription(subscribe);
    }

    @Override
    public void uploadImages(RequestBody type, Map<String, RequestBody> file) {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(UpLoadApi.class)
                .uploadImages(type, file)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
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
                        mView.showErrorMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(HttpResult<ArrayList<String>> result) {
                        mView.hideProgress();
                        mView.onUploadAvatarResult(result);
                    }
                });
        addSubscription(subscribe);
    }

    @Override
    public void editUserInfo(String json) {
        if (TextUtils.isEmpty(json))
            return;
        String token = SettingUtils.getUserToken(mView.getContext());
        if (!TextUtils.isEmpty(token)) {
            Subscription subscribe = RetrofitManager.getInstance().noCache().create(UserApi.class)
                    .changeUserInfo(token, json)
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
                            mView.showEditResult(httpResult);
                        }
                    });
            addSubscription(subscribe);
        }
    }
}
