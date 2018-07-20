package com.zhengdao.zqb.entity;

import java.util.ArrayList;

/**
 * @Author lijiangop
 * @CreateTime 2018/2/1 18:00
 */
public class IntegralEntity {
    public boolean                        hasNextPage;
    public int                            total;
    public int                            integral;
    public ArrayList<IntegralDetailEntity> list;

    public class IntegralDetailEntity {
        public String createTime;
        public String typeState;
        public Double integral;
        public String logDesc;
        public Double outAmount;
        public Double usableSum;
    }
}
