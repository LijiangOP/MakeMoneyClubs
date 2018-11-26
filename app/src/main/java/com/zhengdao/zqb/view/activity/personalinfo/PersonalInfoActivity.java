package com.zhengdao.zqb.view.activity.personalinfo;


import android.Manifest;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.bigkoo.pickerview.TimePickerView;
import com.bumptech.glide.Glide;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.yalantis.ucrop.UCrop;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.UserInfoBean;
import com.zhengdao.zqb.event.UpDataUserInfoEvent;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.FileUtils;
import com.zhengdao.zqb.utils.ImageUtils;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.view.activity.changeinfo.ChangeInfoActivity;
import com.zhengdao.zqb.view.activity.login.LoginActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.functions.Action1;

public class PersonalInfoActivity extends MVPBaseActivity<PersonalInfoContract.View, PersonalInfoPresenter> implements PersonalInfoContract.View, View.OnClickListener {

    private static final int SEX_MAN          = 0;
    private static final int SEX_WOMEN        = 1;
    private static final int SEX_SECRET       = 2;
    private static final int ACTION_CHOOSE    = 10;
    private static final int ACTION_CROP      = 9;
    private static final int ACTION_USER_NAME = 5;
    private static final int ACTION_NICK_NAME = 6;
    private static final int ACTION_QQ        = 7;

    @BindView(R.id.iv_user_icon)
    CircleImageView mIvUserIcon;
    @BindView(R.id.ll_user_icon)
    LinearLayout    mLlUserIcon;
    @BindView(R.id.tv_username)
    TextView        mTvUsername;
    @BindView(R.id.ll_user_name)
    LinearLayout    mLlUserName;
    @BindView(R.id.tv_nickname)
    TextView        mTvNickname;
    @BindView(R.id.ll_user_nickname)
    LinearLayout    mLlUserNickname;
    @BindView(R.id.tv_sex)
    TextView        mTvSex;
    @BindView(R.id.ll_user_sex)
    LinearLayout    mLlUserSex;
    @BindView(R.id.tv_birth)
    TextView        mTvBirth;
    @BindView(R.id.ll_user_birthday)
    LinearLayout    mLlUserBirthday;
    @BindView(R.id.tv_qq)
    TextView        mTvQq;
    @BindView(R.id.ll_user_qq)
    LinearLayout    mLlUserQq;

