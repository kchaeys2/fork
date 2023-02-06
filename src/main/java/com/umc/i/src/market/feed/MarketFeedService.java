package com.umc.i.src.market.feed;

import com.umc.i.src.market.feed.model.GetMarketFeedRes;
import com.umc.i.src.market.feed.model.GetMarketFeedReq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MarketFeedService {

    private final MarketFeedDao marketFeedDao;

    public int postNewFeed(GetMarketFeedReq feed) {
        return marketFeedDao.postNewFeed(feed);
    }

    public int postFeedImages(List<String> filesUrlList, int marketIdx) {
        return marketFeedDao.postFeedImages(filesUrlList, marketIdx);
    }

    public void updateFeed(String marketIdx, GetMarketFeedReq feed) {
        marketFeedDao.updateFeed(marketIdx, feed);
    }

    public void updateFeedSoldout(String marketIdx, GetMarketFeedReq feed) {
        marketFeedDao.updateFeedSoldout(marketIdx, feed);
    }

    public List<GetMarketFeedRes> getFeedByCategory(String category, int userIdx, String soldout, int page) {
        return marketFeedDao.getFeedByCategory(category, userIdx, soldout, page);
    }

    public List<GetMarketFeedRes> getFeedByMarketIdx(String marketIdx, String memIdx) {
        return marketFeedDao.getFeedByMarketIdx(marketIdx, memIdx);
    }

    public void updateMarketFeedHitCount(String marketIdx) {
        marketFeedDao.updateFeedHitCount(marketIdx);
    }

    public void deleteFeed(String marketIdx) {
        marketFeedDao.deleteFeed(marketIdx);
    }

    public void deleteImages(int marketIdx) {
        marketFeedDao.deleteImages(marketIdx);
    }

    public void feedLike(int userIdx, int marketIdx, String isLike, int feedUserIdx) {
        marketFeedDao.feedLike(userIdx, marketIdx, isLike, feedUserIdx);
    }

    public List<GetMarketFeedRes> getFeedByUserIdx(int userIdx) {
        List<GetMarketFeedRes> result = marketFeedDao.getFeedByUserIdx(userIdx);
        return result;
    }

    public int getFeedUserIdx(String marketIdx) {
        int feedUserIdx = marketFeedDao.getFeedUserIdx(marketIdx);
        return feedUserIdx;
    }

    public void postCoverImage(List<String> filesUrlList, String marketIdx) {
        marketFeedDao.postCoverImage(filesUrlList, marketIdx);
    }
}
