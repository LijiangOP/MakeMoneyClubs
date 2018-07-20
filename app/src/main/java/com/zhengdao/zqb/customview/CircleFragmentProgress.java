package com.zhengdao.zqb.customview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.utils.DensityUtil;


/**
 * @Author lijiangop
 * @CreateTime 2018/6/27 10:03
 */
public class CircleFragmentProgress extends View {

    private Context mContext;
    // 刻度画笔
    private Paint   mScalePaint;
    // 小原点画笔
    private Paint   mDotPaint;
    // 文字画笔
    private Paint   mTextPaint;
    // 当前进度
    private int progress = 0;
    // 圆圈背景色
    private int           baseColor;
    // 进度条颜色
    private int           indexColor;
    private int           textColor;
    private int           textSize;
    private int           mWidth;
    private int           mHeight;
    private ValueAnimator animator;

    public CircleFragmentProgress(Context context) {
        this(context, null);
    }

    public CircleFragmentProgress(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleFragmentProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray tya = context.obtainStyledAttributes(attrs, R.styleable.CircleFragmentProgress);
        baseColor = tya.getColor(R.styleable.CircleFragmentProgress_circle_fragment_indexColor, Color.LTGRAY);
        indexColor = tya.getColor(R.styleable.CircleFragmentProgress_circle_fragment_baseColor, Color.BLUE);
        textColor = tya.getColor(R.styleable.CircleFragmentProgress_circle_fragment_textColor, Color.BLUE);
        textSize = tya.getDimensionPixelSize(R.styleable.CircleFragmentProgress_circle_fragment_textSize, 36);
        tya.recycle();
        initUI();
    }

    private void initUI() {
        mContext = getContext();
        // 刻度画笔
        mScalePaint = new Paint();
        mScalePaint.setAntiAlias(true);
        mScalePaint.setStrokeWidth(DensityUtil.dip2px(mContext, 1));
        mScalePaint.setStrokeCap(Paint.Cap.ROUND);
        mScalePaint.setColor(baseColor);
        mScalePaint.setStyle(Paint.Style.STROKE);
        // 文字画笔
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(textColor);
        mTextPaint.setTextSize(textSize);
        mTextPaint.setStrokeWidth(DensityUtil.dip2px(mContext, 1));
        mTextPaint.setStyle(Paint.Style.FILL);
    }


    /**
     * 第一步 测量
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int myWidthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int myWidthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int myHeightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int myHeightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        // 获取宽
        if (myWidthSpecMode == MeasureSpec.EXACTLY) {
            // match_parent/精确值
            mWidth = myWidthSpecSize;
        } else {
            // wrap_content
            mWidth = DensityUtil.dip2px(mContext, 120);
        }
        // 获取高
        if (myHeightSpecMode == MeasureSpec.EXACTLY) {
            // match_parent/精确值
            mHeight = myHeightSpecSize;
        } else {
            // wrap_content
            mHeight = DensityUtil.dip2px(mContext, 120);
        }
        // 设置该view的宽高
        setMeasuredDimension(mWidth, mHeight);
    }

    /**
     * 第二步 位置
     *
     * @param changed
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    /**
     * 第三部 绘画
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawArcScale(canvas);
        drawTextValue(canvas);
    }

    private void drawArcScale(Canvas canvas) {
        canvas.save();
        for (int i = 0; i < 100; i++) {
            if (progress > i) {
                mScalePaint.setColor(indexColor);
            } else {
                mScalePaint.setColor(baseColor);
            }
            canvas.drawLine(mWidth / 2, 0, mHeight / 2, DensityUtil.dip2px(mContext, 10), mScalePaint);
            // 旋转的度数 = 100 / 360
            canvas.rotate(3.6f, mWidth / 2, mHeight / 2);
        }
        canvas.restore();
    }

    private void drawTextValue(Canvas canvas) {
        canvas.save();
        String showValue = String.valueOf(progress);
        Rect textBound = new Rect();
        mTextPaint.getTextBounds(showValue, 0, showValue.length(), textBound);    // 获取文字的矩形范围
        float textWidth = textBound.right - textBound.left;  // 获得文字宽
        float textHeight = textBound.bottom - textBound.top; // 获得文字高
        canvas.drawText(showValue, mWidth / 2 - textWidth / 2, mHeight / 2 + textHeight / 2, mTextPaint);

        canvas.restore();
    }

    /**
     * 设置进度
     */
    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }

    public void resetProgress() {
        this.progress = 0;
        invalidate();
    }
}
