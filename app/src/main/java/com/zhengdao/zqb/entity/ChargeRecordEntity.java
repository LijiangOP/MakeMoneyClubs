package com.zhengdao.zqb.entity;

import java.util.ArrayList;

/**
 * @Author lijiangop
 * @CreateTime 2018/1/9 16:27
 */
public class ChargeRecordEntity {
    public boolean                         hasNextPage;
    public int                             total;
    public ArrayList<ChargeRecordDetailEntity> list;

    public class ChargeRecordDetailEntity {
        public String addTime;
        public Double fee;
        public int    id;
        public Double money;
        public String orderNo;
        public String orderNum;
        public int    payState;
        public int    payType;
        public int    type;
        public int    userId;
    }
}
