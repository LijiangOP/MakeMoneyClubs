package com.zhengdao.zqb.event;

/**
 * @Author lijiangop
 * @CreateTime 2018/6/29 17:48
 */
public class IsShowProgressEvent {
    public boolean isShow;
    public float   progress;

    public IsShowProgressEvent(boolean isShow) {
        this.isShow = isShow;
    }

    public IsShowProgressEvent(boolean isShow, float progress) {
        this.isShow = isShow;
        this.progress = progress;
    }
}
