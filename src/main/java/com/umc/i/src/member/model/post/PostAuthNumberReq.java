package com.umc.i.src.member.model.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostAuthNumberReq {
    private String type;
    private int authIdx;
    private String authNumber;
    private Date createdAt;
}