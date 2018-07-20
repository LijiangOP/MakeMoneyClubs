package com.zhengdao.zqb.view.activity.bindnewphone;

import com.zhengdao.zqb.api.LogInApi;
import com.zhengdao.zqb.entity.ConfirmCodeEntity;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.UserInfo;
import com.zhengdao.zqb.entity.WechatBaseEntity;
import com.zhengdao.zqb.entity.WechatCheckEntity;
import com.zhengdao.zqb.entity.WechatUserEntity;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.SettingUtils;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;


public class BindNewPhonePresenter extends BasePresenterImpl<BindNewPhoneContract.View> implements BindNewPhoneContract.Presenter {
    @Override
    public void getConfirmCode(String phone) {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(LogInApi.class)
                .getConfirmCode(phone)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgress();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ConfirmCodeEntity>() {
                    @Override
                    public void onCompleted() {
                        mView.hideProgress();
                        mView.getConfirmCodeSuccess();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.hideProgress();
                    }

                    @Override
                    public void onNext(ConfirmCodeEntity httpResult) {
                        mView.hideProgress();
                        mView.onGetConfirmCodeResult(httpResult);
                    }
                });
        addSubscription(subscribe);
    }

    @Override
    public void checkConfirmCode(String phone, String code) {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(LogInApi.class)
                .CheckConfirmCode(phone, code)
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
                        mView.onCheckConfirmCodeResult(httpResult);
                    }
                });
        addSubscription(subscribe);
    }

    @Override
    public void BindThirdLoginData(String phone, String nickname, String qqIcon, String gender, int channel, String openId, int loginTypeQq) {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(LogInApi.class)
                .bindThirdLoginData(phone, nickname, qqIcon, gender, channel, openId, loginTypeQq)
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
                        mView.onBindThirdLoginDataOver(httpResult);
                    }
                });
        addSubscription(subscribe);
    }


    @Override
    public void doThirdLogin(String openId) {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(LogInApi.class)
                .thirdLogin(openId)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgress();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HttpResult<UserInfo>>() {
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
                    public void onNext(HttpResult<UserInfo> httpResult) {
                        mView.hideProgress();
                        mView.onThirdLoginOver(httpResult);
                    }
                });
        addSubscription(subscribe);
    }

    @Override
    public void saveUserInfo(final UserInfo bean) {
        Subscription subscribe = Observable.create(new Observable.OnSubscribe<UserInfo>() {
            @Override
            public void call(Subscriber<? super UserInfo> subscriber) {
                SettingUtils.setLoginState(mView.getContext(), true);
                SettingUtils.setPhoneNum(mView.getContext(), bean.phone);
                SettingUtils.setUserToken(mView.getContext(), bean.token);
                SettingUtils.setAccount(mView.getContext(), bean.userName);
                subscriber.onNext(bean);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UserInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e(e.getMessage());
                    }

                    @Override
                    public void onNext(UserInfo bean) {
                        mView.loginSuccess(bean);
                    }
                });
        addSubscription(subscribe);
    }

    @Override
    public void refreshToken(String wechatAppid, String grant_type, String refresh_token) {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(LogInApi.class)
                .refreshToken(wechatAppid, grant_type, refresh_token)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgress();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<WechatBaseEntity>() {
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
                    public void onNext(WechatBaseEntity wechatUserEntity) {
                        mView.hideProgress();
                        mView.onWechatRefreshToken(wechatUserEntity);
                    }
                });
        addSubscription(subscribe);
    }

    @Override
    public void checkTokenAvailable(String access_token, String openid) {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(LogInApi.class)
                .checkTokenAvailable(access_token, openid)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgress();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<WechatCheckEntity>() {
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
                    public void onNext(WechatCheckEntity WechatCheckEntity) {
                        mView.hideProgress();
                        mView.onWechatcheckToken(WechatCheckEntity);
                    }
                });
        addSubscription(subscribe);
    }

    @Override
    public void getWechatUserData(String access_token, String openid) {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(LogInApi.class)
                .getWechatUserData(access_token, openid)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgress();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<WechatUserEntity>() {
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
                    public void onNext(WechatUserEntity WechatUserEntity) {
                        mView.hideProgress();
                        mView.onGetWechatUserData(WechatUserEntity);
                    }
                });
        addSubscription(subscribe);
    }

}
