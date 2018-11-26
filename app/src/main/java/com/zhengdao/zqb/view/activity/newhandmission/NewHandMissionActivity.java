package com.zhengdao.zqb.view.activity.newhandmission;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.kennyc.view.MultiStateView;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.customview.GameListDialog;
import com.zhengdao.zqb.customview.SignedWindow;
import com.zhengdao.zqb.entity.GameListHttpResult;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.NewBieHttpEntity;
import com.zhengdao.zqb.entity.SurveyHttpResult;
import com.zhengdao.zqb.entity.TaskEntity;
import com.zhengdao.zqb.event.NewHandUpDataEvent;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.utils.SkipUtils;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.utils.Utils;
import com.zhengdao.zqb.view.activity.dailysign.DailySignActivity;
import com.zhengdao.zqb.view.activity.dailywechatshare.DailyWeChatShareActivity;
import com.zhengdao.zqb.view.activity.login.LoginActivity;
import com.zhengdao.zqb.view.activity.main.MainActivity;
import com.zhengdao.zqb.view.activity.questionsurvery.QuestionSurveryActivty;
import com.zhengdao.zqb.view.activity.webview.WebViewActivity;
import com.zhengdao.zqb.view.activity.welfareget.WelfareGetActivity;
import com.zhengdao.zqb.view.adapter.GameListAdapter;
import com.zhengdao.zqb.view.adapter.NewHandMissionAdapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


public class NewHandMissionActivity extends MVPBaseActivity<NewHandMissionContract.View, NewHandMissionPresenter> implements NewHandMissionContract.View, NewHandMissionAdapter.CallBack {
    private static final int TYPE_REGIST        = 0;
    private static final int TYPE_BINDALIPAY    = 1;
    private static final int TYPE_MARKETCOMMENT = 2;
    private static final int TYPE_RECOMMEND     = 3;
    private static final int TYPE_SHARE         = 4;
    private static final int TYPE_SIGN          = 5;

    private static final int TYPE_WANTED_REWARD  = 6;
    private static final int TYPE_GAME_REWARD    = 7;
    private static final int TYPE_SHOUTU_REWARD  = 8;
    private static final int TYPE_WENJUAN_REWARD = 9;
    private static final int TYPE_GUANGGAO       = 10;
    private static final int TYPE_XINSHOU_FULI   = 11;

    private static final int REQUESTCODE  = 100;
    private static final int ACTION_LOGIN = 101;
    @BindView(R.id.tv_balance)
    TextView           mTvBalance;
    @BindView(R.id.multiStateView)
    MultiStateView     mMultiStateView;
    @BindView(R.id.swiperefreshlayout)
    SwipeRefreshLayout mSwiperefreshlayout;
    @BindView(R.id.recycle_view)
    RecyclerView       mRecycleView;

