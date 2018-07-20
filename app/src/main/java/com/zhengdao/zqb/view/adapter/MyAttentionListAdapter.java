package com.zhengdao.zqb.view.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.customview.SwipeBackActivity.CommonDialog;
import com.zhengdao.zqb.entity.AttentionEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @Author lijiangop
 * @CreateTime 2018/1/15 16:11
 */
public class MyAttentionListAdapter extends SimpleAdapter {

    private static final int REQUEST_CODE_FIRST = 1;
    private CancleAttentionCallBack cancleAttentionCallBack;
    private CommonDialog            mCommonDialog;

    public interface CancleAttentionCallBack {
        void cancleAttention(int fid);
    }

    public MyAttentionListAdapter(Context context, List data, CancleAttentionCallBack callBack) {
        super(context, data);
        cancleAttentionCallBack = callBack;
    }

    @Override
    protected View getItemView(int i, View convertView, ViewGroup viewGroup) {
        View view;
        final ViewHolder holder;
        if (convertView != null) {
            view = convertView;
            holder = (ViewHolder) convertView.getTag();
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_my_attention, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        final AttentionEntity.AttentionDetailEntity entity = (AttentionEntity.AttentionDetailEntity) mData.get(i);
        Glide.with(mContext).load(entity.avatar).error(R.drawable.net_less_36).into(holder.mIvIcon);
        holder.mTvNickName.setText(TextUtils.isEmpty(entity.nickName) ? "" : entity.nickName);
        holder.mTvState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog(entity.fid);
            }
        });
        return view;
    }

    private void showConfirmDialog(final int fid) {
        if (mCommonDialog == null)
            mCommonDialog = new CommonDialog(mContext);
        mCommonDialog.initView("是否取消关注", "", "取消关注", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCommonDialog != null)
                    mCommonDialog.dismiss();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancleAttentionCallBack.cancleAttention(fid);
                if (mCommonDialog != null)
                    mCommonDialog.dismiss();
            }
        });
        mCommonDialog.setTitleSize(15);
        mCommonDialog.show();
    }

    class ViewHolder {

        @BindView(R.id.iv_icon)
        CircleImageView mIvIcon;
        @BindView(R.id.tv_nick_name)
        TextView        mTvNickName;
        @BindView(R.id.tv_state)
        TextView        mTvState;
        @BindView(R.id.ll_item)
        LinearLayout    mLlItem;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
