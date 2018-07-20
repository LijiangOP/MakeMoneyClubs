package com.zhengdao.zqb.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.SparseArray;
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
import com.zhengdao.zqb.entity.MyWantedDetailEntity;
import com.zhengdao.zqb.view.activity.wanteddetail.WantedDetailActivity;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.iwgang.countdownview.CountdownView;


/**
 * @Author lijiangop
 * @CreateTime 2018/1/11 10:58
 */
public class WantedAdapter extends SimpleAdapter {

    private static final int UN_COMMITED = 0;
    private static final int COMMITED    = 1;
    private static final int UN_DONE     = 2;
    private static final int DONE        = 3;
    private       long              mCurrentTimeMillis = 0;
    private final SparseArray<Long> mCountdownVHList   = new SparseArray<>();
    private ViewEventCallBack mCallback;
    private ViewHolder mViewHolder;

    public interface ViewEventCallBack {

        void remindCheck(int wantedId);

        void cancleMission(int wantedId);

        void commite(int wantedId);

        void evaluate(int wantedId);
    }

    public WantedAdapter(Context context, List data, ViewEventCallBack callback) {
        super(context, data);
        mCallback = callback;
    }

    @Override
    protected View getItemView(int position, View convertView, ViewGroup viewGroup) {
        View view;
        if (convertView != null) {
            view = convertView;
            mViewHolder = (ViewHolder) convertView.getTag();
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_wanted, viewGroup, false);
            mViewHolder = new ViewHolder(view);
            view.setTag(mViewHolder);
        }
        try {
            final MyWantedDetailEntity bean = (MyWantedDetailEntity) mData.get(position);
            if (bean == null || bean.user == null)
                return view;
            mViewHolder.mTvUnDone.setVisibility(View.GONE);
            Glide.with(mContext).load(bean.user.avatar).error(R.drawable.net_less_36).into(mViewHolder.mIvShopIcon);
            mViewHolder.mTvShopName.setText(TextUtils.isEmpty(bean.user.nickName) ? "" : bean.user.nickName);
            Glide.with(mContext).load(bean.reward.picture).error(R.drawable.net_less_140).into(mViewHolder.mIvGoodsIcon);
            mViewHolder.mTvGoodsTitle.setText(TextUtils.isEmpty(bean.reward.title) ? "" : bean.reward.title);
            mViewHolder.mTvGoodsPrice.setText(new DecimalFormat("#0.00").format(bean.money));
            mViewHolder.mTvGoodsPriceDetail.setText("共1个悬赏 合计：￥" + new DecimalFormat("#0.00").format(bean.money));
            //关键词
            mViewHolder.mFluidLayout.removeAllViews();
            if (!TextUtils.isEmpty(bean.reward.keyword)) {
                if (bean.reward.keyword.contains(",")) {
                    String[] split = bean.reward.keyword.split(",");
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
                    addKeyword(bean.reward.keyword);
                }
            }
            //倒计时
            Long lastTime = mCountdownVHList.get(position);
            if (lastTime == null) {//还没存入过
                lastTime = System.currentTimeMillis();
                mCountdownVHList.append(position, lastTime);//存入数据
            }
            long passedTime = System.currentTimeMillis() - lastTime;//过去的时间 就是当前时间减去初次设置时间
            mViewHolder.mCountDownView.start(bean.deadlineTime - passedTime);
            //        if (bean.deadlineTime > 86400000)
            //首部文字及底部按钮
            switch (bean.state) {
                case UN_COMMITED:
                    mViewHolder.mTvWantedState.setVisibility(View.VISIBLE);
                    mViewHolder.mLlBottom.setVisibility(View.VISIBLE);
                    mViewHolder.mTvLeftButton.setVisibility(View.VISIBLE);
                    mViewHolder.mTvRightButton.setVisibility(View.VISIBLE);
                    mViewHolder.mTvWantedState.setText("等待提交");
                    mViewHolder.mTvLeftButton.setText("取消任务");
                    mViewHolder.mCountDownView.setVisibility(View.VISIBLE);
                    mViewHolder.mTvRightButton.setText("提交");
                    break;
                case COMMITED:
                    mViewHolder.mTvWantedState.setVisibility(View.VISIBLE);
                    mViewHolder.mLlBottom.setVisibility(View.VISIBLE);
                    mViewHolder.mTvLeftButton.setVisibility(View.VISIBLE);
                    mViewHolder.mTvWantedState.setText("已提交");
                    mViewHolder.mTvLeftButton.setText("提醒审核");
                    mViewHolder.mCountDownView.setVisibility(View.VISIBLE);
                    mViewHolder.mTvRightButton.setVisibility(View.GONE);
                    break;
                case UN_DONE:
                    mViewHolder.mTvWantedState.setVisibility(View.VISIBLE);
                    mViewHolder.mLlBottom.setVisibility(View.GONE);
                    mViewHolder.mTvUnDone.setVisibility(View.VISIBLE);
                    mViewHolder.mCountDownView.setVisibility(View.GONE);
                    mViewHolder.mTvWantedState.setText("审核未通过");
                    mViewHolder.mTvUnDone.setText(TextUtils.isEmpty(bean.remarks) ? "" : bean.remarks);
                    break;
                case DONE:
                    mViewHolder.mTvWantedState.setVisibility(View.VISIBLE);
                    mViewHolder.mLlBottom.setVisibility(View.VISIBLE);
                    mViewHolder.mTvRightButton.setVisibility(View.VISIBLE);
                    mViewHolder.mCountDownView.setVisibility(View.GONE);
                    mViewHolder.mTvWantedState.setText("悬赏成功");
                    mViewHolder.mTvRightButton.setText("评价");
                    mViewHolder.mTvRightButton.setVisibility(View.GONE);// 暂时没有评价功能
                    mViewHolder.mTvLeftButton.setVisibility(View.GONE);
                    break;
                default:
                    mViewHolder.mLlBottom.setVisibility(View.GONE);
                    break;
            }
            //点击事件
            mViewHolder.mLlGoods.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, WantedDetailActivity.class);
                    intent.putExtra(Constant.Activity.Data, bean.id);
                    intent.putExtra(Constant.Activity.Data1, bean.state);
                    mContext.startActivity(intent);
                }
            });
            mViewHolder.mTvLeftButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null) {
                        if (bean.state == UN_COMMITED) {//取消任务
                            mCallback.cancleMission(bean.id);
                        } else if (bean.state == COMMITED) {//提醒审核
                            mCallback.remindCheck(bean.reward.id);
                        }
                    }
                }
            });
            mViewHolder.mTvRightButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null) {
                        if (bean.state == UN_COMMITED) {//提交
                            mCallback.commite(bean.reward.id);
                        } else if (bean.state == DONE) {//评价
                            mCallback.evaluate(bean.id);
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    private void addKeyword(String keyword) {
        FluidLayout.LayoutParams params = new FluidLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 10, 15, 0);
        TextView textView = new TextView(mContext);
        textView.setText(keyword);
        textView.setTextSize(10);
        textView.setTextColor(mContext.getResources().getColor(R.color.color_6f717f));
        textView.setBackground(mContext.getResources().getDrawable(R.drawable.shape4));
        textView.setPadding(10, 3, 10, 3);
        mViewHolder.mFluidLayout.addView(textView, params);
    }

    public void reset() {
        mCountdownVHList.clear(); //清空
    }

    class ViewHolder {
        @BindView(R.id.iv_shop_icon)
        ImageView     mIvShopIcon;
        @BindView(R.id.tv_shop_name)
        TextView      mTvShopName;
        @BindView(R.id.tv_wanted_state)
        TextView      mTvWantedState;
        @BindView(R.id.iv_goods_icon)
        ImageView     mIvGoodsIcon;
        @BindView(R.id.tv_goods_title)
        TextView      mTvGoodsTitle;
        @BindView(R.id.tv_goods_price)
        TextView      mTvGoodsPrice;
        @BindView(R.id.fluid_layout)
        FluidLayout   mFluidLayout;
        @BindView(R.id.countDownView)
        CountdownView mCountDownView;
        @BindView(R.id.ll_goods)
        LinearLayout  mLlGoods;
        @BindView(R.id.tv_goods_price_detail)
        TextView      mTvGoodsPriceDetail;
        @BindView(R.id.tv_left_button)
        TextView      mTvLeftButton;
        @BindView(R.id.tv_right_button)
        TextView      mTvRightButton;
        @BindView(R.id.ll_bottom)
        LinearLayout  mLlBottom;
        @BindView(R.id.tv_un_done)
        TextView      mTvUnDone;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
