package com.zhengdao.zqb.view.activity.report;


import android.Manifest;
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

import com.tbruyelle.rxpermissions.RxPermissions;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.FileUtils;
import com.zhengdao.zqb.utils.ImageUtils;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.utils.ViewUtils;
import com.zhengdao.zqb.view.activity.login.LoginActivity;
import com.zhengdao.zqb.view.adapter.AddPicAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.functions.Action1;


public class ReportActivity extends MVPBaseActivity<ReportContract.View, ReportPresenter> implements ReportContract.View, View.OnClickListener, AddPicAdapter.CallBack {

    private static final int ACTION_CHOOSE = 007;
    @BindView(R.id.iv_type_1)
    ImageView    mIvType1;
    @BindView(R.id.iv_type_2)
    ImageView    mIvType2;
    @BindView(R.id.iv_type_3)
    ImageView    mIvType3;
    @BindView(R.id.iv_type_4)
    ImageView    mIvType4;
    @BindView(R.id.iv_type_5)
    ImageView    mIvType5;
    @BindView(R.id.iv_type_6)
    ImageView    mIvType6;
    @BindView(R.id.iv_type_other)
    ImageView    mIvTypeOther;
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
    public int editEnd;
    public int mType = 0;
    public int editStart;
    public  int     MaxNum       = 250;
    private boolean mIsUploading = false;
    private int               mUserId;
    private String            mStringType;
    private ArrayList<String> mImages;
    private AddPicAdapter     mAddPicAdapter;
    private int               mCurrentPicPosition;//辅助记位置
    private int               mAlreadyAddCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain);
        ButterKnife.bind(this);
        setTitle("举报");
        init();
        initClickListener();
        initImgUploadPart();
    }

    private void init() {
        mUserId = getIntent().getIntExtra(Constant.Activity.Data, 0);
    }

    private void initClickListener() {
        mIvType1.setOnClickListener(this);
        mIvType2.setOnClickListener(this);
        mIvType3.setOnClickListener(this);
        mIvType4.setOnClickListener(this);
        mIvType5.setOnClickListener(this);
        mIvType6.setOnClickListener(this);
        mIvTypeOther.setOnClickListener(this);
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
        RxPermissions rxPermissions = new RxPermissions(ReportActivity.this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                try {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(intent, ACTION_CHOOSE);
                } catch (Exception ex) {
                    LogUtils.e(ex.getMessage());
                }
            }
        });
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
            case R.id.iv_type_1:
                doTypeSelect(1);
                break;
            case R.id.iv_type_2:
                doTypeSelect(2);
                break;
            case R.id.iv_type_3:
                doTypeSelect(3);
                break;
            case R.id.iv_type_4:
                doTypeSelect(4);
                break;
            case R.id.iv_type_5:
                doTypeSelect(5);
                break;
            case R.id.iv_type_6:
                doTypeSelect(6);
                break;
            case R.id.iv_type_other:
                doTypeSelect(7);
                break;
            case R.id.tv_commit:
                doCommit();
                break;
        }
    }

    private void doTypeSelect(int i) {
        switch (i) {
            case 1:
                if (mType != 1) {
                    mType = 1;
                    mStringType = "悬赏不合理，根本无法达到要求。";
                    mIvType1.setImageDrawable(getResources().getDrawable(R.drawable.btn_select));
                    mIvType2.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvType3.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvType4.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvType5.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvType6.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvTypeOther.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                } else {
                    mType = 0;
                    mStringType = "";
                    mIvType1.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                }
                break;
            case 2:
                if (mType != 2) {
                    mType = 2;
                    mStringType = "悬赏描述与实际操作过程不符，有隐藏步骤等。";
                    mIvType1.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvType2.setImageDrawable(getResources().getDrawable(R.drawable.btn_select));
                    mIvType3.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvType4.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvType5.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvType6.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvTypeOther.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                } else {
                    mType = 0;
                    mStringType = "";
                    mIvType2.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                }
                break;
            case 3:
                if (mType != 3) {
                    mType = 3;
                    mStringType = "分类不清或其他违规。";
                    mIvType1.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvType2.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvType3.setImageDrawable(getResources().getDrawable(R.drawable.btn_select));
                    mIvType4.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvType5.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvType6.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvTypeOther.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                } else {
                    mType = 0;
                    mStringType = "";
                    mIvType3.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                }
                break;
            case 4:
                if (mType != 4) {
                    mType = 4;
                    mStringType = "联系悬赏主无任何回应。";
                    mIvType1.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvType2.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvType3.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvType4.setImageDrawable(getResources().getDrawable(R.drawable.btn_select));
                    mIvType5.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvType6.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvTypeOther.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                } else {
                    mType = 0;
                    mStringType = "";
                    mIvType4.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                }
                break;
            case 5:
                if (mType != 5) {
                    mType = 5;
                    mStringType = "悬赏主要求私下交易。";
                    mIvType1.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvType2.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvType3.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvType4.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvType5.setImageDrawable(getResources().getDrawable(R.drawable.btn_select));
                    mIvType6.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvTypeOther.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                } else {
                    mType = 0;
                    mStringType = "";
                    mIvType5.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                }
                break;
            case 6:
                if (mType != 6) {
                    mType = 6;
                    mStringType = "关键词与实际不符。";
                    mIvType1.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvType2.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvType3.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvType4.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvType5.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvType6.setImageDrawable(getResources().getDrawable(R.drawable.btn_select));
                    mIvTypeOther.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                } else {
                    mType = 0;
                    mStringType = "";
                    mIvType6.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                }
                break;
            case 7:
                if (mType != 7) {
                    mType = 7;
                    mStringType = "其他问题";
                    mIvType1.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvType2.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvType3.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvType4.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvType5.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvType6.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvTypeOther.setImageDrawable(getResources().getDrawable(R.drawable.btn_select));
                } else {
                    mType = 0;
                    mStringType = "";
                    mIvTypeOther.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                }
                break;
        }
    }

    private void doCommit() {
        if (mIsUploading)
            return;
        if (mType == 0) {
            ToastUtil.showToast(ReportActivity.this, "请选择问题类型");
            mIsUploading = false;
            return;
        }
        String describe = mEtInput.getText().toString().trim();
        if (mType == 7 && TextUtils.isEmpty(describe)) {
            ToastUtil.showToast(ReportActivity.this, "请输入问题描述");
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

    private void doUploadJsonInfo(ArrayList<String> strings) {
        String describe = mEtInput.getText().toString().trim();
        mStringType = TextUtils.isEmpty(mStringType) ? "" : mStringType;
        mPresenter.FeedbackOpinion(mType, mStringType + describe, mUserId, 1, strings);
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
                                        ViewUtils.dip2px(ReportActivity.this, 72), ViewUtils.dip2px(ReportActivity.this, 72));
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
        ToastUtil.showToast(ReportActivity.this, "图片上传失败");
    }

    @Override
    public void onImgUploadResult(HttpResult<ArrayList<String>> result) {
        if (result.code == Constant.HttpResult.SUCCEED) {
            doUploadJsonInfo(result.data);
        } else {
            mIsUploading = false;
            ToastUtil.showToast(ReportActivity.this, TextUtils.isEmpty(result.msg) ? "" : result.msg);
        }
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
            ToastUtil.showToast(ReportActivity.this, "提交成功");
            ReportActivity.this.finish();
        } else if (result.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录");
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            ToastUtil.showToast(ReportActivity.this, TextUtils.isEmpty(result.msg) ? "" : result.msg);
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
            this.space = ViewUtils.dip2px(ReportActivity.this, space);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.bottom = 30;
        }

    }
}
