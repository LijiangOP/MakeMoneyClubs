package com.zhengdao.zqb.view.adapter;

import android.content.Context;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.entity.KindOfWantedDetailHttpEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author lijiangop
 * @CreateTime 2018/2/5 16:03
 */
public class KindOfWantedDetailTextAdapter extends SimpleAdapter {

    public KindOfWantedDetailTextAdapter(Context context, List data) {
        super(context, data);
    }

    @Override
    protected View getItemView(int i, View convertView, ViewGroup viewGroup) {
        View view;
        ViewHolder holder;
        if (convertView != null) {
            view = convertView;
            holder = (ViewHolder) convertView.getTag();
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_wanteddetail_text, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        final KindOfWantedDetailHttpEntity.TaskUserInfos entity = (KindOfWantedDetailHttpEntity.TaskUserInfos) mData.get(i);
        holder.mTvConten.setText(TextUtils.isEmpty(entity.content) ? "" : entity.content);
        holder.mTvCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                // 将文本内容放到系统剪贴板里。
                cm.setText(TextUtils.isEmpty(entity.content) ? "" : entity.content);
                Toast.makeText(mContext, "复制成功!" + cm.getText(), Toast.LENGTH_LONG).show();
            }
        });
        return view;
    }

    class ViewHolder {

        @BindView(R.id.tv_conten)
        TextView mTvConten;
        @BindView(R.id.tv_copy)
        TextView mTvCopy;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
