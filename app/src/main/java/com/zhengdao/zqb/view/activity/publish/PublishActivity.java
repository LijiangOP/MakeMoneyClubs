package com.zhengdao.zqb.view.activity.publish;


import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bigkoo.pickerview.TimePickerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.yalantis.ucrop.UCrop;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.DictionaryHttpEntity;
import com.zhengdao.zqb.entity.DictionaryValue;
import com.zhengdao.zqb.entity.HomeWantedDetailEntity;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.RewardPics;
import com.zhengdao.zqb.entity.RewardUserInfos;
import com.zhengdao.zqb.event.WantedPublishEvent;
import com.zhengdao.zqb.event.WantedSaveEvent;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.DensityUtil;
import com.zhengdao.zqb.utils.FileUtils;
import com.zhengdao.zqb.utils.ImageUtils;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.MyDateUtils;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.view.activity.InfoEditActivity;
import com.zhengdao.zqb.view.activity.StatementActivity;
import com.zhengdao.zqb.view.activity.TextEditActivity;
import com.zhengdao.zqb.view.activity.keywords.KeyWordsActivity;
import com.zhengdao.zqb.view.activity.login.LoginActivity;
import com.zhengdao.zqb.view.activity.publishconfirm.PublishConfirmActivity;
import com.zhengdao.zqb.view.adapter.PublishCategroyAdapter;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.zhengdao.zqb.config.Constant.Assist.mCommonPicPath;
import static com.zhengdao.zqb.config.Constant.EditableList;
import static com.zhengdao.zqb.config.Constant.Wanted.DEFAULT_BRAND_NEW_TYPE;

public class PublishActivity extends MVPBaseActivity<PublishContract.View, PublishPresenter> implements PublishContract.View, View.OnClickListener {

    private static final int ACTION_CHOOSE_GOODS_PIC = 001;
    private static final int ACTION_CHOOSE_TITLE     = 002;
    private static final int ACTION_CHOOSE_KEY_WORD  = 003;
    private static final int ACTION_CHOOSE_CONDITION = 004;
    private static final int ACTION_CHOOSE_FLOW      = 005;
    private static final int ACTION_CHOOSE_USERINFO  = 006;
    private static final int ACTION_CHOOSE_PICINFO   = 007;
    private static final int ACTION_GO_TO_LOGIN      = 101;
    private static final int RC_CAMERA_AND_LOCATION  = 123;

    @BindView(R.id.iv_title_bar_back)
    ImageView    mIvTitleBarBack;
    @BindView(R.id.tv_title_bar_title)
    TextView     mTvTitleBarTitle;
    @BindView(R.id.tv_title_bar_right)
    TextView     mTvTitleBarRight;
    @BindView(R.id.scrollView)
    ScrollView   mScrollView;
    @BindView(R.id.iv_wanted_goods_pic)
    ImageView    mIvWantedGoodsPic;
    @BindView(R.id.tv_wanted_goods_pic)
    TextView     mTvWantedGoodsPic;
    @BindView(R.id.fl_wanted_goods_pic)
    FrameLayout  mFlWantedGoodsPic;
    @BindView(R.id.tv_wanted_title)
    TextView     mTvWantedTitle;
    @BindView(R.id.ll_wanted_title)
    LinearLayout mLlWantedTitle;
    @BindView(R.id.et_wanted_price)
    EditText     mEtWantedPrice;
    @BindView(R.id.iv_number_sub)
    ImageView    mIvNumberSub;
    @BindView(R.id.et_numbers)
    EditText     mEtNumbers;
    @BindView(R.id.iv_number_add)
    ImageView    mIvNumberAdd;
    @BindView(R.id.tv_wanted_checkTime)
    TextView     mTvWantedCheckTime;
    @BindView(R.id.ll_wanted_checkTime)
    LinearLayout mLlWantedCheckTime;
    @BindView(R.id.tv_wanted_business_category)
    TextView     mTvWantedBusinessCategory;
    @BindView(R.id.ll_wanted_business_category)
    LinearLayout mLlWantedBusinessCategory;
    @BindView(R.id.tv_wanted_reward_category)
    TextView     mTvWantedRewardCategory;
    @BindView(R.id.ll_wanted_reward_category)
    LinearLayout mLlWantedRewardCategory;
    @BindView(R.id.et_wanted_ticket)
    EditText     mEtWantedTicket;
    @BindView(R.id.ll_wanted_ticket)
    LinearLayout mLlWantedTicket;
    @BindView(R.id.tv_wanted_limited_time)
    TextView     mTvWantedLimitedTime;
    @BindView(R.id.ll_wanted_limited_time)
    LinearLayout mLlWantedLimitedTime;
    @BindView(R.id.tv_wanted_key_word)
    TextView     mTvWantedKeyWord;
    @BindView(R.id.ll_wanted_key_word)
    LinearLayout mLlWantedKeyWord;
    @BindView(R.id.tv_wanted_get_condition)
    TextView     mTvWantedGetCondition;
    @BindView(R.id.ll_wanted_get_condition)
    LinearLayout mLlWantedGetCondition;
    @BindView(R.id.tv_wanted_flow_explain)
    TextView     mTvWantedFlowExplain;
    @BindView(R.id.ll_wanted_flow_explain)
    LinearLayout mLlWantedFlowExplain;
    @BindView(R.id.et_wanted_task_link)
    EditText     mEtWantedTaskLink;
    @BindView(R.id.ll_wanted_task_link)
    LinearLayout mLlWantedTaskLink;
    @BindView(R.id.tv_wanted_commit_info)
    TextView     mTvWantedCommitInfo;
    @BindView(R.id.ll_wanted_commit_user_info)
    LinearLayout mLlWantedCommitUserInfo;
    @BindView(R.id.tv_wanted_commit_pic)
    TextView     mTvWantedCommitPic;
    @BindView(R.id.ll_wanted_commit_pic_info)
    LinearLayout mLlWantedCommitPicInfo;
    @BindView(R.id.cb_state)
    CheckBox     mCbState;
    @BindView(R.id.tv_statement)
    TextView     mTvStatement;
    @BindView(R.id.ll_bottom)
    LinearLayout mLlBottom;
    @BindView(R.id.tv_publish)
    TextView     mTvPublish;

