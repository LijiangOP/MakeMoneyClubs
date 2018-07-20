package com.zhengdao.zqb.entity;

/**
 * @Author lijiangop
 * @CreateTime 2018/6/11 14:45
 */
public class NewBalanceEntity {
    public int                 type;
    public BalanceDetailEntity entity;
    public String              date;

    public NewBalanceEntity(int type, BalanceDetailEntity entity) {
        this.type = type;
        this.entity = entity;
    }

    public NewBalanceEntity(int type, String date) {
        this.type = type;
        this.date = date;
    }

    @Override
    public String toString() {
        return "NewBalanceEntity{" +
                "type=" + type +
                ", entity=" + entity +
                ", date='" + date + '\'' +
                '}';
    }
}
