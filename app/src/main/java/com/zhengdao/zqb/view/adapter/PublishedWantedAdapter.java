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
import com.fynn.fluidlayout.FluidLayout;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.customview.SwipeBackActivity.CommonDialog;
import com.zhengdao.zqb.entity.ManagementWantedHttpEntity;
import com.zhengdao.zqb.view.activity.publish.PublishActivity;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author lijiangop
 * @CreateTime 2018/1/12 16:56
 */
public class PublishedWantedAdapter extends SimpleAdapter {

    private CallBack callBack;
    private boolean mIsVisible = false;
    private ViewHolder   mViewHolder;
    private CommonDialog mCommonDialog;

    public interface CallBack {
        void cancle(int id);
    }

    public PublishedWantedAdapter(Context context, List data, CallBack callBack) {
        super(context, data);
        this.callBack = callBack;
    }

    @Override
    protected View getItemView(final int i, View convertView, ViewGroup viewGroup) {
        View view;
        if (convertView != null) {
            view = convertView;
            mViewHolder = (ViewHolder) convertView.getTag();
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_published_wanted, viewGroup, false);
            mViewHolder = new ViewHolder(view);
            view.setTag(mViewHolder);
        }
        try {
            final ManagementWantedHttpEntity.RewardPageDetail entity = (ManagementWantedHttpEntity.RewardPageDetail) mData.get(i);
            if (entity == null)
                return view;
            Glide.with(mContext).load(entity.picture).error(R.drawable.net_less_140).into(mViewHolder.mIvIcon);
            mViewHolder.mTvDescibe.setText(TextUtils.isEmpty(entity.title) ? "" : entity.title);
            mViewHolder.mTvJoinNum.setText(entity.joincount + "人参与");
            mViewHolder.mTvPrice.setText(new DecimalFormat("#0.00").format(entity.money));
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
            String state = "";
            switch (entity.state) {
                case Constant.Wanted.STATE_SAVE:
                    mViewHolder.mTvCancle.setVisibility(View.GONE);
                    state = "暂存";
                    break;
                case Constant.Wanted.STATE_CHECKING:
                    mViewHolder.mTvCancle.setVisibility(View.GONE);
                    state = "审核中";
                    break;
                case Constant.Wanted.STATE_PUBLISHED:
                    mViewHolder.mTvCancle.setVisibility(View.VISIBLE);
                    state = "已发布";
                    break;
                case Constant.Wanted.STATE_FINISHED:
                    mViewHolder.mTvCancle.setVisibility(View.GONE);
                    state = "已结束";
                    break;
                case Constant.Wanted.STATE_SOLDING_OUT:
                    mViewHolder.mTvCancle.setVisibility(View.GONE);
                    state = "下架中";
                    break;
                case Constant.Wanted.STATE_SOLDED_OUT:
                    mViewHolder.mTvCancle.setVisibility(View.GONE);
                    state = "已下架";
                    break;
            }
            if (entity.payState == 1) {
                mViewHolder.mTvPayState.setText("已支付");
            } else {
                mViewHolder.mTvPayState.setText("未支付");
            }
            mViewHolder.mTvLookSimilarity.setText(state);
            mViewHolder.mTvCancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showConfirmDialog();
                    if (callBack != null)
                        callBack.cancle(entity.id);
                }
            });
            mViewHolder.mLlRecommend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, PublishActivity.class);
                    intent.putExtra(Constant.Activity.Type, entity.state);
                    intent.putExtra(Constant.Activity.Data, entity.id);
                    intent.putExtra(Constant.Activity.Data1, entity.payState);
                    mContext.startActivity(intent);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    private void showConfirmDialog() {
        if (mCommonDialog == null)
            mCommonDialog = new CommonDialog(mContext);
        mCommonDialog.initView("是否确定申请下架", "再看看", "", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCommonDialog != null)
                    mCommonDialog.dismiss();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCommonDialog != null)
                    mCommonDialog.dismiss();
            }
        });
        mCommonDialog.setTitleSize(15);
        mCommonDialog.show();
    }

    private void addKeyword(String keyword) {
        FluidLayout.LayoutParams params = new FluidLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 10, 25, 0);
        TextView textView = new TextView(mContext);
        textView.setText(keyword);
        textView.setTextSize(10);
        textView.setTextColor(mContext.getResources().getColor(R.color.color_6f717f));
        textView.setBackground(mContext.getResources().getDrawable(R.drawable.shape3));
        textView.setPadding(10, 3, 10, 3);
        mViewHolder.mFluidLayout.addView(textView, params);
    }

    class ViewHolder {
        @BindView(R.id.iv_icon)
        ImageView    mIvIcon;
        @BindView(R.id.tv_descibe)
        TextView     mTvDescibe;
        @BindView(R.id.tv_price)
        TextView     mTvPrice;
        @BindView(R.id.fluid_layout)
        FluidLayout  mFluidLayout;
        @BindView(R.id.tv_cancle)
        TextView     mTvCancle;
        @BindView(R.id.tv_join_num)
        TextView     mTvJoinNum;
        @BindView(R.id.tv_pay_state)
        TextView     mTvPayState;
        @BindView(R.id.tv_look_similarity)
        TextView     mTvLookSimilarity;
        @BindView(R.id.ll_recommend)
        LinearLayout mLlRecommend;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
