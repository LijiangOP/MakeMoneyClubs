package com.zhengdao.zqb.view.activity.questionsurvery;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.bigkoo.pickerview.TimePickerView;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.DictionaryHttpEntity;
import com.zhengdao.zqb.entity.DictionaryValue;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.SurveyHttpResult;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.utils.Utils;
import com.zhengdao.zqb.utils.ViewUtils;
import com.zhengdao.zqb.view.activity.login.LoginActivity;
import com.zhengdao.zqb.view.activity.webview.WebViewActivity;
import com.zhengdao.zqb.view.activity.welfareget.WelfareGetActivity;
import com.zhengdao.zqb.view.adapter.PropertiesSurveryAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author lijiangop
 * @CreateTime 2018/10/15 22:19
 */
public class QuestionSurveryActivty extends MVPBaseActivity<QuestionSurveryContract.View, QuestionSurveryPresenter> implements QuestionSurveryContract.View, View.OnClickListener {
    @BindView(R.id.scrollView)
    ScrollView mScrollView;
    @BindView(R.id.iv_back)
    ImageView  mIvBack;

    @BindView(R.id.re_sex)
    RelativeLayout mReSex;
    @BindView(R.id.tv_sex)
    TextView       mTvSex;

    @BindView(R.id.re_age)
    RelativeLayout mReAge;
    @BindView(R.id.tv_age)
    TextView       mTvAge;

    @BindView(R.id.re_profession)
    RelativeLayout mReProfession;
    @BindView(R.id.tv_profession)
    TextView       mTvProfession;

    @BindView(R.id.re__consume)
    LinearLayout mReConsume;
    @BindView(R.id.tv_consume)
    TextView     mTvConsum;

    @BindView(R.id.re_game)
    LinearLayout mReGame;
    @BindView(R.id.tv_game)
    TextView     mTvGame;

    @BindView(R.id.re_hobby)
    LinearLayout mReHobby;
    @BindView(R.id.tv_hobby)
    TextView     mTvHobby;

    @BindView(R.id.tv_lottery)
    TextView mTvLottery;

