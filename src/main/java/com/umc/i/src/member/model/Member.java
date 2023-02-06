package com.umc.i.src.member.model;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Member {
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
