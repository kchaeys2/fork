package com.umc.i.src.market.feed;

import com.umc.i.config.BaseResponse;
import com.umc.i.config.BaseResponseStatus;
import com.umc.i.config.Constant;
import com.umc.i.src.market.feed.model.GetMarketFeedRes;
import com.umc.i.src.market.feed.model.MarketFeed;
import com.umc.i.src.market.feed.model.GetMarketFeedReq;
import com.umc.i.src.market.feed.model.PostMarketFeedRes;
import com.umc.i.utils.S3Storage.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MarketFeedController {

    private final MarketFeedService marketFeedService;

    private final S3Uploader s3Uploader;

//    private final JwtServiceImpl jwtService;

    @PostMapping("/market")
    public BaseResponse<PostMarketFeedRes> postNewsFeed(@RequestHeader Map<String, String> headers,
                                                        @RequestPart(name = "request") GetMarketFeedReq marketFeedReq,
                                                        @RequestPart(required = false, name = "images") List<MultipartFile> files) {
        /**
         * TODO
         * jwt access token으로 사용자 판별
         * access token 이 만료일 경우 refresh token 확인
         * refresh 가 만료일 경우 로그아웃
         * refresh 가 사용 가능하면 access token 재발급 한 뒤 게시물 post 진행, 반환시 새로운 access token 첨부
         */
//
//        String accessToken = headers.get("auth-access");
//        log.info("accessToken={}", accessToken);
//        Long memberId = Long.valueOf(jwtService.getMemberId(accessToken));
//        log.info("memberId={}", memberId);
//        marketFeedReq.setUserId(memberId);

        int marketIdx = marketFeedService.postNewFeed(marketFeedReq);
        log.info("markedIdx={}", marketIdx);

        if (marketIdx == -1) {
            return new BaseResponse<>(BaseResponseStatus.POST_MARKET_FEED_FAILED);
        }

        List<String> filesUrlList = s3Uploader.upload(files);
        log.info("fileUrlList={}", filesUrlList);

        marketFeedService.postFeedImages(filesUrlList, marketIdx);
        marketFeedService.postCoverImage(filesUrlList, String.valueOf(marketIdx));

        return new BaseResponse<>(new PostMarketFeedRes(marketFeedReq.getUserId()));
    }

    @GetMapping("/market/latest")
    public BaseResponse<GetMarketFeedRes> getNewsFeed(@RequestParam String category,
                                                      @RequestParam(defaultValue = "0") String soldout,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestBody GetMarketFeedReq req) throws RuntimeException {
        // query string 값 오류
        String[] marketGoodCategories = Constant.MARKET_GOOD_CATEGORIES;
        String[] booleans = Constant.BOOLEANS;
        int userIdx = req.getUserId();

        log.info("category={}", category);
        log.info("soldout={}", soldout);
        log.info("userIdx={}", userIdx);
        log.info("isValidCategory={}", Arrays.asList(marketGoodCategories).contains(category));

        if (!Arrays.asList(marketGoodCategories).contains(category) || !Arrays.asList(booleans).contains(soldout)) {
            return new BaseResponse<>(BaseResponseStatus.GET_MARKET_FEED_BY_PARAM_FAILED);
        }

        List<GetMarketFeedRes> feedByCategory = marketFeedService.getFeedByCategory(category, userIdx, soldout, page);
        log.info("feedByCategory={}", feedByCategory);

        return new BaseResponse<>(feedByCategory);
    }

    @GetMapping("/market/detail")
    public BaseResponse<GetMarketFeedRes> getFeedDetail(@RequestParam String marketIdx,
                                                  @RequestBody String memIdx) {

        List<GetMarketFeedRes> result = marketFeedService.getFeedByMarketIdx(marketIdx, memIdx);
        marketFeedService.updateMarketFeedHitCount(marketIdx);

        return new BaseResponse<>(result);
    }

    @PutMapping("/market/edit")
    public BaseResponse updateFeedDetail(@RequestParam String marketIdx,
                                         @RequestPart("images") List<MultipartFile> multipartFiles,
                                         @RequestPart("request") GetMarketFeedReq marketFeedReq) {

        int feedUserIdx = marketFeedService.getFeedUserIdx(marketIdx);
        if (marketFeedReq.getUserId() != feedUserIdx) {
            return new BaseResponse<>(BaseResponseStatus.FEED_UNAUTHORIZED);
        }

        marketFeedService.updateFeed(marketIdx, marketFeedReq);
        marketFeedService.deleteImages(Integer.parseInt(marketIdx));

        List<String> filesUrlList = s3Uploader.upload(multipartFiles);
        log.info("fileUrlList={}", filesUrlList);

        int uploadedImagesCnt = marketFeedService.postFeedImages(filesUrlList, Integer.parseInt(marketIdx));
        marketFeedService.postCoverImage(filesUrlList, marketIdx);

        return new BaseResponse<>(marketFeedReq.getUserId());
    }

    @DeleteMapping("/market/delete")
    public BaseResponse deleteFeed(@RequestParam String marketIdx,
                                   @RequestBody GetMarketFeedReq marketFeedReq) {

        int feedUserIdx = marketFeedService.getFeedUserIdx(marketIdx);
        if (marketFeedReq.getUserId() != feedUserIdx) {
            return new BaseResponse<>(BaseResponseStatus.FEED_UNAUTHORIZED);
        }

        marketFeedService.deleteFeed(marketIdx);
        marketFeedService.deleteImages(Integer.parseInt(marketIdx));

        return new BaseResponse<>();
    }

    @PutMapping("/market/soldout")
    public BaseResponse updateFeedSoldoutStatus(@RequestParam String marketIdx,
                                                @RequestBody GetMarketFeedReq marketFeedReq) {

        int feedUserIdx = marketFeedService.getFeedUserIdx(marketIdx);
        if (marketFeedReq.getUserId() != feedUserIdx) {
            return new BaseResponse<>(BaseResponseStatus.FEED_UNAUTHORIZED);
        }

        marketFeedService.updateFeed(marketIdx, marketFeedReq);

        return new BaseResponse<>();
    }

    @PostMapping("/market/like")
    public BaseResponse feedLikeByMember(@RequestBody MarketFeed feed) {

        marketFeedService.feedLike(feed.getUserIdx(), feed.getMarketIdx());

        return new BaseResponse<>();
    }

    @GetMapping("/market/list")
    public BaseResponse getFeedByUserIdx(@RequestParam int userIdx) {

        List<GetMarketFeedRes> result = marketFeedService.getFeedByUserIdx(userIdx);

        return new BaseResponse<>(result);
    }
}
