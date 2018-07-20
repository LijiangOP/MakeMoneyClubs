package com.zhengdao.zqb.entity;

import java.util.ArrayList;

/**
 * @Author lijiangop
 * @CreateTime 2018/4/27 09:13
 */
public class CustomHttpEntity {
    /**
     * {"reward":[],"account":[],"code":0,"like":[],"user":[],"msg":"操作成功"}
     */
    public int                        code;
    public String                     msg;
    public ArrayList<DictionaryValue> reward;
    public ArrayList<DictionaryValue> account;
    public ArrayList<DictionaryValue> like;
    public ArrayList<DictionaryValue> user;
}
