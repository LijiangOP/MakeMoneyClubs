package com.zhengdao.zqb.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhengdao.zqb.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author lijiangop
 * @CreateTime 2018/7/6 09:12
 */
public class TestAdapter extends RecyclerView.Adapter {

    private Context            mContext;
    private ArrayList<Integer> mData;

    public TestAdapter(Context context, ArrayList<Integer> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            return new HeadViewHolder(View.inflate(mContext, R.layout.test_recycle_head_item, null));
        } else {
            return new ContentViewHolder(View.inflate(mContext, R.layout.test_recycle_content_item, null));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mData.get(position)==1){

        }else {

        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position);
    }

    class HeadViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.re_synthesize)
        RelativeLayout mReSynthesize;
        @BindView(R.id.tv_hot)
        TextView       mTvHot;
        @BindView(R.id.iv_hot_arrow_up)
        ImageView      mIvHotArrowUp;
        @BindView(R.id.iv_hot_arrow_down)
        ImageView      mIvHotArrowDown;
        @BindView(R.id.re_hot)
        RelativeLayout mReHot;
        @BindView(R.id.tv_award)
        TextView       mTvAward;
        @BindView(R.id.iv_reward_arrow_up)
        ImageView      mIvRewardArrowUp;
        @BindView(R.id.iv_reward_arrow_down)
        ImageView      mIvRewardArrowDown;
        @BindView(R.id.re_award)
        RelativeLayout mReAward;
        @BindView(R.id.tv_select)
        TextView       mTvSelect;
        @BindView(R.id.re_select)
        RelativeLayout mReSelect;
        @BindView(R.id.ll_heard)
        LinearLayout   mLlHeard;

        public HeadViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class ContentViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.textView)
        TextView mTextView;

        public ContentViewHolder(View inflate) {
            super(inflate);
            ButterKnife.bind(this, inflate);
        }
    }
}
