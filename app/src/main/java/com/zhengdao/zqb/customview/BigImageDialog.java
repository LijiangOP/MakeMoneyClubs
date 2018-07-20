package com.zhengdao.zqb.customview;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.github.chrisbanes.photoview.PhotoView;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.utils.FileUtils;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.ToastUtil;

import java.io.File;
import java.io.FileOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author lijiangop
 * @CreateTime 2018/2/28 16:42
 */
public class BigImageDialog extends Dialog {


    @BindView(R.id.iv_download)
    ImageView mIvDownload;
    private Context mContext;
    @BindView(R.id.imageView)
    PhotoView mImageView;
    private File   mFile;
    private String mPicPath;
    private Bitmap mBitmap;

    public BigImageDialog(Context context) {
        this(context, R.style.shape_dialog);
    }

    public BigImageDialog(Context context, int themeResId) {
        super(context, themeResId);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mContext = context;
        View rootView = View.inflate(context, R.layout.big_image, null);
        ButterKnife.bind(this, rootView);
        setContentView(rootView);
        //dialog宽度,填充整个屏幕
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = width;
        lp.height = height;
        dialogWindow.setAttributes(lp);
        //全屏覆盖状态栏
        dialogWindow.setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL);
        dialogWindow.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialogWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //设置背景黑色
        dialogWindow.setBackgroundDrawableResource(android.R.color.black);
        //图片宽度,填充整个屏幕
        ViewGroup.LayoutParams layoutParams = mImageView.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        mImageView.setLayoutParams(layoutParams);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mIvDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downLoadPic();
            }
        });
    }

    private void downLoadPic() {
        if (!TextUtils.isEmpty(mPicPath)) {
            String filename = mPicPath.hashCode() + ".jpg";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_UNMOUNTED)) {
                ToastUtil.showToast(mContext, "未发现内部存储,无法保存");
            } else {
                try {
                    mFile = new File(FileUtils.getDownloadPath(filename));
                    if (mFile == null || !mFile.exists())
                        mFile.createNewFile();
                    if (mFile != null && mFile.exists() && mBitmap != null) {
                        FileOutputStream out = new FileOutputStream(mFile);
                        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.flush();
                        out.close();
                        ToastUtil.showToast(mContext, "保存成功");
                        LogUtils.i("mFile=" + mFile.getAbsolutePath());
                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        Uri uri = Uri.fromFile(mFile);
                        intent.setData(uri);
                        mContext.sendBroadcast(intent);
                    } else
                        ToastUtil.showToast(mContext, "路径不存在");
                } catch (Exception ex) {
                    LogUtils.e(ex.getMessage());
                    ToastUtil.showToast(mContext, "保存失败");
                }
            }
        }
    }

    public void display(String content) {
        if (!TextUtils.isEmpty(content)) {
            mPicPath = content;
            Glide.with(mContext).load(content).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    if (resource != null) {
                        mBitmap = resource;
                        mImageView.setImageBitmap(resource);
                        mIvDownload.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    public void display(String content, int type) {
        if (!TextUtils.isEmpty(content)) {
            try {
                mImageView.setImageBitmap(BitmapFactory.decodeFile(content));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void display(Bitmap bitmap) {
        if (bitmap != null)
            mImageView.setImageBitmap(bitmap);
    }
}
