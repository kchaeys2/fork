package com.umc.i.src.member.jwt.model;

import lombok.Data;

import java.time.Instant;

@Data
public class Jwt {

    public static String SECRET_KEY = "ThisIsIProjectJwtPrivateKey";

    private String uid;
    private String email;
}
