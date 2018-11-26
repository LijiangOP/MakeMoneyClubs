package com.zhengdao.zqb.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.entity.GameListHttpResult;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author lijiangop
 * @CreateTime 2018/10/22 14:31
 */
public class GameListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private CallBack                           mCallBack;
    private Context                            mContext;
    private ArrayList<GameListHttpResult.Game> mData;
    private ViewHolder                         mHolder;

    public interface CallBack {
        void onItemClick(int id);
    }

    public GameListAdapter(ArrayList<GameListHttpResult.Game> data, Context context, CallBack callBack) {
        this.mCallBack = callBack;
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        RecyclerView.ViewHolder holder;
        View header = View.inflate(mContext, R.layout.recyl_game_list_item, null);
        holder = new ViewHolder(header);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        mHolder = (ViewHolder) viewHolder;
        final GameListHttpResult.Game game = mData.get(i);
        if (game != null) {
            Glide.with(mContext).load(TextUtils.isEmpty(game.icon) ? "" : game.icon).error(R.drawable.game_icon_default).into(mHolder.mIvIcon);
            if (!TextUtils.isEmpty(game.value))
                mHolder.mTvName.setText(game.value);
            mHolder.mLlItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallBack != null)
                        mCallBack.onItemClick(game.type);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_icon)
        ImageView    mIvIcon;
        @BindView(R.id.tv_name)
        TextView     mTvName;
        @BindView(R.id.ll_item)
        LinearLayout mLlItem;

        public ViewHolder(View header) {
            super(header);
            ButterKnife.bind(this, header);
        }
    }
}
