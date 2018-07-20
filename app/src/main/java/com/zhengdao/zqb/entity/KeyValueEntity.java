package com.zhengdao.zqb.entity;

/**
 * @Author lijiangop
 * @CreateTime 2018/5/8 09:57
 */
public class KeyValueEntity {
    public int block;
    public int state;

    public KeyValueEntity(int block, int state) {
        this.block = block;
        this.state = state;
    }

    @Override
    public String toString() {
        return "KeyValueEntity{" +
                "block=" + block +
                ", state=" + state +
                '}';
    }
}
