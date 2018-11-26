package com.zhengdao.zqb.view.activity.news;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.ViewUtils;
import com.zhengdao.zqb.view.fragment.news.NewsFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsActivity extends MVPBaseActivity<NewsContract.View, NewsPresenter> implements NewsContract.View {

    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private ViewPagerAdapter mPagerAdapter;
    private int mCurrentPostion = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        ButterKnife.bind(this);
        setTitle("新闻资讯");
        init();
    }

    private void init() {
        mFragments.add(NewsFragment.newInstance("top"));
        mFragments.add(NewsFragment.newInstance("shehui"));
        mFragments.add(NewsFragment.newInstance("guonei"));
        mFragments.add(NewsFragment.newInstance("guoji"));
        mFragments.add(NewsFragment.newInstance("yule"));
        mFragments.add(NewsFragment.newInstance("tiyu"));
        mFragments.add(NewsFragment.newInstance("junshi"));
        mFragments.add(NewsFragment.newInstance("keji"));
        mFragments.add(NewsFragment.newInstance("caijing"));
        mFragments.add(NewsFragment.newInstance("shishang"));
        mPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mPagerAdapter.setFragments(mFragments);
        mViewPager.setOffscreenPageLimit(mFragments.size());
        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setText("推荐");
        mTabLayout.getTabAt(1).setText("社会");
        mTabLayout.getTabAt(2).setText("国内");
        mTabLayout.getTabAt(3).setText("国际");
        mTabLayout.getTabAt(4).setText("娱乐");
        mTabLayout.getTabAt(5).setText("体育");
        mTabLayout.getTabAt(6).setText("军事");
        mTabLayout.getTabAt(7).setText("科技");
        mTabLayout.getTabAt(8).setText("财经");
        mTabLayout.getTabAt(9).setText("时尚");
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
        ViewUtils.reflex(NewsActivity.this, mTabLayout, 7, 10);
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

        @Override
        public void finishUpdate(ViewGroup container) {
            try {
                super.finishUpdate(container);
            } catch (NullPointerException nullPointerException) {

            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mFragments) {
            mFragments.clear();
        }
    }
}
