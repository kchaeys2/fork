package com.umc.i.utils.S3Storage;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Image {
    // 원본 파일명
    @NotEmpty
    private String originalFileName;
    // 업로드 파일 경로
    @NotEmpty
    private String uploadFilePath;
    private int category;   // 1: 이야기방, 2: 일기장, 3: 장터후기, 4: 나눔장터, 5: 채팅
    private int contentIdx; // 해당 카테고리의 인덱스
}
