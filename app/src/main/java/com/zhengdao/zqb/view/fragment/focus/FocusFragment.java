package com.zhengdao.zqb.view.fragment.focus;


import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.mvp.MVPBaseFragment;
import com.zhengdao.zqb.utils.ViewUtils;
import com.zhengdao.zqb.view.fragment.news.NewsFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FocusFragment extends MVPBaseFragment<FocusContract.View, FocusPresenter> implements FocusContract.View {

    @BindView(R.id.fake_status_bar)
    View      mFakeStatusBar;
    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private ViewPagerAdapter mPagerAdapter;
    private int mCurrentPostion = 0;

    @Override
    protected View getFragmentView() {
        View view = View.inflate(getActivity(), R.layout.focus_fragment, null);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        mFakeStatusBar.setBackgroundColor(calculateStatusColor(getResources().getColor(R.color.main), 0));
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
        mPagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
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
        ViewUtils.reflex(getActivity(), mTabLayout, 7, 10);
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
    public void onDestroy() {
        super.onDestroy();
        if (null != mFragments) {
            for (int i = 0; i < mFragments.size(); i++) {
                Fragment fragment = mFragments.get(i);
                if (fragment != null)
                    fragment.onDestroy();
            }
            mFragments.clear();
        }
    }
}
