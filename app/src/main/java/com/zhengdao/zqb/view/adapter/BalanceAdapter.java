package com.zhengdao.zqb.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.entity.BalanceDetailEntity;
import com.zhengdao.zqb.entity.NewBalanceEntity;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author lijiangop
 * @CreateTime 2018/6/11 14:31
 */
public class BalanceAdapter extends RecyclerView.Adapter {
    private List<NewBalanceEntity> mData;
    private Context                mContext;

    public BalanceAdapter(Context context, List<NewBalanceEntity> data) {
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return new HeadViewHolder(View.inflate(mContext, R.layout.user_balance_item_head, null));
        } else
            return new GoodsViewHolder(View.inflate(mContext, R.layout.user_balance_item, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            NewBalanceEntity newBalance = mData.get(position);
            if (newBalance.type == 0) {
                HeadViewHolder headViewHolder = (HeadViewHolder) holder;
                headViewHolder.mTvDate.setText(TextUtils.isEmpty(newBalance.date) ? "" : newBalance.date);
            } else {
                GoodsViewHolder goodsViewHolder = (GoodsViewHolder) holder;
                BalanceDetailEntity entity = newBalance.entity;
                goodsViewHolder.mTvLeftTop.setText((TextUtils.isEmpty(entity.findState) ? "" : entity.findState) + "-" +
                        (TextUtils.isEmpty(entity.logDesc) ? "" : entity.logDesc));
                goodsViewHolder.mTvLeftBottom.setText("余额: " + new DecimalFormat("#0.00").format(entity.usableSum));
                goodsViewHolder.mTvRightTop.setText(TextUtils.isEmpty(entity.createTime) ? "" : entity.createTime);
                if (entity.outAmount != 0)
                    goodsViewHolder.mTvRightBottom.setText("-" + new DecimalFormat("#0.00").format(entity.outAmount));
                else if (entity.inAmount != 0)
                    goodsViewHolder.mTvRightBottom.setText("+" + new DecimalFormat("#0.00").format(entity.inAmount));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).type;
    }

    class GoodsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_left_top)
        TextView       mTvLeftTop;
        @BindView(R.id.tv_right_top)
        TextView       mTvRightTop;
        @BindView(R.id.tv_left_bottom)
        TextView       mTvLeftBottom;
        @BindView(R.id.tv_right_bottom)
        TextView       mTvRightBottom;
        @BindView(R.id.ll_item)
        RelativeLayout mLlItem;

        GoodsViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    class HeadViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_date)
        TextView mTvDate;

        HeadViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
