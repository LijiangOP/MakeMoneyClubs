package com.zhengdao.zqb.view.activity.rebateservice;


import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tbruyelle.rxpermissions.RxPermissions;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.ToastUtil;

import java.io.File;
import java.io.FileOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action1;

public class RebateServiceActivity extends MVPBaseActivity<RebateServiceContract.View, RebateServicePresenter> implements RebateServiceContract.View {

    @BindView(R.id.tv_title_bar_right)
    TextView  mTvTitleBarRight;
    @BindView(R.id.iv_title_bar_back)
    ImageView mIvTitleBarBack;
    @BindView(R.id.tv_title_bar_title)
    TextView  mTvTitleBarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rebate_service);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        mIvTitleBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RebateServiceActivity.this.finish();
            }
        });
        mTvTitleBarTitle.setText("联系客服");
        mTvTitleBarRight.setText("保存");
        mTvTitleBarRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RxPermissions rxPermissions = new RxPermissions(RebateServiceActivity.this);
                rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE).subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        doSavePic("rebate_service_share.jpg");
                    }
                });
            }
        });
    }

    private void doSavePic(String fileName) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_UNMOUNTED)) {
            ToastUtil.showToast(RebateServiceActivity.this, "未发现内部存储,无法保存");
        } else {
            try {
                File mFileDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "zqb" + File.separator + "download");
                mFileDir.mkdirs();
                if (mFileDir != null && mFileDir.exists()) {
                    File file = new File(mFileDir, fileName);
                    FileOutputStream out = new FileOutputStream(file);
                    Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.rebate_service);
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                    ToastUtil.showToast(RebateServiceActivity.this, "保存成功，正在打开微信");
                    openWechat();
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri uri = Uri.fromFile(file);
                    intent.setData(uri);
                    sendBroadcast(intent);
                } else
                    ToastUtil.showToast(RebateServiceActivity.this, "路径不存在");
            } catch (Exception ex) {
                LogUtils.e(ex.getMessage());
                ToastUtil.showToast(RebateServiceActivity.this, "保存失败");
            }
        }
    }

    private Handler mHandler = new Handler();

    private void openWechat() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setComponent(cmp);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtil.showToast(RebateServiceActivity.this, "请检查是否安装微信");
                }
            }
        }, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
