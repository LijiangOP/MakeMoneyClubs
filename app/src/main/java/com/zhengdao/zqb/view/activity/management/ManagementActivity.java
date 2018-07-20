package com.zhengdao.zqb.view.activity.management;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.RewardManagerHttpEntity;
import com.zhengdao.zqb.event.UpDataUserInfoEvent;
import com.zhengdao.zqb.event.WantedPublishEvent;
import com.zhengdao.zqb.event.WantedSaveEvent;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.utils.Utils;
import com.zhengdao.zqb.view.activity.kindofwanted.KindOfWantedActivity;
import com.zhengdao.zqb.view.activity.login.LoginActivity;
import com.zhengdao.zqb.view.activity.publish.PublishActivity;
import com.zhengdao.zqb.view.activity.publishedrewards.PublishedRewardsActivity;
import com.zhengdao.zqb.view.activity.rewardscheck.RewardsCheckActivity;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class ManagementActivity extends MVPBaseActivity<ManagementContract.View, ManagementPresenter> implements ManagementContract.View, View.OnClickListener {

    @BindView(R.id.iv_title_bar_back)
    ImageView    mIvTitleBarBack;
    @BindView(R.id.tv_title_bar_title)
    TextView     mTvTitleBarTitle;
    @BindView(R.id.tv_title_bar_right)
    TextView     mTvTitleBarRight;
    @BindView(R.id.tv_balance_value)
    TextView     mTvBalanceValue;
    @BindView(R.id.tv_balance_value_tag)
    TextView     mTvBalanceValueTag;
    @BindView(R.id.tv_reward)
    TextView     mTvReward;
    @BindView(R.id.tv_records)
    TextView     mTvRecords;
    @BindView(R.id.tv_publish)
    TextView     mTvPublish;
    @BindView(R.id.tv_check)
    TextView     mTvCheck;
    @BindView(R.id.tv_statistics)
    TextView     mTvStatistics;
    @BindView(R.id.ll_reward)
    LinearLayout mLlReward;
    @BindView(R.id.ll_records)
    LinearLayout mLlRecords;

    private long mCurrentTimeMillis = 0;
    private Disposable mUpDataDisposable;
    private CompositeDisposable mDisposables = new CompositeDisposable();
    private Disposable mWantedPublishDisposable;
    private Disposable mWantedSaveDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management);
        ButterKnife.bind(this);
        init();
        initListener();
    }

    private void init() {
        mTvTitleBarTitle.setText("悬赏管理");
        mTvTitleBarRight.setText("发布悬赏");
        mPresenter.getData();
    }

    private void initListener() {
        mIvTitleBarBack.setOnClickListener(this);
        mTvTitleBarRight.setOnClickListener(this);
        mTvPublish.setOnClickListener(this);
        mLlRecords.setOnClickListener(this);
        mTvCheck.setOnClickListener(this);
        mTvStatistics.setOnClickListener(this);
        mUpDataDisposable = RxBus.getDefault().toObservable(UpDataUserInfoEvent.class).subscribe(new Consumer<UpDataUserInfoEvent>() {
            @Override
            public void accept(UpDataUserInfoEvent upDataUserInfoEvent) throws Exception {
                mPresenter.getData();
            }
        });
        mWantedPublishDisposable = RxBus.getDefault().toObservable(WantedPublishEvent.class).subscribe(new Consumer<WantedPublishEvent>() {
            @Override
            public void accept(WantedPublishEvent wantedPublishEvent) throws Exception {
                mPresenter.getData();
            }
        });
        mWantedSaveDisposable = RxBus.getDefault().toObservable(WantedSaveEvent.class).subscribe(new Consumer<WantedSaveEvent>() {
            @Override
            public void accept(WantedSaveEvent wantedSaveEvent) throws Exception {
                mPresenter.getData();
            }
        });
        mDisposables.add(mWantedPublishDisposable);
        mDisposables.add(mWantedSaveDisposable);
        mDisposables.add(mUpDataDisposable);
        mTvTitleBarRight.setVisibility(View.GONE);//v1.2取消线上悬赏主发布悬赏功能
    }

    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - mCurrentTimeMillis < 500)
            return;
        mCurrentTimeMillis = System.currentTimeMillis();
        switch (v.getId()) {
            case R.id.iv_title_bar_back:
                ManagementActivity.this.finish();
                break;
            case R.id.tv_title_bar_right:
                Utils.StartActivity(ManagementActivity.this, new Intent(ManagementActivity.this, PublishActivity.class));
                break;
            case R.id.ll_records:
                Intent unCheck = new Intent(ManagementActivity.this, KindOfWantedActivity.class);
                unCheck.putExtra(Constant.Activity.Type, "unCheck");
                unCheck.putExtra(Constant.Activity.Skip, 1);
                startActivity(unCheck);
                break;
            case R.id.tv_publish:
                Utils.StartActivity(ManagementActivity.this, new Intent(ManagementActivity.this, PublishedRewardsActivity.class));
                break;
            case R.id.tv_check:
                Utils.StartActivity(ManagementActivity.this, new Intent(ManagementActivity.this, RewardsCheckActivity.class));
                break;
            case R.id.tv_statistics:
                ToastUtil.showToast(this, getResources().getString(R.string.unOpen));
                //                Utils.StartActivity(ManagementActivity.this, new Intent(ManagementActivity.this, DataStatisticsActivity.class));
                break;
        }
    }

    @Override
    public void showView(RewardManagerHttpEntity httpResult) {
        try {
            if (httpResult.code == Constant.HttpResult.SUCCEED) {
                mTvBalanceValue.setText(new DecimalFormat("#0.00").format(httpResult.totalMoney));
                mTvReward.setText(new DecimalFormat("#0.00").format(httpResult.toDayMoney));
                mTvRecords.setText("" + httpResult.stayAuditingCount);
            } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
                ToastUtil.showToast(this, "登录超时,请重新登录");
                startActivity(new Intent(this, LoginActivity.class));
            } else {
                ToastUtil.showToast(this, "获取数据失败");
            }
        } catch (Exception ex) {
            LogUtils.e(ex.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposables != null) {
            mDisposables.dispose();
            mDisposables.clear();
        }
    }
}
