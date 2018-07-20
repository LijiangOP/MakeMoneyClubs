package com.zhengdao.zqb.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mobads.AdView;
import com.baidu.mobads.AdViewListener;
import com.bumptech.glide.Glide;
import com.fynn.fluidlayout.FluidLayout;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.comm.constants.AdPatternType;
import com.qq.e.comm.util.AdError;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.RebateBean;
import com.zhengdao.zqb.utils.AdvertisementUtils;
import com.zhengdao.zqb.utils.Utils;
import com.zhengdao.zqb.view.activity.calculator.CalculatorActivity;
import com.zhengdao.zqb.view.activity.investinfodetailinput.InvestInfoDetailInputActivity;
import com.zhengdao.zqb.view.activity.licaidetail.LicaiDetailActivity;
import com.zhengdao.zqb.view.activity.rebaterecords.RebateRecordsActivity;
import com.zhengdao.zqb.view.activity.rebateservice.RebateServiceActivity;
import com.zhengdao.zqb.view.activity.webview.WebViewActivity;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.zhengdao.zqb.application.ClientAppLike.AppType;
import static com.zhengdao.zqb.utils.AdvertisementUtils.TencentAdv.getAdInfo;

/**
 * @Author lijiangop
 * @CreateTime 2018/6/8 15:27
 */
