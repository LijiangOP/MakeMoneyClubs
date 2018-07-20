package com.zhengdao.zqb.entity;

import java.util.ArrayList;

/**
 * @Author lijiangop
 * @CreateTime 2018/1/15 17:49
 */
public class SnatchRedPackeEntity {

    public boolean                               isHasNext;
    public ArrayList<SnatchRedPackeDetailEntity> result;

    public class SnatchRedPackeDetailEntity {

        public String imgPath;
        public String name;
        public String time;
        public String number;
    }
}
