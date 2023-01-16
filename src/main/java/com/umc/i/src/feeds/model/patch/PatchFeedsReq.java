package com.umc.i.src.feeds.model.patch;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PatchFeedsReq {
    private int feedsIdx;   // 게시글 인덱스
    private int userIdx;  // 글쓴이 인덱스
    private int roomType;   // 1: 수다방(간호 일기), 2: 질문방(무지개 일기), 3: 정보방
    private String title;   // 제목
    private String content; // 내용  
    private int imgCnt;     // 이미지 개수
}
