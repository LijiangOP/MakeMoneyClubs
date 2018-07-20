package com.zhengdao.zqb.view.activity.welfareget;


import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.customview.LotteryView;
import com.zhengdao.zqb.customview.LotteryWindow;
import com.zhengdao.zqb.customview.WeChatQRCodeDialog;
import com.zhengdao.zqb.entity.Prize;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.ToastUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class WelfareGetActivity extends MVPBaseActivity<WelfareGetContract.View, WelfareGetPresenter> implements WelfareGetContract.View {

    @BindView(R.id.lottery_view)
    LotteryView mLotteryView;
    public  LotteryWindow      mActivityDialog;
    private WeChatQRCodeDialog mWeChatQRCodeDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welfare);
        ButterKnife.bind(this);
        setTitle("福利领取");
        init();
    }

    private void init() {
        try {
            final int welfareState = getIntent().getIntExtra(Constant.Activity.Data, 0);
            int[] prizesIcon = {R.drawable.lottery_1, R.drawable.lottery_2, R.drawable.lottery_3, R.drawable.lottery_8,
                    R.drawable.lottery_, R.drawable.lottery_4, R.drawable.lottery_7, R.drawable.lottery_6, R.drawable.lottery_5};
            final List<Prize> prizes = new ArrayList<>();
            for (int x = 0; x < 9; x++) {
                Prize lottery = new Prize();
                lottery.setId(x + 1);
                lottery.setName("Lottery" + (x + 1));
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), prizesIcon[x]);
                lottery.setIcon(bitmap);
                prizes.add(lottery);
            }
            mLotteryView.setWinNum(0);
            mLotteryView.setPrizes(prizes);
            mLotteryView.setOnTransferWinningListener(new LotteryView.OnTransferWinningListener() {
                @Override
                public void onWinning(int position) {
                    if (mActivityDialog == null)
                        mActivityDialog = new LotteryWindow(WelfareGetActivity.this);
                    mActivityDialog.setPosition(0, -100);
                    mActivityDialog.initContentView(welfareState, new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            showWechatQRCodeWindow();
                            mActivityDialog.dismiss();
                        }
                    });
                    mActivityDialog.show();
                }
            });
        } catch (Exception ex) {
            LogUtils.e(ex.getMessage());
        }
    }

    public void showWechatQRCodeWindow() {
        if (mWeChatQRCodeDialog == null)
            mWeChatQRCodeDialog = new WeChatQRCodeDialog(this);
        mWeChatQRCodeDialog.initContent("扫码关注公众号");
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
            ToastUtil.showToast(WelfareGetActivity.this, "未发现内部存储,无法保存");
        } else {
            try {
                File mFileDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "zqb" + File.separator + "download");
                mFileDir.mkdirs();
                if (mFileDir != null && mFileDir.exists()) {
                    File file = new File(mFileDir, fileName);
                    FileOutputStream out = new FileOutputStream(file);
                    Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.wechat_qr_code);
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                    ToastUtil.showToast(WelfareGetActivity.this, "保存成功");
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri uri = Uri.fromFile(file);
                    intent.setData(uri);
                    sendBroadcast(intent);
                } else
                    ToastUtil.showToast(WelfareGetActivity.this, "路径不存在");
            } catch (Exception ex) {
                LogUtils.e(ex.getMessage());
                ToastUtil.showToast(WelfareGetActivity.this, "保存失败");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mActivityDialog != null) {
            mActivityDialog.dismiss();
            mActivityDialog = null;
        }
        if (mWeChatQRCodeDialog != null) {
            mWeChatQRCodeDialog.dismiss();
            mWeChatQRCodeDialog = null;
        }
    }
}
