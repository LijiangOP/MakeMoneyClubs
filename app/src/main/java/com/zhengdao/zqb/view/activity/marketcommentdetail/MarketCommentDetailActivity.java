package com.zhengdao.zqb.view.activity.marketcommentdetail;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.customview.BigImageDialog;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.MarketCommentHttpEntity;
import com.zhengdao.zqb.entity.ShowEntity;
import com.zhengdao.zqb.event.NewHandUpDataEvent;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.DensityUtil;
import com.zhengdao.zqb.utils.FileUtils;
import com.zhengdao.zqb.utils.ImageUtils;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.utils.ViewUtils;
import com.zhengdao.zqb.view.activity.login.LoginActivity;
import com.zhengdao.zqb.view.adapter.HomeDetailAddPicAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class MarketCommentDetailActivity extends MVPBaseActivity<MarketCommentDetailContract.View, MarketCommentDetailPresenter> implements MarketCommentDetailContract.View, View.OnClickListener, HomeDetailAddPicAdapter.CallBack {

    private static final int ACTION_CHOOSE = 007;
    private static final int REQUEST_CODE  = 101;
    @BindView(R.id.iv_example_left)
    ImageView    mIvExampleLeft;
    @BindView(R.id.iv_example_right)
    ImageView    mIvExampleRight;
    @BindView(R.id.recycle_view)
    RecyclerView mRecycleView;
    @BindView(R.id.tv_commit)
    TextView     mTvCommit;
    @BindView(R.id.tv_flow1)
    TextView     mTvFlow1;

    private long mCurrentTimeMillis = 0;
    private File                                  mFilePath1;
    private File                                  mFilePath2;
    private int                                   mCurrentPosition;
    private MarketCommentHttpEntity.MarketComment mMarketComment;
    private int                                   mState;
    private BigImageDialog                        mBigImageDialog;
    private ArrayList<ShowEntity>                 mBitmaps;
    private HomeDetailAddPicAdapter               mAddPicAdapter;
    private int[]                                 mScreenSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_comment_detail);
        ButterKnife.bind(this);
        setTitle("市场评论");
        init();
        initClickListener();
    }

    private void init() {
        mPresenter.getData();
        mBitmaps = new ArrayList<>();
        mBitmaps.add(new ShowEntity("上传评论中心图片"));
        mBitmaps.add(new ShowEntity("上传帐户中心图片"));
        if (mAddPicAdapter == null) {
            mAddPicAdapter = new HomeDetailAddPicAdapter(this, R.layout.home_detail_addpic_layout, mBitmaps, this);
            mRecycleView.addItemDecoration(new SpacesItemDecoration(5));
            mRecycleView.setAdapter(mAddPicAdapter);
            mRecycleView.setLayoutManager(new GridLayoutManager(this, 2));
        }
        reSetImgWidth();
    }

    private void reSetImgWidth() {
        try {
            mScreenSize = DensityUtil.getScreenSize(MarketCommentDetailActivity.this);
            int dimensionPixelOffset = getResources().getDimensionPixelOffset(R.dimen.market_comment_pic_left);
            int dimensionPixelOffset1 = getResources().getDimensionPixelOffset(R.dimen.market_comment_pic_middle);
            int width = (mScreenSize[0] - dimensionPixelOffset * 2 - dimensionPixelOffset1) / 2;
            int height = (width * mScreenSize[1]) / mScreenSize[0];
            ViewGroup.LayoutParams layoutParams = mIvExampleLeft.getLayoutParams();
            layoutParams.width = width;
            layoutParams.height = height;
            mIvExampleLeft.setLayoutParams(layoutParams);
            ViewGroup.LayoutParams layoutParams1 = mIvExampleRight.getLayoutParams();
            layoutParams1.width = width;
            layoutParams1.height = height;
            mIvExampleRight.setLayoutParams(layoutParams1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initClickListener() {
        mIvExampleLeft.setOnClickListener(this);
        mIvExampleRight.setOnClickListener(this);
        mTvFlow1.setOnClickListener(this);
        mTvCommit.setOnClickListener(this);
    }

    @Override
    public void showView(MarketCommentHttpEntity httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            if (httpResult.marketList == null || httpResult.marketList.size() == 0) {
                return;
            }
            if (httpResult.marketList != null && httpResult.marketList.size() > 0) {
                mMarketComment = httpResult.marketList.get(0);
                mState = mMarketComment.state;
                Glide.with(this).load(mMarketComment.img1).error(R.drawable.net_less_vertical).into(mIvExampleLeft);
                Glide.with(this).load(mMarketComment.img2).error(R.drawable.net_less_vertical).into(mIvExampleRight);
                if (mState == 0)
                    mTvCommit.setText("审核未通过,重新提交");
                if (mState == 2)
                    mTvCommit.setText("审核中");
            }
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录");
            startActivityForResult(new Intent(this, LoginActivity.class), REQUEST_CODE);
        } else {
            ToastUtil.showToast(MarketCommentDetailActivity.this, TextUtils.isEmpty(httpResult.msg) ? "获取数据失败" : httpResult.msg);
        }
    }

    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - mCurrentTimeMillis < 1000)
            return;
        mCurrentTimeMillis = System.currentTimeMillis();
        switch (v.getId()) {
            case R.id.tv_flow1:
                doSkipToMarket();
                break;
            case R.id.iv_example_left:
                if (mMarketComment != null)
                    doShowBigImage(mMarketComment.img1);
                break;
            case R.id.iv_example_right:
                if (mMarketComment != null)
                    doShowBigImage(mMarketComment.img2);
                break;
            case R.id.tv_commit:
                if (mState == 2)
                    ToastUtil.showToast(this, "审核中,请勿重复提交!");
                else
                    doCommit();
                break;
        }
    }

    private void doSkipToMarket() {
        try {
            Uri uri = Uri.parse("market://details?id=" + getPackageName());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "您没有安装应用市场", Toast.LENGTH_SHORT).show();
        }
    }

    private void doCommit() {
        if (mFilePath1 == null) {
            ToastUtil.showToast(this, "请上传个人主页截图");
            return;
        }
        if (mFilePath2 == null) {
            ToastUtil.showToast(this, "请上传应用评论截图");
            return;
        }
        Map<String, RequestBody> images = new HashMap<>();

        File compressFile1 = ImageUtils.compressImage(this, mFilePath1, System.currentTimeMillis() + ".jpg");
        RequestBody fileBody1 = RequestBody.create(MediaType.parse("multipart/form-data"), compressFile1);
        images.put("files\";filename=\"" + compressFile1.getName(), fileBody1);

        File compressFile2 = ImageUtils.compressImage(this, mFilePath2, System.currentTimeMillis() + ".jpg");
        RequestBody fileBody2 = RequestBody.create(MediaType.parse("multipart/form-data"), compressFile2);
        images.put("files\";filename=\"" + compressFile2.getName(), fileBody2);

        RequestBody type = RequestBody.create(MediaType.parse("multipart/form-data"), "" + Constant.Upload.Type_Comment);
        mPresenter.uploadImages(type, images);
    }

    private void doShowBigImage(String url) {
        try {
            if (!TextUtils.isEmpty(url)) {
                if (mBigImageDialog == null)
                    mBigImageDialog = new BigImageDialog(MarketCommentDetailActivity.this);
                mBigImageDialog.display(url);
                mBigImageDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onImgUploadError() {
        ToastUtil.showToast(this, "图片上传失败");
    }

    @Override
    public void onImgUploadResult(HttpResult<ArrayList<String>> result) {
        if (result.code == Constant.HttpResult.SUCCEED) {
            if (result.data != null && result.data.size() == 2)
                mPresenter.doCommit(mMarketComment.id, result.data.get(0), result.data.get(1));
        } else {
            ToastUtil.showToast(this, TextUtils.isEmpty(result.msg) ? "" : result.msg);
        }
    }

    @Override
    public void onCommitResult(HttpResult result) {
        if (result.code == Constant.HttpResult.SUCCEED) {
            MarketCommentDetailActivity.this.finish();
            ToastUtil.showToast(this, TextUtils.isEmpty(result.msg) ? "提交成功" : result.msg);
            RxBus.getDefault().post(new NewHandUpDataEvent());
        } else {
            ToastUtil.showToast(this, TextUtils.isEmpty(result.msg) ? "提交失败" : result.msg);
        }
    }

    @Override
    public void onPicAdd(int position) {
        mCurrentPosition = position;
        try {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(intent, ACTION_CHOOSE);
        } catch (Exception ex) {
            LogUtils.e(ex.getMessage());
        }
    }

    @Override
    public void onPicDelete(int position) {
        if (mBitmaps != null && mBitmaps.size() > position) {
            ShowEntity showEntity = mBitmaps.get(position);
            showEntity.setPic(null);
            mBitmaps.set(position, showEntity);
        }
        if (position == 0) {
            mFilePath1 = null;
        } else if (position == 1) {
            mFilePath2 = null;
        }
        if (mAddPicAdapter != null)
            mAddPicAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ACTION_CHOOSE:
                if (resultCode == RESULT_OK && data != null) {
                    try {
                        String path = FileUtils.getImageAbsolutePath(this, data.getData());
                        if (new File(path).exists() && !ImageUtils.isGifFile(new File(path))) {
                            Bitmap bitmap = BitmapFactory.decodeFile(path);
                            if (mBitmaps != null && mBitmaps.size() > mCurrentPosition) {
                                ShowEntity showEntity = mBitmaps.get(mCurrentPosition);
                                showEntity.setPic(bitmap);
                                mBitmaps.set(mCurrentPosition, showEntity);
                                if (mAddPicAdapter != null)
                                    mAddPicAdapter.notifyDataSetChanged();
                                if (mCurrentPosition == 0)
                                    mFilePath1 = new File(path);
                                else if (mCurrentPosition == 1)
                                    mFilePath2 = new File(path);
                            } else {
                                ToastUtil.showToast(this, "获取图片失败");
                            }
                        } else if (ImageUtils.isGifFile(new File(path))) {
                            ToastUtil.showToast(this, "不支持Gif格式图片");
                        } else {
                            ToastUtil.showToast(this, "该文件不存在");
                        }
                    } catch (Exception ex) {
                        LogUtils.e(ex.getMessage());
                    }
                }
                break;
            case REQUEST_CODE:
                mPresenter.getData();
                break;
        }
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SpacesItemDecoration(int space) {
            this.space = ViewUtils.dip2px(MarketCommentDetailActivity.this, space);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.bottom = 30;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBigImageDialog != null) {
            mBigImageDialog.dismiss();
            mBigImageDialog = null;
        }
        FileUtils.deleteZqbCacheImg();
    }
}
