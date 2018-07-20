package com.zhengdao.zqb.view.activity.relevanceaccount;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.tencent.connect.common.Constants;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.RelevanceAccountEntity;
import com.zhengdao.zqb.event.WechatBindEvent;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.LoginUtils;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.view.activity.login.LoginActivity;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static com.zhengdao.zqb.application.ClientAppLike.mTencent;
import static com.zhengdao.zqb.application.ClientAppLike.mWxApi;
import static com.zhengdao.zqb.config.Constant.Third.LOGIN_TYPE_QQ;
import static com.zhengdao.zqb.config.Constant.Third.LOGIN_TYPE_WECHAT;

public class RelevanceAccountActivity extends MVPBaseActivity<RelevanceAccountContract.View, RelevanceAccountPresenter> implements RelevanceAccountContract.View, CompoundButton.OnCheckedChangeListener {
    @BindView(R.id.switch_wechat)
    Switch mSwitchWechat;
    @BindView(R.id.switch_qq)
    Switch mSwitchQq;
    private boolean mIsFromUserQQ     = true;
    private boolean mIsFromUserWeChat = true;
    private int        mRelationId;
    private Disposable mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.relevance_account);
        ButterKnife.bind(this);
        setTitle("关联账号");
        init();
    }

    private void init() {
        mSwitchWechat.setOnCheckedChangeListener(this);
        mSwitchQq.setOnCheckedChangeListener(this);
        mPresenter.getData();
        mDisposable = RxBus.getDefault().toObservable(WechatBindEvent.class).subscribe(new Consumer<WechatBindEvent>() {
            @Override
            public void accept(WechatBindEvent wechatBindEvent) throws Exception {
                onAccountBind(wechatBindEvent.state, wechatBindEvent.msg, wechatBindEvent.type);
            }
        });
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        LogUtils.e("" + System.currentTimeMillis());
        switch (buttonView.getId()) {
            case R.id.switch_wechat:
                if (mIsFromUserWeChat) {
                    if (!isChecked) {
                        mSwitchWechat.setChecked(true);
                        mSwitchWechat.setSelected(true);
                        mPresenter.doAccountUnBind(mRelationId, LOGIN_TYPE_WECHAT);
                    } else {
                        mSwitchWechat.setChecked(false);
                        mSwitchWechat.setSelected(false);
                        showProgress();
                        doWechatBind();
                    }
                }
                mIsFromUserWeChat = true;
                break;
            case R.id.switch_qq:
                if (mIsFromUserQQ) {
                    if (!isChecked) {
                        mSwitchQq.setChecked(true);
                        mSwitchQq.setSelected(true);
                        mPresenter.doAccountUnBind(mRelationId, LOGIN_TYPE_QQ);
                    } else {
                        mSwitchQq.setChecked(false);
                        mSwitchQq.setSelected(false);
                        showProgress();
                        LoginUtils.loginQQ(this, mIUiListener);
                    }
                }
                mIsFromUserQQ = true;
                break;
        }
    }

    private void doWechatBind() {
        if (!mWxApi.isWXAppInstalled()) {
            ToastUtil.showToast(this, "您还未安装微信客户端");
            return;
        }
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = Constant.WechatReq.Bindstate;
        mWxApi.sendReq(req);
    }

    IUiListener mIUiListener = new IUiListener() {
        @Override
        public void onComplete(Object o) {
            hideProgress();
            if (null == o) {
                ToastUtil.showToast(RelevanceAccountActivity.this, "登录失败");
                return;
            }
            JSONObject jsonResponse = (JSONObject) o;
            if (null != jsonResponse && jsonResponse.length() == 0) {
                ToastUtil.showToast(RelevanceAccountActivity.this, "登录失败");
                return;
            }
            LogUtils.i("QQ登录成功返回结果-" + o.toString());
            LoginUtils.initOpenidAndToken(jsonResponse);
            if (mTencent != null && mTencent.isSessionValid())
                mPresenter.doAccountBind(mTencent.getOpenId(), LOGIN_TYPE_QQ);
        }

        @Override
        public void onError(UiError uiError) {
            hideProgress();
            LogUtils.i("QQ登录出错");
        }

        @Override
        public void onCancel() {
            hideProgress();
            LogUtils.i("QQ登录取消");
        }
    };

    @Override
    public void onGetDataResult(RelevanceAccountEntity result) {
        if (result.code == Constant.HttpResult.SUCCEED) {
            if (result.relation != null && result.relation.size() > 0) {
                for (RelevanceAccountEntity.Relevance relevance : result.relation) {
                    if (relevance.type == 1) {
                        mIsFromUserQQ = false;
                        mRelationId = relevance.id;
                        mSwitchQq.setChecked(true);
                        mSwitchQq.setSelected(true);
                    } else if (relevance.type == 2) {
                        mIsFromUserWeChat = false;
                        mRelationId = relevance.id;
                        mSwitchWechat.setChecked(true);
                        mSwitchWechat.setSelected(true);
                    }
                }
            }
        } else if (result.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(RelevanceAccountActivity.this, "登录超时,请重新登录");
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            ToastUtil.showToast(RelevanceAccountActivity.this, TextUtils.isEmpty(result.msg) ? "" : result.msg);
        }
    }


    @Override
    public void onAccountBindResult(HttpResult result, int type) {
        onAccountBind(result.code, result.msg, type);
    }

    private void onAccountBind(int state, String msg, int type) {
        if (state == Constant.HttpResult.SUCCEED) {
            ToastUtil.showToast(RelevanceAccountActivity.this, "绑定成功");
            if (type == LOGIN_TYPE_QQ) {
                mIsFromUserQQ = false;
                mSwitchQq.setChecked(true);
                mSwitchQq.setSelected(true);
            } else if (type == LOGIN_TYPE_WECHAT) {
                mIsFromUserWeChat = false;
                mSwitchWechat.setChecked(true);
                mSwitchWechat.setSelected(true);
            }
            mPresenter.getData();//刷新数据
        } else if (state == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(RelevanceAccountActivity.this, "登录超时,请重新登录");
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            ToastUtil.showToast(RelevanceAccountActivity.this, TextUtils.isEmpty(msg) ? "" : msg);
        }
    }

    @Override
    public void onAccountUnBindResult(HttpResult result, int type) {
        if (result.code == Constant.HttpResult.SUCCEED) {
            ToastUtil.showToast(RelevanceAccountActivity.this, "解除绑定成功");
            if (type == LOGIN_TYPE_QQ) {
                mIsFromUserQQ = false;
                mSwitchQq.setChecked(false);
                mSwitchQq.setSelected(false);
            } else if (type == LOGIN_TYPE_WECHAT) {
                mIsFromUserWeChat = false;
                mSwitchWechat.setChecked(false);
                mSwitchWechat.setSelected(false);
            }
            mPresenter.getData();//刷新数据
        } else if (result.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(RelevanceAccountActivity.this, "登录超时,请重新登录");
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            ToastUtil.showToast(RelevanceAccountActivity.this, TextUtils.isEmpty(result.msg) ? "" : result.msg);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Tencent.onActivityResultData(requestCode, resultCode, data, mIUiListener);
        if (requestCode == Constants.REQUEST_API) {
            if (resultCode == Constants.REQUEST_QQ_SHARE || resultCode == Constants.REQUEST_QZONE_SHARE || resultCode == Constants.REQUEST_OLD_SHARE) {
                Tencent.handleResultData(data, mIUiListener);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null)
            mDisposable.dispose();
    }
}
