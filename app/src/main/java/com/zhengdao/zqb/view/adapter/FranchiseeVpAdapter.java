package com.zhengdao.zqb.view.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.zhengdao.zqb.mvp.MVPBaseFragment;

import java.util.List;


/**
 * @创建者 cairui
 * @创建时间 2017/3/14 14:06
 */
public class FranchiseeVpAdapter extends FragmentPagerAdapter {
    private String[]              titleArr;
    private List<MVPBaseFragment> mData;

    public FranchiseeVpAdapter(FragmentManager fm, List<MVPBaseFragment> data, String[] titleArr) {
        super(fm);
        this.mData = data;
        this.titleArr = titleArr;
    }

    @Override
    public Fragment getItem(int position) {
        return mData == null ? null : mData.get(position);
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return titleArr[0];
        } else {
            return titleArr[1];
        }
    }
}
