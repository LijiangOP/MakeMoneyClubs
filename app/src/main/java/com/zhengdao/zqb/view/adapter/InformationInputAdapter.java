package com.zhengdao.zqb.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.daimajia.swipe.SwipeLayout;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.customview.CircleImageView;
import com.zhengdao.zqb.entity.InvestBean;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author lijiangop
 * @CreateTime 2018/6/11 09:30
 */
public class InformationInputAdapter extends RecyclerView.Adapter {

    private ItemCallBack          mCallBack;
    private Context               mContext;
    private ArrayList<InvestBean> mDatas;

    public interface ItemCallBack {
        void deleteItem(int position);
    }

    public InformationInputAdapter(Context context, ArrayList<InvestBean> datas, ItemCallBack callBack) {
        this.mCallBack = callBack;
        this.mContext = context;
        this.mDatas = datas;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GoodsViewHolder(View.inflate(mContext, R.layout.recyl_input_info_item, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            final GoodsViewHolder viewHolder = (GoodsViewHolder) holder;
            final InvestBean mInvestBean = mDatas.get(position);
            if (mInvestBean == null)
                return;
            if (position % 2 == 0) {
                viewHolder.mSwipe.setBackground(mContext.getResources().getDrawable(R.drawable.invest_info_item_bg_red));
            } else {
                viewHolder.mSwipe.setBackground(mContext.getResources().getDrawable(R.drawable.invest_info_item_bg_blue));
            }
            Glide.with(mContext).load(mInvestBean.logo).error(R.drawable.net_less_36).into(viewHolder.mIvIcon);
            viewHolder.mTvPlatformName.setText(mInvestBean.wzName);
            viewHolder.mTvName.setText(mInvestBean.investmentName);
            viewHolder.mTvPhone.setText(mInvestBean.investmentPhone);
            viewHolder.mTvTime.setText("投资时间 " + mInvestBean.investmentTime);
            viewHolder.mTvNumber.setText(mInvestBean.amount + "元");
            if (mInvestBean.cycleCompany == 1)
                viewHolder.mTvCycle.setText(mInvestBean.cycle + "天");
            else
                viewHolder.mTvCycle.setText(mInvestBean.cycle + "月");
            switch (mInvestBean.state) {
                case 0:
                    viewHolder.mTvInvestState.setText("待审核");
                    break;
                case 1:
                    viewHolder.mTvInvestState.setText("审核通过");
                    break;
                case 2:
                    viewHolder.mTvInvestState.setText("审核未通过");
                    break;
            }
            viewHolder.mTvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mCallBack != null)
                        mCallBack.deleteItem(mInvestBean.id);
                    viewHolder.mSwipe.close();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    class GoodsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_delete)
        TextView        mTvDelete;
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
        @BindView(R.id.swipe)
        SwipeLayout     mSwipe;

        GoodsViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
