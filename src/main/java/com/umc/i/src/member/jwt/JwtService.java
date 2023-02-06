package com.umc.i.src.member.jwt;

import com.umc.i.src.member.login.model.PostLoginMemberReq;


public interface JwtService {
    public String createAccessToken(PostLoginMemberReq postLoginMemberReq);

    public String createRefreshToken(PostLoginMemberReq postLoginMemberReq);

    public void updateRefreshToken(String memIdx, String refreshToken);

    public Boolean isValidToken(String token);

    public Boolean isExpiredToken(String token);

    public String getMemberEmail(String token);

    public String getMemberId(String token);
}
