package com.umc.i.src.member.jwt;

import com.umc.i.src.member.jwt.model.Jwt;
import com.umc.i.src.member.login.model.PostLoginMemberReq;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class JwtServiceImpl implements JwtService{

    private final JwtDao JwtRepository;

    private final String secretKey = Base64.getEncoder().encodeToString(Jwt.SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    @Override
    public String createAccessToken(PostLoginMemberReq postLoginMemberReq) {

        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");
        headers.put("alg", "HS256");

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        Date expireTime = new Date();
        expireTime.setTime(expireTime.getTime() + 1 * (1000L * 60 * 60 * 1));

        String accessToken = Jwts.builder()
                .setHeader(headers)
                .setSubject(postLoginMemberReq.getEmail())
                .claim("uid", postLoginMemberReq.getId())
                .setExpiration(expireTime)
                .signWith(signatureAlgorithm, secretKey)
                .compact();

        return accessToken;
    }

    @Override
    public String createRefreshToken(PostLoginMemberReq postLoginMemberReq) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");
        headers.put("alg", "HS256");

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        Date expireTime = new Date();
        expireTime.setTime(expireTime.getTime() + (30L * (1000 * 60 * 60 * 24)));

        String refreshToken = Jwts.builder()
                .setHeader(headers)
                .setSubject(postLoginMemberReq.getEmail())
                .claim("uid", postLoginMemberReq.getId())
                .setExpiration(expireTime)
                .signWith(signatureAlgorithm, secretKey)
                .compact();

        return refreshToken;
    }

    @Override
    public Boolean isValidToken(String token) {
        if (token == null) {
            return false;
        }

        String userKey = getMemberId(token);
        log.info("userKey={}", userKey);

        PostLoginMemberReq postLoginMemberReq = JwtRepository.findByLoginId(userKey)
                .filter(m -> String.valueOf(m.getId()).equals(userKey))
                .orElse(null);

        if (postLoginMemberReq == null) {
            return false;
        }

        return true;
    }

    @Override
    public Boolean isExpiredToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getMemberEmail(String token) throws RuntimeException{
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return claimsJws.getBody().getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getMemberId(String token) throws RuntimeException{
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return claimsJws.getBody().get("uid") + "";
        } catch (Exception e) {
            log.info("{}", e);
            return null;
        }
    }


    public void updateRefreshToken(String memIdx, String refreshToken) {
        JwtRepository.deleteRefreshToken(memIdx);
        JwtRepository.insertRefreshToken(memIdx, refreshToken);
    }

}
