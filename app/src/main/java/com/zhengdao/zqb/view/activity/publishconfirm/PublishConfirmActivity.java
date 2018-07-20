package com.zhengdao.zqb.view.activity.publishconfirm;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.WalletHttpEntity;
import com.zhengdao.zqb.event.WantedPublishEvent;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.pay.AliPay;
import com.zhengdao.zqb.utils.ImageUtils;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.utils.ViewUtils;
import com.zhengdao.zqb.view.activity.login.LoginActivity;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.zhengdao.zqb.config.Constant.Assist.mCommonPicPath;
import static com.zhengdao.zqb.config.Constant.Wanted.DEFAULT_BRAND_NEW_TYPE;

public class PublishConfirmActivity extends MVPBaseActivity<PublishConfirmContract.View, PublishConfirmPresenter> implements PublishConfirmContract.View, View.OnClickListener, AliPay.PayCallBackListener {
    private static final int REQUESTCODE = 101;
    @BindView(R.id.iv_goods_icon)
    ImageView mIvGoodsIcon;
    @BindView(R.id.tv_goods_title)
    TextView  mTvGoodsTitle;
    @BindView(R.id.tv_goods_price)
    TextView  mTvGoodsPrice;
    @BindView(R.id.tv_number)
    TextView  mTvNumber;
    @BindView(R.id.tv_keyword_1)
    TextView  mTvKeyword1;
    @BindView(R.id.tv_keyword_2)
    TextView  mTvKeyword2;
    @BindView(R.id.tv_keyword_3)
    TextView  mTvKeyword3;
    @BindView(R.id.tv_reward)
    TextView  mTvReward;
    @BindView(R.id.tv_service)
    TextView  mTvService;
    @BindView(R.id.tv_cost)
    TextView  mTvCost;
    @BindView(R.id.tv_total)
    TextView  mTvTotal;
    @BindView(R.id.tv_balance_value)
    TextView  mTvBalanceValue;
    @BindView(R.id.tv_balance_not_enough)
    TextView  mTvBalanceNotEnough;
    @BindView(R.id.tv_confirm)
    TextView  mTvConfirm;
    @BindView(R.id.iv_balance_pay)
    ImageView mIvBalancePay;
    @BindView(R.id.iv_wechat_pay)
    ImageView mIvWechatPay;
    @BindView(R.id.iv_alipay)
    ImageView mIvAlipay;

