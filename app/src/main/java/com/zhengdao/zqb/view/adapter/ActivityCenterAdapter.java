package com.zhengdao.zqb.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.entity.ActivityCenterDetailEntity;
import com.zhengdao.zqb.utils.ActivityUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author lijiangop
 * @CreateTime 2018/1/15 17:23
 */
public class ActivityCenterAdapter extends RecyclerView.Adapter {

    private Context                          mContext;
    private List<ActivityCenterDetailEntity> mData;
    private ViewHolder                       mViewHolder;

    public ActivityCenterAdapter(Context context, List<ActivityCenterDetailEntity> data) {
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(View.inflate(mContext, R.layout.item_activitycenter, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        try {
            mViewHolder = (ViewHolder) holder;
            final ActivityCenterDetailEntity entity = mData.get(position);
            if (entity.state == 0) {
                mViewHolder.mTvState.setText("进行中");
                mViewHolder.mTvState.setBackground(mContext.getResources().getDrawable(R.drawable.shape_rect_zero_ff5624));
            } else if (entity.state == 1) {
                mViewHolder.mTvState.setText("已结束");
                mViewHolder.mTvState.setBackground(mContext.getResources().getDrawable(R.drawable.shape_rect_zero_999999));
            } else {
                mViewHolder.mTvState.setVisibility(View.GONE);
            }
            Glide.with(mContext).load(entity.imgPath).asBitmap().placeholder(R.drawable.net_less_horizontal).into(mViewHolder.mIvImg);
            mViewHolder.mTvContent.setText(TextUtils.isEmpty(entity.title) ? "" : entity.title);
            mViewHolder.mTvTime.setText("活动时间: " + (TextUtils.isEmpty(entity.startTime) ? "" : entity.startTime) + " 至 " + (TextUtils.isEmpty(entity.endTime) ? "" : entity.endTime));
            mViewHolder.mReItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityUtils.doSkip(mContext, entity.type, entity.url, entity.id);
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
        @BindView(R.id.iv_img)
        ImageView    mIvImg;
        @BindView(R.id.tv_state)
        TextView     mTvState;
        @BindView(R.id.tv_content)
        TextView     mTvContent;
        @BindView(R.id.tv_time)
        TextView     mTvTime;
        @BindView(R.id.re_item)
        LinearLayout mReItem;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
