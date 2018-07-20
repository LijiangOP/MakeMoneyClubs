package com.zhengdao.zqb.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.customview.CircleImageView;
import com.zhengdao.zqb.entity.InvestBean;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author lijiangop
 * @CreateTime 2018/6/11 10:28
 */
public class InvestRecordAdapter extends RecyclerView.Adapter {

    private Context               mContext;
    private ArrayList<InvestBean> mData;

    public InvestRecordAdapter(Context context, ArrayList<InvestBean> data) {
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(View.inflate(mContext, R.layout.invest_record_item, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            ViewHolder viewHolder = (ViewHolder) holder;
            InvestBean mInvestBean = mData.get(position);
            if (mInvestBean == null)
                return;
            if (position % 2 == 0) {
                viewHolder.mLlItem.setBackground(mContext.getResources().getDrawable(R.drawable.invest_info_item_bg_red));
            } else {
                viewHolder.mLlItem.setBackground(mContext.getResources().getDrawable(R.drawable.invest_info_item_bg_blue));
            }
            Glide.with(mContext).load(TextUtils.isEmpty(mInvestBean.logo) ? "" : mInvestBean.logo).error(R.drawable.net_less_36).into(viewHolder.mIvIcon);
            viewHolder.mTvPlatformName.setText(TextUtils.isEmpty(mInvestBean.wzName) ? "" : mInvestBean.wzName);
            viewHolder.mTvName.setText(mInvestBean.investmentName);
            viewHolder.mTvTime.setText("投资时间 " + mInvestBean.investmentTime);
            viewHolder.mTvNumber.setText(mInvestBean.amount + "元");
            viewHolder.mTvCycle.setText(mInvestBean.cycle + "天");
            viewHolder.mTvPhone.setText(TextUtils.isEmpty(mInvestBean.investmentPhone) ? "" : mInvestBean.investmentPhone);
            viewHolder.mTvInvestState.setText("奖励 " + mInvestBean.integral + "乐分");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ll_item)
        LinearLayout    mLlItem;
        @BindView(R.id.iv_icon)
        CircleImageView mIvIcon;
        @BindView(R.id.tv_platform_name)
        TextView        mTvPlatformName;
        @BindView(R.id.tv_name)
        TextView        mTvName;
        @BindView(R.id.tv_phone)
        TextView        mTvPhone;
        @BindView(R.id.tv_number)
        TextView        mTvNumber;
        @BindView(R.id.tv_cycle)
        TextView        mTvCycle;
        @BindView(R.id.tv_time)
        TextView        mTvTime;
        @BindView(R.id.tv_invest_state)
        TextView        mTvInvestState;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