    private String mStringPic;
    private String mStringGoodsTitle;
    private String mStringGoodsPrice;
    private String mStringGoodsNumber;
    private String mStringkeyWord1;
    private String mStringkeyWord2;
    private String mStringkeyWord3;
    private int    mPayType;
    private double mTotal;
    private String mStringJson;
    private String mStringFlow;
    private String mmStringUploadFlow;
    private long mCurrentTimeMillis = 0;
    private int     mTypeSkip;
    private boolean isUPloadIconFile;
    private boolean mUPloadingIconFile;
    private boolean isUPloadFlowPicFile;
    private boolean mUPloadingFlowPicFile;
    private boolean mBalanceNotEnough;
    private int     mAccountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_confirm);
        ButterKnife.bind(this);
        setTitle("发布悬赏");
        initData();
        init();
        initListener();
    }

    private void initData() {
        mStringJson = getIntent().getStringExtra(Constant.Activity.Data);
        LogUtils.i("页面传递数据" + mStringJson);
        mTypeSkip = getIntent().getIntExtra(Constant.Activity.Type, DEFAULT_BRAND_NEW_TYPE);
        try {
            JSONObject json = (JSONObject) JSONObject.parse(mStringJson);
            mStringPic = (String) json.get("picture");
            mStringGoodsTitle = (String) json.get("title");
            mStringGoodsPrice = (String) json.get("money");
            mStringGoodsNumber = (String) json.get("number");
            JSONArray keyWords = json.getJSONArray("keyWords");
            if (keyWords != null && keyWords.size() > 0) {
                mStringkeyWord1 = keyWords.getString(0);
            } else if (keyWords != null && keyWords.size() > 1) {
                mStringkeyWord2 = keyWords.getString(1);
            } else if (keyWords != null && keyWords.size() > 2) {
                mStringkeyWord3 = keyWords.getString(2);
            }
            mStringFlow = (String) json.get("explain");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //获取余额
        mPresenter.getBalance();
        mAccountType = SettingUtils.getAccountType(PublishConfirmActivity.this);
    }

    private void init() {
        if (!TextUtils.isEmpty(mStringPic)) {
            if (new File(mStringPic).exists()) {
                Bitmap bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(mStringPic),
                        ViewUtils.dip2px(this, 72), ViewUtils.dip2px(this, 72));
                mIvGoodsIcon.setImageBitmap(bitmap);
            } else
                Glide.with(this).load(mStringPic).error(R.drawable.net_less_140).into(mIvGoodsIcon);
        }
        mTvGoodsTitle.setText(TextUtils.isEmpty(mStringGoodsTitle) ? "" : mStringGoodsTitle);
        mTvGoodsPrice.setText(TextUtils.isEmpty(mStringGoodsPrice) ? "" : mStringGoodsPrice);
        mTvNumber.setText(TextUtils.isEmpty(mStringGoodsNumber) ? "" : "X" + mStringGoodsNumber);
        if (!TextUtils.isEmpty(mStringkeyWord1)) {
            mTvKeyword1.setVisibility(View.VISIBLE);
            mTvKeyword1.setText(TextUtils.isEmpty(mStringkeyWord1) ? "" : mStringkeyWord1);
        }
        if (!TextUtils.isEmpty(mStringkeyWord2)) {
            mTvKeyword2.setVisibility(View.VISIBLE);
            mTvKeyword2.setText(TextUtils.isEmpty(mStringkeyWord2) ? "" : mStringkeyWord2);
        }
        if (!TextUtils.isEmpty(mStringkeyWord3)) {
            mTvKeyword3.setVisibility(View.VISIBLE);
            mTvKeyword3.setText(TextUtils.isEmpty(mStringkeyWord3) ? "" : mStringkeyWord3);
        }
        try {
            if (!TextUtils.isEmpty(mStringGoodsPrice) && !TextUtils.isEmpty(mStringGoodsNumber)) {
                double price = new Double(mStringGoodsPrice);
                int number = new Integer(mStringGoodsNumber).intValue();
                double totalReward = number * price;
                double service = totalReward * 0.05;
                mTotal = totalReward + service + 100;
                mTvReward.setText("¥" + new DecimalFormat("#0.00").format(totalReward));
                mTvService.setText("¥" + new DecimalFormat("#0.00").format(service));
                mTvCost.setText("¥100");
                mTvTotal.setText("¥" + new DecimalFormat("#0.00").format(mTotal));
            }
        } catch (Exception ex) {
            LogUtils.e(ex.getMessage());
        }
        //默认余额选中
        mPayType = Constant.Pay.Balance;
        mIvBalancePay.setImageDrawable(getResources().getDrawable(R.drawable.btn_select));
    }

    private void initListener() {
        mIvBalancePay.setOnClickListener(this);
        mIvWechatPay.setOnClickListener(this);
        mIvAlipay.setOnClickListener(this);
        mTvConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - mCurrentTimeMillis < 500)
            return;
        mCurrentTimeMillis = System.currentTimeMillis();
        switch (v.getId()) {
            case R.id.iv_balance_pay:
                if (mPayType != Constant.Pay.Balance) {
                    mPayType = Constant.Pay.Balance;
                    mIvBalancePay.setImageDrawable(getResources().getDrawable(R.drawable.btn_select));
                    mIvWechatPay.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvAlipay.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                } else {
                    mPayType = 0;
                    mIvBalancePay.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                }
                break;
            case R.id.iv_wechat_pay:
                if (mPayType != Constant.Pay.WechatPay) {
                    mPayType = Constant.Pay.WechatPay;
                    mIvBalancePay.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvWechatPay.setImageDrawable(getResources().getDrawable(R.drawable.btn_select));
                    mIvAlipay.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                } else {
                    mPayType = 0;
                    mIvWechatPay.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                }
                break;
            case R.id.iv_alipay:
                if (mPayType != Constant.Pay.AliPay) {
                    mPayType = Constant.Pay.AliPay;
                    mIvBalancePay.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvWechatPay.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvAlipay.setImageDrawable(getResources().getDrawable(R.drawable.btn_select));
                } else {
                    mPayType = 0;
                    mIvAlipay.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                }
                break;
            case R.id.tv_confirm:
                if (mPayType == 0) {
                    ToastUtil.showToast(this, "请选择支付方式");
                    return;
                }
                if (!TextUtils.isEmpty(mStringJson) && !TextUtils.isEmpty(mStringFlow))
                    doUploadPic();
                break;
        }
    }

    private void doUploadPic() {
        isUPloadIconFile = false;
        Pattern p = Pattern.compile(mCommonPicPath);//是本地文件就上传
        Matcher m = p.matcher(mStringFlow);
        mmStringUploadFlow = mStringFlow;
        ArrayList<File> list = new ArrayList<>();
        while (m.find()) {
            LogUtils.i("正则比对结果:" + m.group());
            if (!TextUtils.isEmpty((m.group()))) {
                list.add(new File(m.group()));
            }
        }
        //上传图片
        RequestBody type = RequestBody.create(MediaType.parse("multipart/form-data"), "" + Constant.Upload.Type_Wanted);
        //icon
        Map<String, RequestBody> iconImages = new HashMap<>();
        if (new File(mStringPic) != null && new File(mStringPic).exists()) {//是文件就上传
            isUPloadIconFile = true;
            mUPloadingIconFile = true;
            RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), new File(mStringPic));
            iconImages.put("files\";filename=\"" + new File(mStringPic).getName(), fileBody);
            mPresenter.uploadIconImages(type, iconImages);
        }
        //flow
        Map<String, RequestBody> images = new HashMap<>();
        if (list.size() > 0) {
            isUPloadFlowPicFile = true;
            mUPloadingFlowPicFile = true;
            for (File file : list) {
                //上传流程图片
                File flowfile = ImageUtils.compressImage(this, file, System.currentTimeMillis() + ".jpg");
                RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data"), flowfile);
                images.put("files\";filename=\"" + flowfile.getName(), body);
            }
            mPresenter.uploadImages(type, images);
        }
        if (!isUPloadIconFile && !isUPloadFlowPicFile)
            doUploadJsonInfo();
    }

    @Override
    public void onImgUploadError() {
        ToastUtil.showToast(this, "流程图片上传失败,请重新上传");
    }

    @Override
    public void onImgUploadResult(HttpResult<ArrayList<String>> result) {
        if (result.code == Constant.HttpResult.SUCCEED) {
            if (result.data != null && result.data.size() > 0) {
                mUPloadingFlowPicFile = false;
                doReplaceFlow(result.data);
                if (isUPloadIconFile && !mUPloadingIconFile)
                    doUploadJsonInfo();
                else if (!isUPloadIconFile)
                    doUploadJsonInfo();
            } else {
                ToastUtil.showToast(this, "流程图片上传失败");
            }
        } else {
            ToastUtil.showToast(this, TextUtils.isEmpty(result.msg) ? "" : result.msg);
        }
    }

    @Override
    public void onIconImgUploadError() {
        ToastUtil.showToast(this, "商品图片上传失败,请重新上传");
    }


    @Override
    public void onIconImgUploadResult(HttpResult<ArrayList<String>> result) {
        if (result.code == Constant.HttpResult.SUCCEED) {
            if (result.data != null && result.data.size() > 0) {
                mUPloadingIconFile = false;
                doReplaceIcon(result.data.get(0));
                if (isUPloadFlowPicFile && !mUPloadingFlowPicFile)
                    doUploadJsonInfo();
                else if (!isUPloadFlowPicFile)
                    doUploadJsonInfo();
            } else {
                ToastUtil.showToast(this, "商品图片上传失败");
            }
        } else {
            ToastUtil.showToast(this, TextUtils.isEmpty(result.msg) ? "" : result.msg);
        }
    }

    private void doUploadJsonInfo() {
        try {
            JSONObject jsonObject = JSON.parseObject(mStringJson);
            jsonObject.put("explain", mmStringUploadFlow);
            mStringJson = jsonObject.toString();
            LogUtils.i("上传的内容=" + mStringJson);
            if (mPayType == Constant.Pay.Balance && mBalanceNotEnough && mAccountType == 0) {
                ToastUtil.showToast(this, "不足的部分将使用支付宝支付");
            }
            mPresenter.doPublish(mPayType, mStringJson.toString());
        } catch (Exception ex) {
            LogUtils.e(ex.getMessage());
        }
    }

    private void doReplaceIcon(String value) {
        try {
            JSONObject jsonObject = JSON.parseObject(mStringJson);
            jsonObject.put("picture", value);
            mStringJson = jsonObject.toString();
        } catch (Exception ex) {
            LogUtils.e(ex.getMessage());
        }
    }

    private void doReplaceFlow(ArrayList<String> data) {
        int count = 0;
        if (data != null && data.size() > count && !TextUtils.isEmpty(mStringFlow)) {
            Pattern p = Pattern.compile(mCommonPicPath);
            Matcher m = p.matcher(mStringFlow);
            while (m.find()) {
                if (!TextUtils.isEmpty((m.group())) && data.size() > count) {
                    String group = m.group();
                    String s = data.get(count);
                    mmStringUploadFlow = mmStringUploadFlow.replace(group, s);
                    count++;
                }
            }
        }
    }

    @Override
    public void onGetDataResult(WalletHttpEntity httpResult) {
        try {
            if (httpResult.code == Constant.HttpResult.SUCCEED) {
                Double usableSum = httpResult.account.usableSum;
                mTvBalanceValue.setText("(可用余额 ¥" + new DecimalFormat("#0.00").format(usableSum) + ")");
                if (mTotal != 0 && mTotal > usableSum) {
                    mBalanceNotEnough = true;
                } else
                    mBalanceNotEnough = false;
            } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
                ToastUtil.showToast(this, "登录超时,请重新登录");
                startActivityForResult(new Intent(this, LoginActivity.class), REQUESTCODE);
            } else {
                ToastUtil.showToast(this, TextUtils.isEmpty(httpResult.msg) ? "数据请求失败" : httpResult.msg);
            }
        } catch (Exception ex) {
            LogUtils.e(ex.getMessage());
        }
    }

    @Override
    public void onPublishResult(HttpResult<String> httpResult, int payType) {
        hideProgress();
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            if (payType == Constant.Pay.Balance) {
                if (httpResult.isArea == 1 && !TextUtils.isEmpty(httpResult.data)) {//混合支付后台控制
                    AliPay aliPay = new AliPay(PublishConfirmActivity.this);
                    aliPay.setPayCallBackListener(this);
                    aliPay.pay(httpResult.data);
                } else {//余额支付
                    ToastUtil.showToast(this, TextUtils.isEmpty(httpResult.msg) ? "发布成功" : httpResult.msg);
                    RxBus.getDefault().post(new WantedPublishEvent());
                    this.finish();
                }
            } else if (payType == Constant.Pay.AliPay) {//支付宝支付
                if (!TextUtils.isEmpty(httpResult.data)) {
                    AliPay aliPay = new AliPay(PublishConfirmActivity.this);
                    aliPay.setPayCallBackListener(this);
                    aliPay.pay(httpResult.data);
                }
            }
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录");
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            ToastUtil.showToast(this, TextUtils.isEmpty(httpResult.msg) ? "发布失败" : httpResult.msg);
        }
    }


    @Override
    public void onPayCallBack(int status, String resultStatus, String progress) {
        ToastUtil.showToast(PublishConfirmActivity.this, progress);
        if (status == 9000) {
            ToastUtil.showToast(this, "发布成功");
            RxBus.getDefault().post(new WantedPublishEvent());
            this.finish();
        } else if (status == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录");
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            ToastUtil.showToast(this, "支付失败");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUESTCODE:
                    mPresenter.getBalance();
                    break;
            }
        }
    }

    @Override
    public void showErrorMessage(String msg) {
        super.showErrorMessage(msg);
        hideProgress();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
