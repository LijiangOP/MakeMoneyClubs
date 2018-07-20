package com.zhengdao.zqb.view.dialogactivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.ScreenLoadEntity;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.utils.ViewUtils;
import com.zhengdao.zqb.view.adapter.BusinessdSelectedAdapter;
import com.zhengdao.zqb.view.adapter.WantedSelectedAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author lijiangop
 * @CreateTime 2018/1/16 16:02
 */
public class SelectedDailogActivty extends Activity implements View.OnClickListener, ISelectedActivityView, WantedSelectedAdapter.ItemSelectedCallBack, BusinessdSelectedAdapter.ItemSelectedCallBack {


    @BindView(R.id.recycleView_business)
    RecyclerView mRecycleViewBusiness;
    @BindView(R.id.recycleView_wanted)
    RecyclerView mRecycleViewWanted;
    @BindView(R.id.tv_reset)
    TextView     mTvReset;
    @BindView(R.id.tv_confirm)
    TextView     mTvConfirm;
    private long mCurrentTimeMillis = 0;
    private int  mBusinessType      = -1;//业务分类
    private int  mWantedType        = -1;//悬赏分类
    private SelectedPresenter                                  mPresenter;
    private WantedSelectedAdapter                              mWantedWantedSelectedAdapter;
    private BusinessdSelectedAdapter                           mBusinessWantedSelectedAdapter;
    private ArrayList<ScreenLoadEntity.ScreenLoadDetailEntity> mBusinessData;
    private ArrayList<ScreenLoadEntity.ScreenLoadDetailEntity> mWantedData;

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        View view = getWindow().getDecorView();
        WindowManager.LayoutParams lp = (WindowManager.LayoutParams) view.getLayoutParams();
        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        lp.x = 0;
        lp.y = 0;
        lp.width = outMetrics.widthPixels * 5 / 6;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.RIGHT;
        getWindowManager().updateViewLayout(view, lp);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.earn_selected);
        setFinishOnTouchOutside(true);
        ButterKnife.bind(this);
        init();
        initClickListener();
    }

    private void init() {
        mBusinessType = getIntent().getIntExtra(Constant.Activity.Data, -1);
        mWantedType = getIntent().getIntExtra(Constant.Activity.Data1, -1);
        mPresenter = new SelectedPresenter(this, this);
        mPresenter.getData();
    }

    private void initClickListener() {
        mTvReset.setOnClickListener(this);
        mTvConfirm.setOnClickListener(this);
    }


    @Override
    public void showView(ScreenLoadEntity result) {
        if (result.code == Constant.HttpResult.SUCCEED) {
            //            if (result.businessType != null)取消了业务分类
            //                initBusinessType(result.businessType);
            if (result.rewardType != null)
                initWantedType(result.rewardType);
        } else {
            ToastUtil.showToast(SelectedDailogActivty.this, TextUtils.isEmpty(result.msg) ? "数据加载失败" : result.msg);
        }
    }

    private void initBusinessType(ArrayList<ScreenLoadEntity.ScreenLoadDetailEntity> businessType) {
        if (businessType.size() == 0)
            return;
        if (mBusinessData == null)
            mBusinessData = new ArrayList<>();
        mBusinessData.addAll(businessType);
        if (mBusinessWantedSelectedAdapter == null) {
            mBusinessWantedSelectedAdapter = new BusinessdSelectedAdapter(this, R.layout.selected_item, mBusinessData, this, mBusinessType);
            mRecycleViewBusiness.setLayoutManager(new GridLayoutManager(this, 3));
            mRecycleViewBusiness.addItemDecoration(new SpaceBusinessItemDecoration(10));
            mRecycleViewBusiness.setAdapter(mBusinessWantedSelectedAdapter);
        } else
            mBusinessWantedSelectedAdapter.notifyDataSetChanged();
    }

    private void initWantedType(ArrayList<ScreenLoadEntity.ScreenLoadDetailEntity> rewardType) {
        if (rewardType.size() == 0)
            return;
        if (mWantedData == null)
            mWantedData = new ArrayList<>();
        mWantedData.addAll(rewardType);
        if (mWantedWantedSelectedAdapter == null) {
            mWantedWantedSelectedAdapter = new WantedSelectedAdapter(this, R.layout.selected_item, mWantedData, this, mWantedType);
            mRecycleViewWanted.setLayoutManager(new GridLayoutManager(this, 3));
            mRecycleViewWanted.addItemDecoration(new SpaceWantedItemDecoration(10));
            mRecycleViewWanted.setAdapter(mWantedWantedSelectedAdapter);
        } else
            mWantedWantedSelectedAdapter.notifyDataSetChanged();
    }

    @Override
    public void wantedItemIsSelected(int id) {
        mWantedType = id;
    }


    @Override
    public void businessItemIsSelected(int id) {
        mBusinessType = id;
    }

    @Override
    public void showErrorMessage(String message) {
        ToastUtil.showToast(SelectedDailogActivty.this, "数据加载失败");
    }

    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - mCurrentTimeMillis < 1000)
            return;
        mCurrentTimeMillis = System.currentTimeMillis();
        switch (v.getId()) {
            case R.id.tv_reset:
                doReset();
                break;
            case R.id.tv_confirm:
                doConfirm();
                break;
        }
    }

    private void doReset() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt("businessType", -1);
        bundle.putInt("wantedType", -1);
        intent.putExtra(Constant.Activity.Data, bundle);
        setResult(RESULT_OK, intent);
        SelectedDailogActivty.this.finish();
    }

    private void doConfirm() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt("businessType", mBusinessType);
        bundle.putInt("wantedType", mWantedType);
        intent.putExtra(Constant.Activity.Data, bundle);
        setResult(RESULT_OK, intent);
        SelectedDailogActivty.this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null)
            mPresenter.onDestory();
    }

    public class SpaceBusinessItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SpaceBusinessItemDecoration(int space) {
            this.space = ViewUtils.dip2px(SelectedDailogActivty.this, space);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int pos = parent.getChildAdapterPosition(view);
            outRect.left = 0;
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

    public class SpaceWantedItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SpaceWantedItemDecoration(int space) {
            this.space = ViewUtils.dip2px(SelectedDailogActivty.this, space);
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
