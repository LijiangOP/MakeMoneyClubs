package com.zhengdao.zqb.view.activity.mywanted;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.ViewUtils;
import com.zhengdao.zqb.view.fragment.wanted.WantedFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MyWantedActivity extends MVPBaseActivity<MyWantedContract.View, MyWantedPresenter> implements MyWantedContract.View {

    private static final int ALLSTATE    = -1;
    private static final int UN_COMMITED = 0;
    private static final int COMMITED    = 1;
    private static final int UN_DONE     = 2;
    private static final int DONE        = 3;

    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;


    private ArrayList<Fragment> mFragments = new ArrayList();
    private ViewPagerAdapter mPagerAdapter;
    private String           mSeleted;
    private int mCurrentPostion = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wanted);
        ButterKnife.bind(this);
        setTitle("我的悬赏");
        init();
    }

    private void init() {
        mSeleted = getIntent().getStringExtra(Constant.Activity.Select);
        if (!TextUtils.isEmpty(mSeleted) && mSeleted.equals("un_commited")) {
            mCurrentPostion = 1;
        } else if (!TextUtils.isEmpty(mSeleted) && mSeleted.equals("commited")) {
            mCurrentPostion = 2;
        } else if (!TextUtils.isEmpty(mSeleted) && mSeleted.equals("un_done")) {
            mCurrentPostion = 3;
        } else if (!TextUtils.isEmpty(mSeleted) && mSeleted.equals("done")) {
            mCurrentPostion = 4;
        } else {
            mCurrentPostion = 0;
        }

        mFragments.add(WantedFragment.newInstance(ALLSTATE));
        mFragments.add(WantedFragment.newInstance(UN_COMMITED));
        mFragments.add(WantedFragment.newInstance(COMMITED));
        mFragments.add(WantedFragment.newInstance(UN_DONE));
        mFragments.add(WantedFragment.newInstance(DONE));

        mPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mPagerAdapter.setFragments(mFragments);
        mViewPager.setOffscreenPageLimit(mFragments.size());
        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        mTabLayout.getTabAt(0).setText("全部");
        mTabLayout.getTabAt(1).setText("未提交");
        mTabLayout.getTabAt(2).setText("已提交");
        mTabLayout.getTabAt(3).setText("未完成");
        mTabLayout.getTabAt(4).setText("已完成");

        ViewUtils.reflex(mTabLayout);
        mTabLayout.getTabAt(mCurrentPostion).select();
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

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
