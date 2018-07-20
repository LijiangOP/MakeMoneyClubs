package com.zhengdao.zqb.view.activity.accountsafe;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.view.activity.changebindphonedetail.ChangeBindPhoneDetailActivity;
import com.zhengdao.zqb.view.activity.identityvertify.IdentityVertifyActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AccountSafeActivity extends MVPBaseActivity<AccountSafeContract.View, AccountSafePresenter> implements AccountSafeContract.View, View.OnClickListener {

    @BindView(R.id.tv_login_psw_desc)
    TextView     mTvLoginPswDesc;
    @BindView(R.id.ll_login_psw)
    LinearLayout mLlLoginPsw;
    @BindView(R.id.tv_phone_num)
    TextView     mTvPhoneNum;
    @BindView(R.id.ll_phone_vertify)
    LinearLayout mLlPhoneVertify;
    @BindView(R.id.tv_pay_psw_desc)
    TextView     mTvPayPswDesc;
    @BindView(R.id.ll_pay_psw)
    LinearLayout mLlPayPsw;
    private String mPhoneNum;
    private long mCurrentTimeMillis = 0;
    private String mPswStrength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountsafe);
        ButterKnife.bind(this);
        setTitle("账户安全");
        init();
        setOnclickListener();
    }

    private void init() {
        mPswStrength = getIntent().getStringExtra(Constant.Activity.Data);
        if (!TextUtils.isEmpty(mPswStrength)) {
            try {
                switch (Integer.valueOf(mPswStrength)) {
                    case 1:
                        mTvLoginPswDesc.setText("强");
                        mTvLoginPswDesc.setTextColor(getResources().getColor(R.color.color_26cb7c));
                        break;
                    default:
                        mTvLoginPswDesc.setText("弱");
                        mTvLoginPswDesc.setTextColor(getResources().getColor(R.color.color_fc3135));
                        break;
                }
            } catch (Exception ex) {
                LogUtils.e(ex.getMessage());
            }
        }
        mPhoneNum = SettingUtils.getPhoneNum(AccountSafeActivity.this);
        if (!TextUtils.isEmpty(mPhoneNum) && mPhoneNum.length() == 11) {
            mTvPhoneNum.setText(TextUtils.isEmpty(mPhoneNum) ? "" : mPhoneNum);
        }
    }

    private void setOnclickListener() {
        mLlLoginPsw.setOnClickListener(this);
        mLlPhoneVertify.setOnClickListener(this);
        mLlPayPsw.setOnClickListener(this);
        mLlPayPsw.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - mCurrentTimeMillis < 1000)
            return;
        mCurrentTimeMillis = System.currentTimeMillis();
        switch (v.getId()) {
            case R.id.ll_login_psw:
                Intent login = new Intent(AccountSafeActivity.this, IdentityVertifyActivity.class);
                login.putExtra(Constant.Activity.SkipData, "loginPsw");
                startActivity(login);
                break;
            case R.id.ll_phone_vertify:
                startActivity(new Intent(AccountSafeActivity.this, ChangeBindPhoneDetailActivity.class));
                break;
            case R.id.ll_pay_psw:
                Intent pay = new Intent(AccountSafeActivity.this, IdentityVertifyActivity.class);
                pay.putExtra(Constant.Activity.SkipData, "payPsw");
                startActivity(pay);
                break;
        }
    }
}
