package com.zhengdao.zqb.entity;

/**
 * @Author lijiangop
 * @CreateTime 2018/1/19 15:58
 */
public class SelectedEntity {
    public int          code;
    public String       msg;
    public BusinessType businessType;
    public RewardType   rewardType;

    private class BusinessType {
        public int    id;
        public String value;
    }

    private class RewardType {
        public int    id;
        public String value;
    }
}
