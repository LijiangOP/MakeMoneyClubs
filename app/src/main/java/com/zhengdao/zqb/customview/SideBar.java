package com.zhengdao.zqb.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * @Author lijiangop
 * @CreateTime 2018/3/27 14:20
 */
public class SideBar extends View {

    private Context context;
    private String[] mTexts = new String[]{
            "A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q",
            "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"
    };
    private Paint mCommonPaint;
    private int mChoose = -1;
    private Paint           mChoosePaint;
    private GestureDetector mGestureDetector;
    private int             mTextHeight;

    public SideBar(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public SideBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public SideBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        mCommonPaint = new Paint();
        mCommonPaint.setAntiAlias(true); //抗锯齿
        mCommonPaint.setTextSize(dip2px(10));
        mCommonPaint.setColor(Color.parseColor("#498FCA"));
        mCommonPaint.setTypeface(Typeface.MONOSPACE); //设置字体

        mChoosePaint = new Paint();
        mChoosePaint.setAntiAlias(true);
        mChoosePaint.setTextSize(dip2px(10));
        mChoosePaint.setColor(Color.parseColor("#F23D3D"));
        mChoosePaint.setTypeface(Typeface.MONOSPACE);

        mGestureDetector = new GestureDetector(getContext(), mOnGestureListener);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < mTexts.length; i++) {
            String text = mTexts[i];
            float x = getMeasuredWidth() / 2 - mCommonPaint.measureText(text) / 2; //measureText测量字体大小
            if (mChoose == i) {
                canvas.drawText(text, x, i * mTextHeight + mTextHeight + getPaddingTop(), mChoosePaint);
            } else {
                canvas.drawText(text, x, i * mTextHeight + mTextHeight + getPaddingTop(), mCommonPaint);
            }
        }
    }

    private GestureDetector.SimpleOnGestureListener mOnGestureListener = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            int y = (int) e.getY() - getPaddingTop();
            mChoose = y / mTextHeight;
            invalidate();
            if (mOnSideChooseListening != null) {
                mOnSideChooseListening.onChooseListener(mChoose, mTexts[mChoose]);
            }
            return super.onSingleTapUp(e);
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //需要考虑padding
        mTextHeight = (getMeasuredHeight() - getPaddingTop() - getPaddingBottom()) / mTexts.length;
    }

    public void setTextSize(int size) {
        mCommonPaint.setTextSize(size);
        mChoosePaint.setTextSize(size);
    }

    public interface OnSideChooseListening {
        void onChooseListener(int choose, String text);
    }

    private OnSideChooseListening mOnSideChooseListening;

    public void setOnSideChooseListening(OnSideChooseListening onSideChooseListening) {
        mOnSideChooseListening = onSideChooseListening;
    }

    public int dip2px(float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
