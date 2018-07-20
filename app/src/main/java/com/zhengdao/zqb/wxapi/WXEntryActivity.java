package com.zhengdao.zqb.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.customview.CustomProgressDialog;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.UserInfo;
import com.zhengdao.zqb.entity.WechatBaseEntity;
import com.zhengdao.zqb.entity.WechatCheckEntity;
import com.zhengdao.zqb.entity.WechatUserEntity;
import com.zhengdao.zqb.event.DailyWechatShareEvent;
import com.zhengdao.zqb.event.ForceBindPhoneEvent;
import com.zhengdao.zqb.event.LogInEvent;
import com.zhengdao.zqb.event.WechatBindEvent;
import com.zhengdao.zqb.event.WechatLoginSuccessEvent;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.utils.WechatUtils;

import static com.zhengdao.zqb.application.ClientAppLike.WECHAT_APPID;
import static com.zhengdao.zqb.config.Constant.Third.LOGIN_TYPE_WECHAT;
import static com.zhengdao.zqb.application.ClientAppLike.WECHAT_SECRET_ID;

/**
 * @Author lijiangop
 * @CreateTime 2017/6/20 10:35
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler, WXEntryContract.View {
    private static final int RETURN_MSG_TYPE_LOGIN = 1;
    private static final int RETURN_MSG_TYPE_SHARE = 2;
    private IWXAPI               api;
    private WXEntryPresenter     mPresenter;
    private CustomProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //如果没回调onResp，八成是这句没有写
        api = WXAPIFactory.createWXAPI(this, WECHAT_APPID);
        api.registerApp(WECHAT_APPID);
        api.handleIntent(getIntent(), this);
        if (mProgressDialog == null) {
            mProgressDialog = new CustomProgressDialog(this);
            mProgressDialog.setMessage("正在加载");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    // 微信发送请求到第三方应用时，会回调到该方法
    @Override
    public void onReq(BaseReq req) {
        LogUtils.i("onReq");
    }

    //获取微信访问
    private void getCode() {
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "carjob_wx_login";
        api.sendReq(req);
    }

    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    // app发送消息给微信，处理返回消息的回调
    @Override
    public void onResp(BaseResp resp) {
        LogUtils.e(resp.errStr);
        LogUtils.e("错误码 : " + resp.errCode + "");
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                if (RETURN_MSG_TYPE_SHARE == resp.getType())
                    ToastUtil.showToast(this, "分享失败");
                else
                    ToastUtil.showToast(this, "授权取消");
                finish();
                break;
            case BaseResp.ErrCode.ERR_OK:
                switch (resp.getType()) {
                    case RETURN_MSG_TYPE_LOGIN://微信登录
                        //拿到了微信返回的code,立马再去请求access_token
                        String stateSuccess = TextUtils.isEmpty(((SendAuth.Resp) resp).state) ? "" : ((SendAuth.Resp) resp).state;
                        LogUtils.i("state = " + stateSuccess);
                        String code = ((SendAuth.Resp) resp).code;
                        LogUtils.i("code = " + code);
                        if (mPresenter == null)
                            mPresenter = new WXEntryPresenter(this, this);
                        mPresenter.getWechatBaseData(WECHAT_APPID, WECHAT_SECRET_ID, code, "authorization_code", stateSuccess);
                        break;
                    case RETURN_MSG_TYPE_SHARE:
                        ToastUtil.showToast(this, "微信分享成功");
                        String transaction = TextUtils.isEmpty(resp.transaction) ? "" : resp.transaction;
                        if (!TextUtils.isEmpty(transaction) && transaction.equals(Constant.WechatReq.DailyShare)) {
                            RxBus.getDefault().post(new DailyWechatShareEvent());
                        }
                        finish();
                        break;
                }
                break;
        }
    }

    @Override
    public void onGetWechatBaseData(WechatBaseEntity wechatBaseEntity, String state) {
        if (wechatBaseEntity == null) {
            ToastUtil.showToast(WXEntryActivity.this, "获取数据失败");
            WXEntryActivity.this.finish();
        } else {
            if (!TextUtils.isEmpty(state) && state.equals(Constant.WechatReq.Loginstate)) {
                String refresh_token = TextUtils.isEmpty(wechatBaseEntity.refresh_token) ? "" : wechatBaseEntity.refresh_token;
                String access_token = TextUtils.isEmpty(wechatBaseEntity.access_token) ? "" : wechatBaseEntity.access_token;
                String openid = TextUtils.isEmpty(wechatBaseEntity.openid) ? "" : wechatBaseEntity.openid;
                WechatUtils.setWechatRefreshToken(WXEntryActivity.this, refresh_token);
                WechatUtils.setWechatAccessToken(WXEntryActivity.this, access_token);
                WechatUtils.setWechatOpenId(WXEntryActivity.this, openid);
                mPresenter.doThirdLogin(openid);
            } else if (!TextUtils.isEmpty(state) && state.equals(Constant.WechatReq.Bindstate)) {
                String openid = TextUtils.isEmpty(wechatBaseEntity.openid) ? "" : wechatBaseEntity.openid;
                mPresenter.doAccountBind(openid, LOGIN_TYPE_WECHAT);
            }
        }
    }

    @Override
    public void onWechatcheckToken(WechatCheckEntity wechatCheckEntity) {
        if (wechatCheckEntity.errcode == 0) {

        } else
            LogUtils.i("token不可用");
    }

    @Override
    public void onWechatRefreshToken(WechatBaseEntity WechatBaseEntity) {
        if (WechatBaseEntity == null) {
            LogUtils.e("刷新用户token失败");
        } else {
            String access_token = WechatBaseEntity.access_token;
            String openid = WechatBaseEntity.openid;
        }
    }

    @Override
    public void onGetWechatUserData(WechatUserEntity wechatUserEntity) {
        if (wechatUserEntity == null) {
            ToastUtil.showToast(WXEntryActivity.this, "获取用户数据失败");
            WXEntryActivity.this.finish();
        } else {
            LogUtils.i(wechatUserEntity.toString());
            WXEntryActivity.this.finish();
        }
    }

    @Override
    public void onThirdLoginOver(HttpResult<UserInfo> httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            mPresenter.saveUserInfo(httpResult.data);
        } else if (httpResult.code == Constant.HttpResult.DATANULL) {
            RxBus.getDefault().post(new ForceBindPhoneEvent(1));
            RxBus.getDefault().post(new WechatLoginSuccessEvent());
            finish();
        } else
            ToastUtil.showToast(WXEntryActivity.this, "登录失败");
    }

    @Override
    public void loginSuccess(UserInfo bean) {
        LogUtils.i("登录成功:" + "\n" + bean.toString());
        ToastUtil.showToast(this, "登录成功！");
        String phoneNum = SettingUtils.getPhoneNum(this);
        if (TextUtils.isEmpty(phoneNum))
            RxBus.getDefault().post(new ForceBindPhoneEvent(1));
        else
            RxBus.getDefault().post(new LogInEvent());
        RxBus.getDefault().post(new WechatLoginSuccessEvent());
        finish();
    }

    @Override
    public void onAccountBindResult(HttpResult result, int type) {
        WechatBindEvent wechatBindEvent = new WechatBindEvent();
        wechatBindEvent.type = type;
        wechatBindEvent.msg = result.msg;
        if (result.code == Constant.HttpResult.SUCCEED) {
            wechatBindEvent.state = Constant.HttpResult.SUCCEED;
        } else if (result.code == Constant.HttpResult.RELOGIN) {
            wechatBindEvent.state = Constant.HttpResult.RELOGIN;
        } else {
            wechatBindEvent.state = Constant.HttpResult.ELSE;
        }
        RxBus.getDefault().post(wechatBindEvent);
    }

    @Override
    public void showProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.show();
        }
    }

    @Override
    public void hideProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void showErrorMessage(String message) {
        if (!TextUtils.isEmpty(message)) {
            LogUtils.e("ErrorMsg:" + message);
            ToastUtil.showToast(this, "请求出错，请稍后再试");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
        if (mPresenter != null)
            mPresenter.unsubcrible();
    }
}
