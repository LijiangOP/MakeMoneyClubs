package com.zhengdao.zqb.view.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.TextView;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.entity.WithDrawSelectEntity;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author lijiangop
 * @CreateTime 2018/7/26 09:50
 */
public class WithDrawSelectedAdapter extends CommonAdapter<WithDrawSelectEntity> {

    private ItemSelectedCallBack mCallBack;
    private ArrayList<Boolean>   isSelectedList;

    public interface ItemSelectedCallBack {
        void onItemSelected(Integer value);

        void onItemClick(boolean type, Integer value);
    }

    public WithDrawSelectedAdapter(Context context, int layoutId, List<WithDrawSelectEntity> datas, ItemSelectedCallBack itemSelectedCallBack) {
        super(context, layoutId, datas);
        this.mCallBack = itemSelectedCallBack;
        isSelectedList = new ArrayList<>();
        for (int i = 0; i < datas.size(); i++) {
            isSelectedList.add(false);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void convert(ViewHolder viewHolder, final WithDrawSelectEntity entity, final int position) {
        try {
            final TextView textView = viewHolder.getView(R.id.text);
            textView.setText(entity.value + "元");
            if (entity.state) {
                if (isSelectedList.get(position)) {
                    textView.setTextColor(mContext.getResources().getColor(R.color.color_fc3135));
                    textView.setBackground(mContext.getResources().getDrawable(R.drawable.shape_rect_white_three_red));
                } else {
                    textView.setTextColor(mContext.getResources().getColor(R.color.color_000000));
                    textView.setBackground(mContext.getResources().getDrawable(R.drawable.shape_rect_white_three_gray));
                }
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < isSelectedList.size(); i++) {
                            if (i == position) {
                                isSelectedList.set(i, true);
                            } else {
                                isSelectedList.set(i, false);
                            }
                        }
                        mCallBack.onItemSelected(entity.value);
                        notifyDataSetChanged();
                    }
                });
            } else {
                textView.setTextColor(mContext.getResources().getColor(R.color.color_000000));
                textView.setBackground(mContext.getResources().getDrawable(R.drawable.shape_rect_gray_there_gray));
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCallBack.onItemClick(entity.state, entity.value);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
