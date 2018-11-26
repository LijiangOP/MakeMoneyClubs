package com.zhengdao.zqb.view.activity.homegoodsdetail;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;

/**
 * @Author lijiangop
 * @CreateTime 2018/9/1 0001 16:08
 */
public class TimeAxleDecoration extends RecyclerView.ItemDecoration {

    private Context mContext;
    private Paint   mPaint;
    private int     itemView_leftinterval;
    private Bitmap  mIcon;

    // 在构造函数里进行绘制的初始化，如画笔属性设置等
    public TimeAxleDecoration(Context context) {
        // 轴点画笔(红色)
        mContext = context;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(2);
        mPaint.setColor(Color.parseColor("#F13041"));
        itemView_leftinterval = Constant.Flow.LeftMargin;
        mIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.home_detail_flow_point);
    }

    // 重写getItemOffsets（）方法
    // 作用：设置ItemView 左 & 上偏移长度
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        // 设置ItemView的左 & 上偏移长度分别为200 px & 50px,即此为onDraw()可绘制的区域
        outRect.set(itemView_leftinterval, 0, 0, 0);

    }

    // 重写onDraw（）
    // 作用:在间隔区域里绘制时光轴线 & 时间文本
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        // 获取RecyclerView的Child view的个数
        int childCount = parent.getChildCount();
        // 遍历每个Item，分别获取它们的位置信息，然后再绘制对应的分割线
        for (int i = 0; i < childCount; i++) {

            // 获取每个Item对象
            View child = parent.getChildAt(i);

            /**
             * 绘制轴点
             */
            float centerx = child.getLeft() - mIcon.getWidth();
            float centery = child.getTop();

            // 通过Canvas绘制角标
            c.drawBitmap(mIcon, centerx - mIcon.getWidth() / 2, centery + mIcon.getHeight() / 2, mPaint);

            /**
             * 绘制轴线
             */
            float bottomLine_up_x = centerx;
            float bottom_up_y = centery + mIcon.getHeight();

            int bottom;
            if (i == childCount - 1) {
                child.getPaddingBottom();
                bottom = child.getBottom() - child.getPaddingBottom();
            } else
                bottom = child.getBottom() + mIcon.getHeight();
            while (bottom_up_y < bottom) {
                c.drawLine(bottomLine_up_x - 5, bottom_up_y, bottomLine_up_x, bottom_up_y + 7, mPaint);
                c.drawLine(bottomLine_up_x + 5, bottom_up_y, bottomLine_up_x, bottom_up_y + 7, mPaint);
                bottom_up_y = bottom_up_y + 20;
            }
        }
    }
}
