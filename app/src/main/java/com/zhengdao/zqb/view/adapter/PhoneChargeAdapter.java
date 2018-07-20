package com.zhengdao.zqb.view.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.entity.PhoneChargeDetailEntity;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

/**
 * @Author lijiangop
 * @CreateTime 2018/2/2 15:35
 */
public class PhoneChargeAdapter extends CommonAdapter<PhoneChargeDetailEntity> {
    private onItemClickCallBack callBack;

    public interface onItemClickCallBack {
        void onItemClick(int position);
    }

    public PhoneChargeAdapter(Context context, int layoutId, List<PhoneChargeDetailEntity> datas, onItemClickCallBack callBack) {
        this(context, layoutId, datas);
        this.callBack = callBack;
    }

    private PhoneChargeAdapter(Context context, int layoutId, List<PhoneChargeDetailEntity> datas) {
        super(context, layoutId, datas);
    }

    @Override
    protected void convert(ViewHolder holder, PhoneChargeDetailEntity phoneChargeDetailEntity, final int position) {
        LinearLayout llItem = holder.getView(R.id.ll_item);
        TextView tvValue = holder.getView(R.id.tv_value);
        TextView tvPrice = holder.getView(R.id.tv_price);
        tvValue.setText(TextUtils.isEmpty(phoneChargeDetailEntity.value) ? "" : phoneChargeDetailEntity.value + "元");
        tvPrice.setText(TextUtils.isEmpty(phoneChargeDetailEntity.price) ? "" : "售价：" + phoneChargeDetailEntity.price + "元");
        llItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBack != null)
                    callBack.onItemClick(position);
            }
        });
    }
}
