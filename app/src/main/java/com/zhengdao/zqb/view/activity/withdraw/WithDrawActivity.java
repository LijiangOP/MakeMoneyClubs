package com.zhengdao.zqb.view.activity.withdraw;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.UserHomeBean;
import com.zhengdao.zqb.event.UpDataUserInfoEvent;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.view.activity.login.LoginActivity;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;


public class WithDrawActivity extends MVPBaseActivity<WithDrawContract.View, WithDrawPresenter> implements WithDrawContract.View, View.OnClickListener {

    private static final int BIND_ALIPAY  = 002;
    private static final int ACTION_LOGIN = 001;
    @BindView(R.id.tv_aliPay_account)
    TextView  mTvAliPayAccount;
    @BindView(R.id.tv_change)
    TextView  mTvChange;
    @BindView(R.id.et_number)
    EditText  mEtNumber;
    @BindView(R.id.iv_clear)
    ImageView mIvClear;
    @BindView(R.id.tv_balance)
    TextView  mTvBalance;
    @BindView(R.id.tv_total)
    TextView  mTvTotal;
    @BindView(R.id.tv_confirm)
    TextView  mTvConfirm;
    private long mCurrentTimeMillis = 0;
    private String mAccount;
    private Double mUsableSum;
    private String mWithDrawNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        ButterKnife.bind(this);
        setTitle("提现");
        initData();
        initListener();
    }

    private void initData() {
        mAccount = getIntent().getStringExtra(Constant.Activity.Data);
        if (TextUtils.isEmpty(mAccount))
            mAccount = SettingUtils.getAlipayAccount(this);
        if (TextUtils.isEmpty(mAccount)) {
            mPresenter.getUserData();
        }
        mTvAliPayAccount.setText(TextUtils.isEmpty(mAccount) ? "" : mAccount);
        mUsableSum = getIntent().getDoubleExtra(Constant.Activity.Data1, 0);
        mTvBalance.setText("¥" + new DecimalFormat("#0.00").format(mUsableSum));
        mEtNumber.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
    }

    private void initListener() {
        mTvChange.setOnClickListener(this);
        mIvClear.setOnClickListener(this);
        mTvTotal.setOnClickListener(this);
        mTvConfirm.setOnClickListener(this);

        mEtNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    String number = editable.toString().trim();
                    if (!TextUtils.isEmpty(number)) {
                        if (number.contains(".")) {
                            if (number.length() - 1 - number.indexOf(".") > 2) {
                                CharSequence charSequence = number.subSequence(0, number.indexOf(".") + 3);
                                mEtNumber.setText(charSequence);
                                mEtNumber.setSelection(charSequence.length());
                            }
                        }
                        if (number.substring(0).equals(".")) {
                            number = "0" + number;
                            mEtNumber.setText(number);
                            mEtNumber.setSelection(2);
                        }
                        if (number.startsWith("0") && number.length() > 1) {
                            if (!number.substring(1, 2).equals(".")) {
                                mEtNumber.setText(number.subSequence(0, 1));
                                mEtNumber.setSelection(1);
                            }
                        }
                        Double aDouble = Double.valueOf(number);
                        if (aDouble > mUsableSum) {
                            mEtNumber.setText("" + mUsableSum);
                            mEtNumber.setSelection(mEtNumber.getText().toString().trim().length());
                        }
                    }
                } catch (Exception e) {
                    LogUtils.e(e.getMessage());
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - mCurrentTimeMillis < 1000)
            return;
        mCurrentTimeMillis = System.currentTimeMillis();
        switch (v.getId()) {
            case R.id.tv_change:
                ToastUtil.showToast(WithDrawActivity.this, "修改");
                break;
            case R.id.iv_clear:
                mEtNumber.setText("");
                break;
            case R.id.tv_total:
                mEtNumber.setText(new DecimalFormat("#0.00").format(mUsableSum));
                break;
            case R.id.tv_confirm:
                doWithDraw();
                break;
        }
    }

    private void doWithDraw() {
        try {
            hideSoftInput();
            Double value;
            if (TextUtils.isEmpty(mAccount)) {
                ToastUtil.showToast(this, "请绑定支付宝账号");
                return;
            }
            mWithDrawNum = mEtNumber.getText().toString().trim();
            if (TextUtils.isEmpty(mWithDrawNum)) {
                ToastUtil.showToast(this, "请输入提现金额");
                return;
            }
            if (mWithDrawNum.contains(".")) {
                value = new Double(mWithDrawNum).doubleValue();
            } else {
                value = new Integer(mWithDrawNum).doubleValue();
            }
            if (value == 0) {
                ToastUtil.showToast(WithDrawActivity.this, "提现金额不能为0");
                return;
            }
            if (value < 9) {
                ToastUtil.showToast(this, "取现的金额不能少于10元。");
            } else {
                mPresenter.doWithDraw("1", mWithDrawNum);
            }
        } catch (Exception ex) {
            LogUtils.e(ex.getMessage());
        }
    }

    @Override
    public void onWithDrawResult(HttpResult httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            ToastUtil.showToast(WithDrawActivity.this, TextUtils.isEmpty(httpResult.msg) ? "提现成功" : httpResult.msg);
            int aDouble = new Double(mWithDrawNum).intValue();
            int total = new Double(mUsableSum).intValue();
            int value = total - aDouble;
            mTvBalance.setText("¥" + value);
            RxBus.getDefault().post(new UpDataUserInfoEvent());
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录");
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            ToastUtil.showToast(WithDrawActivity.this, TextUtils.isEmpty(httpResult.msg) ? "提现失败" : httpResult.msg);
        }
    }

    @Override
    public void ReLogin() {
        ToastUtil.showToast(WithDrawActivity.this, "登录超时,请重新登录");
        startActivityForResult(new Intent(WithDrawActivity.this, LoginActivity.class), ACTION_LOGIN);
    }

    @Override
    public void showView(UserHomeBean httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            mAccount = httpResult.userInfo.zfb;
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(WithDrawActivity.this, "登录超时,请重新登录");
            startActivity(new Intent(WithDrawActivity.this, LoginActivity.class));
        } else if (httpResult.code == Constant.HttpResult.FAILD) {
            ToastUtil.showToast(WithDrawActivity.this, TextUtils.isEmpty(httpResult.msg) ? "数据请求失败" : httpResult.msg);
            showView(new UserHomeBean());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case BIND_ALIPAY:
                    LogUtils.i("绑定成功，返回");
                    break;
                case ACTION_LOGIN:
                    if (data != null) {
                        boolean booleanExtra = data.getBooleanExtra(Constant.Activity.Data, false);
                        if (booleanExtra && SettingUtils.isLogin(WithDrawActivity.this))
                            mPresenter.getUserData();
                    }
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
