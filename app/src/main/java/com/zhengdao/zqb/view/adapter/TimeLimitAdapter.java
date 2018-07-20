package com.zhengdao.zqb.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.TimeLimit;
import com.zhengdao.zqb.utils.DensityUtil;
import com.zhengdao.zqb.utils.Utils;
import com.zhengdao.zqb.view.activity.homegoodsdetail.HomeGoodsDetailActivity;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.text.DecimalFormat;
import java.util.List;

/**
 * @Author lijiangop
 * @CreateTime 2018/4/17 18:07
 */
public class TimeLimitAdapter extends CommonAdapter<TimeLimit> {

    private int[] mScreenSize;
    private int   mInt;

    public TimeLimitAdapter(Context context, int layoutId, List<TimeLimit> datas) {
        super(context, layoutId, datas);
        mInt = mContext.getResources().getDimensionPixelOffset(R.dimen.home_limit_task_left);
        int distance = mContext.getResources().getDimensionPixelOffset(R.dimen.home_limit_task_child);
        mInt = mInt * 2 + distance * 3;
        //        int dimensionPixelOffset = mContext.getResources().getDimensionPixelOffset(R.dimen.home_limit_task_left);
        //        mInt = DensityUtil.dip2px(mContext, dimensionPixelOffset);
    }

    @Override
    protected void convert(ViewHolder holder, final TimeLimit timeLimit, int position) {
        try {
            ImageView iv = holder.getView(R.id.iv_goods);
            TextView tv = holder.getView(R.id.tv_detail);
            LinearLayout ll = holder.getView(R.id.ll_item);
            if (mScreenSize == null)
                mScreenSize = DensityUtil.getScreenSize(mContext);
            ViewGroup.LayoutParams layoutParams = iv.getLayoutParams();
            layoutParams.width = (mScreenSize[0] - mInt) / 4;
            layoutParams.height = (mScreenSize[0] - mInt) / 4;
            if (layoutParams.width != 0 && layoutParams.height != 0)
                iv.setLayoutParams(layoutParams);
            Glide.with(mContext).load(timeLimit.picture).error(R.drawable.net_less_36).skipMemoryCache(true).into(iv);
            Double money = timeLimit.money;
            tv.setText("赚¥" + new DecimalFormat("#0.00").format(money));
            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (timeLimit.id != 0) {
                        Intent intent = new Intent(mContext, HomeGoodsDetailActivity.class);
                        intent.putExtra(Constant.Activity.Data, timeLimit.id);
                        Utils.StartActivity(mContext, intent);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
