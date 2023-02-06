package com.umc.i.src.member.model.patch;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PatchMemReq {
    private int memIdx;
    private String email;
    private String pw;
    private String phone;
    private String nick;
    private String intro;
    private String birth;
    private String addresCode;
    private String addres;
    private String addresPlus;
}
