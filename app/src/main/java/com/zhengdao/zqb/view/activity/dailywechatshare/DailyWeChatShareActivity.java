package com.zhengdao.zqb.view.activity.dailywechatshare;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.ShareHttpEntity;
import com.zhengdao.zqb.event.DailyWechatShareEvent;
import com.zhengdao.zqb.event.NewHandUpDataEvent;
import com.zhengdao.zqb.event.TicketActivateShareEvent;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.ShareUtils;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.view.activity.login.LoginActivity;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


public class DailyWeChatShareActivity extends MVPBaseActivity<DailyWeChatShareContract.View, DailyWeChatSharePresenter> implements DailyWeChatShareContract.View {

    private static final int REQUESTCODE = 007;
    @BindView(R.id.iv_part1)
    ImageView      mIvPart1;
    @BindView(R.id.tv_part2)
    TextView       mTvPart2;
    @BindView(R.id.iv_part3)
    ImageView      mIvPart3;
    @BindView(R.id.iv_code)
    ImageView      mIvCode;
    @BindView(R.id.tv_share)
    TextView       mTvShare;
    @BindView(R.id.re_bottom)
    RelativeLayout mReBottom;
    @BindView(R.id.tv_hint)
    TextView       mTvHint;

    private Disposable mDisposable;
    private int        mReBottomHeight;
    private int        mType;
    private int        mCid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_wechat_share);
        ButterKnife.bind(this);
        setTitle("分享APP");
        init();
    }

    private void init() {
        mType = getIntent().getIntExtra(Constant.Activity.Type, 0);
        if (mType == 1) {
            String useableSum = getIntent().getStringExtra(Constant.Activity.Data1);
            mCid = getIntent().getIntExtra(Constant.Activity.Data2, 0);
            int type = getIntent().getIntExtra(Constant.Activity.Data3, 0);
            SpannableString spannableString;
            switch (type) {
                case 1:
                    spannableString = new SpannableString("分享即可领取\n" + useableSum + "M流量");
                    spannableString.setSpan(new RelativeSizeSpan(2f), 6, spannableString.length() - 3, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    spannableString.setSpan(new StyleSpan(Typeface.BOLD), 6, spannableString.length() - 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    break;
                case 2:
                    spannableString = new SpannableString("分享即可领取\n" + useableSum + "元话费");
                    spannableString.setSpan(new RelativeSizeSpan(2f), 6, spannableString.length() - 3, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    spannableString.setSpan(new StyleSpan(Typeface.BOLD), 6, spannableString.length() - 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    break;
                case 3:
                    spannableString = new SpannableString("分享即可领取\n" + useableSum + "元现金");
                    spannableString.setSpan(new RelativeSizeSpan(2f), 6, spannableString.length() - 3, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    spannableString.setSpan(new StyleSpan(Typeface.BOLD), 6, spannableString.length() - 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    break;
                default:
                    spannableString = new SpannableString("");
                    break;
            }
            mTvPart2.setText(spannableString);
            mTvHint.setText(getString(R.string.share_hint_1));
        } else {
            Double useableSum = getIntent().getDoubleExtra(Constant.Activity.Data, 0);
            String head = "我在" + getString(R.string.app_name) + "又赚了\n";
            SpannableString spannableString = new SpannableString(head + new DecimalFormat("#0.00").format(useableSum));
            spannableString.setSpan(new RelativeSizeSpan(2f), head.length(), spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new StyleSpan(Typeface.BOLD), head.length(), spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            mTvPart2.setText(spannableString);
            mTvHint.setText(getString(R.string.share_hint_2));
        }
        mTvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doShare();
            }
        });
        mDisposable = RxBus.getDefault().toObservable(DailyWechatShareEvent.class).subscribe(new Consumer<DailyWechatShareEvent>() {
            @Override
            public void accept(DailyWechatShareEvent dailyWechatShareEvent) throws Exception {
                if (mType == 1)
                    mPresenter.doActivateTicket(mCid);
                else
                    mPresenter.getDailyShareReward();
            }
        });
        mReBottom.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mReBottomHeight = mReBottom.getHeight();
                return true;
            }
        });
        mPresenter.getData();
    }

    private void doShare() {
        View view = getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();

        // 获取状态栏高度
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        //获取导航栏的高度
        int i = getResources().getDimensionPixelOffset(R.dimen.title_bar_height);

        // 获取屏幕长和高
        int width = getWindowManager().getDefaultDisplay().getWidth();
        int height = getWindowManager().getDefaultDisplay().getHeight();

        // 去掉标题栏
        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight + i, width, height
                - statusBarHeight - i - mReBottomHeight);
        view.destroyDrawingCache();
        ShareUtils.shareToWXWithImg(this, SendMessageToWX.Req.WXSceneTimeline, b, Constant.WechatReq.DailyShare);
    }

    @Override
    public void onDataGet(ShareHttpEntity httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            Glide.with(this).load(httpResult.QRcode).error(R.drawable.wechat_qr_code).into(mIvCode);
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录");
            startActivityForResult(new Intent(this, LoginActivity.class), REQUESTCODE);
        } else {
            ToastUtil.showToast(DailyWeChatShareActivity.this, TextUtils.isEmpty(httpResult.msg) ? "获取数据失败" : httpResult.msg);
        }
    }

    @Override
    public void onGetDailyShareReward(HttpResult httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            ToastUtil.showToast(this, TextUtils.isEmpty(httpResult.msg) ? "" : httpResult.msg);
            RxBus.getDefault().post(new NewHandUpDataEvent());
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录");
            startActivityForResult(new Intent(this, LoginActivity.class), REQUESTCODE);
        } else {
            LogUtils.e(TextUtils.isEmpty(httpResult.msg) ? "" : httpResult.msg);
        }
    }

    @Override
    public void onActivateTicketResult(HttpResult httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            ToastUtil.showToast(this, TextUtils.isEmpty(httpResult.msg) ? "" : httpResult.msg);
            RxBus.getDefault().post(new TicketActivateShareEvent());
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录");
            startActivityForResult(new Intent(this, LoginActivity.class), REQUESTCODE);
        } else {
            LogUtils.e(TextUtils.isEmpty(httpResult.msg) ? "" : httpResult.msg);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUESTCODE:
                if (data != null) {
                    boolean booleanExtra = data.getBooleanExtra(Constant.Activity.Data, false);
                    if (booleanExtra)
                        mPresenter.getData();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null)
            mDisposable.dispose();
    }

}
