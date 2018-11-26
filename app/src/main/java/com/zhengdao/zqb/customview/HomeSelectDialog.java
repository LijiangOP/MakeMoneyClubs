package com.zhengdao.zqb.customview;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.ScreenLoadEntity;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.utils.ViewUtils;
import com.zhengdao.zqb.view.adapter.WantedSelectedAdapter;

import java.util.ArrayList;

/**
 * @Author lijiangop
 * @CreateTime 2018/10/15 20:58
 */
public class HomeSelectDialog extends Dialog {

    private Context                                            mContext;
    private RecyclerView                                       mRecyclerView;
    private ArrayList<ScreenLoadEntity.ScreenLoadDetailEntity> mWantedData;
    private WantedSelectedAdapter                              mWantedWantedSelectedAdapter;

    public HomeSelectDialog(@NonNull Context context) {
        super(context, R.style.RuleHintDialog);//两个参数的构造方法，第二参数可以改变dialog的显示
        this.mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater from = LayoutInflater.from(mContext);
        View inflate = from.inflate(R.layout.layout_selct_dialog, null);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉默认的title框
        setContentView(inflate);
        mRecyclerView = findViewById(R.id.recycleView);
    }

    public void initData(ScreenLoadEntity result, int wantedType, WantedSelectedAdapter.ItemSelectedCallBack callBack) {
        if (result.code == Constant.HttpResult.SUCCEED) {
            if (result.rewardType != null)
                initWantedType(result.rewardType, wantedType, callBack);
        } else {
            ToastUtil.showToast(mContext, TextUtils.isEmpty(result.msg) ? "数据加载失败" : result.msg);
        }
    }


    private void initWantedType(ArrayList<ScreenLoadEntity.ScreenLoadDetailEntity> rewardType, int wantedType, WantedSelectedAdapter.ItemSelectedCallBack callBack) {
        if (rewardType.size() == 0)
            return;
        if (mWantedData == null)
            mWantedData = new ArrayList<>();
        mWantedData.addAll(rewardType);
        if (mWantedWantedSelectedAdapter == null) {
            mWantedWantedSelectedAdapter = new WantedSelectedAdapter(mContext, R.layout.selected_item, mWantedData, callBack, wantedType);
            mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
            mRecyclerView.addItemDecoration(new SpaceWantedItemDecoration(10));
            mRecyclerView.setAdapter(mWantedWantedSelectedAdapter);
        } else
            mWantedWantedSelectedAdapter.notifyDataSetChanged();
    }

    public class SpaceWantedItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SpaceWantedItemDecoration(int space) {
            this.space = ViewUtils.dip2px(mContext, space);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int pos = parent.getChildAdapterPosition(view);
            outRect.left = 0;
            outRect.top = 0;
            outRect.bottom = 0;
            if ((pos + 1) % 3 != 0) {
                outRect.right = space;
            } else {
                outRect.right = 0;
            }
            if (pos < 3) {
                outRect.top = 0;
            } else {
                outRect.top = 20;
            }
        }
    }

}
