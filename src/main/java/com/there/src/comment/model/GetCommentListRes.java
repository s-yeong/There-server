package com.there.src.comment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetCommentListRes {

    private String nickName;
    private String profileImgUrl;
    private String content;
    private String created_At;
}
