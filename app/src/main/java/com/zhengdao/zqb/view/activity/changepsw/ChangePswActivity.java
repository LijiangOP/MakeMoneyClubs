package com.zhengdao.zqb.view.activity.changepsw;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.event.FinishIdentityVertifyEvent;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.Base64Utils;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.view.activity.login.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ChangePswActivity extends MVPBaseActivity<ChangePswContract.View, ChangePswPresenter> implements ChangePswContract.View, View.OnClickListener {

    @BindView(R.id.tv_hint)
    TextView  mTvHint;
    @BindView(R.id.et_input)
    EditText  mEtInput;
    @BindView(R.id.iv_clear)
    ImageView mIvClear;
    @BindView(R.id.tv_save)
    TextView  mTvSave;
    private String mStringType;
    private String mStringToken;
    private String mStringAccount;
    private long mCurrentTimeMillis = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepsw);
        ButterKnife.bind(this);
        initData();
        initView();
        initListener();
    }

    private void initData() {
        mStringType = getIntent().getStringExtra(Constant.Activity.Type);
        mStringAccount = getIntent().getStringExtra(Constant.Activity.Data);
        mStringToken = getIntent().getStringExtra(Constant.Activity.Data1);
    }

    private void initView() {
        if (!TextUtils.isEmpty(mStringType) && mStringType.equals("payPsw"))
            setTitle("修改支付密码");
        else
            setTitle("修改登录密码");
        String account = SettingUtils.getAccount(ChangePswActivity.this);
        if (!TextUtils.isEmpty(mStringAccount)) {
            SpannableString spannableString = new SpannableString("请为您的帐号\n" + mStringAccount + "\n设置一个新登录密码");
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#333333"));
            RelativeSizeSpan sizeSpan = new RelativeSizeSpan(1.5f);
            spannableString.setSpan(colorSpan, 6, 7 + mStringAccount.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(sizeSpan, 6, 7 + mStringAccount.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            mTvHint.setText(spannableString);
        } else if (!TextUtils.isEmpty(account)) {
            SpannableString spannableString;
            if (!TextUtils.isEmpty(mStringType) && mStringType.equals("payPsw"))
                spannableString = new SpannableString("请为您的帐号\n" + account + "\n设置一个新支付密码");
            else
                spannableString = new SpannableString("请为您的帐号\n" + account + "\n设置一个新登录密码");
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#333333"));
            RelativeSizeSpan sizeSpan = new RelativeSizeSpan(1.5f);
            spannableString.setSpan(colorSpan, 6, 7 + account.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(sizeSpan, 6, 7 + account.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            mTvHint.setText(spannableString);
        } else
            mTvHint.setVisibility(View.GONE);
    }

    private void initListener() {
        mIvClear.setOnClickListener(this);
        mTvSave.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - mCurrentTimeMillis < 1000)
            return;
        mCurrentTimeMillis = System.currentTimeMillis();
        switch (v.getId()) {
            case R.id.iv_clear:
                mEtInput.setText("");
                break;
            case R.id.tv_save:
                String psw = mEtInput.getText().toString().trim();
                String token = SettingUtils.getUserToken(ChangePswActivity.this);
                if (!TextUtils.isEmpty(psw)) {
                    if (TextUtils.isEmpty(token) && !TextUtils.isEmpty(mStringToken))
                        token = mStringToken;
                    if (!TextUtils.isEmpty(mStringType) && mStringType.equals("payPsw")) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("paypwd", psw);
                        mPresenter.changePayPsw(token, jsonObject.toString());
                    } else {
                        JSONObject jsonObject = new JSONObject();
                        psw = Base64Utils.getBase64(psw);
                        jsonObject.put("pwd", psw);
                        mPresenter.changeLoginPsw(token, jsonObject.toString());
                    }
                } else {
                    ToastUtil.showToast(ChangePswActivity.this, "请输入密码");
                }
                break;
        }
    }

    @Override
    public void onChangeLoginPswResult(HttpResult httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            ToastUtil.showToast(ChangePswActivity.this, "修改成功！");
            RxBus.getDefault().post(new FinishIdentityVertifyEvent());
            ChangePswActivity.this.finish();
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录");
            startActivity(new Intent(this, LoginActivity.class));
            RxBus.getDefault().post(new FinishIdentityVertifyEvent());
            ChangePswActivity.this.finish();
        } else {
            ToastUtil.showToast(ChangePswActivity.this, TextUtils.isEmpty(httpResult.msg) ? "" : httpResult.msg);
        }
    }

    @Override
    public void onChangePayPswResult(HttpResult httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            ToastUtil.showToast(ChangePswActivity.this, "修改成功！");
            RxBus.getDefault().post(new FinishIdentityVertifyEvent());
            ChangePswActivity.this.finish();
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录");
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            ToastUtil.showToast(ChangePswActivity.this, TextUtils.isEmpty(httpResult.msg) ? "" : httpResult.msg);
        }
    }
}
