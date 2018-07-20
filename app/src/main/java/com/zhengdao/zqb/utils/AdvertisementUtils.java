package com.zhengdao.zqb.utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.baidu.mobads.AdView;
import com.baidu.mobads.AdViewListener;
import com.baidu.mobads.AppActivity;
import com.qq.e.ads.banner.AbstractBannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.comm.constants.AdPatternType;
import com.qq.e.comm.pi.AdData;
import com.qq.e.comm.util.AdError;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;

import org.json.JSONObject;

import java.util.List;


/**
 * @Author lijiangop
 * @CreateTime 2018/5/10 16:40
 */
public class AdvertisementUtils {

    public interface onItemClick {
        void onAdvClick();
    }

    public static class TencentAdv {

        public interface OnNativeAdvListener {
            void onAdvShow();

            void onAdvClose();
        }

        /**
         * 获取固定位置的应用宝广告
         *
         * @param activity
         * @param viewGroup
         */
        public static void getTencentBannerAdv(Activity activity, String advId, final ViewGroup viewGroup, boolean isshowClose) throws Exception {
            try {
                final BannerView bannerView = new BannerView(activity, com.qq.e.ads.banner.ADSize.BANNER, Constant.TencentAdv.advTenCent_APPID, advId);
                bannerView.setRefresh(30);//设置广告轮播时间，为0或30~120之间的数字，单位为s,0标识不自动轮播
                bannerView.setADListener(new AbstractBannerADListener() {

                    @Override
                    public void onNoAD(AdError error) {
                        Log.i("AD_DEMO", String.format("Banner onNoAD，eCode = %d, eMsg = %s", error.getErrorCode(), error.getErrorMsg()));
                    }

                    @Override
                    public void onADReceiv() {
                        Log.i("AD_DEMO", "ONBannerReceive");
                    }

                    @Override
                    public void onADClicked() {
                        Log.i("AD_DEMO", "ONBannerClick");
                    }

                    @Override
                    public void onADClosed() {
                        Log.i("AD_DEMO", "ONBannerClose");
                        if (bannerView != null && viewGroup != null)
                            viewGroup.removeView(bannerView);
                        if (bannerView != null)
                            bannerView.destroy();
                    }
                });
                viewGroup.addView(bannerView);
                bannerView.setShowClose(isshowClose);
                bannerView.loadAD();
            } catch (Exception e) {
                LogUtils.e(e.getMessage());
            }
        }

        /**
         * 获取固定位置的应用宝广告
         *
         * @param activity
         * @param viewGroup
         */
        public static void getTencentBannerAdvWithCallBack(Activity activity, String advId, final ViewGroup viewGroup, boolean isshowClose, final onItemClick mCallback) throws Exception {
            try {
                final BannerView bannerView = new BannerView(activity, com.qq.e.ads.banner.ADSize.BANNER, Constant.TencentAdv.advTenCent_APPID, advId);
                bannerView.setRefresh(30);//设置广告轮播时间，为0或30~120之间的数字，单位为s,0标识不自动轮播
                bannerView.setADListener(new AbstractBannerADListener() {

                    @Override
                    public void onNoAD(AdError error) {
                        Log.i("AD_DEMO", String.format("Banner onNoAD，eCode = %d, eMsg = %s", error.getErrorCode(), error.getErrorMsg()));
                    }

                    @Override
                    public void onADReceiv() {
                        Log.i("AD_DEMO", "ONBannerReceive");
                    }

                    @Override
                    public void onADClicked() {
                        Log.i("AD_DEMO", "ONBannerClick");
                        if (mCallback != null)
                            mCallback.onAdvClick();
                    }

                    @Override
                    public void onADClosed() {
                        Log.i("AD_DEMO", "ONBannerClose");
                        if (bannerView != null && viewGroup != null)
                            viewGroup.removeView(bannerView);
                        if (bannerView != null)
                            bannerView.destroy();
                    }
                });
                viewGroup.addView(bannerView);
                bannerView.setShowClose(isshowClose);
                bannerView.loadAD();
            } catch (Exception e) {
                LogUtils.e(e.getMessage());
            }
        }

