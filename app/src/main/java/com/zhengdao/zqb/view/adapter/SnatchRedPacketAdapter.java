package com.zhengdao.zqb.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.entity.SnatchRedPackeEntity;
import com.zhengdao.zqb.utils.ViewUtils;
import com.zhengdao.zqb.view.activity.homegoodsdetail.HomeGoodsDetailActivity;
import com.zhengdao.zqb.view.activity.redpacketdetail.RedpacketDetailActivity;
import com.zhengdao.zqb.view.activity.snatchredpacket.SnatchRedPacketActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @Author lijiangop
 * @CreateTime 2018/1/15 17:47
 */
public class SnatchRedPacketAdapter extends SimpleAdapter {

    private PopupWindow                                     mPopupWindow;
    private SnatchRedPackeEntity.SnatchRedPackeDetailEntity mEntity;

    public SnatchRedPacketAdapter(Context context, List data) {
        super(context, data);
    }

    @Override
    protected View getItemView(int i, View convertView, ViewGroup viewGroup) {
        View view;
        final ViewHolder holder;
        if (convertView != null) {
            view = convertView;
            holder = (ViewHolder) convertView.getTag();
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_snatchredpacket, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        mEntity = (SnatchRedPackeEntity.SnatchRedPackeDetailEntity) mData.get(i);
        Glide.with(mContext).load(mEntity.imgPath).error(R.drawable.net_less_140).into(holder.mIvIcon);
        holder.mTvName.setText(TextUtils.isEmpty(mEntity.name) ? "" : mEntity.name);
        holder.mTvTime.setText(TextUtils.isEmpty(mEntity.time) ? "" : mEntity.time);
        holder.mTvNumber.setText(TextUtils.isEmpty(mEntity.number) ? "" : mEntity.number);
        holder.mLlItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRedPacket(holder.mLlItem);
            }
        });
        holder.mTvTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRedPacket(holder.mLlItem);
            }
        });
        return view;
    }

    private void showRedPacket(View view) {
        final SnatchRedPacketActivity context = (SnatchRedPacketActivity) mContext;
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.popup_red_packet, null);
        ImageView ivClose = contentView.findViewById(R.id.iv_close);
        CircleImageView ivCircleImg = contentView.findViewById(R.id.iv_icon);
        TextView tvName = contentView.findViewById(R.id.tv_name);
        TextView tvDesc = contentView.findViewById(R.id.tv_desc);
        TextView tvNumber = contentView.findViewById(R.id.tv_number);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPopupWindow != null)
                    mPopupWindow.dismiss();
            }
        });
        Glide.with(mContext).load(mEntity.imgPath).error(R.drawable.net_less_140).into(ivCircleImg);
        tvName.setText(TextUtils.isEmpty(mEntity.name) ? "" : mEntity.name);
        tvDesc.setText("全网最高价,不信你就比,只为开单");
        tvNumber.setText(TextUtils.isEmpty(mEntity.number) ? "" : mEntity.number);
        TextView tvFinishWanted = contentView.findViewById(R.id.tv_finish_wanted);
        TextView tvSeeDetail = contentView.findViewById(R.id.tv_see_detail);
        tvFinishWanted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, HomeGoodsDetailActivity.class);
                mContext.startActivity(intent);
            }
        });
        tvSeeDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RedpacketDetailActivity.class);
                mContext.startActivity(intent);
            }
        });
        mPopupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        mPopupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ViewUtils.backgroundAlpha(context, 1f);
            }
        });
        ViewUtils.backgroundAlpha(context, 0.5f);
    }

    public void onDestory() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
            mPopupWindow = null;
        }
    }

    class ViewHolder {
        @BindView(R.id.iv_icon)
        CircleImageView mIvIcon;
        @BindView(R.id.tv_name)
        TextView        mTvName;
        @BindView(R.id.tv_time)
        TextView        mTvTime;
        @BindView(R.id.tv_number)
        TextView        mTvNumber;
        @BindView(R.id.ll_item)
        LinearLayout    mLlItem;
        @BindView(R.id.tv_tag)
        TextView        mTvTag;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
