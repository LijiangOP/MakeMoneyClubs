package com.zhengdao.zqb.entity;

/**
 * @Author lijiangop
 * @CreateTime 2018/7/31 11:16
 */
public class MenusBean {
    /**
     * "id": 104,
     * "key": "MENU_INFO",
     * "option1": 0,
     * "option2": "",    //链接
     * "option3": "",
     * "state": 0,       //赚钱吧 开关 0启动 1关闭
     * "switchs": 0,       //兼职呗 开关 0启动 1关闭
     * "value": "0元赚"       //菜单名
     * "type": 0,//按钮类型  跳转类型 0.H5 1. 0元赚 2.闲玩游戏 3.每日任务 4.问卷调查
     * "icon": "";//按钮图标
     */
    public int    id;
    public String key;
    public String option1;
    public String option2;
    public String option3;
    public int    state;
    public int    switchs;
    public String value;
    public int    type;
    public String icon;
}
