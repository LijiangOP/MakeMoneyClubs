package com.zhengdao.zqb.utils.update;

/**
 * @创建者 cairui
 * @创建时间 2017/2/16 16:11
 */
public interface DownloadProgressListener {
    void update(long bytesRead, long contentLength, boolean done);
}
