package com.zhengdao.zqb.view.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.entity.RewardUserInfos;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author lijiangop
 * @CreateTime 2018/2/26 17:00
 */
public class AddUserInfoInputAdapter extends SimpleAdapter {

    private ClickCallBack callBack;

    public interface ClickCallBack {

        void onDelete(int position);
    }

    public AddUserInfoInputAdapter(Context context, List data, ClickCallBack callBack) {
        super(context, data);
        this.callBack = callBack;
    }


    @Override
    protected View getItemView(final int i, View convertView, ViewGroup viewGroup) {
        View view;
        ViewHolder holder;
        if (convertView != null) {
            view = convertView;
            holder = (ViewHolder) convertView.getTag();
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.adduserinfoinput_item, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        RewardUserInfos entity = (RewardUserInfos) mData.get(i);
        holder.mEtAddUserInfo.setHint(TextUtils.isEmpty(entity.describe) ? "" : entity.describe);
        holder.mIvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBack != null)
                    callBack.onDelete(i);
            }
        });
        return view;
    }

    class ViewHolder {
        @BindView(R.id.et_add_user_info)
        EditText     mEtAddUserInfo;
        @BindView(R.id.iv_clear)
        ImageView    mIvClear;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
