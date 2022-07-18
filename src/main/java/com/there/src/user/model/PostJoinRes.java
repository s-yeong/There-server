package com.there.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostJoinRes {
    private String jwt;
    private int userIdx;
}
