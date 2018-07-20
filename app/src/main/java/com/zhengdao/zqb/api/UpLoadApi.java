package com.zhengdao.zqb.api;

import com.zhengdao.zqb.entity.HttpResult;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import rx.Observable;

/**
 * @Author lijiangop
 * @CreateTime 2018/1/25 10:22
 */
public interface UpLoadApi {

    /**
     * 上传图片
     *
     * @param file
     * @return
     */
    @Multipart
    @POST("upImage/uploadImgs")
    Observable<HttpResult<ArrayList<String>>> uploadImages(@Part("type") RequestBody type, @PartMap Map<String, RequestBody> file);
}