    private NewHandMissionAdapter mAdapter;
    private List<TaskEntity>      mData;
    private Disposable            mUpDataDisposable;
    private Double                mUseableSum;
    private SignedWindow          mSignedDialog;
    private GameListDialog        mGameListDialog;
    private int                   mCurrentType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newhand_mission);
        ButterKnife.bind(this);
        setTitle("每日任务");
        init();
    }

    private void init() {
        mSwiperefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getData();
                mSwiperefreshlayout.setRefreshing(false);
            }
        });
        mMultiStateView.getView(MultiStateView.VIEW_STATE_ERROR).findViewById(R.id.btn_error_reload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.getData();
            }
        });
        mUpDataDisposable = RxBus.getDefault().toObservable(NewHandUpDataEvent.class).subscribe(new Consumer<NewHandUpDataEvent>() {
            @Override
            public void accept(NewHandUpDataEvent newHandUpDataEvent) throws Exception {
                mPresenter.getData();
            }
        });
        mPresenter.getData();
    }

    @Override
    public void showView(NewBieHttpEntity httpResult) {
        try {
            if (httpResult.code == Constant.HttpResult.SUCCEED) {
                if (httpResult.newbieTasks == null || httpResult.newbieTasks.size() == 0) {
                    mMultiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
                    return;
                }
                mUseableSum = httpResult.sum;
                mTvBalance.setText("" + new DecimalFormat("#0.00").format(httpResult.sum));
                if (mData == null)
                    mData = new ArrayList<>();
                mData.clear();
                ArrayList<TaskEntity> objects1 = new ArrayList<>();
                ArrayList<TaskEntity> objects2 = new ArrayList<>();
                ArrayList<TaskEntity> objects3 = new ArrayList<>();
                for (int i = 0; i < httpResult.newbieTasks.size(); i++) {
                    int type = httpResult.newbieTasks.get(i).type;
                    if (type == 4 || type == 5 || type == 10) {
                        objects1.add(new TaskEntity(1, httpResult.newbieTasks.get(i)));
                    } else if (type == 6 || type == 7 || type == 8 || type == 9) {
                        objects3.add(new TaskEntity(1, httpResult.newbieTasks.get(i)));
                    } else {
                        objects2.add(new TaskEntity(1, httpResult.newbieTasks.get(i)));
                    }
                }
                if (objects1.size() > 0)
                    mData.add(new TaskEntity(0, "每日任务"));
                mData.addAll(objects1);
                if (objects2.size() > 0)
                    mData.add(new TaskEntity(0, "新手任务"));
                mData.addAll(objects2);
                if (objects3.size() > 0)
                    mData.add(new TaskEntity(0, "奖励任务"));
                mData.addAll(objects3);
                mData.add(new TaskEntity(10));//广告
                if (mAdapter == null) {
                    mAdapter = new NewHandMissionAdapter(NewHandMissionActivity.this, mData, this);
                    mRecycleView.setAdapter(mAdapter);
                    mRecycleView.setLayoutManager(new LinearLayoutManager(this));
                } else
                    mAdapter.notifyDataSetChanged();
            } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
                ToastUtil.showToast(this, "登录超时,请重新登录");
                startActivityForResult(new Intent(this, LoginActivity.class), REQUESTCODE);
            } else {
                ToastUtil.showToast(NewHandMissionActivity.this, TextUtils.isEmpty(httpResult.msg) ? "获取数据失败" : httpResult.msg);
                mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
            }
        } catch (Exception ex) {
            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
            LogUtils.e(ex.getMessage());
        }
    }

    @Override
    public void onItemClick(int type, int state) {
        if (state == 2) {
            switch (type) {
                case TYPE_SHARE://分享朋友圈
                    Intent intent = new Intent(NewHandMissionActivity.this, DailyWeChatShareActivity.class);
                    intent.putExtra(Constant.Activity.Data, mUseableSum);
                    startActivity(intent);
                    break;
                case TYPE_SIGN://签到
                    startActivity(new Intent(NewHandMissionActivity.this, DailySignActivity.class));
                    break;
                case TYPE_WENJUAN_REWARD://问卷调查奖励
                    if (!SettingUtils.isLogin(NewHandMissionActivity.this))
                        startActivity(new Intent(NewHandMissionActivity.this, LoginActivity.class));
                    else
                        mPresenter.getSurveyLink();
                    mCurrentType = TYPE_WENJUAN_REWARD;
                    break;
                case TYPE_WANTED_REWARD://悬赏奖励
                    startActivity(new Intent(NewHandMissionActivity.this, MainActivity.class));
                    break;
                case TYPE_GAME_REWARD://游戏奖励
                    mPresenter.getGameList();
                    break;
                case TYPE_XINSHOU_FULI://新手福利
                    mCurrentType = TYPE_XINSHOU_FULI;
                    mPresenter.getSurveyLink();
                    break;
            }
        } else if (state == 4) {
            mPresenter.getGameReward(type);
        }
    }

    @Override
    public void onBaiduAdvClick() {
        mPresenter.getSeeAdvReward(2, 1);
    }

    @Override
    public void onTencentAdvClick() {
        mPresenter.getSeeAdvReward(2, 2);
    }

    @Override
    public void onGetAdvReward(HttpResult httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            LogUtils.i(TextUtils.isEmpty(httpResult.msg) ? "获取奖励成功" : httpResult.msg);
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            LogUtils.e("登录超时");
        } else {
            LogUtils.e(TextUtils.isEmpty(httpResult.msg) ? "请求失败" : httpResult.msg);
        }
    }

    @Override
    public void onRewardGet(HttpResult httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            mPresenter.getData();
            ToastUtil.showToast(NewHandMissionActivity.this, TextUtils.isEmpty(httpResult.msg) ? "获取奖励成功" : httpResult.msg);
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            LogUtils.e("登录超时");
        } else {
            LogUtils.e(TextUtils.isEmpty(httpResult.msg) ? "请求失败" : httpResult.msg);
        }
    }

    @Override
    public void onSurveyLinkGet(SurveyHttpResult httpResult) {
        if (httpResult == null) {
            ToastUtil.showToast(NewHandMissionActivity.this, "请求出错");
            return;
        }
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            switch (mCurrentType) {
                case TYPE_WENJUAN_REWARD:
                    if (!TextUtils.isEmpty(httpResult.url)) {
                        Intent survey = new Intent(NewHandMissionActivity.this, WebViewActivity.class);
                        survey.putExtra(Constant.WebView.TITLE, getString(R.string.home_survey));
                        survey.putExtra(Constant.WebView.URL, httpResult.url);
                        startActivity(survey);
                    }
                    break;
                case TYPE_XINSHOU_FULI:
                    Intent welfare = new Intent(NewHandMissionActivity.this, WelfareGetActivity.class);
                    welfare.putExtra(Constant.Activity.Data, 1);
                    Utils.StartActivity(NewHandMissionActivity.this, welfare);
                    break;
            }
        } else if (httpResult.code == Constant.HttpResult.FAILD) {
            Intent survery = new Intent(NewHandMissionActivity.this, QuestionSurveryActivty.class);
            survery.putExtra(Constant.Activity.Skip, mCurrentType == TYPE_WENJUAN_REWARD ? "survey" : "welfare");
            Utils.StartActivity(NewHandMissionActivity.this, survery);
            ToastUtil.showToast(NewHandMissionActivity.this, "请完善用户信息");
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(NewHandMissionActivity.this, "登录超时,请重新登录");
            startActivity(new Intent(NewHandMissionActivity.this, LoginActivity.class));
        } else {
            ToastUtil.showToast(NewHandMissionActivity.this, TextUtils.isEmpty(httpResult.msg) ? "请求失败" : httpResult.msg);
        }
    }

    @Override
    public void onGetGameList(GameListHttpResult httpResult) {
        if (httpResult == null) {
            ToastUtil.showToast(NewHandMissionActivity.this, "请求出错");
            return;
        }
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            ArrayList<GameListHttpResult.Game> games = httpResult.games;
            if (games != null && games.size() > 0) {
                mGameListDialog = new GameListDialog(NewHandMissionActivity.this);
                mGameListDialog.initView(games, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mGameListDialog.dismiss();
                    }
                }, new GameListAdapter.CallBack() {
                    @Override
                    public void onItemClick(int type) {
                        SkipUtils.SkipGame(type, NewHandMissionActivity.this);
                        mGameListDialog.dismiss();
                    }
                });
                mGameListDialog.show();
            }
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(NewHandMissionActivity.this, "登录超时,请重新登录");
            startActivity(new Intent(NewHandMissionActivity.this, LoginActivity.class));
        } else {
            ToastUtil.showToast(NewHandMissionActivity.this, TextUtils.isEmpty(httpResult.msg) ? "请求失败" : httpResult.msg);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUESTCODE:
                if (data != null) {
                    boolean booleanExtra = data.getBooleanExtra(Constant.Activity.Data, false);
                    if (booleanExtra)
                        mPresenter.getData();
                }
                break;
            case ACTION_LOGIN:
                if (data != null) {
                    boolean booleanExtra = data.getBooleanExtra(Constant.Activity.Data, false);
                    if (booleanExtra) {
                        if (SettingUtils.isLogin(NewHandMissionActivity.this))
                            mPresenter.getData();
                    } else {
                        startActivity(new Intent(NewHandMissionActivity.this, MainActivity.class));
                    }
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUpDataDisposable != null)
            mUpDataDisposable.dispose();
        if (mSignedDialog != null) {
            mSignedDialog.dismiss();
            mSignedDialog = null;
        }
        if (mGameListDialog != null) {
            mGameListDialog.dismiss();
            mGameListDialog = null;
        }
    }
}
