package com.zhengdao.zqb.view.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.customview.BigImageDialog;
import com.zhengdao.zqb.utils.DensityUtil;
import com.zhengdao.zqb.utils.ImageUtils;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.utils.UriUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.zhengdao.zqb.config.Constant.Assist.mCommonPicPath;
import static com.zhengdao.zqb.config.Constant.Assist.mHttpRewardPicPath;
import static com.zhengdao.zqb.config.Constant.EditableList;

/**
 * @Author lijiangop
 * @CreateTime 2018/4/13 16:17
 */
public class TextEditActivity extends AppCompatActivity {

    public final int ACTION_CHOOSE_FLOW = 001;
    @BindView(R.id.iv_title_bar_back)
    ImageView mIvTitleBarBack;
    @BindView(R.id.tv_title_bar_title)
    TextView  mTvTitleBarTitle;
    @BindView(R.id.tv_title_bar_right)
    TextView  mTvTitleBarRight;
    @BindView(R.id.et_input)
    EditText  mEtInput;
    @BindView(R.id.tv_content_count)
    TextView  mTvContentCount;
    @BindView(R.id.iv_add_pic_flow)
    ImageView mIvAddPicFlow;

    private InputMethodManager mImm;
    private View               decorView;
    private String             mActivityContent;
    public int MaxNum = 50;
    public  int            editEnd;
    public  int            editStart;
    private int            mActivityType;
    private int            mActivitySkipType;
    private BigImageDialog mBigImageDialog;
    private HashMap        mHashMap;
    private Handler mHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                onImgDownloadFinish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private String          mHttpComparePicPath;
    private SpannableString mSpannableString;
    private int[]           mScreenSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_text_edit);
        ButterKnife.bind(this);
        init();
        initClickListener();
    }

    private void init() {
        mImm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        decorView = getWindow().getDecorView();
        mIvAddPicFlow.setVisibility(View.GONE);
        mTvContentCount.setVisibility(View.GONE);
        mActivityContent = getIntent().getStringExtra(Constant.Activity.Data);
        mActivityType = getIntent().getIntExtra(Constant.Activity.Type, 0);
        mActivitySkipType = getIntent().getIntExtra(Constant.Activity.Type1, 0);
        mHttpComparePicPath = getIntent().getStringExtra(Constant.Activity.Common);
        if (TextUtils.isEmpty(mHttpComparePicPath))
            mHttpComparePicPath = mHttpRewardPicPath;
        String title;
        switch (mActivityType) {
            case 1:
                title = "领取条件";
                doViewBack(mActivityContent, 0);
                break;
            case 2:
                title = "流程说明";
                doViewBack(mActivityContent, 1);
                mIvAddPicFlow.setVisibility(View.VISIBLE);
                break;
            default:
                title = "标题";
                doViewBack(mActivityContent, 0);
                mTvContentCount.setText("" + MaxNum);
                mTvContentCount.setVisibility(View.VISIBLE);
                break;
        }
        mTvTitleBarTitle.setText(title);
        if (mActivityType == 0)
            mEtInput.addTextChangedListener(mTextWatcher);
        if (!EditableList.contains(mActivitySkipType)) {
            mTvTitleBarRight.setVisibility(View.GONE);
            mIvAddPicFlow.setVisibility(View.GONE);
            mEtInput.setEnabled(false);
        }
        mScreenSize = DensityUtil.getScreenSize(TextEditActivity.this);
    }

    private void initClickListener() {
        mTvTitleBarRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftInput();
                String value = mEtInput.getText().toString().trim();
                Intent intent = new Intent();
                intent.putExtra(Constant.Activity.Data, value);
                setResult(RESULT_OK, intent);
                TextEditActivity.this.finish();
            }
        });
        mIvTitleBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftInput();
                TextEditActivity.this.finish();
            }
        });
        mIvAddPicFlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
                getImage.addCategory(Intent.CATEGORY_OPENABLE);
                getImage.setType("image/*");
                startActivityForResult(getImage, ACTION_CHOOSE_FLOW);
            }
        });
    }

    private void doViewBack(String string, int i) {
        try {
            switch (i) {
                case 0://任务条件;标题
                    if (!TextUtils.isEmpty(string)) {
                        mEtInput.setText(string);
                        mTvContentCount.setText(String.valueOf(MaxNum - calculateLength(mEtInput.getText().toString())));
                    }
                    break;
                case 1://流程说明
                    if (!TextUtils.isEmpty(string)) {
                        Pattern p = Pattern.compile(mHttpComparePicPath);
                        Matcher m = p.matcher(string);
                        while (m.find()) {
                            final String url = m.group();
                            Glide.with(this).load(url).asBitmap().error(getResources().getDrawable(R.drawable.net_less_140)).into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    if (mHashMap == null)
                                        mHashMap = new HashMap();
                                    mHashMap.put(url, resource);
                                    mHander.sendMessage(mHander.obtainMessage());
                                }
                            });
                        }
                        mEtInput.setText(string);
                    }
                    if (!TextUtils.isEmpty(string)) {
                        mSpannableString = new SpannableString(string);
                        Pattern pattern = Pattern.compile(mCommonPicPath);
                        Matcher m = pattern.matcher(string);
                        while (m.find()) {
                            if (!TextUtils.isEmpty(m.group())) {
                                Bitmap bitmap = BitmapFactory.decodeFile(m.group());
                                bitmap = ImageUtils.zoomImg(bitmap, 500, 700);
                                ImageSpan span = new ImageSpan(TextEditActivity.this, bitmap);
                                mSpannableString.setSpan(span, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                        }
                        mEtInput.setText(mSpannableString);
                    }
                    break;
                default:
                    break;
            }
            hideSoftInput();
            mEtInput.clearFocus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onImgDownloadFinish() {
        try {
            if (!TextUtils.isEmpty(mActivityContent)) {
                if (TextUtils.isEmpty(mSpannableString))
                    mSpannableString = new SpannableString(mActivityContent);
                Pattern pattern = Pattern.compile(mHttpComparePicPath);
                Matcher m = pattern.matcher(mActivityContent);
                while (m.find()) {
                    if (mHashMap.containsKey(m.group())) {
                        Bitmap bitmap = ImageUtils.zoomImg((Bitmap) mHashMap.get(m.group()), 500, 700);
                        ImageSpan span = new ImageSpan(TextEditActivity.this, bitmap);
                        mSpannableString.setSpan(span, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        MyClickableSpan myClickableSpan = new MyClickableSpan(m.group());
                        mSpannableString.setSpan(myClickableSpan, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
                mEtInput.setText(mSpannableString);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            mTvContentCount.setText(String.valueOf(MaxNum - calculateLength(mEtInput.getText().toString())));
        }
    };

    public static int calculateLength(CharSequence cs) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ACTION_CHOOSE_FLOW:
                if (resultCode == RESULT_OK && data != null) {
                    Uri originalUri = data.getData();
                    String path = UriUtils.getRealPathFromUri(this, originalUri);
                    if (new File(path).exists() && !ImageUtils.isGifFile(new File(path))) {
                        if (checkIsUsablePic(path)) {
                            InputStream inputStream = null;
                            try {
                                //优化
                                int[] imageScale = ImageUtils.getImageScale(path);//获取图片长宽

                                int width = (mScreenSize[0] - DensityUtil.dip2px(TextEditActivity.this, 30)) / 2;

                                int height = width * imageScale[1] / imageScale[0];

                                inputStream = getContentResolver().openInputStream(originalUri);

                                Bitmap bitmap = ImageUtils.zoomImg(BitmapFactory.decodeStream(inputStream), width, height == 0 ? width : height);

                                if (bitmap != null) {
                                    insertIntoEditText(getBitmapMime(bitmap, originalUri));
                                } else {
                                    ToastUtil.showToast(TextEditActivity.this, "获取图片失败");
                                }
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } finally {
                                try {
                                    inputStream.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else
                            ToastUtil.showToast(TextEditActivity.this, "暂不支持该路径图片");
                    } else if (ImageUtils.isGifFile(new File(path))) {
                        ToastUtil.showToast(this, "不支持Gif格式图片");
                    } else {
                        ToastUtil.showToast(TextEditActivity.this, "该文件不存在");
                    }
                }
                break;
        }
    }

    private boolean checkIsUsablePic(String path) {
        Pattern p = Pattern.compile(mCommonPicPath);
        Matcher m = p.matcher(path);
        while (m.find()) {
            if (!TextUtils.isEmpty(m.group())) {
                LogUtils.i(m.group());
                File file = new File(m.group());
                if (!file.exists())
                    return false;
            } else
                return false;
        }
        return true;
    }

    private SpannableString getBitmapMime(Bitmap pic, Uri uri) {
        SpannableString ss = null;
        try {
            String path = UriUtils.getRealPathFromUri(this, uri);
            ss = new SpannableString(path);
            ImageSpan span = new ImageSpan(this, pic);
            ss.setSpan(span, 0, path.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ss;
    }

    private void insertIntoEditText(SpannableString ss) {
        Editable et = mEtInput.getText();// 先获取Edittext中的内容
        int start = mEtInput.getSelectionStart();
        et.insert(start, ss);// 设置ss要添加的位置
        mEtInput.setText(et);// 把et添加到Edittext中
        if (et.length() < (start + ss.length())) {
            mEtInput.setSelection(et.length());// 设置Edittext中光标在最后面显示
        } else
            mEtInput.setSelection(start + ss.length());// 设置Edittext中光标在最后面显示
    }

    public void hideSoftInput() {
        if (mImm != null && decorView != null) {
            mImm.hideSoftInputFromWindow(decorView.getWindowToken(), 0);
        }
    }

    private void doShowBigImage(String url) {
        if (!TextUtils.isEmpty(url)) {
            if (mBigImageDialog == null)
                mBigImageDialog = new BigImageDialog(TextEditActivity.this);
            mBigImageDialog.display(url);
            mBigImageDialog.show();
        }
    }

    class MyClickableSpan extends ClickableSpan {

        private String content;

        public MyClickableSpan(String content) {
            this.content = content;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setUnderlineText(false);
        }

        @Override
        public void onClick(View widget) {
            doShowBigImage(content);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
