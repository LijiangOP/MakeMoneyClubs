package com.zhengdao.zqb.view.activity.splash;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobads.SplashAd;
import com.baidu.mobads.SplashAdListener;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.qq.e.comm.util.AdError;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.application.ClientAppLike;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.view.activity.main.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.functions.Action1;


public class SplashActivity extends MVPBaseActivity<SplashContract.View, SplashPresenter> implements SplashContract.View {


    @BindView(R.id.re_container)
    RelativeLayout mReContainer;
    @BindView(R.id.ll_adv_part)
    LinearLayout   mLlAdvPart;
    @BindView(R.id.skip_view)
    TextView       mSkipView;
    @BindView(R.id.fl_container)
    FrameLayout    mFlContainer;
    private              boolean canJump   = false;
    private static final String  SKIP_TEXT = "点击跳过 %d";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        if (ClientAppLike.AppType == Constant.App.Wlgfl) {
            initTencentAdv();
        } else {
            initBaiDuAdv();
        }
    }

    /**
     * 百度广告
     */
    private void initBaiDuAdv() {
        mLlAdvPart.setVisibility(View.VISIBLE);
        new SplashAd(this, mReContainer, new SplashAdListener() {
            @Override
            public void onAdPresent() {
                mLlAdvPart.setVisibility(View.VISIBLE);
                LogUtils.e("onAdPresent");
            }

            @Override
            public void onAdDismissed() {
                LogUtils.e("onAdDismissed");
                startMain();
            }

            @Override
            public void onAdFailed(String s) {
                LogUtils.e("onAdFailed");
                initLocal();
                mLlAdvPart.setVisibility(View.GONE);
            }

            @Override
            public void onAdClick() {
                LogUtils.e("onAdClick");
            }
        }, Constant.BaiDuAdv.Splash, true);
    }

    private void startMain() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void initLocal() {
        Observable.timer(3, TimeUnit.SECONDS).subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                startMain();
            }
        });
    }

    /**
     * 腾讯广告
     */
    private void initTencentAdv() {
        if (Build.VERSION.SDK_INT >= 23) {
            checkAndRequestPermission();
        } else {
            // 如果是Android6.0以下的机器，默认在安装时获得了所有权限，可以直接调用SDK
            fetchSplashAD(this, mFlContainer, mSkipView, Constant.TencentAdv.advTenCent_APPID, Constant.TencentAdv.advTenCent_ADV_SPLASH_ID, listener, 5000);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkAndRequestPermission() {
        List<String> lackedPermission = new ArrayList<String>();
        if (!(checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (!(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        // 权限都已经有了，那么直接调用SDK
        if (lackedPermission.size() == 0) {
            fetchSplashAD(this, mFlContainer, mSkipView, Constant.TencentAdv.advTenCent_APPID, Constant.TencentAdv.advTenCent_ADV_SPLASH_ID, listener, 5000);
        } else {
            // 请求所缺少的权限，在onRequestPermissionsResult中再看是否获得权限，如果获得权限就可以调用SDK，否则不要调用SDK。
            String[] requestPermissions = new String[lackedPermission.size()];
            lackedPermission.toArray(requestPermissions);
            requestPermissions(requestPermissions, 1024);
        }
    }

    private boolean hasAllPermissionsGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 请求权限
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1024 && hasAllPermissionsGranted(grantResults)) {
            fetchSplashAD(this, mFlContainer, mSkipView, Constant.TencentAdv.advTenCent_APPID, Constant.TencentAdv.advTenCent_ADV_SPLASH_ID, listener, 5000);
        } else {
            // 如果用户没有授权，那么应该说明意图，引导用户去设置里面授权。
            Toast.makeText(this, "应用缺少必要的权限！请点击\"权限\"，打开所需要的权限。", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            finish();
        }
    }

    private void fetchSplashAD(Activity activity, ViewGroup adContainer, View skipContainer, String appId, String posId, SplashADListener adListener, int fetchDelay) {
        SplashAD splashAD = new SplashAD(activity, adContainer, skipContainer, appId, posId, adListener, fetchDelay);
    }

    private SplashADListener listener = new SplashADListener() {
        @Override
        public void onADDismissed() {
            Log.i("AD_DEMO", "SplashADDismissed");
            next();
        }

        @Override
        public void onNoAD(AdError adError) {
            Log.i("AD_DEMO", String.format("LoadSplashADFail, eCode=%d, errorMsg=%s", adError.getErrorCode(), adError.getErrorMsg()));
            /** 如果加载广告失败，则直接跳转 */
            mLlAdvPart.setVisibility(View.GONE);
            startMain();
        }

        @Override
        public void onADPresent() {
            Log.i("AD_DEMO", "SplashADPresent");
            mSkipView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onADClicked() {
            Log.i("AD_DEMO", "SplashADClicked");
            startMain();
        }

        @Override
        public void onADTick(long l) {
            Log.i("AD_DEMO", "SplashADTick " + l + "ms");
            mSkipView.setText(String.format(SKIP_TEXT, Math.round(l / 1000f)));
        }
    };

    private void next() {
        if (canJump) {
            this.startActivity(new Intent(this, MainActivity.class));
            this.finish();
        } else {
            canJump = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        canJump = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (canJump) {
            next();
        }
        canJump = true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
