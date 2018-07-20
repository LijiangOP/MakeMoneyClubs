package com.zhengdao.zqb.view.activity.setting;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.UserInfoBean;
import com.zhengdao.zqb.event.LogOutEvent;
import com.zhengdao.zqb.event.UpDataUserInfoEvent;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.utils.Utils;
import com.zhengdao.zqb.view.activity.about.AboutActivity;
import com.zhengdao.zqb.view.activity.accountsafe.AccountSafeActivity;
import com.zhengdao.zqb.view.activity.advicefeedback.AdviceFeedbackActivity;
import com.zhengdao.zqb.view.activity.bindalipay.BindAliPayActivity;
import com.zhengdao.zqb.view.activity.identitycertify.IdentityCertifyActivity;
import com.zhengdao.zqb.view.activity.login.LoginActivity;
import com.zhengdao.zqb.view.activity.memberright.MemberRightActivity;
import com.zhengdao.zqb.view.activity.personalinfo.PersonalInfoActivity;
import com.zhengdao.zqb.view.activity.relevanceaccount.RelevanceAccountActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


public class SettingActivity extends MVPBaseActivity<SettingContract.View, SettingPresenter> implements SettingContract.View, View.OnClickListener {
    private static final int ACTION_LOGIN       = 001;
    private static final int ACTION_BIND_ALIPAY = 002;
    @BindView(R.id.iv_user_icon)
    CircleImageView mIvUserIcon;
    @BindView(R.id.tv_nickname)
    TextView        mTvNickname;
    @BindView(R.id.tv_username)
    TextView        mTvUsername;
    @BindView(R.id.ll_user_info)
    LinearLayout    mLlUserInfo;
    @BindView(R.id.ll_member_right)
    LinearLayout    mLlMemberRight;
    @BindView(R.id.ll_identity_certify)
    LinearLayout    mLlIdentityCertify;
    @BindView(R.id.ll_account_safe)
    LinearLayout    mLlAccountSafe;
    @BindView(R.id.ll_bind_aliPay)
    LinearLayout    mLlBindAliPay;
    @BindView(R.id.ll_relevance_account)
    LinearLayout    mLlRelevanceAccount;
    @BindView(R.id.ll_advice_feedback)
    LinearLayout    mLlAdviceFeedback;
    @BindView(R.id.ll_about)
    LinearLayout    mLlAbout;
    @BindView(R.id.tv_log_out)
    TextView        mTvLogOut;
    private long mCurrentTimeMillis = 0;
    private String     mPwdStrength;
    private Disposable mUpDataDisposable;
    private int        mUserId;
    private String     mZfb;
    private String     mRealName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        setTitle("账户设置");
        init();
        setOnclickListener();
        String skip = getIntent().getStringExtra(Constant.Activity.Skip);
        if (!TextUtils.isEmpty(skip) && "Skip_To_Certification".equals(skip)) {
            Utils.StartActivity(SettingActivity.this, new Intent(SettingActivity.this, PersonalInfoActivity.class));
        }
    }

    private void init() {
        mPresenter.getUserData();
        mUpDataDisposable = RxBus.getDefault().toObservable(UpDataUserInfoEvent.class).subscribe(new Consumer<UpDataUserInfoEvent>() {
            @Override
            public void accept(UpDataUserInfoEvent upDataUserInfoEvent) throws Exception {
                mPresenter.getUserData();
            }
        });
    }

    private void setOnclickListener() {
        mLlUserInfo.setOnClickListener(this);
        mLlMemberRight.setOnClickListener(this);
        mLlIdentityCertify.setOnClickListener(this);
        mLlAccountSafe.setOnClickListener(this);
        mLlRelevanceAccount.setOnClickListener(this);
        mLlAdviceFeedback.setOnClickListener(this);
        mLlBindAliPay.setOnClickListener(this);
        mLlAbout.setOnClickListener(this);
        mTvLogOut.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - mCurrentTimeMillis < 500)
            return;
        mCurrentTimeMillis = System.currentTimeMillis();
        switch (v.getId()) {
            case R.id.ll_user_info:
                Utils.StartActivity(SettingActivity.this, new Intent(SettingActivity.this, PersonalInfoActivity.class));
                break;
            case R.id.ll_member_right:
                Utils.StartActivity(SettingActivity.this, new Intent(SettingActivity.this, MemberRightActivity.class));
                break;
            case R.id.ll_identity_certify:
                Utils.StartActivity(SettingActivity.this, new Intent(SettingActivity.this, IdentityCertifyActivity.class));
                break;
            case R.id.ll_account_safe:
                Intent intent = new Intent(SettingActivity.this, AccountSafeActivity.class);
                intent.putExtra(Constant.Activity.Data, mPwdStrength);
                Utils.StartActivity(SettingActivity.this, intent);
                break;
            case R.id.ll_relevance_account:
                Utils.StartActivity(SettingActivity.this, new Intent(SettingActivity.this, RelevanceAccountActivity.class));
                break;
            case R.id.ll_bind_aliPay:
                Intent bindAlipay = new Intent(SettingActivity.this, BindAliPayActivity.class);
                bindAlipay.putExtra(Constant.Activity.Data, mZfb);
                bindAlipay.putExtra(Constant.Activity.Data1, mRealName);
                if (SettingUtils.isLogin(SettingActivity.this))
                    startActivityForResult(bindAlipay, ACTION_BIND_ALIPAY);
                else
                    startActivityForResult(new Intent(SettingActivity.this, LoginActivity.class), ACTION_LOGIN);
                break;
            case R.id.ll_advice_feedback:
                Intent adviceFeedBack = new Intent(SettingActivity.this, AdviceFeedbackActivity.class);
                adviceFeedBack.putExtra(Constant.Activity.Data, mUserId);
                Utils.StartActivity(SettingActivity.this, adviceFeedBack);
                break;
            case R.id.ll_about:
                Utils.StartActivity(SettingActivity.this, new Intent(SettingActivity.this, AboutActivity.class));
                break;
            case R.id.tv_log_out:
                doLogOut();
                break;
        }
    }

    private void doLogOut() {
        SettingUtils.setLoginState(this, false);
        SettingUtils.setPhoneNum(this, "");
        SettingUtils.setUserToken(this, "");
        SettingUtils.setAccount(this, "");
        SettingUtils.setAlipayAccount(this, "");
        RxBus.getDefault().post(new LogOutEvent());
        SettingActivity.this.finish();
        ToastUtil.showToast(this, "退出登录成功");
    }

    @Override
    public void ShowView(UserInfoBean result) {
        if (result.code == Constant.HttpResult.SUCCEED) {
            Glide.with(this).load(result.user.avatar).error(R.drawable.default_icon).into(mIvUserIcon);
            mTvNickname.setText(TextUtils.isEmpty(result.user.nickName) ? "" : result.user.nickName);
            mTvUsername.setText(TextUtils.isEmpty(result.user.userName) ? "" : result.user.userName);
            mPwdStrength = result.user.pwdStrength;
            mUserId = result.user.id;
            mZfb = result.userinfo.zfb;
            mRealName = result.userinfo.realName;
        } else if (result.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录");
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            ToastUtil.showToast(this, TextUtils.isEmpty(result.msg) ? "" : result.msg);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case ACTION_BIND_ALIPAY:
                    mPresenter.getUserData();
                    break;
                case ACTION_LOGIN:
                    if (data != null) {
                        boolean booleanExtra = data.getBooleanExtra(Constant.Activity.Data, false);
                        if (booleanExtra) {
                            if (SettingUtils.isLogin(this))
                                mPresenter.getUserData();
                        }
                    }
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUpDataDisposable != null)
            mUpDataDisposable.dispose();
    }
}
