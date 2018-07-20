package com.zhengdao.zqb.view.activity.login;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tencent.connect.common.Constants;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.application.ClientAppLike;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.event.ForceBindPhoneEvent;
import com.zhengdao.zqb.event.LogInEvent;
import com.zhengdao.zqb.event.RegistSuccessEvent;
import com.zhengdao.zqb.event.WechatLoginSuccessEvent;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.LoginUtils;
import com.zhengdao.zqb.utils.RegUtils;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.view.activity.forgetpwd.ForgetpwdActivity;
import com.zhengdao.zqb.view.activity.register.RegisterActivity;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static com.zhengdao.zqb.application.ClientAppLike.mTencent;
import static com.zhengdao.zqb.application.ClientAppLike.mWxApi;


public class LoginActivity extends MVPBaseActivity<LoginContract.View, LoginPresenter> implements LoginContract.View, View.OnClickListener {


    @BindView(R.id.iv_close)
    ImageView mIvClose;
    @BindView(R.id.iv_icon)
    ImageView mIvIcon;
    @BindView(R.id.et_account)
    EditText  mEtAccount;
    @BindView(R.id.et_password)
    EditText  mEtPassword;
    @BindView(R.id.tv_login)
    TextView  mTvLogin;
    @BindView(R.id.tv_regist)
    TextView  mTvRegist;
    @BindView(R.id.tv_forget_password)
    TextView  mTvForgetPassword;
    @BindView(R.id.iv_qq)
    ImageView mIvQq;
    @BindView(R.id.iv_wechat)
    ImageView mIvWechat;
    @BindView(R.id.iv_alipay)
    ImageView mIvAlipay;
    private Disposable mDisposable;
    private Disposable mDisposable1;
    private String     mAccount;
    private String     mPassword;
    private long mCurrentTimeMillis = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initData();
        initView();
        initClicklistener();
    }

    private void initData() {
        setSwipeBackEnable(false);
    }

    private void initView() {
        mProgressDialog.setMessage("正在登录");
    }

    private void initClicklistener() {
        mIvClose.setOnClickListener(this);
        mTvLogin.setOnClickListener(this);
        mTvRegist.setOnClickListener(this);
        mTvForgetPassword.setOnClickListener(this);
        mIvQq.setOnClickListener(this);
        mIvWechat.setOnClickListener(this);
        mIvAlipay.setOnClickListener(this);
        mDisposable = RxBus.getDefault().toObservable(RegistSuccessEvent.class).subscribe(new Consumer<RegistSuccessEvent>() {
            @Override
            public void accept(RegistSuccessEvent registSuccessEvent) throws Exception {
                doFinish(true);
            }
        });
        mDisposable1 = RxBus.getDefault().toObservable(WechatLoginSuccessEvent.class).subscribe(new Consumer<WechatLoginSuccessEvent>() {
            @Override
            public void accept(WechatLoginSuccessEvent wechatLoginSuccessEvent) throws Exception {
                doFinish(true);
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (System.currentTimeMillis() - mCurrentTimeMillis < 1000)
            return;
        mCurrentTimeMillis = System.currentTimeMillis();
        switch (view.getId()) {
            case R.id.iv_close:
                doFinish(false);
                break;
            case R.id.tv_login:
                doLogin();
                break;
            case R.id.tv_regist:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.tv_forget_password:
                startActivity(new Intent(LoginActivity.this, ForgetpwdActivity.class));
                break;
            case R.id.iv_qq:
                doQQlogin();
                break;
            case R.id.iv_wechat:
                doWechatLogin();
                break;
            case R.id.iv_alipay:
                ToastUtil.showToast(LoginActivity.this, "iv_alipay");
                break;
        }
    }

    private void doFinish(boolean type) {
        Intent intent = new Intent();
        intent.putExtra(Constant.Activity.Data, type);
        setResult(RESULT_OK, intent);
        LoginActivity.this.finish();
    }

    //-------------------------------------------------------------------QQ登录-----------------------------------------------------------------
    private void doQQlogin() {
        showProgress();
        LoginUtils.loginQQ(this, mIUiListener);
    }

    IUiListener mIUiListener = new IUiListener() {
        @Override
        public void onComplete(Object o) {
            hideProgress();
            if (null == o) {
                ToastUtil.showToast(LoginActivity.this, "登录失败");
                return;
            }
            JSONObject jsonResponse = (JSONObject) o;
            if (null != jsonResponse && jsonResponse.length() == 0) {
                ToastUtil.showToast(LoginActivity.this, "登录失败");
                return;
            }
            LogUtils.i("QQ登录成功返回结果-" + o.toString());
            LoginUtils.initOpenidAndToken(jsonResponse);
            if (mTencent != null && mTencent.isSessionValid())
                mPresenter.doThirdLogin(mTencent.getOpenId());
        }

        @Override
        public void onError(UiError uiError) {
            hideProgress();
            LogUtils.i("QQ登录出错");
        }

        @Override
        public void onCancel() {
            hideProgress();
            LogUtils.i("QQ登录取消");
        }
    };

    @Override
    public void onThirdLoginOver(HttpResult<com.zhengdao.zqb.entity.UserInfo> httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            mPresenter.saveUserInfo(httpResult.data);
        } else if (httpResult.code == Constant.HttpResult.DATANULL) {
            RxBus.getDefault().post(new ForceBindPhoneEvent(0));
            doFinish(true);
        } else
            ToastUtil.showToast(LoginActivity.this, "登录失败");
    }

    //-------------------------------------------------------------------微信登录-----------------------------------------------------------------
    private void doWechatLogin() {
        if (!mWxApi.isWXAppInstalled()) {
            ToastUtil.showToast(this, "您还未安装微信客户端");
            return;
        }
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = Constant.WechatReq.Loginstate;
        ClientAppLike.mWxApi.sendReq(req);
    }

    //-------------------------------------------------------------------账号登录-----------------------------------------------------------------
    private void doLogin() {
        mAccount = mEtAccount.getText().toString().trim();
        mPassword = mEtPassword.getText().toString().trim();
        if (TextUtils.isEmpty(mAccount)) {
            ToastUtil.showToast(this, "请填写手机号");
            return;
        }
        if (!RegUtils.isPhoneNum(mAccount)) {
            ToastUtil.showToast(this, getString(R.string.input_correct_phone));
            return;
        }
        if (TextUtils.isEmpty(mPassword)) {
            ToastUtil.showToast(this, "请填写密码");
            return;
        }
        if (mPassword.length() < 6) {
            ToastUtil.showToast(this, getString(R.string.pwd_length_not_enough));
            return;
        }
        hideSoftInput();
        mProgressDialog.setMessage("正在登录");
        mPresenter.login(mAccount, mPassword);
    }

    @Override
    public void onLoginOver(HttpResult<com.zhengdao.zqb.entity.UserInfo> result) {
        if (result.code == Constant.HttpResult.SUCCEED)
            mPresenter.saveUserInfo(result.data);
        else
            ToastUtil.showToast(this, TextUtils.isEmpty(result.msg) ? "登陆失败" : result.msg);
    }

    @Override
    public void loginSuccess(com.zhengdao.zqb.entity.UserInfo bean) {
        LogUtils.i("登录成功:" + "\n" + bean.toString());
        ToastUtil.showToast(this, "登录成功！");
        String phoneNum = SettingUtils.getPhoneNum(this);
        if (TextUtils.isEmpty(phoneNum))
            RxBus.getDefault().post(new ForceBindPhoneEvent(0));
        else
            RxBus.getDefault().post(new LogInEvent());
        doFinish(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Tencent.onActivityResultData(requestCode, resultCode, data, mIUiListener);
        if (requestCode == Constants.REQUEST_API) {
            if (resultCode == Constants.REQUEST_QQ_SHARE || resultCode == Constants.REQUEST_QZONE_SHARE || resultCode == Constants.REQUEST_OLD_SHARE) {
                Tencent.handleResultData(data, mIUiListener);
            }
        }
    }

    @Override
    public void onBackPressed() {
        doFinish(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null)
            mDisposable.dispose();
        if (mDisposable1 != null)
            mDisposable1.dispose();
    }
}
