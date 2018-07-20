package com.zhengdao.zqb.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.KindOfWantedEntity;
import com.zhengdao.zqb.view.activity.kindofwanteddetail.KindOfWantedDetailActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @Author lijiangop
 * @CreateTime 2018/1/12 17:25
 */
public class KindOfWantedAdapter extends SimpleAdapter {

    private final int mType;

    public KindOfWantedAdapter(Context context, List data, int type) {
        super(context, data);
        mType = type;
    }

    @Override
    protected View getItemView(int i, View convertView, ViewGroup viewGroup) {
        View view;
        ViewHolder holder;
        if (convertView != null) {
            view = convertView;
            holder = (ViewHolder) convertView.getTag();
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_kindofwanted, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        final KindOfWantedEntity entity = (KindOfWantedEntity) mData.get(i);
        holder.mLlItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mType != 0) {
                    Intent intent = new Intent(mContext, KindOfWantedDetailActivity.class);
                    intent.putExtra(Constant.Activity.Data, entity.id);
                    intent.putExtra(Constant.Activity.Type, mType);
                    mContext.startActivity(intent);
                }
            }
        });
        Glide.with(mContext).load(entity.user.avatar).error(R.drawable.net_less_140).into(holder.mIvIcon);
        holder.mTvNickName.setText(TextUtils.isEmpty(entity.user.nickName) ? "" : entity.user.nickName);
        holder.mTvContent.setText(TextUtils.isEmpty(entity.reward.title) ? "" : entity.reward.title);
        if (mType == 2) {//审核不通过的悬赏
            if (!TextUtils.isEmpty(entity.remarks)) {
                holder.mTvState.setVisibility(View.VISIBLE);
                holder.mTvState.setText(TextUtils.isEmpty(entity.remarks) ? "" : entity.remarks);
            }
            holder.mTvLessTime.setText(TextUtils.isEmpty(entity.confirmtime) ? "" : entity.confirmtime);
        } else if (mType == 1) {//急需审核的悬赏
            // 时间的显示确认下
            String value = secondToTime(entity.surplusTime / 1000);
            holder.mTvLessTime.setText("还剩" + value + "失效");
        } else if (mType == 0) {//领取未提交的悬赏
            if (entity.surplusTime == -1) {
                holder.mTvLessTime.setText("已失效");
            } else {
                String value = secondToTime(entity.surplusTime / 1000);
                //                holder.mTvLessTime.setText("还剩" + entity.surplusTime / (1000 * 60) + "分钟失效");
                holder.mTvLessTime.setText("还剩" + value + "失效");
            }
        }
        return view;
    }

    public String secondToTime(long second) {
        long days = second / 86400;            //转换天数
        second = second % 86400;            //剩余秒数
        long hours = second / 3600;            //转换小时
        second = second % 3600;                //剩余秒数
        long minutes = second / 60;            //转换分钟
        second = second % 60;                //剩余秒数
        if (days > 0) {
            return days + "天" + hours + "小时" + minutes + "分" + second + "秒";
        } else {
            return hours + "小时" + minutes + "分" + second + "秒";
        }
    }

    class ViewHolder {
        @BindView(R.id.iv_icon)
        CircleImageView mIvIcon;
        @BindView(R.id.tv_nick_name)
        TextView        mTvNickName;
        @BindView(R.id.tv_less_time)
        TextView        mTvLessTime;
        @BindView(R.id.tv_content)
        TextView        mTvContent;
        @BindView(R.id.tv_state)
        TextView        mTvState;
        @BindView(R.id.ll_item)
        LinearLayout    mLlItem;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
