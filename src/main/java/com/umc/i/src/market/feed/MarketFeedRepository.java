package com.umc.i.src.market.feed;

import com.umc.i.src.market.feed.model.GetMarketFeedRes;
import com.umc.i.src.market.feed.model.GetMarketFeedReq;

import java.util.List;

public interface MarketFeedRepository {

    public int postNewFeed(GetMarketFeedReq marketFeed);

    public int postFeedImages(List<String> filesUrlList, int marketIdx);

    public List<GetMarketFeedRes> getFeedByCategory(String category, int userIdx, String soldout, int page);

    public List<GetMarketFeedRes> getFeedByMarketIdx(String marketIdx, String memIdx);

    public void updateFeedHitCount(String marketIdx);

    public void updateFeed(String marketIdx, GetMarketFeedReq req);

    void updateFeedSoldout(String marketIdx, GetMarketFeedReq req);

    public void deleteFeed(String marketIdx);

    public void deleteImages(int marketIdx);

    public void feedLike(int userIdx, int marketIdx, String isLike, int feedUserIdx);

    public List<GetMarketFeedRes> getFeedByUserIdx(int userIdx);

    public int getFeedUserIdx(String marketIdx);

    public void postCoverImage(List<String> filesUrlList, String marketIdx);
}
