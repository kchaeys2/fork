package com.umc.i.src.market.feed.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetMarketFeedRes {
    private int marketIdx;
    private int userIdx; // 게시물 작성자
    private String userNickname;
    private String group;
    private String title;
    private String content;
    private int price;
    private String image;
    private String soldout;
    private int likeCount;
    private int hit;
    private Date createdAt;
    private boolean userLiked;
}
