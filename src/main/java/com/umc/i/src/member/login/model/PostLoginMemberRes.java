package com.umc.i.src.member.login.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostLoginMemberRes {
    private Long id;
    private String email;
    private String nickname;
    private String accessToken;
    private String refreshToken;
}
