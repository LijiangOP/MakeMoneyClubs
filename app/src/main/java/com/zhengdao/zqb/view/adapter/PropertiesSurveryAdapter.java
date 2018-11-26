package com.zhengdao.zqb.view.adapter;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.entity.DictionaryValue;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author lijiangop
 * @CreateTime 2018/10/17 0017 14:24
 */
public class PropertiesSurveryAdapter extends CommonAdapter<DictionaryValue> {
    private int                mType;
    private ArrayList<Integer> mSeclectList;
    private long               mPreClickTime;

    public ArrayList<Integer> getSeclectList() {
        return mSeclectList;
    }

    public PropertiesSurveryAdapter(Context context, int layoutId, List datas, int type) {
        super(context, layoutId, datas);
        mType = type;
        mSeclectList = new ArrayList<>();
    }

    @Override
    protected void convert(ViewHolder holder, final DictionaryValue entity, final int position) {
        try {
            TextView mTvDesc = holder.getView(R.id.tv_type);
            mTvDesc.setText(TextUtils.isEmpty(entity.value) ? "" : entity.value);
            if (mSeclectList.contains(position)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mTvDesc.setBackground(mContext.getResources().getDrawable(R.drawable.shape_rect_selected_red));
                }
                mTvDesc.setTextColor(mContext.getResources().getColor(R.color.white));
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mTvDesc.setBackground(mContext.getResources().getDrawable(R.drawable.shape_balance_type));
                }
                mTvDesc.setTextColor(mContext.getResources().getColor(R.color.color_000000));
            }
            mTvDesc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (System.currentTimeMillis() > mPreClickTime + 500) {
                            if (mType == 2) {
                                mSeclectList.clear();
                                mSeclectList.add(position);
                            } else {
                                if (mSeclectList.contains(position)) {
                                    mSeclectList.remove(position);
                                } else {
                                    mSeclectList.add(position);
                                }
                            }
                            notifyDataSetChanged();
                        }
                        mPreClickTime = System.currentTimeMillis();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception ex) {
            LogUtils.e(ex.getMessage());
        }
    }
}
