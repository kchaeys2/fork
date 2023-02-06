package com.umc.i.src.market.feed.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostMarketFeedRes {
    private int userIdx;
    private int marketIdx;
    private String isLike;

    public PostMarketFeedRes(int userIdx, int marketIdx) {
        this.userIdx = userIdx;
        this.marketIdx = marketIdx;
    }
}
