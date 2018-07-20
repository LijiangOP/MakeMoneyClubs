package com.zhengdao.zqb.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.MarketCommentHttpEntity;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.view.activity.marketcommentdetail.MarketCommentDetailActivity;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author lijiangop
 * @CreateTime 2018/3/5 17:53
 */
public class MarketCommentAdapter extends SimpleAdapter {

    private Double mMoney;

    public MarketCommentAdapter(Context context, List data, Double money) {
        super(context, data);
        this.mMoney = money;
    }

    @Override
    protected View getItemView(int i, View convertView, ViewGroup viewGroup) {
        View view;
        ViewHolder holder;
        if (convertView != null) {
            view = convertView;
            holder = (ViewHolder) convertView.getTag();
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_market_comment, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        final MarketCommentHttpEntity.MarketComment entity = (MarketCommentHttpEntity.MarketComment) mData.get(i);
        try {
            Glide.with(mContext).load(entity.goodsIcon).error(R.drawable.net_less_36).into(holder.mIvGoods);
            holder.mTvTitle.setText(TextUtils.isEmpty(entity.title) ? "" : entity.title);
            DecimalFormat    df   = new DecimalFormat("######0.00");
            holder.mTvMoney.setText("赏" + df.format(mMoney / mData.size()) + "元");
            if (entity.state == 1) {
                holder.mIvAlreadyDone.setVisibility(View.VISIBLE);
                holder.mTvChecking.setVisibility(View.GONE);
                holder.mTvGoFinish.setVisibility(View.GONE);
            } else if (entity.state == 2) {
                holder.mIvAlreadyDone.setVisibility(View.GONE);
                holder.mTvChecking.setVisibility(View.VISIBLE);
                holder.mTvGoFinish.setVisibility(View.GONE);
            } else {
                holder.mIvAlreadyDone.setVisibility(View.GONE);
                holder.mTvChecking.setVisibility(View.GONE);
                holder.mTvGoFinish.setVisibility(View.VISIBLE);
            }
            holder.mLlItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (entity.state != 1 && entity.state != 2) {
                        Intent intent = new Intent(mContext, MarketCommentDetailActivity.class);
                        intent.putExtra(Constant.Activity.Data, entity);
                        mContext.startActivity(intent);
                    }
                }
            });
        } catch (Exception e) {
            LogUtils.e(e.getMessage());
        }
        return view;
    }

    class ViewHolder {
        @BindView(R.id.iv_goods)
        ImageView    mIvGoods;
        @BindView(R.id.tv_title)
        TextView     mTvTitle;
        @BindView(R.id.tv_money)
        TextView     mTvMoney;
        @BindView(R.id.tv_checking)
        TextView     mTvChecking;
        @BindView(R.id.tv_go_finish)
        TextView     mTvGoFinish;
        @BindView(R.id.iv_already_done)
        ImageView    mIvAlreadyDone;
        @BindView(R.id.ll_item)
        LinearLayout mLlItem;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
