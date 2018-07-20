package com.zhengdao.zqb.view.activity.identityvertify;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.ConfirmCodeEntity;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.event.FinishIdentityVertifyEvent;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.RegUtils;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.view.activity.changebindphonedetail.ChangeBindPhoneDetailActivity;
import com.zhengdao.zqb.view.activity.changepsw.ChangePswActivity;
import com.zhengdao.zqb.view.activity.login.LoginActivity;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;


public class IdentityVertifyActivity extends MVPBaseActivity<IdentityVertifyContract.View, IdentityVertifyPresenter> implements IdentityVertifyContract.View, View.OnClickListener {

    @BindView(R.id.tv_title_bar_title)
    TextView     mTvTitleBarTitle;
    @BindView(R.id.tv_hint)
    TextView     mTvHint;
    //获取验证码part
    @BindView(R.id.ll_get_code)
    LinearLayout mLlGetCode;
    @BindView(R.id.et_confirm_code)
    EditText     mEtConfirmCode;
    @BindView(R.id.tv_get_code)
    TextView     mTvGetCode;
    //验证支付密码part
    @BindView(R.id.ll_confirm_pay)
    LinearLayout mLlConfirmPay;
    @BindView(R.id.et_input)
    EditText     mEtInput;
    @BindView(R.id.iv_clear)
    ImageView    mIvClear;
    //按钮
    @BindView(R.id.tv_next)
    TextView     mTvNext;

    private String mSkipType;
    private String mPhone;
    private String mSkip;
    private int    mType;
    private long mCurrentTimeMillis = 0;
    private Disposable mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identityvertify);
        ButterKnife.bind(this);
        initData();
        init();
        initListener();
    }

    private void initData() {
        mType = getIntent().getIntExtra(Constant.Activity.Type, 0);
        mSkip = getIntent().getStringExtra(Constant.Activity.Skip);
        mSkipType = getIntent().getStringExtra(Constant.Activity.SkipData);
    }

    private void init() {
        if (mType == 0) {
            setTitle("身份认证");
            mLlGetCode.setVisibility(View.VISIBLE);
            mLlConfirmPay.setVisibility(View.GONE);
            mPhone = SettingUtils.getPhoneNum(IdentityVertifyActivity.this);
            if (!TextUtils.isEmpty(mPhone)) {
                SpannableString spannableString = new SpannableString("短信验证码将发送至" + mPhone + "的手机上");
                ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#fc3135"));
                spannableString.setSpan(colorSpan, 9, 9 + mPhone.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                mTvHint.setText(spannableString);
            }
            mEtInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
        } else  {
            setTitle("身份认证");
            mLlGetCode.setVisibility(View.GONE);
            mLlConfirmPay.setVisibility(View.VISIBLE);
            mTvHint.setText("输入账户支付密码，完成身份验证");
        }
        mDisposable = RxBus.getDefault().toObservable(FinishIdentityVertifyEvent.class).subscribe(new Consumer<FinishIdentityVertifyEvent>() {
            @Override
            public void accept(FinishIdentityVertifyEvent finishIdentityVertifyEvent) throws Exception {
                IdentityVertifyActivity.this.finish();
            }
        });
    }

    private void initListener() {
        mTvGetCode.setOnClickListener(this);
        mIvClear.setOnClickListener(this);
        mTvNext.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - mCurrentTimeMillis < 1000)
            return;
        mCurrentTimeMillis = System.currentTimeMillis();
        switch (v.getId()) {
            case R.id.tv_get_code:
                doGetCode();
                break;
            case R.id.iv_clear:
                mEtInput.setText("");
                break;
            case R.id.tv_next:
                doNext();
                break;
        }
    }

    private void doGetCode() {
        if (TextUtils.isEmpty(mPhone)) {
            ToastUtil.showToast(IdentityVertifyActivity.this, "获取手机号码出错");
            return;
        }
        if (!RegUtils.isPhoneNum(mPhone)) {
            ToastUtil.showToast(this, getString(R.string.input_correct_phone));
            return;
        }
        mPresenter.getConfirmCode(mPhone);
    }

    private void doNext() {
        if (!TextUtils.isEmpty(mSkip) && mSkip.equals("change_bind_phone")) {
            if (mType == 0) {
                String code = mEtConfirmCode.getText().toString().trim();
                if (!TextUtils.isEmpty(code)) {
                    mPresenter.checkConfirmCode(mPhone, code);
                } else
                    ToastUtil.showToast(IdentityVertifyActivity.this, "请输入验证码");
            } else {
                String payPsw = mEtInput.getText().toString().trim();
                if (!TextUtils.isEmpty(payPsw)) {
                    mPresenter.checkPayPsw(payPsw);
                } else
                    ToastUtil.showToast(IdentityVertifyActivity.this, "请输入支付密码");
            }
        } else {
            String code = mEtConfirmCode.getText().toString().trim();
            if (TextUtils.isEmpty(code)) {
                ToastUtil.showToast(this, "请填写验证码");
                return;
            } else
                mPresenter.checkConfirmCode(mPhone, code);
        }
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
            ToastUtil.showToast(IdentityVertifyActivity.this, "发送成功");
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录");
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            ToastUtil.showToast(IdentityVertifyActivity.this, TextUtils.isEmpty(httpResult.msg) ? "" : httpResult.msg);
        }
    }

    @Override
    public void onCheckConfirmCodeResult(HttpResult httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            if (!TextUtils.isEmpty(mSkip) && mSkip.equals("change_bind_phone")) {
                if (mType == 0) {
                    Intent intent = new Intent(IdentityVertifyActivity.this, ChangeBindPhoneDetailActivity.class);
                    startActivity(intent);
                }
            } else {
                Intent intent = new Intent(IdentityVertifyActivity.this, ChangePswActivity.class);
                intent.putExtra(Constant.Activity.Type, mSkipType);
                startActivity(intent);
            }
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录");
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            ToastUtil.showToast(IdentityVertifyActivity.this, TextUtils.isEmpty(httpResult.msg) ? "" : httpResult.msg);
        }
    }

    @Override
    public void onCheckPayPswResult(HttpResult httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            Intent intent = new Intent(IdentityVertifyActivity.this, ChangeBindPhoneDetailActivity.class);
            startActivity(intent);
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录");
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            ToastUtil.showToast(IdentityVertifyActivity.this, TextUtils.isEmpty(httpResult.msg) ? "" : httpResult.msg);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null)
            mDisposable.dispose();
    }
}
