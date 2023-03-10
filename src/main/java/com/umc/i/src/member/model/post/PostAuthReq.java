package com.umc.i.src.member.model.post;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostAuthReq {
    private int type;   // 1: mail, 2: phone
    private String auth;    // 메일주소, 핸드폰 번호
}
