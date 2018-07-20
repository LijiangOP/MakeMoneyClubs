package com.zhengdao.zqb.view.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fynn.fluidlayout.FluidLayout;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.ScanInfoDetailBean;
import com.zhengdao.zqb.utils.LogUtils;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author lijiangop
 * @CreateTime 2018/1/15 16:59
 */
public class MyBrowsingHistoryAdapter extends SimpleAdapter {

    private boolean mIsEdit = false;
    private allCheckCallBack callBack;
    private ViewHolder       mViewHolder;

    public interface allCheckCallBack {
        void allChecked(boolean b);
    }

    public MyBrowsingHistoryAdapter(Context context, List data) {
        this(context, data, null);
    }

    public MyBrowsingHistoryAdapter(Context context, List data, allCheckCallBack callBack) {
        super(context, data);
        this.callBack = callBack;
    }

    @Override
    protected View getItemView(final int position, View convertView, ViewGroup viewGroup) {
        View view;
        if (convertView != null) {
            view = convertView;
            mViewHolder = (ViewHolder) convertView.getTag();
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_browsinghistory, viewGroup, false);
            mViewHolder = new ViewHolder(view);
            view.setTag(mViewHolder);
        }
        ScanInfoDetailBean entity = (ScanInfoDetailBean) mData.get(position);
        mViewHolder.mCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    Constant.Data.BrowsingHistoryEditSet.add(position);
                else if (Constant.Data.BrowsingHistoryEditSet.contains(position))
                    Constant.Data.BrowsingHistoryEditSet.remove(position);
                if (Constant.Data.BrowsingHistoryEditSet.size() == mData.size() && callBack != null)
                    callBack.allChecked(true);
                else if (Constant.Data.BrowsingHistoryEditSet.size() < mData.size() && callBack != null)
                    callBack.allChecked(false);
            }
        });
        mViewHolder.mCheckbox.setVisibility(View.GONE);
        mViewHolder.mLlLucency.setVisibility(View.GONE);
        mViewHolder.mCheckbox.setChecked(Constant.Data.BrowsingHistoryEditSet.contains(position));
        if (entity.isShowCb) {
            if (mIsEdit) {
                mViewHolder.mCheckbox.setVisibility(View.VISIBLE);
            } else {
                mViewHolder.mCheckbox.setVisibility(View.GONE);
            }
        } else {
            mViewHolder.mCheckbox.setVisibility(View.GONE);
        }
        Glide.with(mContext).load(entity.entity.picture).error(R.drawable.net_less_140).into(mViewHolder.mIvIcon);
        if (!TextUtils.isEmpty(entity.entity.stateName) && entity.entity.stateName.equals("已结束"))
            mViewHolder.mLlLucency.setVisibility(View.VISIBLE);
        mViewHolder.mTvContent.setText(TextUtils.isEmpty(entity.entity.title) ? "" : entity.entity.title);
        mViewHolder.mTvJoinNum.setText(entity.entity.joincount + "人参与");
        mViewHolder.mTvPrice.setText(new DecimalFormat("#0.00").format(entity.entity.money));
        mViewHolder.mFluidLayout.removeAllViews();
        if (!TextUtils.isEmpty(entity.entity.keyword)) {
            if (entity.entity.keyword.contains(",")) {
                String[] split = entity.entity.keyword.split(",");
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
                addKeyword(entity.entity.keyword);
            }
        }
        return view;
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

    public void showEdit(boolean isEdit) {
        mIsEdit = isEdit;
    }

    public void isAllChechked(boolean isAllChechked) {
        if (isAllChechked) {
            if (mData != null)
                for (int i = 0; i < mData.size(); i++) {
                    Constant.Data.BrowsingHistoryEditSet.add(i);
                }
        } else
            Constant.Data.BrowsingHistoryEditSet.clear();
        LogUtils.i("选中集合大小为" + Constant.Data.BrowsingHistoryEditSet.size());
    }

    class ViewHolder {
        @BindView(R.id.checkbox)
        CheckBox     mCheckbox;
        @BindView(R.id.iv_icon)
        ImageView    mIvIcon;
        @BindView(R.id.ll_lucency)
        LinearLayout mLlLucency;
        @BindView(R.id.tv_content)
        TextView     mTvContent;
        @BindView(R.id.fluid_layout)
        FluidLayout  mFluidLayout;
        @BindView(R.id.tv_price)
        TextView     mTvPrice;
        @BindView(R.id.tv_join_num)
        TextView     mTvJoinNum;
        @BindView(R.id.ll_recommend)
        LinearLayout mLlRecommend;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
