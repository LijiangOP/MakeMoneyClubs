package com.zhengdao.zqb.view.adapter;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.entity.RankingListEntity;
import com.zhengdao.zqb.utils.LogUtils;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @Author lijiangop
 * @CreateTime 2018/1/18 16:34
 */
public class RankingListAdapter extends RecyclerView.Adapter {
    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_FOOTER = 1;
    private View    VIEW_FOOTER;
    private Context mContext;
    private List    mData;

    public RankingListAdapter(Context context, List data) {
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER)
            return new MyHolder(VIEW_FOOTER);
        else
            return new MyHolder(LayoutInflater.from(mContext).inflate(R.layout.item_list_layout, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (!isFooterView(position)) {
            MyHolder myHolder = (MyHolder) holder;
            RankingListEntity.AllUser item = (RankingListEntity.AllUser) mData.get(position);
            try {
                switch (Integer.valueOf(item.number)) {
                    case 1:
                        myHolder.mTvRanking.setText("");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                            myHolder.mTvRanking.setBackground(mContext.getResources().getDrawable(R.drawable.img_jin));
                        break;
                    case 2:
                        myHolder.mTvRanking.setText("");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                            myHolder.mTvRanking.setBackground(mContext.getResources().getDrawable(R.drawable.img_yin));
                        break;
                    case 3:
                        myHolder.mTvRanking.setText("");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                            myHolder.mTvRanking.setBackground(mContext.getResources().getDrawable(R.drawable.img_tong));
                        break;
                    default:
                        myHolder.mTvRanking.setText(TextUtils.isEmpty(item.number) ? "" : item.number);
                        myHolder.mTvRanking.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                        break;
                }
                Glide.with(mContext).load(item.avatar).error(R.drawable.default_icon).into(myHolder.mIvIcon);
                myHolder.mTvName.setText(TextUtils.isEmpty(item.nickName) ? "" : item.nickName);
                myHolder.mTvPrice.setText("" + new DecimalFormat("#0.00").format(item.sunMoney));
            } catch (Exception e) {
                LogUtils.i(e.getMessage());
            }
        }
    }

    @Override
    public int getItemCount() {
        int count = (mData == null ? 0 : mData.size());
        if (VIEW_FOOTER != null) {
            count++;
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if (isFooterView(position)) {
            return TYPE_FOOTER;
        } else {
            return TYPE_NORMAL;
        }
    }

    public void addFooterView(View footerView) {
        if (haveFooterView()) {
            throw new IllegalStateException("footerView has already exists!");
        } else {
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            footerView.setLayoutParams(params);
            VIEW_FOOTER = footerView;
            notifyItemInserted(getItemCount() - 1);
        }
    }

    public boolean haveFooterView() {
        return VIEW_FOOTER != null;
    }

    private boolean isFooterView(int position) {
        return haveFooterView() && position == getItemCount() - 1;
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_ranking)
        TextView        mTvRanking;
        @BindView(R.id.iv_icon)
        CircleImageView mIvIcon;
        @BindView(R.id.tv_name)
        TextView        mTvName;
        @BindView(R.id.tv_price)
        TextView        mTvPrice;

        public MyHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}