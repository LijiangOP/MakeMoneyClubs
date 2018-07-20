package com.zhengdao.zqb.view.activity.homegoodsdetail;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Browser;
import android.provider.MediaStore;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.fynn.fluidlayout.FluidLayout;
import com.jaeger.library.OnSelectListener;
import com.jaeger.library.SelectableTextHelper;
import com.kennyc.view.MultiStateView;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.customview.BigImageDialog;
import com.zhengdao.zqb.customview.ReuseListView;
import com.zhengdao.zqb.customview.WantedHintDialog;
import com.zhengdao.zqb.customview.WantedShareDialog;
import com.zhengdao.zqb.entity.DictionaryEntity;
import com.zhengdao.zqb.entity.HomeWantedDetailEntity;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.LessTimeHttpResult;
import com.zhengdao.zqb.entity.RewardUserInfos;
import com.zhengdao.zqb.entity.ShowEntity;
import com.zhengdao.zqb.event.BackToHomeEvent;
import com.zhengdao.zqb.event.UpDataUserInfoEvent;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.DensityUtil;
import com.zhengdao.zqb.utils.FileUtils;
import com.zhengdao.zqb.utils.ImageUtils;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.utils.Utils;
import com.zhengdao.zqb.utils.ViewUtils;
import com.zhengdao.zqb.view.activity.customservice.CustomServiceActivity;
import com.zhengdao.zqb.view.activity.login.LoginActivity;
import com.zhengdao.zqb.view.activity.report.ReportActivity;
import com.zhengdao.zqb.view.adapter.AddUserInfoInputAdapter;
import com.zhengdao.zqb.view.adapter.HomeDetailAddPicAdapter;

import org.xml.sax.XMLReader;

import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.iwgang.countdownview.CountdownView;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * @Author lijiangop
 * @CreateTime 2018/1/18 15:02
 */
public class HomeGoodsDetailActivity extends MVPBaseActivity<HomeGoodsDetailContract.View, HomeGoodsDetailPresenter> implements HomeGoodsDetailContract.View, View.OnClickListener, AddUserInfoInputAdapter.ClickCallBack, HomeDetailAddPicAdapter.CallBack {

    private static final int ACTION_CHOOSE = 007;
    private static final int REQUEST_CODE  = 101;
    @BindView(R.id.iv_title_back)
    ImageView        mIvTitleBack;
    @BindView(R.id.tv_title_title)
    TextView         mTvTitleTitle;
    @BindView(R.id.iv_title_more)
    ImageView        mIvTitleMore;
    @BindView(R.id.re_title_bar)
    RelativeLayout   mReTitleBar;
    @BindView(R.id.iv_logo)
    ImageView        mIvLogo;
    @BindView(R.id.tv_name)
    TextView         mTvName;
    @BindView(R.id.tv_id)
    TextView         mTvId;
    @BindView(R.id.iv_phone)
    ImageView        mIvPhone;
    @BindView(R.id.iv_message)
    ImageView        mIvMessage;
    @BindView(R.id.iv_attention)
    ImageView        mIvAttention;
    @BindView(R.id.tv_price)
    TextView         mTvPrice;
    @BindView(R.id.tv_news)
    TextView         mTvNews;
    @BindView(R.id.tv_time)
    TextView         mTvTime;
    @BindView(R.id.tv_check_time)
    TextView         mTvCheckTime;
    @BindView(R.id.tv_wanted_title)
    TextView         mTvWantedTitle;
    @BindView(R.id.fluid_layout)
    FluidLayout      mFluidLayout;
    @BindView(R.id.tv_wanted_tag1)
    TextView         mTvWantedTag1;
    @BindView(R.id.tv_wanted_tag2)
    TextView         mTvWantedTag2;
    @BindView(R.id.tv_wanted_tag3)
    TextView         mTvWantedTag3;
    @BindView(R.id.tv_wanted_limit)
    TextView         mTvWantedLimit;
    @BindView(R.id.tv_task_link)
    TextView         mTvTaskLink;
    @BindView(R.id.re_task_link)
    RelativeLayout   mReTaskLink;
    @BindView(R.id.tv_wanted_flow)
    TextView         mTvWantedFlow;
    @BindView(R.id.tv_image_count)
    TextView         mTvImageCount;
    @BindView(R.id.tv_image_count_tag)
    TextView         mTvImageCountTag;
    @BindView(R.id.tv_image_count_total)
    TextView         mTvImageCountTotal;
    @BindView(R.id.recycle_view)
    RecyclerView     mRecycleView;
    @BindView(R.id.ll_flow_upload_pic)
    LinearLayout     mLlFlowUploadPic;
    @BindView(R.id.tv_addUserInfo_input)
    TextView         mTvAddUserInfoInput;
    @BindView(R.id.listView)
    ReuseListView    mListView;
    @BindView(R.id.ll_flow_input)
    LinearLayout     mLlFlowInput;
    @BindView(R.id.tv_statement)
    TextView         mTvStatement;
    @BindView(R.id.scrollView)
    NestedScrollView mScrollView;
    @BindView(R.id.tv_get)
    TextView         mTvGet;
    @BindView(R.id.countDownView)
    CountdownView    mCountDownView;
    @BindView(R.id.ll_bottom)
    LinearLayout     mLlBottom;
    @BindView(R.id.multiStateView)
    MultiStateView   mMultiStateView;

