package com.zhengdao.zqb.event;

/**
 * @Author lijiangop
 * @CreateTime 2018/5/16 16:54
 */
public class DownLoadEvent {
    public int    id;
    public int    position;
    public int    state;//0:成功 1:失败
    public String packageName;

    public DownLoadEvent(int id, int position, int state, String packageName) {
        this.id = id;
        this.position = position;
        this.state = state;
        this.packageName = packageName;
    }
}
