package com.zhengdao.zqb.entity;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * @Author lijiangop
 * @CreateTime 2018/3/6 09:42
 */
public class Prize {

    private int             mId;
    private String          mName;
    private Bitmap          mIcon;
    private Rect            mRect;
    public  OnClickListener listener;
    private int             mMaxWidth;
    private int             mMaxHeight;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Bitmap getIcon() {
        return mIcon;
    }

    public void setIcon(Bitmap icon) {
        mIcon = icon;
    }

    /**
     * 是否是中心点被点击
     *
     * @param touchPoint
     * @return
     */
    public boolean isClick(Point touchPoint) {
        boolean isClick = false;
        int x = touchPoint.x;
        int y = touchPoint.y;
        if (mMaxWidth / 3 < x && x < 2 * (mMaxWidth / 3) && mMaxHeight / 3 < y && y < 2 * (mMaxHeight / 3))
            isClick = true;
        return isClick;
    }

    public void click() {
        listener.onClick();
    }

    public Rect getRect() {
        return mRect;
    }

    public void setRect(Rect rect) {
        mRect = rect;
    }

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }

    public void setMaxWidth(int maxWidth) {
        mMaxWidth = maxWidth;
    }

    public void setMaxHeight(int maxHeight) {
        mMaxHeight = maxHeight;
    }

    public interface OnClickListener {
        void onClick();
    }
}
