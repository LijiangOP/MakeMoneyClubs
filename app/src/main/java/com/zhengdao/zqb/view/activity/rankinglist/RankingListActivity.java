package com.zhengdao.zqb.view.activity.rankinglist;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.customview.SlidingTabLayout;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.mvp.MVPBaseFragment;
import com.zhengdao.zqb.view.fragment.moonrankinglist.MoonrankinglistFragment;
import com.zhengdao.zqb.view.fragment.yearrankinglist.YearrankinglistFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RankingListActivity extends MVPBaseActivity<RankingListContract.View, RankingListPresenter> implements RankingListContract.View {
    @BindView(R.id.tab_layout)
    SlidingTabLayout mTabLayout;
    @BindView(R.id.id_vp)
    ViewPager        mViewPager;

    private List<MVPBaseFragment> mTabContents = new ArrayList<MVPBaseFragment>();
    private List<String>          mDatas       = Arrays.asList("本月排行", "总排行");
    private FragmentPagerAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rankinglist);
        ButterKnife.bind(this);
        setTitle("排行榜");
        initDatas();
    }

    private void initDatas() {
        mTabContents.add(new MoonrankinglistFragment());
        mTabContents.add(new YearrankinglistFragment());
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mTabContents.size();
            }

            @Override
            public Fragment getItem(int position) {
                return mTabContents.get(position);
            }
        };
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setData(mDatas);
        mTabLayout.setViewPager(mViewPager, 0);
    }
}
