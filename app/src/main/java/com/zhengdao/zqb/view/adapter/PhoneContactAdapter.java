package com.zhengdao.zqb.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.entity.PhoneContactEntity;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author lijiangop
 * @CreateTime 2018/3/27 11:42
 */
public class PhoneContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ItemCallback             mCallback;
    private Context                  mContext;
    private List<PhoneContactEntity> mData;

    public interface ItemCallback {
        void onItemClick(String string);
    }

    public PhoneContactAdapter(Context context, List<PhoneContactEntity> data, ItemCallback callback) {
        this.mContext = context;
        this.mCallback = callback;
        this.mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        View item = View.inflate(mContext, R.layout.recyl_phone_contact_item, null);
        holder = new ItemViewHolder(item);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final PhoneContactEntity phoneContactEntity = mData.get(position);
        ItemViewHolder viewholder = (ItemViewHolder) holder;
        viewholder.mTvName.setText(TextUtils.isEmpty(phoneContactEntity.name) ? "" : phoneContactEntity.name);
        viewholder.mTvPhone.setText(TextUtils.isEmpty(phoneContactEntity.phone) ? "" : phoneContactEntity.phone);
        viewholder.mIvIcon.setImageBitmap(phoneContactEntity.icon);
        viewholder.mLlItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null)
                    mCallback.onItemClick(phoneContactEntity.phone);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_icon)
        ImageView    mIvIcon;
        @BindView(R.id.tv_name)
        TextView     mTvName;
        @BindView(R.id.tv_phone)
        TextView     mTvPhone;
        @BindView(R.id.ll_item)
        LinearLayout mLlItem;

        public ItemViewHolder(View item) {
            super(item);
            ButterKnife.bind(this, item);
        }
    }
}
