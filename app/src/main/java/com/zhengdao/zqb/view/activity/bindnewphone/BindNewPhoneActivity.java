package com.zhengdao.zqb.view.activity.bindnewphone;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tencent.connect.UserInfo;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.ConfirmCodeEntity;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.WechatBaseEntity;
import com.zhengdao.zqb.entity.WechatCheckEntity;
import com.zhengdao.zqb.entity.WechatUserEntity;
import com.zhengdao.zqb.event.LogInEvent;
import com.zhengdao.zqb.event.LogOutEvent;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.AppUtils;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.RegUtils;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.utils.WechatUtils;
import com.zhengdao.zqb.view.activity.login.LoginActivity;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;

import static com.zhengdao.zqb.application.ClientAppLike.WECHAT_APPID;
import static com.zhengdao.zqb.application.ClientAppLike.mTencent;
import static com.zhengdao.zqb.config.Constant.Third.LOGIN_TYPE_QQ;
import static com.zhengdao.zqb.config.Constant.Third.LOGIN_TYPE_WECHAT;

public class BindNewPhoneActivity extends MVPBaseActivity<BindNewPhoneContract.View, BindNewPhonePresenter> implements BindNewPhoneContract.View, View.OnClickListener {

    @BindView(R.id.et_phone)
    EditText  mEtPhone;
    @BindView(R.id.iv_clear)
    ImageView mIvClear;
    @BindView(R.id.et_confirm_code)
    EditText  mEtConfirmCode;
    @BindView(R.id.tv_get_code)
    TextView  mTvGetCode;
    @BindView(R.id.tv_next)
    TextView  mTvNext;
    @BindView(R.id.iv_title_bar_back)
    ImageView mIvTitleBarBack;
    @BindView(R.id.tv_title_bar_title)
    TextView  mTvTitleBarTitle;
    private long mCurrentTimeMillis = 0;
    private String mPhone;
    private int    mLoginType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_newphone);
        ButterKnife.bind(this);
        init();
        initListener();
        mTvTitleBarTitle.setText("绑定手机号码");
    }

    private void init() {
        mLoginType = getIntent().getIntExtra(Constant.Activity.Data, 0);
    }

    private void initListener() {
        mIvTitleBarBack.setOnClickListener(this);
        mIvClear.setOnClickListener(this);
        mTvGetCode.setOnClickListener(this);
        mTvNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - mCurrentTimeMillis < 1000)
            return;
        mCurrentTimeMillis = System.currentTimeMillis();
        switch (v.getId()) {
            case R.id.iv_title_bar_back:
                doBack();
                break;
            case R.id.tv_get_code:
                getCode();
                break;
            case R.id.iv_clear:
                mEtPhone.setText("");
                break;
            case R.id.tv_next:
                doCommit();
                break;
        }
    }

    private void doBack() {
        SettingUtils.setLoginState(this, false);
        SettingUtils.setUserToken(this, "");
        SettingUtils.setUserID(this, "");
        SettingUtils.setPhoneNum(this, "");
        SettingUtils.setAccount(this, "");
        SettingUtils.setAlipayAccount(this, "");
        RxBus.getDefault().post(new LogOutEvent());
        this.finish();
    }

    private void getCode() {
        mPhone = mEtPhone.getText().toString().trim();
        if (TextUtils.isEmpty(mPhone)) {
            ToastUtil.showToast(BindNewPhoneActivity.this, "请输入手机号码");
            return;
        }
        if (!RegUtils.isPhoneNum(mPhone)) {
            ToastUtil.showToast(this, getString(R.string.input_correct_phone));
            return;
        }
        mPresenter.getConfirmCode(mPhone);
    }

    private void doCommit() {
        String code = mEtConfirmCode.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            ToastUtil.showToast(BindNewPhoneActivity.this, "请输入验证码");
            return;
        }
        if (TextUtils.isEmpty(mPhone)) {
            ToastUtil.showToast(BindNewPhoneActivity.this, "请输入手机号码");
            return;
        }
        if (!RegUtils.isPhoneNum(mPhone)) {
            ToastUtil.showToast(this, getString(R.string.input_correct_phone));
            return;
        }
        mPresenter.checkConfirmCode(mPhone, code);
    }

    @Override
    public void getConfirmCodeSuccess() {
        Observable.interval(0, 1, TimeUnit.SECONDS).take(60).map(new Func1<Long, Long>() {
            @Override
            public Long call(Long aLong) {
                return 60 - aLong;
            }
        }).doOnSubscribe(new Action0() {
            @Override
            public void call() {
                mTvGetCode.setEnabled(false);//在发送数据的时候设置为不能点击
                mTvGetCode.setBackgroundColor(Color.GRAY);//背景色设为灰色
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Long>() {
            @Override
            public void onCompleted() {
                mTvGetCode.setText("获取验证码");//数据发送完后设置为原来的文字
                mTvGetCode.setEnabled(true);
                mTvGetCode.setTextColor(getResources().getColor(R.color.selector_get_code));
                mTvGetCode.setBackgroundColor(Color.WHITE);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(Long aLong) {
                mTvGetCode.setText(aLong + "秒");
                mTvGetCode.setTextColor(getResources().getColor(R.color.color_e6e6e6));
            }
        });
    }

    @Override
    public void onGetConfirmCodeResult(ConfirmCodeEntity httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            ToastUtil.showToast(BindNewPhoneActivity.this, "发送成功");
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(BindNewPhoneActivity.this, "登录超时,请重新登录");
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            ToastUtil.showToast(BindNewPhoneActivity.this, TextUtils.isEmpty(httpResult.msg) ? "" : httpResult.msg);
        }
    }

    @Override
    public void onCheckConfirmCodeResult(HttpResult httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            if (mLoginType == 1) {
                getThirdLoginData(1);
            } else {
                getThirdLoginData(0);
            }
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录");
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            ToastUtil.showToast(BindNewPhoneActivity.this, TextUtils.isEmpty(httpResult.msg) ? "验证验证码失败" : httpResult.msg);
        }
    }

    private void getThirdLoginData(int type) {
        if (type == 1) {
            String wechatAccessToken = TextUtils.isEmpty(WechatUtils.getWechatAccessToken(BindNewPhoneActivity.this)) ? "" : WechatUtils.getWechatAccessToken(BindNewPhoneActivity.this);
            String wechatOpenId = TextUtils.isEmpty(WechatUtils.getWechatOpenId(BindNewPhoneActivity.this)) ? "" : WechatUtils.getWechatOpenId(BindNewPhoneActivity.this);
            mPresenter.checkTokenAvailable(wechatAccessToken, wechatOpenId);
        } else {
            if (mTencent != null && mTencent.isSessionValid()) {
                IUiListener listener = new IUiListener() {
                    @Override
                    public void onError(UiError e) {
                        Message msg = new Message();
                        msg.obj = "把手机时间改成获取网络时间";
                        msg.what = 1;
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void onComplete(final Object response) {
                        Message msg = new Message();
                        msg.obj = response;
                        msg.what = 0;
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void onCancel() {
                        Message msg = new Message();
                        msg.obj = "获取用户信息失败";
                        msg.what = 2;
                        mHandler.sendMessage(msg);
                    }
                };
                UserInfo mInfo = new UserInfo(this, mTencent.getQQToken());
                mInfo.getUserInfo(listener);
            } else {
                LogUtils.e("QQ登录初始化失败");
            }
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            /** 获取用户信息成功 */
            if (msg.what == 0) {
                JSONObject response = (JSONObject) msg.obj;
                if (response.has("nickname")) {
                    try {
                        LogUtils.i("获取用户信息成功，返回结果：" + response.toString());
                        String umeng_channel = AppUtils.getAppMetaDataString(BindNewPhoneActivity.this, "UMENG_CHANNEL");
                        String channel = TextUtils.isEmpty(umeng_channel) ? "0" : umeng_channel;
                        mPresenter.BindThirdLoginData(mPhone, response.get("nickname").toString(), response.get("figureurl_qq_1").toString(),
                                response.get("gender").toString(), new Integer(channel), mTencent.getOpenId(),LOGIN_TYPE_QQ);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (msg.what == 1) {
                LogUtils.i(msg.toString());
            } else if (msg.what == 2) {
                LogUtils.i(msg.toString());
            }
        }

    };

    @Override
    public void onWechatcheckToken(WechatCheckEntity wechatCheckEntity) {
        if (wechatCheckEntity.errcode == 0) {//token可用
            String wechatAccessToken = TextUtils.isEmpty(WechatUtils.getWechatAccessToken(BindNewPhoneActivity.this)) ? "" : WechatUtils.getWechatAccessToken(BindNewPhoneActivity.this);
            String wechatOpenId = TextUtils.isEmpty(WechatUtils.getWechatOpenId(BindNewPhoneActivity.this)) ? "" : WechatUtils.getWechatOpenId(BindNewPhoneActivity.this);
            mPresenter.getWechatUserData(wechatAccessToken, wechatOpenId);
        } else {//token不可用
            String wechatRefreshToken = TextUtils.isEmpty(WechatUtils.getWechatRefreshToken(BindNewPhoneActivity.this)) ? "" : WechatUtils.getWechatRefreshToken(BindNewPhoneActivity.this);
            mPresenter.refreshToken(WECHAT_APPID, "refresh_token", wechatRefreshToken);
        }
    }

    @Override
    public void onWechatRefreshToken(WechatBaseEntity wechatBaseEntity) {
        if (wechatBaseEntity == null) {
            LogUtils.e("刷新用户token失败");
        } else {
            String access_token = wechatBaseEntity.access_token;
            String openid = wechatBaseEntity.openid;
            mPresenter.getWechatUserData(access_token, openid);
        }
    }

    @Override
    public void onGetWechatUserData(WechatUserEntity wechatUserEntity) {
        if (wechatUserEntity == null) {
            ToastUtil.showToast(BindNewPhoneActivity.this, "获取用户数据失败");
            BindNewPhoneActivity.this.finish();
        } else {
            LogUtils.i(wechatUserEntity.toString());
            int sexType = wechatUserEntity.sex;
            String sex = sexType == 1 ? "男" : "女";
            String umeng_channel = AppUtils.getAppMetaDataString(BindNewPhoneActivity.this, "UMENG_CHANNEL");
            String channel = TextUtils.isEmpty(umeng_channel) ? "0" : umeng_channel;
            String wechatOpenId = TextUtils.isEmpty(WechatUtils.getWechatOpenId(BindNewPhoneActivity.this)) ? "" : WechatUtils.getWechatOpenId(BindNewPhoneActivity.this);
            mPresenter.BindThirdLoginData(mPhone, wechatUserEntity.nickname, wechatUserEntity.headimgurl, sex, new Integer(channel), wechatOpenId, LOGIN_TYPE_WECHAT);
        }
    }

    @Override
    public void onBindThirdLoginDataOver(HttpResult httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            ToastUtil.showToast(BindNewPhoneActivity.this, "绑定成功!");
            if (mLoginType == 1) {
                mPresenter.doThirdLogin(WechatUtils.getWechatOpenId(BindNewPhoneActivity.this));
            } else {
                if (mTencent != null && mTencent.isSessionValid())
                    mPresenter.doThirdLogin(mTencent.getOpenId());
            }
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(BindNewPhoneActivity.this, "登录超时,请重新登录");
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            ToastUtil.showToast(BindNewPhoneActivity.this, TextUtils.isEmpty(httpResult.msg) ? "绑定失败!" : httpResult.msg);
        }
    }

    @Override
    public void onThirdLoginOver(HttpResult<com.zhengdao.zqb.entity.UserInfo> httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            mPresenter.saveUserInfo(httpResult.data);
        } else {
            LogUtils.e("三方登录失败");
        }
    }

    @Override
    public void loginSuccess(com.zhengdao.zqb.entity.UserInfo bean) {
        LogUtils.i("登录成功:" + "\n" + bean.toString());
        ToastUtil.showToast(this, "登录成功！");
        RxBus.getDefault().post(new LogInEvent());
        finish();
    }

    @Override
    public void onBackPressed() {
        doBack();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
