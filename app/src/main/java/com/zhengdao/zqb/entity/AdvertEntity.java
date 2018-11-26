package com.zhengdao.zqb.entity;

/**
 * @Author lijiangop
 * @CreateTime 2018/10/24 0024 11:26
 */
public class AdvertEntity {
    private String advId;
    private int    type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAdvId() {
        return advId;
    }

    public void setAdvId(String advId) {
        this.advId = advId;
    }

    public AdvertEntity(int i, String advId) {
        this.type = i;
        this.advId = advId;
    }
}
