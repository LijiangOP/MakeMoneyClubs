package com.zhengdao.zqb.entity;

/**
 * @Author lijiangop
 * @CreateTime 2018/4/28 14:53
 */
public class SynthesizeEntity {
    public String  desc;
    public boolean isSelected;

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public SynthesizeEntity(String desc, boolean isSelected) {
        this.desc = desc;
        this.isSelected = isSelected;
    }
}
