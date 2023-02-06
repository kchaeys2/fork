package com.umc.i.src.member.login.model;

import lombok.Data;

@Data
public class PostLoginMemberReq {
    private Long id;
    private String email;
    private String password;
    private String nickname;
}
