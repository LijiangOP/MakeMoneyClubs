package com.zhengdao.zqb.view.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author lijiangop
 * @CreateTime 2018/6/11 16:09
 */
public class BalanceTypeAdapter extends CommonAdapter {

    private CallBack           mCallBack;
    private ArrayList<Boolean> isSelectedList;

    public BalanceTypeAdapter(Context context, int layoutId, List datas, CallBack callBack, String CurrentPositionString) {
        this(context, layoutId, datas);
        this.mCallBack = callBack;
        isSelectedList = new ArrayList<>();
        for (int i = 0; i < datas.size(); i++) {
            if (datas.get(i).equals(CurrentPositionString)) {
                isSelectedList.add(true);
            } else
                isSelectedList.add(false);
        }
    }

    public BalanceTypeAdapter(Context context, int layoutId, List datas) {
        super(context, layoutId, datas);
    }

    public interface CallBack {
        void doItemClick(int position);
    }

    @Override
    protected void convert(ViewHolder holder, Object o, final int position) {
        try {
            String value = (String) mDatas.get(position);
            TextView mTvDesc = holder.getView(R.id.tv_type);
            mTvDesc.setText(TextUtils.isEmpty(value) ? "" : value);
            if (isSelectedList.get(position)) {
                mTvDesc.setBackground(mContext.getResources().getDrawable(R.drawable.bg_balance_type));
            } else {
                mTvDesc.setBackground(mContext.getResources().getDrawable(R.drawable.shape_balance_type));
            }
            mTvDesc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallBack != null)
                        mCallBack.doItemClick(position);
                    for (int i = 0; i < isSelectedList.size(); i++) {
                        if (i == position) {
                            isSelectedList.set(i, true);
                        } else {
                            isSelectedList.set(i, false);
                        }
                    }
                    notifyDataSetChanged();
                }
            });
        } catch (Exception ex) {
            LogUtils.e(ex.getMessage());
        }
    }

}
