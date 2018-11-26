package com.zhengdao.zqb.view.activity.welfarewechatshare;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.dalong.library.listener.OnItemSelectedListener;
import com.dalong.library.view.LoopRotarySwitchView;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;
import com.yatoooon.screenadaptation.ScreenAdapterTools;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.ShareHttpEntity;
import com.zhengdao.zqb.event.GetWelfareShareRewardEvent;
import com.zhengdao.zqb.event.UpDataUserInfoEvent;
import com.zhengdao.zqb.event.WelfareWechatShareEvent;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.FileUtils;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.ShareUtils;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.view.activity.login.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @Author lijiangop
 * @CreateTime 2018/9/5 0005 14:31
 */
public class WelfareWeChatShareActivity extends MVPBaseActivity<WelfareWeChatShareContract.View, WelfareWeChatSharePresenter> implements WelfareWeChatShareContract.View {

    private static final int REQUESTCODE = 001;
    @BindView(R.id.loopView)
    LoopRotarySwitchView mLoopView;

    @BindView(R.id.iv_code_1)
    ImageView      mIvCode1;
    @BindView(R.id.re_game_break)
    RelativeLayout mReGameBreak;
    @BindView(R.id.iv_code_2)
    ImageView      mIvCode2;
    @BindView(R.id.re_finish_survy)
    RelativeLayout mReFinishSurvy;
    @BindView(R.id.iv_code_3)
    ImageView      mIvCode3;
    @BindView(R.id.re_linghuaqian)
    RelativeLayout mReLinghuaqian;
    @BindView(R.id.iv_code_4)
    ImageView      mIvCode4;
    @BindView(R.id.re_youhuiquan)
    RelativeLayout mReYouhuiquan;
    @BindView(R.id.iv_code_5)
    ImageView      mIvCode5;
    @BindView(R.id.re_zhuanqianle)
    RelativeLayout mReZhuanqianle;
    @BindView(R.id.iv_code_6)
    ImageView      mIvCode6;
    @BindView(R.id.re_daily_earn)
    RelativeLayout mReDailyEarn;
    @BindView(R.id.iv_code_7)
    ImageView      mIvCode7;
    @BindView(R.id.re_shoutu)
    RelativeLayout mReShoutu;

