package com.zhengdao.zqb.view.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhengdao.zqb.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author lijiangop
 * @CreateTime 2017/8/28 11:33
 */
public class PlatformChooseAdapter extends SimpleAdapter {
    private List<String> mDatas;

    public PlatformChooseAdapter(Context context, List data) {
        super(context, data);
        mDatas=data;
    }

    @Override
    protected View getItemView(int i, View convertView, ViewGroup viewGroup) {
        View view;
        ViewHolder holder;
        if (convertView != null) {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        } else {
            view = LayoutInflater.from(mContext).inflate(
                    R.layout.popwindow_platform_choose_item, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        String s = TextUtils.isEmpty(mDatas.get(i)) ? "" : mDatas.get(i);
        holder.mMenuItem.setText(s);
        return view;
    }

    class ViewHolder {
        @BindView(R.id.menu_item)
        TextView mMenuItem;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
