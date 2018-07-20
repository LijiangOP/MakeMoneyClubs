package com.zhengdao.zqb.entity;

/**
 * @Author lijiangop
 * @CreateTime 2018/5/10 17:25
 */
public class AdvertiseMentEntity {
    //type  跳转类型 0-跳转URL 1-新手任务 2-赚钱大厅 3-邀请好友 4-浏览器加载 5-悬赏详情 6-网盟广告
    //address  显示位置 0-app首页 1-微站 2-邀请好友 3-活动弹框 4-发现 5-我的 6-账户中心

    /**
     * "id": 1,                                   //广告ID
     * "imgPath": "123.jpg",                     //广告图片
     * "title": "test",                             //广告标题
     * "type": 0,                                //广告类型
     * “address”:0,                           //广告显示位置
     * "url": "11123",                           //广告链接
     * “isDel”:0,                              //是否有效 0有效 1无效
     */
    public int    id;
    public String imgPath;
    public String url;
    public String title;
    public int    type;
    public int    address;
    public int    isDel;
}
