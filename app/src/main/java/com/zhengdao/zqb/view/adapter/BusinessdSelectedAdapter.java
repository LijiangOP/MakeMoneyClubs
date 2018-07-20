package com.zhengdao.zqb.view.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.entity.ScreenLoadEntity;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author lijiangop
 * @CreateTime 2018/1/29 17:01
 */
public class BusinessdSelectedAdapter extends CommonAdapter<ScreenLoadEntity.ScreenLoadDetailEntity> {

    private ArrayList<Boolean>   isSelectedList;
    private ItemSelectedCallBack itemSelectedCallBack;

    public interface ItemSelectedCallBack {
        void businessItemIsSelected(int id);
    }

    public BusinessdSelectedAdapter(Context context, int layoutId, List<ScreenLoadEntity.ScreenLoadDetailEntity> datas,
                                    ItemSelectedCallBack callback, int type) {
        super(context, layoutId, datas);
        itemSelectedCallBack = callback;
        isSelectedList = new ArrayList<>();
        for (int i = 0; i < datas.size(); i++) {
            if (datas.get(i).id == type) {
                isSelectedList.add(true);
            } else
                isSelectedList.add(false);
        }
    }

    @Override
    protected void convert(ViewHolder holder, final ScreenLoadEntity.ScreenLoadDetailEntity screenLoadDetailEntity, final int position) {
        try {
            final TextView textView = holder.getView(R.id.text);
            textView.setText(TextUtils.isEmpty(screenLoadDetailEntity.value) ? "" : screenLoadDetailEntity.value);
            if (isSelectedList.get(position)) {
                textView.setTextColor(mContext.getResources().getColor(R.color.color_fc3135));
                textView.setBackground(mContext.getResources().getDrawable(R.drawable.shape_rect_gray_three_red));
            } else {
                textView.setTextColor(mContext.getResources().getColor(R.color.color_000000));
                textView.setBackground(mContext.getResources().getDrawable(R.drawable.shape_rect_gray_three_gray));
            }
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < isSelectedList.size(); i++) {
                        if (i == position) {
                            isSelectedList.set(i, !isSelectedList.get(position));
                        } else {
                            isSelectedList.set(i, false);
                        }
                    }
                    if (isSelectedList.get(position))
                        itemSelectedCallBack.businessItemIsSelected(screenLoadDetailEntity.id);
                    else
                        itemSelectedCallBack.businessItemIsSelected(-1);
                    notifyDataSetChanged();
                }
            });
        } catch (Exception ex) {
            LogUtils.e(ex.getMessage());
        }
    }
}
