package com.zhengdao.zqb.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.entity.QuestionClassifyEntity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author lijiangop
 * @CreateTime 2018/4/27 11:02
 */
public class QuestionClassifyAdapter extends RecyclerView.Adapter {
    private static int TYPE_TITLE = 1;
    private CallBack                          mCallBack;
    private Context                           mContext;
    private ArrayList<QuestionClassifyEntity> mData;
    private TitleViewHolder                   mTitleViewHolder;
    private ChildViewHolder                   mChildViewHolder;

    public interface CallBack {
        void onItemChange(int position);
    }

    public QuestionClassifyAdapter(Context context, ArrayList<QuestionClassifyEntity> data, CallBack callback) {
        this.mCallBack = callback;
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        if (viewType == TYPE_TITLE) {
            View header = View.inflate(mContext, R.layout.question_classify_title, null);
            holder = new TitleViewHolder(header);
        } else {
            View child = View.inflate(mContext, R.layout.question_classify_child, null);
            holder = new ChildViewHolder(child);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        try {
            final QuestionClassifyEntity bean = mData.get(position);
            if (bean.type == TYPE_TITLE) {
                mTitleViewHolder = (TitleViewHolder) holder;
                mTitleViewHolder.mTvTitle.setText(TextUtils.isEmpty(bean.title) ? "" : bean.title);
            } else {
                mChildViewHolder = (ChildViewHolder) holder;
                mChildViewHolder.mTvTitle.setText(TextUtils.isEmpty(bean.title) ? "" : bean.title);
                mChildViewHolder.mTvDesc.setText(TextUtils.isEmpty(bean.desc) ? "" : bean.desc);
                mChildViewHolder.mLlModule.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(bean.desc))
                            return;
                        if (mCallBack != null)
                            mCallBack.onItemChange(position);
                    }
                });
                if (bean.isShowDesc) {
                    mChildViewHolder.mTvDesc.setVisibility(View.VISIBLE);
                    mChildViewHolder.mIvArrow.setRotationX(180);
                } else {
                    mChildViewHolder.mTvDesc.setVisibility(View.GONE);
                    mChildViewHolder.mIvArrow.setRotationY(0);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mData == null ? 0 : mData.get(position).type;
    }

    class TitleViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_title)
        TextView mTvTitle;

        public TitleViewHolder(View header) {
            super(header);
            ButterKnife.bind(this, header);
        }
    }

    class ChildViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_title)
        TextView       mTvTitle;
        @BindView(R.id.iv_arrow)
        ImageView      mIvArrow;
        @BindView(R.id.ll_module)
        RelativeLayout mLlModule;
        @BindView(R.id.tv_desc)
        TextView       mTvDesc;

        public ChildViewHolder(View child) {
            super(child);
            ButterKnife.bind(this, child);
        }
    }
}