    private Disposable mDisposable;
    private int mCurrentItem = 0;
    private IUiListener mUiListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welfare_wechat_share);
        ScreenAdapterTools.getInstance().loadView(getWindow().getDecorView());
        ButterKnife.bind(this);
        setTitle("福利领取");
        init();
        mUiListener = new IUiListener() {
            @Override
            public void onComplete(Object o) {
                ToastUtil.showToast(WelfareWeChatShareActivity.this, "分享成功");
            }

            @Override
            public void onError(UiError uiError) {
                ToastUtil.showToast(WelfareWeChatShareActivity.this, "分享失败");
                LogUtils.e("分享失败errorMessage:" + uiError.errorMessage + "errorDetail" + uiError.errorDetail);
            }

            @Override
            public void onCancel() {
                ToastUtil.showToast(WelfareWeChatShareActivity.this, "分享取消");
            }
        };
    }

    private void init() {
        mDisposable = RxBus.getDefault().toObservable(WelfareWechatShareEvent.class).subscribe(new Consumer<WelfareWechatShareEvent>() {
            @Override
            public void accept(WelfareWechatShareEvent welfareWechatShareEvent) throws Exception {
                mPresenter.getWelfareShareReward();
            }
        });
        mPresenter.getData();
        mLoopView.setR(300)//设置R的大小
                .setAutoRotation(false)//是否自动切换
                .setAutoScrollDirection(LoopRotarySwitchView.AutoScrollDirection.left)//切换方向
                .setAutoRotationTime(2000);//自动切换的时间  单位毫秒
        mLoopView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void selected(int item, View view) {
                mCurrentItem = item;
            }
        });
    }

    @OnClick({R.id.tv_share_wechat, R.id.tv_share_wechat_zone, R.id.tv_share_qq, R.id.tv_share_qq_zone})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_share_wechat:
                doShare(0);
                break;
            case R.id.tv_share_wechat_zone:
                doShare(1);
                break;
            case R.id.tv_share_qq:
                doShare(2);
                break;
            case R.id.tv_share_qq_zone:
                doShare(3);
                break;
        }
    }

    private void doShare(int type) {
        View view;
        switch (mCurrentItem) {
            case 0:
                view = mReDailyEarn;
                break;
            case 1:
                view = mReFinishSurvy;
                break;
            case 2:
                view = mReGameBreak;
                break;
            case 3:
                view = mReLinghuaqian;
                break;
            case 4:
                view = mReShoutu;
                break;
            case 5:
                view = mReYouhuiquan;
                break;
            case 6:
                view = mReZhuanqianle;
                break;
            default:
                view = mReDailyEarn;
                break;
        }
        Bitmap viewBitmap = createViewBitmap(view);

        Bundle params = new Bundle();
        //                params.putString(QQShare.SHARE_TO_QQ_TITLE, mNickName + "喊你一起赚钱吧!");// 标题
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, FileUtils.saveBitmap(viewBitmap));// 网络图片地址　　
        //                params.putString(QQShare.SHARE_TO_QQ_SUMMARY, getString(R.string.share_content));// 摘要
        //                params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, TextUtils.isEmpty(mStringShareUrl) ? "" : mStringShareUrl);// 内容地址

        switch (type) {
            case 0:
                ShareUtils.shareToWXWithImg(this, SendMessageToWX.Req.WXSceneSession, viewBitmap, Constant.WechatReq.WELFARESHARE_WECHAT);
                break;
            case 1:
                ShareUtils.shareToWXWithImg(this, SendMessageToWX.Req.WXSceneTimeline, viewBitmap, Constant.WechatReq.WELFARESHARE_WECHAT_CIRCLE);
                break;
            case 2:
                params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
                ShareUtils.shareToQQ(WelfareWeChatShareActivity.this, params, mUiListener);
                break;
            case 3:
                params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE);
                ShareUtils.shareToQZone(WelfareWeChatShareActivity.this, params, mUiListener);
                break;
        }
    }

    public Bitmap createViewBitmap(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }

    @Override
    public void onDataGet(ShareHttpEntity httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            Glide.with(this).load(httpResult.QRcode).error(R.drawable.wechat_qr_code).into(mIvCode1);
            Glide.with(this).load(httpResult.QRcode).error(R.drawable.wechat_qr_code).into(mIvCode2);
            Glide.with(this).load(httpResult.QRcode).error(R.drawable.wechat_qr_code).into(mIvCode3);
            Glide.with(this).load(httpResult.QRcode).error(R.drawable.wechat_qr_code).into(mIvCode4);
            Glide.with(this).load(httpResult.QRcode).error(R.drawable.wechat_qr_code).into(mIvCode5);
            Glide.with(this).load(httpResult.QRcode).error(R.drawable.wechat_qr_code).into(mIvCode6);
            Glide.with(this).load(httpResult.QRcode).error(R.drawable.wechat_qr_code).into(mIvCode7);
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录");
            startActivityForResult(new Intent(this, LoginActivity.class), REQUESTCODE);
        } else {
            ToastUtil.showToast(WelfareWeChatShareActivity.this, TextUtils.isEmpty(httpResult.msg) ? "获取数据失败" : httpResult.msg);
        }
    }

    @Override
    public void onGetWelfareShareReward(HttpResult httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            ToastUtil.showToast(this, TextUtils.isEmpty(httpResult.msg) ? "" : httpResult.msg);
            RxBus.getDefault().post(new GetWelfareShareRewardEvent());
            RxBus.getDefault().post(new UpDataUserInfoEvent());
            finish();
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
