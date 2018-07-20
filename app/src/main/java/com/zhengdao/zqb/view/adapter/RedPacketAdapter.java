package com.zhengdao.zqb.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhengdao.zqb.R;

import java.util.ArrayList;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @Author lijiangop
 * @CreateTime 2018/1/10 11:04
 */
public class RedPacketAdapter extends SimpleAdapter {

    public RedPacketAdapter(Context context, ArrayList data) {
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
            view = LayoutInflater.from(mContext).inflate(R.layout.item_redpacket, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }


        return view;
    }

    class ViewHolder {

        @BindView(R.id.iv_icon)
        CircleImageView mIvIcon;
        @BindView(R.id.tv_nick_name)
        TextView        mTvNickName;
        @BindView(R.id.tv_money)
        TextView        mTvMoney;
        @BindView(R.id.tv_date)
        TextView        mTvDate;

        public ViewHolder(View view) {
            super();
        }
    }
}