public class RebateAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    private static final int TYPE_HEADER = 1;
    private static final int TYPE_GOODS  = 0;
    private HeaderViewHolder mHeaderViewHolder;
    private GoodsViewHolder  mGoodsViewHolder;
    private onItemClick      mCallback;
    private Activity         mContext;
    private List<RebateBean> mData;

    public interface onItemClick {
        void onBaiduAdvClick();

        void onTencentAdvClick();
    }

    public RebateAdapter(Activity context, List<RebateBean> data, onItemClick callback) {
        this.mCallback = callback;
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        if (viewType == TYPE_HEADER) {
            View header = View.inflate(mContext, R.layout.recyl_rebate_header, null);
            holder = new HeaderViewHolder(header);
        } else if (viewType == TYPE_GOODS) {
            View licai = View.inflate(mContext, R.layout.recyl_rebate_goods, null);
            holder = new GoodsViewHolder(licai);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            final RebateBean itemBean = mData.get(position);
            if (itemBean == null)
                return;
            if (itemBean.type == TYPE_HEADER) {
                mHeaderViewHolder = (HeaderViewHolder) holder;
                mHeaderViewHolder.mTvCalculate.setOnClickListener(this);
                mHeaderViewHolder.mTvGuide.setOnClickListener(this);
                mHeaderViewHolder.mTvRecords.setOnClickListener(this);
                mHeaderViewHolder.mTvInfoInput.setOnClickListener(this);
                mHeaderViewHolder.mTvService.setOnClickListener(this);
                mHeaderViewHolder.mFlAdv.removeAllViews();
                initAdv();
            } else {
                mGoodsViewHolder = (GoodsViewHolder) holder;
                Glide.with(mContext).load(itemBean.logo).error(R.drawable.net_less_140).into(mGoodsViewHolder.mIvIcon);
                mGoodsViewHolder.mTvDescibe.setText(TextUtils.isEmpty(itemBean.title) ? "" : itemBean.title);
                mGoodsViewHolder.mFluidLayoutKeyWord.removeAllViews();
                if (!TextUtils.isEmpty(itemBean.mark)) {
                    if (itemBean.mark.contains("/")) {
                        String[] split = itemBean.mark.split("/");
                        switch (split.length) {
                            case 1:
                                addKeyword(split[0]);
                                break;
                            case 2:
                                addKeyword(split[0]);
                                addKeyword(split[1]);
                                break;
                            case 3:
                                addKeyword(split[0]);
                                addKeyword(split[1]);
                                addKeyword(split[2]);
                                break;
                        }
                    } else {
                        addKeyword(itemBean.mark);
                    }
                }
                mGoodsViewHolder.mFluidLayoutMoney.removeAllViews();
                addMoneyPart(itemBean);
                mGoodsViewHolder.mReRecommend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, LicaiDetailActivity.class);
                        intent.putExtra(Constant.Activity.Data, itemBean.id);
                        mContext.startActivity(intent);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initAdv() {
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
                            if (mHeaderViewHolder.mFlAdv != null)
                                mHeaderViewHolder.mFlAdv.addView(nativeExpressADView);
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
                        if (mCallback != null)
                            mCallback.onTencentAdvClick();
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
                AdView baiDuBanner = AdvertisementUtils.BaiDuAdv.getBaiDuBanner(mContext, Constant.BaiDuAdv.Rebate, new AdViewListener() {
                    @Override
                    public void onAdReady(AdView adView) {

                    }

                    @Override
                    public void onAdShow(JSONObject jsonObject) {

                    }

                    @Override
                    public void onAdClick(JSONObject jsonObject) {
                        Log.w("BAIDUA", "onAdClick " + jsonObject.toString());
                        if (mCallback != null)
                            mCallback.onBaiduAdvClick();
                    }

                    @Override
                    public void onAdFailed(String s) {

                    }

                    @Override
                    public void onAdSwitch() {

                    }

                    @Override
                    public void onAdClose(JSONObject jsonObject) {
                        Log.w("BAIDUA", "onAdClose " + jsonObject.toString());
                    }
                });
                DisplayMetrics dm = new DisplayMetrics();
                ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
                int winW = dm.widthPixels;
                int winH = dm.heightPixels;
                int width = Math.min(winW, winH);
                int height = width / 5;
                RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(width, height);
                rllp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                mHeaderViewHolder.mFlAdv.addView(baiDuBanner, rllp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addKeyword(String keyword) {
        FluidLayout.LayoutParams params = new FluidLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 10, 25, 0);
        TextView textView = new TextView(mContext);
        textView.setText(keyword);
        textView.setTextSize(9);
        textView.setTextColor(mContext.getResources().getColor(R.color.color_6f717f));
        textView.setBackground(mContext.getResources().getDrawable(R.drawable.shape3));
        textView.setPadding(9, 2, 9, 2);
        mGoodsViewHolder.mFluidLayoutKeyWord.addView(textView, params);
    }

    private void addMoneyPart(RebateBean itemBean) {
        FluidLayout.LayoutParams params = new FluidLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 25, 0);
        params.gravity = Gravity.CENTER;
        TextView textView = new TextView(mContext);
        textView.setTextSize(12);
        textView.setTextColor(mContext.getResources().getColor(R.color.color_fc3135));
        String value1 = "" + (itemBean.incomeTotal == null ? 0 : new DecimalFormat("#0.00").format(itemBean.incomeTotal));
        String value2 = new DecimalFormat("#0.00").format(new Double(itemBean.incomeTotal) - Double.valueOf(itemBean.rebate));
        SpannableString spannableString = new SpannableString("赚 ¥" + value1 + "  ¥" + value2);
        RelativeSizeSpan sizeSpan01 = new RelativeSizeSpan(1.5f);
        if (value1.contains(".")) {
            String[] split = value1.split("\\.");
            spannableString.setSpan(sizeSpan01, 3, split[0].length() + 3, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        } else {
            spannableString.setSpan(sizeSpan01, 3, value1.length() + 3, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#999999"));
        StrikethroughSpan strikethroughSpan = new StrikethroughSpan();
        spannableString.setSpan(colorSpan, value1.length() + 4, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(strikethroughSpan, value1.length() + 4, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        textView.setText(spannableString);
        mGoodsViewHolder.mFluidLayoutMoney.addView(textView, params);
        //新品显示
        if (itemBean.goodsType == 1) {
            TextView textView2 = new TextView(mContext);
            textView2.setTextSize(9);
            textView2.setTextColor(mContext.getResources().getColor(R.color.color_00b9fd));
            textView2.setBackground(mContext.getResources().getDrawable(R.drawable.shape_rect_home_item_news));
            textView2.setPadding(10, 2, 10, 2);
            textView2.setText("新品");
            textView2.setGravity(Gravity.CENTER_VERTICAL);
            mGoodsViewHolder.mFluidLayoutMoney.addView(textView2, params);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).type;
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.tv_calculate:
                    mContext.startActivity(new Intent(mContext, CalculatorActivity.class));
                    break;
                case R.id.tv_guide:
                    Intent guid = new Intent(mContext, WebViewActivity.class);
                    guid.putExtra(Constant.WebView.TITLE, "新手指引");
                    guid.putExtra(Constant.WebView.URL, Constant.Url.BASEURL + "/goGuide");
                    mContext.startActivity(guid);
                    break;
                case R.id.tv_info_input:
                    Utils.StartActivity(mContext, new Intent(mContext, InvestInfoDetailInputActivity.class));
                    break;
                case R.id.tv_records:
                    Utils.StartActivity(mContext, new Intent(mContext, RebateRecordsActivity.class));
                    break;
                case R.id.tv_service:
                    mContext.startActivity(new Intent(mContext,RebateServiceActivity.class));
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.fl_adv)
        FrameLayout  mFlAdv;
        @BindView(R.id.tv_calculate)
        TextView     mTvCalculate;
        @BindView(R.id.tv_guide)
        TextView     mTvGuide;
        @BindView(R.id.tv_info_input)
        TextView     mTvInfoInput;
        @BindView(R.id.tv_records)
        TextView     mTvRecords;
        @BindView(R.id.tv_service)
        TextView     mTvService;
        @BindView(R.id.linear_layout)
        LinearLayout mLinearLayout;

        public HeaderViewHolder(View header) {
            super(header);
            ButterKnife.bind(this, header);
        }
    }

    public class GoodsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_icon)
        ImageView      mIvIcon;
        @BindView(R.id.tv_descibe)
        TextView       mTvDescibe;
        @BindView(R.id.fluid_layout_keyWord)
        FluidLayout    mFluidLayoutKeyWord;
        @BindView(R.id.fluid_layout_money)
        FluidLayout    mFluidLayoutMoney;
        @BindView(R.id.re_recommend)
        RelativeLayout mReRecommend;

        public GoodsViewHolder(View goods) {
            super(goods);
            ButterKnife.bind(this, goods);
        }
    }
}
