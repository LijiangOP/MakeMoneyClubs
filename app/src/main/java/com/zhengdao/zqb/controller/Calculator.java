package com.zhengdao.zqb.controller;

import java.math.BigDecimal;

public class Calculator {
	/**
	 * 获取总收益
	 * @param tenderAmount 投资本金
	 * @param annualizedRate 基础年化率
	 * @param coupon 加息券
	 * @param cycle 投资周期（天）
	 * @param red 红包
	 * @param rebate 返现
	 * */
	//总收益=投资本金×（基础年化率%+加息券%）×投资周期（天）/360+红包+返利
	public static BigDecimal totalIncome(double tenderAmount,double annualizedRate,double coupon,Integer cycle,double red,double rebate) {
		return Arith.add(Arith.div(Arith.mul((Arith.mul(tenderAmount, (Arith.add(annualizedRate, coupon).doubleValue())).doubleValue()), cycle.doubleValue() ).doubleValue(), 360d, 2).doubleValue(), Arith.add(red, rebate).doubleValue());
	}
	//年化率=总收益/投资周期（天）×360/投资本金
	public static BigDecimal annualizedRate(double totalIncome,Integer cycle,double tenderAmount) {
		return Arith.div(Arith.mul(Arith.div(totalIncome, cycle.doubleValue()).doubleValue(), 360).doubleValue(), tenderAmount, 4);
	}
}
