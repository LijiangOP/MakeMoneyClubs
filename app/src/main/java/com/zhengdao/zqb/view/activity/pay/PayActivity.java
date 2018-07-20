package com.zhengdao.zqb.view.activity.pay;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.customview.PswInputView.InputPwdView;
import com.zhengdao.zqb.customview.PswInputView.MyInputPwdUtil;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.WalletHttpEntity;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.utils.Utils;
import com.zhengdao.zqb.view.activity.accountsafe.AccountSafeActivity;
import com.zhengdao.zqb.view.activity.login.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PayActivity extends MVPBaseActivity<PayContract.View, PayPresenter> implements PayContract.View, View.OnClickListener {

    @BindView(R.id.tv_price)
    TextView  mTvPrice;
    @BindView(R.id.tv_confirm)
    TextView  mTvConfirm;
    @BindView(R.id.tv_balance_value)
    TextView  mTvBalanceValue;
    @BindView(R.id.tv_balance_not_enough)
    TextView  mTvBalanceNotEnough;
    @BindView(R.id.iv_balance_pay)
    ImageView mIvBalancePay;
    @BindView(R.id.iv_wechat_pay)
    ImageView mIvWechatPay;
    @BindView(R.id.iv_alipay)
    ImageView mIvAlipay;
    private String mPrice;
    private int mPayType = -1;
    private MyInputPwdUtil myInputPwdUtil;
    private long mCurrentTimeMillis = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        ButterKnife.bind(this);
        setTitle("支付");
        init();
        mPresenter.getBalance();
    }

    private void init() {
        mPrice = getIntent().getStringExtra(Constant.Activity.Price);
        if (!TextUtils.isEmpty(mPrice))
            mTvPrice.setText(mPrice);
        mTvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPayType == -1) {
                    ToastUtil.showToast(PayActivity.this, "请选择支付方式");
                    return;
                }
                myInputPwdUtil.show();
            }
        });
        myInputPwdUtil = new MyInputPwdUtil(this);
        myInputPwdUtil.getMyInputDialogBuilder().setAnimStyle(R.style.dialog_anim);//可以定制自己进入退出动画，不设置没有动画
        myInputPwdUtil.setListener(new InputPwdView.InputPwdListener() {
            @Override
            public void hide() {
                myInputPwdUtil.hide();
            }

            @Override
            public void forgetPwd() {
                Intent intent = new Intent(PayActivity.this, AccountSafeActivity.class);
                Utils.StartActivity(PayActivity.this, intent);
            }

            @Override
            public void finishPwd(String pwd) {
                if (!TextUtils.isEmpty(mPrice))
                    mPresenter.pay(mPayType, Integer.valueOf(mPrice));
            }
        });
        mIvBalancePay.setOnClickListener(this);
        mIvWechatPay.setOnClickListener(this);
        mIvAlipay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - mCurrentTimeMillis < 500)
            return;
        mCurrentTimeMillis = System.currentTimeMillis();
        switch (v.getId()) {
            case R.id.iv_balance_pay:
                if (mPayType != 0) {
                    mPayType = 0;
                    mIvBalancePay.setImageDrawable(getResources().getDrawable(R.drawable.btn_select));
                    mIvWechatPay.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvAlipay.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                } else {
                    mPayType = -1;
                    mIvBalancePay.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                }
                break;
            case R.id.iv_wechat_pay:
                if (mPayType != 1) {
                    mPayType = 1;
                    mIvBalancePay.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvWechatPay.setImageDrawable(getResources().getDrawable(R.drawable.btn_select));
                    mIvAlipay.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                } else {
                    mPayType = -1;
                    mIvWechatPay.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                }
                break;
            case R.id.iv_alipay:
                if (mPayType != 2) {
                    mPayType = 2;
                    mIvBalancePay.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvWechatPay.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvAlipay.setImageDrawable(getResources().getDrawable(R.drawable.btn_select));
                } else {
                    mPayType = -1;
                    mIvAlipay.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                }
                break;
        }
    }

    @Override
    public void onGetDataResult(WalletHttpEntity httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            Double usableSum = httpResult.account.usableSum;
            mTvBalanceValue.setText("(可用余额 ¥" + usableSum + ")");
            try {
                int value = new Double(mPrice).intValue();
                if (value != 0 && value > usableSum) {
                    mTvBalanceNotEnough.setVisibility(View.VISIBLE);
                    mIvBalancePay.setVisibility(View.GONE);
                }
            } catch (Exception ex) {
                LogUtils.e(ex.getMessage());
            }
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录");
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            ToastUtil.showToast(this, TextUtils.isEmpty(httpResult.msg) ? "数据请求失败" : httpResult.msg);
        }
    }

    @Override
    public void onPayResult(HttpResult httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            ToastUtil.showToast(this, TextUtils.isEmpty(httpResult.msg) ? "支付成功" : httpResult.msg);
        } else {
            ToastUtil.showToast(this, TextUtils.isEmpty(httpResult.msg) ? "支付失败" : httpResult.msg);
        }
    }
}
