package com.zhengdao.zqb.view.fragment.yearrankinglist;


import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kennyc.view.MultiStateView;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.RankingListEntity;
import com.zhengdao.zqb.mvp.MVPBaseFragment;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.view.activity.login.LoginActivity;
import com.zhengdao.zqb.view.adapter.RankingListAdapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class YearrankinglistFragment extends MVPBaseFragment<YearrankinglistContract.View, YearrankinglistPresenter> implements YearrankinglistContract.View {


    private static final int REQUEST_CODE = 101;
    @BindView(R.id.recycle_view)
    RecyclerView       mRecycleView;
    @BindView(R.id.multiStateView)
    MultiStateView     mMultiStateView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.tv_ranking)
    TextView           mTvRanking;
    @BindView(R.id.iv_icon)
    CircleImageView    mIvIcon;
    @BindView(R.id.tv_name)
    TextView           mTvName;
    @BindView(R.id.tv_price)
    TextView           mTvPrice;

    private ArrayList<RankingListEntity.AllUser> mData;
    private RankingListAdapter                   mAdapter;

    @Override
    protected View getFragmentView() {
        View view = View.inflate(getActivity(), R.layout.yearrankinglist_fragment, null);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.color_main);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getData();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        mMultiStateView.getView(MultiStateView.VIEW_STATE_ERROR).findViewById(R.id.btn_error_reload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.getData();
            }
        });
        mPresenter.getData();
    }

    @Override
    public void showErrorMessage(String msg) {
        super.showErrorMessage(msg);
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
    }

    @Override
    public void onGetDataFinished(RankingListEntity httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
            RankingListEntity.User user = httpResult.user;
            List<RankingListEntity.AllUser> allUser = httpResult.allUser;
            if (mData == null)
                mData = new ArrayList();
            mData.clear();
            if (allUser != null)
                mData.addAll(allUser);
            if (mAdapter == null)
                mAdapter = new RankingListAdapter(getActivity(), mData);
            mRecycleView.setAdapter(mAdapter);
            mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
            doAddFootView(user);
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(getActivity(), "登录超时,请重新登录");
            startActivityForResult(new Intent(getActivity(), LoginActivity.class), REQUEST_CODE);
        } else {
            ToastUtil.showToast(getActivity(), TextUtils.isEmpty(httpResult.msg) ? "" : httpResult.msg);
        }
    }

    private void doAddFootView(RankingListEntity.User user) {
        mTvRanking.setText(TextUtils.isEmpty(user.number) ? "" : user.number);
        mTvName.setText(TextUtils.isEmpty(user.nickName) ? "" : user.nickName);
        if (!TextUtils.isEmpty(user.avatar))
            Glide.with(getActivity()).load(user.avatar).error(R.drawable.default_icon).into(mIvIcon);
        mTvPrice.setText(new DecimalFormat("#0.00").format(user.sunMoney));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            mPresenter.getData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
