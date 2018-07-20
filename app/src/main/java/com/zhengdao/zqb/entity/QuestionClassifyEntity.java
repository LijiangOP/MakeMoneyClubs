package com.zhengdao.zqb.entity;

/**
 * @Author lijiangop
 * @CreateTime 2018/4/27 11:00
 */
public class QuestionClassifyEntity {
    public int    type;
    public String title;
    public String desc;
    public boolean isShowDesc = false;

    public void setShowDesc(boolean showDesc) {
        isShowDesc = showDesc;
    }

    public QuestionClassifyEntity(int type, String title, String desc) {
        this.type = type;
        this.title = title;
        this.desc = desc;
    }


}
