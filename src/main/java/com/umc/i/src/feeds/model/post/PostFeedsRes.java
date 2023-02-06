package com.umc.i.src.feeds.model.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostFeedsRes {
    private int boardIdx;   // 작성한 글의 게시판 정보
    private int feedsIdx;   // 작성한 글의 인덱스
}
