package com.wlg.wlgmall.wxapi;

import android.content.Context;
import android.text.TextUtils;

import com.zhengdao.zqb.api.LogInApi;
import com.zhengdao.zqb.api.UserApi;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.UserInfo;
import com.zhengdao.zqb.entity.WechatBaseEntity;
import com.zhengdao.zqb.entity.WechatCheckEntity;
import com.zhengdao.zqb.entity.WechatUserEntity;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.SettingUtils;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * @Author lijiangop
 * @CreateTime 2018/7/2 15:08
 */
public class WXEntryPresenter implements WXEntryContract.Presenter {
    private CompositeSubscription mCompositeSubscription;
    private Context               mContext;
    private WXEntryContract.View  mView;

    public WXEntryPresenter(Context context, WXEntryContract.View view) {
        this.mContext = context;
        this.mView = view;
    }

    @Override
    public void getWechatBaseData(String wechatAppid, String secret, String code, String authorization_code, final String state) {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(LogInApi.class)
                .getWechatBaseData(wechatAppid, secret, code, authorization_code)
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
                        mView.onGetWechatBaseData(wechatUserEntity, state);
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
                SettingUtils.setLoginState(mContext, true);
                SettingUtils.setPhoneNum(mContext, bean.phone);
                SettingUtils.setUserToken(mContext, bean.token);
                SettingUtils.setUserID(mContext, "" + bean.id);
                SettingUtils.setAccount(mContext, bean.userName);
                SettingUtils.setAccountType(mContext, bean.type);
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
    public void doAccountBind(String openId, final int type) {
        String token = SettingUtils.getUserToken(mContext);
        if (TextUtils.isEmpty(token))
            return;
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(UserApi.class)
                .bindAccount(token, openId, type)
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
                        mView.onAccountBindResult(httpResult, type);
                    }
                });
        addSubscription(subscribe);
    }

    public void addSubscription(Subscription s) {
        if (this.mCompositeSubscription == null) {
            this.mCompositeSubscription = new CompositeSubscription();
        }
        this.mCompositeSubscription.add(s);
    }

    public void unsubcrible() {
        if (this.mCompositeSubscription != null) {
            this.mCompositeSubscription.unsubscribe();
        }
    }
}
