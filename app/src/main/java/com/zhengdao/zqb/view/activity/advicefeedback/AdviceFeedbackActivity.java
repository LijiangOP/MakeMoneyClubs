package com.zhengdao.zqb.view.activity.advicefeedback;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.FileUtils;
import com.zhengdao.zqb.utils.ImageUtils;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.utils.ViewUtils;
import com.zhengdao.zqb.view.adapter.AddPicAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class AdviceFeedbackActivity extends MVPBaseActivity<AdviceFeedbackContract.View, AdviceFeedbackPresenter> implements AdviceFeedbackContract.View, View.OnClickListener, AddPicAdapter.CallBack {

    private static final int ACTION_CHOOSE = 007;
    @BindView(R.id.iv_funtion)
    ImageView    mIvFuntion;
    @BindView(R.id.iv_advice)
    ImageView    mIvAdvice;
    @BindView(R.id.iv_other)
    ImageView    mIvOther;
    @BindView(R.id.et_input)
    EditText     mEtInput;
    @BindView(R.id.tv_descibe_count)
    TextView     mTvDescibeCount;
    @BindView(R.id.tv_image_count)
    TextView     mTvImageCount;
    @BindView(R.id.tv_image_count_tag)
    TextView     mTvImageCountTag;
    @BindView(R.id.tv_image_count_total)
    TextView     mTvImageCountTotal;
    @BindView(R.id.recycle_view)
    RecyclerView mRecycleView;
    @BindView(R.id.ll_flow_upload_pic)
    LinearLayout mLlFlowUploadPic;
    @BindView(R.id.tv_commit)
    TextView     mTvCommit;

    private long mCurrentTimeMillis = 0;
    public int mType;
    public int editEnd;
    public int editStart;
    public  int     MaxNum       = 250;
    private boolean mIsUploading = false;
    private int               mUserId;
    public  String            mStringType;
    private ArrayList<String> mImages;
    private AddPicAdapter     mAddPicAdapter;
    private int               mCurrentPicPosition;//辅助记位置
    private int               mAlreadyAddCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.advice_feedback);
        ButterKnife.bind(this);
        setTitle("意见反馈");
        initData();
        initListener();
        initImgUploadPart();
    }

    private void initData() {
        mUserId = getIntent().getIntExtra(Constant.Activity.Data, 0);
    }

    private void initListener() {
        mIvFuntion.setOnClickListener(this);
        mIvAdvice.setOnClickListener(this);
        mIvOther.setOnClickListener(this);
        mTvCommit.setOnClickListener(this);
        mEtInput.addTextChangedListener(mTextWatcher);
        mLlFlowUploadPic.setVisibility(View.VISIBLE);
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            editStart = mEtInput.getSelectionStart();
            editEnd = mEtInput.getSelectionEnd();
            mEtInput.removeTextChangedListener(mTextWatcher);
            while (calculateLength(s.toString()) > MaxNum) {
                s.delete(editStart - 1, editEnd);
                editStart--;
                editEnd--;
            }
            mEtInput.addTextChangedListener(mTextWatcher);
            mTvDescibeCount.setText(String.valueOf(calculateLength(mEtInput.getText().toString())));
        }
    };

    public static long calculateLength(CharSequence cs) {
        double len = 0;
        for (int i = 0; i < cs.length(); i++) {
            int tmp = (int) cs.charAt(i);
            if (tmp > 0 && tmp < 127) {
                len += 1;
            } else {
                len++;
            }
        }
        long round = Math.round(len);
        return new Long(round).intValue();
    }

    private void initImgUploadPart() {
        if (mImages == null)
            mImages = new ArrayList<>();
        mImages.clear();
        mImages.add("");
        if (mAddPicAdapter == null) {
            mAddPicAdapter = new AddPicAdapter(this, R.layout.addpic_layout, mImages, this);
            mRecycleView.setLayoutManager(new GridLayoutManager(this, 4));
            mRecycleView.addItemDecoration(new SpacesItemDecoration(5));
            mRecycleView.setAdapter(mAddPicAdapter);
        } else
            mAddPicAdapter.notifyDataSetChanged();
        mTvImageCount.setText("0");
        mTvImageCountTag.setText("/");
        mTvImageCountTotal.setText("4");
    }

    @Override
    public void onPicAdd(int position) {
        mCurrentPicPosition = position;
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
        if (mImages != null && mImages.size() > position)
            mImages.remove(position);
        if (mImages.size() == 0 || mImages.size() == 3)
            mImages.add("");
        if (mAddPicAdapter != null)
            mAddPicAdapter.notifyDataSetChanged();
        mAlreadyAddCount--;
        mTvImageCount.setText("" + mAlreadyAddCount);
    }

    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - mCurrentTimeMillis < 500)
            return;
        mCurrentTimeMillis = System.currentTimeMillis();
        switch (v.getId()) {
            case R.id.iv_funtion:
                if (mType != 1) {
                    mType = 1;
                    mStringType = "功能异常：功能故障或不可以用";
                    mIvFuntion.setImageDrawable(getResources().getDrawable(R.drawable.btn_select));
                    mIvAdvice.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvOther.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                } else {
                    mType = 0;
                    mStringType = "";
                    mIvFuntion.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                }
                break;
            case R.id.iv_advice:
                if (mType != 2) {
                    mType = 2;
                    mStringType = "产品建议：用的不爽，我有好的建议";
                    mIvFuntion.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvAdvice.setImageDrawable(getResources().getDrawable(R.drawable.btn_select));
                    mIvOther.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                } else {
                    mType = 0;
                    mStringType = "";
                    mIvAdvice.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                }
                break;
            case R.id.iv_other:
                if (mType != 3) {
                    mType = 3;
                    mStringType = "其它问题";
                    mIvFuntion.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvAdvice.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvOther.setImageDrawable(getResources().getDrawable(R.drawable.btn_select));
                } else {
                    mType = 0;
                    mStringType = "";
                    mIvOther.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                }
                break;
            case R.id.tv_commit:
                doCommit();
                break;
        }
    }

    private void doCommit() {
        if (mIsUploading)
            return;
        if (mType == 0) {
            ToastUtil.showToast(AdviceFeedbackActivity.this, "请选择问题类型");
            mIsUploading = false;
            return;
        }
        String describe = mEtInput.getText().toString().trim();
        if (mType == 3 && TextUtils.isEmpty(describe)) {
            ToastUtil.showToast(AdviceFeedbackActivity.this, "请输入问题描述");
            mIsUploading = false;
            return;
        }
        boolean hasPic = false;
        Map<String, RequestBody> images = new HashMap<>();
        if (mImages != null && mImages.size() > 0) {
            for (String string : mImages) {
                if (!TextUtils.isEmpty(string)) {
                    File file = ImageUtils.compressImage(this, new File(string), System.currentTimeMillis() + ".jpg");
                    RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                    images.put("files\";filename=\"" + file.getName(), fileBody);
                    hasPic = true;
                }
            }
            if (images.size() > 0) {
                RequestBody type = RequestBody.create(MediaType.parse("multipart/form-data"), "" + Constant.Upload.Type_Advice_Report);
                mPresenter.uploadImages(type, images);
            }
        }
        if (!hasPic)
            doUploadJsonInfo(new ArrayList<String>());
        mIsUploading = true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ACTION_CHOOSE:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        try {
                            String path = FileUtils.getImageAbsolutePath(this, data.getData());
                            if (new File(path).exists() && !ImageUtils.isGifFile(new File(path))) {
                                Bitmap bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(path),
                                        ViewUtils.dip2px(AdviceFeedbackActivity.this, 72), ViewUtils.dip2px(AdviceFeedbackActivity.this, 72));
                                if (bitmap != null) {
                                    if (mImages != null)
                                        mImages.set(mCurrentPicPosition, path);
                                    boolean ishasAdd = false;
                                    for (String string : mImages) {
                                        if (TextUtils.isEmpty(string)) {
                                            ishasAdd = true;
                                            break;
                                        }
                                    }
                                    if (mImages.size() < 4 && !ishasAdd)
                                        mImages.add("");
                                    if (mAddPicAdapter != null)
                                        mAddPicAdapter.notifyDataSetChanged();
                                    mAlreadyAddCount++;
                                    mTvImageCount.setText("" + mAlreadyAddCount);
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
                }
                break;
        }
    }

    @Override
    public void onImgUploadError() {
        mIsUploading = false;
        ToastUtil.showToast(AdviceFeedbackActivity.this, "图片上传失败");
    }

    @Override
    public void onImgUploadResult(HttpResult<ArrayList<String>> result) {
        if (result.code == Constant.HttpResult.SUCCEED) {
            doUploadJsonInfo(result.data);
        } else {
            mIsUploading = false;
            ToastUtil.showToast(AdviceFeedbackActivity.this, TextUtils.isEmpty(result.msg) ? "" : result.msg);
        }
    }

    private void doUploadJsonInfo(ArrayList<String> list) {
        String describe = mEtInput.getText().toString().trim();
        mStringType = TextUtils.isEmpty(mStringType) ? "" : mStringType;
        mPresenter.FeedbackOpinion(mType, mStringType + describe, mUserId, 2, list);
    }


    @Override
    public void showErrorMessage(String msg) {
        mIsUploading = false;
        super.showErrorMessage(msg);
    }

    @Override
    public void onUploadResult(HttpResult result) {
        mIsUploading = false;
        if (result.code == Constant.HttpResult.SUCCEED) {
            ToastUtil.showToast(AdviceFeedbackActivity.this, "提交成功");
            this.finish();
        } else {
            ToastUtil.showToast(AdviceFeedbackActivity.this, TextUtils.isEmpty(result.msg) ? "" : result.msg);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "zqb");
                deleteFile(dir);
            }
        } catch (Exception e) {
            LogUtils.e(e.getMessage());
        }
    }

    private void deleteFile(File file) throws Exception {
        if (file == null || !file.exists() || !file.isDirectory())
            return;
        for (File file1 : file.listFiles()) {
            if (file1.isFile())
                file1.delete(); // 删除所有文件
            else if (file1.isDirectory())
                deleteFile(file1); // 递规的方式删除文件夹
        }
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SpacesItemDecoration(int space) {
            this.space = ViewUtils.dip2px(AdviceFeedbackActivity.this, space);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.bottom = 30;
        }

    }
}
