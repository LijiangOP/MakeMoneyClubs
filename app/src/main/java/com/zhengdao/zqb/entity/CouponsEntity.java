package com.zhengdao.zqb.entity;

/**
 * @Author lijiangop
 * @CreateTime 2018/6/14 17:22
 */
public class CouponsEntity {
    /**
     * "clickUrl": "https://s.click.taobao.com",            //淘宝客链接
     * "commission": 9.16,                              //佣金
     * "couponId": "7d63c702d9424fa4b",               //优惠券id
     * "couponQuota": "满250元减20元",              //优惠券面额
     * "couponSurplus": 5100,                          //优惠券剩余量
     * "couponTotal": 5100,                            //优惠券总量
     * "couponUrl": "https://taoquan.taobao.",          //优惠券链接
     * "endTime": "2017-12-06 00:00:00",              //优惠券结束时间
     * "extensionUrl": "",                              //商品优惠券推广链接
     * "goodsDetails": "http://item.taobao.com",       //商品详情页
     * "goodsId": 13135463772,                      //商品ID
     * "goodsName": "阿芙薰衣草茶树精油祛痘套装,    //商品名称
     * "goodsPic": "http://img.alicdn.com/bao",       //商品主图
     * "id": 1,
     * "discount": 1,                                //折后价
     * "incomeRatio": 4,                            //收入比率
     * "oneType": "美容护肤/美体/精油",             //一级类目
     * "platformType": "天猫",                     //平台类型
     * "price": 229.00,                            //商品价格
     * "sales": 2098,                             //商品月销量
     * "sellerId": "379250310",                   //卖家id
     * "sellerWW": "阿芙官方旗舰店",             //卖家旺旺
     * "shopName": "阿芙官方旗舰店",           //店铺名称
     * "startTime": "2017-12-06 00:00:00"        //优惠券开始时间
     */
    public String clickUrl;
    public Double commission;
    public String couponId;
    public String couponQuota;
    public long   couponSurplus;
    public long   couponTotal;
    public String couponUrl;
    public String endTime;
    public String extensionUrl;
    public String goodsDetails;
    public long   goodsId;
    public String goodsName;
    public String goodsPic;
    public long   id;
    public Double discount;
    public Double incomeRatio;
    public String oneType;
    public String platformType;
    public Double price;
    public int    sales;
    public String sellerId;
    public String sellerWW;
    public String shopName;
    public String startTime;

    public int type;//0 商品;1 头部;2 无数据;

    public CouponsEntity(int type) {
        this.type = type;
    }
}
