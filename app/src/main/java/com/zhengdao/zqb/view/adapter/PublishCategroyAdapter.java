package com.zhengdao.zqb.view.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.entity.DictionaryValue;
import com.zhengdao.zqb.utils.LogUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author lijiangop
 * @CreateTime 2018/2/7 11:21
 */
public class PublishCategroyAdapter extends SimpleAdapter {

    private int type;

    public PublishCategroyAdapter(Context context, ArrayList<DictionaryValue> rewardData, int type) {
        super(context, rewardData);
        this.type = type;
    }

    @Override
    protected View getItemView(int i, View convertView, ViewGroup viewGroup) {
        View view;
        ViewHolder holder;
        if (convertView != null) {
            view = convertView;
            holder = (ViewHolder) convertView.getTag();
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.category_item, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        try {
            DictionaryValue entity = (DictionaryValue) mData.get(i);
            String value;
            if (type == 1)
                value = formatTime(new Long(entity.value));
            else
                value = TextUtils.isEmpty(entity.value) ? "" : entity.value;
            holder.mTextView.setText(TextUtils.isEmpty(value) ? "" : value);
        } catch (Exception ex) {
            LogUtils.e(ex.getMessage());
        }
        return view;
    }

    public String formatTime(long ms) {
        Long year;
        Long month;
        Long day;
        Long hour;
        Integer dd = 24;
        Integer mm = 24 * 30;
        Integer yy = 24 * 30 * 12;
        if (ms > dd * 3) {
            if (ms > yy) {//大于一年
                year = ms / yy;
                month = (ms - year * yy) / mm;
                day = (ms - year * yy - month * mm) / dd;
                hour = ms - year * yy - month * mm - day * dd;
            } else if (ms > mm) {//大于一个月
                year = 0l;
                month = ms / mm;
                day = (ms - month * mm) / dd;
                hour = ms - month * mm - day * dd;
            } else {//大于72小时
                year = 0l;
                month = 0l;
                day = ms / dd;
                hour = ms - day * dd;
            }
        } else {
            year = 0l;
            month = 0l;
            day = 0l;
            hour = ms;
        }
        StringBuffer sb = new StringBuffer();
        if (year > 0) {
            sb.append(year + "年");
        }
        if (month > 0) {
            sb.append(month + "月");
        }
        if (day > 0) {
            sb.append(day + "天");
        }
        if (hour > 0) {
            sb.append(hour + "小时");
        }
        return sb.toString();
    }

    class ViewHolder {
        @BindView(R.id.textView)
        TextView mTextView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
