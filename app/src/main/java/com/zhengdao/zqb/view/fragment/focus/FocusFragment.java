package com.zhengdao.zqb.view.fragment.focus;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.text.TextUtils;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.kennyc.view.MultiStateView;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.DictionaryHttpEntity;
import com.zhengdao.zqb.h5.JsInterface;
import com.zhengdao.zqb.mvp.MVPBaseFragment;
import com.zhengdao.zqb.utils.LogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FocusFragment extends MVPBaseFragment<FocusContract.View, FocusPresenter> implements FocusContract.View {

    @BindView(R.id.fake_status_bar)
    View           mFakeStatusBar;
    @BindView(R.id.progressbar)
    ProgressBar    mProgressbar;
    @BindView(R.id.webview)
    WebView        mWebview;
    @BindView(R.id.multiStateView)
    MultiStateView mMultiStateView;

    String mUrl = Constant.BaiDuAdv.ContentAlliance;

    @Override
    protected View getFragmentView() {
        View view = View.inflate(getActivity(), R.layout.focus_fragment, null);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        mPresenter.getSwitchState();
        mFakeStatusBar.setBackgroundColor(calculateStatusColor(getResources().getColor(R.color.main), 0));
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
                mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
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
        mWebview.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
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
    }

    private void refreshData() {
        if (!TextUtils.isEmpty(mUrl)) {
            mWebview.loadUrl(mUrl);
            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
        }
    }

    @Override
    public void onSwitchStateGet(DictionaryHttpEntity result) {
        try {
            if (result.code == Constant.HttpResult.SUCCEED) {
                if (result.types != null && result.types.size() > 0) {
                    String value = result.types.get(0).value;
                    if (!TextUtils.isEmpty(value)) {
                        if (new Integer(value) == 1) {//0开启;1关闭 默认关闭
                            mUrl = Constant.BaiDuAdv.ContentAllianceReplace;
                        } else {
                            mUrl = Constant.BaiDuAdv.ContentAlliance;
                        }
                    }
                }
            }
            refreshData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null)
            mPresenter.unsubcrible();
    }

    public boolean isCanGoBack() {
        if (mWebview != null && mWebview.canGoBack())
            return true;
        else
            return false;
    }

    public void goBack() {
        if (mWebview != null)
            mWebview.goBack();
    }

}
