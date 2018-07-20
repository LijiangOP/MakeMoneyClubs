package com.zhengdao.zqb.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fynn.fluidlayout.FluidLayout;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.HomeItemEntity;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.view.activity.homegoodsdetail.HomeGoodsDetailActivity;
import com.zhengdao.zqb.view.activity.login.LoginActivity;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.iwgang.countdownview.CountdownView;

/**
 * @Author lijiangop
 * @CreateTime 2018/4/11 16:44
 */
public class GoodsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context              mContext;
    private List<HomeItemEntity> mData;
    private ViewHolder           mViewHolder;

    public GoodsAdapter(Context context, List<HomeItemEntity> data, boolean isShowFm) {
        this(context, data);
    }

    public GoodsAdapter(Context context, List<HomeItemEntity> data) {
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        View header = View.inflate(mContext, R.layout.recyl_home_goods_item, null);
        holder = new ViewHolder(header);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        mViewHolder = (ViewHolder) holder;
        final HomeItemEntity entity = mData.get(position);
        try {
            Glide.with(mContext).load(entity.picture).error(R.drawable.net_less_140).into(mViewHolder.mIvIcon);
            mViewHolder.mTvDescibe.setText(TextUtils.isEmpty(entity.title) ? "" : entity.title);
            mViewHolder.mTvPrice.setText(new DecimalFormat("#0.00").format(entity.money));
            if (!TextUtils.isEmpty(entity.discount)) {
                mViewHolder.mTvTicket.setVisibility(View.VISIBLE);
                mViewHolder.mTvTicket.setText(entity.discount);
            } else {
                mViewHolder.mTvTicket.setVisibility(View.GONE);
            }
            if (entity.type == 1) {
                mViewHolder.mTvNews.setVisibility(View.VISIBLE);
            } else
                mViewHolder.mTvNews.setVisibility(View.GONE);
            if (entity.lowerTime > 0 && entity.lowerTime < 86400000) {
                mViewHolder.mCountDownView.setVisibility(View.VISIBLE);
                mViewHolder.mCountDownView.start(entity.lowerTime);
                mViewHolder.mTvFinishTag.setVisibility(View.VISIBLE);
            } else {
                mViewHolder.mCountDownView.setVisibility(View.GONE);
                mViewHolder.mTvFinishTag.setVisibility(View.GONE);
            }
            if (entity.isOwn == 1)
                mViewHolder.mTvSelfSupport.setVisibility(View.VISIBLE);
            else
                mViewHolder.mTvSelfSupport.setVisibility(View.GONE);
            mViewHolder.mFluidLayout.removeAllViews();
            if (!TextUtils.isEmpty(entity.keyword)) {
                if (entity.keyword.contains(",")) {
                    String[] split = entity.keyword.split(",");
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
                    addKeyword(entity.keyword);
                }
            }
            mViewHolder.mLlRecommend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent;
                    if (SettingUtils.isLogin(mContext)) {
                        intent = new Intent(mContext, HomeGoodsDetailActivity.class);
                        intent.putExtra(Constant.Activity.Data, entity.id);
                    } else {
                        intent = new Intent(mContext, LoginActivity.class);
                    }
                    mContext.startActivity(intent);
                }
            });
        } catch (Exception ex) {
            LogUtils.e(ex.getMessage());
        }
    }

    private void addKeyword(String keyword) {
        FluidLayout.LayoutParams params = new FluidLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 8, 0);
        TextView textView = new TextView(mContext);
        textView.setText(keyword);
        textView.setTextSize(10);
        textView.setTextColor(mContext.getResources().getColor(R.color.color_6f717f));
        textView.setBackground(mContext.getResources().getDrawable(R.drawable.shape3));
        textView.setPadding(5, 3, 5, 3);
        mViewHolder.mFluidLayout.addView(textView, params);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_icon)
        ImageView     mIvIcon;
        @BindView(R.id.tv_descibe)
        TextView      mTvDescibe;
        @BindView(R.id.fluid_layout)
        FluidLayout   mFluidLayout;
        @BindView(R.id.tv_self_support)
        TextView      mTvSelfSupport;
        @BindView(R.id.tv_price)
        TextView      mTvPrice;
        @BindView(R.id.tv_ticket)
        TextView      mTvTicket;
        @BindView(R.id.tv_news)
        TextView      mTvNews;
        @BindView(R.id.countDownView)
        CountdownView mCountDownView;
        @BindView(R.id.tv_finish_tag)
        TextView      mTvFinishTag;
        @BindView(R.id.ll_recommend)
        LinearLayout  mLlRecommend;

        public ViewHolder(View header) {
            super(header);
            ButterKnife.bind(this, header);
        }
    }
}
