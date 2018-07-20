package com.zhengdao.zqb.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author lijiangop
 * @CreateTime 2018/5/7 18:53
 */
public class HomeDictionnaryEntity {
    public int    id;
    public String key;
    public String option1;
    public String option2;
    public String option3;
    public String value;
    public List<HomeItemEntity> rewardList = new ArrayList<>();
}
