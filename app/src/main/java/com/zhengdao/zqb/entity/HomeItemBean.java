package com.zhengdao.zqb.entity;

import android.widget.TextView;

import java.util.List;

/**
 * @Author lijiangop
 * @CreateTime 2017/8/18 15:16
 */
public class HomeItemBean {

    //类型
    public int                       type;
    //头部;type=1
    public List<BannerBean>          bannerList; //banner
    public List<MenusBean>           menus;     //banner下方导航菜单
    public List<AnnouncementBean>    notice;   //任务快报(数据)
    public List<TextView>            marqueeList; //任务快报(展示)
    public HomeInfo.InvitationBanner invitationBanner; //邀请图片
    //商品;type=2
    public String                    createTime;
    public int                       id;
    public int                       joincount;
    public int                       isOwn;
    public Double                    money;
    public String                    keyword;
    public String                    picture;
    public String                    title;
    public String                    discount;
    public int                       goodsType;
    public long                      lowerTime;
    public String                      name;
}
