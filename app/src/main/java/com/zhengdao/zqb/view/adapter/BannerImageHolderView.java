package com.zhengdao.zqb.view.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bigkoo.convenientbanner.holder.Holder;
import com.bumptech.glide.Glide;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.entity.BannerBean;


/**
 * @创建者 cairui
 * @创建时间 2016/11/28 16:48
 * @描述 ${des}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @版本 $Revision$
 * @更新描述 ${des}
 */
public class BannerImageHolderView implements Holder<BannerBean> {
    private ImageView           imageView;
    private ImageView.ScaleType mScaleType;
    private Context             mContext;

    public BannerImageHolderView(ImageView.ScaleType scaleType) {
        this.mScaleType = scaleType;
    }

    @Override
    public View createView(Context context) {
        this.mContext = context;
        imageView = new ImageView(context);
        imageView.setScaleType(mScaleType);
        return imageView;
    }


    @Override
    public void UpdateUI(Context context, int position, BannerBean data) {
        Glide.with(context).load(data.imgPath).error(R.drawable.net_less_140).into(imageView);
    }
}
