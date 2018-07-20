package com.zhengdao.zqb.customview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @创建者 cairui
 * @创建时间 2016/12/12 11:36
 * @描述 ${des}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @版本 $Revision$
 * @更新描述 ${des}
 */
public class ViewPagerEx extends ViewPager {
    private int startX;
    private int startY;

    public ViewPagerEx(Context context) {
        super(context);
    }

    public ViewPagerEx(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) ev.getX();
                startY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = Math.abs((int) (ev.getX() - startX));
                int moveY = Math.abs((int) (ev.getY() - startY));
                startX = (int) ev.getX();
                startY = (int) ev.getY();
                if (moveX > moveY) {
                    return super.onInterceptTouchEvent(ev);
                } else {
                    return false;
                }
        }
        return super.onInterceptTouchEvent(ev);
    }
}
