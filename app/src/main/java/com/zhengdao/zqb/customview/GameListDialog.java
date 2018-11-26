package com.zhengdao.zqb.customview;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.entity.GameListHttpResult;
import com.zhengdao.zqb.view.adapter.GameListAdapter;

import java.util.ArrayList;

/**
 * @Author lijiangop
 * @CreateTime 2018/10/22 14:16
 */
public class GameListDialog extends Dialog {
    private Context                            mContext;
    private RecyclerView                       mRecyclerView;
    private TextView                           mTvClick;
    private View.OnClickListener               mListener;
    private ArrayList<GameListHttpResult.Game> mData;
    private GameListAdapter                    mAdapter;
    private GameListAdapter.CallBack           mCallBack;

    public GameListDialog(@NonNull Context context) {
        super(context, R.style.RuleHintDialog);//两个参数的构造方法，第二参数可以改变dialog的显示
        this.mContext = context;
    }

    private void initView() {
        LayoutInflater from = LayoutInflater.from(mContext);
        View inflate = from.inflate(R.layout.layout_game_list_dialog, null);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉默认的title框
        setContentView(inflate);

        mRecyclerView = findViewById(R.id.recycler_view);
        mTvClick = findViewById(R.id.tv_close);
        if (mListener != null)
            mTvClick.setOnClickListener(mListener);
        if (mAdapter == null)
            mAdapter = new GameListAdapter(mData, mContext, mCallBack);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = mContext.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.85);
        dialogWindow.setAttributes(lp);
    }

    public void initView(ArrayList<GameListHttpResult.Game> data, View.OnClickListener listener, GameListAdapter.CallBack callBack) {
        this.mListener = listener;
        this.mCallBack = callBack;
        this.mData = data;
        initView();
    }
}
