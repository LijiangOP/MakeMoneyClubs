package com.zhengdao.zqb.view.activity.zeroearn;


import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kennyc.view.MultiStateView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.SynthesizeEntity;
import com.zhengdao.zqb.event.BackToHomeEvent;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.view.activity.login.LoginActivity;
import com.zhengdao.zqb.view.adapter.EarnFragmentSynthesizeAdapter;
import com.zhengdao.zqb.view.adapter.GoodsAdapter;
import com.zhengdao.zqb.view.dialogactivity.SelectedDailogActivty;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static com.zhengdao.zqb.config.Constant.Activity.Data;


public class ZeroEarnActivity extends MVPBaseActivity<ZeroEarnContract.View, ZeroEarnPresenter> implements ZeroEarnContract.View, View.OnClickListener, EarnFragmentSynthesizeAdapter.CallBack {

    private static final int REQUESTCODE   = 001;
    private static final int ACTION_SELECT = 002;
    @BindView(R.id.re_synthesize)
    RelativeLayout     mReSynthesize;
    @BindView(R.id.tv_hot)
    TextView           mTvHot;
    @BindView(R.id.iv_hot_arrow_up)
    ImageView          mIvHotArrowUp;
    @BindView(R.id.iv_hot_arrow_down)
    ImageView          mIvHotArrowDown;
    @BindView(R.id.re_hot)
    RelativeLayout     mReHot;
    @BindView(R.id.tv_award)
    TextView           mTvAward;
    @BindView(R.id.iv_reward_arrow_up)
    ImageView          mIvRewardArrowUp;
    @BindView(R.id.iv_reward_arrow_down)
    ImageView          mIvRewardArrowDown;
    @BindView(R.id.re_award)
    RelativeLayout     mReAward;
    @BindView(R.id.tv_select)
    TextView           mTvSelect;
    @BindView(R.id.re_select)
    RelativeLayout     mReSelect;
    @BindView(R.id.ll_heard)
    LinearLayout       mLlHeard;
    @BindView(R.id.recycle_view)
    RecyclerView       mRecycleView;
    @BindView(R.id.v_cover)
    View               mVCover;
    @BindView(R.id.multiStateView)
    MultiStateView     mMultiStateView;
    @BindView(R.id.swipeRefreshLayout)
    SmartRefreshLayout mSwipeRefreshLayout;

