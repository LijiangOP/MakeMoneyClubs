package com.zhengdao.zqb.view.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anzhi.sdk.ad.main.AzNativeExpressView;
import com.anzhi.sdk.ad.manage.AnzhiNativeAdCallBack;
import com.anzhi.sdk.ad.manage.NativeExpressViewData;
import com.baidu.mobads.AdView;
import com.baidu.mobads.AdViewListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTBannerAd;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.comm.constants.AdPatternType;
import com.qq.e.comm.util.AdError;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.AdvertEntity;
import com.zhengdao.zqb.entity.JinChengAdvEntity;
import com.zhengdao.zqb.entity.JinChengAdvHttpResult;
import com.zhengdao.zqb.toutiao.TTAdManagerHolder;
import com.zhengdao.zqb.utils.AdvertisementUtils;
import com.zhengdao.zqb.utils.DensityUtil;
import com.zhengdao.zqb.utils.DownloadUtils;
import com.zhengdao.zqb.utils.JinChengUtils;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.ToastUtil;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.zhengdao.zqb.application.ClientAppLike.AppType;
import static com.zhengdao.zqb.utils.AdvertisementUtils.TencentAdv.getAdInfo;

/**
 * @Author lijiangop
 * @CreateTime 2018/10/17 0017 16:56
 */
