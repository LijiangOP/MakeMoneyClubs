package com.zhengdao.zqb.view.activity.wanteddetail;


import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kennyc.view.MultiStateView;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.WantedDetailHttpEntity;
import com.zhengdao.zqb.event.UpDataUserInfoEvent;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.utils.Utils;
import com.zhengdao.zqb.view.activity.homegoodsdetail.HomeGoodsDetailActivity;
import com.zhengdao.zqb.view.activity.login.LoginActivity;
import com.zhengdao.zqb.view.activity.report.ReportActivity;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.iwgang.countdownview.CountdownView;

public class WantedDetailActivity extends MVPBaseActivity<WantedDetailContract.View, WantedDetailPresenter> implements WantedDetailContract.View, View.OnClickListener {

    private static final int UN_COMMITED = 0;
    private static final int COMMITED    = 1;
    private static final int UN_DONE     = 2;
    private static final int DONE        = 3;

    @BindView(R.id.tv_wanted_state)
    TextView       mTvWantedState;
    @BindView(R.id.iv_shop_icon)
    ImageView      mIvShopIcon;
    @BindView(R.id.tv_shop_name)
    TextView       mTvShopName;
    @BindView(R.id.iv_goods_icon)
    ImageView      mIvGoodsIcon;
    @BindView(R.id.tv_goods_title)
    TextView       mTvGoodsTitle;
    @BindView(R.id.tv_goods_price)
    TextView       mTvGoodsPrice;
    @BindView(R.id.tv_wanted_money)
    TextView       mTvWantedMoney;
    @BindView(R.id.tv_wanted_reb_packet)
    TextView       mTvWantedRebPacket;
    @BindView(R.id.tv_wanted_discounts)
    TextView       mTvWantedDiscounts;
    @BindView(R.id.tv_wanted_total)
    TextView       mTvWantedTotal;
    @BindView(R.id.tv_wanted_actual)
    TextView       mTvWantedActual;
    @BindView(R.id.ll_complain)
    LinearLayout   mLlComplain;
    @BindView(R.id.tv_integral)
    TextView       mTvIntegral;
    @BindView(R.id.ll_message)
    LinearLayout   mLlMessage;
    @BindView(R.id.ll_phone)
    LinearLayout   mLlPhone;
    @BindView(R.id.tv_order_number)
    TextView       mTvOrderNumber;
    @BindView(R.id.tv_order_copy)
    TextView       mTvOrderCopy;
    @BindView(R.id.tv_get_time)
    TextView       mTvGetTime;
    @BindView(R.id.tv_left_button)
    TextView       mTvLeftButton;
    @BindView(R.id.tv_right_button)
    TextView       mTvRightButton;
    @BindView(R.id.ll_bottom)
    LinearLayout   mLlBottom;
    @BindView(R.id.multiStateView)
    MultiStateView mMultiStateView;
    @BindView(R.id.tv_wanted_time1)
    TextView       mTvWantedTime1;
    @BindView(R.id.countDownView)
    CountdownView  mCountDownView;
    @BindView(R.id.tv_wanted_time2)
    TextView       mTvWantedTime2;
    @BindView(R.id.ll_wanted_time)
    LinearLayout   mLlWantedTime;
    @BindView(R.id.tv_keyword_1)
    TextView       mTvKeyword1;
    @BindView(R.id.tv_keyword_2)
    TextView       mTvKeyword2;
    @BindView(R.id.tv_keyword_3)
    TextView       mTvKeyword3;
    @BindView(R.id.ll_goods_part)
    LinearLayout   mLlGoodsPart;
    private long mCurrentTimeMillis = 0;
    private int    mTaskId;
    private int    mWantedId;
    private int    mCurrentState;
    private String mPhone;
    private String mOrderNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wanteddetail);
        ButterKnife.bind(this);
        setTitle("悬赏详情");
        init();
        initListener();
    }

    private void init() {
        mTaskId = getIntent().getIntExtra(Constant.Activity.Data, 0);
        mCurrentState = getIntent().getIntExtra(Constant.Activity.Data1, 0);
        mMultiStateView.getView(MultiStateView.VIEW_STATE_ERROR).findViewById(R.id.btn_error_reload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.getData(mTaskId);
            }
        });
        mPresenter.getData(mTaskId);
        switch (mCurrentState) {
            case UN_COMMITED:
                mTvLeftButton.setVisibility(View.VISIBLE);
                mTvRightButton.setVisibility(View.VISIBLE);
                mTvWantedState.setText("等待提交");
                mTvWantedTime1.setText("剩余");
                mTvWantedTime2.setText("后重新领取悬赏令");
                mTvLeftButton.setText("取消任务");
                mTvRightButton.setText("提交");
                break;
            case COMMITED:
                mTvLeftButton.setVisibility(View.VISIBLE);
                mTvWantedState.setText("已提交");
                mTvWantedTime1.setText("剩");
                mTvWantedTime2.setText("审核自动结束");
                mTvLeftButton.setText("提醒审核");
                mTvRightButton.setVisibility(View.GONE);
                break;
            case UN_DONE:
                mTvWantedState.setText("悬赏失败");
                mLlWantedTime.setVisibility(View.GONE);
                mLlBottom.setVisibility(View.GONE);
                break;
            case DONE:
                mTvWantedState.setText("悬赏成功");
                mLlWantedTime.setVisibility(View.GONE);
                mTvLeftButton.setVisibility(View.GONE);
                mTvRightButton.setVisibility(View.VISIBLE);
                mTvRightButton.setText("评价");
                mTvRightButton.setVisibility(View.GONE);// 暂时没有评价功能
                break;
            default:
                break;
        }
    }

    private void initListener() {
        mLlComplain.setOnClickListener(this);
        mLlMessage.setOnClickListener(this);
        mLlPhone.setOnClickListener(this);
        mTvOrderCopy.setOnClickListener(this);
        mTvLeftButton.setOnClickListener(this);
        mTvRightButton.setOnClickListener(this);
        mLlGoodsPart.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - mCurrentTimeMillis < 1000)
            return;
        mCurrentTimeMillis = System.currentTimeMillis();
        switch (v.getId()) {
            case R.id.ll_complain:
                Intent intent = new Intent(WantedDetailActivity.this, ReportActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_message:
                doSendMessage();
                break;
            case R.id.ll_phone:
                doCallPhone();
                break;
            case R.id.tv_order_copy:
                doCopy();
                break;
            case R.id.tv_left_button:
                doLeft();
                break;
            case R.id.tv_right_button:
                doRight();
                break;
            case R.id.ll_goods_part:
                gotoWantedDetail();
                break;
        }
    }

    private void doSendMessage() {
        if (TextUtils.isEmpty(mPhone))
            return;
        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + mPhone));
            startActivity(intent);
        } catch (Exception ex) {
            LogUtils.e(ex.getMessage());
        }
    }

    private void doCallPhone() {
        if (TextUtils.isEmpty(mPhone))
            return;
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mPhone));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception ex) {
            LogUtils.e(ex.getMessage());
        }
    }

    private void doCopy() {
        if (TextUtils.isEmpty(mOrderNo))
            return;
        try {
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            cm.setText(mOrderNo);
            ToastUtil.showToast(this, "复制成功！");
        } catch (Exception ex) {
            LogUtils.e(ex.getMessage());
        }
    }

    private void doLeft() {
        switch (mCurrentState) {
            case UN_COMMITED:
                mPresenter.cancleMission(mTaskId);
                break;
            case COMMITED:
                mPresenter.remindCheck(mWantedId);
                break;
            default:
                break;
        }
    }

    private void doRight() {
        switch (mCurrentState) {
            case UN_COMMITED:
                mPresenter.commite(mWantedId);
                break;
            case DONE:
                mPresenter.evaluate(mTaskId);
                break;
            default:
                break;
        }
    }


    private void gotoWantedDetail() {
        //        switch (mCurrentState) {
        //            case DONE:
        Intent intent = new Intent(this, HomeGoodsDetailActivity.class);
        intent.putExtra(Constant.Activity.Data, mWantedId);
        Utils.StartActivity(this, intent);
        //                break;
        //            default:
        //                break;
        //        }
    }

    @Override
    public void showErrorMessage(String msg) {
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
        super.showErrorMessage(msg);
    }

    @Override
    public void onGetDataResult(WantedDetailHttpEntity httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
            WantedDetailHttpEntity.Task task = httpResult.task;
            Glide.with(this).load(task.reward.user.avatar).error(R.drawable.net_less_36).into(mIvShopIcon);
            mTvShopName.setText(TextUtils.isEmpty(task.reward.user.nickName) ? "" : task.reward.user.nickName);
            Glide.with(this).load(task.reward.picture).error(R.drawable.net_less_140).into(mIvGoodsIcon);
            mTvGoodsTitle.setText(TextUtils.isEmpty(task.reward.title) ? "" : task.reward.title);
            mTvGoodsPrice.setText(new DecimalFormat("#0.00").format(task.money));
            //关键词
            mTvKeyword1.setVisibility(View.GONE);
            mTvKeyword2.setVisibility(View.GONE);
            mTvKeyword3.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(task.reward.keyword)) {
                String[] split = task.reward.keyword.split(",");
                switch (split.length) {
                    case 1:
                        mTvKeyword1.setVisibility(View.VISIBLE);
                        mTvKeyword1.setText(split[0]);
                        break;
                    case 2:
                        mTvKeyword1.setVisibility(View.VISIBLE);
                        mTvKeyword1.setText(split[0]);
                        mTvKeyword2.setVisibility(View.VISIBLE);
                        mTvKeyword2.setText(split[1]);
                        break;
                    case 3:
                        mTvKeyword1.setVisibility(View.VISIBLE);
                        mTvKeyword1.setText(split[0]);
                        mTvKeyword2.setVisibility(View.VISIBLE);
                        mTvKeyword2.setText(split[1]);
                        mTvKeyword3.setVisibility(View.VISIBLE);
                        mTvKeyword3.setText(split[2]);
                        break;
                }
            }
            mTvWantedMoney.setText("¥" + new DecimalFormat("#0.00").format(task.money));
            Double quota;
            if (task.coupons == null)
                quota = 0.0;
            else
                quota = task.coupons.quota;
            mTvWantedDiscounts.setText("+" + "¥" + new DecimalFormat("#0.00").format(quota));
            mTvWantedTotal.setText("¥" + new DecimalFormat("#0.00").format((task.money + quota)));
            mTvWantedActual.setText("¥" + new DecimalFormat("#0.00").format((task.money + quota)));
            mTvOrderNumber.setText("订单编号 : " + task.reward.orderNo);
            mTvGetTime.setText("领取时间 :" + task.createtime);
            mWantedId = task.reward.id;
            mOrderNo = task.reward.orderNo;
            mPhone = task.reward.user.phone;
            mCountDownView.start(task.deadlineTime);

        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录");
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            ToastUtil.showToast(this, TextUtils.isEmpty(httpResult.msg) ? "获取数据失败" : httpResult.msg);
        }
    }

    @Override
    public void onRemindCheckResult(HttpResult httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            ToastUtil.showToast(this, "操作成功");
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录");
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            ToastUtil.showToast(this, TextUtils.isEmpty(httpResult.msg) ? "操作失败" : httpResult.msg);
        }
    }

    @Override
    public void onCancleMissionResult(HttpResult httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            ToastUtil.showToast(this, "操作成功");
            RxBus.getDefault().post(new UpDataUserInfoEvent());
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录");
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            ToastUtil.showToast(this, TextUtils.isEmpty(httpResult.msg) ? "操作失败" : httpResult.msg);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
