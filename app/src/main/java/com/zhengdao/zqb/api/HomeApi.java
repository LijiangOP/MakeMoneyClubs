package com.zhengdao.zqb.api;

import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.AdvertisementHttpEntity;
import com.zhengdao.zqb.entity.AnnouncementDetailBean;
import com.zhengdao.zqb.entity.Coupons;
import com.zhengdao.zqb.entity.DictionaryEntity;
import com.zhengdao.zqb.entity.EarnEntity;
import com.zhengdao.zqb.entity.FindEntityHttpResult;
import com.zhengdao.zqb.entity.GoodsCommandHttpEntity;
import com.zhengdao.zqb.entity.HomeInfo;
import com.zhengdao.zqb.entity.HomeWantedDetailEntity;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.LessTimeHttpResult;
import com.zhengdao.zqb.entity.MarketCommentHttpEntity;
import com.zhengdao.zqb.entity.MessageEntity;
import com.zhengdao.zqb.entity.NewsHttpEntity;
import com.zhengdao.zqb.entity.RankingListEntity;
import com.zhengdao.zqb.entity.ScreenLoadEntity;
import com.zhengdao.zqb.entity.SurveyHttpResult;
import com.zhengdao.zqb.entity.WelfareHttpData;

import java.util.List;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * @Author lijiangop
 * @CreateTime 2017/12/29 11:03
 */
public interface HomeApi {

    /**
     * 获取首页信息
     *
     * @return
     */
    @GET(Constant.Url.BASEURL)
    Observable<HomeInfo> getData();


    /**
     * 赚钱大厅 请求类型一
     *
     * @param block     类别
     * @param pageNo    当前页码
     * @param sortName  排序字段(joincount =人气,money = 奖励 默认综合排序)
     * @param sortOrder 排序方式(正序 = asc,倒序 = desc)
     * @param classify  业务分类(字典)
     * @param category  悬赏类别(字典)
     * @param type      类型
     * @param title     标题（模糊搜索）
     * @return
     */
    @GET("reward/rewardSearch")
    Observable<EarnEntity> getEarnData(@Query("block") int block, @Query("pageNo") int pageNo, @Query("sortName") String sortName,
                                       @Query("sortOrder") String sortOrder, @Query("classify") int classify,
                                       @Query("category") int category, @Query("type") int type, @Query("title") String title);


    /**
     * 赚钱大厅 请求类型二
     *
     * @param pageNo
     * @param sortName
     * @return
     */
    @GET("reward/rewardSearch")
    Observable<EarnEntity> getEarnData1(@Query("pageNo") int pageNo, @Query("sortName") String sortName, @Query("sortOrder") String sortOrder, @Query("pagesize") int pagesize);

    /**
     * 筛选字典加载
     *
     * @return
     */
    @GET("reward/searchDataLoad")
    Observable<ScreenLoadEntity> getSelectedData();

    /**
     * 获取悬赏详情
     *
     * @param id
     * @return
     */
    @GET("reward/getRewardDetails")
    Observable<HomeWantedDetailEntity> getWantedDetail(@Query("token") String token, @Query("id") int id);

    /**
     * 悬赏管理查询悬赏详情
     *
     * @param id
     * @return
     */
    @GET("myReward/getMyRewardInfo")
    Observable<HomeWantedDetailEntity> getMyWantedDetail(@Query("token") String token, @Query("id") int id);

    /**
     * 市场评论列表
     *
     * @param token
     * @return
     */
    @GET("newbieTask/getMarketList")
    Observable<MarketCommentHttpEntity> getMarketComment(@Query("token") String token);

    /**
     * 获取悬赏详情的审核时间
     *
     * @param id
     * @return
     */
    @GET("dictionary/getTypesById")
    Observable<DictionaryEntity> getCheckTime(@Query("id") int id);

    /**
     * 添加收藏
     *
     * @param id
     * @return
     */
    @GET("userInfo/addFootprint")
    Observable<HttpResult> AddCollect(@Query("token") String token, @Query("rwId") int id, @Query("flag") String flag);

    /**
     * 添加关注，收藏，浏览
     *
     * @param id
     * @return
     */
    @GET("userInfo/addFootprint")
    Observable<HttpResult> AddACF(@Query("token") String token, @Query("rwUserId") int id, @Query("flag") String flag);