public class AdvertisementListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private CallBack           mCallBack;
    private Activity           mContext;
    private List<AdvertEntity> mData;
    private ImageView          mImageView;

    public interface CallBack {
        void onAdvClick(int position);

        void onAdvInstall(String packageName);
    }

    public AdvertisementListAdapter(Activity context, List<AdvertEntity> data, CallBack callBack) {
        this.mCallBack = callBack;
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(View.inflate(mContext, R.layout.item_container, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            AdvertEntity entity = mData.get(position);
            switch (entity.getType()) {
                case 1://百度广告;腾讯广告
                    initBaiDuTencentAdv((ViewHolder) holder, entity.getAdvId(), position);
                    break;
                case 2://安智广告
                    initANZHIAdv((ViewHolder) holder, 4, position);
                    break;
                case 3://后台广告
                    initMineAdv((ViewHolder) holder, entity.getAdvId(), position);
                    break;
                case 4://头条广告
                    initTouTiaoAdv((ViewHolder) holder, entity.getAdvId(), position);
                    break;
                case 5://推啊广告
                    initTuiAAdv((ViewHolder) holder, entity.getAdvId(), position);
                    break;
                case 6://金橙广告
                    initJinChengAdv((ViewHolder) holder, entity.getAdvId(), position);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initMineAdv(final ViewHolder holder, String advId, final int position) {
        if (!TextUtils.isEmpty(advId)) {
            if (mImageView == null)
                mImageView = new ImageView(mContext);
            final int[] screenSize = DensityUtil.getScreenSize(mContext);
            Glide.with(mContext).load(advId).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    if (resource != null && screenSize[0] != 0) {
                        mImageView.setImageBitmap(resource);
                        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT,
                                FrameLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams.width = screenSize[0];
                        layoutParams.height = screenSize[0] / 5;
                        mImageView.setLayoutParams(layoutParams);
                        mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        if (holder.mContainer != null)
                            holder.mContainer.addView(mImageView);
                    }
                }
            });
            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallBack != null)
                        mCallBack.onAdvClick(position);
                }
            });
        }
    }

    private void initBaiDuTencentAdv(final ViewHolder holder, String advId, final int position) {
        try {
            if (AppType == Constant.App.Zqb) {
                AdView baiDuBanner = AdvertisementUtils.BaiDuAdv.getBaiDuBanner(mContext, advId, new AdViewListener() {
                    @Override
                    public void onAdReady(AdView adView) {
                        Log.w("BAIDUA", "onAdReady");
                    }

                    @Override
                    public void onAdShow(JSONObject jsonObject) {
                        Log.w("BAIDUA", "onAdShow");
                    }

                    @Override
                    public void onAdClick(JSONObject jsonObject) {
                        Log.w("BAIDUA", "onAdClick" + jsonObject.toString());
                        if (mCallBack != null)
                            mCallBack.onAdvClick(position);
                    }

                    @Override
                    public void onAdFailed(String s) {
                        Log.w("BAIDUA", "onAdFailed");
                    }

                    @Override
                    public void onAdSwitch() {
                        Log.w("BAIDUA", "onAdSwitch");
                    }

                    @Override
                    public void onAdClose(JSONObject jsonObject) {
                        Log.w("BAIDUA", "onAdClose" + jsonObject.toString());
                    }
                });
                if (baiDuBanner == null)
                    return;
                DisplayMetrics dm = new DisplayMetrics();
                ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
                int winW = dm.widthPixels;
                int winH = dm.heightPixels;
                int width = Math.min(winW, winH);
                int height = width / 4;
                RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(width, height);
                rllp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                if (holder.mContainer != null) {
                    holder.mContainer.addView(baiDuBanner, rllp);
                }
            } else {
                AdvertisementUtils.TencentAdv.getTencentNativeAdv1(mContext, advId, new NativeExpressAD.NativeExpressADListener() {

                    @Override
                    public void onNoAD(AdError adError) {
                        Log.i("NativeExpressAD", String.format("onNoAD, error code: %d, error msg: %s", adError.getErrorCode(), adError.getErrorMsg()));
                    }

                    @Override
                    public void onADLoaded(List<NativeExpressADView> list) {
                        Log.i("NativeExpressAD", "onADLoaded Success");
                        if (list != null && list.size() > 0) {
                            NativeExpressADView nativeExpressADView = list.get(0);
                            if (nativeExpressADView.getBoundData().getAdPatternType() == AdPatternType.NATIVE_VIDEO) {
                                Log.i("NativeExpressAD", "type=AdPatternType.NATIVE_VIDEO");
                            }
                            Log.i("NativeExpressAD", "nativeExpressADView=" + getAdInfo(nativeExpressADView));
                            if (holder.mContainer != null) {
                                holder.mContainer.addView(nativeExpressADView);
                            }
                            nativeExpressADView.render();
                        }
                    }

                    @Override
                    public void onRenderFail(NativeExpressADView nativeExpressADView) {
                        Log.i("NativeExpressAD", "onRenderFail: " + nativeExpressADView.toString());
                    }

                    @Override
                    public void onRenderSuccess(NativeExpressADView nativeExpressADView) {
                        Log.i("NativeExpressAD", "onRenderSuccess: " + nativeExpressADView.toString() + ", adInfo: " + getAdInfo(nativeExpressADView));
                    }

                    @Override
                    public void onADExposure(NativeExpressADView nativeExpressADView) {
                        Log.i("NativeExpressAD", "onADExposure: " + nativeExpressADView.toString());
                    }

                    @Override
                    public void onADClicked(NativeExpressADView nativeExpressADView) {
                        Log.i("NativeExpressAD", "onADClicked: " + nativeExpressADView.toString());
                        if (mCallBack != null)
                            mCallBack.onAdvClick(position);
                    }

                    @Override
                    public void onADClosed(NativeExpressADView nativeExpressADView) {
                        Log.i("NativeExpressAD", "onADClosed: " + nativeExpressADView.toString());
                    }

                    @Override
                    public void onADLeftApplication(NativeExpressADView nativeExpressADView) {
                        Log.i("NativeExpressAD", "onADLeftApplication: " + nativeExpressADView.toString());
                    }

                    @Override
                    public void onADOpenOverlay(NativeExpressADView nativeExpressADView) {
                        Log.i("NativeExpressAD", "onADOpenOverlay: " + nativeExpressADView.toString());
                    }

                    @Override
                    public void onADCloseOverlay(NativeExpressADView nativeExpressADView) {
                        Log.i("NativeExpressAD", "onADCloseOverlay: " + nativeExpressADView.toString());
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initANZHIAdv(final ViewHolder holder, int count, final int position) {
        AzNativeExpressView advView = new AzNativeExpressView(mContext, "9f346d3926df57f95e7f8c2c7a37dc8a", "1374", new AnzhiNativeAdCallBack() {
            @Override
            public void onReceiveAd(NativeExpressViewData nativeExpressViewData) {
                LogUtils.i("---安智渲染⼴告成功---");
            }

            @Override
            public void onAdFail(String s) {
                LogUtils.e("---安智加载⼴告失败---" + s);
                if (holder != null)
                    holder.mContainer.setVisibility(View.GONE);
            }

            @Override
            public void onRenderFail(NativeExpressViewData nativeExpressViewData) {
                LogUtils.e("---安智渲染⼴告失败---");
            }

            @Override
            public void onCloseAd(NativeExpressViewData nativeExpressViewData) {
                LogUtils.e("---安智广告被关闭---");
            }

            @Override
            public void onAdClik(NativeExpressViewData nativeExpressViewData) {
                LogUtils.e("---安智广告被点击---");
                if (mCallBack != null)
                    mCallBack.onAdvClick(position);
            }

            @Override
            public void onAdExposure(NativeExpressViewData nativeExpressViewData) {
                LogUtils.e("---安智广告展示---");

            }

            @Override
            public void onADLoaded(List<NativeExpressViewData> list) {
                LogUtils.i("---安智广告加载成功---");
                if (list != null && list.size() > 0) {
                    NativeExpressViewData nativeExpressViewData = list.get(0);
                    if (holder.mContainer != null) {
                        nativeExpressViewData.bindView(holder.mContainer);
                    }
                }
            }
        }, count);
        int[] screenSize = DensityUtil.getScreenSize(mContext);
        advView.setSize(screenSize[0], 300);
        advView.loadAd();
    }

    private boolean mHasShowDownloadActive = false;
    private TTAdNative mTTAdNative;

    private void initTouTiaoAdv(final ViewHolder holder, String advId, int position) {
        mTTAdNative = TTAdManagerHolder.getInstance(mContext).createAdNative(mContext);
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(advId)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(600, 257)
                .build();
        mTTAdNative.loadBannerAd(adSlot, new TTAdNative.BannerAdListener() {

            @Override
            public void onError(int code, String message) {

            }

            @Override
            public void onBannerAdLoad(final TTBannerAd ad) {
                if (ad == null) {
                    return;
                }
                View bannerView = ad.getBannerView();
                if (bannerView == null) {
                    return;
                }
                //设置轮播的时间间隔  间隔在30s到120秒之间的值，不设置默认不轮播
                ad.setSlideIntervalTime(30 * 1000);
                if (holder.mContainer != null) {
                    holder.mContainer.addView(bannerView);
                }
                ad.setBannerInteractionListener(new TTBannerAd.AdInteractionListener() {
                    @Override
                    public void onAdClicked(View view, int type) {
                        LogUtils.i("---头条广告被点击---");
                    }

                    @Override
                    public void onAdShow(View view, int type) {
                        LogUtils.i("---头条广告展示---");
                    }
                });
                ad.setDownloadListener(new TTAppDownloadListener() {
                    @Override
                    public void onIdle() {
                        ToastUtil.showToast(mContext, "点击图片开始下载", Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                        if (!mHasShowDownloadActive) {
                            mHasShowDownloadActive = true;
                            ToastUtil.showToast(mContext, "下载中，点击图片暂停", Toast.LENGTH_LONG);
                        }
                    }

                    @Override
                    public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                        ToastUtil.showToast(mContext, "下载暂停，点击图片继续", Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                        ToastUtil.showToast(mContext, "下载失败，点击图片重新下载", Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onInstalled(String fileName, String appName) {
                        ToastUtil.showToast(mContext, "安装完成，点击图片打开", Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                        ToastUtil.showToast(mContext, "点击图片安装", Toast.LENGTH_LONG);
                    }
                });
                //在banner中显示网盟提供的dislike icon
                ad.setShowDislikeIcon(new TTAdDislike.DislikeInteractionCallback() {
                    @Override
                    public void onSelected(int position, String value) {
                        ToastUtil.showToast(mContext, "点击 " + value);
                        if (holder.mContainer != null) {
                            holder.mContainer.removeAllViews();
                        }
                    }

                    @Override
                    public void onCancel() {
                        ToastUtil.showToast(mContext, "点击取消 ");
                    }
                });

            }
        });
    }

    //推啊广告
    private void initTuiAAdv(ViewHolder holder, String advId, int position) {

    }

    //金橙广告
    private void initJinChengAdv(final ViewHolder holder, String advId, int position) {
        try {
            JinChengUtils.getAdv(mContext, new JinChengUtils.JinChengApiAdvListener() {

                private String mPack;
                private ArrayList<String> mItal;
                private ArrayList<String> mDcp;
                private ArrayList<String> mStd;
                private ArrayList<String> mEc;
                private int mDeeplinktype;
                private String mClk;

                @SuppressLint("ClickableViewAccessibility")
                @Override
                public void onAdvGet(JinChengAdvHttpResult result) {
                    if (result != null) {
                        ArrayList<JinChengAdvEntity> ad = result.getAd();
                        if (ad.get(0) != null) {
                            holder.mReOnePic.setVisibility(View.VISIBLE);
                            Glide.with(mContext).load(ad.get(0).getAdl()).error(R.drawable.net_less_140).into(holder.mIcon);
                            holder.mTvTitle.setText(TextUtils.isEmpty(ad.get(0).getAti()) ? "" : ad.get(0).getAti());
                            holder.mTvDesc.setText(TextUtils.isEmpty(ad.get(0).getAtx()) ? "" : ad.get(0).getAtx());

                            mDeeplinktype = ad.get(0).getDeeplinktype();
                            mClk = ad.get(0).getClk();
                            mPack = ad.get(0).getPack();

                            ArrayList<String> es = ad.get(0).getEs();//展示上报
                            //点击上报
                            mEc = ad.get(0).getEc();
                            //开始下载上报
                            mStd = ad.get(0).getStd();
                            //下载完成上报
                            mDcp = ad.get(0).getDcp();
                            //安装完成上报
                            mItal = ad.get(0).getItal();

                            JinChengUtils.ReportAdv(mContext, es);//展示上报

                            holder.mReOnePic.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    float x = 0;
                                    float y = 0;
                                    float x1 = 0;
                                    float y1 = 0;
                                    switch (event.getAction()) {
                                        case MotionEvent.ACTION_DOWN:
                                            x = event.getX();
                                            y = event.getY();
                                            break;
                                        case MotionEvent.ACTION_UP:
                                            x1 = event.getX();
                                            y1 = event.getY();
                                            onItemClick(x, y, x1, y1);
                                            break;
                                    }
                                    return true;//为false不拦截
                                }
                            });
                        }
                    }
                }

                @Override
                public void onAdvFail() {

                }

                private void onItemClick(float x, float y, float x1, float y1) {
                    if (mDeeplinktype == 1) {
                        if (!TextUtils.isEmpty(mClk)) {
                            Uri uri = Uri.parse(mClk);
                            Intent four = new Intent(Intent.ACTION_VIEW, uri);
                            mContext.startActivity(four);
                        }
                    } else {
                        if (mEc != null && mEc.size() > 0) {
                            for (String value : mEc) {
                                String s = doReplaceMacro(value, x, y, x1, y1);
                                JinChengUtils.ReportAdv(mContext, s);
                            }
                        }
                        DownloadUtils.downLoadApk(mContext, mClk, mStd, mDcp);
                        //TODO 安装完成监听 上报 mPack
                    }
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private String doReplaceMacro(String value, float x, float y, float x1, float y1) {
        if (TextUtils.isEmpty(value))
            return null;
        if (value.contains("cb_down_x")) {
            value = value.replace("cb_down_x", x + "");
        }
        if (value.contains("cb_down_y")) {
            value = value.replace("cb_down_y", y + "");
        }
        if (value.contains("cb_up_x")) {
            value = value.replace("cb_up_x", x1 + "");
        }
        if (value.contains("cb_up_y")) {
            value = value.replace("cb_up_y", y1 + "");
        }
        return value;
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.icon)
        ImageView      mIcon;
        @BindView(R.id.tv_title)
        TextView       mTvTitle;
        @BindView(R.id.tv_desc)
        TextView       mTvDesc;
        @BindView(R.id.re_one_pic)
        RelativeLayout mReOnePic;
        @BindView(R.id.container)
        FrameLayout    mContainer;

        ViewHolder(View inflate) {
            super(inflate);
            ButterKnife.bind(this, inflate);
        }
    }
}