        public static void getTencentNativeAdv(Context activity, final ViewGroup container, String advId, final OnNativeAdvListener onNativeAdvListener) throws Exception {
            ADSize adSize = new ADSize(ADSize.FULL_WIDTH, ADSize.AUTO_HEIGHT); // 消息流中用AUTO_HEIGHT
            NativeExpressAD nativeAD = new NativeExpressAD(activity, adSize, Constant.TencentAdv.advTenCent_APPID, advId, new NativeExpressAD.NativeExpressADListener() {

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
                        if (container != null)
                            container.addView(nativeExpressADView);
                    }
                    if (onNativeAdvListener != null)
                        onNativeAdvListener.onAdvShow();
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
                }

                @Override
                public void onADClosed(NativeExpressADView nativeExpressADView) {
                    Log.i("NativeExpressAD", "onADClosed: " + nativeExpressADView.toString());
                    if (onNativeAdvListener != null)
                        onNativeAdvListener.onAdvClose();
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
            int count = 1; // 一次拉取的广告条数：范围1-10
            nativeAD.loadAD(count);
        }

        public static void getTencentNativeAdv1(Context activity, String advId, NativeExpressAD.NativeExpressADListener listener) throws Exception {
            ADSize adSize = new ADSize(ADSize.FULL_WIDTH, ADSize.AUTO_HEIGHT); // 消息流中用AUTO_HEIGHT
            NativeExpressAD nativeAD = new NativeExpressAD(activity, adSize, Constant.TencentAdv.advTenCent_APPID, advId, listener);
            int count = 1; // 一次拉取的广告条数：范围1-10
            nativeAD.loadAD(count);
        }

        public static String getAdInfo(NativeExpressADView nativeExpressADView) {
            AdData adData = nativeExpressADView.getBoundData();
            if (adData != null) {
                StringBuilder infoBuilder = new StringBuilder();
                infoBuilder.append("title:").append(adData.getTitle()).append(",")
                        .append("desc:").append(adData.getDesc()).append(",")
                        .append("patternType:").append(adData.getAdPatternType());
                if (adData.getAdPatternType() == AdPatternType.NATIVE_VIDEO) {
                    infoBuilder.append(", video info: ")
                            .append(getVideoInfo(adData.getProperty(AdData.VideoPlayer.class)));
                }
                return infoBuilder.toString();
            }
            return null;
        }


        public static String getVideoInfo(AdData.VideoPlayer videoPlayer) {
            if (videoPlayer != null) {
                StringBuilder videoBuilder = new StringBuilder();
                videoBuilder.append("state:").append(videoPlayer.getVideoState()).append(",")
                        .append("duration:").append(videoPlayer.getDuration()).append(",")
                        .append("position:").append(videoPlayer.getCurrentPosition());
                return videoBuilder.toString();
            }
            return null;
        }
    }


