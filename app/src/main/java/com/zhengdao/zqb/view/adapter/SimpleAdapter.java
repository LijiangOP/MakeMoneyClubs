package com.zhengdao.zqb.view.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * @Author lijiangop
 * @CreateTime 2017/9/19 15:12
 */
public abstract class SimpleAdapter<T extends List> extends BaseAdapter {

    protected T       mData;
    protected Context mContext;

    public SimpleAdapter(Context context, T data) {
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public Object getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return getItemView(i, view, viewGroup);
    }

    protected abstract View getItemView(int i, View view, ViewGroup viewGroup);
}
