package com.zhengdao.zqb.view.activity.register;


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
import com.zhengdao.zqb.event.NewHandUpDataEvent;
import com.zhengdao.zqb.event.RegistSuccessEvent;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.AppUtils;
import com.zhengdao.zqb.utils.RegUtils;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.view.activity.login.LoginActivity;
import com.zhengdao.zqb.view.activity.registeconfirm.RegisteConfirmActivity;

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


public class RegisterActivity extends MVPBaseActivity<RegisterContract.View, RegisterPresenter> implements RegisterContract.View, View.OnClickListener {
    @BindView(R.id.iv_title_bar_back)
    ImageView mIvTitleBarBack;
    @BindView(R.id.et_account)
    EditText  mEtAccount;
    @BindView(R.id.et_confirm_code)
    EditText  mEtConfirmCode;
    @BindView(R.id.tv_get_code)
    TextView  mTvGetCode;
    @BindView(R.id.et_invited)
    EditText  mEtInvited;
    @BindView(R.id.tv_regist)
    TextView  mTvRegist;
    private Disposable mDisposable;
    private String     mAccount;
    private String     mConfirmCode;
    private String     mRecommend;
    private long mCurrentTimeMillis = 0;
    private String mFromWhere;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        setSwipeBackEnable(false);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mFromWhere = getIntent().getStringExtra(Constant.Activity.Data);
        mIvTitleBarBack.setOnClickListener(this);
        mTvGetCode.setOnClickListener(this);
        mTvRegist.setOnClickListener(this);
        mDisposable = RxBus.getDefault().toObservable(RegistSuccessEvent.class).subscribe(new Consumer<RegistSuccessEvent>() {
            @Override
            public void accept(RegistSuccessEvent registSuccessEvent) throws Exception {
                RegisterActivity.this.finish();
                if (!TextUtils.isEmpty(mFromWhere))
                    RxBus.getDefault().post(new NewHandUpDataEvent());
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - mCurrentTimeMillis < 1000)
            return;
        mCurrentTimeMillis = System.currentTimeMillis();
        switch (v.getId()) {
            case R.id.iv_title_bar_back:
                RegisterActivity.this.finish();
                break;
            case R.id.tv_get_code:
                if (checkInput())
                    mPresenter.getConfirmCode(mAccount);
                break;
            case R.id.tv_regist:
                doRegist();
                break;
        }
    }

    private boolean checkInput() {
        mAccount = mEtAccount.getText().toString().trim();
        if (TextUtils.isEmpty(mAccount)) {
            ToastUtil.showToast(this, "请填写手机号");
            return false;
        }
        if (!RegUtils.isPhoneNum(mAccount)) {
            ToastUtil.showToast(this, getString(R.string.input_correct_phone));
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

    private void doRegist() {
        try {
            if (checkInput()) {
                mConfirmCode = mEtConfirmCode.getText().toString().trim();
                if (TextUtils.isEmpty(mConfirmCode)) {
                    ToastUtil.showToast(this, "请填写验证码");
                    return;
                }
                mRecommend = mEtInvited.getText().toString().trim();
                if (TextUtils.isEmpty(mRecommend) && Constant.Data.InvitedCodeList.size() > 0)
                    mRecommend = Constant.Data.InvitedCodeList.get(0);
                mRecommend = TextUtils.isEmpty(mRecommend) ? "" : mRecommend;
                int channel = getAppChannel();
                mPresenter.doRegist(mAccount, mConfirmCode, mRecommend, channel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getAppChannel() {
        int channel = 0;
        try {
            String umeng_channel = AppUtils.getAppMetaDataString(this, "UMENG_CHANNEL");
            String value = TextUtils.isEmpty(umeng_channel) ? "0" : umeng_channel;
            if (value.length() > 1 && value.startsWith("0")) {
                String substring = value.substring(1, value.length());
                channel = new Integer(substring);
            } else {
                channel = new Integer(value);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return channel;
    }

    @Override
    public void onGetConfirmCodeResult(ConfirmCodeEntity httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            ToastUtil.showToast(RegisterActivity.this, "发送成功");
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录");
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            ToastUtil.showToast(RegisterActivity.this, TextUtils.isEmpty(httpResult.msg) ? "" : httpResult.msg);
        }
    }

    @Override
    public void onRegistFinished(HttpResult httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            if (!TextUtils.isEmpty(httpResult.token)) {
                Intent intent = new Intent(this, RegisteConfirmActivity.class);
                intent.putExtra("token", httpResult.token);
                startActivity(intent);
            }
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录");
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            ToastUtil.showToast(RegisterActivity.this, "注册失败:" + httpResult.msg);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null)
            mPresenter.unsubcrible();
        if (mDisposable != null)
            mDisposable.dispose();
    }
}