    private long mCurrentTimeMillis = 0;
    private WantedHintDialog  mWantedHintDialog;
    private WantedShareDialog mWantedShareDialog;
    private WantedHintDialog  mCheckTimeHintDialog;
    private PopupWindow       mPopupWindow;
    private int mGoodsId           = -1; //添加收藏
    private int mAddAttentionId    = -1; //添加关注
    private int mCancleAttentionId = -1; //取消关注
    private int mFlag;//关注的状态 0未关注，1已关注
    private int mState = -1;//悬赏领取状态 0未领取，1已领取
    private int    mCurrentPicPosition;//辅助记位置
    private String mPhone;
    private String mExplain;
    private HashMap mHashMap = new HashMap<>();
    private AddUserInfoInputAdapter mAddUserInfoInputAdapter;
    private ArrayList<ShowEntity>   mBitmaps;
    private ArrayList<String>       mImages;
    private HomeDetailAddPicAdapter mAddPicAdapter;
    private int                     mTaskId;
    private BigImageDialog          mBigImageDialog;
    private String                  mStringShare;
    private int mAlreadyAddCount = 0;
    private String  mType;
    private boolean isAlreadyAddDesc;
    private String mStringCompile = "http://app.zqb88.cn/upload/images/rewardImages/.+?\\.\\w{3}";
    private String mStringTaskLink;
    private Handler mhander = new Handler();

    //服务器返回字段
    private String mHttpGoodsPic;//商品图片地址
    private String mHttpCommitTime = "24";
    private int[] mScreenSize;

