package com.umc.i.src.member.login;

import com.umc.i.src.member.login.model.PostLoginMemberReq;

import java.util.Optional;

public interface LoginRepository {

    Optional<PostLoginMemberReq> findByLoginEmail(String loginEmail);
}
