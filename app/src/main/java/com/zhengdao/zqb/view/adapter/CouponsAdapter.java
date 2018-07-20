package com.zhengdao.zqb.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.AppLinkService;
import com.alibaba.sdk.android.BaseAlibabaSDK;
import com.bumptech.glide.Glide;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.CouponsEntity;
import com.zhengdao.zqb.utils.DensityUtil;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author lijiangop
 * @CreateTime 2018/6/14 17:20
 */
public class CouponsAdapter extends RecyclerView.Adapter {

    private Context             mContext;
    private List<CouponsEntity> mData;
    private ItemSearchCallBack  mCallBack;
    private HeadViewHolder      mHeadViewHolder;
    private GoodsViewHolder     mGoodsViewHolder;
    private int                 mMeasuredHeight;

    public interface ItemSearchCallBack {
        void search(String value);
    }

    public CouponsAdapter(Context context, List<CouponsEntity> data, ItemSearchCallBack callBack) {
        this.mCallBack = callBack;
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == Constant.Coupons.Head) {
            return new HeadViewHolder(View.inflate(mContext, R.layout.item_coupons_head, null));
        } else if (viewType == Constant.Coupons.Empty) {
            return new EmptyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_empty_view, parent, false));
        } else {
            return new GoodsViewHolder(View.inflate(mContext, R.layout.item_coupons_goods, null));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            final CouponsEntity itemBean = mData.get(position);
            if (itemBean == null)
                return;
            if (itemBean.type == Constant.Coupons.Head) {
                mHeadViewHolder = (HeadViewHolder) holder;
                mHeadViewHolder.mIvSearch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String trim = mHeadViewHolder.mEtSearch.getText().toString().trim();
                        if (mCallBack != null)
                            mCallBack.search(trim);
                    }
                });
                mHeadViewHolder.mTvSearch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String trim = mHeadViewHolder.mEtSearch.getText().toString().trim();
                        if (mCallBack != null)
                            mCallBack.search(trim);
                    }
                });
                mMeasuredHeight = mHeadViewHolder.mReItem.getMeasuredHeight();
            } else if (itemBean.type == Constant.Coupons.Empty) {
                EmptyViewHolder emptyViewHolder = (EmptyViewHolder) holder;
                ViewGroup.LayoutParams layoutParams = emptyViewHolder.mReItem.getLayoutParams();
                int[] screenSize = DensityUtil.getScreenSize(mContext);
                int dimensionPixelOffset = mContext.getResources().getDimensionPixelOffset(R.dimen.fragment_title_height);
                layoutParams.height = screenSize[1] - dimensionPixelOffset - mMeasuredHeight;
                emptyViewHolder.mReItem.setLayoutParams(layoutParams);
            } else if (itemBean.type == Constant.Coupons.JinDong) {
                mGoodsViewHolder = (GoodsViewHolder) holder;
                Glide.with(mContext).load(itemBean.goodsPic).error(R.drawable.net_less_140).into(mGoodsViewHolder.mIvIcon);
                mGoodsViewHolder.mTvPlatform.setText("京东");
                mGoodsViewHolder.mTvPlatform.setBackgroundColor(mContext.getResources().getColor(R.color.main));
                mGoodsViewHolder.mTvTitle1.setText(TextUtils.isEmpty(itemBean.goodsName) ? "" : itemBean.goodsName);
                calculateText(mGoodsViewHolder.mTvTitle1, mGoodsViewHolder.mTvTitle2, TextUtils.isEmpty(itemBean.goodsName) ? "" : itemBean.goodsName);
                mGoodsViewHolder.mTvDesc.setText("京东价 ¥" + (itemBean.price == null ? 0 : new DecimalFormat("#0.00").format(itemBean.price)) + "         月销 " + itemBean.sales);
                SpannableString spannableString = new SpannableString("券后¥" + (itemBean.discount == null ? 0 : new DecimalFormat("#0.00").format(itemBean.discount)));
                String value1 = String.valueOf(itemBean.discount);
                if (value1.contains(".")) {
                    String[] split = value1.split("\\.");
                    spannableString.setSpan(new RelativeSizeSpan(1.4f), 3, split[0].length() + 3, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                } else {
                    spannableString.setSpan(new RelativeSizeSpan(1.4f), 3, value1.length() + 3, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                }
                mGoodsViewHolder.mTvPrice.setText(spannableString);
                if (TextUtils.isEmpty(itemBean.couponQuota)) {
                    mGoodsViewHolder.mTvGet.setVisibility(View.GONE);
                    mGoodsViewHolder.mTvCoupons.setVisibility(View.GONE);
                } else {
                    mGoodsViewHolder.mTvCoupons.setText(TextUtils.isEmpty(itemBean.couponQuota) ? "" : itemBean.couponQuota);
                }
                mGoodsViewHolder.mTvGet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goToJD(itemBean.extensionUrl);
                    }
                });
                mGoodsViewHolder.mReRecommend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goToJD(itemBean.extensionUrl);
                    }
                });
            } else {
                mGoodsViewHolder = (GoodsViewHolder) holder;
                Glide.with(mContext).load(itemBean.goodsPic).error(R.drawable.net_less_140).into(mGoodsViewHolder.mIvIcon);
                mGoodsViewHolder.mTvPlatform.setText("淘宝");
                mGoodsViewHolder.mTvTitle1.setText(TextUtils.isEmpty(itemBean.goodsName) ? "" : itemBean.goodsName);
                calculateText(mGoodsViewHolder.mTvTitle1, mGoodsViewHolder.mTvTitle2, TextUtils.isEmpty(itemBean.goodsName) ? "" : itemBean.goodsName);
                mGoodsViewHolder.mTvDesc.setText("淘宝价 ¥" + (itemBean.price == null ? 0 : new DecimalFormat("#0.00").format(itemBean.price)) + "         月销 " + itemBean.sales);
                SpannableString spannableString = new SpannableString("券后¥" + (itemBean.discount == null ? 0 : new DecimalFormat("#0.00").format(itemBean.discount)));
                String value1 = String.valueOf(itemBean.discount);
                if (value1.contains(".")) {
                    String[] split = value1.split("\\.");
                    spannableString.setSpan(new RelativeSizeSpan(1.4f), 3, split[0].length() + 3, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                } else {
                    spannableString.setSpan(new RelativeSizeSpan(1.4f), 3, value1.length() + 3, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                }
                mGoodsViewHolder.mTvPrice.setText(spannableString);
                if (TextUtils.isEmpty(itemBean.couponQuota)) {
                    mGoodsViewHolder.mTvGet.setVisibility(View.GONE);
                    mGoodsViewHolder.mTvCoupons.setVisibility(View.GONE);
                } else {
                    mGoodsViewHolder.mTvCoupons.setText(TextUtils.isEmpty(itemBean.couponQuota) ? "" : itemBean.couponQuota);
                }
                mGoodsViewHolder.mTvGet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goToTaobao(itemBean.extensionUrl);
                    }
                });
                mGoodsViewHolder.mReRecommend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goToTaobao(itemBean.extensionUrl);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void goToJD(String extensionUrl) {
        if (!TextUtils.isEmpty(extensionUrl)) {
            Uri uri = Uri.parse(extensionUrl);
            Intent four = new Intent(Intent.ACTION_VIEW, uri);
            mContext.startActivity(four);
        }
    }

    private void goToTaobao(String extensionUrl) {
        if (!TextUtils.isEmpty(extensionUrl)) {
            AppLinkService link = BaseAlibabaSDK.getService(AppLinkService.class);
            HashMap<String, String> params = new HashMap<String, String>();
            params.put(AppLinkService.PARAM_KEY_BACK_URL, ".view.activity.IntroduceActivity");
            link.jumpTBURI(mContext, extensionUrl, params);
        }
    }

    public static void calculateText(final TextView first, final TextView second, final String text) {
        ViewTreeObserver observer = first.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                int lineEnd = first.getLayout().getLineEnd(0);
                String substring = text.substring(lineEnd, text.length());
                if (TextUtils.isEmpty(substring)) {
                    second.setVisibility(View.GONE);
                    second.setText(null);
                } else {
                    second.setVisibility(View.VISIBLE);
                    second.setText(substring);
                }
                first.getViewTreeObserver().removeOnPreDrawListener(
                        this);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).type;
    }

    class HeadViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imageView_top_bg)
        ImageView    mImageViewTopBg;
        @BindView(R.id.iv_search)
        ImageView    mIvSearch;
        @BindView(R.id.et_search)
        EditText     mEtSearch;
        @BindView(R.id.tv_search)
        TextView     mTvSearch;
        @BindView(R.id.search_bar)
        LinearLayout mSearchBar;
        @BindView(R.id.re_item)
        LinearLayout mReItem;

        HeadViewHolder(View inflate) {
            super(inflate);
            ButterKnife.bind(this, inflate);
        }
    }

    class EmptyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_empty)
        ImageView      mIvEmpty;
        @BindView(R.id.tv_empty)
        TextView       mTvEmpty;
        @BindView(R.id.re_item)
        RelativeLayout mReItem;

        EmptyViewHolder(View inflate) {
            super(inflate);
            ButterKnife.bind(this, inflate);
        }
    }

    class GoodsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_icon)
        ImageView      mIvIcon;
        @BindView(R.id.tv_platform)
        TextView       mTvPlatform;
        @BindView(R.id.tv_title1)
        TextView       mTvTitle1;
        @BindView(R.id.tv_title2)
        TextView       mTvTitle2;
        @BindView(R.id.tv_desc)
        TextView       mTvDesc;
        @BindView(R.id.tv_price)
        TextView       mTvPrice;
        @BindView(R.id.tv_coupons)
        TextView       mTvCoupons;
        @BindView(R.id.tv_get)
        ImageView      mTvGet;
        @BindView(R.id.re_recommend)
        RelativeLayout mReRecommend;

        GoodsViewHolder(View inflate) {
            super(inflate);
            ButterKnife.bind(this, inflate);
        }
    }
}
