package com.zhengdao.zqb.view.activity.xiangwan;


import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class XiangWanActivity extends AppCompatActivity {

    private static final int      REQUEST_PHONE_STATE      = 100;
    private static final int      REQUEST_EXTERNAL_STORAGE = 1;
    private static       String[] PERMISSIONS_STORAGE      = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};
    @BindView(R.id.webview)
    WebView            mWebview;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout mSwipeContainer;
    @BindView(R.id.iv_title_bar_back)
    ImageView          mIvTitleBarBack;
    private String  mUrl;
    private boolean mIsInit;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xiangwan);
        ButterKnife.bind(this);
        initWebView();
        mUrl = getIntent().getStringExtra(Constant.WebView.URL);
        openUrl();
    }

    @SuppressLint({"SetJavaScriptEnabled", "ResourceAsColor"})
    private void initWebView() {
        //声明WebSettings子类
        WebSettings webSettings = mWebview.getSettings();
        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);
        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        //其他细节操作
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); //关闭webview中缓存
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        mWebview.addJavascriptInterface(XiangWanActivity.this, "android");
        //下拉刷新控件
        mSwipeContainer.setColorScheme(R.color.holo_blue_bright,
                R.color.holo_green_light, R.color.holo_orange_light,
                R.color.holo_red_light);
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //重新刷新页面
                mWebview.loadUrl(mWebview.getUrl());
            }
        });
        mIvTitleBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWebview.canGoBack()) {
                    mWebview.goBack();
                } else {
                    XiangWanActivity.this.finish();
                }
            }
        });
    }

    private void openUrl() {
        WebChromeClient webchromeclient = new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    //隐藏进度条
                    mSwipeContainer.setRefreshing(false);
                } else {
                    if (!mSwipeContainer.isRefreshing())
                        mSwipeContainer.setRefreshing(true);
                }

                super.onProgressChanged(view, newProgress);
            }

            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                Toast.makeText(XiangWanActivity.this, message, Toast.LENGTH_LONG).show();
                result.confirm();
                return true;
            }
        };
        mWebview.setWebChromeClient(webchromeclient);
        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        //加载链接
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mIsInit)
            mWebview.loadUrl(mUrl);
        else
            mWebview.reload();
        mIsInit = true;
    }

    /**
     * 重写返回键
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebview.canGoBack()) {
            mWebview.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mWebview.canGoBack()) {
                    mWebview.goBack();
                } else {
                    finish();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 判断指定包名的app是否已经安装，并且把结果返回给H5
     *
     * @param packageName
     */
    @JavascriptInterface
    public void CheckInstall(String packageName) {
        boolean isInstalled = SystemUtil.isInstalled(XiangWanActivity.this, packageName);

        if (isInstalled) {
            mWebview.post(new Runnable() {
                @Override
                public void run() {
                    mWebview.loadUrl("javascript:CheckInstall_Return(1)");
                }
            });
        } else {
            mWebview.post(new Runnable() {
                @Override
                public void run() {
                    mWebview.loadUrl("javascript:CheckInstall_Return(0)");
                }
            });
        }
    }

    /**
     * 打开指定包名App
     *
     * @param packageName
     */
    @JavascriptInterface
    public void AwallOpen(String packageName) {
        Log.i("open:", packageName + "...");
        doStartApplicationWithPackageName(packageName);
    }

    /**
     * 打开app
     *
     * @param packagename
     */
    private void doStartApplicationWithPackageName(String packagename) {

        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            packageinfo = getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return;
        }

        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = getPackageManager()
                .queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = 参数packname
            String packageName = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            ComponentName cn = new ComponentName(packageName, className);

            intent.setComponent(cn);
            startActivity(intent);
        }
    }

    /**
     * Toast信息提示
     *
     * @param message
     */
    @JavascriptInterface
    public void popout(String message) {
        Log.i("popout:", message + "...");
        if (!TextUtils.isEmpty(message)) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 刷新
     */
    @JavascriptInterface
    public void refresh() {
        Log.i("refresh:", "...");
        if (null != mWebview) {
            mWebview.post(new Runnable() {
                @Override
                public void run() {
                    mWebview.reload();
                }
            });
        }
    }

    /**
     * 下载安装APP，有两种方法
     * 1.使用系统下载，贵公司需要集成到app中
     * 2.直接打开浏览器下载
     *
     * @param url
     */
    @JavascriptInterface
    public void AwallDownLoad(String url) {

        Log.i("open:", url + "...");

        int last = url.lastIndexOf("/") + 1;
        String apkName = url.substring(last);
        if (!apkName.contains(".apk")) {
            if (apkName.length() > 10) {
                apkName = apkName.substring(apkName.length() - 10);
            }
            apkName += ".apk";
        }

        checkDownloadStatus(url, apkName);

        //另一种下载方式
        /*Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        startActivity(intent);*/
    }

    /**
     * 检查下载状态
     *
     * @param url
     * @param apkName
     */
    private void checkDownloadStatus(String url, String apkName) {
        boolean isLoading = false;
        DownloadManager.Query query = new DownloadManager.Query();
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Cursor c = downloadManager.query(query);
        if (c.moveToFirst()) {
            String LoadingUrl = c.getString(c.getColumnIndex(DownloadManager.COLUMN_URI));
            if (url.equals(LoadingUrl)) {
                isLoading = true;
                int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                switch (status) {
                    case DownloadManager.STATUS_PAUSED:
                        Log.i("DownLoadService", ">>>下载暂停");
                    case DownloadManager.STATUS_PENDING:
                        Log.i("DownLoadService", ">>>下载延迟");
                    case DownloadManager.STATUS_RUNNING:
                        Toast.makeText(XiangWanActivity.this, "正在下载", Toast.LENGTH_SHORT).show();
                        Log.i("DownLoadService", ">>>正在下载");
                        break;
                    case DownloadManager.STATUS_SUCCESSFUL:
                        Log.i("DownLoadService", ">>>下载完成");
                        //下载完成安装APK
                        String downloadPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "pceggs" + File.separator + apkName;
                        installAPK(new File(downloadPath), apkName);
                        break;
                    case DownloadManager.STATUS_FAILED:
                        Log.i("DownLoadService", ">>>下载失败");
                        break;
                }
            }
        }

        if (!isLoading) {
            DownLoadService.startActionFoo(XiangWanActivity.this, url);
        }
    }

    /**
     * 下载到本地后执行安装
     */
    protected void installAPK(File file, String apkName) {
        if (!file.exists())
            return;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri;

        // 判断版本大于等于7.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //坑 http://www.jianshu.com/p/c58d17073e65
            File newPath = new File(getFilesDir().getPath() + "/downloads");
            if (!newPath.exists()) {
                //通过file的mkdirs()方法创建 目录中包含却不存在的文件夹
                newPath.mkdirs();
            }
            String path = newPath.getPath() + "/" + apkName;
            String oldPath = file.getPath();
            copyFile(oldPath, path);
            File newfile = new File(path);
            // 即是在清单文件中配置的authorities
            uri = FileProvider.getUriForFile(XiangWanActivity.this, "com.zhengdao.zqb.fileprovider", newfile);
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);

        } else {
            uri = Uri.parse("file://" + file.toString());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        //在服务中开启activity必须设置flag,后面解释
        startActivity(intent);
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        }
    }

    /**
     * 打开浏览器试玩
     *
     * @param url
     */
    @JavascriptInterface
    public void openBrowser(String url) {
        Log.i("open:", url + "...");
        //另一种下载方式
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
