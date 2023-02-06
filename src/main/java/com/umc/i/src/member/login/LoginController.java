package com.umc.i.src.member.login;

import com.umc.i.config.BaseResponse;
import com.umc.i.config.BaseResponseStatus;
import com.umc.i.src.member.jwt.JwtService;
import com.umc.i.src.member.login.model.PostLoginMemberReq;
import com.umc.i.src.member.login.model.PostLoginMemberRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class LoginController {

    private final LoginService loginService;

    private final JwtService jwtService;

    @PostMapping(value = "/login")
    public BaseResponse<PostLoginMemberRes> login(@RequestHeader Map<String, String> headers,
                                                  @RequestBody PostLoginMemberReq member) {
        /**
         * jwt refreshToken 이 전달된 경우 확인
         * jwtService isValid, isExpire 후에 모두 true면
         * uid, email 추출해서 밑에 새로운 토큰 발행 후 전달
         */
        String existRefreshToken = headers.get("auth-refresh");
        log.info("{}", existRefreshToken);
        if (existRefreshToken != null) {
            if (!(jwtService.isExpiredToken(existRefreshToken) && jwtService.isValidToken(existRefreshToken))) {
                return new BaseResponse<>(BaseResponseStatus.POST_AUTH_JWT_TOKEN_INVALID);
            } else {
                PostLoginMemberRes res = new PostLoginMemberRes();
                PostLoginMemberReq postLoginMemberReq = new PostLoginMemberReq();
                res.setId(Long.valueOf(jwtService.getMemberId(existRefreshToken)));
                res.setEmail(jwtService.getMemberEmail(existRefreshToken));
                postLoginMemberReq.setEmail(res.getEmail());
                postLoginMemberReq.setId(res.getId());
                res.setAccessToken(jwtService.createAccessToken(postLoginMemberReq));
                res.setRefreshToken(jwtService.createRefreshToken(postLoginMemberReq));
                jwtService.updateRefreshToken(String.valueOf(postLoginMemberReq.getId()), res.getRefreshToken());
                return new BaseResponse<>(res);
            }
        }

        PostLoginMemberReq postLoginMemberReq = loginService.login(member.getEmail(), member.getPassword());

        if (postLoginMemberReq == null) {
            return new BaseResponse<>(BaseResponseStatus.POST_AUTH_MEMBER_NOT_EXIST);
        }

        String accessToken = jwtService.createAccessToken(postLoginMemberReq);
        String refreshToken = jwtService.createRefreshToken(postLoginMemberReq);

        jwtService.updateRefreshToken(String.valueOf(postLoginMemberReq.getId()), refreshToken);

        PostLoginMemberRes res = new PostLoginMemberRes(postLoginMemberReq.getId(), postLoginMemberReq.getEmail(), postLoginMemberReq.getNickname(), accessToken, refreshToken);

        return new BaseResponse<>(res);
    }

    @PostMapping("/logout")
    public ResponseEntity logout(HttpServletRequest request) {
        return null;
    }
}
