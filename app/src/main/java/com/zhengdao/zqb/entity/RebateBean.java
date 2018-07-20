package com.zhengdao.zqb.entity;

import com.qq.e.ads.nativ.NativeExpressADView;

/**
 * @Author lijiangop
 * @CreateTime 2018/6/8 15:36
 */
public class RebateBean {
    /**
     * "id": 2,                                                              //产品ID
     * "incomeTotal": 505.00,                                                //总收益
     * "interest": 125.00,                                                    //利息
     * "logo": "http://www.17wlg.com/upload/images/lc_bxjr.png",           //logo
     * "mark": "平台返现/3个工作日/首投",                                   //关键词
     * "rebate": "300",                                                     //平台加息/返利
     * "recommend": 1,                                                   //0-不推荐，1-推荐
     * "red": "80",                                                        //红包
     * "expectedAnnualized": 10000.00,                                   //预期年化
     * "term": 60,                                                        //产品期限
     * "termType": 1,                                                    //期限类型 1:天 2:月
     * "title": "投资60天/10000元",                                      //标题
     * "wzName": "宝象金融"                                            //产品名
     */
    public int type = 1;
    //head
    public transient NativeExpressADView nativeAd;

    //goods
    public int    id;
    public Double incomeTotal;
    public Double interest;
    public String logo;
    public String mark;
    public String rebate;
    public int    recommend;
    public int    goodsType;
    public String red;
    public Double expectedAnnualized;
    public int    term;
    public int    termType;
    public String title;
    public String wzName;

    public RebateBean(int type) {
        this.type = type;
    }

    public RebateBean(int type, NativeExpressADView nativeAd) {
        this.type = type;
        this.nativeAd = nativeAd;
    }
}
