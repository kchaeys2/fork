package com.umc.i.src.mypage.model.get;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
public class GetMypageMemRes {
    @NonNull
    private String nick;
    @NonNull
    private String intro;
    @NonNull
    private String profile;
    private Integer feedCount;
    private Integer diaryCount;
    private Integer marketCount;
    @NonNull
    private String alarm;
}
