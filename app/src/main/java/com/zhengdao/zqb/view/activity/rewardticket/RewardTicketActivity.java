package com.zhengdao.zqb.view.activity.rewardticket;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.ViewUtils;
import com.zhengdao.zqb.view.fragment.rewardticket.RewardTicketFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RewardTicketActivity extends MVPBaseActivity<RewardTicketContract.View, RewardTicketPresenter> implements RewardTicketContract.View{

    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    private ArrayList<Fragment> mFragments = new ArrayList();
    private ViewPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_ticket);
        ButterKnife.bind(this);
        setTitle("卡券");
        init();
    }

    private void init() {
        mFragments.add(RewardTicketFragment.newInstance(0));
        mFragments.add(RewardTicketFragment.newInstance(1));
        mFragments.add(RewardTicketFragment.newInstance(2));

        mPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mPagerAdapter.setFragments(mFragments);
        mViewPager.setOffscreenPageLimit(mFragments.size());
        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        mTabLayout.getTabAt(0).setText("未使用");
        mTabLayout.getTabAt(1).setText("已使用");
        mTabLayout.getTabAt(2).setText("已过期");

        ViewUtils.reflex(this, mTabLayout, 10);
    }

    public void onGetData(int type1, int type2, int type3) {
        mTabLayout.getTabAt(0).setText("未使用" + "(" + type1 + ")");
        mTabLayout.getTabAt(1).setText("已使用" + "(" + type2 + ")");
        mTabLayout.getTabAt(2).setText("已过期" + "(" + type3 + ")");
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
