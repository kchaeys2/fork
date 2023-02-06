package com.umc.i.src.feeds;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import com.umc.i.config.BaseException;
import com.umc.i.config.BaseResponse;
import com.umc.i.config.BaseResponseStatus;
import com.umc.i.src.feeds.model.patch.PatchDeleteReq;
import com.umc.i.src.feeds.model.patch.PatchFeedsReq;
import com.umc.i.src.feeds.model.patch.PatchFeedsRes;
import com.umc.i.src.feeds.model.post.PostFeedsReq;
import com.umc.i.src.feeds.model.post.PostFeedsRes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feeds")
@RequiredArgsConstructor
@Slf4j
public class FeedsController {
    @Autowired
    private final FeedsService feedsService;

    @ResponseBody
    @PostMapping("/write")     // 이야기방, 일기장 게시글 작성
    public BaseResponse<PostFeedsRes> createFeeds(@RequestBody PostFeedsReq postFeedsReq) throws BaseException {
        switch(postFeedsReq.getBoardIdx()) {
            case 1: //이야기방
                if(postFeedsReq.getRoomType() > 3) 
                    return new BaseResponse<>(BaseResponseStatus.POST_FEEDS_INVALID_TYPE);
                return new BaseResponse<>(feedsService.writeFeeds(postFeedsReq, null));
            case 2: //일기장
                if(postFeedsReq.getRoomType() > 2) 
                    return new BaseResponse<>(BaseResponseStatus.POST_FEEDS_INVALID_TYPE);
                return new BaseResponse<>(feedsService.writeFeeds(postFeedsReq, null));
        }

        return new BaseResponse<>(BaseResponseStatus.POST_FEEDS_INVALID_TYPE);
    }

    @ResponseBody
    @PostMapping("/write/img")     // 이야기방, 일기장 게시글 작성
    public BaseResponse<PostFeedsRes> createFeedsWithImg(@RequestPart("request") PostFeedsReq postFeedsReq, 
                        @RequestPart("img") List<MultipartFile> file) throws BaseException {
        switch(postFeedsReq.getBoardIdx()) {
            case 1: //이야기방
                if(postFeedsReq.getRoomType() > 3) 
                    return new BaseResponse<>(BaseResponseStatus.POST_FEEDS_INVALID_TYPE);
                return new BaseResponse<>(feedsService.writeFeeds(postFeedsReq, file));
            case 2: //일기장
                if(postFeedsReq.getRoomType() > 2) 
                    return new BaseResponse<>(BaseResponseStatus.POST_FEEDS_INVALID_TYPE);
                return new BaseResponse<>(feedsService.writeFeeds(postFeedsReq, file));

        }

        return new BaseResponse<>(BaseResponseStatus.POST_FEEDS_INVALID_TYPE);
    }


    @ResponseBody
    @PatchMapping("/edit")  // 이야기방, 일기장 게시글 수정
    public BaseResponse<PatchFeedsRes> editFeeds(@RequestBody PatchFeedsReq patchFeedsReq) throws BaseException {
        switch(patchFeedsReq.getBoardIdx()) {
            case 1:     // 이야기방
                if(patchFeedsReq.getRoomType() > 3) break;
                return new BaseResponse<>(feedsService.editFeeds(patchFeedsReq, null));
            case 2:     // 일기장
                if(patchFeedsReq.getRoomType() > 2) break;
                return new BaseResponse<>(feedsService.editFeeds(patchFeedsReq, null));
        }

        return new BaseResponse<>(BaseResponseStatus.POST_FEEDS_INVALID_TYPE);
    }

    @ResponseBody
    @PatchMapping("/edit/img")  // 이야기방, 일기장 게시글 수정(이미지 포함)
    public BaseResponse<PatchFeedsRes> editFeedsWithImg(@RequestPart("request") PatchFeedsReq patchFeedsReq,
                    @RequestPart("img") List<MultipartFile> img) throws BaseException {
        try {
            switch(patchFeedsReq.getBoardIdx()) {
                case 1:     // 이야기방
                    if(patchFeedsReq.getRoomType() > 3) break;
                    return new BaseResponse<>(feedsService.editFeeds(patchFeedsReq, img));
                case 2:     // 일기장
                    if(patchFeedsReq.getRoomType() > 2) break;
                    return new BaseResponse<>(feedsService.editFeeds(patchFeedsReq, img));
            }
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        } 

        return new BaseResponse<>(BaseResponseStatus.POST_FEEDS_INVALID_TYPE);
    }

    @ResponseBody
    @PatchMapping("/delete") // 이야기방, 일기장 게시글 삭제
    public BaseResponse deleteFeeds(@RequestBody PatchDeleteReq patchDeleteReq) throws BaseException {
        try {
            feedsService.deleteFeeds(patchDeleteReq.getBoardIdx(), patchDeleteReq.getFeedsIdx());
        } catch(BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
        return new BaseResponse<>("success!");
    }
    
}
