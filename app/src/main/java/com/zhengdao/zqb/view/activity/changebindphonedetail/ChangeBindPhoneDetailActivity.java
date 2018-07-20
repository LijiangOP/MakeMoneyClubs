package com.zhengdao.zqb.view.activity.changebindphonedetail;


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
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.view.activity.login.LoginActivity;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;


public class ChangeBindPhoneDetailActivity extends MVPBaseActivity<ChangeBindPhoneDetailContract.View, ChangeBindPhoneDetailPresenter> implements ChangeBindPhoneDetailContract.View, View.OnClickListener {
    @BindView(R.id.et_phone)
    EditText  mEtPhone;
    @BindView(R.id.iv_clear)
    ImageView mIvClear;
    @BindView(R.id.et_confirm_code)
    EditText  mEtConfirmCode;
    @BindView(R.id.tv_get_code)
    TextView  mTvGetCode;
    @BindView(R.id.tv_bind_phone)
    TextView  mTvBindPhone;
    private long mCurrentTimeMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_bindphone_detail);
        ButterKnife.bind(this);
        init();
        initListener();
        setTitle("身份认证");
    }

    private void init() {

    }

    private void initListener() {
        mIvClear.setOnClickListener(this);
        mTvGetCode.setOnClickListener(this);
        mTvBindPhone.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - mCurrentTimeMillis < 1000)
            return;
        mCurrentTimeMillis = System.currentTimeMillis();
        switch (v.getId()) {
            case R.id.iv_clear:
                mEtPhone.setText("");
                break;
            case R.id.tv_get_code:
                getCode();
                break;
            case R.id.tv_bind_phone:
                bindPhoneNum();
                break;
        }
    }

    private void getCode() {
        String phone = mEtPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.showToast(ChangeBindPhoneDetailActivity.this, "请输入手机号码");
            return;
        }
        mPresenter.getConfirmCode(phone);
    }

    private void bindPhoneNum() {
        String phone = mEtPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.showToast(ChangeBindPhoneDetailActivity.this, "请输入手机号码");
            return;
        }
        String confirmCode = mEtConfirmCode.getText().toString().trim();
        if (TextUtils.isEmpty(confirmCode)) {
            ToastUtil.showToast(ChangeBindPhoneDetailActivity.this, "请输入手机验证码");
            return;
        }
        mPresenter.BindNewPhone(phone, confirmCode);
    }

    @Override
    public void onGetConfirmCodeResult(ConfirmCodeEntity httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            ToastUtil.showToast(ChangeBindPhoneDetailActivity.this, "发送成功");
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录");
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            ToastUtil.showToast(ChangeBindPhoneDetailActivity.this, TextUtils.isEmpty(httpResult.msg) ? "" : httpResult.msg);
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
    public void onChangePhoneNumResult(HttpResult httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            ToastUtil.showToast(ChangeBindPhoneDetailActivity.this, "修改成功");
            RxBus.getDefault().post(new FinishIdentityVertifyEvent());
            ChangeBindPhoneDetailActivity.this.finish();
        } else {
            ToastUtil.showToast(ChangeBindPhoneDetailActivity.this, TextUtils.isEmpty(httpResult.msg) ? "" : httpResult.msg);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
