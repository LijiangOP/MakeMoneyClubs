package com.zhengdao.zqb.entity;

/**
 * @创建者 cairui
 * @创建时间 2016/12/8 09:30
 * @描述 ${des}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @版本 $Revision$
 * @更新描述 ${des}
 */
public class HttpResult<T> {
    //基本
    public String  msg;
    public int     code;
    public T       data;
    //额外
    public int     state;//1关闭返利 0开启返利
    public String  time;
    public Double  money;
    public int     isArea;
    public String  token;
    public Account account;

    public class Account {
        public String usableSum;
        public double integral;
    }

    @Override
    public String toString() {
        return "HttpResult{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", isArea=" + isArea +
                ", data=" + data +
                ", account=" + account +
                '}';
    }
}
