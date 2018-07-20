package com.zhengdao.zqb.entity;

import java.util.ArrayList;

/**
 * @Author lijiangop
 * @CreateTime 2018/1/29 16:09
 */
public class ScreenLoadEntity {

    public ArrayList<ScreenLoadDetailEntity> businessType;
    public int                               code;
    public String                            msg;
    public ArrayList<ScreenLoadDetailEntity> rewardType;

    public class ScreenLoadDetailEntity {
        public int    id;
        public String key;
        public String value;
    }
}
