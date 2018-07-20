package com.zhengdao.zqb.entity;

/**
 * @Author lijiangop
 * @CreateTime 2017/7/19 10:37
 */
public class TabBean {
    public Class fragment;
    public int   title;
    public int   icon;

    public TabBean() {

    }

    public TabBean(Class fragment, int title, int icon) {
        this.fragment = fragment;
        this.title = title;
        this.icon = icon;
    }
}
