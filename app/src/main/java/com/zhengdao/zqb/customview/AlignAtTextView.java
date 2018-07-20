package com.zhengdao.zqb.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.zhengdao.zqb.R;


/**
 * 一个分布对齐的TextView
 *
 * @Author lijiangop
 * @CreateTime 2017/8/8 10:35
 */
public class AlignAtTextView extends android.support.v7.widget.AppCompatTextView {
    private Typeface mTextFont;
    private Rect     mBound;
    private String   mText;
    private String[] mTextArray;
    private int      mTextColor;
    private int      viewWidth;
    private int      viewHeight;
    private int      oneViewWidth;
    private int      oneViewHeight;
    private float    mTextSize;
    private Context  mContext;
    private int      number;
    private Paint    mPaint;
    private Typeface mTypeface;

    public AlignAtTextView(Context context) {//通常是通过代码初始化控件时使用
        this(context, null);
    }

    public AlignAtTextView(Context context, @Nullable AttributeSet attrs) { //通常对应布局文件中控件被映射成对象时调用（需要解析属性）
        this(context, attrs, 0);
    }

    public AlignAtTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {//通常我们让前两个构造方法最终调用三个参数的构造方法，然后在第三个构造方法中进行一些初始化操作
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        //获取自定义属性的值
        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.AlignAtTextView, defStyleAttr, 0);
        mText = attributes.getString(R.styleable.AlignAtTextView_text);
        mTextColor = attributes.getColor(R.styleable.AlignAtTextView_textColor, Color.BLACK);
        mTextSize = attributes.getDimension(R.styleable.AlignAtTextView_textSize, 20);
        attributes.recycle();//注意回收
        setText(mText);
    }

    private void setText(String text) {
        if (text == null)
            return;
        mText = text;
        if (mPaint == null)
            mPaint = new Paint();
        mPaint.setTextSize(mTextSize);
        mPaint.setColor(mTextColor);
        mPaint.setAntiAlias(true);
        if (mBound == null)
            mBound = new Rect();
        mPaint.getTextBounds(mText, 0, mText.length(), mBound);
        number = text.length();
        this.mText = text;
        this.oneViewWidth = getOneViewRect("汉").width();
        this.oneViewHeight = getOneViewRect(mText).height();
        String[] strs = new String[number];
        for (int i = 0; i < number; i++) {
            strs[i] = mText.substring(i, i + 1);
        }
        this.mTextArray = strs;
        this.invalidate();
    }

    public void setTextTypeface(Typeface typeface) {
        mTypeface = typeface;
        if (mText != null)
            setText(mText);
    }

    public void setTextColor(int textColor) {
        mTextColor = textColor;
        if (mText != null)
            setText(mText);
    }

    public void setTextSize(float textSize) {
        mTextSize = textSize;
        if (mText != null)
            setText(mText);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        init();
        if (number == 0)
            return;
        int drawY = (viewHeight - oneViewHeight) / 2 + oneViewHeight;
        for (int i = 0; i < number; i++) {
            int endLength_X = viewWidth - oneViewWidth - dip2px(mContext, 5);
            int itemLength = endLength_X / (number - 2);
            if (i == number - 1) {
                //最后一个
                canvas.drawText(mTextArray[i], viewWidth - dip2px(mContext, 3), drawY, mPaint);
            } else if (i == number - 2) {
                //倒数第二个
                canvas.drawText(mTextArray[i], endLength_X, drawY, mPaint);
            } else if (i != 0 && i != number - 1 && i != number - 2) {
                //其他
                canvas.drawText(mTextArray[i], i * itemLength, drawY, mPaint);
            } else {
                //第一个
                canvas.drawText(mTextArray[i], 0, drawY, mPaint);
            }
        }
    }

    private void init() {
        this.setPadding(0, 0, 0, 0);
        viewWidth = this.getWidth();
        viewHeight = this.getHeight();
    }

    /**
     * 获得每一个文字的宽高
     *
     * @param str
     * @return
     */
    private Rect getOneViewRect(String str) {
        if (mPaint == null)
            return null;
        Rect rect = new Rect();
        mPaint.getTextBounds(str, 0, 1, rect);
        return rect;
    }

    /**
     * 根据手机的分辨率dp转成px
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
