package com.zhengdao.zqb.view.activity.registeconfirm;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.UserInfo;
import com.zhengdao.zqb.event.RegistSuccessEvent;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.view.activity.login.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisteConfirmActivity extends MVPBaseActivity<RegisteConfirmContract.View, RegisteConfirmPresenter> implements RegisteConfirmContract.View, View.OnClickListener {
    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.et_psw)
    EditText  mEtPsw;
    @BindView(R.id.et_confirm_psw)
    EditText  mEtConfirmPsw;
    @BindView(R.id.tv_confirm)
    TextView  mTvConfirm;
    private String mAccount;
    private String mPsw;
    private long mCurrentTimeMillis = 0;
    private String mPwdStrength; //0:简单 1:中等 2:强

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist_confirm);
        setSwipeBackEnable(false);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mAccount = getIntent().getStringExtra("token");
        mIvBack.setOnClickListener(this);
        mTvConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - mCurrentTimeMillis < 1000)
            return;
        mCurrentTimeMillis = System.currentTimeMillis();
        switch (v.getId()) {
            case R.id.iv_back:
                RegisteConfirmActivity.this.finish();
                break;
            case R.id.tv_confirm:
                if (doRegiste() && !TextUtils.isEmpty(mAccount))
                    mPresenter.doRegiste(mAccount, mPsw, mPwdStrength);
                break;
        }
    }

    private boolean doRegiste() {
        mPsw = mEtPsw.getText().toString().trim();
        String trimCopy = mEtConfirmPsw.getText().toString().trim();
        if (TextUtils.isEmpty(mPsw)) {
            ToastUtil.showToast(RegisteConfirmActivity.this, "密码不能为空");
            return false;
        }
        if (mPsw.length() < 6) {
            ToastUtil.showToast(RegisteConfirmActivity.this, "密码长度不能小于6位");
            return false;
        }
        if (TextUtils.isEmpty(trimCopy)) {
            ToastUtil.showToast(RegisteConfirmActivity.this, "确认密码不能为空");
            return false;
        }
        if (!mPsw.equals(trimCopy)) {
            ToastUtil.showToast(RegisteConfirmActivity.this, "前后密码不一致");
            return false;
        }
        mPwdStrength = TextUtils.isEmpty(checkPassword(mPsw)) ? "0" : checkPassword(mPsw);
        return true;
    }

    public String checkPassword(String passwordStr) {
        String regexZ = "\\d*";
        String regexS = "[a-zA-Z]+";
        String regexT = "\\W+$";
        String regexZT = "\\D*";
        String regexST = "[\\d\\W]*";
        String regexZS = "\\w*";
        String regexZST = "[\\w\\W]*";

        if (passwordStr.matches(regexZ)) {
            return "0";
        }
        if (passwordStr.matches(regexS)) {
            return "0";
        }
        if (passwordStr.matches(regexT)) {
            return "0";
        }
        if (passwordStr.matches(regexZT)) {
            return "1";
        }
        if (passwordStr.matches(regexST)) {
            return "1";
        }
        if (passwordStr.matches(regexZS)) {
            return "1";
        }
        if (passwordStr.matches(regexZST)) {
            return "2";
        }
        return passwordStr;
    }

    @Override
    public void onRegisteFinish(HttpResult<UserInfo> httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            if (httpResult.data != null) {
                UserInfo data = httpResult.data;
                SettingUtils.setLoginState(this, true);
                SettingUtils.setPhoneNum(this, data.phone);
                SettingUtils.setUserToken(this, data.token);
                SettingUtils.setUserID(this, "" + data.id);
                SettingUtils.setAccount(this, data.userName);
                RxBus.getDefault().post(new RegistSuccessEvent());
                ToastUtil.showToast(RegisteConfirmActivity.this, "注册成功!");
                RegisteConfirmActivity.this.finish();
            }
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录");
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            ToastUtil.showToast(RegisteConfirmActivity.this, "注册失败:" + httpResult.msg);
        }
    }
}
