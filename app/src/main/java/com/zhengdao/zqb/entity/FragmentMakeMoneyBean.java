package com.zhengdao.zqb.entity;

import java.util.ArrayList;

/**
 * @Author lijiangop
 * @CreateTime 2017/12/29 15:45
 */
public class FragmentMakeMoneyBean {
    public int                       pageNo; //当前页1
    public int                       pageSize; //分页大小
    public boolean                   isHasNext; //是否有下一页true（有）
    public int                       nextPage; //下一页
    public int                       totalPages; //总页数
    public ArrayList<HomeItemEntity> result;
}
