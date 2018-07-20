package com.zhengdao.zqb.view.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.entity.Recommends;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author lijiangop
 * @CreateTime 2018/1/15 15:46
 */
public class InvitedFriendsListAdapter extends SimpleAdapter {

    public InvitedFriendsListAdapter(Context context, List data) {
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
            view = LayoutInflater.from(mContext).inflate(R.layout.item_invitedfriends_list, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        Recommends.RecommendDetailEntity entity = (Recommends.RecommendDetailEntity) mData.get(i);
        holder.mTvNickName.setText(TextUtils.isEmpty(entity.nickName) ? "" : entity.nickName);
        //        holder.mTvCertifyState.setText(TextUtils.isEmpty(entity.isCertify) ? "" : entity.isCertify);
        holder.mTvRegistTime.setText(TextUtils.isEmpty(entity.createTime) ? "" : "注册于" + entity.createTime);
        switch (entity.userType) {
            case 0:
                holder.mTvRegistPlatform.setText("来自 微站");
                break;
            case 1:
                holder.mTvRegistPlatform.setText("来自 安卓");
                break;
        }
        return view;
    }

    class ViewHolder {

        @BindView(R.id.tv_nick_name)
        TextView     mTvNickName;
        @BindView(R.id.tv_certify_state)
        TextView     mTvCertifyState;
        @BindView(R.id.tv_regist_time)
        TextView     mTvRegistTime;
        @BindView(R.id.tv_regist_platform)
        TextView     mTvRegistPlatform;
        @BindView(R.id.ll_item)
        LinearLayout mLlItem;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
