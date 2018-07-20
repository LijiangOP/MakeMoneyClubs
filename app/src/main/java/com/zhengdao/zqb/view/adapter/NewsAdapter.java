package com.zhengdao.zqb.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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

import com.baidu.mobads.AdView;
import com.baidu.mobads.AdViewListener;
import com.bumptech.glide.Glide;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.comm.constants.AdPatternType;
import com.qq.e.comm.util.AdError;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.BaiDuApiAdvEntity;
import com.zhengdao.zqb.entity.HotRecommendEntity;
import com.zhengdao.zqb.entity.NewsDetailEntity;
import com.zhengdao.zqb.utils.AdvertisementUtils;
import com.zhengdao.zqb.utils.AppUtils;
import com.zhengdao.zqb.utils.BaiDuAPiAdvUtils;
import com.zhengdao.zqb.utils.DownloadUtils;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.TimeUtils;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.view.activity.webview.WebViewActivity;

import org.json.JSONException;
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
 * @CreateTime 2018/6/14 11:55
 */
public class NewsAdapter extends RecyclerView.Adapter {

    private CallBack               mCallBack;
    private Context                mContext;
    private List<NewsDetailEntity> mData;
    private BaiduAdvHolder         mBaiduAdvHolder;
    private APIViewHolder          mAdViewHolder;
    private boolean                isAPiAdvShow;
    private String                 mShow_type;
    private String                 mDown_url;
    private ArrayList<String>      mDc_rpt;
    private ArrayList<String>      mC_rpt;
    private ArrayList<String>      mD_rpt;
    private ArrayList<String>      mI_rpt;


    public interface CallBack {
        void onButtonClick(int position, HotRecommendEntity entity);

        void onInstallClick(int position, HotRecommendEntity entity);

        void onBaiduAdvClick();

        void onTencentAdvClick();

        void onApiDownLoadStart(ArrayList<String> i_rpt, String clickid, String fileName);
    }

