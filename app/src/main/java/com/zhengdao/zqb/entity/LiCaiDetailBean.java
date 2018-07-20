package com.zhengdao.zqb.entity;

/**
 * @Author lijiangop
 * @CreateTime 2017/10/9 15:41
 */
public class LiCaiDetailBean {
    /**
     * "id": 2,										//产品ID
     * "title": "投资一个月标5000元，可获得105元",		//标题
     * "wzName": "余易贷",							//产品名
     * "insideLogo": "http://localhost:8088/flwsm/upload/images/1(2).png",	//内部logo
     * "incomeTotal": 500,							//总收益
     * "interest": 200,								//利息 加息返现的数据+1
     * "term": 20,									//产品期限(工作日)
     * "red": "0",									//红包
     * "url": "https://m.yuyid.com/a/fanli.html?channel_id=ZD8838",	//跳转链接
     * "rebate": "20",								//平台加息/返利 加息返现的数据+1
     * "expectedAnnualized": 20,						//预期年化
     * "abstractStr": "余易贷于2014年10月20日正式上线... //平台简介
     * "activity": "返现金：（立即返现至余易贷账户余额）... //活动说明
     * "reminder": "本次活动不限制投资次数，金额符合.....  //特别提醒
     * "riskWarning": "理财有风险，投资需谨慎。投资....	    //风险提示
     * "marks": [								    //简述
     * "平台返息",
     * "三个工作日返息",
     * "首投"
     * ]
     */
    public int    id;
    public String title;
    public String wzName;
    public String logo;
    public Double incomeTotal;
    public Double interest;
    public Double startingAmount;
    public int    termType;  //期限类型 1天 2月
    public int    term;  //产品期限(工作日)
    public int skipType = 1;  //1 app内跳转,2 浏览器跳转
    public String   red;
    public String   url;
    public String   rebate;
    public Double   expectedAnnualized;
    public String   abstractStr;
    public String   activity;
    public String   reminder;
    public String   riskWarning;
    public String   mark;
    public String[] marks;
}
