package com.zhengdao.zqb.view.activity.bindalipay;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.event.NewHandUpDataEvent;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.view.activity.login.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;


public class BindAliPayActivity extends MVPBaseActivity<BindAliPayContract.View, BindAliPayPresenter> implements BindAliPayContract.View {
    @BindView(R.id.et_real_name)
    EditText mEtRealName;
    @BindView(R.id.et_aliPay)
    EditText mEtAliPay;
    @BindView(R.id.tv_commit)
    TextView mTvCommit;

    private String mFromWhere;
    private String mRealName;
    private String mZfb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_alipay);
        ButterKnife.bind(this);
        setTitle("绑定支付宝");
        init();
    }

    private void init() {
        mZfb = getIntent().getStringExtra(Constant.Activity.Data);
        mRealName = getIntent().getStringExtra(Constant.Activity.Data1);
        mFromWhere = getIntent().getStringExtra(Constant.Activity.Type);
        mTvCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSetAliPay();
            }
        });
        mEtRealName.setHint(TextUtils.isEmpty(mRealName) ? "" : mRealName);
        mEtAliPay.setHint(TextUtils.isEmpty(mZfb) ? "" : mZfb);
    }

    private void doSetAliPay() {
        String userName = mEtRealName.getText().toString().trim();
        String aliPay = mEtAliPay.getText().toString().trim();
        if (TextUtils.isEmpty(userName)) {
            ToastUtil.showToast(BindAliPayActivity.this, "请输入真实姓名");
            return;
        }
        if (TextUtils.isEmpty(aliPay)) {
            ToastUtil.showToast(BindAliPayActivity.this, "请输入支付宝账号");
            return;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("realName", userName);
        jsonObject.put("zfb", aliPay);
        String userToken = SettingUtils.getUserToken(BindAliPayActivity.this);
        mPresenter.setAliPay(userToken, jsonObject.toString());
    }

    @Override
    public void onSetAliPayResult(HttpResult httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            ToastUtil.showToast(BindAliPayActivity.this, "绑定成功");
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            if (!TextUtils.isEmpty(mFromWhere))
                RxBus.getDefault().post(new NewHandUpDataEvent());
            this.finish();
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录");
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            ToastUtil.showToast(BindAliPayActivity.this, TextUtils.isEmpty(httpResult.msg) ? "" : httpResult.msg);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
