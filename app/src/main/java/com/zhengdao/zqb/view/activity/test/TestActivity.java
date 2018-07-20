package com.zhengdao.zqb.view.activity.test;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.application.ExitApplication;
import com.zhengdao.zqb.customview.DividerItemDecoration;
import com.zhengdao.zqb.customview.DropdownButton;
import com.zhengdao.zqb.customview.DropdownListView;
import com.zhengdao.zqb.entity.DropdownItemObject;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TestActivity extends MVPBaseActivity<TestContract.View, TestPresenter> implements TestContract.View, DropdownListView.Container {


    @BindView(R.id.iv_search)
    ImageView               mIvSearch;
    @BindView(R.id.et_search)
    EditText                mEtSearch;
    @BindView(R.id.tv_search)
    TextView                mTvSearch;
    @BindView(R.id.search_bar)
    LinearLayout            mSearchBar;
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
    @BindView(R.id.mRecyclerView)
    RecyclerView            mRecyclerView;
    @BindView(R.id.mask)
    View                    mMask;
    @BindView(R.id.dropdownType)
    DropdownListView        mDropdownType;

    private Animation dropdown_in, dropdown_out, dropdown_mask_out;
    private List<DropdownItemObject> chooseTypeData = new ArrayList<>();//选择类型
    private DropdownListView currentDropdownList;
    private List<String>     mData;
    private DemoAdapter      mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ExitApplication.getInstance().addActivity(this);
        setSwipeBackEnable(false);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        mCollapsingToolbar.setTitle("优惠券");
        mCollapsingToolbar.setCollapsedTitleGravity(Gravity.CENTER);//设置收缩后标题的位置
        mCollapsingToolbar.setExpandedTitleGravity(Gravity.CENTER);//设置展开后标题的位置
        mCollapsingToolbar.setExpandedTitleColor(Color.parseColor("#00ffffff"));//设置展开后状态下字体颜色

        dropdown_in = AnimationUtils.loadAnimation(this, R.anim.dropdown_in);
        dropdown_out = AnimationUtils.loadAnimation(this, R.anim.dropdown_out);
        dropdown_mask_out = AnimationUtils.loadAnimation(this, R.anim.dropdown_mask_out);
        initSectionData();
        mMask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });
        mData = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            mData.add("标题" + i);
        }
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mAdapter = new DemoAdapter(this, R.layout.item_test_demo, mData);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);//设置adapter
    }

    void initSectionData() {
        reset();
        //        mDbSynthesize.setText("");
        chooseTypeData.add(new DropdownItemObject("全部", 0, "全部"));
        chooseTypeData.add(new DropdownItemObject("淘宝", 1, "淘宝"));
        chooseTypeData.add(new DropdownItemObject("天猫", 2, "天猫"));
        chooseTypeData.add(new DropdownItemObject("京东", 3, "京东"));
        mDropdownType.bind(chooseTypeData, mDbSynthesize, this, 0);
        dropdown_mask_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (currentDropdownList == null) {
                    reset();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    void reset() {
        mDbSynthesize.setChecked(false);
        mDropdownType.setVisibility(View.GONE);
        mMask.setVisibility(View.GONE);
        mDropdownType.clearAnimation();
        mMask.clearAnimation();
    }

    @Override
    public void show(DropdownListView listView) {
        if (currentDropdownList != null) {
            currentDropdownList.clearAnimation();
            currentDropdownList.startAnimation(dropdown_out);
            currentDropdownList.setVisibility(View.GONE);
            currentDropdownList.button.setChecked(false);
        }
        currentDropdownList = listView;
        mMask.clearAnimation();
        mMask.setVisibility(View.VISIBLE);
        currentDropdownList.clearAnimation();
        currentDropdownList.startAnimation(dropdown_in);
        currentDropdownList.setVisibility(View.VISIBLE);
        currentDropdownList.button.setChecked(true);
    }

    @Override
    public void hide() {
        if (currentDropdownList != null) {
            currentDropdownList.clearAnimation();
            currentDropdownList.startAnimation(dropdown_out);
            currentDropdownList.button.setChecked(false);
            mMask.clearAnimation();
            mMask.startAnimation(dropdown_mask_out);
        }
        currentDropdownList = null;
    }

    @Override
    public void onSelectionChanged(DropdownListView view) {

    }

    class DemoAdapter extends CommonAdapter<String> {
        int mLayoutId;

        public DemoAdapter(Context context, int LayoutId, List<String> str) {
            super(context, LayoutId, str);
            mLayoutId = LayoutId;
        }

        @Override
        protected void convert(ViewHolder holder, String str, int position) {
            holder.setText(R.id.tvTitle, str);
            holder.setOnClickListener(R.id.tvTitle, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String bug = bug();
                    if (!TextUtils.isEmpty(bug))
                        ToastUtil.showToast(TestActivity.this, bug);
                }
            });
        }

    }

    private String bug() {
        String str = null;
        return str;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