    private PopupWindow              mSexPopupWindow;
    private int                      mCurrentType;
    private String                   mSkipType;
    private int                      mWelfareState;
    private PropertiesSurveryAdapter mAdapter;
    private int mSex = -1;
    private String        mBirth;
    private StringBuilder mStringProfession;
    private StringBuilder mStringCousum;
    private StringBuilder mStringGame;
    private StringBuilder mStringHobby;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_survery);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        mSkipType = getIntent().getStringExtra(Constant.Activity.Skip);
        mWelfareState = getIntent().getIntExtra(Constant.Activity.Data, 0);
        mIvBack.setOnClickListener(this);
        mReSex.setOnClickListener(this);
        mReAge.setOnClickListener(this);
        mReProfession.setOnClickListener(this);
        mReConsume.setOnClickListener(this);
        mReGame.setOnClickListener(this);
        mReHobby.setOnClickListener(this);
        mTvLottery.setOnClickListener(this);
        if (TextUtils.equals(mSkipType, "survey")) {
            mTvLottery.setText("做问卷");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                this.finish();
                break;
            case R.id.re_sex:
                doSelectSex();
                break;
            case R.id.re_age:
                doSelectBirth();
                break;
            case R.id.re_profession:
                mCurrentType = 2;
                mPresenter.getDictionaryByKey("PROFESSION");
                break;
            case R.id.re__consume:
                mCurrentType = 3;
                mPresenter.getDictionaryByKey("INTENT_CONSUME");
                break;
            case R.id.re_game:
                mCurrentType = 4;
                mPresenter.getDictionaryByKey("GAME_INTEREST");
                break;
            case R.id.re_hobby:
                mCurrentType = 5;
                mPresenter.getDictionaryByKey("REWARD_TYPE");
                break;
            case R.id.tv_lottery:
                doChangeUserInfo();
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
                if (mSexPopupWindow != null)
                    mSexPopupWindow.dismiss();
                break;
        }
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
        mSexPopupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mSexPopupWindow.setBackgroundDrawable(new ColorDrawable());
        mSexPopupWindow.showAtLocation(mScrollView, Gravity.BOTTOM, 0, 0);
        mSexPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });
        backgroundAlpha(0.5f);
    }

    private void doChangeSex(int sex) {
        if (mSexPopupWindow != null)
            mSexPopupWindow.dismiss();
        mSex = sex;
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
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String birth = format.format(date);
                mTvAge.setText(birth);
                doChangeBirth(birth);
            }
        })
                .setType(booleen) //显示类型
                .setRangDate(startDate, endDate)//起始终止年月日设定
                .build();
        pvTime.setDate(Calendar.getInstance());
        pvTime.show();
    }

    private void doChangeBirth(String time) {
        mBirth = time;
    }

    @Override
    public void onDictionaryDataGet(DictionaryHttpEntity result, String key) {
        try {
            if (result.code == Constant.HttpResult.SUCCEED) {
                if (result.types != null && result.types.size() > 0) {
                    showPopWindow(result.types, key);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showPopWindow(final ArrayList<DictionaryValue> data, final String key) {
        View contentView = LayoutInflater.from(QuestionSurveryActivty.this).inflate(R.layout.popup_balance_type, null);
        final PopupWindow mOtherPopupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mOtherPopupWindow.setBackgroundDrawable(new ColorDrawable());
        mOtherPopupWindow.setFocusable(true);
        mOtherPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });
        mOtherPopupWindow.showAtLocation(mScrollView, Gravity.BOTTOM, 0, 0);
        RecyclerView recyclerView = contentView.findViewById(R.id.recycle_view);
        TextView cancle = contentView.findViewById(R.id.tv_cancle);
        TextView confirm = contentView.findViewById(R.id.tv_confirm);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOtherPopupWindow != null) {
                    mOtherPopupWindow.dismiss();
                }
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter != null) {
                    ArrayList<Integer> seclectList = mAdapter.getSeclectList();
                    if (seclectList != null && seclectList.size() > 0) {
                        StringBuilder stringBuilder = new StringBuilder();
                        if (key.equals("PROFESSION")) {
                            mTvProfession.setText(data.get(seclectList.get(0)).value);
                            mStringProfession = new StringBuilder();
                            mStringProfession.append("" + data.get(seclectList.get(0)).id);
                        } else if (key.equals("INTENT_CONSUME")) {
                            mStringCousum = new StringBuilder();
                            for (int i = 0; i < seclectList.size(); i++) {
                                mStringCousum.append(i == (seclectList.size() - 1) ? String.valueOf(data.get(seclectList.get(i)).id) : String.valueOf(data.get(seclectList.get(i)).id) + ",");
                                stringBuilder.append(i == (seclectList.size() - 1) ? data.get(seclectList.get(i)).value : data.get(seclectList.get(i)).value + ",");
                            }
                            mTvConsum.setText(stringBuilder);
                        } else if (key.equals("GAME_INTEREST")) {
                            mStringGame = new StringBuilder();
                            for (int i = 0; i < seclectList.size(); i++) {
                                mStringGame.append(i == (seclectList.size() - 1) ? String.valueOf(data.get(seclectList.get(i)).id) : String.valueOf(data.get(seclectList.get(i)).id) + ",");
                                stringBuilder.append(i == (seclectList.size() - 1) ? data.get(seclectList.get(i)).value : data.get(seclectList.get(i)).value + ",");
                            }
                            mTvGame.setText(stringBuilder);
                        } else if (key.equals("REWARD_TYPE")) {
                            mStringHobby = new StringBuilder();
                            for (int i = 0; i < seclectList.size(); i++) {
                                mStringHobby.append(i == (seclectList.size() - 1) ? String.valueOf(data.get(seclectList.get(i)).id) : String.valueOf(data.get(seclectList.get(i)).id) + ",");
                                stringBuilder.append(i == (seclectList.size() - 1) ? data.get(seclectList.get(i)).value : data.get(seclectList.get(i)).value + ",");
                            }
                            mTvHobby.setText(stringBuilder);
                        }
                    }
                }
                if (mOtherPopupWindow != null) {
                    mOtherPopupWindow.dismiss();
                }
            }
        });
        mAdapter = new PropertiesSurveryAdapter(this, R.layout.balance_type_item, data, mCurrentType);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.addItemDecoration(new SpaceBusinessItemDecoration(10));
        backgroundAlpha(0.7f);
    }

    private void doChangeUserInfo() {
        try {
            String age = mTvAge.getText().toString();
            if (mSex == -1) {
                ToastUtil.showToast(this, "请选择性别");
                return;
            }
            if (TextUtils.isEmpty(mBirth)) {
                ToastUtil.showToast(this, "请选择生日");
                return;
            }
            JSONObject json = new JSONObject();
            json.put("sex", "" + mSex);
            json.put("birthday", age);
            if (mStringProfession != null && !TextUtils.isEmpty(mStringProfession.toString()))
                json.put("profession", mStringProfession.toString());
            if (mStringCousum != null && !TextUtils.isEmpty(mStringCousum.toString()))
                json.put("intentionConsumption", mStringCousum.toString());
            if (mStringGame != null && !TextUtils.isEmpty(mStringGame.toString()))
                json.put("gameInterest", mStringGame.toString());
            if (mStringHobby != null && !TextUtils.isEmpty(mStringHobby.toString()))
                json.put("rewardPreference", mStringHobby.toString());
            LogUtils.e("json=" + json.toString());
            mPresenter.ChangeUserInfo(json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onChangeResult(HttpResult httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            if (TextUtils.equals(mSkipType, "welfare")) {
                Intent welfare = new Intent(QuestionSurveryActivty.this, WelfareGetActivity.class);
                welfare.putExtra(Constant.Activity.Data, mWelfareState);
                Utils.StartActivity(QuestionSurveryActivty.this, welfare);
                this.finish();
            }
            if (TextUtils.equals(mSkipType, "survey")) {
                mPresenter.getSurveyLink();
            }
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录");
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            ToastUtil.showToast(QuestionSurveryActivty.this, TextUtils.isEmpty(httpResult.msg) ? "修改失败!" : httpResult.msg);
        }
    }

    /**
     * 获取问卷调查页面链接的数据返回处理
     *
     * @param httpResult
     */
    @Override
    public void onSurveyLinkGet(SurveyHttpResult httpResult) {
        if (httpResult == null) {
            ToastUtil.showToast(QuestionSurveryActivty.this, "请求出错");
            return;
        }
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            if (!TextUtils.isEmpty(httpResult.url)) {
                Intent survey = new Intent(QuestionSurveryActivty.this, WebViewActivity.class);
                survey.putExtra(Constant.WebView.TITLE, getString(R.string.home_survey));
                survey.putExtra(Constant.WebView.URL, httpResult.url);
                startActivity(survey);
                this.finish();
            }
        } else if (httpResult.code == Constant.HttpResult.FAILD) {
            ToastUtil.showToast(QuestionSurveryActivty.this, "用户信息未完善,请重新提交");
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(QuestionSurveryActivty.this, "登录超时,请重新登录");
            startActivity(new Intent(QuestionSurveryActivty.this, LoginActivity.class));
        } else {
            ToastUtil.showToast(QuestionSurveryActivty.this, TextUtils.isEmpty(httpResult.msg) ? "请求失败" : httpResult.msg);
        }
    }

    public class SpaceBusinessItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SpaceBusinessItemDecoration(int space) {
            this.space = ViewUtils.dip2px(QuestionSurveryActivty.this, space);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int pos = parent.getChildAdapterPosition(view);
            outRect.left = space;
            outRect.bottom = space;
            if ((pos + 1) % 3 == 0) {
                outRect.right = space;
            } else {
                outRect.right = 0;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSexPopupWindow != null) {
            mSexPopupWindow.dismiss();
            mSexPopupWindow = null;
        }
    }
}
