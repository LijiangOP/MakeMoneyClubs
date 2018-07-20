package com.zhengdao.zqb.view.activity.forgetpwd;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.ConfirmCodeEntity;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.event.FinishIdentityVertifyEvent;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.RegUtils;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.ToastUtil;
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

public class ForgetpwdActivity extends MVPBaseActivity<ForgetpwdContract.View, ForgetpwdPresenter> implements ForgetpwdContract.View, View.OnClickListener {
    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.et_account)
    EditText  mEtAccount;
    @BindView(R.id.et_confirm_code)
    EditText  mEtConfirmCode;
    @BindView(R.id.tv_get_code)
    TextView  mTvGetCode;
    @BindView(R.id.tv_next)
    TextView  mTvNext;
    private String mConfirmCode;
    private String mAccount;
    private long mCurrentTimeMillis = 0;
    private Disposable mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpwd);
        setSwipeBackEnable(false);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mIvBack.setOnClickListener(this);
        mTvNext.setOnClickListener(this);
        mTvGetCode.setOnClickListener(this);
        mDisposable = RxBus.getDefault().toObservable(FinishIdentityVertifyEvent.class).subscribe(new Consumer<FinishIdentityVertifyEvent>() {
            @Override
            public void accept(FinishIdentityVertifyEvent finishIdentityVertifyEvent
            ) throws Exception {
                ForgetpwdActivity.this.finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - mCurrentTimeMillis < 1000)
            return;
        mCurrentTimeMillis = System.currentTimeMillis();
        switch (v.getId()) {
            case R.id.iv_back:
                ForgetpwdActivity.this.finish();
                break;
            case R.id.tv_get_code:
                if (checkAccount())
                    mPresenter.getConfirmCode(mAccount);
                break;
            case R.id.tv_next:
                if (checkInput())
                    mPresenter.checkConfirmCode(mAccount, mConfirmCode);
                break;
        }
    }

    private boolean checkAccount() {
        mAccount = mEtAccount.getText().toString().trim();
        if (TextUtils.isEmpty(mAccount)) {
            ToastUtil.showToast(ForgetpwdActivity.this, "号码不能为空");
            return false;
        }
        if (!RegUtils.isPhoneNum(mAccount)) {
            ToastUtil.showToast(this, getString(R.string.input_correct_phone));
            return false;
        }
        return true;
    }

    private boolean checkInput() {
        mAccount = mEtAccount.getText().toString().trim();
        if (TextUtils.isEmpty(mAccount)) {
            ToastUtil.showToast(ForgetpwdActivity.this, "号码不能为空");
            return false;
        }
        mConfirmCode = mEtConfirmCode.getText().toString().trim();
        if (TextUtils.isEmpty(mConfirmCode)) {
            ToastUtil.showToast(ForgetpwdActivity.this, "验证码不能为空");
            return false;
        }
        return true;
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
    public void getConfirmCodeResult(ConfirmCodeEntity httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            ToastUtil.showToast(ForgetpwdActivity.this, "发送验证码成功");
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录");
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            ToastUtil.showToast(ForgetpwdActivity.this, TextUtils.isEmpty(httpResult.msg) ? "发送验证码失败" : httpResult.msg);
        }
    }

    @Override
    public void onCheckConfirmCodeResult(HttpResult httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            Intent intent = new Intent(ForgetpwdActivity.this, ChangePswActivity.class);
            intent.putExtra(Constant.Activity.Type, "resetPsw");
            intent.putExtra(Constant.Activity.Data, mAccount);
            intent.putExtra(Constant.Activity.Data1, httpResult.token);
            startActivity(intent);
            mEtConfirmCode.setText("");
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录");
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            ToastUtil.showToast(ForgetpwdActivity.this, TextUtils.isEmpty(httpResult.msg) ? "验证验证码失败" : httpResult.msg);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null)
            mDisposable.dispose();
    }
}