    public NewsAdapter(Context context, List<NewsDetailEntity> data, CallBack callBack) {
        this.mCallBack = callBack;
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 2) {
            return new OnePicHolder(View.inflate(mContext, R.layout.item_news_one_pic, null));
        } else if (viewType == 3) {
            return new ThreePicHolder(View.inflate(mContext, R.layout.item_news_three_pic, null));
        } else if (viewType == 9) {
            return new AppDataHolder(View.inflate(mContext, R.layout.item_news_download_app, null));
        } else if (viewType == 10) {
            return new BaiduAdvHolder(View.inflate(mContext, R.layout.item_news_adv, null));
        } else if (viewType == 11) {
            return new APIViewHolder(View.inflate(mContext, R.layout.item_news_adview_item, null));
        } else {
            return new NoPicHolder(View.inflate(mContext, R.layout.item_news_no_pic, null));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final NewsDetailEntity entity = mData.get(position);
        if (null == entity)
            return;
        try {
            switch (entity.showType) {
                //没有图片
                case 1:
                    NoPicHolder noPicHolder = (NoPicHolder) holder;
                    noPicHolder.mTvTitle.setText(TextUtils.isEmpty(entity.title) ? "" : entity.title);
                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append(TextUtils.isEmpty(entity.author_name) ? "    " : entity.author_name + "   ");
                    stringBuffer.append(TimeUtils.getTimeFormatText(entity.date));
                    noPicHolder.mTvDesc.setText(stringBuffer);
                    noPicHolder.mIvClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mData.remove(position);
                            notifyDataSetChanged();
                        }
                    });
                    noPicHolder.mLlItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            doSkip(entity.url, entity.author_name);
                        }
                    });
                    break;
                //一张图片
                case 2:
                    OnePicHolder onePicHolder = (OnePicHolder) holder;
                    Glide.with(mContext).load(entity.thumbnail_pic_s).error(R.drawable.net_less_140).into(onePicHolder.mImageView);
                    onePicHolder.mTvTitle.setText(TextUtils.isEmpty(entity.title) ? "" : entity.title);
                    StringBuffer onePicStringBuffer = new StringBuffer();
                    onePicStringBuffer.append(TextUtils.isEmpty(entity.author_name) ? "    " : entity.author_name + "   ");
                    onePicStringBuffer.append(TimeUtils.getTimeFormatText(entity.date));
                    onePicHolder.mTvDesc.setText(onePicStringBuffer);
                    onePicHolder.mIvClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mData.remove(position);
                            notifyDataSetChanged();
                        }
                    });
                    onePicHolder.mLlItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            doSkip(entity.url, entity.author_name);
                        }
                    });
                    break;
                //一张以上
                case 3:
                    ThreePicHolder threePicHolder = (ThreePicHolder) holder;
                    Glide.with(mContext).load(entity.thumbnail_pic_s).error(R.drawable.net_less_140).into(threePicHolder.mImageView1);
                    Glide.with(mContext).load(entity.thumbnail_pic_s02).error(R.drawable.net_less_140).into(threePicHolder.mImageView2);
                    if (TextUtils.isEmpty(entity.thumbnail_pic_s03)) {
                        threePicHolder.mImageView3.setVisibility(View.INVISIBLE);
                    } else {
                        threePicHolder.mImageView3.setVisibility(View.VISIBLE);
                        Glide.with(mContext).load(entity.thumbnail_pic_s03).error(R.drawable.net_less_140).into(threePicHolder.mImageView3);
                    }
                    threePicHolder.mTvTitle.setText(TextUtils.isEmpty(entity.title) ? "" : entity.title);
                    StringBuffer threePicStringBuffer = new StringBuffer();
                    threePicStringBuffer.append(TextUtils.isEmpty(entity.author_name) ? "    " : entity.author_name + "   ");
                    threePicStringBuffer.append(TimeUtils.getTimeFormatText(entity.date));
                    threePicHolder.mTvDesc.setText(threePicStringBuffer);
                    threePicHolder.mIvClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mData.remove(position);
                            notifyDataSetChanged();
                        }
                    });
                    threePicHolder.mLlItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            doSkip(entity.url, entity.author_name);
                        }
                    });
                    break;
                //应用下载
                case 9:
                    AppDataHolder appDataHolder = (AppDataHolder) holder;
                    final HotRecommendEntity appEntity = entity.appEntity;
                    if (null == appEntity)
                        return;
                    switch (appEntity.state) {
                        case 0:
                            appDataHolder.mTvInstall.setBackground(mContext.getResources().getDrawable(R.drawable.selector_find_download));
                            appDataHolder.mTvInstall.setTextColor(mContext.getResources().getColor(R.color.selector_find_install));
                            appDataHolder.mTvInstall.setText("下载");
                            break;
                        case 1:
                            appDataHolder.mTvInstall.setBackground(mContext.getResources().getDrawable(R.drawable.shape_find_downloading));
                            appDataHolder.mTvInstall.setTextColor(mContext.getResources().getColor(R.color.color_666666));
                            appDataHolder.mTvInstall.setText("下载中");
                            break;
                        case 2:
                            appDataHolder.mTvInstall.setBackground(mContext.getResources().getDrawable(R.drawable.shape_find_install));
                            appDataHolder.mTvInstall.setTextColor(mContext.getResources().getColor(R.color.white));
                            appDataHolder.mTvInstall.setText("安装");
                            break;
                        case 3:
                            appDataHolder.mTvInstall.setBackground(mContext.getResources().getDrawable(R.drawable.shape_find_open));
                            appDataHolder.mTvInstall.setTextColor(mContext.getResources().getColor(R.color.white));
                            appDataHolder.mTvInstall.setText("打开");
                            break;
                    }
                    Glide.with(mContext).load(appEntity.logo).error(R.drawable.net_less_36).into(appDataHolder.mIvGoods);
                    appDataHolder.mTvTitle.setText(TextUtils.isEmpty(appEntity.appName) ? "" : appEntity.appName);
                    appDataHolder.mTvDesc.setText(TextUtils.isEmpty(appEntity.content) ? "" : appEntity.content);
                    appDataHolder.mTvInstall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            switch (appEntity.state) {
                                case 0:
                                    if (mCallBack != null) {
                                        if (appEntity != null) {
                                            mCallBack.onButtonClick(position, appEntity);
                                            appEntity.setState(1);
                                            notifyDataSetChanged();
                                        } else
                                            ToastUtil.showToast(mContext, "数据出错,请刷新重试。");
                                    }
                                    break;
                                case 1:
                                    ToastUtil.showToast(mContext, "应用下载中");
                                    break;
                                case 2:
                                    if (mCallBack != null) {
                                        mCallBack.onInstallClick(position, appEntity);
                                    }
                                    break;
                                case 3:
                                    AppUtils.openApp(mContext, appEntity.packageName);
                                    break;
                            }
                        }
                    });
                    break;
                //百度腾讯SDKbanner广告
                case 10:
                    mBaiduAdvHolder = (BaiduAdvHolder) holder;
                    initAdv(entity);
                    break;
                //API广告
                case 11:
                    mAdViewHolder = (APIViewHolder) holder;
                    initAPIAdv(position);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doSkip(String url, String source) {
        if (!TextUtils.isEmpty(url)) {
            Intent intent = new Intent(mContext, WebViewActivity.class);
            intent.putExtra(Constant.WebView.URL, url);
            intent.putExtra(Constant.WebView.TITLE, source);
            mContext.startActivity(intent);
        }
    }

    /**
     * 百度 腾讯SDK广告
     *
     * @param entity
     */
    private void initAdv(NewsDetailEntity entity) {
        try {
            if (AppType == Constant.App.Wlgfl) {
                AdvertisementUtils.TencentAdv.getTencentNativeAdv1(mContext, Constant.TencentAdv.advTenCent_ADV_ORIGINAL_ID_1, new NativeExpressAD.NativeExpressADListener() {

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
                            if (mBaiduAdvHolder.mFlContainer != null)
                                mBaiduAdvHolder.mFlContainer.addView(nativeExpressADView);
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
                            mCallBack.onTencentAdvClick();
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
            } else {
                AdView baiDuBanner = AdvertisementUtils.BaiDuAdv.getBaiDuBanner(mContext, entity.advId, new AdViewListener() {
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
                            mCallBack.onBaiduAdvClick();
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
                mBaiduAdvHolder.mFlContainer.addView(baiDuBanner, rllp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * API广告
     *
     * @param position
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initAPIAdv(final int position) {
        try {
            mAdViewHolder.mFlItem.setVisibility(View.GONE);
            clearFiled();
            BaiDuAPiAdvUtils.getAdv(mContext, new BaiDuAPiAdvUtils.BaiDuApiAdvListener() {
                @Override
                public void onAdvGet(final BaiDuApiAdvEntity baiDuApiAdvEntity) {
                    if (baiDuApiAdvEntity != null) {
                        mShow_type = baiDuApiAdvEntity.cnf.dgfly.show_type;
                        mDown_url = baiDuApiAdvEntity.cnf.dgfly.down_url;
                        boolean rtp1 = baiDuApiAdvEntity.cnf.dgfly.rtp1;//是否需要格式化坐标
                        //展示上报数据
                        final ArrayList<String> s_rpt = baiDuApiAdvEntity.cnf.dgfly.s_rpt;//展示上报数据
                        //下载完成上报数据
                        mDc_rpt = baiDuApiAdvEntity.cnf.dgfly.dc_rpt;
                        //开始下载上报数据
                        mD_rpt = baiDuApiAdvEntity.cnf.dgfly.d_rpt;
                        //安装上报数据
                        mI_rpt = baiDuApiAdvEntity.cnf.dgfly.i_rpt;
                        //点击上报数据
                        mC_rpt = baiDuApiAdvEntity.cnf.dgfly.c_rpt;
                        //广告数据
                        ArrayList<String> ad_img = baiDuApiAdvEntity.cnf.dgfly.ad_img;
                        String name = baiDuApiAdvEntity.cnf.dgfly.name;
                        String desc = baiDuApiAdvEntity.cnf.dgfly.desc;
                        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(desc)) {
                            if (ad_img != null && ad_img.size() > 0 && mAdViewHolder != null) {
                                Glide.with(mContext).load(ad_img.get(0)).error(R.drawable.net_less_140).into(mAdViewHolder.mIvOnlyPic);
                            }
                            mAdViewHolder.mReOnlyPic.setVisibility(View.VISIBLE);
                            mAdViewHolder.mReOnePic.setVisibility(View.GONE);
                        } else {
                            if (ad_img != null && ad_img.size() > 0 && mAdViewHolder != null) {
                                Glide.with(mContext).load(ad_img.get(0)).error(R.drawable.net_less_140).into(mAdViewHolder.mIcon);
                            }
                            mAdViewHolder.mReOnePic.setVisibility(View.VISIBLE);
                            mAdViewHolder.mReOnlyPic.setVisibility(View.GONE);
                            mAdViewHolder.mTvTitle.setText(TextUtils.isEmpty(name) ? "" : name);
                            mAdViewHolder.mTvDesc.setText(TextUtils.isEmpty(desc) ? "" : desc);
                        }
                        //展示上报
                        if (s_rpt != null && s_rpt.size() > 0) {
                            for (String string : s_rpt) {
                                BaiDuAPiAdvUtils.ReportAdv(string);
                            }
                        }
                        mAdViewHolder.mFlItem.setVisibility(View.VISIBLE);
                        isAPiAdvShow = true;
                    }
                }

                @Override
                public void onAdvFail() {
                    mData.remove(position);
                    notifyDataSetChanged();
                    LogUtils.e("暂无广告");
                }
            });
            mAdViewHolder.mFlItem.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (!isAPiAdvShow)
                        return true;//拦截剩下的点击事件
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
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            ToastUtil.showToast(mContext, e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearFiled() {
        isAPiAdvShow = false;
        if (mC_rpt != null)
            mC_rpt.clear();
        if (mD_rpt != null)
            mD_rpt.clear();
        if (mDc_rpt != null)
            mDc_rpt.clear();
        if (mI_rpt != null)
            mI_rpt.clear();
    }

    private void onItemClick(float x, float y, float x1, float y1) {
        if (TextUtils.isEmpty(mShow_type))
            return;
        if (mShow_type.equals("bb_banner_web") || mShow_type.equals("bb_banner") || mShow_type.equals("gdt_roll_web")) {//非下载类,落地页用浏览器打开
            if (!TextUtils.isEmpty(mDown_url)) {
                Uri uri = Uri.parse(mDown_url);
                Intent four = new Intent(Intent.ACTION_VIEW, uri);
                mContext.startActivity(four);
            }
            if (mC_rpt != null && mC_rpt.size() > 0) {
                for (String value : mC_rpt) {
                    value = doReplaceMacro(value, x, y, x1, y1);
                    if (!TextUtils.isEmpty(value))
                        BaiDuAPiAdvUtils.ReportAdv(value);
                }
            }
        } else if (mShow_type.equals("bb_banner_app")) {//落地页是下载类
            if (TextUtils.isEmpty(mDown_url))
                return;
            DownloadUtils.downLoadApk(mContext, mDown_url, mD_rpt, mDc_rpt, "");
            if (mCallBack != null)
                mCallBack.onApiDownLoadStart(mI_rpt, "", "" + (mDown_url.hashCode()));
            if (mC_rpt != null && mC_rpt.size() > 0) {
                for (String value : mC_rpt) {
                    value = doReplaceMacro(value, x, y, x1, y1);
                    if (!TextUtils.isEmpty(value))
                        BaiDuAPiAdvUtils.ReportAdv(value);
                }
            }
        } else if (mShow_type.equals("gdt_roll_app")) {//落地页是下载类，但是需要从down_url字段解析获得
            if (TextUtils.isEmpty(mDown_url))
                return;
            BaiDuAPiAdvUtils.getDownloadData(mDown_url, new BaiDuAPiAdvUtils.DownloadDataListener() {
                @Override
                public void onDataGet(String responseStr) {
                    if (TextUtils.isEmpty(responseStr))
                        return;
                    try {
                        JSONObject jsonObject = new JSONObject(responseStr);
                        JSONObject data = (JSONObject) jsonObject.get("data");
                        String clickid = (String) data.get("clickid");
                        String dstlink = (String) data.get("dstlink");
                        if (mC_rpt != null && mC_rpt.size() > 0) {
                            for (String value : mC_rpt) {
                                if (!TextUtils.isEmpty(value)) {
                                    if (value.contains("SZST_CLID") && !TextUtils.isEmpty(clickid))
                                        value = value.replace("SZST_CLID", clickid);
                                    BaiDuAPiAdvUtils.ReportAdv(value);
                                }
                            }
                        }
                        DownloadUtils.downLoadApk(mContext, dstlink, mD_rpt, mDc_rpt, clickid);
                        if (mCallBack != null)
                            mCallBack.onApiDownLoadStart(mI_rpt, clickid, "" + (dstlink.hashCode()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * 宏替换 如果有宏字样
     *
     * @param value
     * @param x
     * @param y
     * @param x1
     * @param y1
     * @return
     */
    private String doReplaceMacro(String value, float x, float y, float x1, float y1) {
        if (TextUtils.isEmpty(value))
            return null;
        if (value.contains("SZST_DX")) {
            value = value.replace("SZST_DX", x + "");
        }
        if (value.contains("SZST_DY")) {
            value = value.replace("SZST_DY", y + "");
        }
        if (value.contains("SZST_UX")) {
            value = value.replace("SZST_UX", x1 + "");
        }
        if (value.contains("SZST_UY")) {
            value = value.replace("SZST_UY", y1 + "");
        }
        return value;
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).showType;
    }

    class NoPicHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_title)
        TextView       mTvTitle;
        @BindView(R.id.tv_desc)
        TextView       mTvDesc;
        @BindView(R.id.iv_close)
        ImageView      mIvClose;
        @BindView(R.id.re_item)
        RelativeLayout mLlItem;

        public NoPicHolder(View inflate) {
            super(inflate);
            ButterKnife.bind(this, inflate);
        }
    }

    class OnePicHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imageView)
        ImageView      mImageView;
        @BindView(R.id.tv_title)
        TextView       mTvTitle;
        @BindView(R.id.tv_desc)
        TextView       mTvDesc;
        @BindView(R.id.iv_close)
        ImageView      mIvClose;
        @BindView(R.id.re_item)
        RelativeLayout mLlItem;

        public OnePicHolder(View inflate) {
            super(inflate);
            ButterKnife.bind(this, inflate);
        }
    }

    class ThreePicHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_title)
        TextView       mTvTitle;
        @BindView(R.id.imageView_1)
        ImageView      mImageView1;
        @BindView(R.id.imageView_2)
        ImageView      mImageView2;
        @BindView(R.id.imageView_3)
        ImageView      mImageView3;
        @BindView(R.id.tv_desc)
        TextView       mTvDesc;
        @BindView(R.id.iv_close)
        ImageView      mIvClose;
        @BindView(R.id.re_item)
        RelativeLayout mLlItem;

        public ThreePicHolder(View inflate) {
            super(inflate);
            ButterKnife.bind(this, inflate);
        }
    }

    class AppDataHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_goods)
        ImageView mIvGoods;
        @BindView(R.id.tv_title)
        TextView  mTvTitle;
        @BindView(R.id.tv_desc)
        TextView  mTvDesc;
        @BindView(R.id.tv_install)
        TextView  mTvInstall;

        public AppDataHolder(View inflate) {
            super(inflate);
            ButterKnife.bind(this, inflate);
        }
    }

    class BaiduAdvHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.fl_container)
        FrameLayout mFlContainer;

        public BaiduAdvHolder(View inflate) {
            super(inflate);
            ButterKnife.bind(this, inflate);
        }
    }

    class APIViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.icon)
        ImageView      mIcon;
        @BindView(R.id.tv_title)
        TextView       mTvTitle;
        @BindView(R.id.tv_desc)
        TextView       mTvDesc;
        @BindView(R.id.re_one_pic)
        RelativeLayout mReOnePic;
        @BindView(R.id.iv_only_pic)
        ImageView      mIvOnlyPic;
        @BindView(R.id.re_only_pic)
        RelativeLayout mReOnlyPic;
        @BindView(R.id.fl_item)
        FrameLayout    mFlItem;

        public APIViewHolder(View inflate) {
            super(inflate);
            ButterKnife.bind(this, inflate);
        }
    }
}