    private ArrayList<SynthesizeEntity>   mSynthesizeEntityArrayList;
    private EarnFragmentSynthesizeAdapter mSynthesizeAdapter;
    private PopupWindow                   mPopupWindow;
    private boolean mIsUpFrist  = false;
    private boolean mIsUpSecond = false;
    private int     mClassify   = -1;
    private int     mCategory   = -1;
    private Disposable mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zero_earn);
        ButterKnife.bind(this);
        setTitle("0元赚");
        init();
    }

    private void init() {
        mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                mPresenter.updateData();
                refreshlayout.finishRefresh();
            }
        });
        mSwipeRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                mPresenter.getMoreData();
                refreshlayout.finishLoadmore();
            }
        });
        mMultiStateView.getView(MultiStateView.VIEW_STATE_ERROR).findViewById(R.id.btn_error_reload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.initData();
            }
        });
        mDisposable = RxBus.getDefault().toObservable(BackToHomeEvent.class).subscribe(new Consumer<BackToHomeEvent>() {
            @Override
            public void accept(BackToHomeEvent backToHomeEvent) throws Exception {
                ZeroEarnActivity.this.finish();
            }
        });
        initClickListener();
        mPresenter.initData();
    }

    private void initClickListener() {
        mReSynthesize.setOnClickListener(this);
        mReHot.setOnClickListener(this);
        mReAward.setOnClickListener(this);
        mReSelect.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.re_synthesize:
                doSynthesize();
                break;
            case R.id.re_hot:
                changeArrow(1);
                break;
            case R.id.re_award:
                changeArrow(2);
                break;
            case R.id.re_select:
                doSelect();
                break;
        }
    }

    private void doSynthesize() {
        try {
            View contentView = LayoutInflater.from(this).inflate(R.layout.popup_earn_fragment_synthesize, null);
            RecyclerView recyclerView = contentView.findViewById(R.id.recycle_view);
            if (mSynthesizeEntityArrayList == null) {
                mSynthesizeEntityArrayList = new ArrayList<>();
                mSynthesizeEntityArrayList.add(new SynthesizeEntity("综合排序", true));
                mSynthesizeEntityArrayList.add(new SynthesizeEntity("新品优先", false));
                mSynthesizeEntityArrayList.add(new SynthesizeEntity("首发优先", false));
            }
            if (mSynthesizeAdapter == null) {
                mSynthesizeAdapter = new EarnFragmentSynthesizeAdapter(this, R.layout.synthesize_item, mSynthesizeEntityArrayList, this);
                recyclerView.setAdapter(mSynthesizeAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
            } else
                mSynthesizeAdapter.notifyDataSetChanged();
            if (mPopupWindow == null) {
                mPopupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                mPopupWindow.setBackgroundDrawable(new ColorDrawable());
                mPopupWindow.setFocusable(true);
                mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        mVCover.setVisibility(View.GONE);
                    }
                });
            }
            if (Build.VERSION.SDK_INT > 18)
                mPopupWindow.showAsDropDown(mLlHeard, 0, 0, Gravity.TOP);
            else
                mPopupWindow.showAsDropDown(mLlHeard, 0, 0);
            mVCover.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeArrow(int i) {
        String sortName;
        String orderType;
        switch (i) {
            case 1:
                if (mIsUpFrist) {
                    mIvHotArrowUp.setImageResource(R.drawable.little_arror_normal_up);
                    mIvHotArrowDown.setImageResource(R.drawable.little_arror_press_down);
                    orderType = "desc";
                } else {
                    mIvHotArrowUp.setImageResource(R.drawable.little_arror_press_up);
                    mIvHotArrowDown.setImageResource(R.drawable.little_arror_normal_down);
                    orderType = "asc";
                }
                mIvRewardArrowUp.setImageResource(R.drawable.little_arror_normal_up);
                mIvRewardArrowDown.setImageResource(R.drawable.little_arror_normal_down);
                mIsUpFrist = !mIsUpFrist;
                mIsUpSecond = false;
                sortName = "joincount";
                mPresenter.getDataWithBaseSearch(sortName, orderType, -1);
                break;
            case 2:
                mIvHotArrowUp.setImageResource(R.drawable.little_arror_normal_up);
                mIvHotArrowDown.setImageResource(R.drawable.little_arror_normal_down);
                if (mIsUpSecond) {
                    mIvRewardArrowUp.setImageResource(R.drawable.little_arror_normal_up);
                    mIvRewardArrowDown.setImageResource(R.drawable.little_arror_press_down);
                    orderType = "desc";
                } else {
                    mIvRewardArrowUp.setImageResource(R.drawable.little_arror_press_up);
                    mIvRewardArrowDown.setImageResource(R.drawable.little_arror_normal_down);
                    orderType = "asc";
                }
                mIsUpSecond = !mIsUpSecond;
                mIsUpFrist = false;
                sortName = "money";
                mPresenter.getDataWithBaseSearch(sortName, orderType, -1);
                break;
        }
    }

    @Override
    public void doItemClick(int position) {
        for (int i = 0; i < mSynthesizeEntityArrayList.size(); i++) {
            if (i == position) {
                mSynthesizeEntityArrayList.get(i).setSelected(true);
            } else {
                mSynthesizeEntityArrayList.get(i).setSelected(false);
            }
        }
        clearArrow();
        switch (position) {
            case 0:
                mPresenter.getDataWithBaseSearch("order", "desc", -1);//综合排序
                break;
            case 1:
                mPresenter.getDataWithBaseSearch("upFrameTime", "desc", -1);//新品优先
                break;
            case 2:
                mPresenter.getDataWithBaseSearch("upFrameTime", "desc", 1);//首发预先
                break;
        }
        mPopupWindow.dismiss();
    }

    /**
     * 清空搜索条件，界面显示
     */
    private void clearArrow() {
        mIvHotArrowUp.setImageResource(R.drawable.little_arror_normal_up);
        mIvHotArrowDown.setImageResource(R.drawable.little_arror_normal_down);
        mIvRewardArrowUp.setImageResource(R.drawable.little_arror_normal_up);
        mIvRewardArrowDown.setImageResource(R.drawable.little_arror_normal_down);
    }

    private void doSelect() {
        Intent intent = new Intent(this, SelectedDailogActivty.class);
        intent.putExtra(Data, mClassify);
        intent.putExtra(Constant.Activity.Data1, mCategory);
        startActivityForResult(intent, ACTION_SELECT);
    }

    @Override
    public void updataAdapter(GoodsAdapter adapter, boolean isHasNext) {
        if (adapter != null) {
            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
            mRecycleView.setAdapter(adapter);
            mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        }
        if (isHasNext) {
            mSwipeRefreshLayout.resetNoMoreData();
        } else {
            mSwipeRefreshLayout.finishLoadmoreWithNoMoreData();
        }
    }

    @Override
    public void showViewState(boolean isHasNext) {
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
        if (isHasNext) {
            mSwipeRefreshLayout.resetNoMoreData();
        } else {
            mSwipeRefreshLayout.finishLoadmoreWithNoMoreData();
        }
    }

    @Override
    public void noData() {
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
        mSwipeRefreshLayout.finishLoadmoreWithNoMoreData();
    }

    @Override
    public void ReLogin() {
        ToastUtil.showToast(this, "登录超时,请重新登录");
        startActivity(new Intent(this, LoginActivity.class));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Bundle bundle = data.getBundleExtra(Data);
                if (bundle != null) {
                    try {
                        mClassify = bundle.getInt("businessType");
                        mCategory = bundle.getInt("wantedType");
                        mPresenter.getDataWithFilter(mClassify, mCategory);
                    } catch (Exception e) {
                        LogUtils.e(e.getMessage());
                    }
                }
            }
            hideSoftInput();
        }
        if (requestCode == REQUESTCODE) {
            if (data != null) {
                boolean booleanExtra = data.getBooleanExtra(Data, false);
                if (booleanExtra && SettingUtils.isLogin(this))
                    mPresenter.initData();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null)
            mPresenter.unsubcrible();
    }
}
