package com.zhengdao.zqb.view.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.entity.SynthesizeEntity;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

/**
 * @Author lijiangop
 * @CreateTime 2018/4/28 14:26
 */
public class EarnFragmentSynthesizeAdapter extends CommonAdapter<SynthesizeEntity> {

    private CallBack callBack;

    public interface CallBack {
        void doItemClick(int position);
    }

    public EarnFragmentSynthesizeAdapter(Context context, int layoutId, List datas, CallBack callBack) {
        this(context, layoutId, datas);
        this.callBack = callBack;
    }

    public EarnFragmentSynthesizeAdapter(Context context, int layoutId, List datas) {
        super(context, layoutId, datas);
    }

    @Override
    protected void convert(ViewHolder holder, SynthesizeEntity synthesizeEntity, final int position) {
        try {
            final TextView mTvDesc = holder.getView(R.id.tv_desc);
            final ImageView mIvSelected = holder.getView(R.id.iv_selected);
            final LinearLayout mLlItem = holder.getView(R.id.ll_item);
            mTvDesc.setText(TextUtils.isEmpty((synthesizeEntity.desc)) ? "" : synthesizeEntity.desc);
            if (synthesizeEntity.isSelected) {
                mIvSelected.setVisibility(View.VISIBLE);
                mTvDesc.setTextColor(mContext.getResources().getColor(R.color.main));
            } else {
                mIvSelected.setVisibility(View.GONE);
                mTvDesc.setTextColor(mContext.getResources().getColor(R.color.text_333333));
            }
            if (position == mDatas.size() - 1)
                mLlItem.setBackground(mContext.getResources().getDrawable(R.drawable.shape_only_bottom_gray));
            mLlItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callBack != null)
                        callBack.doItemClick(position);
                }
            });
        } catch (Exception ex) {
            LogUtils.e(ex.getMessage());
        }
    }
}
