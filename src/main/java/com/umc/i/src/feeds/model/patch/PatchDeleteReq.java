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
public class PatchDeleteReq {
    private int boardIdx;   // 게시판 정보
    private int feedsIdx;   // 삭제할 글 인덱스
}
