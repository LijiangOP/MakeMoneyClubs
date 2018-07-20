package com.zhengdao.zqb.view.activity.calculator;


import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.controller.Calculator;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.port.SimpleTextWatcher;
import com.zhengdao.zqb.utils.LogUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CalculatorActivity extends MVPBaseActivity<CalculatorContract.View, CalculatorPresenter> implements CalculatorContract.View {

    @BindView(R.id.tv_nianhualv)
    TextView mTvNianhualv;
    @BindView(R.id.tv_total_earnings)
    TextView mTvTotalEarnings;
    @BindView(R.id.tv_total_benxi)
    TextView mTvTotalBenxi;
    @BindView(R.id.et_invest_number)
    EditText mEtInvestNumber;
    @BindView(R.id.et_invest_cycle)
    EditText mEtInvestCycle;
    @BindView(R.id.et_nianhualv)
    EditText mEtNianhualv;
    @BindView(R.id.et_rebate_number)
    EditText mEtRebateNumber;
    @BindView(R.id.et_red_packet)
    EditText mEtRedPacket;
    @BindView(R.id.et_jiaxiquan)
    EditText mEtJiaxiquan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        ButterKnife.bind(this);
        setTitle("投资计算器");
        initClicListener();
    }

    private void initClicListener() {
        mEtNianhualv.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        mEtRebateNumber.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        mEtInvestNumber.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                doCount();
            }
        });
        mEtInvestCycle.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                doCount();
            }
        });
        mEtNianhualv.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                doCount();
            }
        });
        mEtRebateNumber.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                doCount();
            }
        });
        mEtRedPacket.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                doCount();
            }
        });
        mEtJiaxiquan.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                doCount();
            }
        });
    }

    private void doCount() {
        String value1 = mEtInvestNumber.getText().toString().trim();    //投资金额
        String value2 = mEtInvestCycle.getText().toString().trim();     //投资周期
        String value3 = mEtNianhualv.getText().toString().trim();       //年化率
        String value4 = mEtRebateNumber.getText().toString().trim();    //返利金额
        String value5 = mEtRedPacket.getText().toString().trim();       //红包
        String value6 = mEtJiaxiquan.getText().toString().trim();       //加息
        if (TextUtils.isEmpty(value1))
            return;
        if (TextUtils.isEmpty(value2))
            return;
        if (TextUtils.isEmpty(value3))
            return;
        try {
            double tenderAmount = Double.valueOf(value1);  //投资金额
            Integer cycle = Integer.valueOf(value2);   //投资周期
            double annualizedRate = Double.valueOf(value3) / 100;  //年化率
            double rebate = TextUtils.isEmpty(value4) ? 0 : Double.valueOf(value4);  //返利金额
            double redPacket = TextUtils.isEmpty(value5) ? 0 : Double.valueOf(value5);  //红包
            double coupon = TextUtils.isEmpty(value6) ? 0 : Double.valueOf(value6) / 100;  //加息

            BigDecimal totalEarnings = Calculator.totalIncome(tenderAmount, annualizedRate,
                    coupon, cycle, redPacket, rebate);
            mTvTotalEarnings.setText("" + new DecimalFormat("#0.00").format(totalEarnings));

            mTvTotalBenxi.setText("" + new DecimalFormat("#0.00").format(totalEarnings.add(new BigDecimal(value1))));

            BigDecimal bigDecimal = Calculator.annualizedRate(totalEarnings.doubleValue(), cycle, tenderAmount);
            BigDecimal multiply = bigDecimal.multiply(BigDecimal.valueOf(100));
            mTvNianhualv.setText(new DecimalFormat("#.00").format(multiply));
        } catch (Exception exception) {
            LogUtils.e(exception.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
