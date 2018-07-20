package com.zhengdao.zqb.view.fragment.coupons;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kennyc.view.MultiStateView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.customview.DropdownButton;
import com.zhengdao.zqb.customview.DropdownListView;
import com.zhengdao.zqb.entity.DropdownItemObject;
import com.zhengdao.zqb.mvp.MVPBaseFragment;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.utils.ViewUtils;
import com.zhengdao.zqb.view.activity.login.LoginActivity;
import com.zhengdao.zqb.view.adapter.CouponsAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CouponsFragment extends MVPBaseFragment<CouponsContract.View, CouponsPresenter> implements CouponsContract.View, DropdownListView.Container, View.OnClickListener {

    private static final int ACTION_LOGIN = 001;
    @BindView(R.id.iv_search)
    ImageView               mIvSearch;
    @BindView(R.id.et_search)
    EditText                mEtSearch;
    @BindView(R.id.tv_search)
    TextView                mTvSearch;
    @BindView(R.id.toolbar)
    Toolbar                 mToolbar;
    @BindView(R.id.collapsingToolbar)
    CollapsingToolbarLayout mCollapsingToolbar;
    @BindView(R.id.db_synthesize)
    DropdownButton          mDbSynthesize;
    @BindView(R.id.tv_hot)
    TextView                mTvHot;
    @BindView(R.id.iv_hot_arrow_up)
    ImageView               mIvHotArrowUp;
    @BindView(R.id.iv_hot_arrow_down)
    ImageView               mIvHotArrowDown;
    @BindView(R.id.re_price)
    RelativeLayout          mRePrice;
    @BindView(R.id.tv_award)
    TextView                mTvAward;
    @BindView(R.id.iv_reward_arrow_up)
    ImageView               mIvRewardArrowUp;
    @BindView(R.id.iv_reward_arrow_down)
    ImageView               mIvRewardArrowDown;
    @BindView(R.id.re_sale)
    RelativeLayout          mReSale;
    @BindView(R.id.recycle_view)
    RecyclerView            mRecycleView;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout      mSmartRefreshLayout;
    @BindView(R.id.multiStateView)
    MultiStateView          mMultiStateView;
    @BindView(R.id.mask)
    View                    mMask;
    @BindView(R.id.dropdownType)
    DropdownListView        mDropdownType;

    private boolean mIsUpFrist  = false;
    private boolean mIsUpSecond = false;
    private DropdownListView mCurrentDropdownListView;
    private Animation        dropdown_in, dropdown_out, dropdown_mask_out;
    private List<DropdownItemObject> mChooseTypeData = new ArrayList<>();

    @Override
    protected View getFragmentView() {
        View view = View.inflate(getActivity(), R.layout.coupons_fragment, null);
        mUnbinder = ButterKnife.bind(this, view);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    @Override
    protected void initView() {
        initCollapsingPart();
        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                mPresenter.updataData();
                refreshlayout.finishRefresh();
            }
        });
        mSmartRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                mPresenter.getMoreData();
                refreshlayout.finishLoadmore();
            }
        });
        //MultiStateView设置
        mMultiStateView.getView(MultiStateView.VIEW_STATE_ERROR).findViewById(R.id.btn_error_reload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.initData();
            }
        });
        mEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                doSearch();
                return false;
            }
        });
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
        mPresenter.initData();
    }

    private void initCollapsingPart() {
        setHasOptionsMenu(true);
        //头部
        mCollapsingToolbar.setTitle("优惠券");
        mCollapsingToolbar.setCollapsedTitleGravity(Gravity.CENTER);//设置收缩后标题的位置
        mCollapsingToolbar.setExpandedTitleGravity(Gravity.CENTER);//设置展开后标题的位置
        mCollapsingToolbar.setExpandedTitleColor(Color.parseColor("#00ffffff"));//设置展开后状态下字体颜色
        //动画
        dropdown_in = AnimationUtils.loadAnimation(getActivity(), R.anim.dropdown_in);
        dropdown_out = AnimationUtils.loadAnimation(getActivity(), R.anim.dropdown_out);
        dropdown_mask_out = AnimationUtils.loadAnimation(getActivity(), R.anim.dropdown_mask_out);
        //筛选
        reset();
        mChooseTypeData.add(new DropdownItemObject("全部", 0, "全部"));
        mChooseTypeData.add(new DropdownItemObject("淘宝", 1, "淘宝"));
        mChooseTypeData.add(new DropdownItemObject("京东", 3, "京东"));
        mDropdownType.bind(mChooseTypeData, mDbSynthesize, this, 0);
        dropdown_mask_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mCurrentDropdownListView == null) {
                    reset();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        initClickListener();
    }

    private void initClickListener() {
        mMask.setOnClickListener(this);
        mIvSearch.setOnClickListener(this);
        mTvSearch.setOnClickListener(this);
        mRePrice.setOnClickListener(this);
        mReSale.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_search:
                doSearch();
                break;
            case R.id.tv_search:
                doSearch();
                break;
            case R.id.mask:
                hide();
                break;
            case R.id.re_price:
                changeArrow(1);
                break;
            case R.id.re_sale:
                changeArrow(2);
                break;
        }
    }

    private void doSearch() {
        hideSoftInput();
        String trim = mEtSearch.getText().toString().trim();
        mPresenter.getDataWithSearch(TextUtils.isEmpty(trim) ? "" : trim);
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
                sortName = "price";
                mPresenter.getDataWithSort(sortName, orderType);
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
                sortName = "sales";
                mPresenter.getDataWithSort(sortName, orderType);
                break;
        }
    }

    private void reset() {
        mDbSynthesize.setChecked(false);
        mDropdownType.setVisibility(View.GONE);
        mMask.setVisibility(View.GONE);
        mDropdownType.clearAnimation();
        mMask.clearAnimation();
    }

    @Override
    public void show(DropdownListView listView) {
        if (mCurrentDropdownListView != null) {
            mCurrentDropdownListView.clearAnimation();
            mCurrentDropdownListView.startAnimation(dropdown_out);
            mCurrentDropdownListView.setVisibility(View.GONE);
            mCurrentDropdownListView.button.setChecked(false);
        }
        mCurrentDropdownListView = listView;
        mMask.clearAnimation();
        mMask.setVisibility(View.VISIBLE);
        mCurrentDropdownListView.clearAnimation();
        mCurrentDropdownListView.startAnimation(dropdown_in);
        mCurrentDropdownListView.setVisibility(View.VISIBLE);
        mCurrentDropdownListView.button.setChecked(true);
    }

    @Override
    public void hide() {
        if (mCurrentDropdownListView != null) {
            mCurrentDropdownListView.clearAnimation();
            mCurrentDropdownListView.startAnimation(dropdown_out);
            mCurrentDropdownListView.button.setChecked(false);
            mMask.clearAnimation();
            mMask.startAnimation(dropdown_mask_out);
        }
        mCurrentDropdownListView = null;
    }

    @Override
    public void onSelectionChanged(DropdownListView view) {
        DropdownItemObject current = view.current;
        String text = current.text;
        if (!TextUtils.isEmpty(text) && text.equals("全部")) {
            mPresenter.getDataWithType(-1);
        } else if (!TextUtils.isEmpty(text) && text.equals("淘宝")) {
            mPresenter.getDataWithType(0);
        } else if (!TextUtils.isEmpty(text) && text.equals("京东")) {
            mPresenter.getDataWithType(1);
        }
    }

    @Override
    public void noData() {
        mSmartRefreshLayout.finishLoadmoreWithNoMoreData();
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
    }

    @Override
    public void ReLogin() {
        ToastUtil.showToast(getActivity(), "登录超时,请重新登录");
        startActivityForResult(new Intent(getActivity(), LoginActivity.class), ACTION_LOGIN);
    }

    @Override
    public void setAdapter(CouponsAdapter adapter, boolean isHasNext) {
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
        if (adapter != null) {
            mSmartRefreshLayout.finishRefresh();
            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
            mRecycleView.setAdapter(adapter);
            mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
        if (isHasNext) {
            mSmartRefreshLayout.resetNoMoreData();
        } else {
            mSmartRefreshLayout.finishLoadmoreWithNoMoreData();
        }
    }

    @Override
    public void updateAdapter(boolean isHasNext) {
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
        if (isHasNext) {
            mSmartRefreshLayout.resetNoMoreData();
        } else {
            mSmartRefreshLayout.finishLoadmoreWithNoMoreData();
        }
    }

    @Override
    public void showErrorMessage(String msg) {
        super.showErrorMessage(msg);
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_LOGIN) {
            if (data != null) {
                boolean booleanExtra = data.getBooleanExtra(Constant.Activity.Data, false);
                if (booleanExtra && SettingUtils.isLogin(getActivity()))
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

    @Override
    public void search(String value) {
        LogUtils.i("search");
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SpacesItemDecoration(int space) {
            this.space = ViewUtils.dip2px(getActivity(), space);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.bottom = space;
        }
    }
}
