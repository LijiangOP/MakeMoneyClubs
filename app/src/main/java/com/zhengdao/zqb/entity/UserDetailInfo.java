package com.zhengdao.zqb.entity;

import java.io.Serializable;

/**
 * Created by LiJiangOP on 2017/8/29.
 */

public class UserDetailInfo implements Serializable {
    public int    sex;//0男。1女。-1保密
    public int    id;
    public String birthday;
    public String qq;
    public String zfb;
    public String realName;
    public String address;

    @Override
    public String
    toString() {
        return "UserDetailInfo{" +
                "sex=" + sex +
                ", birthday='" + birthday + '\'' +
                ", qq='" + qq + '\'' +
                ", zfb='" + zfb + '\'' +
                ", realName='" + realName + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
