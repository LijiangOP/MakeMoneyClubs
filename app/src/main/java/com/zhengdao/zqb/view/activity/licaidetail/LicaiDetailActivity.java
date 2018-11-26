package com.zhengdao.zqb.view.activity.licaidetail;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fynn.fluidlayout.FluidLayout;
import com.kennyc.view.MultiStateView;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.customview.WantedShareDialog;
import com.zhengdao.zqb.entity.HttpLiCaiDetailEntity;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.utils.ViewUtils;
import com.zhengdao.zqb.view.activity.calculator.CalculatorActivity;
import com.zhengdao.zqb.view.activity.login.LoginActivity;
import com.zhengdao.zqb.view.activity.webview.WebViewActivity;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LicaiDetailActivity extends MVPBaseActivity<LicaiDetailContract.View, LicaiDetailPresenter> implements LicaiDetailContract.View, View.OnClickListener {

    private static final int ACTION_LOGIN = 001;
    @BindView(R.id.iv_title_back)
    ImageView      mIvTitleBack;
    @BindView(R.id.tv_title_title)
    TextView       mTvTitleTitle;
    @BindView(R.id.iv_title_more)
    ImageView      mIvTitleMore;
    @BindView(R.id.re_title_bar)
    RelativeLayout mReTitleBar;
    @BindView(R.id.tv_nianhualv)
    TextView       mTvNianhualv;
    @BindView(R.id.tv_total_income)
    TextView       mTvTotalIncome;
    @BindView(R.id.fluid_layout_keyWord)
    FluidLayout    mFluidLayoutKeyWord;
    @BindView(R.id.tv_desc)
    TextView       mTvDesc;
    @BindView(R.id.tv_rebate)
    TextView       mTvRebate;
    @BindView(R.id.tv_interest)
    TextView       mTvInterest;
    @BindView(R.id.tv_red_packet)
    TextView       mTvRedPacket;
    @BindView(R.id.tv_platform_desc)
    TextView       mTvPlatformDesc;
    @BindView(R.id.tabLayout)
    TabLayout      mTabLayout;
    @BindView(R.id.tv_detail)
    TextView       mTvDetail;
    @BindView(R.id.tv_join)
    TextView       mTvJoin;
    @BindView(R.id.iv_calculate)
    ImageView      mIvCalculate;
    @BindView(R.id.re_bottom)
    LinearLayout   mReBottom;
    @BindView(R.id.multiStateView)
    MultiStateView mMultiStateView;

    private int    mGoodsId;
    private String mStringUrl;
    private long mCurrentTimeMillis = 0;
    private int  mSkipType          = 1;
    private String            mStringMianze;
    private String            mStringActivity;
    private String            mStringSpecialRemind;
    private String            mStringRiskHint;
    private PopupWindow       mPopupWindow;
    private String            mStringShare;
    private WantedShareDialog mShareDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_licai_detail);
        ButterKnife.bind(this);
        init();
        initClickListener();
    }

    private void init() {
        mGoodsId = getIntent().getIntExtra(Constant.Activity.Data, 0);
        mMultiStateView.getView(MultiStateView.VIEW_STATE_ERROR).findViewById(R.id.btn_error_reload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.initData(mGoodsId);
            }
        });
        mPresenter.initData(mGoodsId);
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
        mTabLayout.getTabAt(0).setText("活动说明");
        mTabLayout.getTabAt(1).setText("特别提醒");
        mTabLayout.getTabAt(2).setText("风险提示");
        mTabLayout.getTabAt(3).setText("免责声明");
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch (position) {
                    case 0:
                        mTvDetail.setText(Html.fromHtml(mStringActivity));
                        break;
                    case 1:
                        mTvDetail.setText(Html.fromHtml(mStringSpecialRemind));
                        break;
                    case 2:
                        mTvDetail.setText(Html.fromHtml(mStringRiskHint));
                        break;
                    case 3:
                        mTvDetail.setText(Html.fromHtml(mStringMianze));
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        ViewUtils.reflex(this, mTabLayout, 4, 10);
        mTabLayout.getTabAt(0).select();
    }

    private void initClickListener() {
        mTvTitleTitle.setText("产品详情");
        mTvJoin.setOnClickListener(this);
        mIvTitleBack.setOnClickListener(this);
        mIvTitleMore.setOnClickListener(this);
        mIvCalculate.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - mCurrentTimeMillis < 500)
            return;
        mCurrentTimeMillis = System.currentTimeMillis();
        switch (v.getId()) {
            case R.id.tv_join:
                if (TextUtils.isEmpty(mStringUrl)) {
                    ToastUtil.showToast(this, "链接不存在");
                    return;
                }
                if (mSkipType == 2) {
                    Uri uri = Uri.parse(mStringUrl);
                    Intent four = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(four);
                } else {
                    Intent intent = new Intent(this, WebViewActivity.class);
                    intent.putExtra(Constant.WebView.TITLE, "详情");
                    intent.putExtra(Constant.WebView.URL, mStringUrl);
                    startActivity(intent);
                }
                break;
            case R.id.iv_title_back:
                this.finish();
                break;
            case R.id.iv_title_more:
                doShowMore();
                break;
            case R.id.tv_share:
                if (mPopupWindow != null)
                    mPopupWindow.dismiss();
                doShare();
                break;
            case R.id.iv_calculate:
                startActivity(new Intent(LicaiDetailActivity.this, CalculatorActivity.class));
                break;
        }
    }

    private void doShare() {
        if (!TextUtils.isEmpty(mStringShare)) {
            mShareDialog = new WantedShareDialog(this);
            mShareDialog.initView(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mShareDialog != null)
                        mShareDialog.dismiss();
                    ToastUtil.showToast(LicaiDetailActivity.this, "复制成功!");
                }
            });
            SpannableString spannableString = new SpannableString(mStringShare);
            mShareDialog.setMessage(spannableString);
            mShareDialog.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ToastUtil.showToast(LicaiDetailActivity.this, "复制成功,快分享给好友吧!");
                    return true;
                }
            });
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData mClipData = ClipData.newPlainText("Label", spannableString);
            cm.setPrimaryClip(mClipData);
            mShareDialog.show();
        }
    }

    private void doShowMore() {
        if (TextUtils.isEmpty(mStringShare))
            return;
        try {
            if (SettingUtils.isLogin(this)) {
                View contentView = LayoutInflater.from(this).inflate(R.layout.popup_wanted_detail_more, null);
                TextView ll_home = contentView.findViewById(R.id.tv_home);
                TextView ll_help = contentView.findViewById(R.id.tv_help);
                TextView ll_report = contentView.findViewById(R.id.tv_report);
                TextView ll_collect = contentView.findViewById(R.id.tv_collect);
                TextView ll_share = contentView.findViewById(R.id.tv_share);
                ll_home.setVisibility(View.GONE);
                ll_help.setVisibility(View.GONE);
                ll_report.setVisibility(View.GONE);
                ll_collect.setVisibility(View.GONE);
                ll_share.setOnClickListener(this);
                mPopupWindow = new PopupWindow(contentView,
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                mPopupWindow.setBackgroundDrawable(new ColorDrawable());
                if (Build.VERSION.SDK_INT > 18)
                    mPopupWindow.showAsDropDown(mReTitleBar, 0, 0, Gravity.RIGHT);
                else
                    mPopupWindow.showAsDropDown(mReTitleBar, 0, 0);
            } else {
                startActivityForResult(new Intent(this, LoginActivity.class), ACTION_LOGIN);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void showResult(HttpLiCaiDetailEntity result) {
        if (result.code == Constant.HttpResult.SUCCEED) {
            try {
                if (result.wangzhuan == null) {
                    ToastUtil.showToast(this, "数据请求出错");
                    mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
                    return;
                }
                mSkipType = result.wangzhuan.skipType;
                mStringShare = result.share;
                mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);

                String value = "" + new DecimalFormat("#0.00").format(result.wangzhuan.expectedAnnualized) + "%";
                SpannableString spannableString = new SpannableString(value);
                RelativeSizeSpan sizeSpan = new RelativeSizeSpan(0.4f);
                if (value.contains(".")) {
                    String[] split = value.split("\\.");
                    spannableString.setSpan(sizeSpan, split[0].length(), spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                }
                mTvNianhualv.setText(spannableString);

                int totalIncome = new Double(result.wangzhuan.incomeTotal == null ? 0 : result.wangzhuan.incomeTotal).intValue();
                SpannableString spTotalIncome = new SpannableString(totalIncome + "元");
                int start = spTotalIncome.length() - 1;
                spTotalIncome.setSpan(sizeSpan, start < 0 ? 0 : start, spTotalIncome.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                mTvTotalIncome.setText(spTotalIncome);

                mFluidLayoutKeyWord.removeAllViews();
                String mark = result.wangzhuan.mark;
                if (!TextUtils.isEmpty(mark) && mark.contains("/")) {
                    String[] split = mark.split("/");
                    switch (split.length) {
                        case 1:
                            addKeyword(split[0]);
                            break;
                        case 2:
                            addKeyword(split[1]);
                            break;
                        case 3:
                            addKeyword(split[2]);
                            break;
                    }
                }
                String moneyUnit = result.wangzhuan.termType == 1 ? "天" : "月";
                mTvDesc.setText("产品期限" + result.wangzhuan.term + moneyUnit + ",金额" + new DecimalFormat("#0.00").format(result.wangzhuan.startingAmount) + "元");
                mTvRebate.setText("奖励" + new Double(result.wangzhuan.rebate).intValue() + "元");
                mTvInterest.setText("利息" + new Double(result.wangzhuan.interest).intValue() + "元");
                mTvRedPacket.setText("红包" + new Double(result.wangzhuan.red).intValue() + "元");
                String platformDesc = TextUtils.isEmpty(result.wangzhuan.abstractStr) ? "" : result.wangzhuan.abstractStr;
                mTvPlatformDesc.setText(Html.fromHtml(platformDesc));

                mStringUrl = TextUtils.isEmpty(result.wangzhuan.url) ? "" : result.wangzhuan.url;
                mStringActivity = TextUtils.isEmpty(result.wangzhuan.activity) ? "" : result.wangzhuan.activity;
                mStringSpecialRemind = TextUtils.isEmpty(result.wangzhuan.reminder) ? "" : result.wangzhuan.reminder;
                mStringRiskHint = TextUtils.isEmpty(result.wangzhuan.riskWarning) ? "" : result.wangzhuan.riskWarning;
                mStringMianze = getString(R.string.licai_detail_statement1) + getString(R.string.app_name) + getString(R.string.licai_detail_statement2);

                mTvDetail.setText(Html.fromHtml(mStringActivity));
            } catch (Exception ex) {
                LogUtils.e(ex.getMessage());
            }
        } else if (result.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(LicaiDetailActivity.this, TextUtils.isEmpty(result.msg) ? "" : result.msg);
            startActivityForResult(new Intent(this, LoginActivity.class), ACTION_LOGIN);
        } else {
            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
            ToastUtil.showToast(LicaiDetailActivity.this, TextUtils.isEmpty(result.msg) ? "" : result.msg);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void addKeyword(String keyword) {
        FluidLayout.LayoutParams params = new FluidLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 10, 25, 0);
        TextView textView = new TextView(LicaiDetailActivity.this);
        textView.setText(keyword);
        textView.setTextSize(12);
        textView.setTextColor(getResources().getColor(R.color.color_6f717f));
        textView.setBackground(getResources().getDrawable(R.drawable.shape3));
        textView.setPadding(10, 3, 10, 3);
        mFluidLayoutKeyWord.addView(textView, params);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_LOGIN) {
            if (data != null) {
                boolean booleanExtra = data.getBooleanExtra(Constant.Activity.Data, false);
                if (booleanExtra && SettingUtils.isLogin(this))
                    mPresenter.initData(mGoodsId);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null)
            mPresenter.unsubcrible();
    }
}
