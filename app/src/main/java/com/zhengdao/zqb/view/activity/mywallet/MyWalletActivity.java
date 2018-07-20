package com.zhengdao.zqb.view.activity.mywallet;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.WalletHttpEntity;
import com.zhengdao.zqb.event.UpDataUserInfoEvent;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.utils.Utils;
import com.zhengdao.zqb.view.activity.chargeaccount.ChargeAccountActivity;
import com.zhengdao.zqb.view.activity.chargephone.ChargePhoneActivity;
import com.zhengdao.zqb.view.activity.login.LoginActivity;
import com.zhengdao.zqb.view.activity.mybalance.MyBalanceActivity;
import com.zhengdao.zqb.view.activity.rewardticket.RewardTicketActivity;
import com.zhengdao.zqb.view.activity.walletlist.WalletListActivity;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MyWalletActivity extends MVPBaseActivity<MyWalletContract.View, MyWalletPresenter> implements MyWalletContract.View, View.OnClickListener {

    private static final int ACTION_LOGIN  = 001;
    private static final int ACTION_CHARGE = 002;
    @BindView(R.id.tv_red_packet)
    TextView           mTvRedPacket;
    @BindView(R.id.tv_balance)
    TextView           mTvBalance;
    @BindView(R.id.tv_reward)
    TextView           mTvReward;
    @BindView(R.id.tv_balance_value)
    TextView           mTvBalanceValue;
    @BindView(R.id.tv_bar_value)
    TextView           mTvBarValue;
    @BindView(R.id.ll_bar_value)
    LinearLayout       mLlBarValue;
    @BindView(R.id.tv_integral)
    TextView           mTvIntegral;
    @BindView(R.id.ll_integral)
    LinearLayout       mLlIntegral;
    @BindView(R.id.tv_account_charge)
    TextView           mTvAccountCharge;
    @BindView(R.id.tv_brokerage)
    TextView           mTvBrokerage;
    @BindView(R.id.tv_phone_charge)
    TextView           mTvPhoneCharge;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private long mCurrentTimeMillis = 0;
    private Disposable mUpDataUserInfoDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mywallet);
        ButterKnife.bind(this);
        setTitle("我的钱包");
        init();
        initOnclickListener();
        String skip = getIntent().getStringExtra(Constant.Activity.Skip);
        if (!TextUtils.isEmpty(skip) && "Skip_To_Balance".equals(skip)) {
            Utils.StartActivity(MyWalletActivity.this, new Intent(MyWalletActivity.this, MyBalanceActivity.class));
        }
    }

    private void init() {
        mUpDataUserInfoDisposable = RxBus.getDefault().toObservable(UpDataUserInfoEvent.class).subscribe(new Consumer<UpDataUserInfoEvent>() {
            @Override
            public void accept(UpDataUserInfoEvent upDataUserInfoEvent) throws Exception {
                LogUtils.i("需要刷新该页面数据");
                mPresenter.getData();
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getData();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        mPresenter.getData();
        mTvAccountCharge.setVisibility(View.GONE);
    }

    private void initOnclickListener() {
        mTvRedPacket.setOnClickListener(this);
        mTvBalance.setOnClickListener(this);
        mTvReward.setOnClickListener(this);
        mLlBarValue.setOnClickListener(this);
        mLlIntegral.setOnClickListener(this);
        mTvAccountCharge.setOnClickListener(this);
        mTvBrokerage.setOnClickListener(this);
        mTvPhoneCharge.setOnClickListener(this);
    }

    @Override
    public void onGetDataResult(WalletHttpEntity httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            mTvBalanceValue.setText(new DecimalFormat("#0.00").format(httpResult.account.usableSum));
            mTvBarValue.setText("" + httpResult.account.luckPoint);
            mTvIntegral.setText(new DecimalFormat("#0.00").format(httpResult.account.integral));
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录");
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            ToastUtil.showToast(this, TextUtils.isEmpty(httpResult.msg) ? "数据请求失败" : httpResult.msg);
        }
    }

    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - mCurrentTimeMillis < 1000)
            return;
        mCurrentTimeMillis = System.currentTimeMillis();
        switch (v.getId()) {
            case R.id.tv_red_packet:
                ToastUtil.showToast(this, getResources().getString(R.string.unOpen));
                //                Intent rebPocket = new Intent(MyWalletActivity.this, WalletListActivity.class);
                //                rebPocket.putExtra(Constant.Activity.Type, "rebPocket");
                //                startActivity(rebPocket);
                break;
            case R.id.tv_balance:
                Intent balance = new Intent(MyWalletActivity.this, MyBalanceActivity.class);
                Utils.StartActivity(MyWalletActivity.this, balance);
                break;
            case R.id.tv_reward:
                startActivity(new Intent(MyWalletActivity.this, RewardTicketActivity.class));
                break;
            case R.id.ll_integral:
                Intent integral = new Intent(MyWalletActivity.this, WalletListActivity.class);
                integral.putExtra(Constant.Activity.Type, "integral");
                Utils.StartActivity(MyWalletActivity.this, integral);
                break;
            case R.id.tv_account_charge:
                if (!SettingUtils.isLogin(this))
                    startActivity(new Intent(this, LoginActivity.class));
                else {
                    Intent chargeAccount = new Intent(MyWalletActivity.this, ChargeAccountActivity.class);
                    startActivityForResult(chargeAccount, ACTION_CHARGE);
                }
                break;
            case R.id.tv_brokerage:
                Intent brokerage = new Intent(MyWalletActivity.this, WalletListActivity.class);
                brokerage.putExtra(Constant.Activity.Type, "brokerage");
                Utils.StartActivity(MyWalletActivity.this, brokerage);
                break;
            case R.id.tv_phone_charge:
                Intent chargePhone = new Intent(MyWalletActivity.this, ChargePhoneActivity.class);
                Utils.StartActivity(MyWalletActivity.this, chargePhone);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ACTION_CHARGE:
                if (resultCode == Activity.RESULT_OK) {
                    Intent balance = new Intent(MyWalletActivity.this, WalletListActivity.class);
                    balance.putExtra(Constant.Activity.Type, "balance");
                    Utils.StartActivity(MyWalletActivity.this, balance);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUpDataUserInfoDisposable != null)
            mUpDataUserInfoDisposable.dispose();
    }

}
