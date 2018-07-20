package com.zhengdao.zqb.view.activity.webview;


import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.kennyc.view.MultiStateView;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.customview.WeChatQRCodeDialog;
import com.zhengdao.zqb.entity.ShareHttpEntity;
import com.zhengdao.zqb.event.ShowWechatShare;
import com.zhengdao.zqb.event.ToShare;
import com.zhengdao.zqb.h5.JsInterface;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.ShareUtils;
import com.zhengdao.zqb.utils.ToastUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class WebViewActivity extends MVPBaseActivity<WebViewContract.View, WebViewPresenter> implements WebViewContract.View {
    @BindView(R.id.progressbar)
    ProgressBar    mProgressbar;
    @BindView(R.id.webview)
    WebView        mWebview;
    @BindView(R.id.multiStateView)
    MultiStateView mMultiStateView;
    @BindView(R.id.re_title_bar)
    RelativeLayout mReTitleBar;

    private String mTitle;
    private String mHtml;
    private String mUrl;
    private File   mFileDir;

    private Disposable mDisposable1;
    private Disposable mDisposable2;
    private CompositeDisposable mDisposables = new CompositeDisposable();
    private WeChatQRCodeDialog mWeChatQRCodeDialog;
    private int                mCurrentType;
    private Bundle             params;
    private String             mNickName;
    private String             mStringShareUrl;
    private String             mStringShareIcon;
    private String             mStringSharePhone;
    private IUiListener        mUiListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initData() {
        mTitle = getIntent().getStringExtra(Constant.WebView.TITLE);
        mHtml = getIntent().getStringExtra(Constant.WebView.HTML);
        if (!TextUtils.isEmpty(mHtml))
            LogUtils.i("mHtml=" + mHtml);
        mUrl = getIntent().getStringExtra(Constant.WebView.URL);
        if (!TextUtils.isEmpty(mUrl))
            LogUtils.i("mUrl=" + mUrl);
        mUiListener = new IUiListener() {
            @Override
            public void onComplete(Object o) {
                ToastUtil.showToast(WebViewActivity.this, "分享成功");
            }

            @Override
            public void onError(UiError uiError) {
                ToastUtil.showToast(WebViewActivity.this, "分享失败");
                LogUtils.e("分享失败errorMessage:" + uiError.errorMessage + "errorDetail" + uiError.errorDetail);
            }

            @Override
            public void onCancel() {
                ToastUtil.showToast(WebViewActivity.this, "分享取消");
            }
        };
    }

    private void initView() {
        if (TextUtils.isEmpty(mTitle))
            mReTitleBar.setVisibility(View.GONE);
        else
            setTitle(mTitle);
        mDisposable1 = RxBus.getDefault().toObservable(ShowWechatShare.class).subscribe(new Consumer<ShowWechatShare>() {
            @Override
            public void accept(ShowWechatShare showWechatShare) throws Exception {
                showWechatQRCodeWindow();
            }
        });
        mDisposable2 = RxBus.getDefault().toObservable(ToShare.class).subscribe(new Consumer<ToShare>() {
            @Override
            public void accept(ToShare toShare) throws Exception {
                doShare(toShare.type);
            }
        });
        mDisposables.add(mDisposable1);
        mDisposables.add(mDisposable2);
        mMultiStateView.getView(MultiStateView.VIEW_STATE_ERROR).findViewById(R.id.btn_error_reload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshData();//刷新数据
            }
        });
        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                LogUtils.e("url:" + url);
                if (url.startsWith("tbopen:")) {
                    return false;
                } else {
                    view.loadUrl(url);
                    return true;
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mProgressbar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mProgressbar.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);

            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });
        mWebview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                mProgressbar.setProgress(newProgress);
            }
        });
        mWebview.addJavascriptInterface(new JsInterface(), "android");//注入本地方法
        WebSettings settings = mWebview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);//缩放到屏幕大小
        settings.setDisplayZoomControls(false);//是否显示放大缩小按钮
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setAppCacheEnabled(true);//是否使用缓存
        settings.setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
        settings.setSupportZoom(true);//是否可以缩放，默认true
        settings.setDomStorageEnabled(true);//DOM Storage  //记得开启 否则个别网页打不开
        refreshData();
    }

    private void doShare(int type) {
        mPresenter.getShareData();
        mCurrentType = type;
    }

    public void showWechatQRCodeWindow() {
        if (mWeChatQRCodeDialog == null)
            mWeChatQRCodeDialog = new WeChatQRCodeDialog(this);
        mWeChatQRCodeDialog.setSaveListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doSavePic("wechat_share.jpg");
                mWeChatQRCodeDialog.dismiss();
            }
        });
        mWeChatQRCodeDialog.show();
    }

    private void doSavePic(String fileName) {
        methodRequiresTwoPermission();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_UNMOUNTED)) {
            ToastUtil.showToast(WebViewActivity.this, "未发现内部存储,无法保存");
        } else {
            try {
                mFileDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "zqb" + File.separator + "download");
                mFileDir.mkdirs();
                if (mFileDir != null && mFileDir.exists()) {
                    File file = new File(mFileDir, fileName);
                    FileOutputStream out = new FileOutputStream(file);
                    Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.wechat_qrcode);
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                    ToastUtil.showToast(WebViewActivity.this, "保存成功");
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri uri = Uri.fromFile(file);
                    intent.setData(uri);
                    sendBroadcast(intent);
                } else
                    ToastUtil.showToast(WebViewActivity.this, "路径不存在");
            } catch (Exception ex) {
                LogUtils.e(ex.getMessage());
                ToastUtil.showToast(WebViewActivity.this, "保存失败");
            }
        }
    }

    private static final int RC_CAMERA_AND_LOCATION = 001;

    @AfterPermissionGranted(RC_CAMERA_AND_LOCATION)
    private void methodRequiresTwoPermission() {
        String[] perms = {WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, "下列权限未获权，是否开启", RC_CAMERA_AND_LOCATION, perms);
        }
    }

    private void refreshData() {
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
        if (!TextUtils.isEmpty(mHtml))
            mWebview.loadData(mHtml, "text/html; charset=UTF-8", null);
        if (mUrl != null)
            mWebview.loadUrl(mUrl);
    }

    @Override
    public void showSahreData(ShareHttpEntity httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            mStringShareIcon = TextUtils.isEmpty(httpResult.icon) ? "" : httpResult.icon;
            mNickName = TextUtils.isEmpty(httpResult.nickName) ? "" : httpResult.nickName;
            mStringShareUrl = TextUtils.isEmpty(httpResult.uploadUrl) ? "" : httpResult.uploadUrl;
            if (!TextUtils.isEmpty(httpResult.phone) && httpResult.phone.length() > 10)
                mStringSharePhone = httpResult.phone.substring(0, 3) + "****" + httpResult.phone.substring(7, 11);
            switch (mCurrentType) {
                case 1:
                    shareToQQ();
                    break;
                case 2:
                    shareToQzone();
                    break;
                case 3:
                    sharaToWechat();
                    break;
                case 4:
                    sharaToFriendCircle();
                    break;
            }
        }
    }

    private void shareToQQ() {
        params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, mNickName + "喊你一起赚钱吧!");// 标题
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, mStringShareIcon);// 网络图片地址　　
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, getString(R.string.share_content));// 摘要
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, TextUtils.isEmpty(mStringShareUrl) ? "" : mStringShareUrl);// 内容地址
        // 分享操作要在主线程中完成
        ShareUtils.shareToQQ(WebViewActivity.this, params, mUiListener);
    }

    private void shareToQzone() {
        params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, mNickName + "喊你一起赚钱吧!");// 标题
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, getString(R.string.share_content));// 摘要
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, TextUtils.isEmpty(mStringShareUrl) ? "" : mStringShareUrl);// 内容地址
        ArrayList<String> imgUrlList = new ArrayList<>();
        imgUrlList.add(mStringShareIcon);
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imgUrlList);// 图片地址
        // 分享操作要在主线程中完成
        ShareUtils.shareToQZone(WebViewActivity.this, params, mUiListener);
    }

    private void sharaToWechat() {
        ShareUtils.shareToWX(getContext(), TextUtils.isEmpty(mStringShareUrl) ? "" : mStringShareUrl, mNickName + "喊你一起赚钱吧!", getString(R.string.share_content), R.drawable.icon, SendMessageToWX.Req.WXSceneSession);
    }

    private void sharaToFriendCircle() {
        ShareUtils.shareToWX(getContext(), TextUtils.isEmpty(mStringShareUrl) ? "" : mStringShareUrl, mNickName + "喊你一起赚钱吧!", getString(R.string.share_content), R.drawable.icon, SendMessageToWX.Req.WXSceneTimeline);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebview.canGoBack()) {
            mWebview.goBack();
            return true;
        } else {
            onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null)
            mPresenter.unsubcrible();
    }

}
