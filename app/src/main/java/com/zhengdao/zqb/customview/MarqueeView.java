package com.zhengdao.zqb.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ViewFlipper;

import com.zhengdao.zqb.R;

import java.util.List;

/**
 * @创建者 cairui
 * @创建时间 2016/11/23 17:53
 * @描述 仿淘宝头条效果的跑马灯 效果,支持横向纵向滚动
 * @更新者 $Author$
 * @更新时间 $Date$
 * @版本 $Revision$
 * @更新描述 ${des}
 */
public class MarqueeView<T extends View> extends ViewFlipper {
    public static final int HORIZONTAL     = 0;
    public static final int VERTICAL       = 1;
    public static final int BASIC_INTERVAL = 3000;
    public static final int BASIC_DURATION = 500;
    private int                 mAnimDuration;
    private int                 mInterval;
    private int                 mOrientation;
    private List<T>             mViews;
    private onItemClickListener mOnItemClickListener;

    public MarqueeView(Context context) {
        this(context, null);
    }

    public MarqueeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MarqueeView);
        mAnimDuration = a.getInteger(R.styleable.MarqueeView_animDuration, BASIC_DURATION);
        mInterval = a.getInteger(R.styleable.MarqueeView_interval, BASIC_INTERVAL);
        mOrientation = a.getInt(R.styleable.MarqueeView_marqueen_orientation, VERTICAL);
        a.recycle();
        setFlipInterval(mInterval);
        setAnimation();
    }

    private void setAnimation() {
        setInAnimation(new AnimationUtil().createInAnimation(mOrientation));
        setOutAnimation(new AnimationUtil().createOutAnimation(mOrientation));
    }

    public void setAnimDuration(int animDuration) {
        this.mAnimDuration = animDuration;
        setAnimation();
    }

    public void setInterval(int interval) {
        this.mInterval = interval;
        setFlipInterval(mInterval);
    }

    public void setOrientation(int orientation) {
        this.mOrientation = orientation;
        setAnimation();
    }

    @Override
    protected void onDetachedFromWindow() {
        try {
            super.onDetachedFromWindow();
        } catch (IllegalArgumentException e) {
            stopFlipping();
        }
    }

    /**
     * 设置view的list并开始跑马灯效果
     *
     * @param views
     */
    public void startWithViews(@NonNull List<T> views) {
        this.mViews = views;
        start();
    }

    public void setOnItemClickListener(onItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void start() {
        if (mViews == null || mViews.size() == 0) {
            return;
        }
        removeAllViews();
        for (int i = 0; i < mViews.size(); i++) {
            final int position = i;
            final View view = mViews.get(i);
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeAllViews();
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null)
                        mOnItemClickListener.onItemClick(position, view);
                }
            });
            addView(mViews.get(i));
        }
        if (mViews.size() > 1) {
            //条目数大于1才开始滚动
            startFlipping();
        }
    }

    public interface onItemClickListener {
        void onItemClick(int postion, View view);
    }


    class AnimationUtil {
        public static final int RELATIVE_TO_PARENT = 2;
        public static final int RELATIVE_TO_SELF   = 1;

        public Animation createInAnimation(int orientation) {
            switch (orientation) {
                case VERTICAL:
                    return getVerticalInAnimationSet();
                case HORIZONTAL:
                    return getHorizontalInAnimationSet();
            }
            return getVerticalInAnimationSet();
        }

        @NonNull
        private Animation getHorizontalInAnimationSet() {
            AnimationSet set = new AnimationSet(false);
            TranslateAnimation ta = new TranslateAnimation(RELATIVE_TO_PARENT,
                    1,
                    RELATIVE_TO_PARENT,
                    0,
                    RELATIVE_TO_PARENT,
                    0,
                    RELATIVE_TO_PARENT,
                    0);
            ta.setDuration(mAnimDuration);
            AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
            alphaAnimation.setDuration(mAnimDuration);
            set.addAnimation(ta);
            set.addAnimation(alphaAnimation);
            return set;
        }

        @NonNull
        private AnimationSet getVerticalInAnimationSet() {
            AnimationSet set = new AnimationSet(false);
            TranslateAnimation ta = new TranslateAnimation(RELATIVE_TO_PARENT,
                    0,
                    RELATIVE_TO_PARENT,
                    0,
                    RELATIVE_TO_PARENT,
                    1,
                    RELATIVE_TO_PARENT,
                    0);
            ta.setDuration(mAnimDuration);
            AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
            alphaAnimation.setDuration(mAnimDuration);
            set.addAnimation(ta);
            set.addAnimation(alphaAnimation);
            return set;
        }

        public Animation createOutAnimation(int orientation) {
            switch (orientation) {
                case VERTICAL:
                    return getVerticalOutAnimationSet();
                case HORIZONTAL:
                    return getHorizontalOutAnimationSet();
            }
            return getVerticalOutAnimationSet();
        }

        @NonNull
        private AnimationSet getVerticalOutAnimationSet() {
            AnimationSet set = new AnimationSet(false);
            TranslateAnimation ta = new TranslateAnimation(RELATIVE_TO_PARENT,
                    0,
                    RELATIVE_TO_PARENT,
                    0,
                    RELATIVE_TO_PARENT,
                    0,
                    RELATIVE_TO_PARENT,
                    -1);
            ta.setDuration(mAnimDuration);
            AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
            alphaAnimation.setDuration(mAnimDuration);
            set.addAnimation(ta);
            set.addAnimation(alphaAnimation);
            return set;
        }

        @NonNull
        private AnimationSet getHorizontalOutAnimationSet() {
            AnimationSet set = new AnimationSet(false);
            TranslateAnimation ta = new TranslateAnimation(RELATIVE_TO_PARENT,
                    0,
                    RELATIVE_TO_PARENT,
                    -1,
                    RELATIVE_TO_PARENT,
                    0,
                    RELATIVE_TO_PARENT,
                    0);
            ta.setDuration(mAnimDuration);
            AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
            alphaAnimation.setDuration(mAnimDuration);
            set.addAnimation(ta);
            set.addAnimation(alphaAnimation);
            return set;
        }

    }

}
