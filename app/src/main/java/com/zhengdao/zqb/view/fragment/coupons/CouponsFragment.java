package com.zhengdao.zqb.view.fragment.coupons;


import android.graphics.Color;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.mvp.MVPBaseFragment;
import com.zhengdao.zqb.utils.ViewUtils;
import com.zhengdao.zqb.view.fragment.couponsdetail.CouponsdetailFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CouponsFragment extends MVPBaseFragment<CouponsContract.View, CouponsPresenter> implements CouponsContract.View, View.OnClickListener {


    @BindView(R.id.iv_search)
    ImageView               mIvSearch;
    @BindView(R.id.et_search)
    EditText                mEtSearch;
    @BindView(R.id.tv_search)
    TextView                mTvSearch;
    @BindView(R.id.search_bar)
    LinearLayout            mSearchBar;
    @BindView(R.id.toolbar)
    Toolbar                 mToolbar;
    @BindView(R.id.collapsingToolbar)
    CollapsingToolbarLayout mCollapsingToolbar;
    @BindView(R.id.tabLayout)
    TabLayout               mTabLayout;
    @BindView(R.id.view_pager)
    ViewPager               mViewPager;
    private ArrayList<Fragment> mFragments      = new ArrayList<>();
    private ViewPagerAdapter    mPagerAdapter;
    private int                 mCurrentPostion = 0;

    public interface CommunicationCallBack {
        void getDataWithSearch(String value);
    }

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
        setHasOptionsMenu(true);
        //头部
        mCollapsingToolbar.setTitle("优惠券");
        mCollapsingToolbar.setCollapsedTitleGravity(Gravity.CENTER);//设置收缩后标题的位置
        mCollapsingToolbar.setExpandedTitleGravity(Gravity.CENTER);//设置展开后标题的位置
        mCollapsingToolbar.setExpandedTitleColor(Color.parseColor("#00ffffff"));//设置展开后状态下字体颜色
        //列表
        mFragments.add(CouponsdetailFragment.newInstance(Constant.Coupons.TaoBao));
        mFragments.add(CouponsdetailFragment.newInstance(Constant.Coupons.JinDong));
        //        mFragments.add(CouponsdetailFragment.newInstance(Constant.Coupons.PingDD));
        //        mFragments.add(CouponsdetailFragment.newInstance(Constant.Coupons.SuNingYG));
        //        mFragments.add(CouponsdetailFragment.newInstance(Constant.Coupons.WangYiKL));
        mPagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        mPagerAdapter.setFragments(mFragments);
        mViewPager.setOffscreenPageLimit(mFragments.size());
        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setText("淘宝");
        mTabLayout.getTabAt(1).setText("京东");
        //        mTabLayout.getTabAt(2).setText("拼多多");
        //        mTabLayout.getTabAt(3).setText("苏宁易购");
        //        mTabLayout.getTabAt(4).setText("网易考拉");
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mCurrentPostion = tab.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        mTabLayout.getTabAt(mCurrentPostion).select();
        ViewUtils.reflex(getActivity(), mTabLayout, 2, 50);//修改TabLayou的下标宽度
        initClickListener();
    }

    private void initClickListener() {
        mIvSearch.setOnClickListener(this);
        mTvSearch.setOnClickListener(this);
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
        }
    }

    /**
     * 搜索
     */
    private void doSearch() {
        hideSoftInput();
        String trim = mEtSearch.getText().toString().trim();
        CouponsdetailFragment fragment = (CouponsdetailFragment) mPagerAdapter.getItem(mCurrentPostion);
        fragment.getDataWithSearch(TextUtils.isEmpty(trim) ? "" : trim);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null)
            mPresenter.unsubcrible();
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> mFragmentList;

        public void setFragments(ArrayList<Fragment> fragments) {
            mFragmentList = fragments;
        }


        public ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = mFragmentList.get(position);
            return fragment;
        }

        @Override
        public int getCount() {
            return mFragmentList == null ? 0 : mFragmentList.size();
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            try {
                super.finishUpdate(container);
            } catch (NullPointerException nullPointerException) {

            }
        }
    }
}
