package com.zhengdao.zqb.view.activity.investinfodetailinput;


import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.customview.Code;
import com.zhengdao.zqb.entity.PlatformBean;
import com.zhengdao.zqb.entity.PlatformHttpEntity;
import com.zhengdao.zqb.event.RefreshInVestListEvent;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.RegUtils;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.view.activity.login.LoginActivity;
import com.zhengdao.zqb.view.adapter.PlatformChooseAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InvestInfoDetailInputActivity extends MVPBaseActivity<InvestInfoDetailInputContract.View, InvestInfoDetailInputPresenter> implements InvestInfoDetailInputContract.View, View.OnClickListener {

    @BindView(R.id.et_invset_name)
    EditText  mEtInvsetName;
    @BindView(R.id.et_invset_phone)
    EditText  mEtInvsetPhone;
    @BindView(R.id.tv_invest_shops)
    TextView  mTvInvestShops;
    @BindView(R.id.iv_invest_shops)
    ImageView mIvInvestShops;
    @BindView(R.id.et_invset_number)
    EditText  mEtInvsetNumber;
    @BindView(R.id.tv_invset_cycle_tag)
    TextView  mTvInvsetCycleTag;
    @BindView(R.id.et_invset_cycle)
    EditText  mEtInvsetCycle;
    @BindView(R.id.btn_switch)
    Switch    mBtnSwitch;
    @BindView(R.id.textView2)
    TextView  mTextView2;
    @BindView(R.id.tv_invest_date)
    TextView  mTvInvestDate;
    @BindView(R.id.iv_invest_date)
    ImageView mIvInvestDate;
    @BindView(R.id.et_invset_code)
    EditText  mEtInvsetCode;
    @BindView(R.id.iv_invset_code)
    ImageView mIvInvsetCode;
    @BindView(R.id.tv_confirm)
    TextView  mTvConfirm;

    private PopupWindow             mPopupWindow;
    private ArrayList<PlatformBean> mPlatformList;
    private ArrayList<String>       mEditData;
    private int                     mCurrentPlatformId;
    private String mCycleUnit = "月";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invest_info_input);
        ButterKnife.bind(this);
        setTitle("信息录入");
        init();
        mPresenter.getData();
    }

    private void init() {
        mIvInvsetCode.setImageBitmap(Code.getInstance().createBitmap());
        mIvInvsetCode.setBackground(getResources().getDrawable(R.drawable.shape_invest_input_code));
        initClicklistener();
    }

    private void initClicklistener() {
        mIvInvsetCode.setOnClickListener(this);
        mTvInvestShops.setOnClickListener(this);
        mIvInvestShops.setOnClickListener(this);
        mTvConfirm.setOnClickListener(this);
        mTvInvestDate.setOnClickListener(this);
        mIvInvestDate.setOnClickListener(this);
        //        mEtInvsetNumber.addTextChangedListener(new SimpleTextWatcher() {
        //            private boolean isRun = false;
        //            private String d = "";
        //
        //            @Override
        //            public void onTextChanged(CharSequence s, int start, int before, int count) {
        //                if (isRun) {
        //                    isRun = false;
        //                    return;
        //                }
        //                isRun = true;
        //                d = "";
        //                String newStr = s.toString();
        //                newStr = newStr.replace(" ", "");
        //
        //                //                if (newStr.contains("元"))
        //                //                    newStr = newStr.replace("元", "");
        //                int index = 0;
        //                while ((index + 4) < newStr.length()) {
        //                    d += (newStr.substring(index, index + 4) + " ");
        //                    index += 4;
        //                }
        //                d += (newStr.substring(index, newStr.length()));
        //                int i = mEtInvsetNumber.getSelectionStart();
        //                //                //追加单位元
        //                //                if (!d.contains("元") && !TextUtils.isEmpty(d))
        //                //                    mEtInvsetNumber.setText(d + "元");
        //                mEtInvsetNumber.setText(d);
        //                try {
        //                    if (i % 5 == 0 && before == 0) {
        //                        if (i + 1 <= d.length()) {
        //                            mEtInvsetNumber.setSelection(i + 1);
        //                        } else {
        //                            mEtInvsetNumber.setSelection(d.length());
        //                        }
        //                    } else if (before == 1 && i < d.length()) {
        //                        mEtInvsetNumber.setSelection(i);
        //                    } else if (before == 0 && i < d.length()) {
        //                        mEtInvsetNumber.setSelection(i);
        //                    } else
        //                        mEtInvsetNumber.setSelection(d.length());
        //
        //                } catch (Exception e) {
        //
        //                }
        //            }
        //        });
        mBtnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    mTvInvsetCycleTag.setText("投资周期（天）");
                    mCycleUnit = "天";
                } else {
                    mTvInvsetCycleTag.setText("投资周期（月）");
                    mCycleUnit = "月";
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_invest_shops:
            case R.id.iv_invest_shops:
                if (mPlatformList == null || mPlatformList.size() == 0)
                    ToastUtil.showToast(this, "暂无平台数据");
                else
                    showPlatformDialog(mIvInvestShops);
                break;
            case R.id.iv_invset_code:
                mIvInvsetCode.setImageBitmap(Code.getInstance().createBitmap());
                mIvInvsetCode.setBackground(getResources().getDrawable(R.drawable.shape_invest_input_code));
                break;
            case R.id.tv_invest_date:
            case R.id.iv_invest_date:
                showDateDialog();
                break;
            case R.id.tv_confirm:
                if (!doCommit())
                    return;
                mPresenter.addData(mEditData);
                break;
        }
    }

    private void showPlatformDialog(View anchorView) {
        hideSoftInput();
        int layoutId = R.layout.popup_platform_choose;
        View contentView = LayoutInflater.from(this).inflate(layoutId, null);
        TextView tvCancle = contentView.findViewById(R.id.tv_cancle);
        tvCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPopupWindow != null)
                    mPopupWindow.dismiss();
            }
        });
        ListView listView = contentView.findViewById(R.id.listview);
        final ArrayList<String> mStrings = new ArrayList<>();
        if (mPlatformList != null && mPlatformList.size() > 0) {
            for (PlatformBean bean : mPlatformList) {
                mStrings.add(bean.wzName);
            }
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mTvInvestShops.setText(mStrings.get(i));
                mCurrentPlatformId = mPlatformList.get(i).id;
                if (mPopupWindow != null)
                    mPopupWindow.dismiss();
            }
        });
        PlatformChooseAdapter platformChooseAdapter = new PlatformChooseAdapter(this, mStrings);
        listView.setAdapter(platformChooseAdapter);
        mPopupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        mPopupWindow.setAnimationStyle(R.style.MyPopupWindow_anim_style);
        mPopupWindow.showAtLocation(anchorView, Gravity.BOTTOM, 0, 0);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });
        backgroundAlpha(0.7f);
    }

    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);
    }

    private void showDateDialog() {
        hideSoftInput();
        boolean[] booleen = new boolean[]{true, true, true, false, false, false};
        Calendar startDate = Calendar.getInstance();
        startDate.set(1999, 0, 1);
        Calendar endDate = Calendar.getInstance();
        endDate.set(endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH), endDate.get(Calendar.DAY_OF_MONTH));
        TimePickerView pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                mTvInvestDate.setText(getTime(date));
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

    private boolean doCommit() {
        String name = mEtInvsetName.getText().toString().trim();
        String phone = mEtInvsetPhone.getText().toString().trim();
        String platform = mTvInvestShops.getText().toString().trim();
        String number = mEtInvsetNumber.getText().toString().trim();
        String cycle = mEtInvsetCycle.getText().toString().trim();
        String date = mTvInvestDate.getText().toString().trim();
        String code = mEtInvsetCode.getText().toString().trim();
        mEditData = new ArrayList<>();
        if (TextUtils.isEmpty(name)) {
            ToastUtil.showToast(this, "姓名不能为空");
            return false;
        }
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.showToast(this, "手机号码不能为空");
            return false;
        }
        if (!RegUtils.isPhoneNum(phone)) {
            ToastUtil.showToast(this, getString(R.string.input_correct_phone));
            return false;
        }
        if (TextUtils.isEmpty(platform) || platform.equals("请选择")) {
            ToastUtil.showToast(this, "投资平台不能为空");
            return false;
        }
        if (TextUtils.isEmpty(number)) {
            ToastUtil.showToast(this, "投资金额不能为空");
            return false;
        }
        if (number.contains("元"))
            number = number.replace("元", "");
        if (TextUtils.isEmpty(cycle)) {
            ToastUtil.showToast(this, "投资周期不能为空");
            return false;
        }
        if (cycle.contains("天"))
            cycle = cycle.replace("天", "");
        if (TextUtils.isEmpty(date) || date.equals("请选择")) {
            ToastUtil.showToast(this, "投资日期不能为空");
            return false;
        }
        if (TextUtils.isEmpty(code)) {
            ToastUtil.showToast(this, "验证码不能为空");
            return false;
        }
        code = code.toLowerCase();
        String defaultCode = Code.getInstance().getCode().toLowerCase();
        if (!defaultCode.equals(code)) {
            ToastUtil.showToast(this, "验证码输入错误");
            return false;
        }
        mEditData.add(name);
        mEditData.add(phone);
        mEditData.add(mCurrentPlatformId + "");
        mEditData.add(number);
        mEditData.add(cycle);
        mEditData.add(date);
        if (!TextUtils.isEmpty(mCycleUnit) && mCycleUnit.equals("月"))
            mEditData.add("2");
        else
            mEditData.add("1");
        return true;
    }


    @Override
    public void showPlatformData(PlatformHttpEntity httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            ArrayList<PlatformBean> list = httpResult.platformList.list;
            if (list == null || list.size() == 0)
                return;
            else if (list != null) {
                if (mPlatformList == null)
                    mPlatformList = new ArrayList<>();
                mPlatformList.clear();
                mPlatformList.addAll(list);
            }
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录");
            startActivity(new Intent(this, LoginActivity.class));
        } else
            ToastUtil.showToast(this, TextUtils.isEmpty(httpResult.msg) ? "" : httpResult.msg);
    }

    @Override
    public void SuccessEdit() {
        RxBus.getDefault().post(new RefreshInVestListEvent());
    }

    @Override
    public void SuccessAdd() {
        RxBus.getDefault().post(new RefreshInVestListEvent());
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null)
            mPresenter.unsubcrible();
    }
}
