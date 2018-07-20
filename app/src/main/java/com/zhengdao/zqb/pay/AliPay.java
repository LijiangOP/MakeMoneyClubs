package com.zhengdao.zqb.pay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;
import com.zhengdao.zqb.customview.CustomProgressDialog;

import java.util.Map;

public class AliPay {

    private Activity             mActivity;
    private CustomProgressDialog mDialog;

    public AliPay(Activity activity) {
        this.mActivity = activity;
        mDialog = new CustomProgressDialog(activity);
        mDialog.setMessage("正在获取订单信息，请稍后");
        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (mDialog != null) {
                    mDialog.dismiss();
                }
            }
        });
    }

    private int SDK_PAY_FLAG = 6406;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 6406: {
                    AliPayResult aliPayResult = new AliPayResult((Map<String, String>) msg.obj);
                    /**
                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     */
                    String resultInfo = aliPayResult.getResult();// 同步返回需要验证的信息

                    String resultStatus = aliPayResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        if (mPayCallBackListener != null) {
                            mPayCallBackListener.onPayCallBack(9000, "9000", "支付成功");
                        }
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            if (mPayCallBackListener != null) {
                                mPayCallBackListener.onPayCallBack(8000, "8000", "支付结果确认中");
                            }

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            if (mPayCallBackListener != null) {
                                mPayCallBackListener.onPayCallBack(0, "0", "支付失败");
                            }
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };


    public void pay(final String payInfo) {
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(mActivity);
                // 调用支付接口，获取支付结果
                Map<String, String> result = alipay.payV2(payInfo, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();

    }

    public void setPayCallBackListener(PayCallBackListener listener) {
        this.mPayCallBackListener = listener;
    }

    private PayCallBackListener mPayCallBackListener;

    public interface PayCallBackListener {
        void onPayCallBack(int status, String resultStatus, String progress);
    }

}