    /**
     * 取消关注
     *
     * @param fid
     * @return
     */
    @GET("userInfo/cancelFollow")
    Observable<HttpResult> CancleACF(@Query("token") String token, @Query("fid") int fid);

    /**
     * 领取悬赏
     *
     * @param id
     * @return
     */
    @GET("reward/receive")
    Observable<LessTimeHttpResult> GetWanted(@Query("token") String token, @Query("id") int id);

    /**
     * 获取消息
     *
     * @param token
     * @return
     */
    @GET("message/getMsg")
    Observable<MessageEntity> getMessage(@Query("pageNo") int pageNo, @Query("token") String token);

    /**
     * 提交悬赏
     *
     * @param uploadImages
     * @param jsons
     * @param token
     * @param id
     * @return
     */
    @FormUrlEncoded
    @POST("reward/submitReward")
    Observable<HttpResult> CommitWantedQVU(@Field("pics") List<String> uploadImages, @Field("taskInfos") String jsons,
                                           @Field("token") String token, @Field("id") int id);


    /**
     * 排行榜
     *
     * @param token
     * @return
     */
    @GET("find/ranking")
    Observable<RankingListEntity> getRankingList(@Query("token") String token, @Query("flag") String flag);


    /**
     * 获取新人奖励
     */
    @GET("popup")
    Observable<WelfareHttpData> getWelfareData();

    /**
     * 获取悬赏奖励
     */
    @GET("popup")
    Observable<WelfareHttpData> getRewardData();


    /**
     * 获取口令详情
     *
     * @param id
     * @return
     */
    @GET("reward/getRewardInfo")
    Observable<GoodsCommandHttpEntity> getGoodsCommand(@Query("rid") int id);


    /**
     * 提交市场评论
     *
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST("newbieTask/marketReview")
    Observable<HttpResult> commitMarketComment(@Field("mid") int mid, @Field("token") String token,
                                               @Field("imgUrl1") String url1, @Field("imgUrl2") String url2);


    /**
     * 公告详情
     *
     * @param id 公告id
     * @return
     */
    @GET("getNoticeDetail")
    Observable<HttpResult<AnnouncementDetailBean>> getNoticeDetail(@Query("id") int id);


    /**
     * 20.获取广告信息
     *
     * @return
     */
    @GET("advert/getAdvert")
    Observable<AdvertisementHttpEntity> getAdvInfo(@Query("address") int address);

    /**
     * 获取发现页面所需数据
     *
     * @return
     */
    @GET("find/getHotAppList")
    Observable<FindEntityHttpResult> getFindInfo(@Query("pageNo") int pageNo, @Query("pageSize") int pageSize);

    /**
     * 获取下载推荐应用的奖励
     *
     * @return
     */
    @FormUrlEncoded
    @POST("find/hotAppReward")
    Observable<HttpResult> getDownloadReward(@Field("token") String token, @Field("IMEI") String IMEI, @Field("appId") int appId);


    /**
     * 获取看点页面新闻数据
     *
     * @return
     */
    @GET("news/list")
    Observable<NewsHttpEntity> getNews(@Query("type") String type, @Query("pageNo") int pageNo);


    /**
     * 获取优惠券数据
     *
     * @param keyword
     * @param pageNo
     * @return
     */
    @GET("taobaoke/getTaoBaoList")
    Observable<HttpResult<Coupons>> getCouponsData(@Query("sortName") String sortName, @Query("sortOrder") String sortOrder,
                                                   @Query("goodsName") String keyword, @Query("type") int type,
                                                   @Query("pageNo") int pageNo);

    /**
     * 获取优惠券数据
     *
     * @param goodsName
     * @param pageNo
     * @return
     */
    @GET("taobaoke/getTaoBaoList")
    Observable<HttpResult<Coupons>> getCouponsData(@Query("goodsName") String goodsName, @Query("pageNo") int pageNo);

    /**
     * 获取首页问卷调查链接
     *
     * @return
     */
    @GET("questionnaire/goQuestionnairePage")
    Observable<SurveyHttpResult> getSurveyLink(@Query("token") String token);
}
