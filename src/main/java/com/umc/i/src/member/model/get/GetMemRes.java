package com.umc.i.src.member.model.get;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class GetMemRes {
    private String email;
    private String phone;
    private String nick;
    private String intro;
    private String birth;
    private String addresCode;
    private String addres;
    private String addresPlus;
    private String profile;
}
