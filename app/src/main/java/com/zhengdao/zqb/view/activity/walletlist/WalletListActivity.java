package com.zhengdao.zqb.view.activity.walletlist;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.kennyc.view.MultiStateView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.BalanceEntity;
import com.zhengdao.zqb.entity.BalanceHttpEntity;
import com.zhengdao.zqb.entity.FinishEvent;
import com.zhengdao.zqb.entity.IntegralEntity;
import com.zhengdao.zqb.entity.IntegralHttpEntity;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.view.activity.bindalipay.BindAliPayActivity;
import com.zhengdao.zqb.view.activity.chargeaccount.ChargeAccountActivity;
import com.zhengdao.zqb.view.activity.login.LoginActivity;
import com.zhengdao.zqb.view.activity.withdraw.WithDrawActivity;
import com.zhengdao.zqb.view.adapter.WalletListAdapter;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WalletListActivity extends MVPBaseActivity<WalletListContract.View, WalletListPresenter> implements WalletListContract.View, View.OnClickListener {

    private static final int BIND_ALIPAY   = 101;
    private static final int ACTION_CHARGE = 102;
    @BindView(R.id.iv_back)
    ImageView          mIvBack;
    @BindView(R.id.tv_title)
    TextView           mTvTitle;
    @BindView(R.id.tv_charge)
    TextView           mTvCharge;
    @BindView(R.id.tv_head)
    TextView           mTvHead;
    @BindView(R.id.tv_balance)
    TextView           mTvBalance;
    @BindView(R.id.tv_withdraw)
    TextView           mTvWithdraw;
    @BindView(R.id.ll_head)
    LinearLayout       mLlHead;
    @BindView(R.id.tv_date)
    TextView           mTvDate;
    @BindView(R.id.iv_select_date)
    ImageView          mIvSelectDate;
    @BindView(R.id.listView)
    ListView           mListView;
    @BindView(R.id.multiStateView)
    MultiStateView     mMultiStateView;
    @BindView(R.id.swipeRefreshLayout)
    SmartRefreshLayout mSwipeRefreshLayout;

    private String mType;
    private long mCurrentTimeMillis = 0;
    private String mDate;
    private Double mUsableSum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_list);
        ButterKnife.bind(this);
        initClickListener();
        initView();
        init();
        initData();
    }

    private void init() {
        mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                initData();
                refreshlayout.finishRefresh();
            }
        });
        mSwipeRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                if (!TextUtils.isEmpty(mType) && !TextUtils.isEmpty(mDate))
                    mPresenter.getMore(mDate, mType);
                refreshlayout.finishLoadmore();
            }
        });
        mMultiStateView.getView(MultiStateView.VIEW_STATE_ERROR).findViewById(R.id.btn_error_reload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initData();
            }
        });
        mTvCharge.setVisibility(View.GONE);
    }

    private void initView() {
        mType = getIntent().getStringExtra(Constant.Activity.Type);
        if (!TextUtils.isEmpty(mType) && mType.equals("rebPocket")) {
            mTvTitle.setText("红包");
            mLlHead.setVisibility(View.GONE);
        } else if (!TextUtils.isEmpty(mType) && mType.equals("bar_value")) {
            mTvTitle.setText("我的吧值");
            mTvHead.setText("我的吧值");
            mTvWithdraw.setVisibility(View.INVISIBLE);
        } else if (!TextUtils.isEmpty(mType) && mType.equals("integral")) {
            mTvTitle.setText("我的积分");
            mTvHead.setText("我的积分");
            mTvWithdraw.setVisibility(View.INVISIBLE);
        } else if (!TextUtils.isEmpty(mType) && mType.equals("brokerage")) {
            mTvTitle.setText("推广佣金");
            mTvHead.setText("好友贡献的佣金");
            mTvWithdraw.setVisibility(View.INVISIBLE);
        } else if (!TextUtils.isEmpty(mType) && mType.equals("charge_record")) {
            mTvTitle.setText("充值记录");
            mLlHead.setVisibility(View.GONE);
        }
        Calendar c = Calendar.getInstance();//
        int year = c.get(Calendar.YEAR); // 获取当前年份
        int month = c.get(Calendar.MONTH) + 1;// 获取当前月份
        mDate = String.valueOf(year) + "-" + String.valueOf(month);
        mTvDate.setText(mDate);
    }

    private void initData() {
        if (!TextUtils.isEmpty(mType) && !TextUtils.isEmpty(mDate))
            mPresenter.updataData(mDate, mType);
    }

    private void initClickListener() {
        mIvBack.setOnClickListener(this);
        mTvCharge.setOnClickListener(this);
        mTvWithdraw.setOnClickListener(this);
        mIvSelectDate.setOnClickListener(this);
    }

    @Override
    public void showListView(WalletListAdapter adapter, boolean isHasNext) {
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
        if (adapter != null) {
            mSwipeRefreshLayout.finishRefresh();
            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
            mListView.setAdapter(adapter);
        }
        if (isHasNext) {
            mSwipeRefreshLayout.resetNoMoreData();
        } else {
            mSwipeRefreshLayout.finishLoadmoreWithNoMoreData();
        }
    }

    @Override
    public void showBalanceView(BalanceHttpEntity<BalanceEntity> httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            mUsableSum = httpResult.usableSum;
            try {
                mTvBalance.setText(new DecimalFormat("#0.00").format(httpResult.usableSum));
            } catch (Exception ex) {
                LogUtils.e(ex.getMessage());
            }
        } else {
            ToastUtil.showToast(this, TextUtils.isEmpty(httpResult.msg) ? "数据获取失败" : httpResult.msg);
        }
    }

    @Override
    public void showIntegralView(IntegralHttpEntity<IntegralEntity> httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            try {
                mTvBalance.setText(new DecimalFormat("#0.00").format(httpResult.integral));
            } catch (Exception ex) {
                LogUtils.e(ex.getMessage());
            }
        } else {
            ToastUtil.showToast(this, TextUtils.isEmpty(httpResult.msg) ? "数据获取失败" : httpResult.msg);
        }
    }

    @Override
    public void noData() {
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
        mSwipeRefreshLayout.finishLoadmoreWithNoMoreData();
    }

    @Override
    public void showViewState(boolean isHasNext) {
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
        if (isHasNext) {
            mSwipeRefreshLayout.resetNoMoreData();
        } else {
            mSwipeRefreshLayout.finishLoadmoreWithNoMoreData();
        }
    }

    @Override
    public void ReLogin() {
        ToastUtil.showToast(this, "登录超时,请重新登录");
        startActivity(new Intent(this, LoginActivity.class));
    }

    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - mCurrentTimeMillis < 1000)
            return;
        mCurrentTimeMillis = System.currentTimeMillis();
        switch (v.getId()) {
            case R.id.iv_back:
                RxBus.getDefault().post(new FinishEvent());
                WalletListActivity.this.finish();
                break;
            case R.id.tv_charge:
                Intent chargeAccount = new Intent(WalletListActivity.this, ChargeAccountActivity.class);
                startActivityForResult(chargeAccount, ACTION_CHARGE);
                break;
            case R.id.tv_withdraw:
                if (TextUtils.isEmpty(SettingUtils.getAlipayAccount(this))) {
                    ToastUtil.showToast(this, "请绑定支付宝");
                    startActivityForResult(new Intent(this, BindAliPayActivity.class), BIND_ALIPAY);
                    return;
                }
                Intent withdraw = new Intent(WalletListActivity.this, WithDrawActivity.class);
                withdraw.putExtra(Constant.Activity.Data1, mUsableSum);
                startActivity(withdraw);
                break;
            case R.id.iv_select_date:
                selectDate();
                break;
        }
    }

    private void selectDate() {
        hideSoftInput();
        boolean[] booleen = new boolean[]{true, true, false, false, false, false};
        Calendar startDate = Calendar.getInstance();
        startDate.set(1930, 0, 1);
        Calendar endDate = Calendar.getInstance();
        endDate.set(endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH), endDate.get(Calendar.DAY_OF_MONTH));
        TimePickerView pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                mDate = getTime(date);
                mTvDate.setText(mDate);
                if (!TextUtils.isEmpty(mType) && !TextUtils.isEmpty(mDate))
                    mPresenter.getData(mDate, mType);
            }
        })
                .setType(booleen) //显示类型
                .setRangDate(startDate, endDate)//起始终止年月日设定
                .build();
        pvTime.setDate(Calendar.getInstance());
        pvTime.show();
    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        return format.format(date);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case BIND_ALIPAY:
                if (resultCode == Activity.RESULT_OK) {
                    if (!TextUtils.isEmpty(mType) && !TextUtils.isEmpty(mDate))
                        mPresenter.getData(mDate, mType);
                }
                break;
            case ACTION_CHARGE:
                if (resultCode == Activity.RESULT_OK) {
                    initData();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
