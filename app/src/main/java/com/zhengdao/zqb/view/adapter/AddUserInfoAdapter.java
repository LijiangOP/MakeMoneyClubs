package com.zhengdao.zqb.view.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.utils.LogUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.zhengdao.zqb.config.Constant.EditableList;

/**
 * @Author lijiangop
 * @CreateTime 2018/2/22 16:28
 */
public class AddUserInfoAdapter extends SimpleAdapter {

    private int           contenttype;
    private int           type;
    private ClickCallBack callBack;

    public AddUserInfoAdapter(Context context, List data, ClickCallBack callBack, int contenttype, int type) {
        this(context, data, contenttype, type);
        this.callBack = callBack;
    }

    public AddUserInfoAdapter(Context context, List data, int contenttype, int type) {
        super(context, data);
        this.contenttype = contenttype;
        this.type = type;
    }

    public interface ClickCallBack {
        void onDelete(int position);
    }

    @Override
    protected View getItemView(final int i, View convertView, ViewGroup viewGroup) {
        View view = null;
        final ViewHolder holder;
        try {
            if (convertView != null) {
                view = convertView;
                holder = (ViewHolder) convertView.getTag();
            } else {
                view = LayoutInflater.from(mContext).inflate(R.layout.adduserinfo_item, viewGroup, false);
                holder = new ViewHolder(view);
                view.setTag(holder);
            }
            String string = (String) mData.get(i);
            if (TextUtils.isEmpty(string)) {
                switch (contenttype) {
                    case 0:
                        holder.mEtAddUserInfo.setHint("描述,例如输入注册手机号");
                        break;
                    case 1:
                        holder.mEtAddUserInfo.setHint("描述,例如输入账号截图");
                        break;
                }
            } else
                holder.mEtAddUserInfo.setText(string);
            holder.mIvAddUserInfoDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callBack != null) {
                        callBack.onDelete(i);
                    }
                }
            });
            if (!EditableList.contains(type)) {
                holder.mEtAddUserInfo.setEnabled(false);
                holder.mIvAddUserInfoDelete.setVisibility(View.GONE);
            }
        } catch (Exception ex) {
            LogUtils.e(ex.getMessage());
        }
        return view;
    }

    class ViewHolder {
        @BindView(R.id.et_add_user_info)
        EditText     mEtAddUserInfo;
        @BindView(R.id.iv_add_user_info_delete)
        ImageView    mIvAddUserInfoDelete;
        @BindView(R.id.ll_add_user_info_1)
        LinearLayout mLlAddUserInfo1;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
