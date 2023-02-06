package com.umc.i.src.feeds.model.patch;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PatchFeedsRes {
    private int boardIdx;    // 게시판 정보
    private int feedsIdx;   // 수정한 글의 인덱스
}
