package com.zhengdao.zqb.view.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.entity.RewardTicketEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author lijiangop
 * @CreateTime 2018/1/10 16:29
 */
public class RewardTicketAdapter extends RecyclerView.Adapter {

    private int mState = -1;
    private Context                  mContext;
    private List<RewardTicketEntity> mData;
    private ViewHolder               mViewHolder;
    private ItemCallBack             mItemCallBack;

    public interface ItemCallBack {
        void onItemClick(int position);
    }

    public RewardTicketAdapter(Context context, List<RewardTicketEntity> data, ItemCallBack itemCallBack, int state) {
        this.mItemCallBack = itemCallBack;
        this.mContext = context;
        this.mState = state;
        this.mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(View.inflate(mContext, R.layout.item_reward_ticket, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        try {
            mViewHolder = (ViewHolder) holder;
            RewardTicketEntity entity = mData.get(position);
            SpannableString spannableString;
            int value = new Double(entity.quota).intValue();
            if (entity.type == 1) {
                spannableString = new SpannableString(value + "M");
                mViewHolder.mTvType.setText("流量券");
            } else {
                spannableString = new SpannableString(value + "元");
                mViewHolder.mTvType.setText("话费券");
            }
            spannableString.setSpan(new RelativeSizeSpan(0.5f), String.valueOf(value).length(), spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            mViewHolder.mTvPrice.setText(spannableString);
            mViewHolder.mTvTitle.setText(TextUtils.isEmpty(entity.title) ? "" : entity.title);
            if (mState == 0)
                mViewHolder.mTvDeadline.setText(TextUtils.isEmpty(entity.endTime) ? "" : "有效期至:" + entity.endTime);
            else
                mViewHolder.mTvDeadline.setText(TextUtils.isEmpty(entity.endTime) ? "" : "有效期至:" + entity.endTime);
            mViewHolder.mTvGet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemCallBack != null)
                        mItemCallBack.onItemClick(position);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_price)
        TextView       mTvPrice;
        @BindView(R.id.tv_type)
        TextView       mTvType;
        @BindView(R.id.tv_title)
        TextView       mTvTitle;
        @BindView(R.id.tv_get)
        TextView       mTvGet;
        @BindView(R.id.tv_deadline)
        TextView       mTvDeadline;
        @BindView(R.id.ll_item)
        RelativeLayout mLlItem;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
