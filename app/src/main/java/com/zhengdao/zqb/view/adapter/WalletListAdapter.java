package com.zhengdao.zqb.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.entity.AdvRewardEntity;
import com.zhengdao.zqb.entity.BalanceDetailEntity;
import com.zhengdao.zqb.entity.ChargeRecordEntity;
import com.zhengdao.zqb.entity.IntegralEntity;
import com.zhengdao.zqb.entity.WalletListEntity;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.view.activity.redpacketdetail.RedpacketDetailActivity;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.util.TypedValue.COMPLEX_UNIT_PX;
import static com.zhengdao.zqb.config.Constant.Wallet.TYPE_ADVERTISEMENT;
import static com.zhengdao.zqb.config.Constant.Wallet.TYPE_BAR_VALUE;
import static com.zhengdao.zqb.config.Constant.Wallet.TYPE_BROKERAGE;
import static com.zhengdao.zqb.config.Constant.Wallet.TYPE_CHARGERECORD;
import static com.zhengdao.zqb.config.Constant.Wallet.TYPE_INTEGRAL;
import static com.zhengdao.zqb.config.Constant.Wallet.TYPE_REDPACKET;

/**
 * @Author lijiangop
 * @CreateTime 2018/1/9 16:39
 */
public class WalletListAdapter extends SimpleAdapter {

    public WalletListAdapter(Context context, List data) {
        super(context, data);
    }

