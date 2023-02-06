package com.umc.i.src.member.jwt;

import com.umc.i.src.member.login.model.PostLoginMemberReq;

import java.util.Optional;

public interface JwtRepository {
    public Optional<PostLoginMemberReq> findByLoginId(String refreshToken);

    public void deleteRefreshToken(String memIdx);

    public void insertRefreshToken(String memIdx, String refreshToken);
}