    private long mCurrentTimeMillis = 0;
    private PopupWindow mPopupWindow;
    private File        mCropFile;
    private int mMaxWidth  = 1080;
    private int mMaxHeight = 1080;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalinfo);
        ButterKnife.bind(this);
        setTitle("个人信息");
        setOnclickListener();
        mPresenter.getUserData();
    }

    private void setOnclickListener() {
        mLlUserIcon.setOnClickListener(this);
        mLlUserName.setOnClickListener(this);
        mLlUserNickname.setOnClickListener(this);
        mLlUserSex.setOnClickListener(this);
        mLlUserBirthday.setOnClickListener(this);
        mLlUserQq.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - mCurrentTimeMillis < 1000)
            return;
        mCurrentTimeMillis = System.currentTimeMillis();
        switch (v.getId()) {
            case R.id.ll_user_icon:
                doSelectPhoto();
                break;
            case R.id.ll_user_name:
                Intent userName = new Intent(PersonalInfoActivity.this, ChangeInfoActivity.class);
                userName.putExtra(Constant.Activity.Type, "userName");
                if (SettingUtils.isLogin(PersonalInfoActivity.this))
                    startActivityForResult(userName, ACTION_USER_NAME);
                else
                    startActivity(new Intent(PersonalInfoActivity.this, LoginActivity.class));
                break;
            case R.id.ll_user_nickname:
                Intent nickName = new Intent(PersonalInfoActivity.this, ChangeInfoActivity.class);
                nickName.putExtra(Constant.Activity.Type, "nickName");
                if (SettingUtils.isLogin(PersonalInfoActivity.this))
                    startActivityForResult(nickName, ACTION_NICK_NAME);
                else
                    startActivity(new Intent(PersonalInfoActivity.this, LoginActivity.class));
                break;
            case R.id.ll_user_sex:
                doSelectSex();
                break;
            case R.id.ll_user_birthday:
                doSelectBirth();
                break;
            case R.id.ll_user_qq:
                Intent qqIntent = new Intent(PersonalInfoActivity.this, ChangeInfoActivity.class);
                qqIntent.putExtra(Constant.Activity.Type, "qq");
                if (SettingUtils.isLogin(PersonalInfoActivity.this))
                    startActivityForResult(qqIntent, ACTION_QQ);
                else
                    startActivity(new Intent(PersonalInfoActivity.this, LoginActivity.class));
                break;
            case R.id.btn_top:
                doChangeSex(0);
                mTvSex.setText("男");
                break;
            case R.id.btn_center:
                doChangeSex(1);
                mTvSex.setText("女");
                break;
            case R.id.btn_bottom:
                doChangeSex(2);
                mTvSex.setText("保密");
            case R.id.btn_pop_exit:
                if (mPopupWindow != null)
                    mPopupWindow.dismiss();
                break;
        }
    }

    private void doChangeSex(int sex) {
        if (mPopupWindow != null)
            mPopupWindow.dismiss();
        try {
            JSONObject json = new JSONObject();
            json.put("sex", "" + sex);
            mPresenter.editUserInfo(json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void doSelectPhoto() {
        RxPermissions rxPermissions = new RxPermissions(PersonalInfoActivity.this);
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

    private void doSelectSex() {
        int layoutId = R.layout.popup_three_option_layout;
        View contentView = LayoutInflater.from(this).inflate(layoutId, null);
        Button btnTop = contentView.findViewById(R.id.btn_top);
        Button btnCenter = contentView.findViewById(R.id.btn_center);
        Button btnBottom = contentView.findViewById(R.id.btn_bottom);
        Button btnExit = contentView.findViewById(R.id.btn_pop_exit);
        btnTop.setText("男");
        btnCenter.setText("女");
        btnBottom.setText("保密");
        btnCenter.setOnClickListener(this);
        btnTop.setOnClickListener(this);
        btnBottom.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        mPopupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        mPopupWindow.showAtLocation(mLlUserSex, Gravity.BOTTOM, 0, 0);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });
        backgroundAlpha(0.5f);
    }

    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);
    }

    private void doSelectBirth() {
        hideSoftInput();
        boolean[] booleen = new boolean[]{true, true, true, false, false, false};
        Calendar startDate = Calendar.getInstance();
        startDate.set(1930, 0, 1);
        Calendar endDate = Calendar.getInstance();
        endDate.set(endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH), endDate.get(Calendar.DAY_OF_MONTH));
        TimePickerView pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                mTvBirth.setText(getTime(date));
                doChangeBirth(getTime(date));
            }
        })
                .setType(booleen) //显示类型
                .setRangDate(startDate, endDate)//起始终止年月日设定
                .build();
        pvTime.setDate(Calendar.getInstance());
        pvTime.show();
    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    private void doChangeBirth(String time) {
        try {
            JSONObject json = new JSONObject();
            json.put("birthday", time);
            mPresenter.editUserInfo(json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ACTION_CHOOSE:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        String path = FileUtils.getImageAbsolutePath(this, data.getData());
                        if (new File(path).exists() && !ImageUtils.isGifFile(new File(path))) {
                            mCropFile = ImageUtils.compressImage(this, new File(path), System.currentTimeMillis() + ".jpg");
                            try {
                                cropImage(Uri.fromFile(new File(path)), Uri.fromFile(mCropFile));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (ImageUtils.isGifFile(new File(path))) {
                            ToastUtil.showToast(this, "不支持Gif格式图片");
                        } else {
                            ToastUtil.showToast(this, "该文件不存在");
                        }
                    }
                }
                break;
            case ACTION_CROP:
                if (resultCode == RESULT_OK)
                    uploadImage();
                break;
            case UCrop.REQUEST_CROP:
                if (resultCode == RESULT_OK)
                    uploadImage();
                break;
            case UCrop.RESULT_ERROR:
                Throwable cropError = UCrop.getError(data);
                LogUtils.e("" + cropError.getMessage());
                break;
            case ACTION_USER_NAME:
                if (data != null) {
                    String Username = data.getStringExtra(Constant.Activity.FreedBack);
                    if (!TextUtils.isEmpty(Username))
                        mTvUsername.setText(Username);
                }
                break;
            case ACTION_NICK_NAME:
                if (data != null) {
                    String Nickname = data.getStringExtra(Constant.Activity.FreedBack);
                    if (!TextUtils.isEmpty(Nickname))
                        mTvNickname.setText(Nickname);
                }
                break;
            case ACTION_QQ:
                if (data != null) {
                    String qq = data.getStringExtra(Constant.Activity.FreedBack);
                    if (!TextUtils.isEmpty(qq))
                        mTvQq.setText(qq);
                }
                break;
        }
    }

    //选择或拍照完成后跳转切图界面
    public void cropImage(final Uri uri, final Uri cropUri) throws Exception {
        RxPermissions rxPermissions = new RxPermissions(PersonalInfoActivity.this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                UCrop.of(uri, cropUri)
                        .withAspectRatio(9, 9)
                        .withMaxResultSize(mMaxWidth, mMaxHeight)
                        .start(PersonalInfoActivity.this);
            }
        });
    }

    //上传图片
    private void uploadImage() {
        if (mCropFile != null) {
            if (!TextUtils.isEmpty(mCropFile.getAbsolutePath())) {
                LogUtils.i(mCropFile.getAbsolutePath());
                mIvUserIcon.setImageBitmap(BitmapFactory.decodeFile(mCropFile.getAbsolutePath()));
            }
            mProgressDialog.setMessage("正在上传");
            Map<String, RequestBody> images = new HashMap<>();
            if (mCropFile != null && mCropFile.exists()) {
                RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), mCropFile);
                images.put("files\";filename=\"" + mCropFile.getName(), fileBody);
                RequestBody type = RequestBody.create(MediaType.parse("multipart/form-data"), "" + Constant.Upload.Type_Avator);
                mPresenter.uploadImages(type, images);
            }
        }
    }


    @Override
    public void ShowView(UserInfoBean result) {
        if (result.code == Constant.HttpResult.SUCCEED) {
            Glide.with(this).load(result.user.avatar).error(R.drawable.default_icon).into(mIvUserIcon);
            mTvNickname.setText(TextUtils.isEmpty(result.user.nickName) ? "" : result.user.nickName);
            mTvUsername.setText(TextUtils.isEmpty(result.user.userName) ? "" : result.user.userName);
            switch (result.userinfo.sex) {
                case SEX_MAN:
                    mTvSex.setText("男");
                    break;
                case SEX_WOMEN:
                    mTvSex.setText("女");
                    break;
                case SEX_SECRET:
                    mTvSex.setText("保密");
                    break;
                default:
                    break;
            }
            mTvBirth.setText(TextUtils.isEmpty(result.userinfo.birthday) ? "" : result.userinfo.birthday);
            mTvQq.setText(TextUtils.isEmpty(result.userinfo.qq) ? "" : result.userinfo.qq);
        } else if (result.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录");
            startActivity(new Intent(this, LoginActivity.class));
        } else if (!TextUtils.isEmpty(result.msg))
            ToastUtil.showToast(this, TextUtils.isEmpty(result.msg) ? "获取数据失败" : result.msg);
    }

    @Override
    public void onUploadAvatarResult(HttpResult<ArrayList<String>> result) {
        if (result.code == Constant.HttpResult.SUCCEED) {
            doChangeAvator(result.data);
        } else if (result.code == Constant.HttpResult.FAILD) {
            ToastUtil.showToast(this, TextUtils.isEmpty(result.msg) ? "" : result.msg);
        } else if (result.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, result.msg);
            SettingUtils.setLoginState(this, false);
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    private void doChangeAvator(ArrayList<String> time) {
        if (time != null && time.size() > 0) {
            try {
                JSONObject json = new JSONObject();
                json.put("avatar", time.get(0));
                mPresenter.editUserInfo(json.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void showEditResult(HttpResult httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            ToastUtil.showToast(this, "资料修改成功");
            RxBus.getDefault().post(new UpDataUserInfoEvent());
        } else {
            ToastUtil.showToast(this, TextUtils.isEmpty(httpResult.msg) ? "" : httpResult.msg);
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
}