    @Override
    protected View getItemView(int position, View convertView, ViewGroup viewGroup) {
        View view = null;
        ViewHolder holder = null;
        WalletListEntity entity = (WalletListEntity) mData.get(position);
        if (convertView != null) {
            view = convertView;
            holder = (ViewHolder) convertView.getTag();
        } else {
            switch (entity.type) {
                case 1:
                    view = LayoutInflater.from(mContext).inflate(R.layout.item_redpacket, viewGroup, false);
                    holder = new RebPocketViewHolder(view);
                    break;
                default:
                    view = LayoutInflater.from(mContext).inflate(R.layout.item_balance, viewGroup, false);
                    holder = new BalanceViewHolder(view);
                    break;
            }
            view.setTag(holder);
        }
        try {
            switch (entity.type) {
                case TYPE_REDPACKET:
                    RebPocketViewHolder rebPocketHolder = (RebPocketViewHolder) holder;
                    rebPocketHolder.mLlItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, RedpacketDetailActivity.class);
                            mContext.startActivity(intent);
                        }
                    });
                    break;
                case TYPE_BAR_VALUE:
                    BalanceDetailEntity entity2 = (BalanceDetailEntity) entity.object;
                    BalanceViewHolder barValueHolder1 = (BalanceViewHolder) holder;
                    barValueHolder1.mTvLeftTop.setText((TextUtils.isEmpty(entity2.findState) ? "" : entity2.findState) + "-" +
                            (TextUtils.isEmpty(entity2.logDesc) ? "" : entity2.logDesc));
                    barValueHolder1.mTvLeftBottom.setText("余额: " + new DecimalFormat("#0.00").format(entity2.usableSum));
                    barValueHolder1.mTvRightTop.setText(TextUtils.isEmpty(entity2.createTime) ? "" : entity2.createTime);
                    if (entity2.outAmount != 0)
                        barValueHolder1.mTvRightBottom.setText("-" + new DecimalFormat("#0.00").format(entity2.outAmount));
                    else if (entity2.inAmount != 0)
                        barValueHolder1.mTvRightBottom.setText("+" + new DecimalFormat("#0.00").format(entity2.inAmount));
                    else
                        barValueHolder1.mTvRightBottom.setText("+0.00");
                    break;
                case TYPE_BROKERAGE:
                    BalanceDetailEntity entity3 = (BalanceDetailEntity) entity.object;
                    BalanceViewHolder barValueHolder2 = (BalanceViewHolder) holder;
                    barValueHolder2.mTvLeftTop.setText((TextUtils.isEmpty(entity3.findState) ? "" : entity3.findState) + "-" +
                            (TextUtils.isEmpty(entity3.logDesc) ? "" : entity3.logDesc));
                    barValueHolder2.mTvLeftBottom.setText("余额: " + new DecimalFormat("#0.00").format(entity3.usableSum));
                    barValueHolder2.mTvLeftBottom.setTextColor(mContext.getResources().getColor(R.color.text_999999));
                    barValueHolder2.mTvLeftBottom.setTextSize(COMPLEX_UNIT_PX, 33);
                    barValueHolder2.mTvRightTop.setText(TextUtils.isEmpty(entity3.createTime) ? "" : entity3.createTime);
                    barValueHolder2.mTvRightTop.setTextColor(mContext.getResources().getColor(R.color.text_333333));
                    barValueHolder2.mTvRightTop.setTextSize(COMPLEX_UNIT_PX, 39);
                    if (entity3.outAmount != 0)
                        barValueHolder2.mTvRightBottom.setText("余额: -" + new DecimalFormat("#0.00").format(entity3.outAmount));
                    else if (entity3.inAmount != 0)
                        barValueHolder2.mTvRightBottom.setText("+" + new DecimalFormat("#0.00").format(entity3.inAmount));
                    else
                        barValueHolder2.mTvRightBottom.setText("+0.00");
                    barValueHolder2.mTvRightBottom.setTextColor(mContext.getResources().getColor(R.color.text_999999));
                    barValueHolder2.mTvRightBottom.setTextSize(COMPLEX_UNIT_PX, 33);
                    break;
                case TYPE_INTEGRAL:
                    IntegralEntity.IntegralDetailEntity entity4 = (IntegralEntity.IntegralDetailEntity) entity.object;
                    BalanceViewHolder barValueHolder3 = (BalanceViewHolder) holder;
                    barValueHolder3.mTvLeftTop.setText((TextUtils.isEmpty(entity4.typeState) ? "" : entity4.typeState) + "-" +
                            (TextUtils.isEmpty(entity4.logDesc) ? "" : entity4.logDesc));
                    barValueHolder3.mTvLeftBottom.setText("余额: " + new DecimalFormat("#0.00").format(entity4.usableSum));
                    barValueHolder3.mTvRightTop.setText(TextUtils.isEmpty(entity4.createTime) ? "" : entity4.createTime);
                    barValueHolder3.mTvRightBottom.setText("+" + new DecimalFormat("#0.00").format(entity4.integral));
                    break;
                case TYPE_CHARGERECORD:
                    ChargeRecordEntity.ChargeRecordDetailEntity entity5 = (ChargeRecordEntity.ChargeRecordDetailEntity) entity.object;
                    BalanceViewHolder barValueHolder4 = (BalanceViewHolder) holder;
                    barValueHolder4.mTvLeftTop.setText("充值" + new DecimalFormat("#0.00").format(entity5.money) + "元" + "-" + entity5.orderNo);
                    barValueHolder4.mTvLeftBottom.setText(TextUtils.isEmpty(entity5.addTime) ? "" : entity5.addTime);
                    barValueHolder4.mTvLeftBottom.setTextColor(mContext.getResources().getColor(R.color.text_999999));
                    barValueHolder4.mTvLeftBottom.setTextSize(COMPLEX_UNIT_PX, 33);
                    barValueHolder4.mTvRightTop.setText(new DecimalFormat("#0.00").format((entity5.money + entity5.fee)));
                    barValueHolder4.mTvRightTop.setTextColor(mContext.getResources().getColor(R.color.text_333333));
                    barValueHolder4.mTvRightTop.setTextSize(COMPLEX_UNIT_PX, 39);
                    switch (entity5.payState) {
                        case 0:
                            barValueHolder4.mTvRightBottom.setText("待支付");
                            break;
                        case 1:
                            barValueHolder4.mTvRightBottom.setText("支付完成");
                            break;
                    }
                    barValueHolder4.mTvRightBottom.setTextColor(mContext.getResources().getColor(R.color.text_999999));
                    barValueHolder4.mTvRightBottom.setTextSize(COMPLEX_UNIT_PX, 33);
                    break;
                case TYPE_ADVERTISEMENT:
                    AdvRewardEntity entity6 = (AdvRewardEntity) entity.object;
                    BalanceViewHolder barValueHolder5 = (BalanceViewHolder) holder;
                    barValueHolder5.mTvLeftTop.setText(TextUtils.isEmpty(entity6.logDesc) ? "" : entity6.logDesc);
                    barValueHolder5.mTvLeftBottom.setText("余额: " + new DecimalFormat("#0.00").format(entity6.totalAmount));
                    barValueHolder5.mTvLeftBottom.setTextColor(mContext.getResources().getColor(R.color.text_999999));
                    barValueHolder5.mTvRightTop.setText(TextUtils.isEmpty(entity6.createTime) ? "" : entity6.createTime);
                    if (entity6.outAmount != 0)
                        barValueHolder5.mTvRightBottom.setText("+" + new DecimalFormat("#0.00").format(entity6.outAmount));
                    else if (entity6.inAmount != 0)
                        barValueHolder5.mTvRightBottom.setText("-" + new DecimalFormat("#0.00").format(entity6.inAmount));
                    else
                        barValueHolder5.mTvRightBottom.setText("+0.00");
                    break;
            }
        } catch (Exception ex) {
            LogUtils.e(ex.getMessage());
        }
        return view;
    }

    interface ViewHolder {
    }

    class BalanceViewHolder implements ViewHolder {

        @BindView(R.id.tv_left_top)
        TextView     mTvLeftTop;
        @BindView(R.id.tv_right_top)
        TextView     mTvRightTop;
        @BindView(R.id.tv_left_bottom)
        TextView     mTvLeftBottom;
        @BindView(R.id.tv_right_bottom)
        TextView     mTvRightBottom;
        @BindView(R.id.ll_item)
        LinearLayout mLlItem;

        BalanceViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    class RebPocketViewHolder implements ViewHolder {

        @BindView(R.id.ll_item)
        LinearLayout    mLlItem;
        @BindView(R.id.iv_icon)
        CircleImageView mIvIcon;
        @BindView(R.id.tv_nick_name)
        TextView        mTvNickName;
        @BindView(R.id.tv_money)
        TextView        mTvMoney;
        @BindView(R.id.tv_date)
        TextView        mTvDate;

        RebPocketViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
