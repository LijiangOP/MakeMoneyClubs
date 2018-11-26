package com.zhengdao.zqb.entity;

/**
 * @Author lijiangop
 * @CreateTime 2017/12/29 11:22
 */
public class HomeItemEntity {

    /**
     * "createTime": "2017-12-29 10:43:51",    //创建时间
     * "id": 5,                                 //悬赏ID
     * "joincount": 5,                          //已参与人数
     * "keyWords": [72小时,限首投],             //关键词
     * "money": 5,                             //悬赏金额
     * "picture": "测试数据2.jpg",               //悬赏图片
     * "title": "测试数据5",                      //标题
     * “isOwn”0                        //是否自营 0-否 1-是
     * “lowerFrameTime”：”2018-04-01 14:13:25”//下架时间
     * “discount”:”满199-50”         //优惠额度
     */
    public String createTime;
    public int    id;
    public int    block;
    public int    joincount;
    public int    isOwn;
    public int    type;  //默认0;1新品
    public String keyword;
    public Double money;
    public String picture;
    public String title;
    public String lowerFrameTime;
    public long   lowerTime;
    public String discount;
    public String option3;
    public String name;
}