    //TextView自定义选择复制功能Helper
    private SelectableTextHelper mSelectableTextHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homegoodsdetail);
        ButterKnife.bind(this);
        init();
        initClickListener();
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
    }

    private void init() {
        mGoodsId = getIntent().getIntExtra(Constant.Activity.Data, -1);
        mState = getIntent().getIntExtra(Constant.Activity.Data1, -1);
        mType = getIntent().getStringExtra(Constant.Activity.Type);
        mMultiStateView.getView(MultiStateView.VIEW_STATE_ERROR).findViewById(R.id.btn_error_reload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initData();
            }
        });
        initData();
        mScreenSize = DensityUtil.getScreenSize(HomeGoodsDetailActivity.this);
    }

    private void initData() {
        isAlreadyAddDesc = false;
        mPresenter.getData(mGoodsId);
    }

    private void initClickListener() {
        mTvTitleTitle.setText("悬赏详情");
        mIvTitleBack.setOnClickListener(this);
        mIvTitleMore.setOnClickListener(this);
        mIvLogo.setOnClickListener(this);
        mIvPhone.setOnClickListener(this);
        mIvMessage.setOnClickListener(this);
        mIvAttention.setOnClickListener(this);
        mTvCheckTime.setOnClickListener(this);
        mTvTaskLink.setOnClickListener(this);
        mLlBottom.setOnClickListener(this);
        mCountDownView.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
            @Override
            public void onEnd(CountdownView cv) {
                mTvGet.setText("领取悬赏令");
                mCountDownView.setVisibility(View.GONE);
            }
        });
        mSelectableTextHelper = new SelectableTextHelper.Builder(mTvWantedFlow)
                .setSelectedColor(getResources().getColor(R.color.selected_blue))
                .setCursorHandleSizeInDp(20)
                .setCursorHandleColor(getResources().getColor(R.color.cursor_handle_color))
                .build();
        mSelectableTextHelper.setSelectListener(new OnSelectListener() {
            @Override
            public void onTextSelected(CharSequence content) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - mCurrentTimeMillis < 500)
            return;
        mCurrentTimeMillis = System.currentTimeMillis();
        switch (v.getId()) {
            case R.id.iv_title_back:
                HomeGoodsDetailActivity.this.finish();
                break;
            case R.id.iv_title_more:
                doShowMore();
                break;
            case R.id.iv_logo:
                doShowBigImage(mHttpGoodsPic);
                break;
            case R.id.iv_phone:
                doCallPhone();
                break;
            case R.id.iv_message:
                doSendMessage();
                break;
            case R.id.iv_attention:
                doAddAttention();
                break;
            case R.id.tv_check_time:
                showCheckTimeHintDialog();
                break;
            case R.id.ll_bottom:
                if (mState == 0)
                    mPresenter.getWanted(mGoodsId);//领取悬赏
                else if (mState == 1)
                    doCommitWanted();//提交悬赏
                break;
            case R.id.tv_home:
                if (mPopupWindow != null)
                    mPopupWindow.dismiss();
                doGoToHome();
                break;
            case R.id.tv_help:
                if (mPopupWindow != null)
                    mPopupWindow.dismiss();
                doGoToHelpCenter();
                break;
            case R.id.tv_report:
                if (mPopupWindow != null)
                    mPopupWindow.dismiss();
                doGoToReport();
                break;
            case R.id.tv_collect:
                if (mPopupWindow != null)
                    mPopupWindow.dismiss();
                mPresenter.doCollect(mGoodsId);
                break;
            case R.id.tv_share:
                if (mPopupWindow != null)
                    mPopupWindow.dismiss();
                doShare();
                break;
            case R.id.tv_task_link:
                goToLink();
                break;
        }
    }

    // -------------------------------------------------------------------提交悬赏模块---------------------------------------------------------------
    private void doCommitWanted() {
        //首先上传图片
        boolean isPicsAll = true;
        boolean ishasPic = false;
        if (mImages != null && mImages.size() > 0) {
            ishasPic = true;
            for (int i = 0; i < mImages.size(); i++) {
                if (TextUtils.isEmpty(mImages.get(i))) {
                    ToastUtil.showToast(this, "请上传规定数量的图片");
                    mhander.post(new Runnable() {
                        @Override
                        public void run() {
                            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });
                    isPicsAll = false;
                    break;
                }
            }
        }
        if (ishasPic) {
            if (isPicsAll) {
                Map<String, RequestBody> images = new HashMap<>();
                for (String filePath : mImages) {
                    if (!TextUtils.isEmpty(filePath)) {
                        File file = ImageUtils.compressImage(this, new File(filePath), System.currentTimeMillis() + ".jpg");
                        RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                        images.put("files\";filename=\"" + file.getName(), fileBody);
                    }
                }
                if (images.size() > 0) {
                    RequestBody type = RequestBody.create(MediaType.parse("multipart/form-data"), "" + Constant.Upload.Type_Task);
                    mPresenter.uploadImages(type, images);
                }
            }
        } else {
            doCommit(new ArrayList<String>());
        }
    }

    @Override
    public void onImgUploadError() {
        ToastUtil.showToast(this, "图片上传失败!");
    }

    @Override
    public void onImgUploadResult(HttpResult<ArrayList<String>> result) {
        if (result.code == Constant.HttpResult.SUCCEED) {
            if (result.data != null)
                doCommit(result.data);
        } else {
            ToastUtil.showToast(this, "图片上传失败!");
        }
    }

    private void doCommit(ArrayList<String> data) {
        ArrayList<String> jsons = new ArrayList<>();
        if (mAddUserInfoInputAdapter != null) {
            for (int i = 0; i < mAddUserInfoInputAdapter.getCount(); i++) {
                EditText editText = mListView.getChildAt(i).findViewById(R.id.et_add_user_info);
                String trim = editText.getText().toString().trim();
                if (TextUtils.isEmpty(trim)) {
                    ToastUtil.showToast(HomeGoodsDetailActivity.this, "请输入要提交的信息");
                    break;
                }
                RewardUserInfos item = (RewardUserInfos) mAddUserInfoInputAdapter.getItem(i);
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("ruId", item.ruId);
                    jsonObject.put("content", trim);
                    jsons.add(jsonObject.toString());
                } catch (Exception ex) {
                    LogUtils.i(ex.getMessage());
                }
            }
        }
        if (mAddUserInfoInputAdapter == null || jsons.size() == mAddUserInfoInputAdapter.getCount())
            mPresenter.CommitWanted(data, jsons, mTaskId);
    }

    @Override
    public void onCommitWantedFinished(HttpResult httpResult) {
        hideProgress();
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            ToastUtil.showToast(HomeGoodsDetailActivity.this, TextUtils.isEmpty(httpResult.msg) ? "提交成功" : httpResult.msg);
            this.finish();
            if (!TextUtils.isEmpty(mType) && mType.equals("commit")) {
                RxBus.getDefault().post(new UpDataUserInfoEvent());
            }
        } else {
            ToastUtil.showToast(HomeGoodsDetailActivity.this, TextUtils.isEmpty(httpResult.msg) ? "提交失败" : httpResult.msg);
        }
    }

    //----------------------------------------------------------------其它模块---------------------------------------------------------------------
    private void showCheckTimeHintDialog() {
        mCheckTimeHintDialog = new WantedHintDialog(this);
        mCheckTimeHintDialog.initView(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCheckTimeHintDialog != null)
                    mCheckTimeHintDialog.dismiss();
            }
        });
        mCheckTimeHintDialog.setTitle(getString(R.string.checkTime_hint_title));
        mCheckTimeHintDialog.setMessage(getString(R.string.checkTime_hint_content));
        mCheckTimeHintDialog.show();
    }

    private void doShowMore() {
        try {
            if (SettingUtils.isLogin(this)) {
                View contentView = LayoutInflater.from(this).inflate(R.layout.popup_wanted_detail_more, null);
                TextView ll_home = contentView.findViewById(R.id.tv_home);
                TextView ll_help = contentView.findViewById(R.id.tv_help);
                TextView ll_report = contentView.findViewById(R.id.tv_report);
                TextView ll_collect = contentView.findViewById(R.id.tv_collect);
                TextView ll_share = contentView.findViewById(R.id.tv_share);
                ll_home.setOnClickListener(this);
                ll_help.setOnClickListener(this);
                ll_report.setOnClickListener(this);
                ll_collect.setOnClickListener(this);
                ll_share.setOnClickListener(this);
                mPopupWindow = new PopupWindow(contentView,
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                mPopupWindow.setBackgroundDrawable(new ColorDrawable());
                if (Build.VERSION.SDK_INT > 18)
                    mPopupWindow.showAsDropDown(mReTitleBar, 0, 0, Gravity.RIGHT);
                else
                    mPopupWindow.showAsDropDown(mReTitleBar, 0, 0);

            } else {
                startActivityForResult(new Intent(this, LoginActivity.class), REQUEST_CODE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doShowBigImage(String url) {
        if (!TextUtils.isEmpty(url)) {
            if (mBigImageDialog == null)
                mBigImageDialog = new BigImageDialog(HomeGoodsDetailActivity.this);
            mBigImageDialog.display(url);
            mBigImageDialog.show();
        }
    }

    private void doCallPhone() {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mPhone));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception ex) {
            LogUtils.e(ex.getMessage());
        }
    }

    private void doSendMessage() {
        try {
            Uri smsToUri = Uri.parse("smsto:" + mPhone);
            Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
            intent.putExtra("sms_body", "");
            startActivity(intent);
        } catch (Exception ex) {
            LogUtils.e(ex.getMessage());
        }
    }

    private void doAddAttention() {
        if (mFlag == 0) {
            mPresenter.AddAttention(mAddAttentionId);
        } else
            mPresenter.CancleAttention(mCancleAttentionId);
    }

    private void doGoToHome() {
        HomeGoodsDetailActivity.this.finish();
        RxBus.getDefault().post(new BackToHomeEvent());
    }

    private void doGoToHelpCenter() {
        Utils.StartActivity(HomeGoodsDetailActivity.this, new Intent(HomeGoodsDetailActivity.this, CustomServiceActivity.class));
    }

    private void doGoToReport() {
        Intent intent = new Intent(HomeGoodsDetailActivity.this, ReportActivity.class);
        intent.putExtra(Constant.Activity.Data, mAddAttentionId);
        Utils.StartActivity(HomeGoodsDetailActivity.this, intent);
    }

    private void doShare() {
        if (!TextUtils.isEmpty(mStringShare)) {
            mWantedShareDialog = new WantedShareDialog(this);
            mWantedShareDialog.initView(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mWantedShareDialog != null)
                        mWantedShareDialog.dismiss();
                    ToastUtil.showToast(HomeGoodsDetailActivity.this, "复制成功!");
                }
            });
            SpannableString spannableString = new SpannableString(mStringShare);
            mWantedShareDialog.setMessage(spannableString);
            mWantedShareDialog.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ToastUtil.showToast(HomeGoodsDetailActivity.this, "复制成功,快分享给好友吧!");
                    return true;
                }
            });
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData mClipData = ClipData.newPlainText("Label", spannableString);
            cm.setPrimaryClip(mClipData);
            mWantedShareDialog.show();
        }
    }

    private void goToLink() {
        try {
            Uri uri = Uri.parse(mStringTaskLink);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.putExtra(Browser.EXTRA_APPLICATION_ID, getPackageName());
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void noData() {
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
    }

    @Override
    public void showErrorMessage(String msg) {
        super.showErrorMessage(msg);
        hideProgress();
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
    }

    //----------------------------------------------------------------显示界面数据模块---------------------------------------------------------------
    @Override
    public void onGetDataFinished(HomeWantedDetailEntity httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            HomeWantedDetailEntity.Reward reward = httpResult.reward;
            mStringShare = httpResult.share;
            mState = httpResult.state;
            mFlag = httpResult.flag;
            if (mState == 1)
                onGetLessTimeResult(httpResult.time);
            else if (mState == 2)
                mTvGet.setText("该悬赏已提交");
            if (mFlag == 1)
                mIvAttention.setImageDrawable(getResources().getDrawable(R.drawable.btn_monet_co));
            else
                mIvAttention.setImageDrawable(getResources().getDrawable(R.drawable.btn_monet_c));
            if (reward != null) {
                mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
                mPresenter.getCheckTime(reward.mode);
                try {
                    mPhone = TextUtils.isEmpty(reward.user.phone) ? "" : reward.user.phone;
                    mTaskId = reward.taskId;
                    mAddAttentionId = reward.userId;
                    mCancleAttentionId = httpResult.fid;
                    mHttpGoodsPic = reward.picture;
                    Glide.with(HomeGoodsDetailActivity.this).load(reward.picture).error(R.drawable.net_less_140).into(mIvLogo);
                    mTvName.setText(TextUtils.isEmpty(reward.user.nickName) ? "" : reward.user.nickName);
                    mTvId.setText("ID:" + reward.id);
                    if (reward.money != null) {
                        SpannableString spannableString;
                        if (reward.block == 71) {
                            String format = new DecimalFormat("#0.00").format(reward.money);
                            spannableString = new SpannableString("最高¥" + format);
                            spannableString.setSpan(new RelativeSizeSpan(2), 3, format.length() + 3, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        } else {
                            String format = new DecimalFormat("#0.00").format(reward.money);
                            spannableString = new SpannableString("¥" + format);
                            spannableString.setSpan(new RelativeSizeSpan(2), 1, format.length() + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        }
                        mTvPrice.setText(spannableString);
                    }
                    mTvWantedTitle.setText(TextUtils.isEmpty(reward.title) ? "" : reward.title);
                    if (!isAlreadyAddDesc)
                        doShowDesc(reward);
                    if (!TextUtils.isEmpty(reward.keyword)) {
                        String[] split = reward.keyword.split(",");
                        switch (split.length) {
                            case 1:
                                mTvWantedTag1.setVisibility(View.VISIBLE);
                                mTvWantedTag1.setText(split[0]);
                                break;
                            case 2:
                                mTvWantedTag1.setVisibility(View.VISIBLE);
                                mTvWantedTag1.setText(split[0]);
                                mTvWantedTag2.setVisibility(View.VISIBLE);
                                mTvWantedTag2.setText(split[1]);
                                break;
                            case 3:
                                mTvWantedTag1.setVisibility(View.VISIBLE);
                                mTvWantedTag1.setText(split[0]);
                                mTvWantedTag2.setVisibility(View.VISIBLE);
                                mTvWantedTag2.setText(split[1]);
                                mTvWantedTag3.setVisibility(View.VISIBLE);
                                mTvWantedTag3.setText(split[2]);
                                break;
                        }
                    }
                    if (TextUtils.isEmpty(reward.condition)) {
                        mTvWantedLimit.setVisibility(View.GONE);
                    } else {
                        mTvWantedLimit.setVisibility(View.VISIBLE);
                        mTvWantedLimit.setText(Html.fromHtml(reward.condition));
                    }
                    if (reward.type == 1) {
                        mTvNews.setVisibility(View.VISIBLE);
                    } else {
                        mTvNews.setVisibility(View.GONE);
                    }
                    doShowFlow(reward);//显示流程
                    mStringTaskLink = reward.content;
                    if (TextUtils.isEmpty(reward.content)) {
                        mReTaskLink.setVisibility(View.GONE);
                    } else {
                        mReTaskLink.setVisibility(View.VISIBLE);
                    }
                } catch (Exception ex) {
                    LogUtils.e(ex.getMessage());
                }
            } else {
                mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
            }
        } else if (httpResult.code == Constant.HttpResult.FAILD && !TextUtils.isEmpty(httpResult.msg) && httpResult.msg.equals("该悬赏已经下架!")) {
            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
            ToastUtil.showToast(HomeGoodsDetailActivity.this, TextUtils.isEmpty(httpResult.msg) ? "" : httpResult.msg);
        } else if (httpResult.code == Constant.HttpResult.FAILD && !TextUtils.isEmpty(httpResult.msg) && httpResult.msg.equals("悬赏已经结束了!")) {
            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
            ToastUtil.showToast(HomeGoodsDetailActivity.this, TextUtils.isEmpty(httpResult.msg) ? "" : httpResult.msg);
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(HomeGoodsDetailActivity.this, TextUtils.isEmpty(httpResult.msg) ? "" : httpResult.msg);
            startActivityForResult(new Intent(this, LoginActivity.class), REQUEST_CODE);
        } else {
            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
            ToastUtil.showToast(HomeGoodsDetailActivity.this, TextUtils.isEmpty(httpResult.msg) ? "" : httpResult.msg);
        }
    }

    /**
     * 商品标题下的三个小模块
     *
     * @param reward
     */
    private void doShowDesc(HomeWantedDetailEntity.Reward reward) {
        if (reward != null) {
            try {
                mFluidLayout.removeAllViews();
                FluidLayout.LayoutParams params = new FluidLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 0, 15, 0);
                TextView tv1 = new TextView(this);
                tv1.setText("限" + reward.number + "人领取");
                tv1.setTextSize(10);
                Drawable drawable1 = getResources().getDrawable(R.drawable.img_c);
                drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
                tv1.setCompoundDrawables(drawable1, null, null, null);
                tv1.setCompoundDrawablePadding(2);
                tv1.setTextColor(getResources().getColor(R.color.color_000000));
                mFluidLayout.addView(tv1, params);

                TextView tv2 = new TextView(this);
                tv2.setText("剩" + (reward.number - reward.joincount) + "个名额");
                tv2.setTextSize(10);
                Drawable drawable2 = getResources().getDrawable(R.drawable.img_t);
                drawable2.setBounds(0, 0, drawable2.getMinimumWidth(), drawable2.getMinimumHeight());
                tv2.setCompoundDrawables(drawable2, null, null, null);
                tv2.setCompoundDrawablePadding(2);
                tv2.setTextColor(getResources().getColor(R.color.color_000000));
                mFluidLayout.addView(tv2, params);

                TextView tv3 = new TextView(this);
                tv3.setText("赏金已托管");
                tv3.setTextSize(10);
                Drawable drawable3 = getResources().getDrawable(R.drawable.img_b);
                drawable3.setBounds(0, 0, drawable3.getMinimumWidth(), drawable3.getMinimumHeight());
                tv3.setCompoundDrawables(drawable3, null, null, null);
                tv3.setCompoundDrawablePadding(2);
                tv3.setTextColor(getResources().getColor(R.color.color_000000));
                mFluidLayout.addView(tv3, params);
                isAlreadyAddDesc = true;
            } catch (Exception ex) {
                LogUtils.e(ex.getMessage());
            }
        }
    }

    /**
     * 流程介绍 mState=0,只显示流程；mState=1额外显示添加截图和输入信息模块
     *
     * @param reward
     */
    private void doShowFlow(HomeWantedDetailEntity.Reward reward) {
        mExplain = reward.explain;
        if (!TextUtils.isEmpty(mExplain)) {
            if (doCompareTime(reward))
                showNativeText(reward);
            else
                showHtmlText(mExplain);
            mTvWantedFlow.setVisibility(View.VISIBLE);
        } else {
            mTvWantedFlow.setVisibility(View.GONE);
        }
        doShowUploadPic(reward);
        doShowUserInputInfo(reward);
        if (mState == 0 || mState == 2) //如果未领取,已提交，不显示图片上传，信息输入模块
            mLlFlowInput.setVisibility(View.GONE);
        else
            mLlFlowInput.setVisibility(View.VISIBLE);
    }

    private boolean doCompareTime(HomeWantedDetailEntity.Reward reward) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");//年-月-日 时-分
        try {
            String createTime = reward.upFrameTime;
            if (!TextUtils.isEmpty(createTime)) {
                Date compareDate = dateFormat.parse(createTime);//比较时间
                Date standardDate = dateFormat.parse("2018-06-25 00:00:00");//标准时间
                if (compareDate.getTime() < standardDate.getTime())
                    return true;
                else
                    return false;
            } else {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 流程显示：非html类型数据
     */
    private void showNativeText(HomeWantedDetailEntity.Reward reward) {
        String picture = reward.picture;
        if (!TextUtils.isEmpty(picture)) {
            int i = picture.lastIndexOf("/");
            String substring = picture.substring(0, i + 1);
            mStringCompile = substring + ".+?\\.\\w{3}";
        }
        //正则匹配图片
        Pattern pattern = Pattern.compile(mStringCompile);
        Matcher matcher = pattern.matcher(mExplain);
        while (matcher.find()) {
            final String group = matcher.group();
            Glide.with(this).load(group).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    mHashMap.put(group, resource);
                    try {
                        onImgDownloadFinish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        mTvWantedFlow.setText(mExplain);
    }

    //图片下载回调
    private void onImgDownloadFinish() throws Exception {
        try {
            if (!TextUtils.isEmpty(mExplain)) {
                SpannableString mSpannableString = new SpannableString(mExplain);
                Pattern p = Pattern.compile(mStringCompile);
                final Matcher m = p.matcher(mExplain);
                while (m.find()) {
                    if (mHashMap.containsKey(m.group())) {
                        int[] imageScale = ImageUtils.getImageScale((Bitmap) mHashMap.get(m.group()));
                        int mShowImgWidth = mScreenSize[0] - DensityUtil.dip2px(HomeGoodsDetailActivity.this, 23);
                        int mShowImgHeight = mShowImgWidth * imageScale[1] / imageScale[0];
                        Bitmap bitmap = ImageUtils.zoomImg((Bitmap) mHashMap.get(m.group()), mShowImgWidth, mShowImgHeight);
                        MyClickableSpan myClickableSpan = new MyClickableSpan(m.group());
                        ImageSpan span = new ImageSpan(HomeGoodsDetailActivity.this, bitmap);
                        mSpannableString.setSpan(span, m.start() < 0 ? 0 : m.start(), m.end(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                        mSpannableString.setSpan(myClickableSpan, m.start() < 0 ? 0 : m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
                mTvWantedFlow.setMovementMethod(LinkMovementMethod.getInstance());
                mTvWantedFlow.setText(mSpannableString);
                mExplain = mSpannableString.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 流程显示：html类型数据
     */
    public void showHtmlText(final String htmlContent) {
        URLImageParser imageGetter = new URLImageParser(mTvWantedFlow);
        mTvWantedFlow.setText(Html.fromHtml(htmlContent, imageGetter, new DetailTagHandler(HomeGoodsDetailActivity.this)));
        mTvWantedFlow.setClickable(true);
        mTvWantedFlow.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * 添加图片模块
     *
     * @param reward
     */
    private void doShowUploadPic(HomeWantedDetailEntity.Reward reward) {
        if (reward.rewardPics != null && reward.rewardPics.size() > 0) {
            mLlFlowUploadPic.setVisibility(View.VISIBLE);
            if (mBitmaps == null)
                mBitmaps = new ArrayList<>();
            mBitmaps.clear();
            if (mImages == null)
                mImages = new ArrayList<>();
            mImages.clear();
            for (int i = 0; i < reward.rewardPics.size(); i++) {
                mBitmaps.add(new ShowEntity(reward.rewardPics.get(i).picture));
                mImages.add("");
            }
            if (mAddPicAdapter == null) {
                mAddPicAdapter = new HomeDetailAddPicAdapter(this, R.layout.home_detail_addpic_layout, mBitmaps, this);
                mRecycleView.addItemDecoration(new SpacesItemDecoration(5));
                mRecycleView.setAdapter(mAddPicAdapter);
                mRecycleView.setLayoutManager(new GridLayoutManager(this, 2));
            } else
                mAddPicAdapter.notifyDataSetChanged();
            mTvImageCount.setText("0");
            mTvImageCountTag.setText("/");
            mTvImageCountTotal.setText("" + reward.rewardPics.size());
        } else {
            mLlFlowUploadPic.setVisibility(View.GONE);
        }
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
        if (mBitmaps != null && mBitmaps.size() > position) {
            ShowEntity showEntity = mBitmaps.get(position);
            showEntity.setPic(null);
            mBitmaps.set(position, showEntity);
        }
        if (mImages != null)
            mImages.set(position, "");
        if (mAddPicAdapter != null)
            mAddPicAdapter.notifyDataSetChanged();
        mAlreadyAddCount--;
        mTvImageCount.setText("" + mAlreadyAddCount);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ACTION_CHOOSE:
                if (resultCode == RESULT_OK) {
                    if (data != null)
                        try {
                            String path = FileUtils.getImageAbsolutePath(this, data.getData());
                            if (new File(path).exists() && !ImageUtils.isGifFile(new File(path))) {
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inSampleSize = 2;
                                Bitmap bitmap = BitmapFactory.decodeFile(path, options);
                                if (null == bitmap) {
                                    ToastUtil.showToast(this, "无效图片");
                                    return;
                                }
                                if (mBitmaps != null && mBitmaps.size() > mCurrentPicPosition) {
                                    ShowEntity showEntity = mBitmaps.get(mCurrentPicPosition);
                                    showEntity.setPic(bitmap);
                                    mBitmaps.set(mCurrentPicPosition, showEntity);
                                    if (mImages != null)
                                        mImages.set(mCurrentPicPosition, path);
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
                                ToastUtil.showToast(this, "图片文件不存在");
                            }
                        } catch (Exception ex) {
                            LogUtils.e(ex.getMessage());
                        }
                }
                break;
            case REQUEST_CODE:
                if (data != null) {
                    boolean booleanExtra = data.getBooleanExtra(Constant.Activity.Data, false);
                    if (booleanExtra)
                        initData();
                }
                break;
        }
    }

    /**
     * 用户输入信息模块
     *
     * @param reward
     */
    private void doShowUserInputInfo(HomeWantedDetailEntity.Reward reward) {
        if (reward != null && reward.rewardUserInfos != null && reward.rewardUserInfos.size() > 0) {
            if (mAddUserInfoInputAdapter == null) {
                mAddUserInfoInputAdapter = new AddUserInfoInputAdapter(HomeGoodsDetailActivity.this, reward.rewardUserInfos, HomeGoodsDetailActivity.this);
                mListView.setAdapter(mAddUserInfoInputAdapter);
            } else
                mAddUserInfoInputAdapter.notifyDataSetChanged();
            mListView.setVisibility(View.VISIBLE);
            mTvAddUserInfoInput.setVisibility(View.VISIBLE);
        } else {
            mListView.setVisibility(View.GONE);
            mTvAddUserInfoInput.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDelete(int position) {
        if (mAddUserInfoInputAdapter != null)
            mAddUserInfoInputAdapter.notifyDataSetChanged();
        EditText editText = mListView.getChildAt(position).findViewById(R.id.et_add_user_info);
        editText.setText("");
    }


    // -----------------------------------------------------------------其它功能的回调模块------------------------------------------------------------
    @Override
    public void onGetCheckTimeFinish(DictionaryEntity httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            if (!TextUtils.isEmpty(httpResult.value)) {
                SpannableString spannableString;
                if (isNumeric(httpResult.value)) {
                    spannableString = new SpannableString(httpResult.value + "小时");
                } else {
                    spannableString = new SpannableString(httpResult.value);
                }
                spannableString.setSpan(new RelativeSizeSpan(2), 0, httpResult.value.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                mTvTime.setText(spannableString);
            }
        }
    }

    public boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    @Override
    public void onAddAttentionFinished(HttpResult httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            ToastUtil.showToast(HomeGoodsDetailActivity.this, "添加关注成功");
            mFlag = 1;
            mIvAttention.setImageDrawable(getResources().getDrawable(R.drawable.btn_monet_co));
        } else {
            ToastUtil.showToast(HomeGoodsDetailActivity.this, TextUtils.isEmpty(httpResult.msg) ? "" : httpResult.msg);
        }
    }

    @Override
    public void onCancleAttentionFinished(HttpResult httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            ToastUtil.showToast(HomeGoodsDetailActivity.this, "取消关注成功");
            mFlag = 0;
            mIvAttention.setImageDrawable(getResources().getDrawable(R.drawable.btn_monet_c));
        } else {
            ToastUtil.showToast(HomeGoodsDetailActivity.this, TextUtils.isEmpty(httpResult.msg) ? "" : httpResult.msg);
        }
    }

    @Override
    public void onAddCollectFinished(HttpResult httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            ToastUtil.showToast(HomeGoodsDetailActivity.this, "添加收藏成功");
        } else {
            ToastUtil.showToast(HomeGoodsDetailActivity.this, TextUtils.isEmpty(httpResult.msg) ? "" : httpResult.msg);
        }
    }

    @Override
    public void onGetWantedFinished(LessTimeHttpResult httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            initData();
            mState = 1;
            mLlFlowInput.setVisibility(View.VISIBLE);
            showGetWantedDialog(httpResult);
            onGetLessTimeResult(new Long(httpResult.time));
            mhander.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            }, 500);
        } else {
            ToastUtil.showToast(HomeGoodsDetailActivity.this, TextUtils.isEmpty(httpResult.msg) ? "" : httpResult.msg);
        }
    }

    private void onGetLessTimeResult(Long aLong) {
        mTvGet.setText("提交悬赏（剩余 ");
        mCountDownView.setVisibility(View.VISIBLE);
        mCountDownView.start(aLong);
    }

    private void showGetWantedDialog(LessTimeHttpResult httpResult) {
        mWantedHintDialog = new WantedHintDialog(this);
        mWantedHintDialog.initView(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mWantedHintDialog != null)
                    mWantedHintDialog.dismiss();
            }
        });
        Long time = httpResult.time;
        try {
            long l = time / (1000 * 60 * 60);
            int value = new Long(l).intValue();
            if (value != 0)
                mHttpCommitTime = "" + value;
        } catch (Exception e) {
            e.printStackTrace();
        }
        SpannableString spannableString = new SpannableString("请在领取悬赏令" + mHttpCommitTime + "小时内完成悬赏任务，超时未完成将自动取消您的本次任务，如需再次完成任务需重新领取悬赏令，领取的任务可到我的进行查看。");
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#fc3135")), 7, 9, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        mWantedHintDialog.setMessage(spannableString);
        mWantedHintDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWantedHintDialog != null) {
            mWantedHintDialog.dismiss();
            mWantedHintDialog = null;
        }
        if (mCheckTimeHintDialog != null) {
            mCheckTimeHintDialog.dismiss();
            mCheckTimeHintDialog = null;
        }
        if (mWantedShareDialog != null) {
            mWantedShareDialog.dismiss();
            mWantedShareDialog = null;
        }
        if (mBigImageDialog != null) {
            mBigImageDialog.dismiss();
            mBigImageDialog = null;
        }
        if (mHashMap != null)
            mHashMap.clear();
        if (mBitmaps != null)
            mBitmaps.clear();
        if (mPopupWindow != null)
            mPopupWindow.dismiss();
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

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SpacesItemDecoration(int space) {
            this.space = ViewUtils.dip2px(HomeGoodsDetailActivity.this, space);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.bottom = 30;
        }

    }

    public class URLImageParser implements Html.ImageGetter {
        TextView mTextView;

        public URLImageParser(TextView textView) {
            this.mTextView = textView;
        }

        @Override
        public Drawable getDrawable(String source) {
            final UrlDrawable urlDrawable = new UrlDrawable();

            Glide.with(HomeGoodsDetailActivity.this).load(source).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    int[] imageScale = ImageUtils.getImageScale(resource);
                    int mShowImgWidth = mScreenSize[0] - DensityUtil.dip2px(HomeGoodsDetailActivity.this, 23);
                    int mShowImgHeight = mShowImgWidth * imageScale[1] / imageScale[0];
                    Drawable drawable = new BitmapDrawable(resource);
                    drawable.setBounds(0, 0, mShowImgWidth, mShowImgHeight);
                    urlDrawable.setBounds(0, 0, mShowImgWidth, mShowImgHeight);
                    urlDrawable.setDrawable(drawable);
                    mTextView.invalidate();
                    mTextView.setText(mTextView.getText());
                }
            });
            return urlDrawable;
        }

        public class UrlDrawable extends BitmapDrawable {
            private Drawable drawable;

            @Override
            public void draw(Canvas canvas) {
                if (drawable != null)
                    drawable.draw(canvas);
            }

            public void setDrawable(Drawable drawable) {
                this.drawable = drawable;
            }
        }
    }

    public class DetailTagHandler implements Html.TagHandler {
        private Context           context;
        private ArrayList<String> strings;

        public DetailTagHandler(Context context) {
            this.context = context;
            strings = new ArrayList<>();
        }

        @Override
        public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
            try {
                // 处理标签<img>
                if ("img".equals(tag.toLowerCase(Locale.getDefault()))) {
                    // 获取长度
                    int len = output.length();
                    // 获取图片地址
                    ImageSpan[] images = output.getSpans(len - 1, len, ImageSpan.class);
                    String imgURL = images[0].getSource();
                    // 记录所有图片地址
                    strings.add(imgURL);
                    // 记录是第几张图片
                    int position = strings.size() - 1;
                    // 使图片可点击并监听点击事件
                    output.setSpan(new ClickableImage(context, position), (len - 1) < 0 ? 0 : (len - 1), len,
                            Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private class ClickableImage extends ClickableSpan {
            private Context context;
            private int     position;

            public ClickableImage(Context context, int position) {
                this.context = context;
                this.position = position;
            }

            @Override
            public void onClick(View widget) {
                doShowBigImage(strings.get(position));
            }
        }
    }
}
