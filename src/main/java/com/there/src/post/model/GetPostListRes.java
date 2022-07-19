package com.there.src.post.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetPostListRes {
    private String imgUrl;
    private String content;
    private String created_At;
    private int likeCount;
}
