package com.zhengdao.zqb.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.entity.MessageEntity;
import com.zhengdao.zqb.utils.LogUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author lijiangop
 * @CreateTime 2017/12/29 15:55
 */
public class MessageAdapter extends SimpleAdapter {

    public MessageAdapter(Context context, List data) {
        super(context, data);
    }

    @Override
    protected View getItemView(int i, View convertView, ViewGroup viewGroup) {
        View view;
        ViewHolder holder;
        if (convertView != null) {
            view = convertView;
            holder = (ViewHolder) convertView.getTag();
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_message, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        final MessageEntity.MessageDeatilEntity messageBean = (MessageEntity.MessageDeatilEntity) mData.get(i);
        if (messageBean == null)
            return view;
        try {
            if (!TextUtils.isEmpty(messageBean.comFromName)) {
                SpannableString spannableString = new SpannableString("来自 " + messageBean.comFromName);
                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#56b9f8")), 3, 3 + messageBean.comFromName.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                holder.mTvItemMessageType.setText(spannableString);
            }
            holder.mTvItemMessageTitle.setText(TextUtils.isEmpty(messageBean.title) ? "" : messageBean.title);
            holder.mTvItemMessageContent.setText(TextUtils.isEmpty(messageBean.content) ? "" : messageBean.content);
            holder.mTvItemMessageTime.setText(TextUtils.isEmpty(messageBean.createTime) ? "" : messageBean.createTime);
            holder.mRlItemMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LogUtils.i("点击了整个条目");
                }
            });
        } catch (Exception e) {
            LogUtils.e(e.getMessage());
        }
        return view;
    }

    class ViewHolder {
        @BindView(R.id.tv_item_message_title)
        TextView     mTvItemMessageTitle;
        @BindView(R.id.tv_item_message_time)
        TextView     mTvItemMessageTime;
        @BindView(R.id.tv_item_message_content)
        TextView     mTvItemMessageContent;
        @BindView(R.id.tv_item_message_type)
        TextView     mTvItemMessageType;
        @BindView(R.id.rl_item_message)
        LinearLayout mRlItemMessage;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