    public static class BaiDuAdv {
        /**
         * 横幅广告:带自定义关闭按钮
         *
         * @param context
         * @param advId   广告位ID
         */
        public static void addAdvWithDefClose(Context context, String advId, ViewGroup groupView) throws Exception {
            try {
                AppActivity.setActionBarColorTheme(AppActivity.ActionBarColorTheme.ACTION_BAR_RED_THEME);
                final AdView adView = new AdView(context, advId);
                final ImageView mImageView = new ImageView(context);

                // 设置监听器
                adView.setListener(new AdViewListener() {
                    public void onAdSwitch() {
                        //广告切换
                        Log.w("BAIDUA", "onAdSwitch");
                    }

                    public void onAdShow(JSONObject info) {
                        // 广告已经渲染出来
                        Log.w("BAIDUA", "onAdShow " + info.toString());
                        if (mImageView != null)
                            mImageView.setVisibility(View.VISIBLE);
                    }

                    public void onAdReady(AdView adView) {
                        // 资源已经缓存完毕，还没有渲染出来
                        Log.w("BAIDUA", "onAdReady " + adView);
                    }

                    public void onAdFailed(String reason) {
                        //失败或后台关闭该广告时会调用
                        Log.w("BAIDUA", "onAdFailed " + reason);
                        if (mImageView != null)
                            mImageView.setVisibility(View.GONE);
                    }

                    public void onAdClick(JSONObject info) {
                        Log.w("BAIDUA", "onAdClick " + info.toString());
                    }

                    @Override
                    public void onAdClose(JSONObject arg0) {
                        Log.w("BAIDUA", "onAdClose");
                    }
                });
                DisplayMetrics dm = new DisplayMetrics();
                ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
                int winW = dm.widthPixels;
                int winH = dm.heightPixels;
                int width = Math.min(winW, winH);
                int height = width * 4 / 20;

                // 将adView添加到父控件中(注：该父控件不一定为您的根控件，只要该控件能通过addView能添加广告视图即可)
                RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(width, height);
                rllp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                groupView.addView(adView, rllp);

                //关闭按钮
                mImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.btn_to));
                mImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adView.destroy();
                        mImageView.setVisibility(View.GONE);
                    }
                });
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP | RelativeLayout.ALIGN_PARENT_RIGHT);
                groupView.addView(mImageView, layoutParams);
                mImageView.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 横幅广告;没有自定义的关闭按钮
         *
         * @param context
         * @param advId   广告位ID
         */
        public static void addAdvNoDefClose(Context context, String advId, ViewGroup groupView) throws Exception {
            try {
                AppActivity.setActionBarColorTheme(AppActivity.ActionBarColorTheme.ACTION_BAR_RED_THEME);
                final AdView adView = new AdView(context, advId);

                // 设置监听器
                adView.setListener(new AdViewListener() {
                    public void onAdSwitch() {
                        //广告切换
                        Log.w("BAIDUA", "onAdSwitch");
                    }

                    public void onAdShow(JSONObject info) {
                        // 广告已经渲染出来
                        Log.w("BAIDUA", "onAdShow " + info.toString());
                    }

                    public void onAdReady(AdView adView) {
                        // 资源已经缓存完毕，还没有渲染出来
                        Log.w("BAIDUA", "onAdReady " + adView);
                    }

                    public void onAdFailed(String reason) {
                        //失败或后台关闭该广告时会调用
                        Log.w("BAIDUA", "onAdFailed " + reason);
                    }

                    public void onAdClick(JSONObject info) {
                        Log.w("BAIDUA", "onAdClick " + info.toString());
                    }

                    @Override
                    public void onAdClose(JSONObject arg0) {
                        Log.w("BAIDUA", "onAdClose");
                    }
                });
                DisplayMetrics dm = new DisplayMetrics();
                ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
                int winW = dm.widthPixels;
                int winH = dm.heightPixels;
                int width = Math.min(winW, winH);
                int height = width * 4 / 20;

                // 将adView添加到父控件中(注：该父控件不一定为您的根控件，只要该控件能通过addView能添加广告视图即可)
                RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(width, height);
                rllp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                groupView.addView(adView, rllp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 横幅广告;没有自定义的关闭按钮
         *
         * @param context
         * @param advId   广告位ID
         */
        public static void addAdvNoDefCloseWithCallBack(Context context, String advId, ViewGroup groupView, final onItemClick mCallback) throws Exception {
            try {
                AppActivity.setActionBarColorTheme(AppActivity.ActionBarColorTheme.ACTION_BAR_RED_THEME);
                final AdView adView = new AdView(context, advId);

                // 设置监听器
                adView.setListener(new AdViewListener() {
                    public void onAdSwitch() {
                        //广告切换
                        Log.w("BAIDUA", "onAdSwitch");
                    }

                    public void onAdShow(JSONObject info) {
                        // 广告已经渲染出来
                        Log.w("BAIDUA", "onAdShow " + info.toString());
                    }

                    public void onAdReady(AdView adView) {
                        // 资源已经缓存完毕，还没有渲染出来
                        Log.w("BAIDUA", "onAdReady " + adView);
                    }

                    public void onAdFailed(String reason) {
                        //失败或后台关闭该广告时会调用
                        Log.w("BAIDUA", "onAdFailed " + reason);
                    }

                    public void onAdClick(JSONObject info) {
                        Log.w("BAIDUA", "onAdClick " + info.toString());
                        if (mCallback != null)
                            mCallback.onAdvClick();
                    }

                    @Override
                    public void onAdClose(JSONObject arg0) {
                        Log.w("BAIDUA", "onAdClose");
                    }
                });
                DisplayMetrics dm = new DisplayMetrics();
                ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
                int winW = dm.widthPixels;
                int winH = dm.heightPixels;
                int width = Math.min(winW, winH);
                int height = width * 4 / 20;

                // 将adView添加到父控件中(注：该父控件不一定为您的根控件，只要该控件能通过addView能添加广告视图即可)
                RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(width, height);
                rllp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                groupView.addView(adView, rllp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 获取百度广告的Bnanner广告控件
         *
         * @param context
         * @param advId
         */
        public static AdView getBaiDuBanner(Context context, String advId, AdViewListener listener) throws Exception {
            AdView adView = null;
            if (TextUtils.isEmpty(advId))
                return null;
            try {
                AppActivity.setActionBarColorTheme(AppActivity.ActionBarColorTheme.ACTION_BAR_RED_THEME);
                adView = new AdView(context, advId);
                adView.setListener(listener);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return adView;
        }
    }
}