    private PopupWindow            mPopupWindow;
    private PublishCategroyAdapter mTimeAdapter;
    private PublishCategroyAdapter mBusinessAdapter;
    private PublishCategroyAdapter mCategroyAdapter;
    //界面传递数据
    private int                    mActivitySkipType;
    private int                    mActivityWantedId;
    private int                    mActivityPayState;
    //成员变量
    private Disposable             mDisposable;
    private long mCurrentTimeMillis = 0;
    private int  mMinNumber         = 1;//加减运算模块最小值
    private int  mMaxNumber         = 999999999;//加减运算模块最大值
    private int                        mFieldsFlHeight;//FrameLayout的高度
    private int                        mRewardId;//奖励类型ID
    private int                        mBusinessId;//业务类型ID
    private int                        mCheckTimeId;//审核时间ID
    private File                       mFieldsGoodsPicFile;//商品图片文件(压缩后)
    private ArrayList<String>          mFieldsUserInfoData;//用户提交信息集合
    private ArrayList<String>          mFieldsPicInfoData;//提交图片信息集合
    private Date                       mFieldsLimitedTime;//下架时间
    //网络数据
    private ArrayList<DictionaryValue> mHttpRewardData;//奖励类型数据
    private ArrayList<DictionaryValue> mHttpBusinessData;//业务类型数据
    private ArrayList<DictionaryValue> mHttpCheckTimeData;//审核时间数据
    private String                     mHttpGoodsPic;
    private String                     mHttpComparePicPath;
    //上传辅助状态
    private boolean                    isUploadIconFile;
    private boolean                    isUploadFlowPicFile;
    private boolean                    mUploadingIconFile;
    private boolean                    mUploadingFlowPicFile;
    private String                     mUploadString;
    private String                     mUploadStringFlow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        setSwipeBackEnable(false);
        ButterKnife.bind(this);
        initClickListener();
        init();
        initData();
        doViewBack();//悬赏管理的条目回显入口
    }

    private void initClickListener() {
        mIvTitleBarBack.setOnClickListener(this);
        mTvTitleBarRight.setOnClickListener(this);
        mTvWantedGoodsPic.setOnClickListener(this);
        mIvWantedGoodsPic.setOnClickListener(this);
        mLlWantedTitle.setOnClickListener(this);
        mIvNumberAdd.setOnClickListener(this);
        mIvNumberSub.setOnClickListener(this);
        mLlWantedCheckTime.setOnClickListener(this);
        mLlWantedBusinessCategory.setOnClickListener(this);
        mLlWantedRewardCategory.setOnClickListener(this);
        mLlWantedLimitedTime.setOnClickListener(this);
        mLlWantedKeyWord.setOnClickListener(this);
        mLlWantedGetCondition.setOnClickListener(this);
        mLlWantedFlowExplain.setOnClickListener(this);
        mLlWantedCommitUserInfo.setOnClickListener(this);
        mLlWantedCommitPicInfo.setOnClickListener(this);
        mTvStatement.setOnClickListener(this);
        mTvPublish.setOnClickListener(this);
    }

    private void init() {
        mActivitySkipType = getIntent().getIntExtra(Constant.Activity.Type, DEFAULT_BRAND_NEW_TYPE);
        LogUtils.i("mActivitySkipType=" + mActivitySkipType);
        mActivityWantedId = getIntent().getIntExtra(Constant.Activity.Data, -1);
        LogUtils.i("mActivityWantedId=" + mActivityWantedId);
        mActivityPayState = getIntent().getIntExtra(Constant.Activity.Data1, 0);
        LogUtils.i("mActivityPayState=" + mActivityPayState);
        mTvTitleBarTitle.setText("发布悬赏");
        mTvTitleBarRight.setText("保存");
        mDisposable = RxBus.getDefault().toObservable(WantedPublishEvent.class).subscribe(new Consumer<WantedPublishEvent>() {
            @Override
            public void accept(WantedPublishEvent wantedPublishEvent) throws Exception {
                LogUtils.i("发布成功");
                PublishActivity.this.finish();
            }
        });
        ViewTreeObserver vto = mFlWantedGoodsPic.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mFlWantedGoodsPic.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mFieldsFlHeight = mFlWantedGoodsPic.getHeight() == 0 ? DensityUtil.dip2px(PublishActivity.this, 150) : mFlWantedGoodsPic.getHeight();
            }
        });
    }

    private void initData() {
        mPresenter.getData("AUDIT_TIME");
        mPresenter.getData("BUSINESS_TYPE");
        mPresenter.getData("REWARD_TYPE");
    }

    private void doViewBack() {
        EditableList.add(Constant.Wanted.STATE_SAVE);
        EditableList.add(Constant.Wanted.STATE_PUBLISHED);
        EditableList.add(Constant.Wanted.DEFAULT_BRAND_NEW_TYPE);
        //根据类型判断控件是否可编辑
        if (!EditableList.contains(mActivitySkipType)) {
            mTvTitleBarRight.setVisibility(View.GONE);
            mIvWantedGoodsPic.setEnabled(false);
            mTvWantedGoodsPic.setEnabled(false);

            mEtWantedPrice.setEnabled(false);
            mEtNumbers.setEnabled(false);
            mIvNumberSub.setEnabled(false);
            mIvNumberAdd.setEnabled(false);

            mLlWantedCheckTime.setEnabled(false);
            mLlWantedBusinessCategory.setEnabled(false);
            mLlWantedRewardCategory.setEnabled(false);

            mLlWantedLimitedTime.setEnabled(false);
            mEtWantedTicket.setEnabled(false);
            mLlWantedKeyWord.setEnabled(false);

            mEtWantedTaskLink.setEnabled(false);

            mLlBottom.setVisibility(View.GONE);
            mTvPublish.setVisibility(View.GONE);
        }
        //非新建悬赏,获取数据
        if (mActivitySkipType != DEFAULT_BRAND_NEW_TYPE) {
            mPresenter.getWantedData(mActivityWantedId);
        }
        hideSoftInput();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_number_sub) {
            doSubAndAdd(0);
        } else if (v.getId() == R.id.iv_number_add) {
            doSubAndAdd(1);
        }
        if (System.currentTimeMillis() - mCurrentTimeMillis < 500)
            return;
        mCurrentTimeMillis = System.currentTimeMillis();
        switch (v.getId()) {
            case R.id.iv_title_bar_back:
                PublishActivity.this.finish();
                break;
            case R.id.tv_title_bar_right:
                doSave();
                break;
            case R.id.iv_wanted_goods_pic:
            case R.id.tv_wanted_goods_pic:
                doAddPic();
                break;
            case R.id.ll_wanted_title:
                doEditTitle();
                break;
            case R.id.ll_wanted_checkTime:
                doSelectCheckTime();
                break;
            case R.id.ll_wanted_business_category:
                doSelectBusinessCategory();
                break;
            case R.id.ll_wanted_reward_category:
                doSelectRewardCategory();
                break;
            case R.id.ll_wanted_limited_time:
                doSelectLimitedTime();
                break;
            case R.id.ll_wanted_key_word:
                doAddKeyWord();
                break;
            case R.id.ll_wanted_get_condition:
                doEditGetCondition();
                break;
            case R.id.ll_wanted_flow_explain:
                doEditFlowExplain();
                break;
            case R.id.ll_wanted_commit_user_info:
                doAddCommitUserInfo();
                break;
            case R.id.ll_wanted_commit_pic_info:
                doAddCommitPicInfo();
                break;
            case R.id.tv_statement:
                mPresenter.getData("DISCLAIMER");
                break;
            case R.id.tv_publish:
                doPublish();
                break;
        }
    }

    //-----------------------------------------------------------------常规方法---------------------------------------------------------------------------
    private void doAddPic() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(intent, ACTION_CHOOSE_GOODS_PIC);
            hideSoftInput();
        } catch (Exception ex) {
            LogUtils.e(ex.getMessage());
        }
    }

    private void doEditTitle() {
        Intent intent = new Intent(this, TextEditActivity.class);
        String value = mTvWantedTitle.getText().toString().trim();
        intent.putExtra(Constant.Activity.Data, value);
        intent.putExtra(Constant.Activity.Type1, mActivitySkipType);
        startActivityForResult(intent, ACTION_CHOOSE_TITLE);
    }

    /**
     * @param type 0：减; 1:加
     */
    private void doSubAndAdd(int type) {
        mEtNumbers.clearFocus();
        try {
            switch (type) {
                case 0:
                    String subNumber = mEtNumbers.getText().toString().trim();
                    if (TextUtils.isEmpty(subNumber) || subNumber.equals("0") || subNumber.equals("" + mMinNumber))
                        return;
                    int subValue = new Integer(subNumber).intValue();
                    subValue = --subValue;
                    mEtNumbers.setText("" + subValue);
                    break;
                case 1:
                    String addNumber = mEtNumbers.getText().toString().trim();
                    addNumber = TextUtils.isEmpty(addNumber) ? "0" : addNumber;
                    int addValue = new Integer(addNumber).intValue();
                    if (addValue == mMaxNumber)
                        return;
                    addValue = ++addValue;
                    mEtNumbers.setText("" + addValue);
                    break;
            }
        } catch (Exception ex) {
            LogUtils.e(ex.getMessage());
        }
    }

    private void doSelectCheckTime() {
        doListView("审核时间选择", mHttpCheckTimeData, 1);
    }

    private void doSelectBusinessCategory() {
        doListView("业务分类选择", mHttpBusinessData, 2);
    }

    private void doSelectRewardCategory() {
        doListView("悬赏类别选择", mHttpRewardData, 3);
    }

    private void doListView(String title, final ArrayList<DictionaryValue> list, final int type) {
        try {
            if (mPopupWindow != null)
                mPopupWindow.dismiss();
            if (list != null) {
                int layoutId = R.layout.popup_list_title_layout;
                View contentView = LayoutInflater.from(this).inflate(layoutId, null);
                TextView textView = contentView.findViewById(R.id.tv_title);
                final ListView listView = contentView.findViewById(R.id.listView);
                textView.setText(TextUtils.isEmpty(title) ? "" : title);
                switch (type) {
                    case 1:
                        if (mTimeAdapter == null)
                            mTimeAdapter = new PublishCategroyAdapter(this, list, type);
                        listView.setAdapter(mTimeAdapter);
                        break;
                    case 2:
                        if (mBusinessAdapter == null)
                            mBusinessAdapter = new PublishCategroyAdapter(this, list, type);
                        listView.setAdapter(mBusinessAdapter);
                        break;
                    case 3:
                        if (mCategroyAdapter == null)
                            mCategroyAdapter = new PublishCategroyAdapter(this, list, type);
                        listView.setAdapter(mCategroyAdapter);
                        break;
                }
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (mPopupWindow != null)
                            mPopupWindow.dismiss();
                        int id1 = list.get(position).id;
                        int option1 = list.get(position).option1;
                        String value = list.get(position).value;
                        switch (type) {
                            case 1:
                                mCheckTimeId = id1;
                                String s = MyDateUtils.formatTime(new Long(value));
                                mTvWantedCheckTime.setText(TextUtils.isEmpty(s) ? "" : s);
                                break;
                            case 2:
                                mBusinessId = id1;
                                mTvWantedBusinessCategory.setText(TextUtils.isEmpty(value) ? "" : value);
                                if (option1 == 1)
                                    mLlWantedTicket.setVisibility(View.VISIBLE);
                                else
                                    mLlWantedTicket.setVisibility(View.GONE);
                                break;
                            case 3:
                                mRewardId = id1;
                                mTvWantedRewardCategory.setText(TextUtils.isEmpty(value) ? "" : value);
                                break;
                        }
                        LogUtils.i("value=" + value + "ID=" + id1);
                    }
                });
                mPopupWindow = new PopupWindow(contentView,
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                mPopupWindow.setBackgroundDrawable(new ColorDrawable());
                mPopupWindow.showAtLocation(decorView, Gravity.BOTTOM, 0, 0);
                mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        backgroundAlpha(1f);
                    }
                });
                backgroundAlpha(0.5f);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doSelectLimitedTime() {
        hideSoftInput();
        boolean[] booleen = new boolean[]{true, true, true, true, true, false};
        Calendar startDate = Calendar.getInstance();
        startDate.set(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH));
        Calendar endDate = Calendar.getInstance();
        endDate.set(endDate.get(Calendar.YEAR) + 3, endDate.get(Calendar.MONTH), endDate.get(Calendar.DAY_OF_MONTH));
        TimePickerView pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                Date currentDate = new Date();
                if (date.after(currentDate)) {
                    mTvWantedLimitedTime.setText(getTime(date));
                } else {
                    ToastUtil.showToast(PublishActivity.this, "下架时间必须大于当前时间");
                }
            }
        })
                .setType(booleen) //显示类型
                .setCancelText("选择截止时间")
                .setRangDate(startDate, endDate)//起始终止年月日设定
                .setLabel("年", "月", "日", "", "", "")
                .isCenterLabel(false)
                .build();
        pvTime.setDate(Calendar.getInstance());
        pvTime.show();
    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        mFieldsLimitedTime = date;
        return format.format(date);
    }


    private void doAddKeyWord() {
        Intent intent = new Intent(PublishActivity.this, KeyWordsActivity.class);
        intent.putExtra(Constant.Activity.Data2, mTvWantedKeyWord.getText().toString().trim());
        startActivityForResult(intent, ACTION_CHOOSE_KEY_WORD);
    }

    private void doEditGetCondition() {
        Intent intent = new Intent(this, TextEditActivity.class);
        String value = mTvWantedGetCondition.getText().toString().trim();
        intent.putExtra(Constant.Activity.Data, value);
        intent.putExtra(Constant.Activity.Type, 1);
        intent.putExtra(Constant.Activity.Type1, mActivitySkipType);
        startActivityForResult(intent, ACTION_CHOOSE_CONDITION);
    }

    private void doEditFlowExplain() {
        Intent intent = new Intent(this, TextEditActivity.class);
        String value = mTvWantedFlowExplain.getText().toString().trim();
        intent.putExtra(Constant.Activity.Data, value);
        intent.putExtra(Constant.Activity.Type, 2);
        intent.putExtra(Constant.Activity.Type1, mActivitySkipType);
        intent.putExtra(Constant.Activity.Common, mHttpComparePicPath);
        startActivityForResult(intent, ACTION_CHOOSE_FLOW);
    }

    private void doAddCommitUserInfo() {
        Intent intent = new Intent(this, InfoEditActivity.class);
        intent.putExtra(Constant.Activity.Data, mFieldsUserInfoData);
        intent.putExtra(Constant.Activity.Type, 0);
        intent.putExtra(Constant.Activity.Type1, mActivitySkipType);
        startActivityForResult(intent, ACTION_CHOOSE_USERINFO);
    }

    private void doAddCommitPicInfo() {
        Intent intent = new Intent(this, InfoEditActivity.class);
        intent.putExtra(Constant.Activity.Data, mFieldsPicInfoData);
        intent.putExtra(Constant.Activity.Type, 1);
        intent.putExtra(Constant.Activity.Type1, mActivitySkipType);
        startActivityForResult(intent, ACTION_CHOOSE_PICINFO);
    }

    //保存悬赏
    private void doSave() {
        if (TextUtils.isEmpty(SettingUtils.getUserToken(this)))
            startActivity(new Intent(this, LoginActivity.class));
        mUploadString = checkInput(0);
        if (TextUtils.isEmpty(mUploadString))
            return;
        doUpload();
    }

    //发布悬赏
    private void doPublish() {
        if (TextUtils.isEmpty(SettingUtils.getUserToken(this)))
            startActivity(new Intent(this, LoginActivity.class));
        String json = checkInput(1);
        if (TextUtils.isEmpty(json))
            return;
        Intent intent = new Intent(this, PublishConfirmActivity.class);
        intent.putExtra(Constant.Activity.Data, json);
        intent.putExtra(Constant.Activity.Type, mActivitySkipType);
        startActivity(intent);
    }


    //检查输入
    private String checkInput(int state) {
        String result = null;
        //商品图片
        if (mActivitySkipType == DEFAULT_BRAND_NEW_TYPE) {
            if (mFieldsGoodsPicFile == null || !mFieldsGoodsPicFile.exists()) {
                ToastUtil.showToast(this, "请选择商品图片");
                return result;
            }
        } else {
            if (TextUtils.isEmpty(mHttpGoodsPic) && mFieldsGoodsPicFile == null && !mFieldsGoodsPicFile.exists()) {
                ToastUtil.showToast(this, "请选择商品图片");
                return result;
            }
        }
        String title = mTvWantedTitle.getText().toString().trim();
        if (TextUtils.isEmpty(title) && EditableList.contains(mActivitySkipType)) {
            ToastUtil.showToast(this, "标题不能为空");
            return result;
        }
        String price = mEtWantedPrice.getText().toString().trim();
        if (TextUtils.isEmpty(price) && EditableList.contains(mActivitySkipType)) {
            ToastUtil.showToast(this, "奖励不能为空");
            return result;
        }
        String number = mEtNumbers.getText().toString().trim();
        if (TextUtils.isEmpty(number) && EditableList.contains(mActivitySkipType)) {
            ToastUtil.showToast(this, "名额不能为空");
            return result;
        }
        String stringCheckTime = mTvWantedCheckTime.getText().toString().trim();
        if (TextUtils.isEmpty(stringCheckTime) && EditableList.contains(mActivitySkipType)) {
            ToastUtil.showToast(this, "请选择审核时间");
            return result;
        }
        String stringBusinessCategory = mTvWantedBusinessCategory.getText().toString().trim();
        if (TextUtils.isEmpty(stringBusinessCategory) && EditableList.contains(mActivitySkipType)) {
            ToastUtil.showToast(this, "请选择业务类别");
            return result;
        }
        String stringRewardCategory = mTvWantedRewardCategory.getText().toString().trim();
        if (TextUtils.isEmpty(stringRewardCategory) && EditableList.contains(mActivitySkipType)) {
            ToastUtil.showToast(this, "请选择悬赏类别");
            return result;
        }
        String stringTicket = mEtWantedTicket.getText().toString().trim();
        if (TextUtils.isEmpty(stringTicket) && EditableList.contains(mActivitySkipType) && mBusinessId == 15) {
            ToastUtil.showToast(this, "请输入优惠额度");
            return result;
        }
        String stringLimitedTime = mTvWantedLimitedTime.getText().toString().trim();
        if (TextUtils.isEmpty(stringLimitedTime) && EditableList.contains(mActivitySkipType)) {
            ToastUtil.showToast(this, "请选择截止时间");
            return result;
        }
        String keyWord = mTvWantedKeyWord.getText().toString().trim();
        String stringGetCondition = mTvWantedGetCondition.getText().toString().trim();
        if (TextUtils.isEmpty(stringGetCondition) && EditableList.contains(mActivitySkipType)) {
            ToastUtil.showToast(this, "条件不能为空");
            return result;
        }
        mUploadStringFlow = mTvWantedFlowExplain.getText().toString().trim();
        if (TextUtils.isEmpty(mUploadStringFlow) && EditableList.contains(mActivitySkipType)) {
            ToastUtil.showToast(this, "流程不能为空");
            return result;
        }
        String stringTaskLink = mEtWantedTaskLink.getText().toString().trim();
        if (!TextUtils.isEmpty(stringTaskLink)) {
            Pattern pattern = Pattern.compile("[a-zA-z]+://[^\\s]*");
            Matcher matcher = pattern.matcher(stringTaskLink);
            if (!matcher.find()) {
                ToastUtil.showToast(this, "请输入可用链接");
                return result;
            }
        }

        if (!mCbState.isChecked() && EditableList.contains(mActivitySkipType)) {
            ToastUtil.showToast(this, "请阅读风险提示及免责声明");
            return result;
        }
        JSONObject child = new JSONObject();
        try {
            //商品图片
            if (mActivitySkipType == DEFAULT_BRAND_NEW_TYPE) {
                child.put("picture", mFieldsGoodsPicFile.getAbsolutePath());
            } else {
                if (mFieldsGoodsPicFile != null && mFieldsGoodsPicFile.exists()) {
                    child.put("picture", mFieldsGoodsPicFile.getAbsolutePath());
                } else {
                    child.put("picture", mHttpGoodsPic);
                }
            }
            child.put("title", title);
            child.put("money", price);
            child.put("number", number);
            child.put("mode", mCheckTimeId);
            child.put("classify", mBusinessId);
            child.put("discount", stringTicket);
            child.put("category", mRewardId);
            child.put("lowerFrameTime", mFieldsLimitedTime);
            if (!TextUtils.isEmpty(keyWord)) {
                ArrayList<String> list = new ArrayList<>();
                if (keyWord.contains(",")) {
                    String[] split = keyWord.split(",");
                    if (split != null && split.length > 0) {
                        for (int i = 0; i < split.length; i++) {
                            list.add(split[i]);
                        }
                    }
                }
                child.put("keyWords", list);
            }
            child.put("condition", stringGetCondition);
            child.put("explain", mUploadStringFlow);
            child.put("content", stringTaskLink);
            child.put("describe", mFieldsUserInfoData);
            child.put("rewardPicInfo", mFieldsPicInfoData);
            child.put("state", state);
            if (mActivitySkipType == Constant.Wanted.STATE_PUBLISHED)//已发布的状态,是重新发布
                child.put("id", -1);
            else
                child.put("id", mActivityWantedId);
            result = child.toString();
        } catch (Exception ex) {
            LogUtils.e(ex.getMessage());
        }
        return result;
    }

    private void doUpload() {
        if (!TextUtils.isEmpty(mUploadStringFlow)) {
            isUploadIconFile = false;
            isUploadFlowPicFile = false;
            Pattern p = Pattern.compile(mCommonPicPath);
            Matcher m = p.matcher(mUploadStringFlow);
            ArrayList<File> mUploadPicList = new ArrayList<>();
            while (m.find()) {
                if (!TextUtils.isEmpty((m.group()))) {
                    LogUtils.i("正则比对结果:" + m.group());
                    mUploadPicList.add(new File(m.group()));
                }
            }
            //上传图片
            RequestBody type = RequestBody.create(MediaType.parse("multipart/form-data"), "" + Constant.Upload.Type_Wanted);
            //icon
            Map<String, RequestBody> iconImages = new HashMap<>();
            if (mFieldsGoodsPicFile != null && mFieldsGoodsPicFile.exists()) {
                isUploadIconFile = true;
                mUploadingIconFile = true;
                RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), mFieldsGoodsPicFile);
                iconImages.put("files\";filename=\"" + mFieldsGoodsPicFile.getName(), fileBody);
                mPresenter.uploadIconImages(type, iconImages);
            }
            //flow
            Map<String, RequestBody> images = new HashMap<>();
            if (mUploadPicList.size() > 0) {
                isUploadFlowPicFile = true;
                mUploadingFlowPicFile = true;
                for (File file : mUploadPicList) {
                    //上传流程图片
                    File uploadfile = ImageUtils.compressImage(this, file, System.currentTimeMillis() + ".jpg");
                    RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data"), uploadfile);
                    images.put("files\";filename=\"" + uploadfile.getName(), body);
                }
                mPresenter.uploadImages(type, images);
            }
            if (!isUploadIconFile && !isUploadFlowPicFile)//两种类型的图片都不上传直接发布
                doSavePublishInfo("");
        }
    }

    private void doSavePublishInfo(String replace) {
        try {
            if (TextUtils.isEmpty(replace)) {
                mPresenter.doPublish(-1, mUploadString);
            } else {
                JSONObject jsonObject = JSON.parseObject(mUploadString);
                jsonObject.put("explain", replace);
                LogUtils.i("发布的最终数据=" + jsonObject.toString());
                mPresenter.doPublish(-1, jsonObject.toString());
            }
        } catch (Exception ex) {
            LogUtils.e(ex.getMessage());
        }
    }

    //---------------------------------------------------------------------回调-------------------------------------------------------------------------
    @Override
    public void onGetDataFinished(HomeWantedDetailEntity httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            HomeWantedDetailEntity.Reward reward = httpResult.reward;
            if (reward != null) {
                try {
                    if (!TextUtils.isEmpty(reward.picture)) {
                        mIvWantedGoodsPic.setVisibility(View.VISIBLE);
                        mTvWantedGoodsPic.setVisibility(View.GONE);
                        mHttpGoodsPic = reward.picture;
                        Glide.with(this).load(reward.picture).asBitmap().error(R.drawable.net_less_140).into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                mIvWantedGoodsPic.setImageBitmap(resource);
                            }
                        });
                        int i = mHttpGoodsPic.lastIndexOf("/");
                        String substring = mHttpGoodsPic.substring(0, i + 1);
                        mHttpComparePicPath = substring + ".+?\\.\\w{3}";
                    } else {
                        mIvWantedGoodsPic.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.net_less_140));
                    }
                    mTvWantedTitle.setText(TextUtils.isEmpty(reward.title) ? "" : reward.title);
                    if (reward.money != null)
                        mEtWantedPrice.setText(new DecimalFormat("#0.00").format(reward.money));
                    if (reward.number != 0)
                        mEtNumbers.setText("" + reward.number);
                    doShowType(reward);
                    mTvWantedLimitedTime.setText(TextUtils.isEmpty(reward.lowerFrameTime) ? "" : reward.lowerFrameTime);
                    mEtWantedTicket.setText(TextUtils.isEmpty(reward.discount) ? "" : reward.discount);
                    mTvWantedKeyWord.setText(TextUtils.isEmpty(reward.keyword) ? "" : reward.keyword);
                    mTvWantedGetCondition.setText(TextUtils.isEmpty(reward.condition) ? "" : reward.condition);
                    mTvWantedFlowExplain.setText(TextUtils.isEmpty(reward.explain) ? "" : reward.explain);
                    mEtWantedTaskLink.setText(TextUtils.isEmpty(reward.content) ? "" : reward.content);
                    //需要用户提交的信息
                    List<RewardUserInfos> rewardUserInfos = reward.rewardUserInfos;
                    if (rewardUserInfos != null && rewardUserInfos.size() > 0) {
                        if (mFieldsUserInfoData == null)
                            mFieldsUserInfoData = new ArrayList<>();
                        for (RewardUserInfos entity : rewardUserInfos) {
                            mFieldsUserInfoData.add(entity.describe);
                        }
                    }
                    if (mFieldsUserInfoData.size() == 0)
                        mTvWantedCommitInfo.setHint(mFieldsUserInfoData.size() + "项");
                    else
                        mTvWantedCommitInfo.setText(mFieldsUserInfoData.size() + "项");
                    List<RewardPics> rewardPics = reward.rewardPics;
                    if (rewardPics != null && rewardPics.size() > 0) {
                        if (mFieldsPicInfoData == null)
                            mFieldsPicInfoData = new ArrayList<>();
                        for (RewardPics entity : rewardPics) {
                            mFieldsPicInfoData.add(entity.picture);
                        }
                    }
                    if (mFieldsPicInfoData.size() == 0)
                        mTvWantedCommitPic.setHint(mFieldsPicInfoData.size() + "项");
                    else
                        mTvWantedCommitPic.setText(mFieldsPicInfoData.size() + "项");
                } catch (Exception ex) {
                    LogUtils.e(ex.getMessage());
                }
            }
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(PublishActivity.this, TextUtils.isEmpty(httpResult.msg) ? "" : httpResult.msg);
            startActivityForResult(new Intent(this, LoginActivity.class), ACTION_GO_TO_LOGIN);
        } else {
            ToastUtil.showToast(PublishActivity.this, TextUtils.isEmpty(httpResult.msg) ? "" : httpResult.msg);
        }
    }

    private void doShowType(HomeWantedDetailEntity.Reward reward) {
        mCheckTimeId = reward.mode;
        mBusinessId = reward.classify;
        mRewardId = reward.category;
        doMacthType();
    }

    @Override
    public void showView(DictionaryHttpEntity result, String key) {
        if (result.code == Constant.HttpResult.SUCCEED) {
            if (!TextUtils.isEmpty(key) && key.equals("AUDIT_TIME")) {
                if (result.types != null) {
                    mHttpCheckTimeData = result.types;
                }
            } else if (!TextUtils.isEmpty(key) && key.equals("BUSINESS_TYPE")) {
                if (result.types != null) {
                    mHttpBusinessData = result.types;
                }
            } else if (!TextUtils.isEmpty(key) && key.equals("REWARD_TYPE")) {
                if (result.types != null) {
                    mHttpRewardData = result.types;
                }
            } else {
                if (!TextUtils.isEmpty(key) && key.equals("DISCLAIMER")) {
                    if (result.types != null && result.types.size() > 0) {
                        String value = result.types.get(0).value;
                        if (!TextUtils.isEmpty(value)) {
                            Intent intent = new Intent(PublishActivity.this, StatementActivity.class);
                            intent.putExtra(Constant.Activity.Data, value);
                            startActivity(intent);
                        }
                    }
                }
            }
            doMacthType();
        } else if (result.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录");
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            ToastUtil.showToast(PublishActivity.this, TextUtils.isEmpty(result.msg) ? "数据加载失败" : result.msg);
        }
    }

    private void doMacthType() {
        try {
            if (mActivitySkipType != DEFAULT_BRAND_NEW_TYPE) {
                if (mHttpCheckTimeData != null && mHttpCheckTimeData.size() > 0 && mCheckTimeId != 0) {
                    for (DictionaryValue value : mHttpCheckTimeData) {
                        if (value.id == mCheckTimeId) {
                            mTvWantedCheckTime.setText(MyDateUtils.formatTime(new Long(value.value)));
                            break;
                        }
                    }
                }
                if (mHttpBusinessData != null && mHttpBusinessData.size() > 0 && mBusinessId != 0) {
                    for (DictionaryValue value : mHttpBusinessData) {
                        if (value.option1 == 1) {
                            mLlWantedTicket.setVisibility(View.VISIBLE);
                        }
                        if (value.id == mBusinessId) {
                            mTvWantedBusinessCategory.setText(value.value);
                            break;
                        }
                    }
                }
                if (mHttpRewardData != null && mHttpRewardData.size() > 0 && mRewardId != 0) {
                    for (DictionaryValue value : mHttpRewardData) {
                        if (value.id == mRewardId) {
                            mTvWantedRewardCategory.setText(value.value);
                            break;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            LogUtils.e(ex.getMessage());
        }
    }

    @Override
    public void onSaveOrPublishResult(HttpResult httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            ToastUtil.showToast(PublishActivity.this, TextUtils.isEmpty(httpResult.msg) ? "保存成功" : httpResult.msg);
            RxBus.getDefault().post(new WantedSaveEvent());
        } else {
            ToastUtil.showToast(PublishActivity.this, TextUtils.isEmpty(httpResult.msg) ? "保存失败" : httpResult.msg);
        }
    }

    @Override
    public void onImgUploadError() {
        ToastUtil.showToast(PublishActivity.this, "流程图片上传失败,请重新上传");
    }

    @Override
    public void onImgUploadResult(HttpResult<ArrayList<String>> result) {
        if (result.code == Constant.HttpResult.SUCCEED) {
            if (result.data != null && result.data.size() > 0) {
                mUploadingFlowPicFile = false;
                doReplaceFlow(result.data);
                if (isUploadIconFile) {//有上传商品图片
                    if (!mUploadingIconFile)//上传成功商品图片后再发布
                        doSavePublishInfo(mUploadStringFlow);
                } else {
                    doSavePublishInfo(mUploadStringFlow);
                }
            } else {
                ToastUtil.showToast(this, "流程图片上传失败");
            }
        } else {
            ToastUtil.showToast(PublishActivity.this, TextUtils.isEmpty(result.msg) ? "" : result.msg);
        }
    }

    private void doReplaceFlow(ArrayList<String> data) {
        int count = 0;
        if (data != null && data.size() > count && !TextUtils.isEmpty(mUploadStringFlow)) {
            Pattern p = Pattern.compile(mCommonPicPath);
            Matcher m = p.matcher(mUploadStringFlow);
            while (m.find()) {
                if (!TextUtils.isEmpty((m.group())) && data.size() > count) {
                    String group = m.group();
                    String s = data.get(count);
                    mUploadStringFlow = mUploadStringFlow.replace(group, s);
                    count++;
                    continue;
                }
            }
        }
    }

    @Override
    public void onIconImgUploadError() {
        ToastUtil.showToast(PublishActivity.this, "商品图片上传失败,请重新上传");
    }

    @Override
    public void onIconImgUploadResult(HttpResult<ArrayList<String>> result) {
        if (result.code == Constant.HttpResult.SUCCEED) {
            if (result.data != null && result.data.size() > 0) {
                mUploadingIconFile = false;
                doReplaceIcon(result.data.get(0));
                if (isUploadFlowPicFile) {//有上传流程图片
                    if (!mUploadingFlowPicFile) {//上传成功流程图片后再发布
                        doSavePublishInfo(mUploadStringFlow);
                    }
                } else {
                    doSavePublishInfo(mUploadStringFlow);
                }
            } else {
                ToastUtil.showToast(this, "商品图片上传失败");
            }
        } else {
            ToastUtil.showToast(PublishActivity.this, TextUtils.isEmpty(result.msg) ? "" : result.msg);
        }
    }

    private void doReplaceIcon(String value) {
        try {
            JSONObject jsonObject = JSON.parseObject(mUploadString);
            jsonObject.put("picture", value);
            mUploadString = jsonObject.toString();
        } catch (Exception ex) {
            LogUtils.e(ex.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ACTION_CHOOSE_GOODS_PIC:
                if (resultCode == RESULT_OK && data != null) {
                    try {
                        String path = FileUtils.getImageAbsolutePath(this, data.getData());
                        FileUtils.checkAndCreateDirs(path);
                        mFieldsGoodsPicFile = ImageUtils.compressImage(this, new File(path), System.currentTimeMillis() + ".jpg");
                        if (mFieldsGoodsPicFile != null)
                            cropImage(Uri.fromFile(new File(path)), Uri.fromFile(mFieldsGoodsPicFile));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case UCrop.REQUEST_CROP:
                if (resultCode == RESULT_OK) {
                    if (data != null)
                        LogUtils.i(data.toString());
                    try {
                        showImage(mFieldsGoodsPicFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case ACTION_CHOOSE_TITLE:
                if (resultCode == RESULT_OK && data != null) {
                    String value = data.getStringExtra(Constant.Activity.Data);
                    mTvWantedTitle.setText(TextUtils.isEmpty(value) ? "" : value);
                }
                break;
            case ACTION_CHOOSE_KEY_WORD:
                if (resultCode == RESULT_OK && data != null) {
                    Bundle keyWords = data.getBundleExtra(Constant.Activity.FreedBack);
                    LogUtils.i(keyWords.toString());
                    String keyWord1 = (String) keyWords.get("keyWord1");
                    String keyWord2 = (String) keyWords.get("keyWord2");
                    String keyWord3 = (String) keyWords.get("keyWord3");
                    StringBuffer stringBuffer = new StringBuffer();
                    if (!TextUtils.isEmpty(keyWord1))
                        stringBuffer.append(keyWord1 + ",");
                    if (!TextUtils.isEmpty(keyWord2))
                        stringBuffer.append(keyWord2 + ",");
                    if (!TextUtils.isEmpty(keyWord3))
                        stringBuffer.append(keyWord3);
                    mTvWantedKeyWord.setText(stringBuffer.toString());
                }
                break;
            case ACTION_CHOOSE_CONDITION:
                if (resultCode == RESULT_OK && data != null) {
                    String value = data.getStringExtra(Constant.Activity.Data);
                    mTvWantedGetCondition.setText(TextUtils.isEmpty(value) ? "" : value);
                }
                break;
            case ACTION_CHOOSE_FLOW:
                if (resultCode == RESULT_OK && data != null) {
                    String value = data.getStringExtra(Constant.Activity.Data);
                    mTvWantedFlowExplain.setText(TextUtils.isEmpty(value) ? "" : value);
                }
                break;
            case ACTION_CHOOSE_USERINFO:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList list = data.getStringArrayListExtra(Constant.Activity.Data);
                    mFieldsUserInfoData = new ArrayList<>();
                    checkResult(list, 0);
                    if (mFieldsUserInfoData != null && mFieldsUserInfoData.size() > 0)
                        mTvWantedCommitInfo.setText(mFieldsUserInfoData.size() + "项");
                    else
                        mTvWantedCommitInfo.setHint("0项");
                }
                break;
            case ACTION_CHOOSE_PICINFO:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList list = data.getStringArrayListExtra(Constant.Activity.Data);
                    mFieldsPicInfoData = new ArrayList<>();
                    checkResult(list, 1);
                    if (mFieldsPicInfoData != null && mFieldsPicInfoData.size() > 0)
                        mTvWantedCommitPic.setText(mFieldsPicInfoData.size() + "项");
                    else
                        mTvWantedCommitInfo.setHint("0项");
                }
                break;
            case ACTION_GO_TO_LOGIN:
                if (data != null && data.getBooleanExtra(Constant.Activity.Data, false) && mActivitySkipType != DEFAULT_BRAND_NEW_TYPE) {
                    mPresenter.getWantedData(mActivityWantedId);
                }
                break;
        }
    }

    private void checkResult(ArrayList<String> list, int type) {
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                String value = list.get(i);
                if (value != null && !TextUtils.isEmpty(value)) {
                    switch (type) {
                        case 0:
                            mFieldsUserInfoData.add(value);
                            break;
                        case 1:
                            mFieldsPicInfoData.add(value);
                            break;
                    }
                }
            }
        }
    }

    public void cropImage(Uri uri, Uri cropUri) throws Exception {
        methodRequiresTwoPermission();
        UCrop.of(uri, cropUri)
                .withAspectRatio(9, 9)
                .withMaxResultSize(1080, 1080)
                .start(PublishActivity.this);
    }

    @AfterPermissionGranted(RC_CAMERA_AND_LOCATION)
    private void methodRequiresTwoPermission() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, "下列权限未获权，是否开启", RC_CAMERA_AND_LOCATION, perms);
        }
    }

    private void showImage(File cropFile) throws Exception {
        if (!TextUtils.isEmpty(cropFile.getAbsolutePath())) {
            Bitmap bitmap = BitmapFactory.decodeFile(mFieldsGoodsPicFile.getAbsolutePath());
            if (bitmap != null) {
                mIvWantedGoodsPic.setVisibility(View.VISIBLE);
                mIvWantedGoodsPic.setImageBitmap(bitmap);
                mTvWantedGoodsPic.setVisibility(View.GONE);
                mScrollView.smoothScrollBy(0, mFieldsFlHeight);
            } else {
                ToastUtil.showToast(this, "图片获取失败");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null)
            mDisposable.dispose();
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